package top.offsetmonkey538.monkeyconfig538.serializer;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonNull;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A {@link DynamicOps} for jankson.
 */
public class JanksonOps implements DynamicOps<JsonElement> {
    /**
     * JanksonOps instance for serialization
     */
    public static final JanksonOps INSTANCE = new JanksonOps(false);

    /**
     * JanksonOps instance for compressed serialization
     */
    public static final JanksonOps COMPRESSED = new JanksonOps(true);

    private final boolean compressed;

    /**
     * Creates a new JanksonOps instance
     *
     * @param compressed Whether this JanksonOps should use compressed serialization.
     */
    protected JanksonOps(final boolean compressed) {
        this.compressed = compressed;
    }

    @Override
    public JsonElement empty() {
        return JsonNull.INSTANCE;
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, JsonElement input) {
        if (input instanceof JsonObject) return convertMap(outOps, input);
        if (input instanceof JsonArray) return convertList(outOps, input);

        if (!(input instanceof JsonPrimitive primitive)) return outOps.empty();
        Object value = primitive.getValue();

        if (value instanceof Boolean actualValue) return outOps.createBoolean(actualValue);
        if (value instanceof Byte actualValue) return outOps.createByte(actualValue);
        if (value instanceof Double actualValue) return outOps.createDouble(actualValue);
        if (value instanceof Float actualValue) return outOps.createFloat(actualValue);
        if (value instanceof Integer actualValue) return outOps.createInt(actualValue);
        if (value instanceof Long actualValue) return outOps.createLong(actualValue);
        if (value instanceof Short actualValue) return outOps.createShort(actualValue);
        if (value instanceof String actualValue) return outOps.createString(actualValue);

        return outOps.empty();
    }

    @Override
    public DataResult<Number> getNumberValue(JsonElement input) {
        if (!(input instanceof JsonPrimitive primitive)) return DataResult.error(() -> "Not a number: " + input);
        Object value = primitive.getValue();

        if (value instanceof Number actualValue) return DataResult.success(actualValue);
        if (value instanceof Boolean actualValue) return DataResult.success(actualValue ? 1 : 0);
        if (compressed && value instanceof String actualValue) {
            try {
                return DataResult.success(Integer.parseInt(actualValue));
            } catch (final NumberFormatException e) {
                return DataResult.error(() -> "Not a number: " + e + " " + input);
            }
        }

        return DataResult.error(() -> "Not a number: " + input);
    }

    @Override
    public JsonElement createNumeric(Number i) {
        return new JsonPrimitive(i);
    }

    //@Override
    //public DataResult<Boolean> getBooleanValue(JsonElement input) {
    //    if (!(input instanceof JsonPrimitive primitive)) return DataResult.error(() -> "Not a boolean: " + input);
    //    Object value = primitive.getValue();

    //    if (value instanceof Boolean actualValue) return DataResult.success(actualValue);
    //    if (value instanceof Number actualValue) return DataResult.success(actualValue.byteValue() != 0);

    //    return DataResult.error(() -> "Not a boolean: " + input);
    //}

    //@Override
    //public JsonElement createBoolean(boolean value) {
    //    return new JsonPrimitive(value);
    //}

    @Override
    public DataResult<String> getStringValue(JsonElement input) {
        if (!(input instanceof JsonPrimitive primitive)) return DataResult.error(() -> "Not a string: " + input);

        if (!(compressed || primitive.getValue() instanceof String)) return DataResult.error(() -> "Not a string: " + input);
        return DataResult.success(primitive.asString());
    }

    @Override
    public JsonElement createString(String value) {
        return new JsonPrimitive(value);
    }

    @Override
    public DataResult<JsonElement> mergeToList(JsonElement list, JsonElement value) {
        if (!(list instanceof JsonArray || list instanceof JsonNull)) return DataResult.error(() -> "Not an array: " + list);

        JsonArray output = new JsonArray();

        if (list instanceof JsonArray) output.addAll((JsonArray) list);
        output.add(value);

        return DataResult.success(output);
    }

    ////@Override
    ////public DataResult<JsonElement> mergeToList(JsonElement list, List<JsonElement> values) {
    ////    if (!(list instanceof JsonArray || list instanceof JsonNull)) return DataResult.error(() -> "Not an array: " + list);

    ////    JsonArray output = new JsonArray();

    ////    if (list instanceof JsonArray) output.addAll((JsonArray) list);
    ////    output.addAll(values);

    ////    return DataResult.success(output);
    ////}

