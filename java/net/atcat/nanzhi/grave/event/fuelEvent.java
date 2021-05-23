package net.atcat.nanzhi.grave.event;

import com.google.common.collect.Maps;
import net.minecraft.item.Item;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber( )
public class fuelEvent {
  
  private static final Map< Item, Integer> map = Maps.newLinkedHashMap( );
  
  @SubscribeEvent // 画不出
  public static void onFuelTime ( FurnaceFuelBurnTimeEvent event ) {
    Integer i = map.get( event.getItemStack( ).getItem( ) ) ;
    if ( i != null ) {
      event.setBurnTime( i ) ;
      event.setResult( Event.Result.ALLOW ) ;
    } ;
  } ;
  
  // 人生初见真颜色
  public static void add ( Item item, int burnTime ) {
    map.put( item, burnTime ) ;
  } ;
  
  
}
