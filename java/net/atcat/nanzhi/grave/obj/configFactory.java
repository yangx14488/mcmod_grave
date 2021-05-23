package net.atcat.nanzhi.grave.obj;

import net.atcat.nanzhi.grave.config;
import net.atcat.nanzhi.grave.grave;
import net.atcat.nanzhi.grave.gui.configScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;

public class configFactory {
  
  public ForgeConfigSpec builder ;
  
  private String id ;
  
  public final ArrayList<setter<?,?>> LIST = new ArrayList<>( ) ;
  
  private ForgeConfigSpec.Builder comBuilder ;
  private boolean build = false ;
  
  public configFactory ( String modID ) {
    this( "", modID ) ;
  } ;
  
  public configFactory ( String tips, String modID ) {
    this.id = modID ;
    this.comBuilder = new ForgeConfigSpec.Builder( ) ;
    this.comBuilder.comment( tips ).push( modID ) ;
  } ;
  
  public setter_bool add ( String key, boolean val ) { // ����һ������ֵ����
    return this.add( key, "", val ) ;
  } ;
  
  public setter_bool add ( String path, String name, boolean val ) {
    setter_bool ret = new setter_bool( comBuilder.comment( name ).define( path, val ), name, this.getTranslationKey( path ) ) ;
    LIST.add( ret ) ;
    return ret ;
  } ;
  
  public setter_int add ( String key, int val, int min, int max ) { // ����һ����������
    return this.add( key, "", val, min, max ) ;
  } ;
  
  public setter_int add ( String path, String name, int val, int min, int max ) {
    setter_int ret = new setter_int( comBuilder.comment( name ).defineInRange( path, val, min, max ), name, this.getTranslationKey( path ), min, max ) ;
    LIST.add( ret ) ;
    return ret ;
  } ;
  
  public setter_double add ( String key, double val, double min, double max, double step ) { // ����һ��С������
    return this.add( key, "", val, min, max, step ) ;
  } ;
  
  public setter_double add ( String path, String name, double val, double min, double max, double step ) {
    setter_double ret = new setter_double( comBuilder.comment( name ).defineInRange( path, val, min, max ), name, this.getTranslationKey( path ), (float) min, (float) max, (float) step ) ;
    LIST.add( ret ) ;
    return ret ;
  } ;
  
  public setter_percentage add ( String key, double val ) { // ����һ���ٷֱ�
    return this.add( key, "", val ) ;
  } ;
  
  public setter_percentage add ( String path, String name, double val ) {
    setter_percentage ret = new setter_percentage( comBuilder.comment( name ).defineInRange( path, Math.max( 0, Math.min( val, 1 ) ), 0, 1 ), name, this.getTranslationKey( path ) ) ;
    LIST.add( ret ) ;
    return ret ;
  } ;
  
  public String getTranslationKey ( String key ) {
    return "conf." + id + "." + key ;
  } ;
  
  public void build ( ) { // ����
    if ( !build ) {
      this.comBuilder.pop( ) ;
      this.builder = this.comBuilder.build( ) ;
      this.build = true ;
    } ;
  } ;
  
  public configFactory read ( ) { // ��ȡ���е����õ�������
    for ( setter<?,?> set : LIST ) set.read( ) ;
    return this ;
  } ;
  
  public configFactory write ( ) { // �����еĻ���д�뵽������
    for ( setter<?,?> set : LIST ) set.write( ) ;
    return this ;
  } ;
  
  public void save ( ) { // ������������
    builder.save( ) ;
  } ;
  
  public static class setter<T,V extends ForgeConfigSpec.ConfigValue<T>>{
    public V setter ;
    public T val ;
    private String name ;
    private String key ;
    public float max = 0 ;
    public float min = 0 ;
    public float step = 0 ;
    public setter ( V setter, String str, String key ) {
      this.setter = setter ;
      this.name = str ;
      this.key = key ;
    } ;
    public T get ( ) { // ����
      return setter.get( ) ;
    } ;
    public void set ( T val ) {
      setter.set( val ) ;
    } ;
    public String getDisplayStr ( ) { // ����
      return this.name;
    } ;
    public String getTranslationKey ( ) { // ����
      return this.key;
    } ;
    public void save ( ) {
      setter.save( ) ;
    } ;
    public void write ( ) { // ������д�뵽��ǰ��ֵ
      if ( val != null ) {
        setter.set( val ) ;
        val = null ;
      } ;
    } ;
    public void read ( ) { // ����ǰ��ֵ��ȡ������
      val = setter.get( ) ;
    } ;
  }
  public static class setter_bool extends setter<Boolean,ForgeConfigSpec.BooleanValue> {
    public setter_bool ( ForgeConfigSpec.BooleanValue setter, String str, String key ) {
      super( setter, str, key ) ;
    }
  }
  public static class setter_int extends setter<Integer,ForgeConfigSpec.IntValue> {
    public setter_int ( ForgeConfigSpec.IntValue setter, String str, String key, float min, float max ) {
      super( setter, str, key ) ;
      this.max = max ;
      this.min = min ;
      this.step = 1 ;
    }
  }
  public static class setter_double extends setter<Double,ForgeConfigSpec.DoubleValue> {
    public setter_double ( ForgeConfigSpec.DoubleValue setter, String str, String key, float min, float max, float step ) {
      super( setter, str, key ) ;
      this.max = max ;
      this.min = min ;
      this.step = step ;
    }
  }
  public static class setter_percentage extends setter_double {
    public setter_percentage ( ForgeConfigSpec.DoubleValue setter, String str, String key ) {
      super( setter, str, key, 0, 100, 1 ) ;
    }
    public void write ( ) {
      if ( val != null ) {
        setter.set( Math.max( 0, Math.min( val / 100, 1 ) ) ) ;
        val = null ;
      } ;
    } ;
    public void read ( ) {
      val = Math.max( 0, Math.min( setter.get( ) * 100, 100 ) ) ;
    } ;
  }

  
}