# MonkeyConfig538
[![discord-singular](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-singular_vector.svg)](https://discord.offsetmonkey538.top/)
[![jitpack](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/jitpack_vector.svg)](https://jitpack.io/#top.offsetmonkey538/monkeyconfig538)
[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/monkeyconfig538)

## What is this?
This is a configuration library for Minecraft mods. It is made by and for me, OffsetMonkey538, but I guess other people can use it too if they want -\_o\_-  
The library uses Jankson so you can have *comments* inside of the config file. Making a config should be pretty simple and understandable.  
I do have config screens and mod menu integration in mind, but making a config screen seems like a huge task and I'm just not willing to do that at the moment.

## Great, how do I use it?
If you're just a player, you shouldn't need to do anything as mods *should* include the library. If a mod doesn't, just head over to the [modrinth](https://modrinth.com/mod/monkeyconfig538) page and download it from there like any other mod.

### Usage examples
You can check out the [testmod](https://github.com/OffsetMonkey538/MonkeyConfig538/tree/master/src/testmod/java/testmod) and my [baguette mod](https://github.com/OffsetMonkey538/Baguette/blob/master/src/main/java/com/github/offsetmonkey538/baguette/config/BaguetteConfig.java) for usage examples.  
You can also look at the [javadoc](https://jitpack.io/top/offsetmonkey538/monkeyconfig538/latest/javadoc/)

### Including
Now if you're looking to use the library in your own mod, you'll first have to include it in your project.  
To do that, you first need to add jitpack to the `repositories` block in your `build.gradle` file like this: 
```gradle
repositories {
  // Other repositories

  maven {
    name = "JitPack"
    url = "https://jitpack.io"
    // This part is technically unneeded. Tells gradle to only search for my libraries in this repository.
    // Might make it a bit faster -_o_-
    content {
      // Other stuff

      includeGroup "top.offsetmonkey538"

      // Other stuff
    }
  }

  // Other repositories
}
```

Once you have JitPack, you can include the library in your project like this:
```gradle
dependencies {
  // Other dependencies

  include(modImplementation("top.offsetmonkey538:monkeyconfig538:[LIBRARY_VERSION]"))

  // Other dependencies
}
```
Just make sure to replace `[LIBRARY_VERSION]` with an actually valid version which you can find on [JitPack](https://jitpack.io/#top.offsetmonkey538/monkeyconfig538).

### Using
Alright, now that you have the library included, you can finally start using it.  

#### Creating a config
First, lets make a config class, let's just call it `MyModConfig`:
```java
package my.mod.config;

import top.offsetmonkey538.monkeyconfig538.Config;


public class MyModConfig extends Config {
    
}
```

#### Initializing the config
You'll also have to initialize the config in your mod initializer like this:
```java
@Override
public void onInitialize() {
  // Other stuff


  // This will be the name of the config file.
  // Using for example "cool_stuff" would create the config file at "minecraft_folder/config/cool_stuff.json"
  // It's best to just use your mod id
  ConfigManager.init(new MyModConfig(), "my-mod");

  // Using something like "my-mod/config1" would create the config at "minecraft_folder/config/my-mod/config1.json"
  // So you can have multiple configs for your mod by putting them in a folder.


  // Other stuff
}
```

#### Adding entries
Now, let's actually get to adding a config entry. It's actually really simple!  
Let's say we wanted to store an integer, let's call it "itemUsageTimeInTicks":
```java
public class MyModConfig extends Config {

  // The initial value you set (120 in this case) is the default value that will be used
  // if the config or this specific entry doesn't exist in the file.
  public int itemUsageTimeInTicks = 120;

}
```

### Accessing an entry
Before you can access an entry, you need to add another method to your mod initializer:
```java
public static MyModConfig config() {
  return (MyModConfig) ConfigManager.get("my-mod");
}
```

Finally, all you have to do to access an entry is this:
```java
import static my.mod.ModInitializer.config;

// Other stuff

System.out.println("Item usage time: " + config().itemUsageTimeInTicks);
```

It's as easy as that!

#### Comments
Because the library uses Jankson, you can add comments to the config file.  
Let's take the last example and add a comment `"This is the max usage time for my cool item. It is measured in ticks"` to it:
```java
// Other stuff

@Comment("This is the max usage time for my cool item. It is measured in ticks")
public int itemUsageTimeInTicks = 120;

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

  @Comment("This is configuration for a very cool item")
  public MyCoolItemConfig myCoolItemConfig = new MyCoolItemConfig();
  public static class MyCoolItemConfig {
    @Comment("This is the max usage time for my cool item. It is measured in ticks")
    public int itemUsageTimeInTicks = 120;
  }

  @Comment("This is configuration for another very cool item")
  public MyOtherCoolItemConfig = new MyOtherCoolItemConfig();
  public static class MyOtherCoolItemConfig {
    @Comment("This is the max usage time for my other cool item. It is measured in ticks")
    public int itemUsageTimeInTicks = 60;
  }
}
```
To access those values, you'd use `config().myCoolItemConfig.itemUsageTimeInTicks` and `config().myOtherCoolItemConfig.itemUsageTimeInTicks`.  
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

  return super.configureJankson(builder);
}
```

Then you can use the `MyValues` class as a config entry like any other:
```java
public MyValues = new MyValues(1234, "Hello, World!");
```

Alright, I think that should be everything for the mod. If you have any questions then you can ask me in my [discord server](https://discord.offsetmonkey538.top/).
