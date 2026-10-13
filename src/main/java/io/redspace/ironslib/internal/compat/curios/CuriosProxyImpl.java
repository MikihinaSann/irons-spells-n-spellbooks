package io.redspace.ironslib.internal.compat.curios;

import io.redspace.ironslib.attribute.AttributeEventsHandler;
import io.redspace.ironslib.attribute.AttributeInstance;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.NeoForge;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

public class CuriosProxyImpl implements CuriosProxy {
   public CuriosProxyImpl() {
      NeoForge.EVENT_BUS.addListener(CurioAttributeModifierEvent.class, CuriosProxyImpl::handleCuriosModifiers);
   }

   public static void handleCuriosModifiers(CurioAttributeModifierEvent event) {
      for (Entry<Holder<Attribute>, AttributeModifier> entry : event.getModifiers().entries()) {
         AttributeEventsHandler.handleAttributeEvent(AttributeInstance.of(entry.getKey(), entry.getValue()), event::removeModifier, event::addModifier);
      }
   }
}
