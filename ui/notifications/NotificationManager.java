package cat.ui.notifications;

import cat.BlueZenith;
import cat.module.modules.render.HUD;
import cat.module.value.types.BooleanValue;
import cat.util.MinecraftInstance;
import cat.util.RenderUtil;
import cat.util.font.sigma.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.ArrayList;

public class NotificationManager extends MinecraftInstance {
    private static final ArrayList<Notification> notis = new ArrayList<>();
    private static final ArrayList<Notification> removeQueue = new ArrayList<>();
    public static void render() {
        final FontRenderer f = FontUtil.fontSFLight42;
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        float height = 30;
        float margin = 5;
        float inMargin = 5;
        float y = height + margin;
        for (Notification n : notis) {
            float t = Math.max(f.getStringWidthF(n.title), f.getStringWidthF(n.desc));
            float width = 40 + inMargin + t;
            if(System.currentTimeMillis() - n.timeStarted > n.time){
                n.baixarMinecraftFree2021(-(width + margin), 0, 0.15f);
                if(n.yOffset <= 0){
                    removeQueue.add(n);
                }
            }else{
                n.yOffset = height + margin;
                n.baxiarX(width + margin, 0.15f);
            }
            GlStateManager.pushMatrix();
            if(((BooleanValue) BlueZenith.moduleManager.getModule(HUD.class).getValue("Notification blur")).get())
            RenderUtil.blur(res.getScaledWidth() - n.xOffset, res.getScaledHeight() - y, res.getScaledWidth() - n.xOffset + width, res.getScaledHeight() - y + height);
            GlStateManager.translate(res.getScaledWidth() - n.xOffset, res.getScaledHeight() - y, 0);
            RenderUtil.rect(0, 0, width, height, new Color(10,10,30, 120));
            float b = ((n.time - n.getTimePassed()) / n.time) % 1;
            RenderUtil.rect(0, 0, Math.max(width * (1.0f - b), 0.1f), 1, n.type.getColor());
            f.drawString(n.title, FontUtil.I_icon.getStringWidthF(n.type.err) + inMargin * 2, height / 2f - f.FONT_HEIGHT / 2f, n.type.getColor().getRGB());
            FontUtil.I_icon.drawString(n.type.err, inMargin, height / 2f - FontUtil.I_icon.FONT_HEIGHT / 2f, n.type.getColor().getRGB());
            GlStateManager.popMatrix();
            y += n.yOffset;
        }
        notis.removeAll(removeQueue);
        removeQueue.clear();
    }
    public static void addNoti(String title, String desc, NotificationType notiType, long time){
        // i know that desc is unused, i don't what to do that now.
        notis.add(new Notification(title, desc, notiType, time));
    }

    public static void publish(String content, NotificationType type, long ms) {
        notis.add(new Notification(content, "", type, ms));
    }

    public static class Notification {
        public String title, desc;
        public long time;
        public NotificationType type;
        public long timeStarted;
        public float xOffset;
        public float yOffset;
        public Notification(String title, String desc, NotificationType notiType, long time){
            this.title = title;
            this.desc = desc;
            this.time = time;
            this.type = notiType;
            this.timeStarted = System.currentTimeMillis();
            this.xOffset = 0;
            this.yOffset = 0;
        }
        public float getTimePassed(){
            return System.currentTimeMillis() - timeStarted;
        }
        public void baxiarX(float x, float s){
            this.xOffset = RenderUtil.animate(x, this.xOffset, s);
        }
        public void baxiarY(float y, float s){
            this.yOffset = RenderUtil.animate(y, this.yOffset, s);
        }
        public void baixarMinecraftFree2021(float x, float y, float s){
            baxiarX(x, s);
            baxiarY(y, s);
        }
    }

}
