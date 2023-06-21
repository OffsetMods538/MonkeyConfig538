package top.offsetmonkey538.monkeyconfig538;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import top.offsetmonkey538.monkeyconfig538.annotation.ConfigEntry;
import top.offsetmonkey538.monkeyconfig538.serializer.ConfigSerializer;

import static top.offsetmonkey538.monkeyconfig538.MonkeyConfig538.*;

public abstract class Config {
    protected final Jankson jankson;

    public Config() {
        jankson = configureJankson(new Jankson.Builder()).build();
    }

    public void init() {
        if (getConfigFile().exists()) load();
        save();
    }

    public void load() {
        if (!getConfigFile().exists()) return;

        try {
            JsonObject jsonObject = jankson.load(getConfigFile());

            populateFieldsFromJsonObject(jsonObject, this.getClass());
        } catch (IOException | SyntaxError e) {
            LOGGER.error(String.format("Couldn't load config file '%s'!", getConfigFilePath()), e);
        }
    }

    public void save() {
        JsonObject jsonObject = new JsonObject();
        populateJsonObjectFromFields(jsonObject, this.getClass());

        try {
            Files.writeString(getConfigFilePath(), jsonObject.toJson(true, true));
        } catch (IOException e) {
            LOGGER.error(String.format("Couldn't write to config file '%s'!", getConfigFilePath()), e);
        }
    }

    private void populateFieldsFromJsonObject(JsonObject jsonObject, Class<?> configClass) {
        for (Field field : configClass.getDeclaredFields()) {
            final ConfigEntry entryAnnotation = field.getAnnotation(ConfigEntry.class);
            if (entryAnnotation == null) continue;

            String entryName = field.getName();

            // Throw error when config entry is not static
            if (!Modifier.isStatic(field.getModifiers())) throw new IllegalStateException(String.format("Config entry '%s' should be static!", entryName));


            Object value = jsonObject.get(field.getType(), entryName);
            if (value == null) continue;

            field.setAccessible(true);
            try {
                field.set(null, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(String.format("Couldn't set field '%s'!", entryName), e);
            }
        }

        for (Class<?> nestedClass : configClass.getDeclaredClasses()) {
            final ConfigEntry entryAnnotation = nestedClass.getAnnotation(ConfigEntry.class);
            if (entryAnnotation == null) return;

            String entryName = nestedClass.getSimpleName();

            JsonObject nestedJsonObject = jsonObject.getObject(entryName);
            if (nestedJsonObject == null) continue;

            populateFieldsFromJsonObject(nestedJsonObject, nestedClass);
        }
    }

    private void populateJsonObjectFromFields(JsonObject jsonObject, Class<?> configClass) {
        for (Field field : configClass.getDeclaredFields()) {
            final ConfigEntry entryAnnotation = field.getAnnotation(ConfigEntry.class);
            if (entryAnnotation == null) continue;

            String entryName = field.getName();
            String entryComment = entryAnnotation.value();

            // Throw error when config entry is not static
            if (!Modifier.isStatic(field.getModifiers())) throw new IllegalStateException(String.format("Config entry '%s' should be static!", entryName));


            Object value;

            field.setAccessible(true);
            try {
                value = field.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(String.format("Couldn't get field '%s'!", entryName), e);
            }

            jsonObject.put(entryName, jankson.toJson(value), entryComment);
        }

        for (Class<?> nestedClass : configClass.getDeclaredClasses()) {
            final ConfigEntry entryAnnotation = nestedClass.getAnnotation(ConfigEntry.class);
            if (entryAnnotation == null) return;

            String entryName = nestedClass.getSimpleName();
            String entryComment = entryAnnotation.value();

            JsonObject nestedJsonObject = new JsonObject();
            populateJsonObjectFromFields(nestedJsonObject, nestedClass);

            jsonObject.put(entryName, nestedJsonObject, entryComment);
        }
    }

    protected Jankson.Builder configureJankson(Jankson.Builder builder) {
        // TODO: default serializers
        return builder;
    }

    protected <T> void registerSerializer(Jankson.Builder builder, Class<T> type, ConfigSerializer<T> serializer) {
        builder.registerSerializer(type, serializer::toJson);
        builder.registerDeserializer(JsonElement.class, type, serializer::fromJson);
    }

    private File getConfigFile() {
        return getConfigFilePath().toFile();
    }

    private Path getConfigFilePath() {
        return FabricLoader.getInstance().getConfigDir().resolve(getName() + ".json");
    }

    protected abstract String getName();
}
