package net.alshanex.familiarslib.setup;

import net.alshanex.familiarslib.network.FamiliarDataPacket;
import net.alshanex.familiarslib.network.FamiliarDeathPacket;
import net.alshanex.familiarslib.network.MoveFamiliarPacket;
import net.alshanex.familiarslib.network.OpenFamiliarSelectionPacket;
import net.alshanex.familiarslib.network.OpenFamiliarStoragePacket;
import net.alshanex.familiarslib.network.OpenMultiSelectionScreenPacket;
import net.alshanex.familiarslib.network.ReleaseFamiliarPacket;
import net.alshanex.familiarslib.network.ReloadFamiliarScreenPacket;
import net.alshanex.familiarslib.network.RequestFamiliarSelectionPacket;
import net.alshanex.familiarslib.network.SelectFamiliarPacket;
import net.alshanex.familiarslib.network.SetStorageModePacket;
import net.alshanex.familiarslib.network.SummonPetPackage;
import net.alshanex.familiarslib.network.SyncFamiliarDataPacket;
import net.alshanex.familiarslib.network.UpdateFamiliarStoragePacket;
import net.alshanex.familiarslib.network.UpdateMultiSelectionCurioPacket;
import net.alshanex.familiarslib.network.UpdateStorageSettingsPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(
   bus = Bus.MOD,
   modid = "familiarslib"
)
public class PayloadHandler {
   @SubscribeEvent
   public static void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar payloadRegistrar = event.registrar("familiarslib").versioned("1.0.0").optional();
      payloadRegistrar.playToServer(SummonPetPackage.TYPE, SummonPetPackage.STREAM_CODEC, SummonPetPackage::handle);
      payloadRegistrar.playToServer(RequestFamiliarSelectionPacket.TYPE, RequestFamiliarSelectionPacket.STREAM_CODEC, RequestFamiliarSelectionPacket::handle);
      payloadRegistrar.playToServer(SelectFamiliarPacket.TYPE, SelectFamiliarPacket.STREAM_CODEC, SelectFamiliarPacket::handle);
      payloadRegistrar.playToServer(UpdateMultiSelectionCurioPacket.TYPE, UpdateMultiSelectionCurioPacket.STREAM_CODEC, UpdateMultiSelectionCurioPacket::handle);
      payloadRegistrar.playToServer(ReleaseFamiliarPacket.TYPE, ReleaseFamiliarPacket.STREAM_CODEC, ReleaseFamiliarPacket::handle);
      payloadRegistrar.playToServer(MoveFamiliarPacket.TYPE, MoveFamiliarPacket.STREAM_CODEC, MoveFamiliarPacket::handle);
      payloadRegistrar.playToServer(SetStorageModePacket.TYPE, SetStorageModePacket.STREAM_CODEC, SetStorageModePacket::handle);
      payloadRegistrar.playToServer(UpdateStorageSettingsPacket.TYPE, UpdateStorageSettingsPacket.STREAM_CODEC, UpdateStorageSettingsPacket::handle);
      payloadRegistrar.playToClient(FamiliarDataPacket.TYPE, FamiliarDataPacket.STREAM_CODEC, FamiliarDataPacket::handle);
      payloadRegistrar.playToClient(SyncFamiliarDataPacket.TYPE, SyncFamiliarDataPacket.STREAM_CODEC, SyncFamiliarDataPacket::handle);
      payloadRegistrar.playToClient(UpdateFamiliarStoragePacket.TYPE, UpdateFamiliarStoragePacket.STREAM_CODEC, UpdateFamiliarStoragePacket::handle);
      payloadRegistrar.playToClient(OpenFamiliarStoragePacket.TYPE, OpenFamiliarStoragePacket.STREAM_CODEC, OpenFamiliarStoragePacket::handle);
      payloadRegistrar.playToClient(OpenFamiliarSelectionPacket.TYPE, OpenFamiliarSelectionPacket.STREAM_CODEC, OpenFamiliarSelectionPacket::handle);
      payloadRegistrar.playToClient(FamiliarDeathPacket.TYPE, FamiliarDeathPacket.STREAM_CODEC, FamiliarDeathPacket::handle);
      payloadRegistrar.playToClient(OpenMultiSelectionScreenPacket.TYPE, OpenMultiSelectionScreenPacket.STREAM_CODEC, OpenMultiSelectionScreenPacket::handle);
      payloadRegistrar.playToClient(ReloadFamiliarScreenPacket.TYPE, ReloadFamiliarScreenPacket.STREAM_CODEC, ReloadFamiliarScreenPacket::handle);
   }
}
