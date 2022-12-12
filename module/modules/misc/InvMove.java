package cat.module.modules.misc;

import cat.events.impl.UpdateEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

@SuppressWarnings("unused")
public class InvMove extends Module {
    public InvMove() {
        super("InvMove", "", ModuleCategory.MISC, "InventoryMove");
    }

    private final BooleanValue sneak = new BooleanValue("Sneak", false, true, null);

    @Subscribe
    public void onUpdate(UpdateEvent event){
        if(mc.currentScreen instanceof GuiChat || mc.currentScreen == null)
            return;
        set(mc.gameSettings.keyBindForward);
        set(mc.gameSettings.keyBindBack);
        set(mc.gameSettings.keyBindRight);
        set(mc.gameSettings.keyBindLeft);
        set(mc.gameSettings.keyBindJump);
        set(mc.gameSettings.keyBindSprint);
        if(sneak.get())
            set(mc.gameSettings.keyBindSneak);
    }
    private void set(KeyBinding key){
        key.pressed = GameSettings.isKeyDown(key);
    }
}
