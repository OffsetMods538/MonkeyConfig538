package testmod.config;

import top.offsetmonkey538.monkeyconfig538.Config;
import top.offsetmonkey538.monkeyconfig538.annotation.ConfigEntry;

import static testmod.Testmod.*;

public class ModConfig extends Config {

    @ConfigEntry
    public static int iDoNotHaveAComment = 2;

    @ConfigEntry("But I do have a comment!")
    public static String myString = "Hello, World!";

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
