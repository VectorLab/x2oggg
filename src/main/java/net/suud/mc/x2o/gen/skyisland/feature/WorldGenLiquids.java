package net.suud.mc.x2o.gen.skyisland.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenLiquids extends WorldGenerator {
	private final Block block;

	public WorldGenLiquids(Block blockIn) {
		this.block = blockIn;
	}

	public boolean generate(World worldIn, Random rand, BlockPos position) {
		if (worldIn.getBlockState(position.up()).getBlock() != Blocks.STONE) {
			return false;
		}
		IBlockState iblockstate = worldIn.getBlockState(position);

		if (!iblockstate.getBlock().isAir(iblockstate, worldIn, position) && iblockstate.getBlock() != Blocks.STONE) {
			return false;
		}
		int i = 0;

		if (worldIn.getBlockState(position.west()).getBlock() == Blocks.STONE) {
			++i;
		}

		if (worldIn.getBlockState(position.east()).getBlock() == Blocks.STONE) {
			++i;
		}

		if (worldIn.getBlockState(position.north()).getBlock() == Blocks.STONE) {
			++i;
		}

		if (worldIn.getBlockState(position.south()).getBlock() == Blocks.STONE) {
			++i;
		}

		if (worldIn.getBlockState(position.down()).getBlock() == Blocks.STONE) {
			++i;
		}

		if (4 != i) {
			return false;
		}

		int j = 0;

		if (worldIn.isAirBlock(position.west())) {
			++j;
		}

		if (worldIn.isAirBlock(position.east())) {
			++j;
		}

		if (worldIn.isAirBlock(position.north())) {
			++j;
		}

		if (worldIn.isAirBlock(position.south())) {
			++j;
		}

		if (worldIn.isAirBlock(position.down())) {
			++j;
		}

		if (1 != j) {
			return false;
		}

		// TODO fixed ?
		IBlockState iblockstate1 = this.block.getDefaultState();
		worldIn.setBlockState(position, iblockstate1, 2);
		//worldIn.immediateBlockTick(position, iblockstate1, rand);
		return true;

	}
}
