package net.atcat.nanzhi.grave.registry;

import net.atcat.nanzhi.grave.com.block.graveBlock;
import net.atcat.nanzhi.grave.com.item.grave;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;

public class blockReg {
  
  private static Item.Properties $g ( ) { return new Item.Properties( ) ; }
  
  public static final Block graveBlock = registry.register( "grave", new graveBlock( ), ( Block block ) -> new grave( block, new Item.Properties( )
      .maxStackSize( 1 )
      .group( ItemGroup.DECORATIONS )
  ) ) ;
  
  public static void registry ( IEventBus bus ) {
    registry.blockRegDef.register( bus ) ;
  } ;

}
