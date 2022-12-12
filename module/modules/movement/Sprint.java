package cat.module.modules.movement;

import cat.events.impl.UpdateEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.util.MillisTimer;
import cat.util.MovementUtil;
import com.google.common.eventbus.Subscribe;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", "", ModuleCategory.MOVEMENT, "Sprint");
    }

    @Subscribe
    public void onUpdate(UpdateEvent e) {
//        if (MovementUtil.areMovementKeysPressed()) {
//            if (!mc.thePlayer.isCollidedHorizontally) {
//                if (!mc.thePlayer.isUsingItem()) {
//                    if (!mc.thePlayer.isSneaking()) {
//                        mc.thePlayer.setSprinting(true);
//                    }
//                }
//            }
//        }
    	if (mc.gameSettings.keyBindForward.pressed)
    		mc.thePlayer.setSprinting(true);
    }
}





