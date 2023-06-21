package testmod.config;

import com.mojang.authlib.GameProfile;
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
import top.offsetmonkey538.monkeyconfig538.annotation.ConfigEntry;

import static testmod.Testmod.*;

public class ModConfig extends Config {

    @ConfigEntry
    public static int iDoNotHaveAComment = 2;

    @ConfigEntry("But I do have a comment!")
    public static String myString = "Hello, World!";

    @ConfigEntry("This is a very cool item")
    public static Item coolItem = Items.ICE;

    @ConfigEntry("This is a very cool block")
    public static Block coolBlock = Blocks.ICE;

    @ConfigEntry("This item stack is very cool!")
    public static ItemStack coolItemStack = new ItemStack(Items.ICE, 12);

    @ConfigEntry("This block state is very cool!")
    public static BlockState coolBlockState = Blocks.ICE.getDefaultState();

    @ConfigEntry("This is a block pos")
    public static BlockPos coolBlockPos = new BlockPos(123, 60, 321);

    @ConfigEntry("This is a status effect instance")
    public static StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.JUMP_BOOST, 526, 21);

    @ConfigEntry
    public static Vec3d vec3d = new Vec3d(525, 854, 266);

    @ConfigEntry
    public static GameProfile gameProfile = new GameProfile(null, "OffsetMonkey538");

    @ConfigEntry
    public static Identifier identifier = new Identifier("very_cool", "identifier");

    @ConfigEntry("An array of integers!")
    public static int[] myIntArray = new int[] {
            1234,
            4321,
            6789,
            9876
    };

    @ConfigEntry("An array of floats!")
    public static float[] myFloatArray = new float[] {
            12.34f,
            43.21f,
            42.5431f,
            1643.151f
    };

    @ConfigEntry("Nesting level 1")
    public static class NestedThing {
        @ConfigEntry("Very cool boolean")
        public static boolean veryCoolBoolean = true;

        @ConfigEntry("Nesting level 2")
        public static class NestedThing2 {
            @ConfigEntry("Nesting level 3")
            public static class NestedThing3 {
                @ConfigEntry("Nesting level 4")
                public static class NestedThing4 {
                    @ConfigEntry("Nesting level 5")
                    public static class NestedThing5 {
                        @ConfigEntry("Very nice float")
                        public static float aFloatingNumber = 420.69f;
                    }
                }

                @ConfigEntry
                public static boolean anotherBoolean = false;
            }
        }
    }


    @Override
    protected String getName() {
        return MOD_ID;
    }
}
