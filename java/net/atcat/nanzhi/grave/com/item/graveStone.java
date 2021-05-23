package net.atcat.nanzhi.grave.com.item;

import net.atcat.nanzhi.grave.grave;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class graveStone extends BlockItem {
  
  
  public graveStone ( Block blockIn, Properties builder ) {
    super( blockIn, builder );
  }
  
  @Override
  public String getTranslationKey( ) { return "item." + grave.modID + ".gravestone" ; }
  
  @OnlyIn( Dist.CLIENT )
  @Override
  public void addInformation( ItemStack stack, @Nullable World worldIn, List< ITextComponent > tooltip, ITooltipFlag flagIn) {
    
    tooltip.add( new TranslationTextComponent( "lore.item." + grave.modID + ".grave" ).mergeStyle( TextFormatting.GRAY ) ) ;
    tooltip.add( ITextComponent.getTextComponentOrEmpty( "" ) ) ;
    tooltip.add( new TranslationTextComponent( "lore_end.item." + grave.modID + ".grave" ).mergeStyle( TextFormatting.DARK_GRAY ) ) ;
    
  }
  
}
