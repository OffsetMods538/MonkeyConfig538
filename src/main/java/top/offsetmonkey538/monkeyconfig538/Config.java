package top.offsetmonkey538.monkeyconfig538;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import top.offsetmonkey538.monkeyconfig538.serializer.ConfigSerializer;
import top.offsetmonkey538.monkeyconfig538.serializer.defaultSerializers.CodecSerializer;
import top.offsetmonkey538.monkeyconfig538.serializer.defaultSerializers.RegistrySerializer;
import top.offsetmonkey538.monkeyconfig538.serializer.defaultSerializers.StatusEffectInstanceSerializer;

/**
 * A config class.
 */
public abstract class Config {

    /**
     * Called when creating a {@link Jankson} instance.
     * <br />
     * Override to add custom {@link ConfigSerializer serializers}.
     *
     * @param builder The builder to configure.
     * @return the configured {@link Jankson.Builder builder}.
     */
    protected Jankson.Builder configureJankson(Jankson.Builder builder) {
        // TODO: default serializers

        // Registrable things
        registerSerializer(builder, Block.class,                       new RegistrySerializer<>(Registries.BLOCK));
        registerSerializer(builder, Enchantment.class,                 new RegistrySerializer<>(Registries.ENCHANTMENT));
        registerSerializer(builder, EntityType.class,                  new RegistrySerializer<>(Registries.ENTITY_TYPE));;
        registerSerializer(builder, Fluid.class,                       new RegistrySerializer<>(Registries.FLUID));
        registerSerializer(builder, Item.class,                        new RegistrySerializer<>(Registries.ITEM));
        registerSerializer(builder, ParticleType.class,                new RegistrySerializer<>(Registries.PARTICLE_TYPE));
        registerSerializer(builder, Potion.class,                      new RegistrySerializer<>(Registries.POTION));
        registerSerializer(builder, SoundEvent.class,                  new RegistrySerializer<>(Registries.SOUND_EVENT));
        registerSerializer(builder, StatusEffect.class,                new RegistrySerializer<>(Registries.STATUS_EFFECT));

        // Things with codecs
        registerSerializer(builder, ItemStack.class,                   new CodecSerializer<>(ItemStack.CODEC));
        registerSerializer(builder, BlockState.class,                  new CodecSerializer<>(BlockState.CODEC));
        registerSerializer(builder, BlockPos.class,                    new CodecSerializer<>(BlockPos.CODEC));
        registerSerializer(builder, Identifier.class,                  new CodecSerializer<>(Identifier.CODEC));

        // Others
        registerSerializer(builder, StatusEffectInstance.class,        new StatusEffectInstanceSerializer());

        return builder;
    }

    /**
     * Registers a serializer for the {@link Jankson.Builder builder}.
     *
     * @param builder The builder to add the serializer to.
     * @param type The class for which the serializer is for.
     * @param serializer The serializer itself.
     * @param <T> The class for which the serializer is for.
     */
    protected <T> void registerSerializer(Jankson.Builder builder, Class<T> type, ConfigSerializer<T> serializer) {
        builder.registerSerializer(type, serializer::toJson);
        builder.registerDeserializer(JsonElement.class, type, serializer::fromJson);
    }
}
