package net.suud.mc.x2o.gen.skyisland.structure;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureStart;

public class MapGenMineshaft extends MapGenStructure
{
	
	public static void load(String p1) {
		String v1=p1+"Ms";
		MapGenStructureIO.registerStructure(StructureMineshaftStart.class, v1+"S");
		StructureMineshaftPieces.load(v1);
	}
	
    private double chance = 0.004D;

    public String getStructureName()
    {
        return "Mineshaft";
    }

    public MapGenMineshaft(double chanceIn)
    {
        this.chance=chanceIn;
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
/*
    	if(Biome.TempCategory.OCEAN==this.generator.biomesForPopulation[0b01110111].getTempCategory()){
    		return false;
    	}

    	if(Biome.TempCategory.OCEAN==this.world.getBiome(new BlockPos((chunkX << 4) | 8, 64, (chunkZ << 4) | 8)).getTempCategory()) {
    		return false;
    	}
*/
        return this.rand.nextDouble() < this.chance && this.rand.nextInt(80) < Math.max(Math.abs(chunkX), Math.abs(chunkZ));
    }

    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored)
    {
        int j = pos.getX() >> 4;
        int k = pos.getZ() >> 4;

        for (int l = 0; l <= 1000; ++l)
        {
            for (int i1 = -l; i1 <= l; ++i1)
            {
                boolean flag = i1 == -l || i1 == l;

                for (int j1 = -l; j1 <= l; ++j1)
                {
                    boolean flag1 = j1 == -l || j1 == l;

                    if (flag || flag1)
                    {
                        int k1 = j + i1;
                        int l1 = k + j1;
                        this.rand.setSeed((long)(k1 ^ l1) ^ worldIn.getSeed());
                        this.rand.nextInt();

                        if (this.canSpawnStructureAtCoords(k1, l1) && (!findUnexplored || !worldIn.isChunkGeneratedAt(k1, l1)))
                        {
                            return new BlockPos((k1 << 4) + 8, 64, (l1 << 4) + 8);
                        }
                    }
                }
            }
        }

        return null;
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        Biome biome = this.world.getBiome(new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8));
        
        StructureMineshaftType mapgenmineshaft$type = StructureMineshaftTypeRegistry.type_choose(biome);
        
        return new StructureMineshaftStart(this.world, this.rand, chunkX, chunkZ, mapgenmineshaft$type);
    }

}
