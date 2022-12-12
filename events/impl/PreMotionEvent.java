package cat.events.impl;

import cat.events.EventType;
import cat.events.MultiTypeEvent;
import cat.util.RotationUtil;
import net.minecraft.client.Minecraft;

public class PreMotionEvent extends MultiTypeEvent
{
	public static PreMotionEvent instance;
	public EventType type;
	private static float yaw, pitch;
	
	public PreMotionEvent(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
    }

	public static float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        Minecraft.getMinecraft().thePlayer.renderYawOffset = RotationUtil.calculateCorrectYawOffset((float)yaw);
        Minecraft.getMinecraft().thePlayer.rotationYawHead = yaw;
    }
    
    public static float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
    	this.pitch = pitch;
        Minecraft.getMinecraft().thePlayer.prevRotationPitchHead = pitch;
        Minecraft.getMinecraft().thePlayer.rotationPitchHead = pitch;
    }

	public static PreMotionEvent getInstance() {
		return instance;
	}
    
}