    ////@Override
    ////public DataResult<JsonElement> mergeToMap(JsonElement map, MapLike<JsonElement> values) {
    ////    if (!(map instanceof JsonObject || map instanceof JsonNull)) return DataResult.error(() -> "mergeToMap called with not a map: " + map, map);

    ////    JsonObject result = new JsonObject();
    ////    if (map instanceof JsonObject mapAsObject) result.putAll(mapAsObject);

    ////    List<JsonElement> missed = new ArrayList<>();

    ////    values.entries().forEach(entry -> {
    ////        JsonElement key = entry.getFirst();

    ////        if (!(key instanceof JsonPrimitive primitive) || (!(primitive.getValue() instanceof String) && !compressed)) {
    ////            missed.add(key);
    ////            return;
    ////        }

    ////        result.put(((JsonPrimitive) key).asString(), entry.getSecond());
    ////    });

    ////    if (!missed.isEmpty()) {
    ////        return DataResult.error(() -> "some keys are not strings: " + missed, result);
    ////    }

    ////    return DataResult.success(result);
    ////}

    @Override
    public DataResult<JsonElement> mergeToMap(JsonElement map, JsonElement key, JsonElement value) {
        if (!(key instanceof JsonPrimitive primitive && (primitive.getValue() instanceof String keyValue && !compressed))) return DataResult.error(() -> "Key is not a string: " + key);
        if (!(map instanceof JsonNull || map instanceof JsonObject)) return DataResult.error(() -> "Not JSON object: " + map);

        JsonObject output = new JsonObject();

        output.put(keyValue, value);
        if (map instanceof JsonObject jsonObject) output.putAll(jsonObject);

        return DataResult.success(output);
    }

    @Override
    public DataResult<Stream<Pair<JsonElement, JsonElement>>> getMapValues(JsonElement input) {
        if (!(input instanceof JsonObject actualInput)) return DataResult.error(() -> "Not a JSON object: " + input);

        return DataResult.success(
                actualInput.entrySet().stream().map(entry -> new Pair<>(new JsonPrimitive(entry.getKey()), entry.getValue()))
        );
    }

    //// fixme: maybe getMapEntries()?


    ////@Override
    ////public DataResult<MapLike<JsonElement>> getMap(JsonElement input) {
    ////    if (!(input instanceof JsonObject inputAsObject)) return DataResult.error(() -> "Not a JSON object: " + input);
    ////    return DataResult.success(new MapLike<>() {

    ////        @Override
    ////        public JsonElement get(JsonElement key) {
    ////            return get(((JsonPrimitive) key).asString());
    ////        }

    ////        @Override
    ////        public JsonElement get(String key) {
    ////            return inputAsObject.get(key);
    ////        }

    ////        @Override
    ////        public Stream<Pair<JsonElement, JsonElement>> entries() {
    ////            return inputAsObject.entrySet().stream().map(entry -> Pair.of(new JsonPrimitive(entry.getKey()), entry.getValue()));
    ////        }

    ////        @Override
    ////        public String toString() {
    ////            return "MapLike[" + inputAsObject + "]";
    ////        }
    ////    });
    ////}

    @Override
    public JsonElement createMap(Stream<Pair<JsonElement, JsonElement>> map) {
        JsonObject result = new JsonObject();
        map.forEach(pair -> result.put(((JsonPrimitive) pair.getFirst()).asString(), pair.getSecond()));
        return result;
    }

    @Override
    public DataResult<Stream<JsonElement>> getStream(JsonElement input) {
        if (!(input instanceof JsonArray actualInput)) return DataResult.error(() -> "Not an array: " + input);
        return DataResult.success(actualInput.stream());
    }

    @Override
    public JsonElement createList(Stream<JsonElement> input) {
        JsonArray result = new JsonArray();
        input.forEach(result::add);
        return result;
    }

    @Override
    public JsonElement remove(JsonElement input, String key) {
        if (!(input instanceof JsonObject actualInput)) return input;

        JsonObject result = new JsonObject();

        for (Map.Entry<String, JsonElement> entry : actualInput.entrySet()) {
            if (!entry.getKey().equals(key)) result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    @Override
    public JsonElement emptyMap() {
        return new JsonObject();
    }

    @Override
    public JsonElement emptyList() {
        return new JsonArray();
    }

    @Override
    public boolean compressMaps() {
        return compressed;
    }

    @Override
    public String toString() {
        return "Jankson";
    }
}
