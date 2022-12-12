package cat.ui.clickgui.components.Panels;

import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.Value;
import cat.module.value.types.*;
import cat.ui.clickgui.components.Panel;
import cat.util.ClientUtils;
import cat.util.MillisTimer;
import cat.util.RenderUtil;
import cat.util.font.sigma.FontUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModulePanel extends Panel implements MouseClickedThing {
    private final ArrayList<Module> modules = new ArrayList<>();
    private final MillisTimer timer = new MillisTimer();
    private final ModuleCategory category;
    private StringValue selectedTextField = null;
    private float textFieldCounter = 0;
    private boolean wasPressed = false;
    private Value<?> sliderVal = null;
    private Module lastMod = null;
    private final FontRenderer f2;
    boolean listeningForKey = false;
    List<Module> bindListeners = new ArrayList<>();
    public ModulePanel(float x, float y, ModuleCategory category){
        super(x, y, "Modules " + category.displayName);
        this.category = category;
        f = FontUtil.fontSFLight35;
        f2 = FontUtil.I_testFont2;
        mHeight = f.FONT_HEIGHT + 14;
    }
    public Panel calculateSize(){
        float pY = mHeight;
        width = f.getStringWidth(category.displayName) + 12;
        for (Module m : modules) {
            if(f.getStringWidth(m.getName()) + 12 > width){
                width = f.getStringWidth(m.getName()) + 12;
            }
            pY += mHeight;
        }
        width = Math.max(width, 120);
        height = pY;
        return this;
    }
    public ArrayList<Module> getModules(){
        return modules;
    }
    public void addModule(Module mod){
        this.modules.add(mod);
        mod.clickGuiAnim.setReversed(true);
    }
    public void drawPanel(int mouseX, int mouseY, float partialTicks, boolean handleClicks){
        float w = width;
        float h = f.FONT_HEIGHT + 8;
        Color mainColor = click.main_color;
        Color backgroundColor = click.backgroundColor;
        Color settingsColor = backgroundColor.brighter();

        RenderUtil.rect(x, y, x + width, y + mHeight, new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), click.ba.get()));
        f.drawString(category.displayName, x + 4, y + mHeight / 2f - f.FONT_HEIGHT / 2f, Color.WHITE.getRGB());
        float y1 = y + mHeight;
        for (Module m : modules) {
            if(!this.showContent) continue;

            List<Value<?>> vl = m.getValues().stream().filter(Value::isVisible).collect(Collectors.toList());
            boolean hovering = i(mouseX, mouseY, x, y1, x + width, y1 + mHeight);
            if(hovering && !wasPressed) {
                if(Mouse.isButtonDown(2)) {
                    listeningForKey = true;
                    if(!bindListeners.contains(m))
                    bindListeners.add(m);
                }
                if(Mouse.isButtonDown(0)){
                    m.toggle();
                    toggleSound();
                }
                if(Mouse.isButtonDown(1) && !vl.isEmpty()){
                    if(lastMod != null && lastMod != m && click.closePrevious.get()) lastMod.showSettings = false;
                    lastMod = m;
                    m.showSettings = !m.showSettings;
                    m.clickGuiAnim.setReversed(!m.showSettings);
                    toggleSound();
                    timer.reset();
                }
            }

            RenderUtil.rect(x, y1, x + width, y1 + mHeight, backgroundColor);
            f.drawString(m.getName(), x + 5, y1 + (mHeight / 2f - f.FONT_HEIGHT / 2f), m.getState() ? mainColor.getRGB() : mainColor.darker().darker().getRGB());
            if((hovering || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) && m.keyBind > 0 && !bindListeners.contains(m)) {
                String bindString = "[" + Keyboard.getKeyName(m.keyBind) + "]";
                f.drawString(bindString, x + width - f.getStringWidthF(bindString) - 3, y1 + (mHeight / 2f - f.FONT_HEIGHT / 2f), Color.GRAY.getRGB());
            } else if(bindListeners.contains(m)) {
                String bindString = "[...]";
                f.drawString(bindString, x + width - f.getStringWidthF(bindString) - 3, y1 + (mHeight / 2f - f.FONT_HEIGHT / 2f), Color.GRAY.getRGB());
            }
            //else f.drawString(m.keyBind > 0 ? "[" + Keyboard.getKeyName(m.keyBind) + "...]" : "[...]", x + 5, y1 + (mHeight / 2f - f.FONT_HEIGHT / 2f), mainColor.getRGB());
            y1 += mHeight;
            if(canRender(m) && (timer.hasTimeReached(35) || !click.closePrevious.get())){
                float y2 = y1; // im sorry for too many variables :(
                float why = 0; // lol
                if(m.arrayListHeight == 0) m.arrayListHeight = h * vl.size();
                if(click.animate.get()) m.clickGuiAnim.setMax(m.arrayListHeight).update();

                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                RenderUtil.crop(x, y1, x + width, y1 + d(m.arrayListHeight, m.clickGuiAnim.getValue()) + 1);
                for (Value<?> v : vl) {
                    float y2r = y2 + h;
                    float she_lied = h / 2f - f.FONT_HEIGHT / 2f;
                    switch (v.getClass().getSimpleName()){
                        case "FontValue":
                            FontValue fontValue = (FontValue) v;
                            RenderUtil.rect(x, y2, x + width, y2 + h, settingsColor);
                            f.drawString(fontValue.name, x + 5, y2 + she_lied, mainColor.getRGB());
                            f.drawString(fontValue.get().getName(), x + width - f.getStringWidth(fontValue.get().getName()) - 5, y2 + she_lied, Color.GRAY.getRGB());
                            if(i(mouseX, mouseY, x, y2, x + width, y2 + h) && !wasPressed && handleClicks) {
                                if(Mouse.isButtonDown(0)) {
                                    fontValue.next();
                                    toggleSound();
                                } else if(Mouse.isButtonDown(1)) {
                                    fontValue.previous();
                                    toggleSound();
                                }
                            }
                            y2 += h;
                            why += h;
                            break;
                        case "ModeValue":
                            ModeValue modeValue = (ModeValue) v;
                            RenderUtil.rect(x, y2, x + width, y2 + h, settingsColor);
                            f.drawString(modeValue.name, x + 5, y2 + she_lied, mainColor.getRGB());
                            f.drawString(modeValue.get(), x + width - f.getStringWidth(modeValue.get()) - 5, y2 + she_lied, Color.GRAY.getRGB());
                            if(i(mouseX, mouseY, x, y2, x + width, y2 + h) && !wasPressed && handleClicks) {
                                if(Mouse.isButtonDown(0)) {
                                    modeValue.next();
                                    toggleSound();
                                } else if(Mouse.isButtonDown(1)) {
                                    modeValue.previous();
                                    toggleSound();
                                }
                            }
                            y2 += h;
                            why += h;
                            break;
                        case "FloatValue":
                            FloatValue floatValue = (FloatValue) v;
                            float a = x + w * (Math.max(floatValue.min, Math.min(floatValue.get(), floatValue.max)) - floatValue.min) / (floatValue.max - floatValue.min);
                            RenderUtil.rect(x, y2, x + width, y2 + h, settingsColor);
                            RenderUtil.rect(x, y2, x + w, y2r, new Color(63, 65, 68));
                            RenderUtil.rect(x, y2, a, y2r, mainColor.darker().darker());
                            f.drawString(floatValue.name + ": " + floatValue.get(), x + 4, y2 + she_lied, mainColor.getRGB());

                            if (Mouse.isButtonDown(0) && handleClicks && ((i(mouseX, mouseY, x, y2, x + w, y2r) && sliderVal == null) || sliderVal == v)) {
                                sliderVal = v;
                                double i = MathHelper.clamp_double(((double) mouseX - (double) x) / ((double) w - 3), 0, 1);

                                BigDecimal bigDecimal = new BigDecimal(Double.toString((floatValue.min + (floatValue.max - floatValue.min) * i)));
                                bigDecimal = bigDecimal.setScale(2, 4);
                                floatValue.set(bigDecimal.floatValue());
                            } else if (!Mouse.isButtonDown(0) && sliderVal == v) {
                                sliderVal = null;
                            }

                            y2 += h;
                            why += h;
                            break;
                        case "IntegerValue":
                            IntegerValue intValue = (IntegerValue) v;
                            a = x + w * (Math.max(intValue.min, Math.min(intValue.get(), intValue.max)) - intValue.min) / (intValue.max - intValue.min);
                            RenderUtil.rect(x, y2, x + width, y2 + h, settingsColor);
                            RenderUtil.rect(x, y2, x + w, y2r, new Color(63, 65, 68));
                            RenderUtil.rect(x, y2, a, y2r, mainColor.darker().darker());
                            f.drawString(intValue.name + ": " + intValue.get(), x + 4, y2 + she_lied, mainColor.getRGB());

                            if (Mouse.isButtonDown(0) && handleClicks && ((i(mouseX, mouseY, x, y2, x + w, y2r) && sliderVal == null) || sliderVal == v)) {
                                sliderVal = v;
                                double i = MathHelper.clamp_double(((double) mouseX - (double) x) / ((double) w - 3), 0, 1);

                                BigDecimal bigDecimal = new BigDecimal(Double.toString((intValue.min + (intValue.max - intValue.min) * i)));
                                bigDecimal = bigDecimal.setScale(2, 4);
                                intValue.set(bigDecimal.intValue());
                            } else if (!Mouse.isButtonDown(0) && sliderVal == v) {
                                sliderVal = null;
                            }

                            y2 += h;
                            why += h;
                            break;
                        case "StringValue":
                            StringValue val = (StringValue) v;
                            String typingIndicator = (selectedTextField != null && selectedTextField == val && textFieldCounter > 0.5 ? "_" : "");
                            RenderUtil.rect(x, y2, x + width, y2 + h, settingsColor);
                            if(val.get().isEmpty())
                                f.drawString(val.name + "... " + typingIndicator, x + width/2f - f.getStringWidthF(val.name + "... ")/2f, y2 + she_lied, Color.GRAY.getRGB());
                            else f.drawString(val.get() + typingIndicator, x + width/2f - f.getStringWidthF(val.get() + " ")/2f, y2 + she_lied, Color.WHITE.getRGB());
                            if (i(mouseX, mouseY, x, y2, x + width, y2 + h) && (Mouse.isButtonDown(0)) && !wasPressed && handleClicks) {
                                this.selectedTextField = val;
                                toggleSound();
                            } else if(this.selectedTextField != null && Mouse.isButtonDown(0) && !i(mouseX, mouseY, x, y, x + width, y + 10) && handleClicks && !wasPressed) {
                                this.selectedTextField = null;
                                wasPressed = true;
                            }
                            y2 += h;
                            why += h;
                            break;
                        case "BooleanValue" :
                            float downscale = 0.45f;
                            BooleanValue bValue = (BooleanValue) v;
                            RenderUtil.rect(x, y2, x + width, y2 + h, settingsColor);
                            RenderUtil.rect(x + width - 14.5 + downscale, y2 + 2f + downscale, x + width - 2.2f - downscale, y2 + h - 2f - downscale, new Color(60, 60, 60));
                            if(bValue.get())
                                RenderUtil.rect(x + width - 13f + downscale, y2 + 3.5f + downscale, x + width - 3.5f - downscale, y2 + h - 3.5f - downscale, mainColor);
                            f.drawString(bValue.name, x + 5, y2 + she_lied, mainColor.getRGB());
                            if (i(mouseX, mouseY, x, y2, x + width, y2 + h) && (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) && !wasPressed && handleClicks) {
                                bValue.next();
                                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                            }
                            y2 += h;
                            why += h;
                            break;
                        case "ActionValue":
                            RenderUtil.rect(x, y2, x + width, y2 + h, settingsColor);
                            f.drawString(v.name, x + width/2-f.getStringWidth(v.name)/2f, y2 + she_lied, mainColor.getRGB());
                            if (i(mouseX, mouseY, x, y2, x + width, y2 + h) && (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) && !wasPressed && handleClicks) {
                                v.next();
                                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                            }
                            y2 += h;
                            why += h;
                            break;
                        //TODO: Fix this bullshit
                        case "ListValue" :
                            ListValue lValue = (ListValue) v;
                            RenderUtil.rect(x, y2, x + width, y2 + h, settingsColor);
                            f.drawString(lValue.name, x + width/2f - f.getStringWidth(lValue.name)/2f, y2 + she_lied, Color.GRAY.getRGB(), true);
                            if (i(mouseX, mouseY, x, y2, x + width, y2 + h) && (Mouse.isButtonDown(0)) && !wasPressed && handleClicks) {
                                lValue.expanded = !lValue.expanded;
                                toggleSound();
                            }
                            y2 += h;
                            why += h;
                            for (String i : lValue.getOptions()) {
                                if(!lValue.expanded) continue;
                                boolean free_download = lValue.getOptionState(i);
                                String z = free_download ? "F" : "D";
                                RenderUtil.rect(x, y2, x + width, y2 + h, settingsColor);
                                f2.drawString(z, x + 6, y2 + (h / 2f - f2.FONT_HEIGHT / 2f) + 1, mainColor.getRGB());
                                f.drawString(i, x + width/2f - f.getStringWidth(i)/2f, y2 + she_lied, Color.WHITE.getRGB());
                                if (i(mouseX, mouseY, x, y2, x + width, y2 + h) && (Mouse.isButtonDown(0)) && !wasPressed && handleClicks) {
                                    toggleSound();
                                    lValue.toggleOption(i);
                                }
                                y2 += h;
                                why += h;
                            }
                            break;
                    }
                }
                m.arrayListHeight = why;
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                y1 += d(m.arrayListHeight, m.clickGuiAnim.getValue());
            }
        }
        height = y1 - y;
        textFieldCounter = (textFieldCounter + RenderUtil.delta * 0.003f) % 1;
        wasPressed = Mouse.isButtonDown(0) || Mouse.isButtonDown(1);
    }
    private float d(float h, float value){
        return click.animate.get() ? value : h;
    }
    private boolean canRender(Module m){
        return click.animate.get() ? m.clickGuiAnim.isReversed() ? m.clickGuiAnim.getValue() != m.clickGuiAnim.getMin() : m.clickGuiAnim.getValue() <= m.clickGuiAnim.getMax() : m.showSettings;
    }

    public void keyTyped(char charTyped, int keyCode){
        if(listeningForKey && !Mouse.isButtonDown(2) && keyCode != 28 && keyCode != 1 && keyCode != 54 && keyCode != 42) {
            ClientUtils.fancyMessage("sexed at " + bindListeners.size());
            bindListeners.forEach(k -> k.setKeybind(keyCode));
            listeningForKey = false;
            bindListeners.clear();
        }
        Keyboard.enableRepeatEvents(true);
        if(selectedTextField == null || selectedTextField.get() == null){
            if (keyCode == 1 && selectedTextField == null) {
                mc.displayGuiScreen(null);

                if (mc.currentScreen == null) {
                    mc.setIngameFocus();
                }
            }
            return;
        }
        String fieldText = selectedTextField.get();
        if(keyCode == 14) {
            selectedTextField.set(fieldText.substring(0, fieldText.length() > 0 ? fieldText.length() - 1 : 0));
        }else if(keyCode == 28 || keyCode == Keyboard.KEY_ESCAPE){
            selectedTextField = null;
        }else if(!Character.isISOControl(charTyped)){
            selectedTextField.set(fieldText + charTyped);
        }
    }

    @Override
    public void onClick(int button) {
        if(listeningForKey) {
            switch (button) {
                case 0:
                    bindListeners.clear();
                    listeningForKey = false;
                break;

                case 1:
                    bindListeners.forEach(mod -> {
                       if(mod.keyBind > 0)
                       mod.setKeybind(0);
                    });
                    bindListeners.clear();
                    listeningForKey = false;
                break;
            }
        }
    }
}
