package cat.module.modules.misc;

import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.FloatValue;

public class Timer extends Module
{
	public Timer() { super("Timer", "", ModuleCategory.MISC, 0, ""); }

	private FloatValue speed = new FloatValue("Timer", 1f, 1f, 2f, .1f, true, null);
	
	@Override
	public void onEnable() {
		mc.timer.timerSpeed = speed.get();
	}

	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1f;
	}
}