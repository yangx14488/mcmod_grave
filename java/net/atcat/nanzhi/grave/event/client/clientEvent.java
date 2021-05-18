package net.atcat.nanzhi.grave.event.client;

import net.atcat.nanzhi.grave.com.blockEntity.render.* ;

import net.atcat.nanzhi.grave.registry.tileEntityReg;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT )
public class clientEvent {
  @SubscribeEvent
  public static void onClientSetup ( FMLClientSetupEvent event ) {
    event.enqueueWork( ( ) -> {
      ClientRegistry.bindTileEntityRenderer( tileEntityReg.graveTileEntity, graveTER::new ) ;
    } ) ;
  } ;
}
