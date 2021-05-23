package net.atcat.nanzhi.grave.com.blockEntity;

import net.atcat.nanzhi.grave.com.block.graveBlock;
import net.atcat.nanzhi.grave.registry.blockReg;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.BarrelTileEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;

public class storageEntity extends LockableLootTileEntity {
  
  protected int storageRows ;
  protected int storageSize ;
  protected ContainerType<ChestContainer> storageType ;
  protected NonNullList<ItemStack> storageCont;
  protected int numPlayersUsing;
  protected ITextComponent title ;
  
  
  public storageEntity ( TileEntityType<?> typeIn, int rows, ITextComponent title ) {
    super( typeIn );
    this.storageRows = Math.max( 1, Math.min( rows, 6 ) ) ;
    this.storageSize = this.storageRows * 9 ;
    this.storageCont = NonNullList.withSize( this.storageSize, ItemStack.EMPTY ) ;
    this.title = title ;
    this.storageType =
        this.storageRows == 1 ? ContainerType.GENERIC_9X1 :
        this.storageRows == 2 ? ContainerType.GENERIC_9X2 :
        this.storageRows == 3 ? ContainerType.GENERIC_9X3 :
        this.storageRows == 4 ? ContainerType.GENERIC_9X4 :
        this.storageRows == 5 ? ContainerType.GENERIC_9X5 :
        ContainerType.GENERIC_9X6 ;
  }
  
  public CompoundNBT write(CompoundNBT compound) {
    super.write(compound);
    if (!this.checkLootAndWrite(compound)) {
      ItemStackHelper.saveAllItems(compound, this.storageCont);
    }
    
    return compound;
  }
  
  public void read( BlockState state, CompoundNBT nbt ) {
    super.read(state, nbt);
    this.storageCont = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
    if (!this.checkLootAndRead(nbt)) {
      ItemStackHelper.loadAllItems(nbt, this.storageCont);
    }
    
  }

  public int getSizeInventory( ) {
    return this.storageSize ;
  }
  
  public NonNullList<ItemStack> getItemStorage() {
    return this.storageCont;
  }
  public void setItemStorage(NonNullList<ItemStack> itemsIn) {
    this.storageCont = itemsIn;
  }
  
  protected NonNullList<ItemStack> getItems() {
    return this.storageCont;
  }
  
  protected void setItems(NonNullList<ItemStack> itemsIn) {
    this.storageCont = itemsIn;
  }
  
  protected ITextComponent getDefaultName() {
    return this.title ;
  }
  
  protected Container createMenu(int id, PlayerInventory player) {
    return new ChestContainer( this.storageType, id, player, this, this.storageRows );
  }
  
  @Override
  public void openInventory( PlayerEntity player) {
    if (!player.isSpectator()) {
      if (this.numPlayersUsing < 0) {
        this.numPlayersUsing = 0;
      }
      
      ++this.numPlayersUsing;
      this.scheduleTick();
    }
    
  }
  
  private void scheduleTick() {
    this.world.getPendingBlockTicks().scheduleTick(this.getPos(), this.getBlockState().getBlock(), 5);
  }
  
  public void barrelTick() {
    int i = this.pos.getX();
    int j = this.pos.getY();
    int k = this.pos.getZ();
    this.numPlayersUsing = ChestTileEntity.calculatePlayersUsing(this.world, this, i, j, k);
    if (this.numPlayersUsing > 0) {
      this.scheduleTick();
    } else {
      BlockState blockstate = this.getBlockState();
      if (!blockstate.isIn( blockReg.graveBlock )) {
        this.remove();
      }
    }
    
  }
  
  public void closeInventory(PlayerEntity player) {
    if (!player.isSpectator()) {
      --this.numPlayersUsing;
    }
    
  }
  
  public void playSound(BlockState state, SoundEvent sound) {
    Vector3i vector3i = state.get( graveBlock.FACING ).getDirectionVec();
    double d0 = (double)this.pos.getX() + 0.5D + (double)vector3i.getX() / 2.0D;
    double d1 = (double)this.pos.getY() + 0.5D + (double)vector3i.getY() / 2.0D;
    double d2 = (double)this.pos.getZ() + 0.5D + (double)vector3i.getZ() / 2.0D;
    this.world.playSound((PlayerEntity)null, d0, d1, d2, sound, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
  }
  
}
