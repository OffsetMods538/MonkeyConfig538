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

/**
 * The config manager.
 */
public final class ConfigManager {
    private ConfigManager() {

    }

    private static final Map<String, Config> configs = new HashMap<>();

    /**
     * Initializes a config.
     *
     * @param config The config to initialize.
     * @param configName The name of the config. Can have forward-slashes to put the config in folders.
     */
    public static void init(Config config, String configName) {
        if (getConfigFile(configName).exists()) config = load(config, configName);
        save(config, configName);

        configs.put(configName, config);
    }

    /**
     * Returns the config with the provided name, null if it hasn't been initialized.
     *
     * @param configName The name of the config.
     * @return the config with the provided name, null if it hasn't been initialized.
     */
    public static Config get(String configName) {
        if (configs.containsKey(configName)) return configs.get(configName);

        LOGGER.warn("Config '{}' not found!", configName);
        return null;
    }

    /**
     * Loads the config.
     *
     * @param config The config class to load.
     * @param configName The name of the config.
     * @return the config loaded from the file.
     */
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

    /**
     * Saves the config into a file.
     *
     * @param config The config to save.
     * @param configName The name of the config to save.
     */
    private static void save(Config config, String configName) {
        Path configFilePath = getConfigFilePath(configName);
        Jankson jankson = config.configureJankson(Jankson.builder()).build();
        String json = jankson.toJson(config).toJson(true, true);

        try {
            Path parentDir = configFilePath.getParent();
            if (Files.notExists(parentDir)) Files.createDirectories(parentDir);
            Files.writeString(configFilePath, json);
        } catch (IOException e) {
            LOGGER.error(String.format("Couldn't write to config file '%s'!", configFilePath), e);
        }
    }

    /**
     * Helper function for getting the file for a specific config.
     *
     * @param name The name of the config.
     * @return The file for the specified config.
     */
    private static File getConfigFile(String name) {
        return getConfigFilePath(name).toFile();
    }

    /**
     * Helper function for getting the path for a specific config.
     *
     * @param name The name of the config.
     * @return The path for the specified config.
     */
    private static Path getConfigFilePath(String name) {
        return FabricLoader.getInstance().getConfigDir().resolve(name + ".json");
    }
}
