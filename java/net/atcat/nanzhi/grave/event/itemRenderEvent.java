package net.atcat.nanzhi.grave.event;

import net.atcat.nanzhi.grave.grave;
import net.atcat.nanzhi.grave.registry.itemReg;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT )
public class itemRenderEvent {
  @SubscribeEvent
  public static void propertyOverrideRegistry( FMLClientSetupEvent event) {
    event.enqueueWork( () ->  ItemModelsProperties.registerProperty(
          itemReg.idTag, new ResourceLocation( grave.modID, "named"),
          ( itemStack, clientWorld, livingEntity) -> itemStack.getOrCreateTag().getBoolean( "named" ) ? 1 : 0
    )) ;
  }
}