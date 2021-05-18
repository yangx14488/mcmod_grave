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
    comBuilder.comment( "�������ã�����ʱ���Է���������Ϊ����" ).push( "nanzhi_grave" );
    can_work = comBuilder.comment( "����ģ�飿������Ϊ����ôģ�鲻�ᴦ����ҵ������¼���" )
        .define("grave_can_work", true ) ;
    in_void = comBuilder.comment( "������������ʱ���Ƿ�����Ĺ�����ǣ�������Y0�ĵط����д������񣺲�����" )
        .define("grave_create_void", true ) ;
    in_lava = comBuilder.comment( "��������ҽ���ʱ���Ƿ�����Ĺ�����ǣ��Ƴ��ҽ�������Ĺ�����񣺲�����" )
        .define("grave_create_lava", true ) ;
    in_sky = comBuilder.comment( "��������ڿ���ʱ���Ƿ���ͼ�����������巽���Թ���Ĺ����" )
        .define("search_the_ground", true ) ;
    in_sky_y = comBuilder.comment( "����Ҫ������������ô����������������ٸ�߶�?" )
        .defineInRange("search_the_ground_y", 64, 1, 255 ) ;
    death_by_asphyxia = comBuilder.comment( "������ڷ�����Ϣʱ���Ƿ���ͼ�����������õķ����Թ���Ĺ����" )
        .define("death_by_asphyxia", true ) ;
    loop_radius = comBuilder.comment( "���ÿռ������뾶��Ĺ������ʱ���ҵĿռ䷶Χ��" )
        .defineInRange("grave_create_radius", 1, 0, 2);
    free_nameless = comBuilder.comment( "���������ɷ�Ĺ���ǣ�����������������Ĺ������Ҫ�����������ӻ�Ͱ��" )
        .define("grave_create_free_nameless", false ) ;
    free_named = comBuilder.comment( "������[grave_create_free_nameless]ѡ��������ø�ѡ���ô�������ʱ�ܻ�����һ��������Ĺ��" )
        .define("grave_create_free_named", false ) ;
    spawn_zombie = comBuilder.comment( "��Ĺ���ƻ�Ĺ��ʱ�Ƿ������С��ʬ��" )
        .define("grave_break_spawn_zombie", true ) ;
    break_useShove = comBuilder.comment( "����Ĺ���ֲֳ����Ҽ����ƻ�Ĺ�أ�" )
        .define("grave_break_shovel", true ) ;
    drop_chance = comBuilder.comment( "�ƻ�����֮Ĺ��Ĺ��ʱ������Ĺ���ĸ��ʡ�0�������䡣1����Զ���䡣������0~1֮���С��ֵ��" )
        .defineInRange("grave_break_drop", 0d, 0d, 1d);
    death_msg = comBuilder.comment( "�������ʱ���Ƿ��֪��Ĺ�����ɹ�������Ϣ�����鿪����" )
        .define("death_msg", true ) ;
    death_msg_pos = comBuilder.comment( "��֪��Ĺ��������Ϣʱ���Ƿ񸽴�����㣿" )
        .define("death_msg_pos", false ) ;
    death_msg_not_found_container = comBuilder.comment( "�ڿ�����Ϣ��֪ʱ������ұ�����û���������Ƿ�һ��֪ͨ��ң�" )
        .define("death_msg_not_found_container", false ) ;
    isnot_valid_position = comBuilder.comment( "����֮Ĺ�ײ��ķ��鱻�ƻ����·�Ĺ������Чλ��ʱ���Ƿ������𻵣��������˽�ʬ���ɣ���ô��ͬʱ����С��ʬ��" )
        .define("isnot_valid_position", true ) ;
    cause_of_death = comBuilder.comment( "���û���Զ��屮�ģ���ô��ʾ��������ʾ�����ļ����Ԥ�����ݣ���ע�⣬�ڷ������ϣ����������Ӣ�ġ�" )
        .define("cause_of_death", false ) ;
    comBuilder.pop( ) ;
    builder = comBuilder.build();
  }
}