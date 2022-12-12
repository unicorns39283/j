package cat.module.modules.legit;

import com.google.common.eventbus.Subscribe;

import cat.events.impl.UpdateEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.modules.combat.Aura;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.FloatValue;
import cat.module.value.types.IntegerValue;
import cat.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;

public class AimAssist extends Module
{
	public AimAssist() { super("Aim Assist", "", ModuleCategory.LEGIT, ""); }
	
	private final FloatValue strength = new FloatValue("Speed", 5f, 1f, 10f, .1f, true, null);
	private final IntegerValue range = new IntegerValue("Range", 5, 3, 10, 1, true, null);
	
	private final BooleanValue players = new BooleanValue("Players", true, true);
	private final BooleanValue nonplayers = new BooleanValue("Non Players", false, true);
	private final BooleanValue teams = new BooleanValue("Teams", false, true);
	private final BooleanValue invisibles = new BooleanValue("Invisibles", false, true);
	private final BooleanValue dead = new BooleanValue("Dead", false, true);
	private final BooleanValue onlySword = new BooleanValue("Only Sword", false, true);
	private final BooleanValue vertical = new BooleanValue("Vertical", false, true);
	private final BooleanValue clickAim = new BooleanValue("Click Aim", true, true);
	
	public int deltaX, deltaY;
	public EntityLivingBase target;
	
	@Subscribe
	public void onUpdate(UpdateEvent e)
	{
		if(clickAim.get() && !mc.gameSettings.keyBindAttack.isKeyDown()) return;
		if(onlySword.get() && !isHoldingSword()) return;
		target = getClosest(range.get());
		final float s = (float)(10f - strength.get()) + 1f * 10f;

		// if (target == null || onlySword.get() || !mc.thePlayer.canEntityBeSeen(target))
		// {
		// 	deltaX = deltaY = 0;
		// 	return;
		// }
		final float[] rotations = getRotations();
		
		final float targetYaw = (float) (rotations[0] + Math.random());
		final float targetPitch = (float) (rotations[1] + Math.random());
	
		final float ongYaw = (targetYaw - mc.thePlayer.rotationYaw) / Math.max(2, s);
		final float ongPitch = (targetPitch - mc.thePlayer.rotationPitch) / Math.max(2, s);
	
		final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float gcd = f * f * f * 8.0F;
		
		deltaX = Math.round(ongYaw / gcd);

		if (vertical.get()) deltaY = Math.round(ongPitch / gcd); else deltaY = 0;

		 mc.thePlayer.rotationYaw += deltaX * gcd;
		 mc.thePlayer.rotationPitch += deltaY * gcd;
	}

	private boolean isHoldingSword()
	{
		return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
	}
	
	private float[] getRotations() {
        final double var4 = (target.posX - (target.lastTickPosX - target.posX)) + 0.01 - mc.thePlayer.posX;
        final double var6 = (target.posZ - (target.lastTickPosZ - target.posZ)) - mc.thePlayer.posZ;
        final double var8 = (target.posY - (target.lastTickPosY - target.posY)) + 0.4 + target.getEyeHeight() / 1.3 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());

        final double var14 = MathHelper.sqrt_double(var4 * var4 + var6 * var6);

        float yaw = (float) (Math.atan2(var6, var4) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(var8, var14) * 180.0D / Math.PI);

        yaw = mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw);
        pitch = mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch);

        final float[] rotations = new float[]{yaw, pitch};
        final float[] lastRotations = new float[]{Aura.yaw, Aura.pitch};

        final float[] fixedRotations = RotationUtil.getFixedRotation(rotations, lastRotations);

        yaw = fixedRotations[0];
        pitch = fixedRotations[1];

        pitch = MathHelper.clamp_float(pitch, -90.0F, 90.0F);

		rotations[0] = yaw;
		rotations[1] = pitch;

		return rotations;
    }

	@Override
	public void onDisable() 
	{
        deltaX = 0;
        deltaY = 0;
    }
	
	private EntityLivingBase getClosest(final double range)
	{
		if (mc.theWorld == null) return null;
		double dist = range;
		EntityLivingBase target = null;
		
		for (final Entity entity : mc.theWorld.loadedEntityList)
		{
			if (entity instanceof EntityLivingBase)
			{
				final EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
				
				if (canAttack(entityLivingBase))
				{
					final double currentDist = mc.thePlayer.getDistanceToEntity(entityLivingBase);
					
					if (currentDist <= range)
					{
						dist = currentDist;
						target = entityLivingBase;
					}
				}
			}
		}
		return target;
	}
	
	private boolean canAttack(final EntityLivingBase player)
	{
		if (player instanceof EntityPlayer && !players.get())
		{
			return false;
		}
		
		if (player instanceof EntityAnimal || player instanceof EntityMob || player instanceof EntityVillager)
		{
			if (!nonplayers.get()) return false; 
		}
		
		if (player.isInvisible() && !invisibles.get()) return false;
		if (player.isDead && !dead.get()) return false;
		if (player.isOnSameTeam(mc.thePlayer) && !teams.get()) return false;
		if (player.ticksExisted < 2) return false;
		
		return mc.thePlayer != player;
	}	
}