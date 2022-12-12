package cat.util;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;

public class MovementUtil extends MinecraftInstance {
    public static float currentSpeed() {
        return (float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }
    public static boolean areMovementKeysPressed(){
        return mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F;
    }
    public static void setSpeed(float f){
        if(mc.thePlayer == null || !(mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F)){
            return;
        }
        float rotationYaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward < 0.0F)
            rotationYaw += 180.0F;
        float forward = 1.0F;
        if (mc.thePlayer.moveForward < 0.0F) {
            forward = -0.5F;
        } else if (mc.thePlayer.moveForward > 0.0F) {
            forward = 0.5F;
        }
        if (mc.thePlayer.moveStrafing > 0.0F)
            rotationYaw -= 90.0F * forward;
        if (mc.thePlayer.moveStrafing < 0.0F)
            rotationYaw += 90.0F * forward;

        float yaw = (float) Math.toRadians(rotationYaw);
        mc.thePlayer.motionX = -Math.sin(yaw) * f;
        mc.thePlayer.motionZ = Math.cos(yaw) * f;
    }

    public static double getNormalSpeed() {
        double speed = 0.2875D;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            speed *= 1.0D + 0.2D * (double)(mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }

        return speed;
    }
    
    private double getDistance(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static void stopMoving() {
        mc.thePlayer.motionX *= 0D;
        mc.thePlayer.motionZ *= 0D;
    }
	public static boolean isMoving() 
    {
        return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
	}
	
	public static void strafe(double d) 
	{
        if (!isMoving()) return;
        mc.thePlayer.motionX = -Math.sin(getDirection()) * d;
        mc.thePlayer.motionZ = Math.cos(getDirection()) * d;
    }
	
	public static float getSpeed()
	{
		return (float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
	}

	public static void strafe() 
	{
		strafe(getSpeed());
    }
	
    public static float getDirection() {
        float yaw = mc.thePlayer.rotationYaw;
        float forward = mc.thePlayer.moveForward;
        float strafe = mc.thePlayer.moveStrafing;
        yaw += (forward < 0.0F ? 180 : 0);
        if (strafe < 0.0F) {
            yaw += (forward == 0.0F ? 90 : forward < 0.0F ? -45 : 45);
        }
        if (strafe > 0.0F) {
            yaw -= (forward == 0.0F ? 90 : forward < 0.0F ? -45 : 45);
        }
        return (float) Math.toRadians(yaw);
    }
    
    public static double getLowHopMotion(double motion) 
    {
        double base = MathUtil.roundToDecimal(mc.thePlayer.posY - (int) mc.thePlayer.posY, 2);

        if (base == 0.4) {
            return 0.31f;
        } else if (base == 0.71) {
            return 0.05f;
        } else if (base == 0.76) {
            return -0.2f;
        } else if (base == 0.56) {
            return -0.19f;
        } else if (base == 0.42) {
            return -0.12;
        }

        return motion;
    }

    // getBaseMoveSpeed
    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0D + 0.2D * (double)(mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }
    
    public static double getJumpBoostMotion() 
    {
        if (mc.thePlayer.isPotionActive(Potion.jump))
            return (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1;

        return 0;
    }
    
    public static boolean isBlockUnder() 
    {
        for (int offset = 0; offset < mc.thePlayer.posY + mc.thePlayer.getEyeHeight(); offset += 2) {
            AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, boundingBox).isEmpty())
                return true;
        }
        return false;
    }

    public static double getJumpBoostModifier(double baseJumpHeight) {
        return getJumpBoostModifier(baseJumpHeight, true);
    }

    public static double getJumpBoostModifier(double baseJumpHeight, boolean potionJump) {
        if (mc.thePlayer.isPotionActive(Potion.jump) && potionJump) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            baseJumpHeight += ((float) (amplifier + 1) * 0.1f);
        }

        return baseJumpHeight;
    }
    // hypot
    public static double hypot(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }
    
    public static void setSpeed(double speed) 
    {
    	Minecraft mc = Minecraft.getMinecraft();
        double yaw = mc.thePlayer.rotationYaw;
        boolean isMoving = (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F);
        boolean isMovingForward = (mc.thePlayer.moveForward > 0.0F);
        boolean isMovingBackward = (mc.thePlayer.moveForward < 0.0F);
        boolean isMovingRight = (mc.thePlayer.moveStrafing > 0.0F);
        boolean isMovingLeft = (mc.thePlayer.moveStrafing < 0.0F);
        boolean isMovingSideways = (isMovingLeft || isMovingRight);
        boolean isMovingStraight = (isMovingForward || isMovingBackward);
        if (isMoving) 
        {
        	if (isMovingForward && !isMovingSideways) 
        	{
        		yaw += 0.0D;
        	} 
        	else if (isMovingBackward && !isMovingSideways) 
        	{
        		yaw += 180.0D;
        	} else if (isMovingForward && isMovingLeft) 
        	{
        		yaw += 45.0D;
        	} else if (isMovingForward) {
        		yaw -= 45.0D;
        	} else if (!isMovingStraight && isMovingLeft) 
        	{
        		yaw += 90.0D;
        	} else if (!isMovingStraight && isMovingRight) 
        	{
        		yaw -= 90.0D;
        	} else if (isMovingBackward && isMovingLeft) 
        	{
        		yaw += 135.0D;
        	} else if (isMovingBackward) 
        	{
        		yaw -= 135.0D;
        	} 
        	yaw = Math.toRadians(yaw);
        	mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        	mc.thePlayer.motionZ = Math.cos(yaw) * speed;
        }
    }
}
