package net.atcat.nanzhi.grave.com.blockEntity;

import net.atcat.nanzhi.grave.registry.tileEntityReg;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Objects;

public class graveEntity extends storageEntity {
  
  private ITextComponent DISPLAY_NAME = null ;
  private ITextComponent DISPLAY_MSG = null ;
  private java.util.UUID UUID = null ;
  private boolean BUILD = false ;
  
  // 客户端独有
  public String[] strings = { null, null } ; // 0: name, 1:msg
  public String[][] stringCutArr = { null, null } ;
  
  public graveEntity ( ITextComponent title ) {
    super( tileEntityReg.graveTileEntity, 5, title );
  }
  
  public graveEntity displayName ( @Nonnull ITextComponent name ) {
    this.DISPLAY_NAME = name ;
    markDirty( ) ;
    return this ;
  } ;
  public graveEntity displayName ( @Nonnull String name ) {
    return this.displayName( new StringTextComponent( name ) ) ;
  } ;
  public graveEntity displayMsg ( @Nonnull ITextComponent msg ) {
    this.DISPLAY_MSG = msg ;
    markDirty( ) ;
    return this ;
  } ;
  public graveEntity displayMsg ( @Nonnull String msg ) {
    return this.displayMsg( new StringTextComponent( msg ) ) ;
  } ;
  public graveEntity isBuild ( boolean build ) {
    if ( this.BUILD = build ) this.UUID = null ;
    markDirty( ) ;
    return this ;
  } ;
  public graveEntity uuid ( @Nonnull java.util.UUID uuid ) {
    if ( !this.BUILD ) this.UUID = uuid ;
    markDirty( ) ;
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
  } ;
  
  public void readNBT ( CompoundNBT nbt ) {
    String name = nbt.getString( "display_name" ) ;
    String msg = nbt.getString( "display_msg" ) ;
    String uuid = nbt.getString( "uuid" ) ;
    if ( !Objects.equals( name, "" ) ) this.displayName( name ) ;
    if ( !Objects.equals( msg, "" ) ) this.displayMsg( msg ) ;
    if ( !Objects.equals( uuid, "" ) ) this.uuid( uuid ) ;
    this.isBuild( nbt.getBoolean( "build" ) ) ;
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
  
  @Override // 通知客户端更新
  public void handleUpdateTag( BlockState state, CompoundNBT tag ) {
    this.readNBT( tag ) ;
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
  
}
