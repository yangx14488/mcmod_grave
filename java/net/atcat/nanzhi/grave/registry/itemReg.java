package net.atcat.nanzhi.grave.registry;

import net.atcat.nanzhi.grave.com.item.* ;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;

public class itemReg {
  

  public static final Item idTag = registry.register( "id_tag", new idTag( new Item.Properties( )
      .maxStackSize( 16 )
      .group( ItemGroup.TOOLS )
  ) ) ;
  
  
  public static void registry ( IEventBus bus ) {
    registry.itemRegDef.register( bus ) ;
  } ;
  
  
}
