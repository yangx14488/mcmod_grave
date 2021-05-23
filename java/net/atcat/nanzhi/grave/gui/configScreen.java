package net.atcat.nanzhi.grave.gui;

import net.atcat.nanzhi.grave.obj.configFactory;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;

public class configScreen extends Screen {
  
  private static final int OPTIONS_LIST_TOP_HEIGHT = 24 ;
  private static final int OPTIONS_LIST_BOTTOM_OFFSET = 32 ;
  private static final int OPTIONS_LIST_ITEM_HEIGHT = 25 ;
  
  private static final int BUTTON_WIDTH = 200 ; // Ҫ������Զ���
  private static final int BUTTON_HEIGHT = 20 ;
  private static final int DONE_BUTTON_TOP_OFFSET = 26 ;
  
  private OptionsRowList optionsRowList ; // ѡ���
  private final Screen parentScreen ; // ������Ļ
  private final Minecraft mc ;
  private final configFactory factory ;
  
  public configScreen ( Minecraft mc, Screen parentScreen, configFactory factory, String modid ) {
    super( new TranslationTextComponent( "conf." + modid + ".title" ) );
    this.parentScreen = parentScreen ;
    this.mc = mc ;
    this.factory = factory ;
  }
  
  @Override
  protected void init( ) {
    
    // ��ȡ���õ�����ֵ������ѡ���б�
    factory.read( ) ;
    this.optionsRowList = writeOptions( new OptionsRowList(
        this.mc, this.width, this.height,
        OPTIONS_LIST_TOP_HEIGHT,
        this.height - OPTIONS_LIST_BOTTOM_OFFSET,
        OPTIONS_LIST_ITEM_HEIGHT ), factory.LIST ) ;
    
    this.children.add( this.optionsRowList ) ;
    
    this.addButton( new Button(
        ( this.width - BUTTON_WIDTH ) / 2,
        this.height - DONE_BUTTON_TOP_OFFSET,
        BUTTON_WIDTH, BUTTON_HEIGHT,
        new TranslationTextComponent("gui.done" ),
        button -> this.closeScreen( )
    ) ) ;
    
    super.init( ) ;
    
  }
  
  @Override
  public void render ( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks ) {
    // ���Ʊ���
    this.renderBackground( matrixStack ) ;
    // ����ѡ������
    this.optionsRowList.render( matrixStack, mouseX, mouseY, partialTicks ) ;
    // ���Ʊ���
    drawCenteredString( matrixStack, this.font, this.title.getString( ), this.width / 2, 8, 0xFFFFFF ) ;
    super.render( matrixStack, mouseX, mouseY, partialTicks ) ;
  } ;
  
  @Override
  public void onClose( ) {
    factory.write( ).save( ) ; // д�벢����
  } ;
  
  @Override
  public void closeScreen( ) {
    this.mc.displayGuiScreen( this.parentScreen ) ;
  } ;
  
  // �������㷢���Ҳ�������
  public static AbstractOption opt_cell ( configFactory.setter_bool s ) {
    return new BooleanOption( I18n.format( s.getTranslationKey( ) ), a -> s.val, ( a, b ) -> s.val = b ) ;
  } ;
  // ������������
  public static AbstractOption opt_cell ( configFactory.setter_int s ) {
    return new SliderPercentageOption(
        I18n.format( s.getTranslationKey( ) ), s.min, s.max, s.step, unused -> (double) s.val ,
        ( a, b ) -> s.val = b.intValue( ) ,
        ( a, b ) -> new StringTextComponent( I18n.format( s.getTranslationKey( ) ) + ": " + (int) b.get(a) )
    ) ;
  } ;
  // �Ǹ������޹��׷������Ĺ켣
  public static AbstractOption opt_cell ( configFactory.setter_double s, boolean p ) {
    return new SliderPercentageOption(
        I18n.format( s.getTranslationKey( ) ), s.min, s.max, s.step, unused -> s.val,
        ( a, b ) -> s.val = b,
        ( a, b ) -> new StringTextComponent( I18n.format( s.getTranslationKey( ) ) + ": " + (int) b.get(a) + ( p ? "%" : "" ) )
    ) ;
  } ;
  
  public static OptionsRowList writeOptions ( OptionsRowList optList, ArrayList< configFactory.setter<?,?> > LIST ) { // д������ѡ��
    for ( configFactory.setter<?,?> s : LIST ) {
      if ( s instanceof configFactory.setter_bool ) {
        optList.addOption( opt_cell( (configFactory.setter_bool) s ) ) ;
      } else if ( s instanceof configFactory.setter_int ) {
        optList.addOption( opt_cell( (configFactory.setter_int) s ) ) ;
      } else if ( s instanceof configFactory.setter_percentage ) {
        optList.addOption( opt_cell( (configFactory.setter_percentage) s, true ) ) ;
      } else if ( s instanceof configFactory.setter_double ) {
        optList.addOption( opt_cell( (configFactory.setter_double) s, false ) ) ;
      } ;
    } ;
    return optList ;
  } ;
  
  
}