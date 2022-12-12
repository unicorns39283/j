package cat.ui.clickgui.components.Panels;

import cat.ui.clickgui.components.Panel;
import cat.util.EntityManager;
import cat.util.RenderUtil;
import cat.util.font.sigma.FontUtil;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class TargetsPanel extends Panel {
    private final ArrayList<EntityManager.Targets> targets = new ArrayList<>();
    private boolean wasButtonDown = false;
    public TargetsPanel(float x, float y) {
        super(x, y, "Targets");
        f = FontUtil.fontSFLight35;
        mHeight = f.FONT_HEIGHT + 14;
        targets.addAll(Arrays.asList(EntityManager.Targets.values()));
        width = 120;
    }
    public void drawPanel(int mouseX, int mouseY, float partialTicks, boolean handleClicks){
        Color mainColor = click.main_color;
        Color backgroundColor = click.backgroundColor;
        RenderUtil.rect(x, y, x + width, y + mHeight, new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), click.ba.get()));
        f.drawString("Targets", x + 4, y + mHeight / 2f - f.FONT_HEIGHT / 2f, Color.WHITE.getRGB());
        if(!showContent) return;
        float y = this.y + mHeight;
        for (EntityManager.Targets tar : targets) {
            RenderUtil.rect(x, y, x + width, y + mHeight, backgroundColor);
            f.drawString(tar.displayName, x + 5, y + (mHeight / 2f - f.FONT_HEIGHT / 2f), tar.on ? mainColor.getRGB() : mainColor.darker().darker().getRGB());
            if(i(mouseX, mouseY, x, y, x + width, y + mHeight) && Mouse.isButtonDown(0) && !wasButtonDown && handleClicks){
                tar.on = !tar.on;
                toggleSound();
            }
            y += mHeight;
        }
        wasButtonDown = Mouse.isButtonDown(0);
    }
}
