package net.atcat.nanzhi.grave;

import net.atcat.nanzhi.grave.gui.configScreen;
import net.atcat.nanzhi.grave.obj.configFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class config {
  
  public static configFactory factory = new configFactory( "联机时，以服务器设置为准", grave.modID ) ;
  public static configFactory.setter_bool can_work = factory.add(
      "can_work",
      "让模组处理玩家死亡" ,
      true
  ) ;
  public static configFactory.setter_bool in_void = factory.add(
      "in_void",
      "玩家死在虚空中时，要试图生成坟墓并从Y0开始向上搜索吗" ,
      true
  ) ;
  public static configFactory.setter_bool in_lava = factory.add(
      "in_lava",
      "玩家死在岩浆中时，要移除岩浆并试图生成坟墓吗" ,
      true
  ) ;
  public static configFactory.setter_bool in_sky = factory.add(
      "in_sky",
      "玩家死在天上时，要向下搜索地面吗" ,
      true
  ) ;
  public static configFactory.setter_int in_sky_y = factory.add(
      "in_sky_y",
      "如果要向下搜索，那么最低搜索到多低？" ,
      64, 1, 128
  ) ;
  public static configFactory.setter_bool death_by_asphyxia = factory.add(
      "death_by_asphyxia",
      "玩家死于方块窒息时要向上查找可用空间生成坟墓吗" ,
      true
  ) ;
  public static configFactory.setter_int loop_radius = factory.add(
      "loop_radius",
      "生成墓碑时的搜索半径" ,
      1, 0, 2
  ) ;
  public static configFactory.setter_bool free_nameless = factory.add(
      "free_nameless",
      "无消耗生成无主坟墓吗？设置为否时需要背包内有箱子或木桶" ,
      false
  ) ;
  public static configFactory.setter_bool free_named = factory.add(
      "free_named",
      "开启无消耗生成坟墓时再启用该选项，会无消耗生成有主坟墓" ,
      false
  ) ;
  public static configFactory.setter_bool spawn_zombie = factory.add(
      "spawn_zombie",
      "非墓主破坏非人造坟墓时生成小僵尸吗",
      true
  ) ;
  public static configFactory.setter_bool break_useShove = factory.add(
      "break_use_shove",
      "允许墓主手持铲子右键坟墓以快速破坏吗",
      true
  ) ;
  public static configFactory.setter_percentage drop_chance = factory.add(
      "drop_chance",
      "材质为石头的坟墓被破坏时掉落墓碑的概率",
      0
  ) ;
  public static configFactory.setter_bool death_msg = factory.add(
      "death_msg",
      "启用消息提醒：坟墓创建时告知玩家创建信息",
      true
  ) ;
  public static configFactory.setter_bool death_msg_pos = factory.add(
      "death_msg_pos",
      "若启用了消息提醒，要同时发送坐标吗",
      false
  ) ;
  public static configFactory.setter_bool death_msg_nfc = factory.add(
      "death_msg_nfc",
      "若启用了消息提醒，找不到容器创建坟墓时要提醒玩家吗",
      true
  ) ;
  public static configFactory.setter_bool isnot_valid_position = factory.add(
      "not_valid_position",
      "坟墓找不到依赖方块时要自行破坏吗？若启用了<spawn_zombie>选项，还会生成一只小僵尸",
      true
  ) ;
  public static configFactory.setter_bool cause_of_death = factory.add(
      "cause_of_death",
      "碑文要显示死因吗？若是服务器，可能会显示为英文。",
      false
  ) ;
  public static configFactory.setter_bool notify_owner = factory.add(
      "notify_owner",
      "有主之墓碑破坏时，要通知墓主吗？",
      true
  ) ;
  
  static {
    factory.build( ) ;
  }
  
  @OnlyIn( Dist.CLIENT )
  public static configScreen guiFacotory ( Minecraft mc, Screen p ) {
    return new configScreen( mc, p, config.factory, grave.modID ) ;
  } ;
  
}
