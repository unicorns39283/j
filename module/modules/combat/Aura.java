package cat.module.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.eventbus.Subscribe;

import cat.events.impl.PacketEvent;
import cat.events.impl.PreMotionEvent;
import cat.events.impl.Render2DEvent;
import cat.events.impl.Render3DEvent;
import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.FloatValue;
import cat.module.value.types.IntegerValue;
import cat.module.value.types.ModeValue;
import cat.ui.notifications.NotificationManager;
import cat.ui.notifications.NotificationType;
import cat.util.ClientUtils;
import cat.util.ColorUtil;
import cat.util.EntityManager;
import cat.util.MillisTimer;
import cat.util.PacketUtil;
import cat.util.RenderUtil;
import cat.util.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSword;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

public class Aura extends Module {
    public static boolean blockStatus = false;
    
    private final ModeValue customMode1 = new ModeValue("Location", "Test", true, null, "Test2", "Test");
    private final ModeValue mode = new ModeValue("Mode", "Single", true, (__, ___) -> { target = null; return ___; }, null, "Single", "Switch", "Multi");
    private final IntegerValue switchDelay = new IntegerValue("Switch Delay", 500, 50, 2000, 50, true, ___ -> mode.get().equals("Switch"));
    private final ModeValue sortMode = new ModeValue("Sort by", "Health", true, (a1, a2) -> { target = null; return a2; }, null, "Health", "Distance", "HurtTime");
    private final IntegerValue maxCPS = new IntegerValue("MaxCPS", 7, 1, 20, 1, true, (a1, a2) -> { if(a2 < getMinCPS().get()){ return a1; } return a2; }, null);
    private final IntegerValue minCPS = new IntegerValue("MinCPS", 4, 1, 20, 1, true, (a1, a2) -> { if(a2 > maxCPS.get()) { return a1; } return a2; }, null);
    private final FloatValue range = new FloatValue("Range", 3f, 1f, 6f, 1f, true, null);
    private final IntegerValue hurtTime = new IntegerValue("HurtTime", 10, 1, 10, 1, true, __ -> !mode.get().equals("Multi"));
    private final FloatValue aimHeight = new FloatValue("Aim Height", 1.5f, 0f, 1.5f, 0.1f, true, __ -> getRotationsValue());
    private final BooleanValue swing = new BooleanValue("Swing", true, true, null);
    private final BooleanValue blink = new BooleanValue("Blink", true, true, null);
    private final BooleanValue autoBlock = new BooleanValue("AutoBlock", true, true, null);
    private final ModeValue autoBlockMode = new ModeValue("AutoBlock Mode", "Watchdog", true, __ -> autoBlock.get(), "Watchdog", "WatchdogNew");
    private final BooleanValue rotations = new BooleanValue("Rotations", true, true, null);
    private final BooleanValue silent = new BooleanValue("Silent Rotations", true, true, __ -> rotations.get());
    private final BooleanValue esp = new BooleanValue("ESP", true, true);
    private final BooleanValue raytrace = new BooleanValue("Raytrace", false, true);
    private final BooleanValue players = new BooleanValue("Players", true, true);
    private final BooleanValue mobs = new BooleanValue("Mobs", false, true);
    private final BooleanValue invisibles = new BooleanValue("Invisibles", false, true);

    public Aura() {
        super("Aura", "", ModuleCategory.COMBAT, Keyboard.KEY_R, "aura", "ka", "killaura");
    }
    
    private boolean blinking;
    private final List<Packet<?>> toDispatch = new ArrayList<>();
    public static EntityLivingBase target = null;
    public static float yaw, pitch;
    private boolean attackTick = false;
	private float prevYaw, lastYaw, lastPitch;
	private final MillisTimer lossTimer = new MillisTimer();
    private final MillisTimer attackTimer = new MillisTimer();
    private final MillisTimer switchTimer = new MillisTimer();
    private final MillisTimer blockTimer = new MillisTimer();
    private final MillisTimer timer = new MillisTimer();
    List<EntityLivingBase> list;

