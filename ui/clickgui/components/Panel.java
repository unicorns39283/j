package cat.ui.clickgui.components;

import cat.BlueZenith;
import cat.module.modules.render.ClickGUI;
import cat.util.MinecraftInstance;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

public class Panel extends MinecraftInstance {
    protected FontRenderer f = mc.fontRendererObj;
    public float x, y, prevX, prevY, width, height, mHeight;
    public boolean showContent;
    public String id;
    protected final ClickGUI click = (ClickGUI) BlueZenith.moduleManager.getModule(ClickGUI.class);
    public Panel(float x, float y, String id){
        this.x = x;
        this.y = y;
        showContent = true;
        this.id = id;
    }
    public Panel calculateSize(){
        width = 120;
        return this;
    }
    public void drawPanel(int mouseX, int mouseY, float partialTicks, boolean handleClicks){}

    public void keyTyped(char charTyped, int keyCode){}

    public boolean i(int mouseX, int mouseY, float x, float y, float x2, float y2){
        return mouseX >= x && mouseY >= y && mouseX <= x2 && mouseY <= y2;
    }
    public final void toggleSound(){
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

}
