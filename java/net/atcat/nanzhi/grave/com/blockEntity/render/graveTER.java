package net.atcat.nanzhi.grave.com.blockEntity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.atcat.nanzhi.grave.com.block.graveBlock;
import net.atcat.nanzhi.grave.com.blockEntity.graveEntity;
import net.atcat.nanzhi.grave.grave;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Objects;

@OnlyIn( Dist.CLIENT )
public class graveTER extends TileEntityRenderer<graveEntity> {
  
  public graveTER( TileEntityRendererDispatcher rendererDispatcherIn ) {
    super( rendererDispatcherIn ) ;
  }
  
  public static class drawString {
    private MatrixStack matrixStack;
    private IRenderTypeBuffer buffer;
    private int combinedLight;
    private FontRenderer fontrenderer ;
    private float width ;
    public int color = NativeImage.getCombined(0, 0, 0, 0 ) ;
    public drawString ( MatrixStack m, IRenderTypeBuffer b, int c, FontRenderer f, float widthIn ) {
      this.matrixStack = m ;
      this.buffer = b ;
      this.combinedLight = c ;
      this.fontrenderer = f ;
      this.width = widthIn ;
    }
    public void render ( String str, float x, float y ) {
      fontrenderer.renderString( str, x, y, color, false, matrixStack.getLast().getMatrix(), buffer, false, 0, combinedLight) ;
    } ;
    public void renderCenter ( String str, float y ) {
      this.render( str, ( this.width - fontrenderer.getStringWidth( str ) ) / 2, y );
    } ;
    public FontRenderer getRenderer ( ) {
      return fontrenderer ;
    } ;
  } ;
  
  private static String[] cutString ( String str, FontRenderer renderer, int max_width, int max_row ) {
    String[] ret = new String[max_row] ;
    // 记得限定str的长度
    boolean rg = false ;
    char[] s_arr = str.toCharArray( ) ;
    int width = 0 ;
    int row = 0 ;
    // 咕，什么时候把换行符弄进去
    StringBuilder stringBuilder = new StringBuilder( );
    for ( char s : s_arr ) {
        int l = renderer.getStringWidth( String.valueOf( s ) ) ;
        stringBuilder.append( s ) ; // 追加字符
        if ( l + width <= max_width ) { // 长度还可控
          width += l ; // 长度叠加
        } else { // 过长
          if ( row >= max_row ) { return ret ; } ; // 停止处理并直接返回
          ret[ row++ ] = stringBuilder.toString( ) ; // 字符写入数组中并更新索引
          width = l ; // 长度重设
          stringBuilder.append( s ) ; // 追加
          stringBuilder.delete( 0, stringBuilder.length( ) ) ; // 清空
        } ;
    } ;

    if ( stringBuilder.length( ) > 0 && row < max_row ) ret[ row ] = stringBuilder.toString( ) ; // 写入缓存
    return ret ;
  } ;
  
  @OnlyIn( Dist.CLIENT )
  public void renderStr ( graveEntity tileEntity, ITextComponent iTextComponent, graveTER.drawString draw, int index, int row, int y_pixel, int max_width ) {
    if ( iTextComponent != null ) {
      String str_name = iTextComponent.getString( ) ;
      if ( tileEntity.strings[ index ] == null || !Objects.equals( tileEntity.strings[ index ], str_name ) ) { // 不匹配
        tileEntity.strings[ index ] = str_name ;
        tileEntity.stringCutArr[ index ] = cutString( tileEntity.strings[ index ] = str_name, draw.getRenderer( ), max_width, row ) ;
      } ;
      if ( tileEntity.stringCutArr[ index ] != null ) {
        for ( int i = 0; i < tileEntity.stringCutArr[ index ].length ; i++ ) {
          if ( tileEntity.stringCutArr[ index ][ i ] != null ) {
            draw.renderCenter( tileEntity.stringCutArr[ index ][ i ], y_pixel + 9 * i ) ;
          } ;
        } ;
      } ;
    } else {
      tileEntity.stringCutArr[ index ] = null ;
    } ;
  } ;
  
