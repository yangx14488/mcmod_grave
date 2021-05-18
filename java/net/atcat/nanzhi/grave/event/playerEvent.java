package net.atcat.nanzhi.grave.event;

import net.atcat.nanzhi.grave.com.block.graveBlock;
import net.atcat.nanzhi.grave.config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( ) // 注入forge总线
public class playerEvent {
  
  public static ITextComponent getDeathMessage( LivingEntity entityLivingBaseIn, DamageSource damageSource ) {
    if ( config.cause_of_death.get( ) ) {
      LivingEntity livingentity = entityLivingBaseIn.getAttackingEntity();
      String s = "death.attack." + damageSource.damageType;
      String s1 = s + ".player";
      return livingentity != null ? new TranslationTextComponent( s1, "", livingentity.getDisplayName( ) ) : new TranslationTextComponent( s, "" );
    } else {
      return new TranslationTextComponent( "txt.nanzhi_grave.death" ) ;
    }
  }
  
  @SubscribeEvent
  public static void onPlayerDeath ( LivingDeathEvent event ) {
  
    Entity entity = event.getEntity( ) ;
    World world = entity.getEntityWorld( ) ;

    // 试图生成墓碑
    graveBlock.createGraveBlock( world, entity, getDeathMessage( (LivingEntity) entity, event.getSource( ) ).getString( ) ) ;

  } ;
}
