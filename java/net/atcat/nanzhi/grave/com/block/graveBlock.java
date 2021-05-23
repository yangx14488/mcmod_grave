package net.atcat.nanzhi.grave.com.block;

import net.atcat.nanzhi.grave.com.blockEntity.graveEntity;
import net.atcat.nanzhi.grave.config;
import net.atcat.nanzhi.grave.grave;
import net.atcat.nanzhi.grave.obj.tags;
import net.atcat.nanzhi.grave.registry.blockReg;
import net.atcat.nanzhi.grave.registry.itemReg;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.*;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class graveBlock extends ContainerBlock {

  public static final DirectionProperty FACING = BlockStateProperties.FACING ;
  public static final BooleanProperty STONE = BooleanProperty.create( "stone" ) ;
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED ;
  public static final IntegerProperty LIGHT = IntegerProperty.create( "light", 0, 15 ) ;
  
  public static final VoxelShape[] shapeList = {
      // north
      VoxelShapes.or( Block.makeCuboidShape(2, 0, 0, 14, 16, 1), Block.makeCuboidShape(2, 0, 1, 14, 2, 16) ) ,
      // east
      VoxelShapes.or( Block.makeCuboidShape(15, 0, 2, 16, 16, 14), Block.makeCuboidShape(0, 0, 2, 15, 2, 14) ) ,
      // south
      VoxelShapes.or( Block.makeCuboidShape(2, 0, 15, 14, 16, 16), Block.makeCuboidShape(2, 0, 0, 14, 2, 15) ),
      // west
      VoxelShapes.or( Block.makeCuboidShape(0, 0, 2, 1, 16, 14), Block.makeCuboidShape(1, 0, 2, 16, 2, 14) ),
  } ;
  
  public graveBlock ( ) {
    super( AbstractBlock.Properties
        .create( Material.EARTH )
        .hardnessAndResistance( 40f, 10f )
        .sound( SoundType.GROUND )
        .notSolid( )
        .harvestTool( ToolType.SHOVEL )
        .harvestLevel( 1 )
        .setLightLevel( ( state ) -> state.get( WATERLOGGED ) ? 6 : state.get( LIGHT ) )
    ) ;
    this.setDefaultState( this.stateContainer.getBaseState( )
        .with( FACING, Direction.NORTH ) // 朝向
        .with( WATERLOGGED, false )
        .with( STONE, true )
        .with( LIGHT, 0 )
    ) ;
  }
  
  private void notify_owner ( ServerWorld worldIn, @Nullable UUID uuid, BlockPos pos ) {
    if ( config.notify_owner.get( ) && uuid != null ) {
      MinecraftServer server = worldIn.getServer( ) ;
      ServerPlayerEntity player = server.getPlayerList( ).getPlayerByUUID( uuid ) ;
      if ( player != null ) {
        player.sendStatusMessage( new TranslationTextComponent( "txt." + grave.modID + ".notify" ), false ) ;
        player.sendStatusMessage( new TranslationTextComponent( "txt." + grave.modID + ".create_grave_pos", pos.getX(), pos.getY(), pos.getZ() ), false ) ;
      } ;
    } ;
  } ;
  
  private static class graveBlockFactory {
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(41, ItemStack.EMPTY );
    private int conIndex = -1 ;
    private int gsIndex = -1 ;
    private boolean locked = false ;
    private ItemStack conStack = ItemStack.EMPTY ; // 1
    private ItemStack gsStack = ItemStack.EMPTY ; // 2
    private String msg = "" ;
    private BlockState graveState;
    private ITextComponent playerName;
    private UUID playerUUID;
    private Direction facing = Direction.NORTH ;
    public boolean setBlock ( World world, BlockPos pos, boolean named ) {
      if ( graveBlock.canBeReplaced( world, pos ) ) { // 当前位置可以放置
        boolean replace = false ;
        // 这个函数只管放置，检查不属于它管
        if ( graveBlock.canBeReplaced( world, pos.down( 1 ) ) ) { // 底部方块可替换
          replace = world.setBlockState( pos.down( 1 ), Blocks.DIRT.getDefaultState( ) ) ;
        } ;
        if ( replace || this.graveState.isValidPosition( world, pos ) ) { // 底部支持放置
          if ( world.setBlockState( pos, this.graveState.with( WATERLOGGED, graveBlock.isWater( world, pos ) ) ) ) { // 放置自身
            TileEntity tileEntity = world.getTileEntity( pos ) ; // 取得方块实体
            if ( tileEntity instanceof graveEntity ) { // 是实体墓碑容器
              NonNullList<ItemStack> stacks = ( (graveEntity) tileEntity ).getItemStorage( ) ; // 套取出存储器
              int index = 0 ;
              if ( named ) { // 拿走墓碑或者身份牌
                if ( !( config.free_nameless.get( ) && config.free_named.get( ) ) && gsStack != ItemStack.EMPTY && inventory.get( gsIndex ) == gsStack ) {
                  if ( gsStack.getCount( ) > 1 ) {
                    inventory.get( gsIndex ).setCount( gsStack.getCount( ) - 1 ) ;
                  } else {
                    inventory.set( gsIndex, ItemStack.EMPTY ) ;
                  } ;
                } ;
              } else if ( !config.free_nameless.get( ) && conStack != ItemStack.EMPTY && inventory.get( conIndex ) == conStack ) { // 拿走容器
                if ( conStack.getCount( ) > 1 ) {
                  inventory.get( conIndex ).setCount( conStack.getCount( ) - 1 ) ;
                } else {
                  inventory.set( conIndex, ItemStack.EMPTY ) ;
                } ;
              } ;
              // 顺序写入
              for ( ItemStack stack : inventory ) {
                if ( !stack.isEmpty( ) ) {
                  if ( stack.getItem( ) instanceof BlockItem ) {
                    Block block = ( (BlockItem) stack.getItem( ) ).getBlock( ) ;
                    if ( stacks.get( 44 ).isEmpty( ) && ( block instanceof FlowerBlock || block instanceof TallFlowerBlock || block instanceof TorchBlock || block instanceof LanternBlock ) ) {
                      stacks.set( 44, stack ) ;
                    } else {
                      stacks.set( index++, stack ) ;
                    } ;
                  } else {
                    stacks.set( index++, stack ) ;
                  } ;
                } ;
              } ;
              // 将库存复制到实体
              ( (graveEntity) tileEntity ).setItemStorage( stacks ) ;
              // 设置属性
              if ( named && this.playerName != null && this.playerUUID != null ) { // 有主
                ( (graveEntity) tileEntity )
                    .displayName( this.playerName )
                    .uuid( this.playerUUID )
                    .displayMsg( this.msg )
                    .isBuild( false )
                    .setCustomName( new TranslationTextComponent( "txt." + grave.modID + ".g_o_name", playerName ) );
              } else { // 无主
                LocalDate date = LocalDate.now( ) ; // 获取系统时间
                ( (graveEntity) tileEntity )
                    .displayMsg( new TranslationTextComponent( "txt."  + grave.modID +  ".d_o_n",
                        date.getYear( ) ,
                        date.getDayOfYear( )
                    ) )
                    .isBuild( false ) ;
              } ;
              // 通知完成
              return true ;
            }
            else { // 故障意外退出
              world.removeBlock( pos, false ) ;
            }
          } ;
        } ;
      } ;
      return false ;
    } ;
    public int graveLevel ( ) {
      return ( gsStack == ItemStack.EMPTY ? 0 : 0b10 ) | ( conStack == ItemStack.EMPTY ? 0 : 0b01 );
    } ;
    public boolean graveLevel ( int index ) {
      return ( this.graveLevel( ) & index ) == index ;
    } ;
    // 内建函数，提供给copyPlayerInv使用
    private void __putItemStack ( ItemStack stack, int index, PlayerEntity player ) {
      Item item = stack.getItem( ) ;
      boolean skip = false ;
      if ( !locked ) {
        if ( item == blockReg.graveBlock.asItem( ) ) { // 墓碑
          gsIndex = index ;
          gsStack = stack ;
          locked = true ; // 锁
          skip = true ;
        } else if ( item == itemReg.idTag ) { // 如果写入了墓碑，代码不会执行到这里
          CompoundNBT nbt = stack.getOrCreateTag( ) ;
          boolean named = nbt.getBoolean( "named" ) ;
          UUID uuid = null ;
          if ( named ) {
            try { // 错误检查
              uuid = nbt.getUniqueId( "uuid" ) ;
            } catch ( Exception ignore ) {
              named = false ;
              LogManager.getLogger( ).warn( "WARN: id_tag, nbt lost" ) ;
            } ;
          } ;
          if ( named && ( gsIndex == -1 || uuid == player.getUniqueID( ) ) ) { // 已命名且没有身份牌或身份牌是玩家id
            gsIndex = index ;
            gsStack = stack ;
          } ;
          skip = true ;
        } ;
      } ;
      // 容器( free开关？都免费了还计算容器干嘛 )
      if ( !skip ) {
        if ( item instanceof BlockItem ) {
          Block block = ( (BlockItem) item ).getBlock( ) ;
          if ( !config.free_nameless.get( ) && conIndex == -1 && ( block instanceof BarrelBlock || block instanceof ChestBlock ) ) {
            conIndex = index ;
            conStack = stack ;
          } ;
        } ;
      } ;
      inventory.set( index, stack ) ;
    } ;
    // 设置显示内容
    public graveBlockFactory setDisplayMsg ( String msgIn ) {
      this.msg = msgIn ;
      return this ;
    } ;
    // 设置方向
    public graveBlockFactory setDirection ( Direction direction ) {
      this.facing = direction ;
      return this ;
    } ;
    public graveBlockFactory setPlayerName ( ITextComponent name ) {
      this.playerName = name ;
      return this ;
    }
    public graveBlockFactory setPlayerUUID ( UUID uuid ) {
      this.playerUUID = uuid ;
      return this ;
    } ;
    // 复制玩家库存
    public graveBlockFactory copyPlayerInv ( PlayerEntity player ) {
      this.locked = false ;
      for ( int i = 0 ; i < 36 ; i++ ) {
        __putItemStack( player.inventory.mainInventory.get( i ).copy( ), i, player ) ;
      } ;
      for ( int i = 0 ; i < 4 ; i++ ) {
        __putItemStack( player.inventory.armorInventory.get( i ).copy( ), i + 37, player ) ;
      } ;
      __putItemStack( player.inventory.offHandInventory.get( 0 ).copy( ), 36, player ) ;
      return this ;
    } ;
    public void complete ( ) {
      if ( this.gsStack != ItemStack.EMPTY ) {
        if ( this.gsStack.hasDisplayName( ) ) { // 写入自定义内容
          this.setDisplayMsg( this.gsStack.getDisplayName( ).getString( ) ) ;
        } if ( this.gsStack.getItem( ) == itemReg.idTag ) { // 身份牌
          CompoundNBT nbt = this.gsStack.getOrCreateTag( ) ; // 读取出nbt
          try { // 设置为身份牌所有者
            this.setPlayerName( new StringTextComponent( nbt.getString( "name" ) ) ) ;
            this.setPlayerUUID( nbt.getUniqueId( "uuid" ) ) ;
          } catch ( Exception ignore ) {
            LogManager.getLogger( ).warn( "WARN: id_tag, nbt lost" ) ;
          } ;
        } ;
      } ;
      this.graveState = blockReg.graveBlock.getDefaultState( )
          .with( graveBlock.STONE, this.locked )
          .with( graveBlock.FACING, this.facing )
      ;
    } ;
  } ;
  
  private static void notice ( PlayerEntity player, BlockPos pos, int msg ) {
    if ( !config.death_msg.get( ) ) return ; // 不告知
    if ( ( msg & 1 ) == 1 ) { // 1: 成功消息
      player.sendStatusMessage( new TranslationTextComponent( "txt." + grave.modID + ".create_grave" ), false ) ;
    } ;
    if ( config.death_msg_pos.get( ) && ( ( msg >> 1 ) & 1 ) == 1 ) { // 2: 坐标点
      player.sendStatusMessage( new TranslationTextComponent( "txt." + grave.modID + ".create_grave_pos", pos.getX( ), pos.getY( ), pos.getZ( ) ), false ) ;
    } ;
    if ( ( ( msg >> 2 ) & 1 ) == 1 ) { // 4: 无法创建
      player.sendStatusMessage( new TranslationTextComponent( "txt." + grave.modID + ".c_c_g_u" ), false ) ;
    } ;
    if ( config.death_msg_nfc.get( ) && ( ( msg >> 3 ) & 1 ) == 1 ) { // 8: 找不到容器
      player.sendStatusMessage( new TranslationTextComponent( "txt." + grave.modID + ".c_c_g_c" ), false ) ;
    } ;
    
  } ;
  
  public static boolean buildGraveBlock (  graveBlockFactory factory, World world, BlockPos pos, boolean named, int radius ) {
    boolean ret = false ;
    if ( radius < 1 ) { // 半径：0
      if ( factory.setBlock( world, pos, named ) ) {
        ret = true ;
      } ;
    }
    if ( !ret && radius < 2 ) { // 半径：1
      for ( int x = -1 ; x <= 1 && !ret ; x++ ) {
        for ( int y = -1 ; y <= 1 && !ret ; y++ ) {
          for ( int z = -1 ; z <= 1 ; z++ ) {
            if ( Math.max( Math.max( Math.abs( x ), Math.abs( y ) ), Math.abs( z ) ) == 1 ) {
              if ( factory.setBlock( world, pos.north( x ).up( y ).east( z ), named ) ) {
                ret = true ;
                break ;
              } ;
            } ;
          } ;
        } ;
      } ;
    }
    if ( !ret && radius < 3 ) { // 半径：2
      for ( int x = -2 ; x <= 2 && !ret ; x++ ) {
        for ( int y = -2 ; y <= 2 && !ret ; y++ ) {
          for ( int z = -2; z <= 2 ; z++ ) {
            if ( Math.max( Math.max( Math.abs( x ), Math.abs( y ) ), Math.abs( z ) ) == 2 ) {
              if ( factory.setBlock( world, pos.north( x ).up( y ).east( z ), named ) ) {
                ret = true ;
                break ;
              } ;
            } ;
          } ;
        } ;
      } ;
    }
    return ret ;
  } ;
  
  // 试图生成墓碑，如果无法生成，返回布尔值false通知事件不处理
  public static boolean createGraveBlock ( World world, Entity entity, String defaultMsg ) { // 生成
    // 如果插件可以工作且是服务端且是玩家
    return config.can_work.get( )
        && !world.isRemote( ) // 服务器
        && entity instanceof PlayerEntity // 是玩家
        && !world.getGameRules( ).get( GameRules.KEEP_INVENTORY ).get( ) // 未开启死亡不掉落
        && createGraveBlock( (ServerWorld) world, (PlayerEntity) entity, entity.getPosition( ), defaultMsg ) // 试图生成
        ;
  } ;
  public static boolean createGraveBlock ( ServerWorld world, PlayerEntity player, BlockPos posIn, String defaultMsg ) { // 生成
    // 如果玩家坐标小于O且不允许处理虚空生成时
    if ( posIn.getY( ) < 0 && !config.in_void.get( ) ) return false ;
    // 取得流体
    Fluid fluid = world.getBlockState( posIn ).getFluidState( ).getFluid( ) ;
    // 如果玩家死在了岩浆中但配置不允许mod处理时
    if ( !config.in_lava.get( ) && ( fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA ) ) return false ;
    // 取得合理的半径
    int radius = Math.max( 0, Math.min( config.loop_radius.get( ), 2 ) ) ;
    // 布尔开关
    boolean success = false ;
    boolean notFoundContainer = false ;
    // 取得合理的坐标
    BlockPos pos = new BlockPos( posIn.getX( ), Math.max( 0, Math.min( posIn.getY( ), 255 ) ), posIn.getZ( ) ) ;
    BlockState state = world.getBlockState( pos ) ;
    // 取得工厂
    graveBlockFactory factory = new graveBlockFactory( ) ;
    // 初始化工厂
    factory
        .setDisplayMsg( defaultMsg ) // 设置显示语句
        .setPlayerName( player.getName( ) ) // 设置玩家名
        .setPlayerUUID( player.getUniqueID( ) ) // 设置玩家uuid
        .copyPlayerInv( player ) // 复制库存
        .setDirection( player.getHorizontalFacing( ) )
        .complete( )
    ;
    // 向上搜索
    if ( config.death_by_asphyxia.get( ) && state.getShape( world, pos ) == VoxelShapes.fullCube( ) ) {
      int i ;
      for ( i = pos.getY( ) ; i < 256 ; i++ ) {
        if ( graveBlock.canBeReplaced( world, pos = new BlockPos( pos.getX( ), i, pos.getZ( ) ) ) ) {
          break ;
        } ;
      } ;
    } else if ( config.in_sky.get( ) ) { // 向下搜索
      int i ;
      for ( i = pos.getY( ) ; i > config.in_sky_y.get( ); i-- ) {
        // 遇到不可替换方块时
        if ( !graveBlock.canBeReplaced( world, new BlockPos( pos.getX( ), i, pos.getZ( ) ) ) ) {
          break ;
        } ;
        pos = new BlockPos( pos.getX( ), i + 1, pos.getZ( ) ) ;
      } ;
    } ;
    // 检查生成
    if ( config.free_nameless.get( ) ) {
      if ( config.free_named.get( ) ) { // 有主
        success = buildGraveBlock( factory, world, pos, true, radius ) ;
      } else  {
        if ( factory.graveLevel( 2 ) ) { // 玩家拥有墓碑
          success = buildGraveBlock( factory, world, pos, true, radius ) ;
        } else { // 无主
          success = buildGraveBlock( factory, world, pos, false, radius ) ;
        } ;
      } ;
    } else {
      // 测试
      if ( factory.graveLevel( 2 ) ) { // 玩家拥有墓碑
        success = buildGraveBlock( factory, world, pos, true, radius ) ;
      } else if ( factory.graveLevel( 1 ) ) { // 玩家拥有容器
        success = buildGraveBlock( factory, world, pos, false, radius ) ;
      } else { // 没有容器
        notFoundContainer = true ;
      } ;
    } ;
    if ( success ) {
      player.inventory.clear( );
      graveBlock.notice( player, pos, 3 ) ;
    } else {
      graveBlock.notice( player, pos, notFoundContainer ? 8 : 4 ) ;
    } ;
    return success ;
  } ;
  
  // 僵尸生成
  public static void trySpawnAZombie ( IWorld world, BlockPos pos, boolean down ) {

    if ( !config.spawn_zombie.get( ) ) return ;

    ZombieEntity zombie = new ZombieEntity( (World) world ) ;
    zombie.setChild( true ) ; // 小僵尸
    zombie.setLocationAndAngles( pos.getX( ) +0.5, pos.getY( ) - ( down ? 1 : 0 ), pos.getZ( ) +0.5, zombie.rotationYaw, zombie.rotationPitch );
    world.addEntity( zombie ) ;
    // 变强了
    zombie.addPotionEffect( new EffectInstance( Effects.STRENGTH, 200, 1 ) ) ;
    zombie.addPotionEffect( new EffectInstance( Effects.ABSORPTION, 200, 4 ) ) ;
    zombie.addPotionEffect( new EffectInstance( Effects.SPEED, 200, 1 ) ) ;
    zombie.addPotionEffect( new EffectInstance( Effects.POISON, 1200 ) ) ;
    
    zombie.attackEntityFrom( DamageSource.FALL, 1 ) ; // 让僵尸受一点点伤害

  }
  
  // 自我摧毁
  public static boolean destroy ( IWorld world, BlockPos pos ) {
    BlockState state = world.getBlockState( pos ) ;
    if ( !world.isRemote( ) && config.isnot_valid_position.get( ) && state.getBlock( ) instanceof graveBlock ) { // 允许摧毁
      // 摧毁自身
      return world.destroyBlock( pos, true ) ;
    } ;
    return false ;
  }
  
  // 替换
  public static boolean canBeReplaced ( World world, BlockPos pos ) {
    BlockState state = world.getBlockState( pos ) ;
    Block block = state.getBlock( ) ;
    return ( world.isAirBlock( pos ) || block instanceof TallGrassBlock || block == Blocks.SNOW || block == Blocks.WATER || ( block == Blocks.LAVA && config.in_lava.get( ) ) ) ;
  } ;
  
  @Override
  public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    if ( state.getBlock( ) == blockReg.graveBlock ) {
      boolean bucket = false ;
      TileEntity tileentity = worldIn.getTileEntity( pos ) ;
      Fluid fluid = state.getFluidState( ).getFluid ( ) ; // 水
      // 如果是水桶右键，那么生成水，如果是空桶右键，那么检查是否有水，记得检查玩家是否蹲下
      ItemStack stack = player.getHeldItem( handIn ) ;
      BlockState nState = state ;
      if ( stack.getItem( ) == Items.BUCKET && state.get( WATERLOGGED ) ) { // 空桶，有水
        if ( !player.isCreative( ) ) player.setHeldItem( handIn, new ItemStack( Items.WATER_BUCKET ) ) ;
        bucket = true ;
        nState = state.with( WATERLOGGED, false ) ;
      } else if ( stack.getItem( ) == Items.WATER_BUCKET && !state.get( WATERLOGGED ) ) {
        if ( !player.isCreative( ) ) player.setHeldItem( handIn, new ItemStack( Items.BUCKET ) ) ;
        bucket = true ;
        nState = state.with( WATERLOGGED, true ) ;
      } ;
      if ( state != nState ) { // 更新
        if ( bucket ) {
          SoundEvent soundevent = fluid.getAttributes( ).getFillSound( ) ;
          if ( soundevent == null ) soundevent = SoundEvents.ITEM_BUCKET_FILL ;
          player.playSound( soundevent, 1.0F, 1.0F ) ;
        } ;
        if ( !worldIn.isRemote( ) && worldIn.setBlockState( pos, nState, 3 )  ) { // 服务器更新
          worldIn.getPendingFluidTicks().scheduleTick( pos, this.getFluidState( nState ).getFluid( ), 2 );
        } ;
      } else if ( !worldIn.isRemote( ) && tileentity instanceof graveEntity ) { // 服务器且是墓碑
        graveEntity entity = ( graveEntity ) tileentity ;
        boolean flag1 = entity.uuid( ) == null && entity.isBuild( ) ; // 人为建造的无主之墓
        boolean flag2 = entity.uuid( ) != null && entity.uuid( ).equals( player.getUniqueID( ) ) ; // 墓主
        // 墓主，或者人为建造的无主之墓可打开，或创造模式
        if ( flag1 || flag2 || player.isCreative( ) ) {
          if ( flag2 && config.break_useShove.get( ) && stack.getToolTypes( ).contains( ToolType.SHOVEL ) ) { // 试图摧毁
            if ( graveBlock.destroy( worldIn, pos ) ) {
              stack.damageItem( 2, player, ( e )->{ } ); // 损坏
            } ;
          } else { // 打开
            player.openContainer( (graveEntity) tileentity );
            player.addStat(Stats.OPEN_BARREL);
            ( (graveEntity) tileentity ).playSound( state, SoundEvents.BLOCK_GRAVEL_HIT );
          } ;
        } else {
          player.sendStatusMessage( new TranslationTextComponent( "txt." + grave.modID + ".cannot_open" ), true );
          return ActionResultType.PASS ;
        } ;
        PiglinTasks.func_234478_a_(player, true);
      }
    }
    return ActionResultType.SUCCESS ;
  }
  
  @Override
  public void onBlockHarvested( World worldIn, BlockPos pos, BlockState state, PlayerEntity player ) {
    if ( !worldIn.isRemote( ) && config.spawn_zombie.get( ) && !player.isCreative( ) ) { // 读取配置且是服务器且玩家不是创造模式
      TileEntity tileEntity = worldIn.getTileEntity( pos ) ;
      if ( tileEntity instanceof graveEntity ) {
        graveEntity entity = ( graveEntity ) tileEntity ;
        // 非创造模式的玩家以及非墓主的玩家破坏时
        if ( !player.isCreative( ) && ( entity.uuid( ) == null || !entity.uuid( ).equals( player.getUniqueID( ) ) ) ) {
          if ( !entity.isBuild( ) ) { // 非人为构建
            trySpawnAZombie( worldIn, pos, false );
          } ;
          notify_owner( (ServerWorld) worldIn, entity.uuid( ), pos ) ;
        } ;
      } ;
    } ;
    super.onBlockHarvested( worldIn, pos, state, player ) ;
  }
  
  @Override
  public boolean isValidPosition( BlockState state, IWorldReader worldIn, BlockPos pos)  {
    if ( pos.getY( ) < 1 || pos.getY( ) > 255 ) return false ;
    BlockState ns = worldIn.getBlockState( pos.down( ) ) ;
    return tags.stone.contains( ns ) || tags.dirt.contains( ns ) || tags.sand.contains( ns ) || tags.gravel.contains( ns ) || ns.getBlock( ) instanceof OreBlock ;
  }
  @Override
  public FluidState getFluidState( BlockState state) {
    return state.get( WATERLOGGED ) ? Fluids.WATER.getStillFluidState( false ) : super.getFluidState( state ) ;
  }
  
  @Override // pp更新
  public BlockState updatePostPlacement( BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
    if ( stateIn.get( WATERLOGGED ) ) {
      worldIn.getPendingFluidTicks( ) .scheduleTick( currentPos, Fluids.WATER, Fluids.WATER.getTickRate( worldIn ) );
    }
    if ( !stateIn.isValidPosition( worldIn, currentPos ) ) {
      TileEntity entity = worldIn.getTileEntity( currentPos ) ;
      UUID uuid = ( entity instanceof graveEntity ) ? ((graveEntity) entity).uuid() : null ;
      if ( destroy( worldIn, currentPos ) ) {
        trySpawnAZombie( worldIn, currentPos, true ) ;
        notify_owner( (ServerWorld) worldIn, uuid, currentPos ) ;
      }
      return stateIn ;
    } ;
    return super.updatePostPlacement( stateIn, facing, facingState, worldIn, currentPos, facingPos );
  }
  
  @Override
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.isIn(newState.getBlock())) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof IInventory) {
        InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
        worldIn.updateComparatorOutputLevel(pos, this);
      }
      
      super.onReplaced(state, worldIn, pos, newState, isMoving);
    }
  }
  
  @Override
  public void tick( BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
    TileEntity tileentity = worldIn.getTileEntity(pos);
    if (tileentity instanceof graveEntity) {
      ((graveEntity)tileentity).barrelTick();
    }
    
  }
  
  @Nullable
  public TileEntity createNewTileEntity(IBlockReader worldIn) {
    return new graveEntity( new TranslationTextComponent( "block." + grave.modID + ".grave" ) );
  }
  
  
  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }
  
  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    // 无论是服务器还是客户端都触发
    TileEntity tileentity = worldIn.getTileEntity(pos);
    
    if ( tileentity instanceof graveEntity ) {
      graveEntity grave_entity = ((graveEntity)tileentity) ;
      if ( placer instanceof PlayerEntity ) {
        boolean sneaking = false ;
        if ( placer.isSneaking( ) ) { // 写入uuid和玩家名，标记为一个有主之墓（建造的无主之墓任何人都能开）
          grave_entity.displayName( placer.getName( ) ) ; // 写入玩家名
          grave_entity.uuid( placer.getUniqueID( ) ) ; // 写入uuid
          sneaking = true ;
        } ;
        // 仓库名
        grave_entity.setCustomName(
            new TranslationTextComponent( "txt."  + grave.modID +  ".grave", placer.getName( ).getString( ), sneaking ? new TranslationTextComponent( "block."  + grave.modID +  ".grave" ).getString( ) : new TranslationTextComponent( "txt." + grave.modID + ".g_o_n" ).getString( ) )
        ) ;
        // 自定义显示内容
        if ( stack.hasDisplayName( ) ) grave_entity.displayMsg( stack.getDisplayName( ) ) ;
        // 人为
        grave_entity.isBuild( true ) ;
        // 服务器会自动同步数据，因为是新创建的
      } ;
    }
    
  }
  
  @Override
  public boolean hasComparatorInputOverride(BlockState state) {
    return true;
  }
  
  @Override
  public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
    return Container.calcRedstone(worldIn.getTileEntity(pos));
  }
  
  
  @Override
  public BlockState rotate(BlockState state, Rotation rot) {
    return state.with( FACING, rot.rotate(state.get( FACING )) );
  }
  
  
  @Override
  public BlockState mirror(BlockState state, Mirror mirrorIn) {
    return state.rotate(mirrorIn.toRotation(state.get( FACING )));
  }

  public static boolean isWater ( World world, BlockPos pos ) {
   return world.getBlockState( pos ).getBlock( ) == Blocks.WATER && world.getFluidState( pos ).isSource( ) ;
  } ;
  
  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add( FACING ).add( WATERLOGGED, STONE ).add( LIGHT ) ;
  }
  
  @Override
  public BlockState getStateForPlacement( BlockItemUseContext context) {
    return this.getDefaultState( )
        .with( FACING, context.getPlacementHorizontalFacing( ) )
        .with( WATERLOGGED, graveBlock.isWater( context.getWorld( ), context.getPos( ) ) )
        ;
  }
  
  @Override
  public VoxelShape getShape( BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context ) {
    Direction dire = state.get( FACING ) ;
    return shapeList[ dire == Direction.WEST ? 3 : dire == Direction.EAST ? 1 : dire == Direction.SOUTH ? 2 : 0 ] ;
  }
  
  @Override
  public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
    List<ItemStack> list = new ArrayList<>( ) ;
    double drop_chance = Math.max( 0d, Math.min( config.drop_chance.get( ), 1d ) ) ;
    if ( state.hasProperty( STONE ) && state.get( STONE ) ) {
      boolean drop = true ;
      Entity entity = builder.get( LootParameters.THIS_ENTITY ) ;
      if ( entity instanceof PlayerEntity ) {
        if ( ( ( PlayerEntity ) entity ).isCreative( ) ) drop = false ;
      } ;
      if ( drop && ( drop_chance == 1 || drop_chance > 0 && Math.random( ) < drop_chance ) ) {
        list.add( new ItemStack( this.asItem( ) ) ) ;
      } ;
    } ;
    return list ;
  } ;
  
}
