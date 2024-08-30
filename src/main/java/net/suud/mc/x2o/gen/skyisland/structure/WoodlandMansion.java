package net.suud.mc.x2o.gen.skyisland.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.WoodlandMansionPieces;
import net.suud.mc.x2o.ExampleMod;
import net.suud.mc.x2o.gen.skyisland.ChunkGeneratorX2oSkyIsland;

public class WoodlandMansion extends MapGenStructure
{
    public static final List<Biome> ALLOWED_BIOMES = Arrays.<Biome>asList(Biomes.ROOFED_FOREST, Biomes.MUTATED_ROOFED_FOREST);
    private final ChunkGeneratorX2oSkyIsland provider;
    
    public static void load(String p1) {
    	MapGenStructureIO.registerStructure(Start.class, p1+"MaS");
    }

    public WoodlandMansion(ChunkGeneratorX2oSkyIsland providerIn)
    {
        this.provider = providerIn;
    }

    public String getStructureName()
    {
        return "Mansion";
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0)
        {
            i = chunkX - 79;
        }

        if (chunkZ < 0)
        {
            j = chunkZ - 79;
        }

        int k = i / 80;
        int l = j / 80;
        Random random = this.world.setRandomSeed(k, l, 10387319);
        k = k * 80;
        l = l * 80;
        k = k + (random.nextInt(60) + random.nextInt(60)) / 2;
        l = l + (random.nextInt(60) + random.nextInt(60)) / 2;

        if (chunkX == k && chunkZ == l)
        {
            boolean flag = this.world.getBiomeProvider().areBiomesViable(chunkX * 16 + 8, chunkZ * 16 + 8, 32, ALLOWED_BIOMES);

            if (flag)
            {
                return true;
            }
        }

        return false;
    }

    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored)
    {
        this.world = worldIn;
        BiomeProvider biomeprovider = worldIn.getBiomeProvider();
        return biomeprovider.isFixedBiome() && biomeprovider.getFixedBiome() != Biomes.ROOFED_FOREST ? null : findNearestStructurePosBySpacing(worldIn, this, pos, 80, 20, 10387319, true, 100, findUnexplored);
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new WoodlandMansion.Start(this.world, this.provider, this.rand, chunkX, chunkZ);
    }

    public static class Start extends StructureStart
        {
            private boolean isValid;

            public Start()
            {
            }

            public Start(World p_i47235_1_, ChunkGeneratorX2oSkyIsland p_i47235_2_, Random p_i47235_3_, int p_i47235_4_, int p_i47235_5_)
            {
                super(p_i47235_4_, p_i47235_5_);
                this.create(p_i47235_1_, p_i47235_2_, p_i47235_3_, p_i47235_4_, p_i47235_5_);
                if(!this.isValid) {
                	ExampleMod.logger.warn("Structure place find failed, structure: {} , world : {} , chunkX : {} , chunkZ : {}",this.getClass().toString(),p_i47235_2_.dim,p_i47235_4_,p_i47235_5_);
                }
            }

            private void create(World world, ChunkGeneratorX2oSkyIsland generator, Random rand, int chunkX, int chunkZ)
            {
                Rotation rotation = Rotation.values()[rand.nextInt(Rotation.values().length)];
                ChunkPrimer chunkprimer = new ChunkPrimer();
                generator.setBlocksInChunk(chunkX, chunkZ, chunkprimer);
                int i = 5;
                int j = 5;

                if (rotation == Rotation.CLOCKWISE_90)
                {
                    i = -5;
                }
                else if (rotation == Rotation.CLOCKWISE_180)
                {
                    i = -5;
                    j = -5;
                }
                else if (rotation == Rotation.COUNTERCLOCKWISE_90)
                {
                    j = -5;
                }

                int k = chunkprimer.findGroundBlockIdx(7, 7);
                int l = chunkprimer.findGroundBlockIdx(7, 7 + j);
                int i1 = chunkprimer.findGroundBlockIdx(7 + i, 7);
                int j1 = chunkprimer.findGroundBlockIdx(7 + i, 7 + j);
                
                int k2=Math.max(Math.max(k, l), Math.max(i1, j1));
                if (k2<64)
                {
                    this.isValid = false;
                    return;
                }
                
                int k1 = Math.min(Math.min(k, l), Math.min(i1, j1));
                if(k1>191) {
                    this.isValid = false;
                    return;
                }
                
                int k3=0;
                {
                	int k4=0;
                	int k5=0;
                	int k6=0;
                	if(k>=64&&k<192) {
                		k5+=k;
                		++k6;
                	}
                	if(l>=64&&l<192) {
                		k5+=l;
                		++k6;
                	}
                	if(i1>=64&&i1<192) {
                		k5+=i1;
                		++k6;
                	}
                	if(j1>=64&&j1<192) {
                		k5+=j1;
                		++k6;
                	}
                	k4=k5/k6;
                	k5=0;
                	k6=65536;
                	if(Math.abs(k-k4)<k6) {
                		k5=k;
                		k6=Math.abs(k-k4);
                	}
                	if(Math.abs(l-k4)<k6) {
                		k5=l;
                		k6=Math.abs(l-k4);
                	}
                	if(Math.abs(i1-k4)<k6) {
                		k5=i1;
                		k6=Math.abs(i1-k4);
                	}
                	if(Math.abs(j1-k4)<k6) {
                		k5=j1;
                		k6=Math.abs(j1-k4);
                	}
                	k3=k5;
                }

                    BlockPos blockpos = new BlockPos(chunkX * 16 + 8, k3 + 1, chunkZ * 16 + 8);
                    List<WoodlandMansionPieces.MansionTemplate> list = Lists.<WoodlandMansionPieces.MansionTemplate>newLinkedList();
                    WoodlandMansionPieces.generateMansion(world.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, list, rand);
                    this.components.addAll(list);
                    this.updateBoundingBox();
                    this.isValid = true;
            }

            public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb)
            {
                super.generateStructure(worldIn, rand, structurebb);
                int i = this.boundingBox.minY;

                for (int j = structurebb.minX; j <= structurebb.maxX; ++j)
                {
                    for (int k = structurebb.minZ; k <= structurebb.maxZ; ++k)
                    {
                        BlockPos blockpos = new BlockPos(j, i, k);

                        if (!worldIn.isAirBlock(blockpos) && this.boundingBox.isVecInside(blockpos))
                        {
                            boolean flag = false;

                            for (StructureComponent structurecomponent : this.components)
                            {
                                if (structurecomponent.getBoundingBox().isVecInside(blockpos))
                                {
                                    flag = true;
                                    break;
                                }
                            }

                            if (flag)
                            {
                                for (int l = i - 1; l > 1; --l)
                                {
                                    BlockPos blockpos1 = new BlockPos(j, l, k);

                                    if (!worldIn.isAirBlock(blockpos1) && !worldIn.getBlockState(blockpos1).getMaterial().isLiquid())
                                    {
                                        break;
                                    }

                                    worldIn.setBlockState(blockpos1, Blocks.COBBLESTONE.getDefaultState(), 2);
                                }
                            }
                        }
                    }
                }
            }

            public boolean isSizeableStructure()
            {
                return this.isValid;
            }
        }
}

