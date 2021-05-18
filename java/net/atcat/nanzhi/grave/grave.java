package net.atcat.nanzhi.grave;

import net.atcat.nanzhi.grave.registry.*;
import net.minecraftforge.eventbus.api.IEventBus;
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
  
    ModLoadingContext.get().registerConfig( ModConfig.Type.COMMON, config.builder );
    
    blockReg.registry( bus ) ;
    itemReg.registry( bus ) ;
    tileEntityReg.registry( bus ) ;
    
  } ;
  
  /*
  * 玩家可以制作墓碑，以及通过铁砧为墓碑写一些什么话上去
  *
  * 死亡时：
  * 若存在石碑，则生成有主之墓
  * 若不存在石碑但存在箱子或桶，则生成无主之墓
  *
  * 掉落：
  * 无主之墓：掉落容器内物品，自身不掉落
  * 有主之墓：掉落容器内物品，50%概率掉落石碑
  * 人造坟墓：掉落容器内物品，掉落石碑
  *
  * 放置：
  * 使用石碑右键地面可放置一个人造坟墓，地面必须为泥土、草方块等泥土类的方块，破坏底部方块时墓碑会随之一同破坏
  * 人造坟墓不区分墓主
  *
  * 开采：
  * 无主之墓：较容易开采，抗爆低，右键可开启，不支持漏斗，开采会被雷劈。
  * 有主之墓：较难开采，抗爆高，仅墓主右键可开启，不支持漏斗，非墓主破坏时会被雷劈。墓主手持铲子在坟墓上蹲一下也可破坏，但铲子损耗双倍耐久，防死在别人领地。
  * 人造坟墓：较容易开采，抗爆低，右键可开启，不支持漏斗，不会有任何事
  *
  * player_name : 放置者或死者的名字
  * player_uuid : 所属的uuid，若没有这个值，则视为无主之墓，若uuid为00000000-0000-0000-0000-000000000000，则视为人造坟墓
  * display_msg : 显示的字符，不超过24个字，12个字时自动断句
  *
  * */
  
}
