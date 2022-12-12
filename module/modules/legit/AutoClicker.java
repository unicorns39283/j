package cat.module.modules.legit;

import org.lwjgl.input.Mouse;

import com.google.common.eventbus.Subscribe;

import cat.events.impl.UpdateEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.IntegerValue;
import cat.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;

public class AutoClicker extends Module
{
	public AutoClicker() { super("AutoClicker", "", ModuleCategory.LEGIT, ""); }

	private IntegerValue minCPS = new IntegerValue("Min CPS", 8, 1, 20, 1, true, null);
	private IntegerValue maxCPS = new IntegerValue("Max CPS", 12, 1, 20, 1, true, null);
	private BooleanValue randomize = new BooleanValue("Randomize", true, true);
	private BooleanValue onlySword = new BooleanValue("Only Sword", false, true);
	private BooleanValue breakBlocks = new BooleanValue("Break Blocks", false, true);
	private BooleanValue inventoryFill = new BooleanValue("Inventory Fill", false, true);
	private BooleanValue blockhit = new BooleanValue("Blockhit", false, true);
	private BooleanValue randomizeJitter = new BooleanValue("Randomize Jitter", false, true);

	@Subscribe
	public void onUpdate(UpdateEvent e)
	{
		if (Mouse.isButtonDown(0) && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            mc.gameSettings.keyBindAttack.setKeyPressed(true);
            return;
        }

        if (mc.currentScreen == null && !mc.thePlayer.isBlocking()) {
            //Mouse.poll();

            if (Mouse.isButtonDown(0) && Math.random() * 50 <= minCPS.get() + (MathUtil.RANDOM.nextDouble() * (maxCPS.get() - minCPS.get()))) {
                sendClick(0, true);
                sendClick(0, false);
            }
        }
	}
	
	private void sendClick(final int button, final boolean state) 
	{
        final Minecraft mc = Minecraft.getMinecraft();
        final int keyBind = button == 0 ? mc.gameSettings.keyBindAttack.getKeyCode() : mc.gameSettings.keyBindUseItem.getKeyCode();

        KeyBinding.setKeyBindState(button == 0 ? mc.gameSettings.keyBindAttack.getKeyCode() : mc.gameSettings.keyBindUseItem.getKeyCode(), state);

        if (state) {
            KeyBinding.onTick(keyBind);
        }
    }
}