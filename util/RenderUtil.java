package cat.util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil extends MinecraftInstance {
    public static int delta = 0;
    public static void drawImage(ResourceLocation image, float x, float y, float width, float height, float alpha) {
        GlStateManager.pushMatrix();
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor4f(1.0F, 1.0F, 1.0F, alpha);
        mc.getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        GlStateManager.popMatrix();
    }
    public static float animate(float target, float current, float speed) {
        boolean larger = (target > current);
        speed = range(speed * 10 / delta, 0, 1);
        float dif = Math.max(target, current) - Math.min(target, current);
        float factor = dif * speed;
        if (factor < 0.001f)
            factor = 0.001f;
        if (larger) {
            current += factor;
        } else {
            current -= factor;
        }
        return current;
    }
    public static float range(float v, float min, float max){
        return Math.max(Math.min(v, max), min);
    }
    public static void rect(final float x, final float y, final float x2, final float y2, final int color) {
        Gui.drawRect(x,y,x2,y2, color);
        GlStateManager.resetColor();
    }
    public static void rect(final float x, final float y, final float x2, final float y2, final Color color) {
        Gui.drawRect(x,y,x2,y2,color.getRGB());
        GlStateManager.resetColor();
    }
    public static void rect(final double x, final double y, final double x2, final double y2, final Color color) {
        Gui.drawRect(x,y,x2,y2,color.getRGB());
        GlStateManager.resetColor();
    }
    public static void crop(final float x, final float y, final float x2, final float y2) {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int factor = scaledResolution.getScaleFactor();
        glScissor((int) (x * factor), (int) ((scaledResolution.getScaledHeight() - y2) * factor), (int) ((x2 - x) * factor), (int) ((y2 - y) * factor));
    }
    private static int lastScale;
    private static int lastScaleWidth;
    private static int lastScaleHeight;
    private static Framebuffer buffer;
    private static final ResourceLocation shader = new ResourceLocation("cat/blur.json");
    private static ShaderGroup blurShader;
    public static void initFboAndShader() {
        try {
            blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), shader);
            blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            buffer = blurShader.mainFramebuffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void blur(float x, float y, float x2, float y2, ScaledResolution sc) {
        int factor = sc.getScaleFactor();
        int factor2 = sc.getScaledWidth();
        int factor3 = sc.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null
                || blurShader == null) {
            initFboAndShader();
        }
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        crop(x, y, x2, y2);
        buffer.framebufferHeight = mc.displayHeight;
        buffer.framebufferWidth = mc.displayWidth;
        GlStateManager.resetColor();
        blurShader.loadShaderGroup(mc.timer.renderPartialTicks);
        buffer.bindFramebuffer(true);
        mc.getFramebuffer().bindFramebuffer(true);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
    public static void blur(float x, float y, float x2, float y2){
        GlStateManager.disableAlpha();
        blur(x, y, x2, y2, new ScaledResolution(mc));
        GlStateManager.enableAlpha();
    }
    public static float drawScaledFont(FontRenderer f, String text, float x, float y, int color, boolean shadow, float scale){
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1);
        f.drawString(text, 0, 0, color, shadow);
        GlStateManager.popMatrix();
        return f.getStringWidthF(text) * scale;
    }
    public static void glColor(final Color color) {
        final float red = color.getRed() / 255F;
        final float green = color.getGreen() / 255F;
        final float blue = color.getBlue() / 255F;
        final float alpha = color.getAlpha() / 255F;

        GlStateManager.color(red, green, blue, alpha);
    }
    private static void glColor(final int hex) {
        final float alpha = (hex >> 24 & 0xFF) / 255F;
        final float red = (hex >> 16 & 0xFF) / 255F;
        final float green = (hex >> 8 & 0xFF) / 255F;
        final float blue = (hex & 0xFF) / 255F;

        GlStateManager.color(red, green, blue, alpha);
    }
    
    public static void scissor(double x, double y, double width, double height) {
        final ScaledResolution sr = new ScaledResolution(mc);
        final double scale = sr.getScaleFactor();

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }
//    public static void glColor(int hex) 
//    {
//		float alpha = (hex >> 24 & 0xFF) / 255.0F;
//		float red = (hex >> 16 & 0xFF) / 255.0F;
//		float green = (hex >> 8 & 0xFF) / 255.0F;
//		float blue = (hex & 0xFF) / 255.0F;
//		GL11.glColor4f(red, green, blue, alpha);
//	}
      
    public static void color(float red, float green, float blue, float alpha) 
    {
    	GL11.glColor4f(red / 255.0F, green / 255.0F, blue / 255.0F, alpha / 255.0F);
    }
  
    public static void color(Color color) {
    	color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
    
    public static void drawBorderedRect(double var0, double var2, double var4, double var6, float var8, int var9, int var10) 
    {
        drawRect((float)var0, (float)var2, (float)var4, (float)var6, var10);
        float var11 = (var9 >> 24 & 0xFF) / 255.0F;
        float var12 = (var9 >> 16 & 0xFF) / 255.0F;
        float var13 = (var9 >> 8 & 0xFF) / 255.0F;
        float var14 = (var9 & 0xFF) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(var12, var13, var14, var11);
        GL11.glLineWidth(var8);
        GL11.glBegin(1);
        GL11.glVertex2d(var0, var2);
        GL11.glVertex2d(var0, var6);
        GL11.glVertex2d(var4, var6);
        GL11.glVertex2d(var4, var2);
        GL11.glVertex2d(var0, var2);
        GL11.glVertex2d(var4, var2);
        GL11.glVertex2d(var0, var6);
        GL11.glVertex2d(var4, var6);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
        GL11.glColor4f(255.0F, 1.0F, 1.0F, 255.0F);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    public static void drawRect(double d, double e, double g, double h, int color) 
    {
        if (d < g) 
        {
	          int f3 = (int)d;
	          d = g;
	          g = f3;
        } 
        if (e < h) 
        {
        	int f3 = (int)e;
	          e = h;
	          h = f3;
        } 
        float f31 = (color >> 24 & 0xFF) / 255.0F;
        float f = (color >> 16 & 0xFF) / 255.0F;
        float f1 = (color >> 8 & 0xFF) / 255.0F;
        float f2 = (color & 0xFF) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f31);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(d, h, 0.0D).endVertex();
        worldrenderer.pos(g, h, 0.0D).endVertex();
        worldrenderer.pos(g, e, 0.0D).endVertex();
        worldrenderer.pos(d, e, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
      }
    
 }