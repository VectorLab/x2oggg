package net.suud.mc.x2o.gen.skyisland.structure;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public interface StructureMineshaftType
{
	public int getId();
	public int markAvailableHeight(World world, Random rand,StructureBoundingBox bb);
	public IBlockState getBlockPlanks();
	public IBlockState getBlockFence();
	public IBlockState getBlockWeb();
	public ResourceLocation getMobSpawnerEntity();
	public IBlockState getTorchNorth();
	public IBlockState getTorchSouth();
	public IBlockState getBlockRoomGround();
	public IBlockState getBlockRoomFloating();
}
