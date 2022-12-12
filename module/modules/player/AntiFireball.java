package cat.module.modules.player;

import com.google.common.eventbus.Subscribe;

import cat.events.impl.UpdateEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.ModeValue;
import cat.util.PacketUtil;
import cat.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;

public class AntiFireball extends Module
{
	public AntiFireball() { super("AntiFireball", "", ModuleCategory.PLAYER, 0, "afb"); }

	private ModeValue modes = new ModeValue("Swing", "Packet", true, null, "Packet", "Normal", "None");
	private BooleanValue rotations = new BooleanValue("Rotations", false, true);
	
	@Subscribe
	public void onUpdate(UpdateEvent e)
	{
		for (Entity entity : mc.theWorld.loadedEntityList)
		{
			if (rotations.get()) 
			{
				RotationUtil.getRotations((EntityLivingBase)entity);
			}
			
			PacketUtil.send(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
			
			switch (modes.get())
			{
			case "Normal":
				mc.thePlayer.swingItem();
				break;
			case "Packet":
				PacketUtil.send(new C0APacketAnimation());
				break;
			}
		}
	}
	
}