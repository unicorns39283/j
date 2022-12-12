package cat.module.modules.combat;

import cat.events.impl.PacketEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.FloatValue;
import cat.module.value.types.ModeValue;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends Module {
    public Velocity() {
        super("Velocity", "", ModuleCategory.COMBAT);
    }
    private final FloatValue horizontal = new FloatValue("Horizontal", 100F, 0F, 100F, 1F, true, null);
    private final FloatValue vertical = new FloatValue("Vertical", 100F, 0F, 100F, 1F, true, null);
    private final BooleanValue explosions = new BooleanValue("Explosions", true, true, null);
    @Subscribe
    public void onPacket(PacketEvent e){
        Packet<?> packet = e.packet;
        if(packet instanceof S12PacketEntityVelocity){
            S12PacketEntityVelocity s = (S12PacketEntityVelocity) packet;
            if(s.getEntityID() == mc.thePlayer.getEntityId()){
                if(horizontal.get() == 0F && vertical.get() == 0F) {
                    e.cancel();
                }
                s.motionX *= (horizontal.get() / 100);
                s.motionZ *= (horizontal.get() / 100);
                s.motionY *= (vertical.get() / 100);
            }
        }
        if(packet instanceof S27PacketExplosion && explosions.get()) {
                e.cancel();
        }
    }

    @Override
    public String getTag() {
        return (horizontal.get() == 0 && vertical.get() == 0) ? "Cancel" : horizontal.get() + "% " + vertical.get() + "%";
    }
}
