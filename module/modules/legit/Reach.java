package cat.module.modules.legit;

import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.FloatValue;
import cat.module.value.types.IntegerValue;

public class Reach extends Module
{
	public Reach() { super("Reach", "", ModuleCategory.LEGIT, 0, ""); }

	public static FloatValue reach = new FloatValue("Reach", 3f, 3f, 6f, .1f, true, null);
}