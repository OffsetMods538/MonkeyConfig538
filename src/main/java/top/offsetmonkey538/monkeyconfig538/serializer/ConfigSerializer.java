package top.offsetmonkey538.monkeyconfig538.serializer;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.api.Marshaller;

public interface ConfigSerializer<T> {

    JsonElement toJson(T value, Marshaller marshaller);

    T fromJson(JsonElement json, Marshaller marshaller);
}
