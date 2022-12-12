package cat.module.modules.player;

import com.google.common.eventbus.Subscribe;

import cat.events.impl.UpdateEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

public class AutoTool extends Module
{
	public AutoTool() { super("AutoTool", "", ModuleCategory.PLAYER, 0, ""); }

	private int slot;
	
	@Subscribe
	public void onUpdate(UpdateEvent e)
	{
		double lastSpeed = 0.0D;
		this.slot = mc.thePlayer.inventory.currentItem;
		
		if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
		{
			for (int i = 36; i < mc.thePlayer.inventoryContainer.inventorySlots.size(); i++)
			{
				ItemStack itemStack = mc.thePlayer.inventoryContainer.inventorySlots.get(i).getStack();

				if (itemStack != null)
				{
					Item item = itemStack.getItem();

					if (mc.gameSettings.keyBindAttack.isKeyDown() && (item instanceof net.minecraft.item.ItemTool || item instanceof net.minecraft.item.ItemSword))
					{
						double toolSpeed = getToolSpeed(itemStack);
						double currentSpeed = getToolSpeed(mc.thePlayer.getHeldItem());

						if (toolSpeed > 1.0D && toolSpeed > currentSpeed && toolSpeed > lastSpeed) 
						{
							this.slot = i - 36;
							lastSpeed = toolSpeed;
						}
					}
				}
			}
		}
		mc.thePlayer.inventory.currentItem = this.slot;
	}
	
 	private double getToolSpeed(ItemStack itemStack) 
 	{
 		double damage = 0.0D;

 	 	if (itemStack != null && (itemStack
 	 	.getItem() instanceof net.minecraft.item.ItemTool || itemStack.getItem() instanceof net.minecraft.item.ItemSword)) {
 	 	if (itemStack.getItem() instanceof net.minecraft.item.ItemAxe) 
 	 	{
 	 		damage += (itemStack.getItem().getStrVsBlock(itemStack, mc.theWorld
 	 			.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock()) + 
 	 		EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack));
 	 	} 
 	 	else if (itemStack.getItem() instanceof net.minecraft.item.ItemPickaxe) 
 	 	{
 	 		damage += (itemStack.getItem().getStrVsBlock(itemStack, mc.theWorld
 	 			.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock()) + 
 	 		EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack));
 	 	} 
 	 	else if (itemStack.getItem() instanceof net.minecraft.item.ItemSpade) 
 	 	{
 	 		damage += (itemStack.getItem().getStrVsBlock(itemStack, mc.theWorld
 	 			.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock()) + 
 	 		EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack));
 	 	} 
 	 	else if (itemStack.getItem() instanceof net.minecraft.item.ItemSword) 
 	 	{
 	 		damage += itemStack.getItem().getStrVsBlock(itemStack, mc.theWorld
 	 			.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock());
 	 	}
		else if (itemStack.getItem() instanceof ItemShears && mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() == Blocks.wool)
		{
			damage += 15.0D;
		}
 	 	damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0D;
 	 	damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, itemStack) / 11.0D;
 	 	damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) / 33.0D;
 	 	damage -= itemStack.getItemDamage() / 10000.0D;
 	 	return damage;
      } 
     return 0.0D;
   }
}




