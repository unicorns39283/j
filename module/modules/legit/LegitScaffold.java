package cat.module.modules.legit;

import com.google.common.eventbus.Subscribe;

import cat.events.impl.UpdateEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.FloatValue;
import cat.module.value.types.ModeValue;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;

public class LegitScaffold extends Module
{
	private boolean waitingForAim;
    private final float[] normalPos = {78f, -315, -225, -135, -45, 0, 45, 135, 225, 315};
    
	private final FloatValue range = new FloatValue("Range", 3f, 1f, 6f, 1f, true, null);
	private final BooleanValue pitchCheck = new BooleanValue("Pitch Check", false, true);
	private final ModeValue mode = new ModeValue("Mode", "Rotations", true, null, "Rotations", "Vape");

	public LegitScaffold() {
		super("LegitScaffold", "", ModuleCategory.LEGIT, 0, "");
	}
	@Subscribe
	public void onUpdate(UpdateEvent e)
	{
		switch (mode.get()) 
		{
		case "Rotations":
			float fuckedYaw = mc.thePlayer.rotationYaw;
	        float fuckedPitch = mc.thePlayer.rotationPitch;

	        float yaw = fuckedYaw - ((int)fuckedYaw/360) * 360;
	        float pitch = fuckedPitch - ((int)fuckedPitch/360) * 360;

			float range = this.range.get();
			
			if (normalPos[0] >= (pitch - range) && normalPos[0] <= (pitch + range))
			{
				for (int k = 1; k < normalPos.length; k++)
				{
					if (normalPos[k] >= (yaw - range) && normalPos[k] <= (yaw + range))
					{
						aimAt(normalPos[0], normalPos[k], fuckedYaw, fuckedPitch);
						shiftAtEdge();
						this.waitingForAim = false;
						return;
					}
				}
			}
		case "Vape":
			shiftAtEdge();
		}	
	}

	public void shiftAtEdge()
	{
		if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock() instanceof BlockAir && mc.thePlayer.onGround)
		{
			mc.gameSettings.keyBindSneak.pressed = true;
		}
		else
		{
			mc.gameSettings.keyBindSneak.pressed = false;
		}
	}
	
	public void aimAt(float pitch, float yaw, float fuckedYaw, float fuckedPitch)
	{
		mc.thePlayer.rotationPitch = pitch + ((int)fuckedPitch/360) * 360;
		mc.thePlayer.rotationYaw = yaw;
	}
}