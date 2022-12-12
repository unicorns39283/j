package cat.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;

public class EntityManager extends MinecraftInstance{
    public enum Targets{
        MOBS("Mobs", true),
        PLAYERS("Players", true),
        ANIMALS("Animals", true),
        INVISIBLE("Invisible", true),
        DEAD("Dead", false),
        TEAMS("Teams", false)
        ;
        public String displayName;
        public boolean on;
        Targets(String displayName, boolean on){
            this.displayName = displayName;
            this.on = on;
        }
    }
    public static boolean isTarget(Entity ent){
        if(ent == mc.thePlayer || ent instanceof EntityArmorStand)
            return false;
        if(ent instanceof EntityLivingBase && ((EntityLivingBase) ent).getHealth() <= 0 && !Targets.DEAD.on)
            return false;
        if(ent instanceof EntityLivingBase){
            Team lol = ((EntityLivingBase) ent).getTeam();
            Team lel = mc.thePlayer.getTeam();
            if(lol != null && lel != null && lol.isSameTeam(lel)){
                return Targets.TEAMS.on;
            }
        }
        if(isMob(ent))
            return Targets.MOBS.on;
        if(ent instanceof EntityPlayer)
            return Targets.PLAYERS.on;
        if(isAnimal(ent))
            return Targets.ANIMALS.on;
        if(ent.isInvisible())
            return Targets.INVISIBLE.on;

        return false;
    }
    public static boolean isAnimal(Entity ent){
        return ent instanceof EntitySheep || ent instanceof EntityCow || ent instanceof EntityPig
                || ent instanceof EntityChicken || ent instanceof EntityRabbit || ent instanceof EntityHorse
                || ent instanceof EntityBat;
    }
    public static boolean isMob(Entity ent){
        return ent instanceof EntityZombie || ent instanceof EntitySkeleton
                || ent instanceof EntityVillager || ent instanceof EntitySlime
                || ent instanceof EntityCreeper || ent instanceof EntityEnderman
                || ent instanceof EntityEndermite || ent instanceof EntitySpider
                || ent instanceof EntityWitch || ent instanceof EntityWither || ent instanceof EntityBlaze;
    }
}
