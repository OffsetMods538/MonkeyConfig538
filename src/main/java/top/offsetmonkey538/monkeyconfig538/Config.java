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
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Instrument;
import net.minecraft.item.Item;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.nbt.LootNbtProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.score.LootScoreProviderType;
import net.minecraft.particle.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatType;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSourceType;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.size.FeatureSizeType;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.heightprovider.HeightProviderType;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;
import net.minecraft.world.gen.root.RootPlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.structure.StructureType;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import net.minecraft.world.poi.PointOfInterestType;
import top.offsetmonkey538.monkeyconfig538.annotation.ConfigEntry;
import top.offsetmonkey538.monkeyconfig538.serializer.ConfigSerializer;
import top.offsetmonkey538.monkeyconfig538.serializer.defaultSerializers.RegistrySerializer;

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

        // Registrable things
        registerSerializer(builder, Activity.class,                 new RegistrySerializer<>(Registries.ACTIVITY));
        registerSerializer(builder, EntityAttribute.class,          new RegistrySerializer<>(Registries.ATTRIBUTE));
        registerSerializer(builder, BannerPattern.class,            new RegistrySerializer<>(Registries.BANNER_PATTERN));
        registerSerializer(builder, Block.class,                    new RegistrySerializer<>(Registries.BLOCK));
        registerSerializer(builder, BlockEntityType.class,          new RegistrySerializer<>(Registries.BLOCK_ENTITY_TYPE));
        registerSerializer(builder, BlockPredicateType.class,       new RegistrySerializer<>(Registries.BLOCK_PREDICATE_TYPE));
        registerSerializer(builder, BlockStateProviderType.class,   new RegistrySerializer<>(Registries.BLOCK_STATE_PROVIDER_TYPE));
        registerSerializer(builder, Carver.class,                   new RegistrySerializer<>(Registries.CARVER));
        registerSerializer(builder, CatVariant.class,               new RegistrySerializer<>(Registries.CAT_VARIANT));
        registerSerializer(builder, ChunkStatus.class,              new RegistrySerializer<>(Registries.CHUNK_STATUS));
        registerSerializer(builder, ArgumentSerializer.class,       new RegistrySerializer<>(Registries.COMMAND_ARGUMENT_TYPE));
        registerSerializer(builder, Enchantment.class,              new RegistrySerializer<>(Registries.ENCHANTMENT));
        registerSerializer(builder, EntityType.class,               new RegistrySerializer<>(Registries.ENTITY_TYPE));
        registerSerializer(builder, Feature.class,                  new RegistrySerializer<>(Registries.FEATURE));
        registerSerializer(builder, FeatureSizeType.class,          new RegistrySerializer<>(Registries.FEATURE_SIZE_TYPE));
        registerSerializer(builder, FloatProviderType.class,        new RegistrySerializer<>(Registries.FLOAT_PROVIDER_TYPE));
        registerSerializer(builder, Fluid.class,                    new RegistrySerializer<>(Registries.FLUID));
        registerSerializer(builder, FoliagePlacerType.class,        new RegistrySerializer<>(Registries.FOLIAGE_PLACER_TYPE));
        registerSerializer(builder, FrogVariant.class,              new RegistrySerializer<>(Registries.FROG_VARIANT));
        registerSerializer(builder, GameEvent.class,                new RegistrySerializer<>(Registries.GAME_EVENT));
        registerSerializer(builder, HeightProviderType.class,       new RegistrySerializer<>(Registries.HEIGHT_PROVIDER_TYPE));
        registerSerializer(builder, Instrument.class,               new RegistrySerializer<>(Registries.INSTRUMENT));
        registerSerializer(builder, IntProviderType.class,          new RegistrySerializer<>(Registries.INT_PROVIDER_TYPE));
        registerSerializer(builder, Item.class,                     new RegistrySerializer<>(Registries.ITEM));
        registerSerializer(builder, LootConditionType.class,        new RegistrySerializer<>(Registries.LOOT_CONDITION_TYPE));
        registerSerializer(builder, LootFunctionType.class,         new RegistrySerializer<>(Registries.LOOT_FUNCTION_TYPE));
        registerSerializer(builder, LootNbtProviderType.class,      new RegistrySerializer<>(Registries.LOOT_NBT_PROVIDER_TYPE));
        registerSerializer(builder, LootNumberProviderType.class,   new RegistrySerializer<>(Registries.LOOT_NUMBER_PROVIDER_TYPE));
        registerSerializer(builder, LootPoolEntryType.class,        new RegistrySerializer<>(Registries.LOOT_POOL_ENTRY_TYPE));
        registerSerializer(builder, LootScoreProviderType.class,    new RegistrySerializer<>(Registries.LOOT_SCORE_PROVIDER_TYPE));
        registerSerializer(builder, MemoryModuleType.class,         new RegistrySerializer<>(Registries.MEMORY_MODULE_TYPE));
        registerSerializer(builder, PaintingVariant.class,          new RegistrySerializer<>(Registries.PAINTING_VARIANT));
        registerSerializer(builder, ParticleType.class,             new RegistrySerializer<>(Registries.PARTICLE_TYPE));
        registerSerializer(builder, PlacementModifierType.class,    new RegistrySerializer<>(Registries.PLACEMENT_MODIFIER_TYPE));
        registerSerializer(builder, PointOfInterestType.class,      new RegistrySerializer<>(Registries.POINT_OF_INTEREST_TYPE));
        registerSerializer(builder, PositionSourceType.class,       new RegistrySerializer<>(Registries.POSITION_SOURCE_TYPE));
        registerSerializer(builder, PosRuleTestType.class,          new RegistrySerializer<>(Registries.POS_RULE_TEST));
        registerSerializer(builder, Potion.class,                   new RegistrySerializer<>(Registries.POTION));
        registerSerializer(builder, RecipeSerializer.class,         new RegistrySerializer<>(Registries.RECIPE_SERIALIZER));
        registerSerializer(builder, RecipeType.class,               new RegistrySerializer<>(Registries.RECIPE_TYPE));
        registerSerializer(builder, Registry.class,                 new RegistrySerializer<>(Registries.REGISTRIES));
        registerSerializer(builder, RootPlacerType.class,           new RegistrySerializer<>(Registries.ROOT_PLACER_TYPE));
        registerSerializer(builder, RuleTestType.class,             new RegistrySerializer<>(Registries.RULE_TEST));
        registerSerializer(builder, Schedule.class,                 new RegistrySerializer<>(Registries.SCHEDULE));
        registerSerializer(builder, ScreenHandlerType.class,        new RegistrySerializer<>(Registries.SCREEN_HANDLER));
        registerSerializer(builder, SensorType.class,               new RegistrySerializer<>(Registries.SENSOR_TYPE));
        registerSerializer(builder, SoundEvent.class,               new RegistrySerializer<>(Registries.SOUND_EVENT));
        registerSerializer(builder, StatusEffect.class,             new RegistrySerializer<>(Registries.STATUS_EFFECT));
        registerSerializer(builder, StatType.class,                 new RegistrySerializer<>(Registries.STAT_TYPE));
        registerSerializer(builder, StructurePieceType.class,       new RegistrySerializer<>(Registries.STRUCTURE_PIECE));
        registerSerializer(builder, StructurePlacementType.class,   new RegistrySerializer<>(Registries.STRUCTURE_PLACEMENT));
        registerSerializer(builder, StructurePoolElementType.class, new RegistrySerializer<>(Registries.STRUCTURE_POOL_ELEMENT));
        registerSerializer(builder, StructureProcessorType.class,   new RegistrySerializer<>(Registries.STRUCTURE_PROCESSOR));
        registerSerializer(builder, StructureType.class,            new RegistrySerializer<>(Registries.STRUCTURE_TYPE));
        registerSerializer(builder, TreeDecoratorType.class,        new RegistrySerializer<>(Registries.TREE_DECORATOR_TYPE));
        registerSerializer(builder, TrunkPlacerType.class,          new RegistrySerializer<>(Registries.TRUNK_PLACER_TYPE));
        registerSerializer(builder, VillagerProfession.class,       new RegistrySerializer<>(Registries.VILLAGER_PROFESSION));
        registerSerializer(builder, VillagerType.class,             new RegistrySerializer<>(Registries.VILLAGER_TYPE));

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
