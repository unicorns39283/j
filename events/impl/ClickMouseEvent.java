package cat.events.impl;

import cat.events.MultiTypeEvent;
import net.minecraft.util.MovingObjectPosition;

public class ClickMouseEvent extends MultiTypeEvent
{
	private int button;
    private boolean cancelled;
    public MovingObjectPosition objectMouseOver;

    public ClickMouseEvent(int button, MovingObjectPosition mouseOver) {
        this.button = button;
        this.cancelled = false;
        this.objectMouseOver = mouseOver;
    }

    public int getButton() {
        return button;
    }

    public void setButton(int button) {
        this.button = button;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public MovingObjectPosition getObjectMouseOver() {
        return objectMouseOver;
    }

    public void setObjectMouseOver(MovingObjectPosition objectMouseOver) {
        this.objectMouseOver = objectMouseOver;
    }
}