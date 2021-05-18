package net.atcat.nanzhi.grave;

import net.minecraftforge.common.ForgeConfigSpec;

public class config {
  public static ForgeConfigSpec builder;
  public static ForgeConfigSpec.IntValue loop_radius; // v
  public static ForgeConfigSpec.IntValue in_sky_y; // v
  public static ForgeConfigSpec.DoubleValue drop_chance; // v
  public static ForgeConfigSpec.BooleanValue spawn_zombie; // v
  public static ForgeConfigSpec.BooleanValue can_work; // v
  public static ForgeConfigSpec.BooleanValue in_void ; // v
  public static ForgeConfigSpec.BooleanValue in_lava ; // v
  public static ForgeConfigSpec.BooleanValue in_sky ; // v
  public static ForgeConfigSpec.BooleanValue death_by_asphyxia ; // v
  public static ForgeConfigSpec.BooleanValue break_useShove; // v
  public static ForgeConfigSpec.BooleanValue free_nameless ; // test
  public static ForgeConfigSpec.BooleanValue free_named ; // test
  public static ForgeConfigSpec.BooleanValue death_msg ;
  public static ForgeConfigSpec.BooleanValue death_msg_pos ;
  public static ForgeConfigSpec.BooleanValue death_msg_not_found_container ;
  public static ForgeConfigSpec.BooleanValue cause_of_death ;
  public static ForgeConfigSpec.BooleanValue isnot_valid_position ; // v
  
  static {
    ForgeConfigSpec.Builder comBuilder = new ForgeConfigSpec.Builder();
    comBuilder.comment( "基础设置：联机时，以服务器配置为主。" ).push( "nanzhi_grave" );
    can_work = comBuilder.comment( "激活模组？若设置为否，那么模组不会处理玩家的死亡事件。" )
        .define("grave_can_work", true ) ;
    in_void = comBuilder.comment( "玩家死在虚空中时，是否生成墓碑？是：尝试在Y0的地方进行创建。否：不处理。" )
        .define("grave_create_void", true ) ;
    in_lava = comBuilder.comment( "玩家死在岩浆中时，是否生成墓碑？是：移除岩浆并生成墓碑，否：不处理。" )
        .define("grave_create_lava", true ) ;
    in_sky = comBuilder.comment( "玩家死于在空中时，是否试图向下搜索固体方块以构建墓碑？" )
        .define("search_the_ground", true ) ;
    in_sky_y = comBuilder.comment( "若需要向下搜索，那么最低允许搜索到多少格高度?" )
        .defineInRange("search_the_ground_y", 64, 1, 255 ) ;
    death_by_asphyxia = comBuilder.comment( "玩家死于方块窒息时，是否试图向上搜索可用的方块以构建墓碑？" )
        .define("death_by_asphyxia", true ) ;
    loop_radius = comBuilder.comment( "可用空间搜索半径：墓地生成时查找的空间范围。" )
        .defineInRange("grave_create_radius", 1, 0, 2);
    free_nameless = comBuilder.comment( "无消耗生成坟墓？是：无消耗生成无主坟墓。否：需要背包内有箱子或桶。" )
        .define("grave_create_free_nameless", false ) ;
    free_named = comBuilder.comment( "若启用[grave_create_free_nameless]选项后再启用该选项，那么玩家死亡时总会生成一个有主坟墓。" )
        .define("grave_create_free_named", false ) ;
    spawn_zombie = comBuilder.comment( "非墓主破坏墓地时是否会生成小僵尸？" )
        .define("grave_break_spawn_zombie", true ) ;
    break_useShove = comBuilder.comment( "允许墓主手持铲子右键以破坏墓地？" )
        .define("grave_break_shovel", true ) ;
    drop_chance = comBuilder.comment( "破坏有主之墓的墓地时，掉落墓碑的概率。0：不掉落。1：永远掉落。可设置0~1之间的小数值。" )
        .defineInRange("grave_break_drop", 0d, 0d, 1d);
    death_msg = comBuilder.comment( "玩家死亡时，是否告知坟墓创建成功与否的消息？建议开启。" )
        .define("death_msg", true ) ;
    death_msg_pos = comBuilder.comment( "告知坟墓创建的消息时，是否附带坐标点？" )
        .define("death_msg_pos", false ) ;
    death_msg_not_found_container = comBuilder.comment( "在开启消息告知时，若玩家背包内没有容器，是否一并通知玩家？" )
        .define("death_msg_not_found_container", false ) ;
    isnot_valid_position = comBuilder.comment( "有主之墓底部的方块被破坏导致坟墓不是有效位置时，是否自行损坏？若启用了僵尸生成，那么会同时生成小僵尸。" )
        .define("isnot_valid_position", true ) ;
    cause_of_death = comBuilder.comment( "如果没有自定义碑文，那么显示死因还是显示语言文件里的预设内容？请注意，在服务器上，死因可能是英文。" )
        .define("cause_of_death", false ) ;
    comBuilder.pop( ) ;
    builder = comBuilder.build();
  }
}