package top.offsetmonkey538.monkeyconfig538.serializer;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.api.Marshaller;

/**
 * A serializer for <code>T</code>
 *
 * @param <T> The serializable class.
 */
public interface ConfigSerializer<T> {

    /**
     * Serializes a value to a {@link JsonElement}.
     *
     * @param value The value to serialize.
     * @param marshaller An instance of the {@link Marshaller}. Used for serializing values.
     * @return the value serialized into a {@link JsonElement}.
     */
    JsonElement toJson(T value, Marshaller marshaller);

    /**
     * Deserializes a value from a {@link JsonElement}.
     *
     * @param json The {@link JsonElement} to deserialize.
     * @param marshaller An instance of the {@link Marshaller}. Used for deserializing values.
     * @return the value deserialized from the {@link JsonElement}.
     */
    T fromJson(JsonElement json, Marshaller marshaller);
}
