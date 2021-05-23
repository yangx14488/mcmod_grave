package net.atcat.nanzhi.grave.com.item;

import net.atcat.nanzhi.grave.grave;
import net.atcat.nanzhi.grave.registry.itemReg;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

// @Mod.EventBusSubscriber( ) ��Ҫȼ���������¼�
public class idTag extends Item {
  
  public idTag ( Properties properties ) {
    super( properties );
  }
  
  @Override
  public ActionResult<ItemStack> onItemRightClick( World worldIn, PlayerEntity playerIn, Hand handIn) {
    
    // ȡ����Ʒ��
    ItemStack stack = playerIn.getHeldItem( handIn ) ;
    if ( stack.getItem( ) == itemReg.idTag ) {
      // ItemStack stack_oth = playerIn.getHeldItem( handIn == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND ) ;
      // ȡ�ñ�ǩ
      CompoundNBT tag = stack.getOrCreateTag( ) ;
      // δ����
      if ( !tag.getBoolean( "named" ) ) {
        if ( worldIn.isRemote( )  ) {
          playerIn.playSound( SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 0.5f, 1 ) ; // ��ͼ��������Ч��
          playerIn.sendStatusMessage( new TranslationTextComponent( "txt." + grave.modID + ".id_tag.named" ), true ) ;
          return ActionResult.resultSuccess( stack ) ;
        } else {
          tag.putBoolean( "named", true ) ; // ������
          tag.putString( "name", playerIn.getName( ).getString( ) ) ; // ��ʾ����
          tag.putUniqueId( "uuid", playerIn.getUniqueID( ) ); ; // ��� uuid
          return ActionResult.resultConsume( stack ) ;
        }
      } ;
    } ;
    return ActionResult.resultPass( stack ) ;
  }
  
  @OnlyIn( Dist.CLIENT )
  @Override
  public void addInformation( ItemStack stack, @Nullable World worldIn, List< ITextComponent > tooltip, ITooltipFlag flagIn) {
    CompoundNBT tag = stack.getOrCreateTag( ) ;
    boolean err = false ;
    if ( tag.getBoolean( "named" ) ) {
      try {
        tag.getUniqueId( "uuid" ) ;
      } catch ( Exception ignore ) {
        err = true ;
      } ;
      tooltip.add( new TranslationTextComponent( err ? "lore_err.item." + grave.modID + ".id_tag" : "lore.item.nanzhi_grave.id_tag" ).mergeStyle( TextFormatting.GRAY ) ) ;
      tooltip.add( ITextComponent.getTextComponentOrEmpty( "" ) ) ;
      tooltip.add( new TranslationTextComponent( "lore_end.item." + grave.modID + ".id_tag", tag.getString( "name" ) ).mergeStyle( TextFormatting.DARK_GRAY ) ) ;
    } else {
      tooltip.add( new TranslationTextComponent( "lore_nl.item." + grave.modID + ".id_tag" ).mergeStyle( TextFormatting.GRAY ) ) ;
      tooltip.add( ITextComponent.getTextComponentOrEmpty( "" ) ) ;
      tooltip.add( new TranslationTextComponent( "lore_n_e.item." + grave.modID + ".id_tag" ).mergeStyle( TextFormatting.DARK_GRAY ) ) ;
    } ;
    
  }
  
  /*
  @SubscribeEvent // ע��Ϊ��ȼ�յĴ���
  public static void onFuelTime ( FurnaceFuelBurnTimeEvent event ) {
      event.setBurnTime( 300 ) ;
      event.setResult( Event.Result.ALLOW ) ;
  } ;
  */
  
}
