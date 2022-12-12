package cat.ui;

import java.awt.Color;
import java.io.IOException;

import cat.ui.alt.GuiAltLogin;
import cat.util.ColorUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiMain extends GuiScreen {
    public void initGui(){
        int j = this.height / 3 + 48;
        this.buttonList.clear();

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options")));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("menu.quit")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, j, I18n.format("menu.singleplayer")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, j + 24, I18n.format("menu.multiplayer")));
        this.buttonList.add(new GuiButton(14, this.width / 2 - 100, j + 24 * 2, "Alt Manager"));
    }
    ResourceLocation bg = new ResourceLocation("cat/ui/bluezenith.jpg");
    String hamburger = "test";
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        ScaledResolution sc = new ScaledResolution(mc);
        //RenderUtil.drawImage(bg, 0, 0, this.width, this.height, 1);
        drawGradientRect(0, 0, this.width, this.height, new Color(0, 0, 69).getRGB(),ColorUtil.getMainColor().getRGB());
        drawGradientRect(0,0, this.width, this.height, new Color(0, 0, 69).getRGB(), ColorUtil.getEpicColor(10).getRGB());
        GlStateManager.pushMatrix();
        float j = this.height / 3.5f + 48;
        Color Z = ColorUtil.getEpicColor(120);
        sussy(mc.fontRendererObj, hamburger, sc.getScaledWidth() / 2f, j - 12, Z.getRGB(), true, 4);
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    public void sussy(FontRenderer f, String s,float x, float y, int color, boolean shadow, float scale){
        GlStateManager.pushMatrix();
        GlStateManager.translate(x - (f.getStringWidth(hamburger) * scale / 2f), y - f.FONT_HEIGHT * scale, 0);
        GlStateManager.scale(scale,scale,1);
        f.drawString(s, 0,0, color, shadow);
        GlStateManager.popMatrix();
    }
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id){
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            case 5:
                this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
                break;
            case 1:
                this.mc.displayGuiScreen(new GuiSelectWorld(this));
                break;
            case 2:
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 4:
                mc.displayGuiScreen(new GuiConfirmShutdown(this));
                break;
            case 14:
                this.mc.displayGuiScreen(new GuiAltLogin(this));
                break;
        }
    }
}
