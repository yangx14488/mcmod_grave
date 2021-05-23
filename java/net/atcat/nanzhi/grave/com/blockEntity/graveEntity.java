package net.atcat.nanzhi.grave.com.blockEntity;

import net.atcat.nanzhi.grave.com.block.graveBlock;
import net.atcat.nanzhi.grave.registry.tileEntityReg;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Objects;

public class graveEntity extends storageEntity {
  
  private ITextComponent DISPLAY_NAME = null ;
  private ITextComponent DISPLAY_MSG = null ;
  private java.util.UUID UUID = null ;
  private boolean BUILD = false ;
  private ItemStack modelStack = ItemStack.EMPTY.copy( ) ;
  
  // 客户端独有
  public String[] strings = { null, null } ; // 0: name, 1:msg
  public String[][] stringCutArr = { null, null } ;
  
  public graveEntity ( ITextComponent title ) {
    super( tileEntityReg.graveTileEntity, 5, title );
  }
  
  public graveEntity displayName ( @Nonnull ITextComponent name ) {
    this.DISPLAY_NAME = name ;
    this.markDirty( ) ;
    return this ;
  } ;
  public graveEntity displayName ( @Nonnull String name ) {
    return this.displayName( new StringTextComponent( name ) ) ;
  } ;
  public graveEntity displayMsg ( @Nonnull ITextComponent msg ) {
    this.DISPLAY_MSG = msg ;
    this.markDirty( ) ;
    return this ;
  } ;
  public graveEntity displayMsg ( @Nonnull String msg ) {
    return this.displayMsg( new StringTextComponent( msg ) ) ;
  } ;
  public graveEntity isBuild ( boolean build ) {
    this.BUILD = build ;
    this.markDirty( ) ;
    return this ;
  } ;
  public graveEntity uuid ( @Nonnull java.util.UUID uuid ) {
    this.UUID = uuid ;
    this.markDirty( ) ;
    return this ;
  } ;
  public graveEntity uuid ( @Nonnull String uuid ) {
    this.uuid( java.util.UUID.fromString( uuid ) ) ;
    return this ;
  } ;
  
  public boolean isBuild ( ) { return this.BUILD ; } ;
  public ITextComponent displayName ( ) { return this.DISPLAY_NAME ; } ;
  public ITextComponent displayMsg ( ) { return this.DISPLAY_MSG ; } ;
  public java.util.UUID uuid ( ) { return this.UUID ; } ;
  
  
  public void writeNBT ( CompoundNBT nbt ) {
    nbt.putString( "display_name", this.DISPLAY_NAME == null ? "" : this.DISPLAY_NAME.getString( ) ) ;
    nbt.putString( "display_msg", this.DISPLAY_MSG == null ? "" : this.DISPLAY_MSG.getString( ) ) ;
    nbt.putString( "uuid", this.UUID == null ? "" : UUID.toString( ) ) ;
    nbt.putBoolean( "build", this.BUILD ) ;
    nbt.put( "model_stack", modelStack.serializeNBT( ) ) ;
  } ;
  
  public void readNBT ( CompoundNBT nbt ) {
    String name = nbt.getString( "display_name" ) ;
    String msg = nbt.getString( "display_msg" ) ;
    String uuid = nbt.getString( "uuid" ) ;
    if ( !Objects.equals( name, "" ) ) this.displayName( name ) ;
    if ( !Objects.equals( msg, "" ) ) this.displayMsg( msg ) ;
    if ( !Objects.equals( uuid, "" ) ) this.uuid( uuid ) ;
    this.isBuild( nbt.getBoolean( "build" ) ) ;
    INBT itemStack = nbt.get( "model_stack" ) ;
    if ( itemStack != null ) {
      this.modelStack = ItemStack.read( (CompoundNBT) itemStack ) ;
    } ;
  } ;
  
  @Override
  public CompoundNBT write( CompoundNBT nbt ) {
    super.write( nbt ) ;
    this.writeNBT( nbt ) ;
    return nbt;
  }
  
  @Override
  public void read( BlockState state, CompoundNBT nbt) {
    this.readNBT( nbt ) ;
    super.read( state, nbt );
  }
  
  @Override  // 服务器发送数据
  public SUpdateTileEntityPacket getUpdatePacket( ) {
    return new SUpdateTileEntityPacket( pos, 1, getUpdateTag( ) );
  }
  
  @Override // 客户端接收数据
  public void onDataPacket( NetworkManager net, SUpdateTileEntityPacket pkt) {
    handleUpdateTag( world.getBlockState( pkt.getPos( ) ), pkt.getNbtCompound( ) ) ;
  }
  
  @Override // 区块更新时取得数据包
  public CompoundNBT getUpdateTag( ) {
    CompoundNBT nbt = super.getUpdateTag();
    this.writeNBT( nbt ) ;
    return nbt;
  }
  
  @Override // 客户端更新解包
  public void handleUpdateTag( BlockState state, CompoundNBT tag ) {
    this.readNBT( tag ) ;
  }
  
  private static void light ( World world, BlockPos pos, ItemStack stack ) {
    BlockState state = world.getBlockState( pos ) ;
    int light = 0 ;
    if ( state.getBlock( ) instanceof graveBlock ) { // 墓碑
      if ( state.get( graveBlock.WATERLOGGED ) ) {
        light = 6 ;
      } else if ( !stack.isEmpty( ) && stack.getItem( ) instanceof BlockItem ) {
        Block block = ( (BlockItem) stack.getItem( ) ).getBlock( ) ;
        light = block.getLightValue( block.getDefaultState( ), world, pos ) ;
      } ;
    } ;
    BlockState newState = state.with( graveBlock.LIGHT, light ) ;
    if ( state != newState ) {
      world.setBlockState( pos, newState, 3 ) ;
    } ;
  } ;
  
  @Override
  public void markDirty ( ) {
    boolean notify = false ;
    if ( this.world != null ) { // 触发数据同步
      ItemStack stack = this.storageCont.get( 44 ) ;
      if ( stack.isEmpty( ) ) {
        if ( !this.modelStack.isEmpty( ) ) {
          light( this.world, this.pos, this.modelStack = ItemStack.EMPTY.copy( ) ) ;
          notify = true ;
        }
      } else if ( !stack.isItemEqual( this.modelStack ) ) {
        if ( stack.getItem( ) instanceof BlockItem ) {
          Block block = ( (BlockItem) stack.getItem( ) ).getBlock( ) ;
          if ( block instanceof AbstractSkullBlock ) {
            super.markDirty( );
            return ;
          } ;
        } ;
        light( this.world, this.pos, this.modelStack = this.storageCont.get( 44 ).copy( ) );
        notify = true ;
      } ;
      if ( notify ) {
        this.world.notifyBlockUpdate( this.getPos( ), this.getBlockState( ), this.getBlockState( ), 3 );
      } ;
    }
    super.markDirty( );
  }
  
  // 移除能力
  @Override
  protected net.minecraftforge.items.IItemHandler createUnSidedHandler( ) {
    return null ;
  }
  
  @Override
  public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, @javax.annotation.Nullable net.minecraft.util.Direction side) {
    return LazyOptional.empty( ) ;
  }
  
  @Override
  public boolean isItemValidForSlot(int index, ItemStack stack) {
    return false;
  }
  
  @Override
  protected void invalidateCaps() {
  
  }
  
  @OnlyIn( Dist.CLIENT )
  public ItemStack getModelStack ( ) {
    return this.modelStack ;
  } ;
  
}
