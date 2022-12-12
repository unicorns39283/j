package cat.events.impl;

import cat.events.Event;
import net.minecraft.client.Minecraft;

public class PreUpdateEvent extends Event
{
	private float yaw;
	  
  public static float pitch;
  
  public double y;
  
  private boolean ground;
  
  public PreUpdateEvent(float yaw, float pitch, double y, boolean ground) {
    this.yaw = yaw;
    PreUpdateEvent.pitch = pitch;
    this.y = y;
    this.ground = ground;
  }
  
  public float getYaw() {
    return this.yaw;
  }
  
  public void setYaw(float yaw) {
    this.yaw = yaw;
    Minecraft.getMinecraft();
    Minecraft.getMinecraft().thePlayer.rotationYawHead = yaw;
    Minecraft.getMinecraft();
    Minecraft.getMinecraft().thePlayer.renderYawOffset = yaw;
  }
  
  public float getPitch() {
    return pitch;
  }
  
  public void setPitch(float pitch) {
	PreUpdateEvent.pitch = pitch;
    Minecraft.getMinecraft();
    Minecraft.getMinecraft().thePlayer.rotationPitchHead = pitch;
  }
  
  public double getY() {
    return this.y;
  }
  
  public void setY(double y) {
    this.y = y;
  }
  
  public boolean isOnground() {
    return this.ground;
  }
  
  public void setOnground(boolean ground) {
    this.ground = ground;
  }
}
