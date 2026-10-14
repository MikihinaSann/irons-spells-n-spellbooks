package issprobe;

import com.mojang.logging.LogUtils;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.conditions.IConditionalOpsProvider;
import net.neoforged.neoforge.common.extensions.*;
import net.neoforged.neoforge.entity.IEntityExtension;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.slf4j.Logger;

/** Temporary production-ABI probe: asserts the loom:injected_interfaces table
 * is actually present on intermediary runtime (mixin bridges), then exercises
 * real API calls and the DamageEventDispatcher mixin. */
public final class IssProdProbe implements ModInitializer {
    public static final Logger LOG = LogUtils.getLogger();
    public static final List<String> FAILURES = new ArrayList<>();

    private static void check(String name, Class<?> target, Class<?> ext) {
        if (!ext.isAssignableFrom(target)) {
            FAILURES.add(name + " does not implement " + ext.getSimpleName());
            LOG.error("[ISS-PROBE] FAIL {} !instanceof {}", name, ext.getSimpleName());
        } else {
            LOG.info("[ISS-PROBE] ok {} -> {}", name, ext.getSimpleName());
        }
    }

    private static void checkAll(String name, Class<?> target, Class<?>... exts) {
        for (Class<?> ext : exts) check(name, target, ext);
    }

    @Override
    public void onInitialize() {
        checkAll("Entity", Entity.class, IAttachmentHolder.class, IEntityExtension.class);
        check("BlockEntity", BlockEntity.class, IBlockEntityExtension.class);
        check("Level", Level.class, IAttachmentHolder.class);
        check("ChunkAccess", ChunkAccess.class, IAttachmentHolder.class);
        check("ItemStack", ItemStack.class, IItemStackExtension.class);
        check("Item", Item.class, IItemExtension.class);
        check("SimpleJsonResourceReloadListener",
                net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener.class,
                IConditionalOpsProvider.class);
        check("BlockBehaviour", BlockBehaviour.class, IBlockExtension.class);
        check("BlockStateBase", BlockBehaviour.BlockStateBase.class, IBlockStateExtension.class);
        check("Fluid", Fluid.class, IFluidExtension.class);
        check("Player", Player.class, IPlayerExtension.class);
        check("Item.Properties", Item.Properties.class, IItemPropertiesExtension.class);
        check("CreativeModeTab.Builder", net.minecraft.world.item.CreativeModeTab.Builder.class,
                ICreativeModeTabBuilderExtension.class);
        check("LightningBolt", LightningBolt.class, ILightningBoltExtension.class);
        check("BlockParticleOption", net.minecraft.core.particles.BlockParticleOption.class,
                IBlockParticleOptionExtension.class);
        check("Ingredient", Ingredient.class, IIngredientExtension.class);
        check("Attribute", Attribute.class, IAttributeExtension.class);
        check("PacketFlow", PacketFlow.class, IPacketFlowExtension.class);
        check("MobSpawnSettings", MobSpawnSettings.class, IMobSpawnSettingsExtension.class);
        check("BiomeGenerationSettings", BiomeGenerationSettings.class, IBiomeGenerationSettingsExtension.class);
        check("MobEffect", net.minecraft.world.effect.MobEffect.class, IMobEffectExtension.class);
        check("Holder", Holder.class, IHolderExtension.class);
        check("LevelAccessor", LevelAccessor.class, ILevelAccessorExtension.class);
        check("ServerLevelAccessor", ServerLevelAccessor.class, ILevelAccessorExtension.class);
        check("TooltipFlag", TooltipFlag.class, ITooltipFlagExtension.class);
        check("LivingEntity", LivingEntity.class, ILivingEntityExtension.class);
        check("MobEffectInstance", MobEffectInstance.class, IMobEffectInstanceExtension.class);
        check("ServerGamePacketListenerImpl", ServerGamePacketListenerImpl.class, ICommonPacketListener.class);
        // citadel video decoder JIJ classes must resolve at runtime
        try {
            Class.forName("org.jcodec.api.FrameGrab");
            Class.forName("net.sourceforge.jaad.aac.Decoder");
            LOG.info("[ISS-PROBE] ok citadel video deps resolvable");
        } catch (Throwable t) {
            FAILURES.add("citadel video deps " + t);
            LOG.error("[ISS-PROBE] FAIL citadel video deps", t);
        }
        if (FAILURES.isEmpty()) {
            LOG.info("[ISS-PROBE] common ABI check done, 0 failures");
        } else {
            LOG.error("[ISS-PROBE] common ABI check done, {} FAILURES: {}", FAILURES.size(), FAILURES);
        }
        ServerLifecycleEvents.SERVER_STARTED.register(IssProdProbe::onServerStarted);
    }

