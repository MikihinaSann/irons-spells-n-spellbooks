package en.predator.horrorelementmod;

import en.predator.horrorelementmod.init.HorrorElementModModBlockEntities;
import en.predator.horrorelementmod.init.HorrorElementModModBlocks;
import en.predator.horrorelementmod.init.HorrorElementModModItems;
import en.predator.horrorelementmod.init.HorrorElementModModSounds;
import en.predator.horrorelementmod.init.HorrorElementModModTabs;
import en.predator.horrorelementmod.network.HorrorElementModModVariables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.util.Tuple;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.util.thread.SidedThreadGroups;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("horror_element_mod")
public class HorrorElementModMod {
   public static final Logger LOGGER = LogManager.getLogger(HorrorElementModMod.class);
   public static final String MODID = "horror_element_mod";
   private static boolean networkingRegistered = false;
   private static final Map<Type<?>, HorrorElementModMod.NetworkMessage<?>> MESSAGES = new HashMap<>();
   private static final Collection<Tuple<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

   public HorrorElementModMod(IEventBus modEventBus) {
      NeoForge.EVENT_BUS.register(this);
      modEventBus.addListener(this::registerNetworking);
      HorrorElementModModSounds.REGISTRY.register(modEventBus);
      HorrorElementModModBlocks.REGISTRY.register(modEventBus);
      HorrorElementModModBlockEntities.REGISTRY.register(modEventBus);
      HorrorElementModModItems.REGISTRY.register(modEventBus);
      HorrorElementModModTabs.REGISTRY.register(modEventBus);
      HorrorElementModModVariables.ATTACHMENT_TYPES.register(modEventBus);
   }

   public static <T extends CustomPacketPayload> void addNetworkMessage(
      Type<T> id, StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler
   ) {
      if (networkingRegistered) {
         throw new IllegalStateException("Cannot register new network messages after networking has been registered");
      } else {
         MESSAGES.put(id, new HorrorElementModMod.NetworkMessage(reader, handler));
      }
   }

   private void registerNetworking(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("horror_element_mod");
      MESSAGES.forEach((id, networkMessage) -> registerOne(registrar, id, networkMessage));
      networkingRegistered = true;
   }

   public static void queueServerWork(int tick, Runnable action) {
      if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
         workQueue.add(new Tuple(action, tick));
      }
   }

   @SubscribeEvent
   public void tick(Post event) {
      List<Tuple<Runnable, Integer>> actions = new ArrayList<>();
      workQueue.forEach(work -> {
         work.setB((Integer)work.getB() - 1);
         if ((Integer)work.getB() == 0) {
            actions.add((Tuple<Runnable, Integer>)work);
         }
      });
      actions.forEach(e -> ((Runnable)e.getA()).run());
      workQueue.removeAll(actions);
   }

   @SuppressWarnings("unchecked")
   private static <T extends CustomPacketPayload> void registerOne(PayloadRegistrar registrar, CustomPacketPayload.Type<T> id, NetworkMessage<?> m) {
      registrar.playBidirectional(id, (StreamCodec<? super RegistryFriendlyByteBuf, T>)(StreamCodec) m.reader(), (IPayloadHandler<T>) m.handler());
   }

   private record NetworkMessage<T extends CustomPacketPayload>(StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
   }
}
