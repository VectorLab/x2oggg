package net.suud.mc.x2o.gen.skyisland.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

public class MapGenScatteredFeature extends MapGenStructure
{
    private static final List<Biome> BIOMELIST = Arrays.<Biome>asList(Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.SWAMPLAND, Biomes.ICE_PLAINS, Biomes.COLD_TAIGA);
    private final List<Biome.SpawnListEntry> monsters;
    private int maxDistanceBetweenScatteredFeatures;
    
    public static void load(String p1){
    	String v1=p1+"Te";
    	MapGenStructureIO.registerStructure(Start.class,v1+"S");
    	ComponentScatteredFeaturePieces.load(v1);
    }
    
    public MapGenScatteredFeature()
    {
        this.monsters = Lists.<Biome.SpawnListEntry>newArrayList();
        this.maxDistanceBetweenScatteredFeatures = 32;
        this.monsters.add(new Biome.SpawnListEntry(EntityWitch.class, 1, 1, 1));
    }

    public MapGenScatteredFeature(Map<String, String> p_i2061_1_)
    {
        this();

        for (Entry<String, String> entry : p_i2061_1_.entrySet())
        {
            if (((String)entry.getKey()).equals("distance"))
            {
                this.maxDistanceBetweenScatteredFeatures = MathHelper.getInt(entry.getValue(), this.maxDistanceBetweenScatteredFeatures, 9);
            }
        }
    }

    public String getStructureName()
    {
        return "Temple";
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        if (chunkZ < 0)
        {
            chunkZ -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        int k = chunkX / this.maxDistanceBetweenScatteredFeatures;
        int l = chunkZ / this.maxDistanceBetweenScatteredFeatures;
        Random random = this.world.setRandomSeed(k, l, 14357617);
        k = k * this.maxDistanceBetweenScatteredFeatures;
        l = l * this.maxDistanceBetweenScatteredFeatures;
        k = k + random.nextInt(this.maxDistanceBetweenScatteredFeatures - 8);
        l = l + random.nextInt(this.maxDistanceBetweenScatteredFeatures - 8);

        if (i == k && j == l)
        {
            Biome biome = this.world.getBiomeProvider().getBiome(new BlockPos(i * 16 + 8, 0, j * 16 + 8));

            if (biome == null)
            {
                return false;
            }

            for (Biome biome1 : BIOMELIST)
            {
                if (biome == biome1)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored)
    {
        this.world = worldIn;
        return findNearestStructurePosBySpacing(worldIn, this, pos, this.maxDistanceBetweenScatteredFeatures, 8, 14357617, false, 100, findUnexplored);
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new MapGenScatteredFeature.Start(this.world, this.rand, chunkX, chunkZ);
    }

    public boolean isSwampHut(BlockPos pos)
    {
        StructureStart structurestart = this.getStructureAt(pos);

        if (structurestart != null && structurestart instanceof MapGenScatteredFeature.Start && !structurestart.getComponents().isEmpty())
        {
            StructureComponent structurecomponent = structurestart.getComponents().get(0);
            return structurecomponent instanceof ComponentScatteredFeaturePieces.SwampHut;
        }
        else
        {
            return false;
        }
    }

    public List<Biome.SpawnListEntry> getMonsters()
    {
        return this.monsters;
    }

    public static class Start extends StructureStart
        {
            public Start()
            {
            }

            public Start(World worldIn, Random random, int chunkX, int chunkZ)
            {
                this(worldIn, random, chunkX, chunkZ, worldIn.getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8)));
            }

            public Start(World worldIn, Random random, int chunkX, int chunkZ, Biome biomeIn)
            {
                super(chunkX, chunkZ);

                if (biomeIn != Biomes.JUNGLE && biomeIn != Biomes.JUNGLE_HILLS)
                {
                    if (biomeIn == Biomes.SWAMPLAND)
                    {
                    	// Biomes.SWAMPLAND
                        ComponentScatteredFeaturePieces.SwampHut componentscatteredfeaturepieces$swamphut = new ComponentScatteredFeaturePieces.SwampHut(random, chunkX * 16, chunkZ * 16);
                        this.components.add(componentscatteredfeaturepieces$swamphut);
                    }
                    else if (biomeIn != Biomes.DESERT && biomeIn != Biomes.DESERT_HILLS)
                    {
                        if (biomeIn == Biomes.ICE_PLAINS || biomeIn == Biomes.COLD_TAIGA)
                        {
                        	// Biomes.ICE_PLAINS Biomes.COLD_TAIGA
                            ComponentScatteredFeaturePieces.Igloo componentscatteredfeaturepieces$igloo = new ComponentScatteredFeaturePieces.Igloo(random, chunkX * 16, chunkZ * 16);
                            this.components.add(componentscatteredfeaturepieces$igloo);
                        }
                    }
                    else
                    {
                    	// Biomes.DESERT Biomes.DESERT_HILLS
                        ComponentScatteredFeaturePieces.DesertPyramid componentscatteredfeaturepieces$desertpyramid = new ComponentScatteredFeaturePieces.DesertPyramid(random, chunkX * 16, chunkZ * 16);
                        this.components.add(componentscatteredfeaturepieces$desertpyramid);
                    }
                }
                else
                {
                	// Biomes.JUNGLE Biomes.JUNGLE_HILLS
                    ComponentScatteredFeaturePieces.JunglePyramid componentscatteredfeaturepieces$junglepyramid = new ComponentScatteredFeaturePieces.JunglePyramid(random, chunkX * 16, chunkZ * 16);
                    this.components.add(componentscatteredfeaturepieces$junglepyramid);
                }

                this.updateBoundingBox();
            }
        }
}

