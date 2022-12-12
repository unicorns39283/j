package cat.util;

import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RotationUtil extends MinecraftInstance {
	
	public static float yaw;
	public static float pitch;
	
    public static boolean isFacingPlayer(float yaw, float pitch) {
        for (Object o : mc.theWorld.loadedEntityList) {
            if (o instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) o;
                if (entity != mc.thePlayer) {
                    if (entity.getDistanceToEntity(mc.thePlayer) <= 4) {
                        float[] rotations = getRotations(entity);
                        if (Math.abs(yaw - rotations[0]) < 30 && Math.abs(pitch - rotations[1]) < 30) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // getRotations(EntityLivingBase entity)
    public static float[] getRotations(EntityLivingBase entity) {
        double x = entity.posX - mc.thePlayer.posX;
        double z = entity.posZ - mc.thePlayer.posZ;
        double y;

        if (entity instanceof EntityLivingBase) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;
            y = livingBase.posY + livingBase.getEyeHeight() - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        } else {
            y = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0D - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        }

        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(y, dist) * 180.0D / Math.PI);
        return new float[]{mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw), mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)};
    }
    
    public static float[] getFixedRotation(final float[] rotations, final float[] lastRotations) {
        final Minecraft mc = Minecraft.getMinecraft();

        final float yaw = rotations[0];
        final float pitch = rotations[1];

        final float lastYaw = lastRotations[0];
        final float lastPitch = lastRotations[1];

        final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float gcd = f * f * f * 1.2F;

        final float deltaYaw = yaw - lastYaw;
        final float deltaPitch = pitch - lastPitch;

        final float fixedDeltaYaw = deltaYaw - (deltaYaw % gcd);
        final float fixedDeltaPitch = deltaPitch - (deltaPitch % gcd);

        final float fixedYaw = lastYaw + fixedDeltaYaw;
        final float fixedPitch = lastPitch + fixedDeltaPitch;

        return new float[]{fixedYaw, fixedPitch};
    }
    
    public static Vec3 getHead(AxisAlignedBB bb, double height) {
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * height, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
    }
    
    public static Vec3 getRandomCenter(AxisAlignedBB bb) {
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * ThreadLocalRandom.current().nextDouble(0.2, 0.8),
                bb.minY + (bb.maxY - bb.minY) * ThreadLocalRandom.current().nextDouble(0.2, 0.8),
                bb.minZ + (bb.maxZ - bb.minZ) * ThreadLocalRandom.current().nextDouble(0.2, 0.8));
    }
    
    public static Vec3 getClosestPoint(final Vec3 start, final AxisAlignedBB boundingBox) {
        final double closestX = start.xCoord >= boundingBox.maxX ? boundingBox.maxX : boundingBox.minX,
                closestY = start.yCoord >= boundingBox.maxY ? boundingBox.maxY :
                        start.yCoord <= boundingBox.minY ? boundingBox.minY : boundingBox.minY + (start.yCoord - boundingBox.minY),
                closestZ = start.zCoord >= boundingBox.maxZ ? boundingBox.maxZ : boundingBox.minZ;
        return new Vec3(closestX, closestY, closestZ);
    }
    
    public static float[] getNeededRotations(Vec3 vec)
    {
    	Vec3 playerVector = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    	double y = vec.yCoord - playerVector.yCoord;
        double x = vec.xCoord - playerVector.xCoord;
        double z = vec.zCoord - playerVector.zCoord;
        double dff = Math.sqrt(x * x + z * z);
        float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90;
        float pitch = (float) -Math.toDegrees(Math.atan2(y, dff));
        return new float[]{updateRotation(yaw, yaw, 180), updateRotation(pitch, pitch, 90)};
    }
    
    public static Vec3 getCenter(AxisAlignedBB bb) 
    {
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
    }
    
    public static float updateRotation(float p_75652_1_, float p_75652_2_, float p_75652_3_) {
        float f = MathHelper.wrapAngleTo180_float(p_75652_2_ - p_75652_1_);

        if (f > p_75652_3_)
        {
            f = p_75652_3_;
        }

        if (f < -p_75652_3_)
        {
            f = -p_75652_3_;
        }

        return p_75652_1_ + f;
    }
    
    public static float calculateCorrectYawOffset(float yaw) {
        float offsetDiff;
        float yawOffsetDiff;
        float renderYawOffset;
        double xDiff = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDiff = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        float dist = (float)(xDiff * xDiff + zDiff * zDiff);
        float offset = renderYawOffset = mc.thePlayer.renderYawOffset;
        if (dist > 0.0025000002f) {
            offset = (float)MathHelper.func_181159_b((double)zDiff, (double)xDiff) * 180.0f / (float)Math.PI - 90.0f;
        }
        if (mc.thePlayer != null && mc.thePlayer.swingProgress > 0.0f) {
            offset = yaw;
        }
        if ((yawOffsetDiff = MathHelper.wrapAngleTo180_float((float)(yaw - (renderYawOffset += (offsetDiff = MathHelper.wrapAngleTo180_float((float)(offset - renderYawOffset))) * 0.3f)))) < -75.0f) {
            yawOffsetDiff = -75.0f;
        }
        if (yawOffsetDiff >= 75.0f) {
            yawOffsetDiff = 75.0f;
        }
        renderYawOffset = yaw - yawOffsetDiff;
        if (yawOffsetDiff * yawOffsetDiff > 2500.0f) {
            renderYawOffset += yawOffsetDiff * 0.2f;
        }
        return renderYawOffset;
    }
    
    public static float[] mouseFix(float lastYaw, float lastPitch, float yaw, float pitch) {
        float f = 0.5f * 0.6F + 0.2F;
        float f1 = f * f * f * 8.0F;
        float f2 = MathHelper.wrapAngleTo180_float(yaw - lastYaw % 360f);
        float f3 = MathHelper.wrapAngleTo180_float(pitch - lastPitch % 360f);
        float f4 = (int) f2 * 8 * f1;
        float f5 = (int) f3 * 8 * f1;
        yaw = (float) ((double) lastYaw + (double) f4 * 0.15D);
        pitch = (float) ((double) lastPitch + (double) f5 * 0.15D);
        return new float[]{yaw, pitch};
    }
    
    public static float getYawChange(float yaw, double posX, double posZ) {
        Minecraft.getMinecraft();
        double deltaX = posX - mc.thePlayer.posX;
        Minecraft.getMinecraft();
        double deltaZ = posZ - mc.thePlayer.posZ;
        double yawToEntity = 0.0D;
        if (deltaZ < 0.0D && deltaX < 0.0D) {
          if (deltaX != 0.0D)
            yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX)); 
        } else if (deltaZ < 0.0D && deltaX > 0.0D) {
          if (deltaX != 0.0D)
            yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX)); 
        } else if (deltaZ != 0.0D) {
          yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        } 
        return MathHelper.wrapAngleTo180_float(-(yaw - (float)yawToEntity));
      }
      
      public static float getPitchChange(float pitch, Entity entity, double posY) {
        Minecraft.getMinecraft();
        double deltaX = entity.posX - mc.thePlayer.posX;
        Minecraft.getMinecraft();
        double deltaZ = entity.posZ - mc.thePlayer.posZ;
        Minecraft.getMinecraft();
        double deltaY = posY - 2.2D + entity.getEyeHeight() - mc.thePlayer.posY;
        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapAngleTo180_float(pitch - (float)pitchToEntity) - 2.5F;
      }
    
}
