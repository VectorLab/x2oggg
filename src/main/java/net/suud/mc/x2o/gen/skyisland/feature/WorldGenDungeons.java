package net.suud.mc.x2o.gen.skyisland.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootTableList;
import net.suud.mc.x2o.ExampleMod;

public class WorldGenDungeons extends WorldGenerator
{

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {

    	int t=0;
        for(int x=-1;x<=1;++x) {
            for(int z=-1;z<=1;++z) {
                for(int y=-1;y<=1;++y) {
                	if(Material.AIR!=worldIn.getBlockState(position.add(x,y,z)).getMaterial()) {
                		++t;
                	}
                }
            }
        }

        if(t<4) {
        	return false;
        }
        
        // internal space
        for(int x=-1;x<=1;++x) {
            for(int z=-1;z<=1;++z) {
                for(int y=-1;y<=1;++y) {
                	BlockPos v1 = position.add(x,y,z);
                	this.setBlockTransparent(worldIn,v1);
                }
            }
        }
        
        // framework
        this.setBlockSolid(worldIn,position.add(0,-2,0));
        this.setBlockSolid(worldIn,position.add(0,-2,1));
        this.setBlockSolid(worldIn,position.add(0,-2,-1));
        this.setBlockSolid(worldIn,position.add(1,-2,0));
        this.setBlockSolid(worldIn,position.add(-1,-2,0));
        
        this.setBlockSolid(worldIn,position.add(0,2,0));
        this.setBlockSolid(worldIn,position.add(0,2,1));
        this.setBlockSolid(worldIn,position.add(0,2,-1));
        this.setBlockSolid(worldIn,position.add(1,2,0));
        this.setBlockSolid(worldIn,position.add(-1,2,0));
        
        this.setBlockSolid(worldIn,position.add(2,0,0));
        this.setBlockSolid(worldIn,position.add(2,0,-1));
        this.setBlockSolid(worldIn,position.add(2,0,1));
        this.setBlockSolid(worldIn,position.add(2,-1,0));
        this.setBlockSolid(worldIn,position.add(2,1,0));
        
        this.setBlockSolid(worldIn,position.add(-2,0,0));
        this.setBlockSolid(worldIn,position.add(-2,0,-1));
        this.setBlockSolid(worldIn,position.add(-2,0,1));
        this.setBlockSolid(worldIn,position.add(-2,-1,0));
        this.setBlockSolid(worldIn,position.add(-2,1,0));
        
        this.setBlockSolid(worldIn,position.add(0,1,2));
        this.setBlockSolid(worldIn,position.add(0,-1,2));
        this.setBlockSolid(worldIn,position.add(0,0,2));
        this.setBlockSolid(worldIn,position.add(1,0,2));
        this.setBlockSolid(worldIn,position.add(-1,0,2));
        
        this.setBlockSolid(worldIn,position.add(0,1,-2));
        this.setBlockSolid(worldIn,position.add(0,-1,-2));
        this.setBlockSolid(worldIn,position.add(0,0,-2));
        this.setBlockSolid(worldIn,position.add(1,0,-2));
        this.setBlockSolid(worldIn,position.add(-1,0,-2));

        this.setChest(worldIn, rand, position.add(0, -1, 0));
        this.setMobSpawner(worldIn, rand, position);
        return true;
    }
    
    public static final IBlockState AIR=Blocks.AIR.getDefaultState();

    protected void setBlockTransparent(World p1, BlockPos p3) {
    	IBlockState v1=p1.getBlockState(p3);
    	if(v1.getMaterial() == Material.AIR) {
    		return;
    	}
    	p1.setBlockState(p3,AIR,2);
    }
    
    public static final IBlockState STONEBRICK=Blocks.STONEBRICK.getDefaultState();
    public static final IBlockState STONE_ANDESITE_SMOOTH=Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH);
    public static final IBlockState STONE_DIORITE_SMOOTH=Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH);
    public static final IBlockState STONE_GRANITE_SMOOTH=Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE_SMOOTH);
    public static final IBlockState DOUBLE_STONE_SLAB_SMOOTHBRICK=Blocks.DOUBLE_STONE_SLAB.getDefaultState().withProperty(BlockStoneSlab.VARIANT,BlockStoneSlab.EnumType.SMOOTHBRICK);
    public static final IBlockState GLASS=Blocks.GLASS.getDefaultState();
    
    protected void setBlockSolid(World p1, BlockPos p3) {
    	IBlockState v1=p1.getBlockState(p3);
    	Block v3=v1.getBlock();
    	if(v1.getMaterial() == Material.AIR) {
    		p1.setBlockState(p3,GLASS, 2);
    		return;
    	}
    	if(Blocks.STONE==v3) {
    		IBlockState v2;
    		switch(v1.getValue(BlockStone.VARIANT)) {
			case ANDESITE:
				v2=STONE_ANDESITE_SMOOTH;
				break;
			case DIORITE:
				v2=STONE_DIORITE_SMOOTH;
				break;
			case GRANITE:
				v2=STONE_GRANITE_SMOOTH;
				break;
			case STONE:
				v2=DOUBLE_STONE_SLAB_SMOOTHBRICK;
				break;
			case GRANITE_SMOOTH:
			case DIORITE_SMOOTH:
			case ANDESITE_SMOOTH:
			default:
				v2=STONEBRICK;
				break;
    		}
    		p1.setBlockState(p3,v2, 2);
    		return;
    	}
    	if(Blocks.CHEST==v3) {
    		return;
    	}
    	if(v1.isFullBlock()) {
    		p1.setBlockState(p3,STONEBRICK, 2);
    		return;
    	}
    	// any others
    	p1.setBlockState(p3,GLASS, 2);
    }
    
    public static final IBlockState CHEST=Blocks.CHEST.getDefaultState();
    
    protected void setChest(World worldIn, Random rand, BlockPos blockpos2) {
    	worldIn.setBlockState(blockpos2,AIR,2);
        worldIn.setBlockState(blockpos2, Blocks.CHEST.correctFacing(worldIn, blockpos2,CHEST ), 2);
        TileEntity tileentity1 = worldIn.getTileEntity(blockpos2);

        if (tileentity1 instanceof TileEntityChest)
        {
            ((TileEntityChest)tileentity1).setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, rand.nextLong());
        }
    }
    
    public static final IBlockState MOB_SPAWNER=Blocks.MOB_SPAWNER.getDefaultState();
    
    protected void setMobSpawner(World worldIn, Random rand, BlockPos position) {
        worldIn.setBlockState(position, MOB_SPAWNER, 2);
        TileEntity tileentity = worldIn.getTileEntity(position);

        if (tileentity instanceof TileEntityMobSpawner)
        {
            ((TileEntityMobSpawner)tileentity).getSpawnerBaseLogic().setEntityId(net.minecraftforge.common.DungeonHooks.getRandomDungeonMob(rand));
        }
        else
        {
            ExampleMod.logger.error("Failed to fetch mob spawner entity at ({}, {}, {})", Integer.valueOf(position.getX()), Integer.valueOf(position.getY()), Integer.valueOf(position.getZ()));
        }
    }

}
