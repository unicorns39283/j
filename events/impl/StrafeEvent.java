package cat.events.impl;

import cat.events.MultiTypeEvent;

public class StrafeEvent extends MultiTypeEvent
{
	public float forward, strafe, yaw, friction;
    private boolean cancelled = false;

    public StrafeEvent(float forward, float strafe, float yaw, float friction) {
        this.forward = forward;
        this.strafe = strafe;
        this.yaw = yaw;
        this.friction = friction;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}