    @Subscribe
    public void onUpdate(UpdatePlayerEvent e) {
        //filter the list of entities
        list = mc.theWorld.loadedEntityList.parallelStream().filter(ent -> ent instanceof EntityLivingBase
                && EntityManager.isTarget(ent)
                && mc.thePlayer.getDistanceSqToEntity(ent) <= range.get() * range.get())
                .map(j -> (EntityLivingBase) j) //due to the loadedEntityList being a list of Entity by default, you need to cast every entity to EntityLivingBase
                .sorted((ent1, ent2) -> {
                    switch (sortMode.get()) { //this language fucking sucks
                        case "Distance":
                            return Double.compare(mc.thePlayer.getDistanceSqToEntity(ent2), mc.thePlayer.getDistanceSqToEntity(ent2));

                        case "HurtTime":
                            return Integer.compare(ent1.hurtTime, ent2.hurtTime);

                        default:
                            return Float.compare(ent1.getHealth(), ent2.getHealth());
                    }
                }).collect(Collectors.toList());
        if (list.isEmpty())
        {
            if (blockStatus) 
            {
                unblock();
            }
            return;
        }

        switch (mode.get()) 
        {
            case "Single":
                if (!isSex(target)) {
                    target = list.get(0);
                }
                break;

            case "Switch":
                if (!isSex(target) || switchTimer.hasTimeReached(switchDelay.get())) {
                    setTargetToNext(list);
                    switchTimer.reset();
                }
                break;

            case "Multi":
                if (!isSex(target)|| target.hurtTime > 0) {
                    setTargetToNext(list);
                }
                break;
        }
        if (e.post() || !isValid(target))
            return;

        attacknew(target, false, e);
    }
    
    @Subscribe
    public void onRender3D(Render3DEvent e)
    {
    	if (esp.get() && target != null)
    	{
    		drawTargetESP();
    	}
    }
    
    private void setTargetToNext(List<EntityLivingBase> f) 
    {
        int g = f.indexOf(target) + 1;
        if (g >= f.size()) {
            target = f.get(0);
        } else target = f.get(g);
    }
    
    private long funnyVariable = 0;

    @Subscribe
    public void onPreMotion(PreMotionEvent e)
    {
    	attackTick = target != null && timer.hasReached((long) (1000 / maxCPS.get() + Math.random() * 15-7)) && mc.thePlayer.ticksExisted % 5 != 0 && mc.thePlayer.ticksExisted % 17 != 0;
    }
    
//    private void attack(EntityLivingBase target, UpdatePlayerEvent e)
//    {
//    	if (autoBlock.get())
//    	{
//    		block(true, true);
//    	}
//    	
//        if (rotations.get())
//        {
//            setRotation(e);
//        }
//
//        if (attackTimer.hasTimeReached(funnyVariable))
//        {
//            AttackEvent event = new AttackEvent(target, EventType.PRE);
//            BlueZenith.eventManager.call(event);
//
//            if (swing.get())
//            {
//                mc.thePlayer.swingItem();
//            }
//            else
//            {
//                PacketUtil.send(new C0APacketAnimation());
//            }
//
//            PacketUtil.send(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
//            ClientUtils.getRandomLong(minCPS.get(), maxCPS.get());
//            attackTimer.reset();
//        }
//        unblock();
//    }
    
