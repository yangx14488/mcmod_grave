package net.atcat.nanzhi.grave;

import net.atcat.nanzhi.grave.gui.configScreen;
import net.atcat.nanzhi.grave.obj.configFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class config {
  
  public static configFactory factory = new configFactory( "����ʱ���Է���������Ϊ׼", grave.modID ) ;
  public static configFactory.setter_bool can_work = factory.add(
      "can_work",
      "��ģ�鴦���������" ,
      true
  ) ;
  public static configFactory.setter_bool in_void = factory.add(
      "in_void",
      "������������ʱ��Ҫ��ͼ���ɷ�Ĺ����Y0��ʼ����������" ,
      true
  ) ;
  public static configFactory.setter_bool in_lava = factory.add(
      "in_lava",
      "��������ҽ���ʱ��Ҫ�Ƴ��ҽ�����ͼ���ɷ�Ĺ��" ,
      true
  ) ;
  public static configFactory.setter_bool in_sky = factory.add(
      "in_sky",
      "�����������ʱ��Ҫ��������������" ,
      true
  ) ;
  public static configFactory.setter_int in_sky_y = factory.add(
      "in_sky_y",
      "���Ҫ������������ô�����������ͣ�" ,
      64, 1, 128
  ) ;
  public static configFactory.setter_bool death_by_asphyxia = factory.add(
      "death_by_asphyxia",
      "������ڷ�����ϢʱҪ���ϲ��ҿ��ÿռ����ɷ�Ĺ��" ,
      true
  ) ;
  public static configFactory.setter_int loop_radius = factory.add(
      "loop_radius",
      "����Ĺ��ʱ�������뾶" ,
      1, 0, 2
  ) ;
  public static configFactory.setter_bool free_nameless = factory.add(
      "free_nameless",
      "����������������Ĺ������Ϊ��ʱ��Ҫ�����������ӻ�ľͰ" ,
      false
  ) ;
  public static configFactory.setter_bool free_named = factory.add(
      "free_named",
      "�������������ɷ�Ĺʱ�����ø�ѡ�������������������Ĺ" ,
      false
  ) ;
  public static configFactory.setter_bool spawn_zombie = factory.add(
      "spawn_zombie",
      "��Ĺ���ƻ��������Ĺʱ����С��ʬ��",
      true
  ) ;
  public static configFactory.setter_bool break_useShove = factory.add(
      "break_use_shove",
      "����Ĺ���ֲֳ����Ҽ���Ĺ�Կ����ƻ���",
      true
  ) ;
  public static configFactory.setter_percentage drop_chance = factory.add(
      "drop_chance",
      "����Ϊʯͷ�ķ�Ĺ���ƻ�ʱ����Ĺ���ĸ���",
      0
  ) ;
  public static configFactory.setter_bool death_msg = factory.add(
      "death_msg",
      "������Ϣ���ѣ���Ĺ����ʱ��֪��Ҵ�����Ϣ",
      true
  ) ;
  public static configFactory.setter_bool death_msg_pos = factory.add(
      "death_msg_pos",
      "����������Ϣ���ѣ�Ҫͬʱ����������",
      false
  ) ;
  public static configFactory.setter_bool death_msg_nfc = factory.add(
      "death_msg_nfc",
      "����������Ϣ���ѣ��Ҳ�������������ĹʱҪ���������",
      true
  ) ;
  public static configFactory.setter_bool isnot_valid_position = factory.add(
      "not_valid_position",
      "��Ĺ�Ҳ�����������ʱҪ�����ƻ�����������<spawn_zombie>ѡ���������һֻС��ʬ",
      true
  ) ;
  public static configFactory.setter_bool cause_of_death = factory.add(
      "cause_of_death",
      "����Ҫ��ʾ���������Ƿ����������ܻ���ʾΪӢ�ġ�",
      false
  ) ;
  public static configFactory.setter_bool notify_owner = factory.add(
      "notify_owner",
      "����֮Ĺ���ƻ�ʱ��Ҫ֪ͨĹ����",
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
