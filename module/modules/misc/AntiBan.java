package cat.module.modules.misc;

import cat.events.impl.PacketEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.ui.notifications.NotificationManager;
import cat.ui.notifications.NotificationType;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

public class AntiBan extends Module {
    private final BooleanValue confirm = new BooleanValue("Cancel C0F", false, true, null);
    private final BooleanValue keepalive = new BooleanValue("Cancel C00", false, true, null);

    public AntiBan() {
        super("AntiBan", "", ModuleCategory.MISC, "antiban");
    }

    @Subscribe
    public void onPacket(PacketEvent e) {
        if (confirm.get()) {
            if (e.packet instanceof C0FPacketConfirmTransaction) {
                e.cancel();
            }
        }
        if (keepalive.get()) {
            if (e.packet instanceof C00PacketKeepAlive) {
                e.cancel();
            }
        }
    }
    public void onEnable() {
        NotificationManager.publish("Do not use AntiBan on Hypixel!", NotificationType.WARNING, 3000);
    }
}


