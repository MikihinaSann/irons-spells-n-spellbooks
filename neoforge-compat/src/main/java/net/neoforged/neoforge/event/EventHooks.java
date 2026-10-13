package net.neoforged.neoforge.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;

/**
 * Static event-hook helpers; on Fabric they post the corresponding shim events
 * and fall back to vanilla behavior when nothing handles them.
 */
public class EventHooks {

    public static boolean canEntityGrief(Level level, Entity entity) {
        EntityMobGriefingEvent event = new EntityMobGriefingEvent(level, entity);
        NeoForge.EVENT_BUS.post(event);
        return !event.isCanceled() && level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
    }

    @SuppressWarnings("unchecked")
    public static boolean checkSpawnPosition(Entity entity, LevelAccessor level, MobSpawnType spawnType) {
        if (entity instanceof Monster monster && level instanceof ServerLevelAccessor sla) {
            return Monster.checkMonsterSpawnRules((EntityType<? extends Monster>) entity.getType(), sla, spawnType,
                    entity.blockPosition(), level.getRandom());
        }
        return true;
    }

    /**
     * Whether the entity may destroy the block. NeoForge fires an event here;
     * without listeners the default is true.
     */
    public static boolean onEntityDestroyBlock(net.minecraft.world.entity.LivingEntity entity, BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        return true;
    }
}
