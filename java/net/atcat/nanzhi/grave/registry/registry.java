/*××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××
 × The MIT License (MIT)
 × Copyright © 2020. 南织( 1448848683@qq.com )
 ×
 × Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 ×
 × The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 ×
 × THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××*/

package net.atcat.nanzhi.grave.registry;

import com.mojang.datafixers.types.Func;
import net.atcat.nanzhi.grave.grave;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

public class registry {
    
    public static final String mid = grave.modID ;
    
    public static final DeferredRegister<Block> blockRegDef = DeferredRegister.create( ForgeRegistries.BLOCKS, mid ) ;
    public static final DeferredRegister<Item> itemRegDef = DeferredRegister.create( ForgeRegistries.ITEMS, mid ) ;
    public static final DeferredRegister<TileEntityType<?>> tileEntityRegDef = DeferredRegister.create( ForgeRegistries.TILE_ENTITIES, mid ) ;
    
    
    private static String _dn( String domain, String name ) {
        return domain + "/" + name ;
    }
    public static Block register(String name, Block block, Item.Properties properties) {
        blockRegDef.register( name, ( ) -> block ) ;
        itemRegDef.register( name, ( ) -> new BlockItem( block, properties ) );
        return block ;
    }
    public static Block register( String name, Block block, Function<Block,BlockItem> func ) {
        blockRegDef.register( name, ( ) -> block ) ;
        itemRegDef.register( name, ( ) -> func.apply( block ) );
        return block ;
    }
    
    public static Block register( String domain, String name, Block block, Item.Properties properties) {
        return register( _dn( domain, name ), block, properties ) ;
    }
    public static Item register(String name, Item item) {
        itemRegDef.register( name, ( ) -> item );
        return item ;
    }
    public static Item register( String domain, String name, Item item ) {
        return register( _dn( domain, name ), item ) ;
    }
    public static Block register( String name, BlockItem BlockItemIn ) {
        blockRegDef.register( name, BlockItemIn::getBlock ) ;
        itemRegDef.register( name, ( ) -> BlockItemIn );
        return BlockItemIn.getBlock( ) ;
    }
    public static Block register( String domain, String name, BlockItem BlockItemIn ) {
        return register( _dn( domain, name ), BlockItemIn ) ;
    }
    public static <T extends TileEntity> TileEntityType<T> register ( String key, Supplier<T> ts, Block blockIn ) {
        TileEntityType<T> te = TileEntityType.Builder.create( ts, blockIn ).build( Util.attemptDataFix( TypeReferences.BLOCK_ENTITY, key ) ) ;
        tileEntityRegDef.register( key, ( ) -> te ) ;
        return te ;
    } ;

}
