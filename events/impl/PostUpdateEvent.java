package cat.events.impl;

import cat.events.Event;

public class PostUpdateEvent extends Event {
	private float yaw;
	  
	public static float pitch;
  
  public static boolean rotatingPitch;
  
  public PostUpdateEvent(float yaw, float pitch) {
    this.yaw = yaw;
    PostUpdateEvent.pitch = pitch;
  }
  
  public float getYaw() {
    return this.yaw;
  }
  
  public void setYaw(float yaw) {
    this.yaw = yaw;
  }
  
  public float getPitch() {
    return pitch;
  }
  
  public void setPitch(float pitch) {
    PostUpdateEvent.pitch = pitch;
    rotatingPitch = true;
  }
}
