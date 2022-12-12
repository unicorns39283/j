package cat.ui.clickgui;

import cat.BlueZenith;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.Value;
import cat.module.value.types.*;
import cat.util.RenderUtil;
import cat.util.font.sigma.FontUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IntellijClickGui extends GuiScreen {
    private final Color background = new Color(43, 43, 43);
    private final Color background2 = new Color(49, 51, 53);
    private final Color background3 = new Color(85, 85, 85);
    private final Color background4 = new Color(74, 136, 199);
    private final Color background5 = new Color(78, 82, 84);
    private final Color background6 = new Color(60, 63, 65);
    private final Color background7 = new Color(50, 50, 50);
    private final Color color1 = new Color(204, 120, 50);
    private final Color color2 = new Color(152, 118, 170);
    private final Color color3 = new Color(169, 183, 198);
    private final Color color4 = new Color(104, 151, 187);
    private final Color color5 = new Color(106, 135, 89);
    private final Color color6 = new Color(96, 99, 102);
    public IntellijClickGui(){
    }
    private final FontRenderer f = FontUtil.fontJetBrainsLight36;
    private final FontRenderer f2 = FontUtil.fontSegoeUI28;
    private final FontRenderer f3 = FontUtil.I_testFont;
    private final ResourceLocation classImage = new ResourceLocation("cat/ui/class.png");
    private final float yIncrement = f.FONT_HEIGHT + 4;
    private final float yIncrement2 = f.FONT_HEIGHT + 4;
    private final float sussyRectHeight = f.FONT_HEIGHT + 6;
    private final float moduleTreeWidth = 150;
    private final float lol = (yIncrement / 2f - getHalfHeight());
    private final float xOffset = 45;
    private final float yOffset = 45;
    private float prevX = 0;
    private float prevY = 0;
    private Module selectedModule = null;
    private List<Value<?>> l = Collections.emptyList();
    private boolean mousePressed = false;
    private float panelHeight = 0;
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        float width = 800;
        float x = xOffset;
        float y = yOffset;
        y = drawSussyRect(x, y, mouseX, mouseY, width);
        x = drawModulesTree(x, y, mouseX, mouseY, width);
        y = drawValues(x, y, mouseX, mouseY, width);
        super.drawScreen(mouseX, mouseY, partialTicks);
        mousePressed = Mouse.isButtonDown(0) || Mouse.isButtonDown(1);
        panelHeight = Math.max(400, sussyRectHeight + 4 + (yIncrement * l.size()) + sussyRectHeight);
    }
    private float drawModulesTree(float x, float y, int mouseX, int mouseY, float width) {
        RenderUtil.rect(x, y, x + moduleTreeWidth, y + panelHeight, background6);
        boolean isOpen = true;
        final String str1 = (isOpen ? "B" : "C") + " A ";
        final float strWidth1 = f3.getStringWidthF(str1);
        float tempY = y;
        final float middle = yIncrement2 / 2f - f3.FONT_HEIGHT / 2f;
        final float middle2 = yIncrement2 / 2f - f2.FONT_HEIGHT / 2f;
        f3.drawString(str1, x + 5, tempY + middle, Color.WHITE.getRGB());
        f2.drawString("Modules", x + strWidth1 + 5, tempY + middle2, Color.WHITE.getRGB());
        tempY += yIncrement2;
        for (int i = 0; i < ModuleCategory.values().length; i++) {
            ModuleCategory cat = ModuleCategory.values()[i];
            final String str = (cat.showContent ? "B" : "C") + " A ";
            final float strWidth = f3.getStringWidthF(str1);
            f3.drawString(str, x + strWidth + 5, tempY + middle, Color.WHITE.getRGB());
            f2.drawString(cat.displayName, x + (strWidth * 2) + 5, tempY + middle2, Color.WHITE.getRGB());
            if(i(mouseX, mouseY, x, tempY, x + moduleTreeWidth, tempY + yIncrement2) && Mouse.isButtonDown(1) && !mousePressed){
                cat.showContent = !cat.showContent;
                toggleSound();
            }
            tempY += yIncrement2;
            if(cat.showContent){
                for (Module m : BlueZenith.moduleManager.getModules()) {
                    if(m.getCategory() == cat){
                        if(i(mouseX, mouseY, x, tempY, x + moduleTreeWidth, tempY + yIncrement2) && !mousePressed){
                            if(Mouse.isButtonDown(0)){
                                m.toggle();
                                toggleSound();
                            }else if(Mouse.isButtonDown(1) && !m.getValues().isEmpty()){
                                selectedModule = m;
                                toggleSound();
                            }
                        }
                        f2.drawString(m.getName() + ".java", x + (strWidth * 2) + 5, tempY + middle2, m.getState() ? Color.WHITE.getRGB() : Color.GRAY.getRGB());
                        tempY += yIncrement2;
                    }
                }
            }
        }
        return x + moduleTreeWidth;
    }
    private float drawSussyRect(float x, float y, int mouseX, int mouseY, float width){
        width -= moduleTreeWidth;
        RenderUtil.rect(x, y, x + width, y + sussyRectHeight + 2, background7.getRGB());
        RenderUtil.rect(x, y + 1, x + width - 1, y + sussyRectHeight + 1, background6.getRGB());

        if(selectedModule != null){
            String displayString = selectedModule.getName() + ".java";
            float moduleNameWidth = f.getStringWidthF(displayString);
            float rectWidth = 9 + 5 + 4 + moduleNameWidth;
            float classIcon = 9;
            RenderUtil.rect(x + 1, y + 1, x + rectWidth - 1, y + sussyRectHeight - 1, background5.getRGB());
            RenderUtil.rect(x + 1, y + 1, x + rectWidth - 1, y + sussyRectHeight - 1, background5.getRGB());
            RenderUtil.rect(x + 1, y + sussyRectHeight - 1, x + rectWidth - 1, y + sussyRectHeight + 1, background4.getRGB());
            RenderUtil.drawImage(classImage, x + 4, y + (sussyRectHeight / 2f - classIcon / 2f), classIcon, classIcon, 1);
            f2.drawString(displayString, x + classIcon + 6, y + (sussyRectHeight / 2f - f2.FONT_HEIGHT / 2f), Color.WHITE.getRGB());
        }
        return y + sussyRectHeight + 2;
    }
    private float drawValues(float x, float aY, int mouseX, int mouseY, float width){
        width -= moduleTreeWidth * 2f;
        final float rectWidth = 29;
        RenderUtil.rect(x, aY, x + width, aY + panelHeight, background.getRGB());
        RenderUtil.rect(x - 5, aY, x + rectWidth, aY + panelHeight, background2.getRGB());
        RenderUtil.rect(x + rectWidth, aY, x + rectWidth + 1, aY + panelHeight, background3.getRGB());
        float y = aY;
        if(selectedModule != null){
            l = selectedModule.getValues().stream().filter(Value::isVisible).collect(Collectors.toList());
            String prefix = "public";
            for (int i = 0; i < l.size(); i++) {
                Value<?> v = l.get(i);
                f.drawString((i + 1) + "", x, y + (yIncrement / 2f - getHalfHeight()), color6.getRGB());
                String valueName = v.name.replaceAll(" ", "_");
                String displayValue = "${"+color3.getRGB()+"}Object ${"+color2.getRGB()+"}"+valueName+" ${"+color3.getRGB()+"}= ${"+color1.getRGB()+"}null";
                if(v instanceof IntegerValue){
                    displayValue = "int ${"+color2.getRGB()+"}" + valueName + "${"+color3.getRGB()+"} = ${"+color4.getRGB()+"}" + v.get();
                }else if(v instanceof FloatValue){
                    displayValue = "float ${"+color2.getRGB()+"}" + valueName + "${"+color3.getRGB()+"} = ${"+color4.getRGB()+"}" + v.get() + "f";
                }else if(v instanceof StringValue){
                    displayValue = "${"+color3.getRGB()+"}String ${"+color2.getRGB()+"}" + valueName + "${"+color3.getRGB()+"} = ${"+color5.getRGB()+"}\"" + v.get() + "\"";
                }else if(v instanceof ModeValue){
                    displayValue = "${"+color3.getRGB()+"}String[] ${"+color2.getRGB()+"}" + valueName + "${"+color3.getRGB()+"} = ${"+color1.getRGB()+"}new ${"+color3.getRGB()+"}String[]{${"+color5.getRGB()+"}\""+ v.get() + "\"${"+color3.getRGB()+"}}";
                    String[] f = new String[]{};
                    ModeValue l = (ModeValue) v;
                    if(i(mouseX, mouseY, x, y, x + width, y + yIncrement) && Mouse.isButtonDown(0) && !mousePressed){
                        l.next();
                        toggleSound();
                    }
                }else if(v instanceof BooleanValue){
                    displayValue = "boolean ${"+color2.getRGB()+"}" + valueName + "${"+color3.getRGB()+"} = ${"+color1.getRGB()+"}" + v.get();
                    BooleanValue f = (BooleanValue) v;
                    if(i(mouseX, mouseY, x, y, x + width, y + yIncrement) && Mouse.isButtonDown(0) && !mousePressed){
                        f.set(!f.get());
                        toggleSound();
                    }
                }else if(v instanceof ActionValue){
                    displayValue = "${"+color3.getRGB()+"}Runnable ${"+color2.getRGB()+"}" + valueName + "${"+color3.getRGB()+"} = () -> {}";
                    ActionValue f = (ActionValue) v;
                    if(i(mouseX, mouseY, x, y, x + width, y + yIncrement) && Mouse.isButtonDown(0) && !mousePressed){
                        f.next();
                        toggleSound();
                    }
                }
                f.drawString(prefix + " " + displayValue + "${"+color1.getRGB()+"};", x + rectWidth + 5, y + lol, color1.getRGB());
                y += yIncrement;
            }
        }
        return y;
    }
    public boolean i(int mouseX, int mouseY, float x, float y, float x2, float y2){
        return mouseX >= x && mouseY >= y && mouseX <= x2 && mouseY <= y2;
    }
    public final void toggleSound(){
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }
    private float getHalfHeight(){
        return f.FONT_HEIGHT / 2f;
    }
    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }
}
