package cat.module.modules.movement;

import java.util.Random;

import com.google.common.eventbus.Subscribe;

import cat.events.impl.PreMotionEvent;
import cat.events.impl.UpdateEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.modules.combat.Aura;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.FloatValue;
import cat.module.value.types.ModeValue;
import cat.util.MovementUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoubleStoneSlab;
import net.minecraft.block.BlockDoubleStoneSlabNew;
import net.minecraft.block.BlockDoubleWoodSlab;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class Speed extends Module
{
	private ModeValue modes = new ModeValue("Bypass", "Watchdog", true, null, "Watchdog", "WatchdogNew");
	
	private BooleanValue watchdogBoost = new BooleanValue("Boost", false, true);
	
	private FloatValue speedTimerBoost = new FloatValue("Timer Boost", 1.3f, 1.0f, 3.0f, 0.01f, true, null);
	
	private float speed;
	private int stage;
	
	public Speed() {
		super("Speed", "", ModuleCategory.MOVEMENT, 0, "");
	}

	@Subscribe
	public void onUpdate(UpdateEvent e)
	{
		switch (modes.get())
		{
		case "Watchdog":
			if (mc.thePlayer.onGround && MovementUtil.isMoving())
			{
				mc.thePlayer.jump();
				if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null)
				{
					MovementUtil.setSpeed(MovementUtil.getSpeed() + 0.105D);
				}
				else
				{
					MovementUtil.setSpeed(MovementUtil.getSpeed() + 0.06D);
				}
				
				mc.timer.timerSpeed = 1.0f;
				mc.thePlayer.jumpMovementFactor = 0.04f;
			}
			break;
		case "WatchdogNew":
			if (mc.thePlayer.onGround)
			{
				if (MovementUtil.isMoving())
				{
					mc.thePlayer.jump();
					stage = 0;
					speed = 1.10f;
				}
			}
			else
			{
				speed -= 0.004;
				MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * speed);
			}
		}
	}
		
	Block getBlockUnderPlayer(float offsetY) 
	{
		return getBlockUnderPlayer((EntityPlayer)mc.thePlayer, offsetY);
	}
	  
	Block getBlockUnderPlayer(EntityPlayer player, float offsetY) 
	{
		return mc.theWorld.getBlockState(new BlockPos(player.posX, player.posY - offsetY, player.posZ)).getBlock();
	}
	
}








