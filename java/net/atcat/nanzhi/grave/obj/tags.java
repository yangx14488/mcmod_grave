package net.atcat.nanzhi.grave.obj;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;

public class tags {
    public static final res dirt = new res( "forge", "dirt" ) ;
    public static final res stone = new res( "forge", "stone" ) ;
    public static final res sand = new res( "minecraft", "sand" ) ;

    public static class res {
        public ResourceLocation $;
        public res ( String namespaceIn, String pathIn ) {
            this.$ = new ResourceLocation( namespaceIn, pathIn ) ;
        } ;
        public boolean contains ( Block block ) {
            return BlockTags.getCollection( ).get( this.$ ).contains( block ) ;
        }
        public boolean contains ( BlockState state ) {
            return BlockTags.getCollection( ).get( this.$ ).contains( state.getBlock( ) ) ;
        }
    }

}
