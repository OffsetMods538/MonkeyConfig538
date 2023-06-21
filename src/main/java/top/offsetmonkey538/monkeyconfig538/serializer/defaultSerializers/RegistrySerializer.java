package top.offsetmonkey538.monkeyconfig538.serializer.defaultSerializers;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonNull;
import blue.endless.jankson.api.Marshaller;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import top.offsetmonkey538.monkeyconfig538.serializer.ConfigSerializer;

import static top.offsetmonkey538.monkeyconfig538.MonkeyConfig538.*;

public class RegistrySerializer<T, U extends T> implements ConfigSerializer<T> {
    private final Registry<U> registry;

    public RegistrySerializer(Registry<U> registry) {
        this.registry = registry;
    }


    @SuppressWarnings("unchecked")
    @Override
    public JsonElement toJson(T value, Marshaller marshaller) {
        Identifier identifier = registry.getId((U) value);

        if (identifier != null) return marshaller.serialize(identifier.toString());

        LOGGER.warn("Failed to serialize registry value '{}' as its identifier is null!", value.toString());
        return JsonNull.INSTANCE;
    }

    @Override
    public T fromJson(JsonElement json, Marshaller marshaller) {
        String indentifierString = marshaller.marshall(String.class, json);
        Identifier identifier = new Identifier(indentifierString);

        if (registry.containsId(identifier)) return registry.get(identifier);
        return null;
    }
}
