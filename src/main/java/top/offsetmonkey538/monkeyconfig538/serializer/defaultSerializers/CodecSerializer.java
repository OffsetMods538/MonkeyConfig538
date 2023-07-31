package top.offsetmonkey538.monkeyconfig538.serializer.defaultSerializers;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonNull;
import blue.endless.jankson.api.Marshaller;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import top.offsetmonkey538.monkeyconfig538.serializer.ConfigSerializer;
import top.offsetmonkey538.monkeyconfig538.serializer.JanksonOps;

import static top.offsetmonkey538.monkeyconfig538.MonkeyConfig538.*;

/**
 * A serializer for classes which have a {@link Codec}.
 *
 * @param <T> The serializable class.
 */
public class CodecSerializer<T> implements ConfigSerializer<T> {
    private final Codec<T> codec;

    /**
     * Creates a new {@link CodecSerializer} for a codec.
     *
     * @param codec The codec used for (de)serializing values.
     */
    public CodecSerializer(Codec<T> codec) {
        this.codec = codec;
    }

    @Override
    public JsonElement toJson(T value, Marshaller marshaller) {
        DataResult<JsonElement> result = codec.encodeStart(JanksonOps.INSTANCE, value);

        Optional<JsonElement> optionalResult = result.resultOrPartial(LOGGER::warn);

        return optionalResult.orElse(JsonNull.INSTANCE);
    }

    @Override
    public T fromJson(JsonElement json, Marshaller marshaller) {
        DataResult<T> result = codec.parse(JanksonOps.INSTANCE, json);

        Optional<T> optionalResult = result.resultOrPartial(LOGGER::warn);

        return optionalResult.orElse(null);
    }
}
