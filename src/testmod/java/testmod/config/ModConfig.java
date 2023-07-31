package testmod.config;

import blue.endless.jankson.Comment;
import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import top.offsetmonkey538.monkeyconfig538.Config;

public class ModConfig extends Config {

    public int iDoNotHaveAComment = 2;

    @Comment("But I do have a comment!")
    public String myString = "Hello, World!";

    @Comment("This is a very cool item")
    public Item coolItem = Items.ICE;

    @Comment("This is a very cool block")
    public Block coolBlock = Blocks.ICE;

    @Comment("This item stack is very cool!")
    public ItemStack coolItemStack = new ItemStack(Items.ICE, 12);

    @Comment("This block state is very cool!")
    public BlockState coolBlockState = Blocks.ICE.getDefaultState();

    @Comment("This is a block pos")
    public BlockPos coolBlockPos = new BlockPos(123, 60, 321);

    @Comment("This is a status effect instance")
    public StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.JUMP_BOOST, 526, 21);

    public Vec3d vec3d = new Vec3d(525, 854, 266);

    public GameProfile gameProfile = new GameProfile(null, "OffsetMonkey538");

    public Identifier identifier = new Identifier("very_cool", "identifier");

    // You need to wrap a 'Map.of(...)' in a HashMap constructor because jankson can't modify immutable maps
    public Map<String, Integer> stringToIntegerMap = new HashMap<>(Map.of(
            "Hello!", 26,
            "Goodbye!", 47
    ));

    @Comment("An array of integers!")
    public int[] myIntArray = new int[] {
            1234,
            4321,
            6789,
            9876
    };

    @Comment("An array of floats!")
    public float[] myFloatArray = new float[] {
            12.34f,
            43.21f,
            42.5431f,
            1643.151f
    };

    @Comment("Nesting level 1")
    public NestedThing nestedThing = new NestedThing();
    public static class NestedThing {
        @Comment("Very cool boolean")
        public boolean veryCoolBoolean = true;

        @Comment("Nesting level 2")
        public NestedThing2 nestedThing2 = new NestedThing2();
        public static class NestedThing2 {
            @Comment("Nesting level 3")
            public NestedThing3 nestedThing3 = new NestedThing3();
            public static class NestedThing3 {
                @Comment("Nesting level 4")
                public NestedThing4 nestedThing4 = new NestedThing4();
                public static class NestedThing4 {
                    @Comment("Nesting level 5")
                    public NestedThing5 nestedThing5 = new NestedThing5();
                    public static class NestedThing5 {
                        @Comment("Very nice float")
                        public float aFloatingNumber = 420.69f;
                    }
                }

                public boolean anotherBoolean = false;
            }
        }
    }
}