    private static void onServerStarted(MinecraftServer server) {
        server.execute(() -> {
            try {
                runApiAndDamageChecks(server.overworld());
            } catch (Throwable t) {
                FAILURES.add("damage test threw " + t);
                LOG.error("[ISS-PROBE] damage test failed", t);
            }
            report();
        });
    }

    // ---- fixture state ----
    private static final AtomicReference<LivingEntity> expectedVictim = new AtomicReference<>();
    private static final AtomicReference<DamageSource> expectedSource = new AtomicReference<>();
    private static final AtomicReference<LivingDamageEvent.Post> lastPost = new AtomicReference<>();
    private static final AtomicInteger postCount = new AtomicInteger();

    private static Zombie mob(ServerLevel level, double maxHealth) {
        Zombie z = EntityType.ZOMBIE.create(level);
        z.setPos(0, 200, 0);
        z.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        if (z.getAttribute(Attributes.ARMOR) != null) z.getAttribute(Attributes.ARMOR).setBaseValue(0);
        z.setHealth((float) maxHealth);
        return z;
    }

    private static void setBase(LivingEntity e, Holder<Attribute> attr, double v) {
        var inst = e.getAttribute(attr);
        if (inst != null) inst.setBaseValue(v);
    }

    private static void beginScenario(LivingEntity victim, DamageSource source) {
        expectedVictim.set(victim);
        expectedSource.set(source);
        lastPost.set(null);
        postCount.set(0);
        victim.invulnerableTime = 0;
    }

