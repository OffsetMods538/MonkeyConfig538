package top.offsetmonkey538.monkeyconfig538.serializer.defaultSerializers;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.Marshaller;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import top.offsetmonkey538.monkeyconfig538.serializer.ConfigSerializer;

/**
 * A serializer for {@link StatusEffectInstance}.
 */
public class StatusEffectInstanceSerializer implements ConfigSerializer<StatusEffectInstance> {
    @Override
    public JsonElement toJson(StatusEffectInstance value, Marshaller marshaller) {
        JsonObject result = new JsonObject();

        result.put("type", marshaller.serialize(value.getEffectType()));
        result.put("duration", marshaller.serialize(value.getDuration()));
        result.put("amplifier", marshaller.serialize(value.getAmplifier()));
        result.put("ambient", marshaller.serialize(value.isAmbient()));
        result.put("showParticles", marshaller.serialize(value.shouldShowParticles()));
        result.put("showIcon", marshaller.serialize(value.shouldShowIcon()));

        return result;
    }

    @Override
    public StatusEffectInstance fromJson(JsonElement json, Marshaller marshaller) {
        if (!(json instanceof JsonObject jsonObject)) return null;

        StatusEffect type = marshaller.marshall(StatusEffect.class, jsonObject.get("type"));
        if (type == null) return null;


        Integer duration = marshaller.marshall(int.class, jsonObject.get("duration"));
        Integer amplifier = marshaller.marshall(int.class, jsonObject.get("amplifier"));
        Boolean ambient = marshaller.marshall(boolean.class, jsonObject.get("ambient"));
        Boolean showParticles = marshaller.marshall(boolean.class, jsonObject.get("showParticles"));
        Boolean showIcon = marshaller.marshall(boolean.class, jsonObject.get("showIcon"));


        if (duration == null) return new StatusEffectInstance(type);
        if (amplifier == null) return new StatusEffectInstance(type, duration);
        if (showParticles == null || showIcon == null) return new StatusEffectInstance(type, duration, amplifier);
        return new StatusEffectInstance(type, duration, amplifier, ambient, showParticles, showIcon);
    }
}