    private void attacknew(Entity ent, boolean crits, UpdatePlayerEvent e)
    {
//    	if (autoBlock.get())
//    	{
//    		block(true, true);
//    	}
    	unblock();
    	
    	if (swing.get())
    		mc.thePlayer.swingItem();
    	else
    		PacketUtil.send(new C0APacketAnimation());
    	
    	if (crits)
    	{
    		mc.thePlayer.jump();
    	}
    	mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(ent, C02PacketUseEntity.Action.ATTACK));
    	float sharpLevel = EnchantmentHelper.getModifierForCreature(mc.thePlayer.inventory.getCurrentItem(), target.getCreatureAttribute());
    	if (sharpLevel > 0.0f)
    	{
    		mc.thePlayer.onEnchantmentCritical(target);
    	}
    	block(true, true);
    }

    private float[] getTestRotations()
    {
        final double targetX = (target.posX - (target.lastTickPosX - target.posX)) + 0.01 - mc.thePlayer.posX;
        final double targetZ = (target.posZ - (target.lastTickPosZ - target.posZ)) - mc.thePlayer.posZ;
        final double targetY = (target.posY - (target.lastTickPosY - target.posY)) + target.getEyeHeight() / 1.1f - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());

        final double dist = MathHelper.sqrt_double(targetX * targetX + targetZ * targetZ);

        float yaw = (float) (Math.atan2(targetZ, targetX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(targetY, dist) * 180.0D / Math.PI);

        final float[] rotations = new float[]{yaw, pitch};
        final float[] lastRotations = new float[]{Aura.yaw, Aura.pitch};

        final float[] fixedRotations = RotationUtil.getFixedRotation(rotations, lastRotations);

        yaw = fixedRotations[0];
        pitch = fixedRotations[1];

        pitch = MathHelper.clamp_float(pitch, -90.0F, 90.0F);

        rotations[0] = yaw;
		rotations[1] = pitch;

        return new float[] { yaw, pitch };
    }
    
    public float[] getRotations() 
    {
    	lastYaw = yaw;
        lastPitch = pitch;
        EntityLivingBase target = this.target;
        float[] rotations = new float[2];
        float[] nextRotations = new float[]{};
        float[] centerRotations = RotationUtil.getNeededRotations(RotationUtil.getCenter(list.get(0).getEntityBoundingBox()));
        switch (customMode1.get())
        {
        case "Test":
            nextRotations = RotationUtil.getRotations(target);
            break;
        case "Test2":
        	nextRotations = getTestRotations();
            break;
        }
        rotations = nextRotations;
        rotations = RotationUtil.mouseFix(yaw, pitch, rotations[0], rotations[1]);
        rotations[1] = MathHelper.clamp_float(rotations[1], -90, 90);
        return rotations;
    }

    private void setRotation(UpdatePlayerEvent e) {
        float[] rotations = getTestRotations();
//    	float[] rotations = getRotations();
    	
        // silent rotations
        if (silent.get()) {
            e.yaw = rotations[0];
            e.pitch = rotations[1];
        } else {
            mc.thePlayer.rotationYaw = rotations[0];
            mc.thePlayer.rotationPitch = rotations[1];
        }
    }
    
    @Subscribe
    public void onPacket(PacketEvent e)
    {
    	if (e.direction == EnumPacketDirection.SERVERBOUND)
    	{
    		if (blinking)
    		{
    			if (e.packet instanceof C03PacketPlayer)
    			{
    				C03PacketPlayer packet = (C03PacketPlayer) e.packet;

                    if (packet.isMoving()) 
                    {
                        toDispatch.add(e.packet);
                        e.cancel();
                    }
    			}
    			
    			if (e.packet instanceof C02PacketUseEntity)
    			{
    				C02PacketUseEntity packet = (C02PacketUseEntity)e.packet;
    				
    				if (packet.getAction().equals(C02PacketUseEntity.Action.ATTACK) && packet.getEntityFromWorld(mc.theWorld).equals(target))
    				{
    					dispatchPackets();
    					blinking = false;
    				}
    			}
    			
    			if (toDispatch.size() > 60)
    			{
    				dispatchPackets();
    				NotificationManager.publish("Aura onPacket overflowing!", NotificationType.ERROR, 2000);
    				blinking = false;
    			}
    			
    			if (shouldBlock())
    			{
    				if (e.packet instanceof C07PacketPlayerDigging)
    				{
    					C07PacketPlayerDigging packet = (C07PacketPlayerDigging)e.packet;
    					
    					if (packet.getStatus().equals(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM))
    					{
    						e.cancel();
    					}
    				}
    				
    				if (e.packet instanceof C08PacketPlayerBlockPlacement)
    				{
    					C08PacketPlayerBlockPlacement packet = (C08PacketPlayerBlockPlacement) e.packet;
    					
    					if (packet.getPlacedBlockDirection() == 255)
    					{
    						e.cancel();
    					}
    				}
    			}
    		}
    	}
    	else
    	{
    		lossTimer.reset();
    	}
        ClientUtils.displayChatMessage("size " + toDispatch.size());
    }
    
    private void dispatchPackets()
    {
    	blinking = false;
    	toDispatch.forEach(packet -> PacketUtil.sendSilent(packet));
    	ClientUtils.displayChatMessage("packets dispatched");
    	toDispatch.clear();
    }
    
    private void interactAutoblock()
    {
    	if (mc.gameSettings.keyBindUseItem.isKeyDown())
    	{
    		if (mc.objectMouseOver.entityHit != null)
    		{
    			PacketUtil.send(new C02PacketUseEntity(mc.objectMouseOver.entityHit, C02PacketUseEntity.Action.INTERACT));
    		}
    		else if (interactable(mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock()))
    		{
    			mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), mc.objectMouseOver.getBlockPos(), Block.getFacingDirection(mc.objectMouseOver.getBlockPos()), mc.objectMouseOver.hitVec);
    		}
    	}
    }
    
    public void unblock()
    {
    	if (shouldBlock() && blockStatus && blockTimer.delay(50.0F))
		{
    		blockTimer.reset();
//    		mc.thePlayer.setItemInUse(mc.thePlayer.getCurrentEquippedItem(), mc.thePlayer.getCurrentEquippedItem().getMaxItemUseDuration());
 			PacketUtil.sendSilent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
 			mc.gameSettings.keyBindUseItem.pressed = false;
 			//mc.thePlayer.itemInUseCount = 0;
 			blockStatus = false;
		}
    }
    
    public void block(boolean status, boolean sync)
    {
        if (shouldBlock() && status && blockTimer.delay(50.0F))
        {
        	//interactAutoblock();
            //PacketUtil.sendSilent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
            mc.gameSettings.keyBindUseItem.pressed = true;
            PacketUtil.sendSilent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            blockStatus = true;
        }
    }
    
    public boolean shouldBlock() {
        return autoBlock.get() && target != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && target.isEntityAlive() && !mc.playerController.isBreakingBlock();
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null) return;
        target = null;
        blockStatus = false;
        lossTimer.reset();

        if (blink.get())
        {
            toDispatch.clear();
            blinking = true;
        }
        else
        {
            blinking = true;
        }
    }

    @Override
    public void onDisable() {
        target = null;
        if (blockStatus) {
            unblock();
            blockStatus = false;
        }
        dispatchPackets();
        list.clear();
        if (blockStatus) mc.gameSettings.keyBindUseItem.pressed = false;
    }

    @Override
    public String getTagName() {
        return this.displayName + " §7" + mode.get();
    }

    @Override
    public String getTag() {
        return this.mode.get();
    }

    private IntegerValue getMinCPS(){
        return minCPS;
    }

    private boolean getRotationsValue() {
        return rotations.get();
    }

    @SuppressWarnings("null")
	private boolean isValid(EntityLivingBase ent)
    {
    	boolean valid = ent instanceof EntityLivingBase;
    	
    	if (ent == null)
    	{
    		if (ent == mc.thePlayer)
    		{
    			valid = false;
    		}
    		
    		if (raytrace.get() && !mc.thePlayer.canEntityBeSeen(ent))
    		{
    			valid = false;
    		}
    		
    		if (!ent.isEntityAlive() || ent.isDead)
    		{
    			valid = false;
    		}
    		
    		if(!invisibles.get() && ent.isInvisible())
    		{
    			valid = false;
    		}
    		
    		if (!(ent instanceof EntityPlayer) && !mobs.get())
    		{
    			valid = false;
    		}
    		
    		if (ent instanceof EntityArmorStand)
    		{
    			valid = false;
    		}

            if (ent instanceof EntityPlayer && ((EntityPlayer) ent).isPlayerSleeping()) {
                valid = false;
            }

            if (ent instanceof EntityPlayer && ((EntityPlayer) ent).capabilities.isCreativeMode) {
                valid = false;
            }

            if (ent instanceof EntityPlayer && ((EntityPlayer) ent).isSpectator()) {
                valid = false;
            }

            if (ent instanceof EntityPlayer && ((EntityPlayer) ent).getGameProfile().getName().startsWith("\u00A7"))
            {
                valid = false;
            }
            
            if (ent.getName() == "WultuhJr")
            {
            	valid = false;
            }

            if (autisticShopkeeperCheck(ent))
            {
                valid = false;
            }
    	}
    	return valid;
    }
    
    @Subscribe
    public void onRender2D(Render2DEvent e)
    {
    	if (blinking)
    	{
    		ScaledResolution sr = new ScaledResolution(mc);
            int size = toDispatch.size();
            String color = "";
            
            if (size > 40)
            	color = "\u00A78";
            else if (size > 20)
            	color = "\u00A77";
            
            String sizeStr = color + toDispatch.size();
            mc.fontRendererObj.drawString("Buffer size: " + sizeStr, sr.getScaledWidth() / 2 - 26, sr.getScaledHeight() / 2 + 10, 0xFFFFFF, true);
    	}
    }

    private boolean autisticShopkeeperCheck(Entity e)
    {
        IChatComponent chatComponent = e.getDisplayName();
        String formatted = chatComponent.getFormattedText();
        String unformatted = chatComponent.getUnformattedText();
        boolean first = !formatted.substring(0, formatted.length() - 2).contains("\u00A7");
        boolean second = formatted.substring(formatted.length() - 2).contains("\u00A7");
        return first && second;
    }

    public EntityLivingBase getTarget(){
        return isValid(target) ? target : null;
    }

    private boolean isSex(EntityLivingBase target) {
        return target != null && (target.getHealth() > 0  && !target.isDead || EntityManager.Targets.DEAD.on) && mc.thePlayer.getDistanceToEntity(target) <= range.get();
    }
    
    public void drawTargetESP()
    {
        double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
        double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY;
        double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;
        int rgb = Color.HSBtoRGB((System.currentTimeMillis() % 2000L) / 2000.0F, 0.8F, 0.8F);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableAlpha();
        GL11.glLineWidth(3.0F);
        GL11.glShadeModel(7425);
        GL11.glDisable(2884);
        double size = target.width * 1.2D;
        float factor = (float)Math.sin(((float)System.nanoTime() / 3.0E8F));
        GL11.glTranslatef(0.0F, factor, 0.0F);
        GL11.glBegin(5);
        
        for (int j = 0; j < 361; j++)
        {
        	RenderUtil.color(ColorUtil.getColorAlpha(rgb, 160));
            double x1 = x + Math.cos(Math.toRadians(j)) * size;
            double z1 = z - Math.sin(Math.toRadians(j)) * size;
            GL11.glVertex3d(x1, y + 1.0D, z1);
            RenderUtil.color(ColorUtil.getColorAlpha(rgb, 0));
            GL11.glVertex3d(x1, y + 1.0D + (factor * 0.4F), z1);
        }
        GL11.glEnd();
        GL11.glBegin(2);

        for (int i = 0; i < 361; i++) 
        {
            RenderUtil.color(ColorUtil.getColorAlpha(rgb, 50));
            GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * size, y + 1.0D, z - Math.sin(Math.toRadians(i)) * size);
        } 

        GL11.glEnd();
        GlStateManager.enableAlpha();
        GL11.glShadeModel(7424);
        GL11.glDisable(2848);
        GL11.glEnable(2884);
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
        GlStateManager.popMatrix();
    }
    
    private boolean interactable(Block block) 
    {
        return block == Blocks.chest || block == Blocks.trapped_chest || block == Blocks.crafting_table || block == Blocks.furnace || block == Blocks.ender_chest || block == Blocks.enchanting_table;
    }
}