package net.alshanex.familiarslib.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SelectedFamiliarsComponent(Set<UUID> selectedFamiliars) {
   public static final Codec<SelectedFamiliarsComponent> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.list(Codec.STRING.xmap(UUID::fromString, UUID::toString))
               .xmap(HashSet::new, list -> list.stream().toList())
               .fieldOf("selected_familiars")
               .forGetter(component -> new HashSet<>(component.selectedFamiliars()))
         )
         .apply(instance, SelectedFamiliarsComponent::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, SelectedFamiliarsComponent> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.STRING_UTF8.map(UUID::fromString, UUID::toString)).map(set -> set, set -> new HashSet(set)),
      SelectedFamiliarsComponent::selectedFamiliars,
      SelectedFamiliarsComponent::new
   );

   public SelectedFamiliarsComponent() {
      this(new HashSet<>());
   }

   public Set<UUID> getSelectedFamiliars() {
      return new HashSet<>(this.selectedFamiliars);
   }

   public SelectedFamiliarsComponent withFamiliar(UUID familiarId) {
      Set<UUID> newSet = new HashSet<>(this.selectedFamiliars);
      newSet.add(familiarId);
      return new SelectedFamiliarsComponent(newSet);
   }

   public SelectedFamiliarsComponent withoutFamiliar(UUID familiarId) {
      Set<UUID> newSet = new HashSet<>(this.selectedFamiliars);
      newSet.remove(familiarId);
      return new SelectedFamiliarsComponent(newSet);
   }

   public boolean contains(UUID familiarId) {
      return this.selectedFamiliars.contains(familiarId);
   }

   public int size() {
      return this.selectedFamiliars.size();
   }

   public boolean canAddMore() {
      return this.selectedFamiliars.size() < 10;
   }
}
