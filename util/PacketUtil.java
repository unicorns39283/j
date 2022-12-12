package cat.util;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public final class PacketUtil extends MinecraftInstance {

    public static void send(Packet packet) {
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }
    public static void sendSilent(Packet packet) {
        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
    }
    
    public static void sendBlocking(boolean callEvent, boolean placement) {
		if(mc.thePlayer == null)
			return;
		
		if(placement) {
			C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0);
			if(callEvent) {
				send(packet);
			} else {
				sendSilent(packet);
			}
		} else {
			C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem());
			if(callEvent) {
				send(packet);
			} else {
				sendSilent(packet);
			}
		}
	}
    
    public static void releaseUseItem(boolean callEvent) 
    {
		if(mc.thePlayer == null)
			return;
		
		C07PacketPlayerDigging packet = new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN);
		if(callEvent) {
			send(packet);
		} else {
			sendSilent(packet);
		}
	}
}
