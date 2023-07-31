package top.offsetmonkey538.monkeyconfig538;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.SyntaxError;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;

import static top.offsetmonkey538.monkeyconfig538.MonkeyConfig538.*;

public class ConfigManager {
    private static final Map<String, Config> configs = new HashMap<>();

    public static void init(Config config, String configName) {
        if (getConfigFile(configName).exists()) config = load(config, configName);
        save(config, configName);

        configs.put(configName, config);
    }

    public static Config get(String configName) {
        if (configs.containsKey(configName)) return configs.get(configName);

        LOGGER.warn("Config '{}' not found!", configName);
        return null;
    }

    private static Config load(Config config, String configName) {
        File configFile = getConfigFile(configName);

        if (!configFile.exists()) return config;

        Jankson jankson = config.configureJankson(Jankson.builder()).build();

        try {
            JsonObject jsonObject = jankson.load(configFile);

            return jankson.fromJsonCarefully(jsonObject, config.getClass());
        } catch (IOException e) {
            LOGGER.error(String.format("Couldn't read config file '%s'!", configFile), e);
            return config;
        } catch (SyntaxError e) {
            LOGGER.error("Couldn't read config file '{}'!", configFile);
            LOGGER.error("Reason: {}", e.getCompleteMessage());
            return config;
        } catch (DeserializationException e) {
            LOGGER.error("Deserialization exception for config file '{}'!", configFile);
            LOGGER.error("Reason: ", e);
            return config;
        }
    }

    private static void save(Config config, String configName) {
        Path configFilePath = getConfigFilePath(configName);
        Jankson jankson = config.configureJankson(Jankson.builder()).build();
        String json = jankson.toJson(config).toJson(true, true);

        try {
            Files.writeString(configFilePath, json);
        } catch (IOException e) {
            LOGGER.error(String.format("Couldn't write to config file '%s'!", configFilePath), e);
        }
    }

    private static File getConfigFile(String name) {
        return getConfigFilePath(name).toFile();
    }

    private static Path getConfigFilePath(String name) {
        return FabricLoader.getInstance().getConfigDir().resolve(name + ".json");
    }
}
