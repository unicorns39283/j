package cat.module.modules.combat;

import cat.events.impl.AttackEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.util.ClientUtils;
import cat.util.PacketUtil;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Criticals extends Module {

    public Criticals() {
        super("Criticals", "", ModuleCategory.COMBAT);
    }
    
    @Subscribe
    public void onAttack(AttackEvent event) {
        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.05250000001304, mc.thePlayer.posZ, false));
        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.00150000001304, mc.thePlayer.posZ, false));
        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.01400000001304, mc.thePlayer.posZ, false));
        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY = 0.00150000001304, mc.thePlayer.posZ, false));
        ClientUtils.displayChatMessage("crit hit");
    }
}