    private static void runApiAndDamageChecks(ServerLevel level) {
        Zombie attacker = mob(level, 20);
        attacker.setHealth(10);
        // zero base crit chance, neutral crit damage so no crit inflates the hit
        setBase(attacker, ALObjects.Attributes.CRIT_CHANCE, 0);
        setBase(attacker, ALObjects.Attributes.CRIT_DAMAGE, 1.0);
        setBase(attacker, ALObjects.Attributes.LIFE_STEAL, 1.0);
        setBase(attacker, ALObjects.Attributes.OVERHEAL, 0);

        // real extension API after mixin
        ItemStack stack = new ItemStack(Items.STONE);
        expect("stone.canElytraFly==false", !stack.canElytraFly(attacker));
        expect("empty.canElytraFly==false", !ItemStack.EMPTY.canElytraFly(attacker));
        expect("Item.canElytraFly baseline==false", !Items.STONE.canElytraFly(stack, attacker));
        Object food = stack.get(() -> DataComponents.FOOD);
        expect("stack.get(() -> FOOD) null", food == null);
        var holder = net.minecraft.core.registries.BuiltInRegistries.ITEM
                .getHolderOrThrow(net.minecraft.core.registries.BuiltInRegistries.ITEM
                        .getResourceKey(Items.STONE).orElseThrow());
        expect("holder.getKey()=stone", holder.getKey().location().toString().equals("minecraft:stone"));

        // registry alias: old familiars id resolves to the new item
        var aliasItem = net.minecraft.core.registries.BuiltInRegistries.ITEM
                .get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "pet_bed"));
        expect("alias alshanex_familiars:pet_bed resolves",
                net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(aliasItem)
                        .equals(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("familiarslib", "pet_bed")));

        Object fixture = new Object() {
            @SubscribeEvent
            public void onPost(LivingDamageEvent.Post e) {
                if (e.getEntity() == expectedVictim.get() && e.getSource() == expectedSource.get()) {
                    lastPost.set(e);
                    postCount.incrementAndGet();
                }
            }
        };
        NeoForge.EVENT_BUS.register(fixture);
        try {
            DamageSource src = level.damageSources().mobAttack(attacker);

            // 1: plain hit, no armor
            Zombie v1 = mob(level, 20);
            beginScenario(v1, src);
            v1.hurt(src, 4);
            expectF("hit4 victim health", v1.getHealth(), 16);
            expectPost(4, 4);
            expectF("life steal +4", attacker.getHealth(), 14);

            // 2: armor 20 -> reduced final, Post == actual loss, heal == final
            Zombie v2 = mob(level, 20);
            v2.getAttribute(Attributes.ARMOR).setBaseValue(20);
            attacker.setHealth(10);
            beginScenario(v2, src);
            float before = v2.getHealth();
            v2.hurt(src, 4);
            float finalDmg = before - v2.getHealth();
            expect("armor reduces final", finalDmg > 0 && finalDmg < 4);
            expectPost(4, finalDmg);
            expectF("life steal == final", attacker.getHealth(), 10 + finalDmg);

            // 3: full absorption -> health unchanged, Post.newDamage 0, no heal
            Zombie v3 = mob(level, 20);
            setBase(v3, Attributes.MAX_ABSORPTION, 20);
            v3.setAbsorptionAmount(4);
            attacker.setHealth(10);
            beginScenario(v3, src);
            v3.hurt(src, 4);
            expectF("absorbed health unchanged", v3.getHealth(), 20);
            expectF("absorbed absorption consumed", v3.getAbsorptionAmount(), 0);
            expectPost(4, 0);
            expectF("absorbed no heal", attacker.getHealth(), 10);

            // 4: Sundering I, no armor -> 4 * 1.2 = 4.8, heal 4.8
            Zombie v4 = mob(level, 20);
            v4.addEffect(new MobEffectInstance(ALObjects.MobEffects.SUNDERING, 200, 0));
            attacker.setHealth(10);
            beginScenario(v4, src);
            v4.hurt(src, 4);
            expectF("sundered victim health", v4.getHealth(), 15.2f);
            expectPost(4, 4.8f);
            expectF("sundered heal", attacker.getHealth(), 14.8f);

            // 5: Player.actuallyHurt path via probe fake player
            ProbeFakePlayer fake = new ProbeFakePlayer(level.getServer(), level);
            fake.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
            fake.setHealth(20);
            fake.getAbilities().instabuild = false;
            fake.getAbilities().invulnerable = false;
            ((issprobe.mixin.ServerPlayerAcc) fake).iss_probe$setSpawnInvulnerableTime(0);
            attacker.setHealth(10);
            beginScenario(fake, src);
            fake.hurt(src, 4);
            expectF("player hit4 health", fake.getHealth(), 16);
            expectPost(4, 4);

            // 6: player absorption path
            setBase(fake, Attributes.MAX_ABSORPTION, 20);
            fake.setAbsorptionAmount(4);
            attacker.setHealth(10);
            beginScenario(fake, src);
            fake.hurt(src, 4);
            expectF("player absorbed health unchanged", fake.getHealth(), 16);
            expectPost(4, 0);

            checkContainers(v1);
            checkContainers(v2);
            checkContainers(v3);
            checkContainers(v4);
            checkContainers(fake);
            checkContainers(attacker);
        } finally {
            NeoForge.EVENT_BUS.unregister(fixture);
        }
    }

    private static void checkContainers(LivingEntity e) {
        expect("damageContainers empty", ((ILivingEntityExtension) e).getDamageContainers().isEmpty());
    }

    public static void expect(String what, boolean ok) {
        if (ok) {
            LOG.info("[ISS-PROBE] ok {}", what);
        } else {
            FAILURES.add(what);
            LOG.error("[ISS-PROBE] FAIL {}", what);
        }
    }

    public static boolean amountsEqual(float actual, float expected) {
        return Math.abs(actual - expected) < 0.01f;
    }

    public static void expectF(String what, float actual, float expected) {
        if (amountsEqual(actual, expected)) {
            LOG.info("[ISS-PROBE] ok {} = {}", what, actual);
        } else {
            FAILURES.add(what + " expected " + expected + " got " + actual);
            LOG.error("[ISS-PROBE] FAIL {} expected {} got {}", what, expected, actual);
        }
    }

    /** Asserts the fixture captured exactly one Post for the expected
     * victim+source with the expected recorded amounts. */
    public static void expectPost(float orig, float newDmg) {
        LivingDamageEvent.Post p = lastPost.get();
        if (postCount.get() != 1) {
            FAILURES.add("Post count " + postCount.get() + " (expected 1)");
            LOG.error("[ISS-PROBE] FAIL Post count {} != 1", postCount.get());
            return;
        }
        if (p == null) {
            FAILURES.add("no Post captured");
            LOG.error("[ISS-PROBE] FAIL no Post event");
            return;
        }
        if (p.getEntity() != expectedVictim.get()) {
            FAILURES.add("Post entity mismatch");
            LOG.error("[ISS-PROBE] FAIL Post entity {} != {}", p.getEntity(), expectedVictim.get());
            return;
        }
        if (p.getSource() != expectedSource.get()) {
            FAILURES.add("Post source mismatch");
            LOG.error("[ISS-PROBE] FAIL Post source mismatch");
            return;
        }
        expectF("Post.originalDamage", p.getOriginalDamage(), orig);
        expectF("Post.newDamage", p.getNewDamage(), newDmg);
    }

    private static void report() {
        if (FAILURES.isEmpty()) {
            LOG.info("[ISS-PROBE] ALL CHECKS PASSED");
        } else {
            LOG.error("[ISS-PROBE] {} FAILURES: {}", FAILURES.size(), FAILURES);
        }
    }
}
