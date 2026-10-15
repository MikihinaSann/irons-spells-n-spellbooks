package com.somake.somakespells.event;

import com.github.L_Ender.cataclysm.blockentities.AltarOfFire_Block_Entity;
import com.somake.somakespells.block.entity.PedestalOfFireBlockEntity;
import com.somake.somakespells.block.entity.SoulFireAltarState;
import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.entity.RitualFireSigilEntity;
import com.somake.somakespells.item.ElementalChargeNecklaceItem;
import com.somake.somakespells.recipe.pedestal_ritual.PedestalRitualRecipe;
import com.somake.somakespells.registries.ModParticles;
import com.somake.somakespells.registries.ModRecipes;
import com.somake.somakespells.ritual.RitualElementalMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public class PedestalRitualHandler {
   private static final int RITUAL_DURATION_TICKS = 110;
   private static final int SOUND_REPEAT_INTERVAL = 14;
   private static final int PREVIEW_SCAN_INTERVAL = 20;
   private static final int PREVIEW_DURATION_TICKS = 14;
   private static final int PREVIEW_HORIZONTAL_RADIUS = 5;
   private static final int PREVIEW_VERTICAL_RADIUS = 2;
   private static final int SOUL_FIRE_RITUAL_CHARGES = 5;
   private static final int INFERNAL_FIRE_RITUAL_CHARGES = 5;
   private static final float SIGIL_RADIUS = 1.85F;
   private static final ResourceLocation FLAME_TRAP_SOUND_ID = ResourceLocation.fromNamespaceAndPath("cataclysm", "flame_trap");
   private static final ResourceLocation FINISH_SOUND_ID = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "entity.fire_boss.stance_break");
   private static final Map<PedestalRitualHandler.RitualKey, PedestalRitualHandler.ActiveRitual> ACTIVE_RITUALS = new ConcurrentHashMap<>();

   public static PedestalRitualHandler.RitualActivationResult validateRitualActivation(ServerLevel level, BlockPos altarPos) {
      return validateRitualActivation(level, altarPos, RitualElementalMode.NORMAL);
   }

   public static PedestalRitualHandler.RitualActivationResult validateRitualActivation(ServerLevel level, BlockPos altarPos, RitualElementalMode activationMode) {
      PedestalRitualHandler.RitualKey ritualKey = new PedestalRitualHandler.RitualKey(level.dimension(), altarPos.immutable());
      if (ACTIVE_RITUALS.containsKey(ritualKey)) {
         return PedestalRitualHandler.RitualActivationResult.ALREADY_ACTIVE;
      } else if (level.getBlockEntity(altarPos) instanceof AltarOfFire_Block_Entity altar) {
         if (!isActivationModeCompatible(getRitualMode(altar), activationMode)) {
            return PedestalRitualHandler.RitualActivationResult.WRONG_ACTIVATION_MODE;
         } else {
            return resolveMatchingRecipe(level, altarPos, altar).isPresent()
               ? PedestalRitualHandler.RitualActivationResult.READY
               : PedestalRitualHandler.RitualActivationResult.NO_RECIPE;
         }
      } else {
         return PedestalRitualHandler.RitualActivationResult.INVALID_ALTAR;
      }
   }

   public static RitualElementalMode getAltarRitualMode(Level level, BlockPos altarPos) {
      return level.getBlockEntity(altarPos) instanceof AltarOfFire_Block_Entity altar ? getRitualMode(altar) : RitualElementalMode.NORMAL;
   }

   public static PedestalRitualHandler.RitualActivationResult tryStartRitual(ServerLevel level, BlockPos altarPos) {
      return tryStartRitual(level, altarPos, RitualElementalMode.NORMAL);
   }

   public static PedestalRitualHandler.RitualActivationResult tryStartRitual(ServerLevel level, BlockPos altarPos, RitualElementalMode activationMode) {
      PedestalRitualHandler.RitualKey ritualKey = new PedestalRitualHandler.RitualKey(level.dimension(), altarPos.immutable());
      if (ACTIVE_RITUALS.containsKey(ritualKey)) {
         return PedestalRitualHandler.RitualActivationResult.ALREADY_ACTIVE;
      } else if (level.getBlockEntity(altarPos) instanceof AltarOfFire_Block_Entity altar) {
         if (!isActivationModeCompatible(getRitualMode(altar), activationMode)) {
            return PedestalRitualHandler.RitualActivationResult.WRONG_ACTIVATION_MODE;
         } else {
            Optional<PedestalRitualHandler.ResolvedRitualMatch> match = resolveMatchingRecipe(level, altarPos, altar);
            if (match.isEmpty()) {
               return PedestalRitualHandler.RitualActivationResult.NO_RECIPE;
            } else {
               beginRitual(level, altarPos, match.get());
               return PedestalRitualHandler.RitualActivationResult.STARTED;
            }
         }
      } else {
         return PedestalRitualHandler.RitualActivationResult.INVALID_ALTAR;
      }
   }

   @SubscribeEvent
   public static void onServerTick(Post event) {
      Iterator<Entry<PedestalRitualHandler.RitualKey, PedestalRitualHandler.ActiveRitual>> iterator = ACTIVE_RITUALS.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry<PedestalRitualHandler.RitualKey, PedestalRitualHandler.ActiveRitual> entry = iterator.next();
         ServerLevel level = event.getServer().getLevel(entry.getKey().dimension());
         if (level == null) {
            iterator.remove();
         } else {
            PedestalRitualHandler.ActiveRitual ritual = entry.getValue();
            if (!ritual.isStillValid(level)) {
               ritual.stopAnimation(level);
               iterator.remove();
            } else {
               ritual.tick(level);
               if (ritual.isComplete()) {
                  ritual.finish(level);
                  iterator.remove();
               }
            }
         }
      }

      for (ServerLevel level : event.getServer().getAllLevels()) {
         for (Player player : level.players()) {
            if (player.tickCount % 20 == 0) {
               scanPreviewNearbyAltars(level, player);
            }
         }
      }
   }

   public static boolean isSoulFireLinkValid(Level level, BlockPos altarPos, BlockPos pedestalPos) {
      if (level.getBlockEntity(altarPos) instanceof AltarOfFire_Block_Entity altar && altar instanceof SoulFireAltarState soulFireState) {
         RitualElementalMode altarMode = soulFireState.somake$getRitualMode();
         if (!altarMode.isEmpowered()) {
            return false;
         } else if (!soulFireState.somake$getRitualPedestals().contains(pedestalPos.immutable())) {
            return false;
         } else {
            return !(level.getBlockEntity(pedestalPos) instanceof PedestalOfFireBlockEntity pedestal)
               ? false
               : pedestal.isEmpoweredRitualActive() && pedestal.getRitualMode() == altarMode && altarPos.equals(pedestal.getRitualAltarPos());
         }
      } else {
         return false;
      }
   }

   public static void resetSoulFireNetwork(ServerLevel level, BlockPos altarPos) {
      if (level.getBlockEntity(altarPos) instanceof AltarOfFire_Block_Entity altar && altar instanceof SoulFireAltarState soulFireState) {
         soulFireState.somake$clearRitualMode();
      }

      for (int offsetX = -2; offsetX <= 2; offsetX++) {
         for (int offsetZ = -2; offsetZ <= 2; offsetZ++) {
            if (offsetX != 0 || offsetZ != 0) {
               BlockPos pedestalPos = altarPos.offset(offsetX, 0, offsetZ);
               if (level.getBlockEntity(pedestalPos) instanceof PedestalOfFireBlockEntity pedestal
                  && pedestal.isEmpoweredRitualActive()
                  && altarPos.equals(pedestal.getRitualAltarPos())) {
                  pedestal.clearRitualMode();
               }
            }
         }
      }
   }

   private static Optional<PedestalRitualHandler.RitualContext> resolveContext(ServerLevel level, BlockPos altarPos, AltarOfFire_Block_Entity altar) {
      Set<BlockPos> soulFirePedestalPositions = getSoulFirePedestalPositions(level, altarPos, altar);
      List<PedestalOfFireBlockEntity> pedestals = new ArrayList<>();
      List<NonNullList<ItemStack>> pedestalItems = new ArrayList<>();

      for (int offsetX = -2; offsetX <= 2; offsetX++) {
         for (int offsetZ = -2; offsetZ <= 2; offsetZ++) {
            if (offsetX != 0 || offsetZ != 0) {
               BlockPos pedestalPos = altarPos.offset(offsetX, 0, offsetZ);
               if (soulFirePedestalPositions.isEmpty() || soulFirePedestalPositions.contains(pedestalPos)) {
                  PedestalOfFireBlockEntity pedestal = getPedestal(level, pedestalPos);
                  if (pedestal != null) {
                     pedestals.add(pedestal);
                     pedestalItems.add(copyItems(pedestal));
                  }
               }
            }
         }
      }

      if (pedestals.isEmpty()) {
         return Optional.empty();
      } else if (!soulFirePedestalPositions.isEmpty() && pedestals.size() != soulFirePedestalPositions.size()) {
         resetSoulFireNetwork(level, altarPos);
         return Optional.empty();
      } else {
         return Optional.of(new PedestalRitualHandler.RitualContext(altar.getItem(0).copy(), pedestals, pedestalItems));
      }
   }

   private static Optional<PedestalRitualHandler.ResolvedRitualMatch> resolveMatchingRecipe(
      ServerLevel level, BlockPos altarPos, AltarOfFire_Block_Entity altar
   ) {
      Optional<PedestalRitualHandler.RitualContext> ritualContext = resolveContext(level, altarPos, altar);
      if (ritualContext.isEmpty()) {
         return Optional.empty();
      } else {
         RitualElementalMode ritualMode = getRitualMode(altar);
         PedestalRitualRecipe.Input input = new PedestalRitualRecipe.Input(ritualContext.get().centerItem(), ritualContext.get().pedestalItems());
         return level.getRecipeManager()
            .getAllRecipesFor((RecipeType<PedestalRitualRecipe>)(RecipeType<?>)ModRecipes.PEDESTAL_RITUAL_TYPE.get())
            .stream()
            .<PedestalRitualRecipe>map(RecipeHolder::value)
            .filter(recipe -> isRecipeCompatibleWithMode(recipe, ritualMode))
            .filter(recipe -> recipe.matchesCenter(input.centerItem()))
            .map(
               recipe -> recipe.findMatchingPedestalIndexes(input.pedestals())
                  .map(
                     indexes -> new PedestalRitualHandler.ResolvedRitualMatch(
                        recipe, indexes.stream().map(index -> ritualContext.get().pedestals().get(index)).toList()
                     )
                  )
            )
            .flatMap(Optional::stream)
            .findFirst();
      }
   }

   private static Optional<PedestalRitualHandler.ResolvedRitualMatch> findMatchingRecipe(ServerLevel level, PedestalRitualHandler.RitualContext context) {
      PedestalRitualRecipe.Input input = new PedestalRitualRecipe.Input(context.centerItem(), context.pedestalItems());
      return level.getRecipeManager()
         .getAllRecipesFor((RecipeType<PedestalRitualRecipe>)(RecipeType<?>)ModRecipes.PEDESTAL_RITUAL_TYPE.get())
         .stream()
         .<PedestalRitualRecipe>map(RecipeHolder::value)
         .filter(recipe -> recipe.matchesCenter(input.centerItem()))
         .map(
            recipe -> recipe.findMatchingPedestalIndexes(input.pedestals())
               .map(indexes -> new PedestalRitualHandler.ResolvedRitualMatch(recipe, indexes.stream().map(index -> context.pedestals().get(index)).toList()))
         )
         .flatMap(Optional::stream)
         .findFirst();
   }

   private static void beginRitual(ServerLevel level, BlockPos altarPos, PedestalRitualHandler.ResolvedRitualMatch match) {
      if (level.getBlockEntity(altarPos) instanceof AltarOfFire_Block_Entity altar) {
         boolean var12 = match.recipe().activatesSoulFire();
         boolean activateInfernalFireOnFinish = match.recipe().activatesInfernalFire();
         RitualElementalMode visualMode = getRitualMode(altar);
         boolean consumeSoulFireOnFinish = visualMode.isEmpowered() && !var12 && !activateInfernalFireOnFinish;
         ItemStack centerItem = altar.getItem(0).copy();
         altar.placeItem(null, 0, ItemStack.EMPTY);

         for (PedestalOfFireBlockEntity pedestal : match.matchedPedestals()) {
            pedestal.beginRitualAnimation(110);
            pedestal.setPreviewTicks(0);
         }

         ItemEntity centerVisualEntity = spawnCenterVisualEntity(level, altarPos, centerItem);
         RitualFireSigilEntity sigilEntity = spawnSigilEntity(level, altarPos, visualMode);
         PedestalRitualHandler.ActiveRitual ritual = new PedestalRitualHandler.ActiveRitual(
            altarPos.immutable(),
            match.recipe(),
            match.matchedPedestals().stream().map(BlockEntity::getBlockPos).<BlockPos>map(BlockPos::immutable).toList(),
            centerItem,
            centerVisualEntity,
            sigilEntity,
            110,
            visualMode,
            var12,
            activateInfernalFireOnFinish,
            consumeSoulFireOnFinish
         );
         ACTIVE_RITUALS.put(new PedestalRitualHandler.RitualKey(level.dimension(), altarPos.immutable()), ritual);
         level.playSound(null, altarPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.9F, 0.85F + level.random.nextFloat() * 0.1F);
         playFlameTrap(level, altarPos);
      }
   }

   private static void executeRecipe(ServerLevel level, BlockPos altarPos, PedestalRitualHandler.ResolvedRitualMatch match) {
      PedestalRitualRecipe recipe = match.recipe();

      for (int i = 0; i < recipe.pedestalGroups().size(); i++) {
         consumeRequirement(match.matchedPedestals().get(i), recipe.pedestalGroups().get(i));
      }

      ItemStack result = recipe.result().copy();
      ElementalChargeNecklaceItem.rechargeIfElementalNecklace(result);
      Containers.dropItemStack(level, altarPos.getX() + 0.5, altarPos.getY() + 1.1, altarPos.getZ() + 0.5, result);
   }

   private static PedestalOfFireBlockEntity getPedestal(Level level, BlockPos pos) {
      return level.getBlockEntity(pos) instanceof PedestalOfFireBlockEntity pedestal ? pedestal : null;
   }

   private static NonNullList<ItemStack> copyItems(PedestalOfFireBlockEntity pedestal) {
      NonNullList<ItemStack> copied = NonNullList.withSize(16, ItemStack.EMPTY);

      for (int i = 0; i < pedestal.getItems().size(); i++) {
         copied.set(i, ((ItemStack)pedestal.getItems().get(i)).copy());
      }

      return copied;
   }

   private static ItemEntity spawnCenterVisualEntity(ServerLevel level, BlockPos altarPos, ItemStack stack) {
      ItemEntity entity = new ItemEntity(level, altarPos.getX() + 0.5, altarPos.getY() + 1.08, altarPos.getZ() + 0.5, stack.copy());
      entity.setNoGravity(true);
      entity.setPickUpDelay(Integer.MAX_VALUE);
      entity.setDeltaMovement(0.0, 0.0, 0.0);
      level.addFreshEntity(entity);
      return entity;
   }

   private static RitualFireSigilEntity spawnSigilEntity(ServerLevel level, BlockPos altarPos, RitualElementalMode ritualMode) {
      RitualFireSigilEntity sigil = new RitualFireSigilEntity(
         level, altarPos.getX() + 0.5, altarPos.getY() + 0.02, altarPos.getZ() + 0.5, 1.85F, 110, ritualMode
      );
      level.addFreshEntity(sigil);
      return sigil;
   }

   private static void updateCenterVisualEntity(ServerLevel level, PedestalRitualHandler.ActiveRitual ritual) {
      ItemEntity entity = ritual.centerVisualEntity();
      if (entity != null && entity.isAlive()) {
         float progress = ritual.progress();
         float bob = (float)((Math.sin(ritual.elapsedTicks() * 0.35F) + 1.0) * 0.5);
         double yOffset;
         if (progress < 0.35F) {
            yOffset = 1.08 + progress / 0.35F * 0.22;
         } else if (progress < 0.7F) {
            yOffset = 1.3 + bob * 0.06;
         } else {
            double descent = Math.min(1.0, (double)((progress - 0.7F) / 0.2F));
            yOffset = 1.3 - descent * 0.36;
         }

         entity.setNoGravity(true);
         entity.setDeltaMovement(0.0, 0.0, 0.0);
         entity.setPos(ritual.altarPos().getX() + 0.5, ritual.altarPos().getY() + yOffset, ritual.altarPos().getZ() + 0.5);
      }
   }

   private static void emitPreviewParticles(ServerLevel level, BlockPos altarPos, RitualElementalMode ritualMode) {
      level.sendParticles(getCoreParticle(ritualMode), altarPos.getX() + 0.5, altarPos.getY() + 1.06, altarPos.getZ() + 0.5, 2, 0.05, 0.02, 0.05, 0.0);
      level.sendParticles(ParticleTypes.SMOKE, altarPos.getX() + 0.5, altarPos.getY() + 1.1, altarPos.getZ() + 0.5, 1, 0.04, 0.01, 0.04, 0.0);
   }

   private static void emitAltarParticles(ServerLevel level, BlockPos altarPos, RitualElementalMode ritualMode) {
      level.sendParticles(getMainRitualParticle(ritualMode), altarPos.getX() + 0.5, altarPos.getY() + 1.15, altarPos.getZ() + 0.5, 8, 0.12, 0.08, 0.12, 0.01);
      level.sendParticles(getFlameRitualParticle(ritualMode), altarPos.getX() + 0.5, altarPos.getY() + 1.26, altarPos.getZ() + 0.5, 5, 0.1, 0.05, 0.1, 0.0);
      level.sendParticles(ParticleTypes.LARGE_SMOKE, altarPos.getX() + 0.5, altarPos.getY() + 1.2, altarPos.getZ() + 0.5, 2, 0.08, 0.03, 0.08, 0.0);
   }

   private static void emitFinalBurst(ServerLevel level, BlockPos altarPos, RitualElementalMode ritualMode) {
      level.sendParticles(getMainRitualParticle(ritualMode), altarPos.getX() + 0.5, altarPos.getY() + 1.2, altarPos.getZ() + 0.5, 18, 0.2, 0.12, 0.2, 0.03);
      level.sendParticles(getFlameRitualParticle(ritualMode), altarPos.getX() + 0.5, altarPos.getY() + 1.24, altarPos.getZ() + 0.5, 28, 0.3, 0.14, 0.3, 0.04);
      level.sendParticles(ParticleTypes.LARGE_SMOKE, altarPos.getX() + 0.5, altarPos.getY() + 1.18, altarPos.getZ() + 0.5, 12, 0.22, 0.1, 0.22, 0.01);
   }

   private static ParticleOptions getCoreParticle(RitualElementalMode ritualMode) {
      return getMainRitualParticle(ritualMode);
   }

   private static ParticleOptions getMainRitualParticle(RitualElementalMode ritualMode) {
      return (ParticleOptions)(switch (ritualMode) {
         case SOUL_FIRE -> (SimpleParticleType)ModParticles.SOUL_FIRE.get();
         case INFERNAL_FIRE -> getInfernalCoreParticle();
         default -> (SimpleParticleType)ModParticles.FIRE.get();
      });
   }

   private static ParticleOptions getFlameRitualParticle(RitualElementalMode ritualMode) {
      return switch (ritualMode) {
         case SOUL_FIRE -> ParticleTypes.SOUL_FIRE_FLAME;
         case INFERNAL_FIRE -> ModParticles.infernalFireFlameOr((SimpleParticleType)ModParticles.SOUL_FIRE.get());
         default -> ParticleTypes.FLAME;
      };
   }

   private static ParticleOptions getInfernalCoreParticle() {
      return getExternalSimpleParticle("born_in_chaos_v1:soul_fire", (SimpleParticleType)ModParticles.SOUL_FIRE.get());
   }

   private static ParticleOptions getExternalSimpleParticle(String particleId, SimpleParticleType fallback) {
      return (ParticleOptions)BuiltInRegistries.PARTICLE_TYPE
         .getOptional(ResourceLocation.parse(particleId))
         .filter(SimpleParticleType.class::isInstance)
         .map(SimpleParticleType.class::cast)
         .orElse(fallback);
   }

   private static void playFlameTrap(ServerLevel level, BlockPos altarPos) {
      SoundEvent soundEvent = (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(FLAME_TRAP_SOUND_ID);
      if (soundEvent != null) {
         level.playSound(null, altarPos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
   }

   private static void playFinishSound(ServerLevel level, BlockPos altarPos) {
      SoundEvent soundEvent = (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(FINISH_SOUND_ID);
      if (soundEvent != null) {
         level.playSound(null, altarPos, soundEvent, SoundSource.BLOCKS, 1.15F, 1.0F);
      }
   }

   private static void scanPreviewNearbyAltars(ServerLevel level, Player player) {
      int baseX = player.blockPosition().getX();
      int baseY = player.blockPosition().getY();
      int baseZ = player.blockPosition().getZ();

      for (int x = baseX - 5; x <= baseX + 5; x++) {
         for (int y = baseY - 2; y <= baseY + 2; y++) {
            for (int z = baseZ - 5; z <= baseZ + 5; z++) {
               BlockPos altarPos = new BlockPos(x, y, z);
               if (level.getBlockEntity(altarPos) instanceof AltarOfFire_Block_Entity altar
                  && !ACTIVE_RITUALS.containsKey(new PedestalRitualHandler.RitualKey(level.dimension(), altarPos.immutable()))) {
                  Optional<PedestalRitualHandler.ResolvedRitualMatch> match = resolveMatchingRecipe(level, altarPos, altar);
                  if (!match.isEmpty()) {
                     emitPreviewParticles(level, altarPos, getRitualMode(altar));

                     for (PedestalOfFireBlockEntity pedestal : match.get().matchedPedestals()) {
                        pedestal.setPreviewTicks(14);
                     }
                  }
               }
            }
         }
      }
   }

   private static void consumeRequirement(PedestalOfFireBlockEntity pedestal, PedestalRitualRecipe.PedestalRequirement requirement) {
      for (PedestalRitualRecipe.CountedIngredient ingredient : requirement.ingredients()) {
         pedestal.consumeIngredient(ingredient.ingredient(), ingredient.count());
      }
   }

   private static Set<BlockPos> getSoulFirePedestalPositions(ServerLevel level, BlockPos altarPos, AltarOfFire_Block_Entity altar) {
      RitualElementalMode altarMode = getRitualMode(altar);
      if (altarMode.isEmpowered() && altar instanceof SoulFireAltarState soulFireState) {
         List<BlockPos> positions = soulFireState.somake$getRitualPedestals();
         if (positions.isEmpty()) {
            resetSoulFireNetwork(level, altarPos);
            return Set.of();
         } else {
            Set<BlockPos> validPositions = new HashSet<>();

            for (BlockPos pedestalPos : positions) {
               if (!(level.getBlockEntity(pedestalPos) instanceof PedestalOfFireBlockEntity pedestal)
                  || !pedestal.isEmpoweredRitualActive()
                  || pedestal.getRitualMode() != altarMode
                  || !altarPos.equals(pedestal.getRitualAltarPos())) {
                  resetSoulFireNetwork(level, altarPos);
                  return Set.of();
               }

               validPositions.add(pedestalPos.immutable());
            }

            return validPositions;
         }
      } else {
         return Set.of();
      }
   }

   private static void activateSoulFireNetwork(ServerLevel level, BlockPos altarPos, List<PedestalOfFireBlockEntity> pedestals) {
      activateRitualMode(level, altarPos, pedestals, RitualElementalMode.SOUL_FIRE, 5);
   }

   private static void activateInfernalFireNetwork(ServerLevel level, BlockPos altarPos, List<PedestalOfFireBlockEntity> pedestals) {
      activateRitualMode(level, altarPos, pedestals, RitualElementalMode.INFERNAL_FIRE, 5);
   }

   private static void consumeRitualCharge(ServerLevel level, BlockPos altarPos) {
      if (level.getBlockEntity(altarPos) instanceof AltarOfFire_Block_Entity altar && altar instanceof SoulFireAltarState soulFireState) {
         RitualElementalMode var11 = soulFireState.somake$getRitualMode();
         int remainingCharges = soulFireState.somake$getRitualCharges() - 1;
         if (var11.isEmpowered() && remainingCharges > 0) {
            List<BlockPos> pedestalPositions = soulFireState.somake$getRitualPedestals();
            soulFireState.somake$activateRitualMode(var11, pedestalPositions, remainingCharges);

            for (BlockPos pedestalPos : pedestalPositions) {
               if (level.getBlockEntity(pedestalPos) instanceof PedestalOfFireBlockEntity pedestal) {
                  pedestal.activateRitualMode(var11, altarPos);
               }
            }
         } else {
            resetSoulFireNetwork(level, altarPos);
         }
      }
   }

   private static void activateRitualMode(
      ServerLevel level, BlockPos altarPos, List<PedestalOfFireBlockEntity> pedestals, RitualElementalMode ritualMode, int charges
   ) {
      if (level.getBlockEntity(altarPos) instanceof AltarOfFire_Block_Entity altar && altar instanceof SoulFireAltarState soulFireState) {
         List<PedestalOfFireBlockEntity> pedestalsInArea = collectPedestalsInArea(level, altarPos);
         List pedestalPositions = pedestalsInArea.stream().map(BlockEntity::getBlockPos).map(BlockPos::immutable).toList();
         soulFireState.somake$activateRitualMode(ritualMode, pedestalPositions, charges);

         for (PedestalOfFireBlockEntity pedestal : pedestalsInArea) {
            pedestal.activateRitualMode(ritualMode, altarPos);
         }
      }
   }

   private static List<PedestalOfFireBlockEntity> collectPedestalsInArea(ServerLevel level, BlockPos altarPos) {
      List<PedestalOfFireBlockEntity> pedestals = new ArrayList<>();

      for (int offsetX = -2; offsetX <= 2; offsetX++) {
         for (int offsetZ = -2; offsetZ <= 2; offsetZ++) {
            if (offsetX != 0 || offsetZ != 0) {
               BlockPos pedestalPos = altarPos.offset(offsetX, 0, offsetZ);
               if (level.getBlockEntity(pedestalPos) instanceof PedestalOfFireBlockEntity pedestal) {
                  pedestals.add(pedestal);
               }
            }
         }
      }

      return pedestals;
   }

   private static boolean isSoulFireModeActive(AltarOfFire_Block_Entity altar) {
      return altar instanceof SoulFireAltarState soulFireState && soulFireState.somake$isSoulFireActive();
   }

   private static RitualElementalMode getRitualMode(AltarOfFire_Block_Entity altar) {
      return altar instanceof SoulFireAltarState soulFireState ? soulFireState.somake$getRitualMode() : RitualElementalMode.NORMAL;
   }

   private static boolean isRecipeCompatibleWithMode(PedestalRitualRecipe recipe, RitualElementalMode ritualMode) {
      if (recipe.activatesSoulFire()) {
         return ritualMode == RitualElementalMode.NORMAL;
      } else if (recipe.activatesInfernalFire()) {
         return !BornInChaosCompat.isLoaded() ? false : ritualMode == RitualElementalMode.SOUL_FIRE;
      } else {
         return !BornInChaosCompat.isLoaded() && recipe.requiredMode() == RitualElementalMode.INFERNAL_FIRE
            ? false
            : recipe.requiredMode() == RitualElementalMode.NORMAL || recipe.requiredMode() == ritualMode;
      }
   }

   private static boolean isActivationModeCompatible(RitualElementalMode altarMode, RitualElementalMode activationMode) {
      return !altarMode.isEmpowered() || altarMode == activationMode;
   }

   private static class ActiveRitual {
      private final BlockPos altarPos;
      private final PedestalRitualRecipe recipe;
      private final List<BlockPos> matchedPedestalPositions;
      private final ItemStack centerItem;
      private final ItemEntity centerVisualEntity;
      private final RitualFireSigilEntity sigilEntity;
      private final int durationTicks;
      private final RitualElementalMode visualMode;
      private final boolean activateSoulFireOnFinish;
      private final boolean activateInfernalFireOnFinish;
      private final boolean consumeSoulFireOnFinish;
      private int elapsedTicks;

      private ActiveRitual(
         BlockPos altarPos,
         PedestalRitualRecipe recipe,
         List<BlockPos> matchedPedestalPositions,
         ItemStack centerItem,
         ItemEntity centerVisualEntity,
         RitualFireSigilEntity sigilEntity,
         int durationTicks,
         RitualElementalMode visualMode,
         boolean activateSoulFireOnFinish,
         boolean activateInfernalFireOnFinish,
         boolean consumeSoulFireOnFinish
      ) {
         this.altarPos = altarPos;
         this.recipe = recipe;
         this.matchedPedestalPositions = matchedPedestalPositions;
         this.centerItem = centerItem;
         this.centerVisualEntity = centerVisualEntity;
         this.sigilEntity = sigilEntity;
         this.durationTicks = durationTicks;
         this.visualMode = visualMode;
         this.activateSoulFireOnFinish = activateSoulFireOnFinish;
         this.activateInfernalFireOnFinish = activateInfernalFireOnFinish;
         this.consumeSoulFireOnFinish = consumeSoulFireOnFinish;
      }

      private void tick(ServerLevel level) {
         this.elapsedTicks++;
         PedestalRitualHandler.emitAltarParticles(level, this.altarPos, this.visualMode);
         PedestalRitualHandler.updateCenterVisualEntity(level, this);
         if (this.elapsedTicks % 14 == 0) {
            PedestalRitualHandler.playFlameTrap(level, this.altarPos);
         }
      }

      private boolean isStillValid(ServerLevel level) {
         if (!(level.getBlockEntity(this.altarPos) instanceof AltarOfFire_Block_Entity)) {
            return false;
         } else {
            for (BlockPos pedestalPos : this.matchedPedestalPositions) {
               if (!(level.getBlockEntity(pedestalPos) instanceof PedestalOfFireBlockEntity)) {
                  return false;
               }
            }

            return true;
         }
      }

      private boolean isComplete() {
         return this.elapsedTicks >= this.durationTicks;
      }

      private void finish(ServerLevel level) {
         if (level.getBlockEntity(this.altarPos) instanceof AltarOfFire_Block_Entity altar) {
            ArrayList var8 = new ArrayList();

            for (BlockPos pedestalPos : this.matchedPedestalPositions) {
               if (level.getBlockEntity(pedestalPos) instanceof PedestalOfFireBlockEntity pedestal) {
                  var8.add(pedestal);
               }
            }

            PedestalRitualHandler.executeRecipe(level, this.altarPos, new PedestalRitualHandler.ResolvedRitualMatch(this.recipe, var8));
            if (this.centerVisualEntity != null && this.centerVisualEntity.isAlive()) {
               this.centerVisualEntity.discard();
            }

            if (this.sigilEntity != null && this.sigilEntity.isAlive()) {
               this.sigilEntity.discard();
            }

            PedestalRitualHandler.emitFinalBurst(level, this.altarPos, this.visualMode);
            PedestalRitualHandler.playFinishSound(level, this.altarPos);
            this.stopAnimation(level);
            if (this.activateSoulFireOnFinish) {
               PedestalRitualHandler.activateSoulFireNetwork(level, this.altarPos, var8);
            } else if (this.activateInfernalFireOnFinish) {
               PedestalRitualHandler.activateInfernalFireNetwork(level, this.altarPos, var8);
            } else if (this.consumeSoulFireOnFinish) {
               PedestalRitualHandler.consumeRitualCharge(level, this.altarPos);
            }
         } else {
            this.cancel(level);
         }
      }

      private void cancel(ServerLevel level) {
         if (level.getBlockEntity(this.altarPos) instanceof AltarOfFire_Block_Entity altar && altar.getItem(0).isEmpty()) {
            altar.placeItem(null, 0, this.centerItem.copy());
         }

         if (this.centerVisualEntity != null && this.centerVisualEntity.isAlive()) {
            this.centerVisualEntity.discard();
         }

         if (this.sigilEntity != null && this.sigilEntity.isAlive()) {
            this.sigilEntity.discard();
         }

         this.stopAnimation(level);
         if (this.consumeSoulFireOnFinish) {
            PedestalRitualHandler.resetSoulFireNetwork(level, this.altarPos);
         }
      }

      private void stopAnimation(ServerLevel level) {
         for (BlockPos pedestalPos : this.matchedPedestalPositions) {
            if (level.getBlockEntity(pedestalPos) instanceof PedestalOfFireBlockEntity pedestal) {
               pedestal.endRitualAnimation();
            }
         }
      }

      private float progress() {
         return Math.min(1.0F, (float)this.elapsedTicks / this.durationTicks);
      }

      private BlockPos altarPos() {
         return this.altarPos;
      }

      private ItemEntity centerVisualEntity() {
         return this.centerVisualEntity;
      }

      private int elapsedTicks() {
         return this.elapsedTicks;
      }
   }

   private record ResolvedRitualMatch(PedestalRitualRecipe recipe, List<PedestalOfFireBlockEntity> matchedPedestals) {
   }

   public static enum RitualActivationResult {
      READY,
      STARTED,
      INVALID_ALTAR,
      ALREADY_ACTIVE,
      WRONG_ACTIVATION_MODE,
      NO_RECIPE;
   }

   private record RitualContext(ItemStack centerItem, List<PedestalOfFireBlockEntity> pedestals, List<NonNullList<ItemStack>> pedestalItems) {
   }

   private record RitualKey(ResourceKey<Level> dimension, BlockPos altarPos) {
   }
}
