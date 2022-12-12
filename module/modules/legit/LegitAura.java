package cat.module.modules.legit;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.eventbus.Subscribe;

import cat.BlueZenith;
import cat.events.EventType;
import cat.events.impl.AttackEvent;
import cat.events.impl.PacketEvent;
import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.IntegerValue;
import cat.module.value.types.ListValue;
import cat.module.value.types.ModeValue;
import cat.util.ClientUtils;
import cat.util.EntityManager;
import cat.util.MillisTimer;
import cat.util.PacketUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class LegitAura extends Module
{
	public LegitAura() { super("LegitAura", "", ModuleCategory.LEGIT, 0, ""); }

	private final IntegerValue range = new IntegerValue("Range", 4, 1, 6, 1, true, null);
	private final IntegerValue minCPS = new IntegerValue("Min CPS", 8, 1, 20, 1, true, null);
	private final IntegerValue maxCPS = new IntegerValue("Max CPS", 12, 1, 20, 1, true, null);
	private final IntegerValue minSwitchDelay = new IntegerValue("Switch Delay", 100, 0, 500, 50, true, null); 

	private final BooleanValue silent = new BooleanValue("Silent", true, true, null);
	private final BooleanValue raytrace = new BooleanValue("Raytrace", true, true, null);
	private final BooleanValue autoBlock = new BooleanValue("AutoBlock", true, true, null);
	private final BooleanValue autoWeapon = new BooleanValue("AutoWeapon", true, true, null);
	private final BooleanValue rotations = new BooleanValue("Rotations", true, true, null);

	private final ListValue entityTypes = new ListValue("Entity types", true, "Players", "Mobs", "Animals", "Villagers");
	
	private final ModeValue mode = new ModeValue("Mode", "Single", true, null, "Single", "Multi");
	private final ModeValue priority = new ModeValue("Priority", "Distance", true, null, "Distance", "Health", "Armor");
	private final ModeValue autoBlockMode = new ModeValue("AutoBlock", "C0A", true, null, "C0A", "Hypixel");

	private EntityLivingBase target;
	private int delay;
	private final MillisTimer attackTimer = new MillisTimer();
    private final MillisTimer switchTimer = new MillisTimer();
	private boolean blocking;
	private boolean blockStatus;

	@Subscribe
	public void onUpdate(UpdatePlayerEvent e)
	{
		List<EntityLivingBase> list = mc.theWorld.loadedEntityList.parallelStream().filter(ent -> ent instanceof EntityLivingBase
		&& EntityManager.isTarget(ent)
		&& mc.thePlayer.getDistanceSqToEntity(ent) <= range.get() * range.get())
		.map(j -> (EntityLivingBase) j)
		.sorted((ent1, ent2) -> {
			switch (priority.get())
			{
				case "Distance":
					return Double.compare(mc.thePlayer.getDistanceSqToEntity(ent2), mc.thePlayer.getDistanceSqToEntity(ent2));

				case "Health":
					return Double.compare(ent2.getHealth(), ent1.getHealth());

				default:
					return Float.compare(ent1.getHealth(), ent2.getHealth());
			}
		}).collect(Collectors.toList());

		if (list.isEmpty())
		{
			target = null;
			return;
		}

		switch (mode.get())
		{
			case "Single":
				target = list.get(0);
				break;

			case "Multi":
				if (!isSex(target) || switchTimer.hasTimeReached(minSwitchDelay.get())) {
                    setTargetToNext(list);
                    switchTimer.reset();
                }
		}

		if (e.post() || !isValid(target)) return;

		attack(target, e);
	}

	private void setTargetToNext(List<EntityLivingBase> f) 
	{ 
        int g = f.indexOf(target) + 1;
        if (g >= f.size()) {
            target = f.get(0);
        } else target = f.get(g);
    }

	private long funnyVariable = 0;

	private void attack(EntityLivingBase target, UpdatePlayerEvent e)
	{
		if (rotations.get()) 
		{
			setRotations(target, e);
        }

		if (attackTimer.hasTimeReached(funnyVariable))
		{
			AttackEvent event = new AttackEvent(target, EventType.PRE);
			BlueZenith.eventManager.call(event);
			//PacketUtil.send(new C0APacketAnimation());
			PacketUtil.send(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
			AttackEvent event2 = new AttackEvent(target, EventType.POST);
			BlueZenith.eventManager.call(event2);
			funnyVariable = ClientUtils.getRandomLong(minCPS.get(), maxCPS.get());
			attackTimer.reset();
		}
		block();
	}

	private void setRotations(EntityLivingBase target, UpdatePlayerEvent e)
	{
		AxisAlignedBB bb = target.getEntityBoundingBox();

		double x = target.posX - mc.thePlayer.posX;
		double y = target.posY + target.getEyeHeight() - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
		double z = target.posZ - mc.thePlayer.posZ;

		double dist = MathHelper.sqrt_double(x * x + z * z);
		float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(y, dist) * 180.0D / Math.PI);

		if (silent.get())
		{
			e.yaw = yaw;
			e.pitch = pitch;
		}
		else
		{
			mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
		}
	}

	private void block()
	{
		//if (autoBlock.get() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)
		if (autoBlock.get())
		{
			switch (autoBlockMode.get())
			{
				case "C0A":
					PacketUtil.send(new C0APacketAnimation());
					break;

				case "Hypixel":
					mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
					break;
			}
			blocking = true;
		}
	}

	@Subscribe
    public void onPacket(PacketEvent e) 
	{
		if (e.packet instanceof C09PacketHeldItemChange && blockStatus) {
            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            blockStatus = false;
        }
	}

//	@Subscribe
//	public void onPacket1(PacketEvent e)
//	{
//		if (e.packet instanceof S08PacketPlayerPosLook)
//		{
//			S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.packet;
//			if (packet.getFlags().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT))
//			{
//				packet.setYaw(mc.thePlayer.rotationYaw);
//			}
//			if (packet.getFlags().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT))
//			{
//				packet.setPitch(mc.thePlayer.rotationPitch);
//			}
//		}
//	}

	private boolean isValid(EntityLivingBase ent)
	{
        return target != null && EntityManager.isTarget(ent) && mc.theWorld.loadedEntityList.contains(ent);
    }

	private boolean isSex(EntityLivingBase target) {
        return target != null && (target.getHealth() > 0  && !target.isDead || EntityManager.Targets.DEAD.on) && mc.thePlayer.getDistanceToEntity(target) <= range.get();
    }

}