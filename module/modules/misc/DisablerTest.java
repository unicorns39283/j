package cat.module.modules.misc;

import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.eventbus.Subscribe;

import cat.events.impl.PacketEvent;
import cat.events.impl.PreMotionEvent;
import cat.events.impl.UpdateEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.IntegerValue;
import cat.ui.notifications.NotificationManager;
import cat.ui.notifications.NotificationType;
import cat.util.ClientUtils;
import cat.util.MillisTimer;
import cat.util.PacketUtil;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S02PacketChat;

public class DisablerTest extends Module
{
	public DisablerTest() { super("DisablerTest", "", ModuleCategory.MISC, "", ""); }
	
	private final BooleanValue banWarn = new BooleanValue("Ban Warning", true, true);
	private final BooleanValue timerA = new BooleanValue("Timer A", true, true);
	private final BooleanValue timerB = new BooleanValue("Timer B", true, true);
	private final BooleanValue C03 = new BooleanValue("C03 Meme", true, true);
	private final BooleanValue strafe = new BooleanValue("Strafe", true, true);

	private final IntegerValue strafePackets = new IntegerValue("Strafe Packets", 70, 60, 120, 1, true, null);

	private boolean inCage;
	private boolean timerShouldCancel = true;
	private boolean canBlink;
	private MillisTimer timerCancelTimer = new MillisTimer();
	private MillisTimer timerCancelDelay = new MillisTimer();
	private LinkedBlockingQueue<Packet<INetHandlerPlayServer>> packets = new LinkedBlockingQueue<Packet<INetHandlerPlayServer>>();
	private boolean cancel;
	
	@Override
	public void onEnable()
	{
		inCage = true;
	}
	
	@Subscribe
	public void onPacket(PacketEvent e)
	{
		if (banWarn.get() && e.packet instanceof S02PacketChat && ((S02PacketChat)e.packet).getChatComponent().getUnformattedText().contains("Cages opened")) 
		{
			NotificationManager.publish("Disabler: DO NOT USE SPEED UNTIL THIS DISAPPEARS", NotificationType.WARNING, 20000);
			inCage = false;
		}

		if (mc.thePlayer.ticksExisted > 200f) inCage = false;

		if (timerA.get() && inCage == false)
		{
			if (e.packet instanceof C02PacketUseEntity || e.packet instanceof C03PacketPlayer || e.packet instanceof C07PacketPlayerDigging || e.packet instanceof C08PacketPlayerBlockPlacement || e.packet instanceof C0APacketAnimation || e.packet instanceof C0BPacketEntityAction)
			{
				if (timerShouldCancel)
				{
					if (!timerCancelTimer.hasTicksPassed(350))
					{
						packets.add((Packet<INetHandlerPlayServer>) e.packet);
						e.cancel();
						canBlink = false;
					}
					else
					{
						ClientUtils.displayChatMessage("disabler packets releasing");
						ClientUtils.displayChatMessage("size: " + packets.size());
						timerShouldCancel = false;
						
						while (!packets.isEmpty())
						{
							PacketUtil.sendSilent(packets.poll());
						}
					}
					
					if ((mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword))
					{
						ClientUtils.displayChatMessage("disabler packets releasing");
						ClientUtils.displayChatMessage("size: " + packets.size());
						timerShouldCancel = false;
						
						while (!packets.isEmpty())
						{
							PacketUtil.sendSilent(packets.poll());
						}
					}
				}
			}
		}

		if (timerB.get() && inCage == false)
		{
			if (e.packet instanceof C02PacketUseEntity || e.packet instanceof C03PacketPlayer || e.packet instanceof C07PacketPlayerDigging || e.packet instanceof C08PacketPlayerBlockPlacement || e.packet instanceof C0APacketAnimation || e.packet instanceof C0BPacketEntityAction)
			{
				if (timerShouldCancel)
				{
					if (timerCancelTimer.hasTicksPassed(250))
					{
						packets.add((Packet<INetHandlerPlayServer>) e.packet);
						e.cancel();
						canBlink = false;
					}
					else
					{
						ClientUtils.displayChatMessage("disabler packets releasing");
						ClientUtils.displayChatMessage("size: " + packets.size());
						timerShouldCancel = false;
						
						while (!packets.isEmpty())
						{
							PacketUtil.sendSilent(packets.poll());
						}
					}

					if ((mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword))
					{
						ClientUtils.displayChatMessage("disabler packets releasing");
						ClientUtils.displayChatMessage("size: " + packets.size());
						timerShouldCancel = false;
						
						while (!packets.isEmpty())
						{
							PacketUtil.sendSilent(packets.poll());
						}
					}
				}
			}
		}

		if (e.packet instanceof C03PacketPlayer && !(e.packet instanceof C03PacketPlayer.C05PacketPlayerLook || e.packet instanceof C03PacketPlayer.C06PacketPlayerPosLook || e.packet instanceof C03PacketPlayer.C04PacketPlayerPosition) && C03.get())
		{
			ClientUtils.displayChatMessage("meme packets releasing");
			ClientUtils.displayChatMessage("size: " + packets.size());
			e.cancel();
			canBlink = false;
		}

		if (strafe.get())
		{
			if (e.packet instanceof C03PacketPlayer || e.packet instanceof C03PacketPlayer.C04PacketPlayerPosition || e.packet instanceof C03PacketPlayer.C06PacketPlayerPosLook)
			{
				if (mc.thePlayer.ticksExisted < 50)
				{
					e.cancel();
				}
			}
		}
	}
	
	private void onUpdate(UpdateEvent e)
	{
		if (timerA.get()) 
		{
			if (timerCancelDelay.hasTicksPassed(2000))
			{
				timerShouldCancel = true;
				timerCancelTimer.reset();
				timerCancelDelay.reset();
			}
		}
		
		if (timerB.get())
		{
			if (timerCancelDelay.hasTicksPassed(2000))
			{
				timerShouldCancel = true;
				timerCancelTimer.reset();
				timerCancelDelay.reset();
			}
		}
	}
	
}