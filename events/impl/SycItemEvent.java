package cat.events.impl;

import cat.events.Event;

public class SycItemEvent extends Event
{
	public int slot;
	
	public SycItemEvent(int slot) {
		this.slot = slot;
	}
}
