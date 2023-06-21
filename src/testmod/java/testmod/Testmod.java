package testmod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testmod.config.ModConfig;

public class Testmod implements ModInitializer {
	public static final String MOD_ID = "testmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		new ModConfig().init();

		LOGGER.info(String.valueOf(ModConfig.iDoNotHaveAComment));
		LOGGER.info(String.valueOf(ModConfig.myString));
		for (int i = 0; i < ModConfig.myIntArray.length; i++) {
			LOGGER.info("myIntArray {} = {}", i, ModConfig.myIntArray[i]);
		}
		for (int i = 0; i < ModConfig.myFloatArray.length; i++) {
			LOGGER.info("myFloatArray {} = {}", i, ModConfig.myFloatArray[i]);
		}
		LOGGER.info(String.valueOf(ModConfig.NestedThing.veryCoolBoolean));
		LOGGER.info(String.valueOf(ModConfig.NestedThing.NestedThing2.NestedThing3.anotherBoolean));
		LOGGER.info(String.valueOf(ModConfig.NestedThing.NestedThing2.NestedThing3.NestedThing4.NestedThing5.aFloatingNumber));
	}
}
