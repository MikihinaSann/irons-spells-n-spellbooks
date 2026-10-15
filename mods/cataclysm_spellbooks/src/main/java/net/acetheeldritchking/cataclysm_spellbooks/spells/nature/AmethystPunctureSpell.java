package net.acetheeldritchking.cataclysm_spellbooks.spells.nature;

import com.github.L_Ender.cataclysm.entity.projectile.Amethyst_Cluster_Projectile_Entity;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.cataclysm.init.ModItems;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import java.util.List;
import net.acetheeldritchking.cataclysm_spellbooks.registries.ItemRegistries;
import net.acetheeldritchking.cataclysm_spellbooks.util.CSUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AmethystPunctureSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "amethyst_puncture");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(5.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable(
            "ui.cataclysm_spellbooks.amethyst_speed",
            new Object[]{Utils.stringTruncation(this.getAmethystClusterSpeed(spellLevel, this.getSpellPower(spellLevel, caster)), 1)}
         ),
         Component.translatable(
            "ui.irons_spellbooks.damage",
            new Object[]{Utils.stringTruncation(this.getDamageForProjectileSpeed(spellLevel, 1.0F, this.getSpellPower(spellLevel, caster)) / 5.0F, 1)}
         )
      );
   }

   public AmethystPunctureSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 15;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      if (entity.getItemBySlot(EquipmentSlot.CHEST).is((Item)ModItems.BLOOM_STONE_PAULDRONS.get())
         && entity.getItemBySlot(EquipmentSlot.MAINHAND).is((Item)ItemRegistries.BLOOM_STONE_STAFF.get())) {
         return 1 + spellLevel;
      } else {
         return !entity.getItemBySlot(EquipmentSlot.CHEST).is((Item)ModItems.BLOOM_STONE_PAULDRONS.get())
               && !entity.getItemBySlot(EquipmentSlot.MAINHAND).is((Item)ItemRegistries.BLOOM_STONE_STAFF.get())
            ? 2
            : spellLevel;
      }
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         playerMagicData.getPlayerRecasts()
            .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), 100, castSource, null), playerMagicData);
      }

      this.shootAmethystCluster(entity, spellLevel, level);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void shootAmethystCluster(LivingEntity caster, int spellLevel, Level level) {
      double casterX = caster.getX();
      double casterY = CSUtils.getEyeHeight(caster);
      double casterZ = caster.getZ();
      float speed = 0.2F;
      Amethyst_Cluster_Projectile_Entity amethyst = new Amethyst_Cluster_Projectile_Entity(
         (EntityType)ModEntities.AMETHYST_CLUSTER_PROJECTILE.get(),
         level,
         caster,
         this.getDamageForProjectileSpeed(spellLevel, speed, this.getSpellPower(spellLevel, caster))
      );
      amethyst.moveTo(casterX, casterY, casterZ, 0.0F, caster.getXRot());
      float speedSpellPower = this.getAmethystClusterSpeed(speed, this.getSpellPower(spellLevel, caster));
      amethyst.setNoGravity(true);
      amethyst.shootFromRotation(caster, caster.getXRot(), caster.getYHeadRot(), 0.0F, speedSpellPower, 1.0F);
      level.addFreshEntity(amethyst);
   }

   private float getAmethystClusterSpeed(float speed, float spellPower) {
      return speed * spellPower;
   }

   private float getDamageForProjectileSpeed(int spellLevel, float speed, float spellPower) {
      float totalSpeed = this.getAmethystClusterSpeed(speed, spellPower);
      return spellLevel * totalSpeed;
   }
}
