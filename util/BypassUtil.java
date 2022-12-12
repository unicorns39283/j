package cat.util;

public class BypassUtil extends MinecraftInstance {
    public static float bypass_value = 0.1536f;
    public static void NCPDisabler(){
        mc.getFramebuffer().unbindFramebuffer();
        mc.thePlayer = null;
        mc.fontRendererObj = null;
    }
}
