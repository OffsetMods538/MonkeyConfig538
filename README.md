# MonkeyConfig538
[![discord-singular](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-singular_vector.svg)](https://discord.offsetmonkey538.top/)
[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/monkeyconfig538)

## What is this?
This is a configuration library for Minecraft mods. It is made by and for me, OffsetMonkey538, but I guess other people can use it too if they want -\_o\_-  
The library uses Jankson so you can have *comments* inside of the config file. Making a config should be pretty simple and understandable.  
I do have config screens and mod menu integration in mind, but making a config screen seems like a huge task and I'm just not willing to do that at the moment.

## Great, how do I use it?
If you're just a player, you shouldn't need to do anything as mods *should* include the library. If a mod doesn't, just head over to the [modrinth](https://modrinth.com/mod/monkeyconfig538) page and download it from there like any other mod.

### Usage examples
You can check out the [testmod](https://github.com/OffsetMonkey538/MonkeyConfig538/tree/master/src/testmod/java/testmod) and my [baguette mod](https://github.com/OffsetMonkey538/Baguette/blob/master/src/main/java/com/github/offsetmonkey538/baguette/config/BaguetteConfig.java) for usage examples.

### Including
Now if you're looking to use the library in your own mod, you'll first have to include it in your project.  
To do that, you first need to add modrinth maven to the `repositories` block in your `build.gradle` file like this: 
```gradle
repositories {
  // Other repositories

  maven {
    name = "Modrinth"
    url = "https://api.modrinth.com/maven"
    content {
      includeGroup "maven.modrinth"
    }
  }

  // Other repositories
}
```

Once you have the modrinth maven repository, you can include the library in your project like this:
```gradle
dependencies {
  // Other dependencies

  include(modImplementation("maven.modrinth:monkeyconfig538:[LIBRARY_VERSION]"))

  // Other dependencies
}
```
Just make sure to replace `[LIBRARY_VERSION]` with an actually valid version which you can find on [Modrinth](https://modrinth.com/mod/monkeyconfig538/versions).

### Using
Alright, now that you have the library included, you can finally start using it.  

#### Creating a config
First, lets make a config class, let's just call it `MyModConfig`:
```java
package my.mod.config;

import top.offsetmonkey538.monkeyconfig538.Config;


public class MyModConfig extends Config {


  // You have to override the `getName()` method.
  @Override
  protected String getName() {
    // This will be the name of the config file.
    // Returning for example "cool_stuff" would create the config file at "minecraft_folder/config/cool_stuff.json"
    // It's best to just return your mod id
    return "my-mod";

    // Returning something like "my-mod/config1" would create the config at "minecraft_folder/config/my-mod/config1.json"
    // So you can have multiple configs for your mod by putting them in a folder.
  }
}
```

#### Initializing the config
You'll also have to initialize the config in your mod initializer like this:
```java
@Override
public void onInitialize() {
  // Other stuff

  new MyModConfig.init();

  // Other stuff
}
```
There also exist `save()` and `load()` methods, they're pretty self explanatory.

#### Adding entries
Now, let's actually get to adding a config entry. It's actually really simple!  
Let's say we wanted to store an integer, let's call it "itemUsageTimeInTicks":
```java
public class MyModConfig extends Config {

  // Each config entry has to be annotated with the "ConfigEntry" annotation.
  @ConfigEntry
  // All entries have to be public and static.
  // The initial value you set (120 in this case) is the default value that will be used
  // if the config or this specific entry doesn't exist in the file.
  public static int itemUsageTimeInTicks = 120;
  
}
```

To access that value, all you have to do is this:
```java
System.out.println("Item usage time: " + MyModConfig.itemUsageTimeInTicks);
```

It's as easy as that!

#### Comments
Because the library uses Jankson, you can add comments to the config file.  
Let's take the last example and add a comment `"This is the max usage time for my cool item. It is measured in ticks"` to it:
```java
// Other stuff

@ConfigEntry("This is the max usage time for my cool item. It is measured in ticks")
public static int itemUsageTimeInTicks = 120;

// Other stuff
```
Now the generated config file should look like this:
```json5
{
  // This is the max usage time for my cool item. It is measured in ticks
  "itemUsageTimeInTicks": 120
}
```

#### Nesting
Want to organize your config file? You can do that with nesting!  
Nested entries are also very easy to create. All you have to do is add a nested class to your config class.  
Let's use the example from above:
```java
public class MyModConfig extends Config {

  @ConfigEntry("This is configuration for a very cool item")
  public static class MyCoolItemConfig {
    @ConfigEntry("This is the max usage time for my cool item. It is measured in ticks")
    public static int itemUsageTimeInTicks = 120;
  }

  @ConfigEntry("This is configuration for another very cool item")
  public static class MyOtherCoolItemConfig {
    @ConfigEntry("This is the max usage time for my other cool item. It is measured in ticks")
    public static int itemUsageTimeInTicks = 60;
  }
}
```
To access those values, you'd use `MyModConfig.MyCoolItemConfig.itemUsageTimeInTicks` and `MyModConfig.MyOtherCoolItemConfig.itemUsageTimeInTicks`.  
The generated config file would look like this:
```json5
{
  // This is configuration for a very cool item
  "MyCoolItemConfig": {
    // This is the max usage time for my cool item. It is measured in ticks
    "itemUsageTimeInTicks": 120
  },
  // This is configuration for another very cool item
  "MyOtherCoolItemConfig": {
    // This is the max usage time for my other cool item. It is measured in ticks
    "itemUsageTimeInTicks": 60
  }
}
```

#### (de)serializers
Alright, we know that we can add normal values, but you might want to store more complicated things in your config.  
For that, we have serializers and deserializers.

##### Default (de)serializers
I have included Minecraft-related (de)serializers in the library.

You can check out all default serializers in the `configureJankson` method in the [`Config`](https://github.com/OffsetMonkey538/MonkeyConfig538/blob/master/src/main/java/top/offsetmonkey538/monkeyconfig538/Config.java) class.

Most things that have a registry are included, this includes `Item`, `Block`, `Potion`, `Enchantment`, etc. They are just serialized into their `Identifier` (`"minecraft:apple"`).  
Speaking of `Identifier`s, they also have a (de)serializer, they are just serialized into a string (`"minecraft:apple"`).

Things that have codecs are also included.  
These include:
- ItemStack: Serialized into an item identifier, stack count and nbt.
- BlockState: Serialized into a block identifier.
- BlockPos: Serialized into an int array.
- Vec3d: Serialized into a double array.

There's also a serializer for StatusEffectInstances.

##### Custom (de)serializers
You can also add custom (de)serializers for your own classes.  
For example let's create a record called `MyValues` and have it contain and integer and a string:
```java
public record MyValues(int coolNumber, String coolText) {

}
```

Now we need to make a serializer for it, lets call it `MyValuesSerializer`:
```java
public class MyValuesSerializer implements ConfigSerializer<MyValues> {
  @Override
  public JsonElement toJson(MyValues value, Marshaller marshaller) {
    JsonObject result = new JsonObject();

    result.put("coolNumber", marshaller.serialize(value.coolNumber);
    result.put("coolText", marshaller.serialize(value.coolText);

    return result;
  }

  @Override
  // Returning null from this method will just make it use the default value.
  public MyValues fromJson(JsonElement json, Marshaller marshaller) {
    if (!(json instanceof JsonObject jsonObject)) return null;

    Integer coolNumber = marshaller.marshall(int.class, jsonObject.get("coolNumber"));
    String coolText = marshaller.marshall(String.class, jsonObject.get("coolText"));

    return new MyValues(coolNumber, coolText);
  }
}
```

Now we just need to register it in our `MyModConfig` class and then we can use it.  
We have to override the `configureJankson` method in our `MyModConfig` class to then register the serializer in it:
```java
@Override
protected Jankson.Builder configureJankson(Jankson.Builder builder) {
  registerSerializer(builder, MyValues.class, new MyValuesSerializer());

  return builder;
}
```

Then you can use the `MyValues` class as a config entry like any other:
```java
@ConfigEntry
public static MyValues = new MyValues(1234, "Hello, World!");
```

Alright, I think that should be everything for the mod. If you have any questions then you can ask me in my [discord server](https://discord.offsetmonkey538.top/).
