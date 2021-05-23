package net.atcat.nanzhi.grave;

import net.atcat.nanzhi.grave.gui.configScreen;
import net.atcat.nanzhi.grave.obj.configFactory;
import net.atcat.nanzhi.grave.registry.blockReg;
import net.atcat.nanzhi.grave.registry.itemReg;
import net.atcat.nanzhi.grave.registry.tileEntityReg;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod( grave.modID )
@Mod.EventBusSubscriber( modid = grave.modID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class grave {
  
  public static final String modID = "nanzhi_grave" ;
  
  public grave ( ) {

    final IEventBus bus = FMLJavaModLoadingContext.get( ).getModEventBus( ) ;
  
    ModLoadingContext.get( ).registerConfig( ModConfig.Type.COMMON, config.factory.builder ) ;
    
    ModLoadingContext.get( ).registerExtensionPoint(
          ExtensionPoint.CONFIGGUIFACTORY,
          ( ) -> config::guiFacotory
      ) ;
    
    blockReg.registry( bus ) ;
    itemReg.registry( bus ) ;
    tileEntityReg.registry( bus ) ;
    
    // 改天改改成就：我们终将失去 - 我们终会面对这个问题，就从现在开始珍惜生活吧~
    // - 解锁：墓碑，身份牌
    // - 获取：破坏一个坟墓

  } ;
  
}
