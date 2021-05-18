package net.atcat.nanzhi.grave.registry;

import net.atcat.nanzhi.grave.com.blockEntity.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.IEventBus;

public class tileEntityReg {
  
  public static final TileEntityType<graveEntity> graveTileEntity = registry.register( "grave", ( ) -> new graveEntity( new TranslationTextComponent( "block.nanzhi_grave.grave" ) ), blockReg.graveBlock ) ;
  
  public static void registry ( IEventBus bus ) {
    registry.tileEntityRegDef.register( bus ) ;
  } ;

  
}
