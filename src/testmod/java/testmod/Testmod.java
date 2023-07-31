package testmod;

import java.util.Map;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testmod.config.ModConfig;
import top.offsetmonkey538.monkeyconfig538.ConfigManager;

public class Testmod implements ModInitializer {
	public static final String MOD_ID = "testmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ConfigManager.init(new ModConfig(), MOD_ID);

		LOGGER.info(String.valueOf(config().iDoNotHaveAComment));
		LOGGER.info(String.valueOf(config().myString));
		LOGGER.info(String.valueOf(config().coolItem));
		LOGGER.info(String.valueOf(config().coolBlock));
		LOGGER.info(String.valueOf(config().coolItemStack));
		LOGGER.info(String.valueOf(config().coolBlockState));
		LOGGER.info(String.valueOf(config().coolBlockPos));
		LOGGER.info(String.valueOf(config().statusEffectInstance));
		LOGGER.info(String.valueOf(config().vec3d));
		LOGGER.info(String.valueOf(config().gameProfile));
		LOGGER.info(String.valueOf(config().identifier));
		for (Map.Entry<String, Integer> entry : config().stringToIntegerMap.entrySet()) {
			LOGGER.info("stringToIntegerMap {} = {}", entry.getKey(), entry.getValue());
		}
		for (int i = 0; i < config().myIntArray.length; i++) {
			LOGGER.info("myIntArray {} = {}", i, config().myIntArray[i]);
		}
		for (int i = 0; i < config().myFloatArray.length; i++) {
			LOGGER.info("myFloatArray {} = {}", i, config().myFloatArray[i]);
		}
		LOGGER.info(String.valueOf(config().nestedThing.veryCoolBoolean));
		LOGGER.info(String.valueOf(config().nestedThing.nestedThing2.nestedThing3.anotherBoolean));
		LOGGER.info(String.valueOf(config().nestedThing.nestedThing2.nestedThing3.nestedThing4.nestedThing5.aFloatingNumber));
	}

	public static ModConfig config() {
		return (ModConfig) ConfigManager.get(MOD_ID);
	}
}
