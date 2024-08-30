package net.suud.mc.x2o.gen.skyisland.structure;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeForest;
import net.minecraft.world.biome.BiomeJungle;
import net.minecraft.world.biome.BiomeMesa;
import net.minecraft.world.biome.BiomeSavanna;
import net.minecraft.world.biome.BiomeTaiga;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.suud.mc.x2o.ExampleMod;

public final class StructureMineshaftTypeRegistry {
	// Registry
	
    public static HashMap<Integer,StructureMineshaftType> type_known=new HashMap<>();
    
    public static StructureMineshaftType type_get(int p1) {
    	return type_known.get(p1);
    }
    
    public static boolean type_add(StructureMineshaftType p1) {
    	if(type_known.containsKey(p1.getId())) {
    		return false;
    	}
    	type_known.put(p1.getId(), p1);
    	return true;
    }
	
    // type
    public static final Field biome_forest_accessor=ObfuscationReflectionHelper.findField(BiomeForest.class,"field_150632_aF");

    public static StructureMineshaftType type_choose(Biome p1) {
    	if(p1 instanceof BiomeMesa) {
    		return type_get(1);
    	}
    	if(p1 instanceof BiomeForest) {
    		BiomeForest v1=(BiomeForest) p1;
    		BiomeForest.Type v2 = null;
			try {
				v2 = (BiomeForest.Type) biome_forest_accessor.get(v1);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				ExampleMod.logger.error("Failed to get BiomeForest runtime type !");
				ExampleMod.logger.catching(e);
			}
    		
    		switch(v2) {
			case BIRCH:
				return type_get(2); 
			case NORMAL:
				return type_get(0);
			case ROOFED:
				return type_get(6);
			case FLOWER:
			default:
				return type_get(1);
    		
    		}
    	}
    	if(p1 instanceof BiomeJungle) {
    		return type_get(4);
    	}
    	if(p1 instanceof BiomeSavanna) {
    		return type_get(5);
    	}
    	if(p1 instanceof BiomeTaiga) {
    		return type_get(3);
    	}
    	
    	
    	
    	
    	
    	if(Biome.TempCategory.COLD==p1.getTempCategory()) {
    		return type_get(3);
    	}
    	return type_get(0);
    }
    
    // ================
    // default entry
	static {
		boolean a=true;
		a&=type_add(new TypeNormal(0,Blocks.PLANKS.getDefaultState(),Blocks.OAK_FENCE.getDefaultState()));
		a&=type_add(new TypeMesa());
		a&=type_add(new TypeNormal(2,
				Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.BIRCH),
				Blocks.BIRCH_FENCE.getDefaultState()));
		a&=type_add(new TypeNormal(3,
				Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE),
				Blocks.SPRUCE_FENCE.getDefaultState()));
		a&=type_add(new TypeNormal(4,
				Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE),
				Blocks.JUNGLE_FENCE.getDefaultState()));
		a&=type_add(new TypeNormal(5,
				Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.ACACIA),
				Blocks.ACACIA_FENCE.getDefaultState()));
		a&=type_add(new TypeNormal(6,
				Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.DARK_OAK),
				Blocks.DARK_OAK_FENCE.getDefaultState()));
		
		
		if(!a) {
			ExampleMod.logger.error("Unable to register all Mineshaft types !");
		}
	}
	// ================
	// default implements
	public static abstract class TypeSimple implements StructureMineshaftType{

		public static final IBlockState GLASS=Blocks.GLASS.getDefaultState();
		@Override
		public IBlockState getBlockRoomFloating() {
			return GLASS;
		}

		public static final IBlockState DIRT=Blocks.DIRT.getDefaultState();
		@Override
		public IBlockState getBlockRoomGround() {
			return DIRT;
		}

		public static final IBlockState TORCH_NORTH=Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH);
		@Override
		public IBlockState getTorchNorth() {
			return TORCH_NORTH;
		}
		
		public static final IBlockState TORCH_SOUTH=Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH);
		@Override
		public IBlockState getTorchSouth() {
			return TORCH_SOUTH;
		}

		public static final ResourceLocation SPIDER=EntityList.getKey(EntityCaveSpider.class);
		@Override
		public ResourceLocation getMobSpawnerEntity() {
			return SPIDER;
		}
		
		public static final IBlockState WEB=Blocks.WEB.getDefaultState();
		@Override
		public IBlockState getBlockWeb() {
			return WEB;
		}
		
	}

	public static class TypeNormal extends TypeSimple{
		
		public final int id;
		public final IBlockState planks;
		public final IBlockState fence;
		
		public TypeNormal(int p1, IBlockState p2, IBlockState p3) {
			this.id=p1;
			this.planks = p2;
			this.fence=p3;
		}

		@Override
		public int getId() {
			return this.id;
		}

		@Override
		public IBlockState getBlockPlanks() {
			return planks;
		}

		@Override
		public IBlockState getBlockFence() {
			return this.fence;
		}

		@Override
		public int markAvailableHeight(World world, Random rand,StructureBoundingBox bb) {
/*
 * v1 original
	    	// world.getSeaLevel() => 128
			// p_75067_3_ => 10
	        int i = 128 - 10;
	        int j = bb.getYSize() + 1;

	        if (j < i)
	        {
	            j += rand.nextInt(i - j);
	        }

	        return j - bb.maxY;
// v2
			if(64<=bb.minY) {
				return bb.minY-64+rand.nextInt(32);
			}else {
				return 64-bb.minY+rand.nextInt(32);
			}
*/
			//return -bb.maxY+128-16-rand.nextInt(32);
			return 111-bb.maxY-rand.nextInt(32);
		}

	}
	
	public static class TypeMesa extends TypeSimple{

		public static final IBlockState planks=Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.DARK_OAK);
		public static final IBlockState fence=Blocks.DARK_OAK_FENCE.getDefaultState();
		
		@Override
		public int getId() {
			return 1;
		}

		@Override
		public IBlockState getBlockPlanks() {
			return planks;
		}

		@Override
		public IBlockState getBlockFence() {
			return fence;
		}

		@Override
		public int markAvailableHeight(World world, Random rand,StructureBoundingBox bb) {
			// world.getSeaLevel() => 128
			return 128 - bb.maxY + bb.getYSize() / 2 - -5;
		}
		
	}

}
