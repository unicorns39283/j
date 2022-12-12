package cat.events.impl;

import cat.events.MultiTypeEvent;

public class EventGlowESP extends MultiTypeEvent {
	  private final Runnable runnable;
	  
	  public boolean cancelled;
	  int result;
	  
	  public void setCancelled(boolean cancelled) {
	    this.cancelled = cancelled;
	  }
	  
	  public boolean equals(Object o) {
	    if (o == this)
	      return true; 
	    if (!(o instanceof EventGlowESP))
	      return false; 
	    EventGlowESP other = (EventGlowESP)o;
	    if (!other.canEqual(this))
	      return false; 
	    if (isCancelled() != other.isCancelled())
	      return false; 
	    Object this$runnable = getRunnable(), other$runnable = other.getRunnable();
	    return !((this$runnable == null) ? (other$runnable != null) : !this$runnable.equals(other$runnable));
	  }
	  
	  protected boolean canEqual(Object other) {
	    return other instanceof EventGlowESP;
	  }
	  
	  public int hashCode() {
	    int PRIME = 59;
	    result = 1;
	    result = result * 59 + (isCancelled() ? 79 : 97);
	    Object $runnable = getRunnable();
	    return result * 59 + (($runnable == null) ? 43 : $runnable.hashCode());
	  }
	  
	  public String toString() {
	    return "EventGlowESP(runnable=" + getRunnable() + ", cancelled=" + isCancelled() + ")";
	  }
	  
	  public EventGlowESP(Runnable runnable) {
	    this.runnable = runnable;
	  }
	  
	  public Runnable getRunnable() {
	    return this.runnable;
	  }
	  
	  public boolean isCancelled() {
	    return this.cancelled;
	  }
	}