  @Override
  public void render( graveEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
  
    // 获得状态
    BlockState blockstate = tileEntityIn.getBlockState( ) ;
    boolean water = blockstate.get( graveBlock.WATERLOGGED ) ;
    // 客户端
    GraphicsFanciness graphics = Minecraft.getInstance( ).gameSettings.graphicFanciness ;
    // 渲染准备
    BlockState state = null ;
    ItemStack stack = null ;
    
    if ( water ) {
      // 海泡菜
      state = Blocks.SEA_PICKLE.getDefaultState( ).with( SeaPickleBlock.PICKLES, 2 ).with( SeaPickleBlock.WATERLOGGED, true ) ;
    } else if ( ( graphics != GraphicsFanciness.FAST || blockstate.getLightValue( ) > 0 ) ) { // 图像品质高或者有亮度的时候绘制
      // 物品写入
      stack = tileEntityIn.getModelStack( ) ;
      if ( !stack.isEmpty( ) && stack.getItem( ) instanceof BlockItem ) {
        // 模型写入
        state = ( (BlockItem) stack.getItem( ) ).getBlock( ).getDefaultState( ) ;
      } ;
    } ;
    
    // 获得状态
    matrixStackIn.push( ) ;
  
    // 获得索引值
    int hIndex = blockstate.get( graveBlock.FACING ).getHorizontalIndex( ) ;
    
    // 偏移计算
    int offsetR = 180 - hIndex * 90 ;
    int offsetX = ( hIndex & 2 ) == 0 ? -1 : 0 ;
    int offsetZ = ( ( hIndex - 1 ) & 2 ) == 0 ? 0 : -1 ;
    
    // 坐标旋转
    matrixStackIn.rotate( Vector3f.YP.rotationDegrees( offsetR ) ) ;
  
    matrixStackIn.push( ) ;
    // 模型绘制
    if ( state != null ) {
      matrixStackIn.translate( offsetX + .25, 0.125, offsetZ+ .3 ) ;
      matrixStackIn.scale(.5f, .5f, .5f ) ;
      // matrixStackIn.rotate( new Quaternion( 0f, 0f, 180f, true ) );
      BlockRendererDispatcher blockRenderer = Minecraft.getInstance( ).getBlockRendererDispatcher( ) ;
      blockRenderer.renderBlock( state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE ) ;
    } else if ( stack != null && !stack.isEmpty( ) ) {
      matrixStackIn.translate( offsetX +.5, .125, offsetZ +.5 ) ;
      matrixStackIn.rotate( new Quaternion( 90f, 0f, 180f, true ) );
      matrixStackIn.scale(.4f, .4f, .4f ) ;
      ItemRenderer itemRenderer = Minecraft.getInstance( ).getItemRenderer( ) ;
      IBakedModel model = itemRenderer.getItemModelWithOverrides( stack, tileEntityIn.getWorld( ), null ) ;
      itemRenderer.renderItem( stack, ItemCameraTransforms.TransformType.FIXED, true, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, model ) ;
    } ;
    matrixStackIn.pop( ) ;
    
    // 坐标系变换（位移）
    matrixStackIn.translate( offsetX, 1, offsetZ +0.063 ) ; // 多绘制0.005,不然有很奇怪的不显示的bug
  
    // 缩放(这里的缩放值经过计算，1个方块的单位:缩放的单位应该是 1:96)
    matrixStackIn.scale(0.010416667F, -0.010416667F, 0.010416667F ) ;
    
    // 字符渲染器
    drawString strDraw = new drawString( matrixStackIn, bufferIn, combinedLightIn, this.renderDispatcher.getFontRenderer( ), 96f ) ;
    
    // 绘制墓碑显示内容
    renderStr(
        tileEntityIn,
        tileEntityIn.displayName( ) == null ? new TranslationTextComponent( "txt."+ grave.modID + ".g_o_n" ) : tileEntityIn.displayName( ) , strDraw,0,2,6, 48
    ); ;
    renderStr( tileEntityIn, tileEntityIn.displayMsg( ), strDraw,1, 5, 29, 60 );

    matrixStackIn.pop( ) ;
    
  }
  



  
  
}
