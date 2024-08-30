package net.suud.mc.x2o.gen.skyisland;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
//import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.TempCategory;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.IChunkGenerator;
//import net.minecraft.world.gen.MapGenBase;
//import net.minecraft.world.gen.MapGenCaves;
//import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenLakes;
//import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.suud.mc.x2o.ExampleMod;
import net.suud.mc.x2o.gen.skyisland.feature.WorldGenDungeons;
import net.suud.mc.x2o.gen.skyisland.feature.WorldGenLiquids;
import net.suud.mc.x2o.gen.skyisland.structure.MapGenMineshaft;
import net.suud.mc.x2o.gen.skyisland.structure.MapGenScatteredFeature;
//import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.suud.mc.x2o.gen.skyisland.structure.WoodlandMansion;

public class ChunkGeneratorX2oSkyIsland implements IChunkGenerator {
/**
 * WARNING: NEVER call this.world.setBlockToAir()
 */
    protected final Random rand;
//    protected NoiseGeneratorOctaves minLimitPerlinNoise;
//    protected NoiseGeneratorOctaves maxLimitPerlinNoise;
//    protected NoiseGeneratorOctaves mainPerlinNoise;
    protected NoiseGeneratorPerlin surfaceNoise;
//    protected NoiseGeneratorOctaves scaleNoise;
//    protected NoiseGeneratorOctaves depthNoise;
//    protected NoiseGeneratorOctaves forestNoise;
    public final World world;
    public final int dim;
    public final boolean mapFeaturesEnabled;
//    protected final WorldType terrainType;
//    protected final double[] heightMap;
//    protected final float[] biomeWeights;
    protected ChunkGeneratorSettings settings;
//    protected IBlockState oceanBlock = Blocks.WATER.getDefaultState();
    protected double[] depthBuffer = new double[256];
//    protected MapGenBase caveGenerator = new MapGenCaves();
//    protected MapGenStronghold strongholdGenerator = new MapGenStronghold();
//    protected MapGenVillage villageGenerator;
    protected MapGenMineshaft mineshaftGenerator;
    // Temples
    protected MapGenScatteredFeature scatteredFeatureGenerator;
//    protected MapGenBase ravineGenerator = new MapGenRavine();
//    protected StructureOceanMonument oceanMonumentGenerator = new StructureOceanMonument();
    protected WoodlandMansion woodlandMansionGenerator;
    public Biome[] biomesForGeneration=null;
    protected double[] mainNoiseRegion;
    protected double[] minLimitRegion;
    protected double[] maxLimitRegion;
    protected double[] depthRegion;
	
    // dimension
    public static final Field reobf_worldinfo_dim=ObfuscationReflectionHelper.findField(WorldInfo.class,"field_76105_j");

    public ChunkGeneratorX2oSkyIsland(World worldIn, long seed, boolean mapFeaturesEnabledIn, String generatorOptions){

        if (generatorOptions != null)
        {
            this.settings = ChunkGeneratorSettings.Factory.jsonToFactory(generatorOptions).build();
//            this.oceanBlock = this.settings.useLavaOceans ? Blocks.LAVA.getDefaultState() : Blocks.WATER.getDefaultState();
            worldIn.setSeaLevel(this.settings.seaLevel+64);
        }

    	{
/*
     		if(this.settings.useVillages) {
    			villageGenerator=new MapGenVillage();
    		}
*/
    		if(this.settings.useMineShafts) {
    			mineshaftGenerator = new MapGenMineshaft(0.004D);
    		}
    		if(this.settings.useTemples) {
    			scatteredFeatureGenerator = new MapGenScatteredFeature();
    		}
    		if(this.settings.useMansions) {
    			woodlandMansionGenerator = new WoodlandMansion(this);
    		}
    		
//        caveGenerator = net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(caveGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.CAVE);
//        strongholdGenerator = (MapGenStronghold)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(strongholdGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.STRONGHOLD);
//        villageGenerator = (MapGenVillage)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(villageGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.VILLAGE);
        mineshaftGenerator = (MapGenMineshaft)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(mineshaftGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.MINESHAFT);
        scatteredFeatureGenerator = (MapGenScatteredFeature)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(scatteredFeatureGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.SCATTERED_FEATURE);
//        ravineGenerator = net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(ravineGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.RAVINE);
//        oceanMonumentGenerator = (StructureOceanMonument)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(oceanMonumentGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.OCEAN_MONUMENT);
        woodlandMansionGenerator = (WoodlandMansion)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(woodlandMansionGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.WOODLAND_MANSION);
    };
    this.world = worldIn;
    int dim=-1;
    {
    	WorldInfo t1 = this.world.getWorldInfo();
    	try {
			dim=reobf_worldinfo_dim.getInt(t1);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			ExampleMod.logger.error("failed to detect dimension id, is SecurityManager enabled or World has been proxied ?");
			ExampleMod.logger.catching(e);
		}
    	
    };
    this.dim=dim;
    this.mapFeaturesEnabled = mapFeaturesEnabledIn;
//    this.terrainType = worldIn.getWorldInfo().getTerrainType();
    this.rand = new Random(seed);
/*
    this.minLimitPerlinNoise = new NoiseGeneratorOctaves(this.rand, 16);
    this.maxLimitPerlinNoise = new NoiseGeneratorOctaves(this.rand, 16);
    this.mainPerlinNoise = new NoiseGeneratorOctaves(this.rand, 8);
    this.surfaceNoise = new NoiseGeneratorPerlin(this.rand, 4);
    this.scaleNoise = new NoiseGeneratorOctaves(this.rand, 10);
    this.depthNoise = new NoiseGeneratorOctaves(this.rand, 16);
    this.forestNoise = new NoiseGeneratorOctaves(this.rand, 8);

    this.heightMap = new double[825];
    this.biomeWeights = new float[25];

    for (int i = -2; i <= 2; ++i)
    {
        for (int j = -2; j <= 2; ++j)
        {
            float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
            this.biomeWeights[i + 2 + (j + 2) * 5] = f;
        }
    }

    net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextOverworld ctx =
            new net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextOverworld(minLimitPerlinNoise, maxLimitPerlinNoise, mainPerlinNoise, surfaceNoise, scaleNoise, depthNoise, forestNoise);
    ctx = net.minecraftforge.event.terraingen.TerrainGen.getModdedNoiseGenerators(worldIn, this.rand, ctx);
    this.minLimitPerlinNoise = ctx.getLPerlin1();
    this.maxLimitPerlinNoise = ctx.getLPerlin2();
    this.mainPerlinNoise = ctx.getPerlin();
    this.surfaceNoise = ctx.getHeight();
    this.scaleNoise = ctx.getScale();
    this.depthNoise = ctx.getDepth();
    this.forestNoise = ctx.getForest();
*/
    this.surfaceNoise = new NoiseGeneratorPerlin(this.rand, 4);
    // TODO fix this.noiseBase octaves
    this.noiseBase=new NoiseGeneratorOctaves(this.rand,8);
}

public void replaceBiomeBlocks(int x, int z, ChunkPrimer primer, Biome[] biomesIn)
{
    if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, x, z, primer, this.world)) return;
    this.depthBuffer = this.surfaceNoise.getRegion(this.depthBuffer, (double)(x * 16), (double)(z * 16), 16, 16, 0.0625D, 0.0625D, 1.0D);

    for (int i = 0; i < 16; ++i)
    {
        for (int j = 0; j < 16; ++j)
        {
            Biome biome = biomesIn[j|(i<<4)];
            biome.genTerrainBlocks(this.world, this.rand, primer, x * 16 + i, z * 16 + j, this.depthBuffer[j + i * 16]);
            
            for(int y=4;y>=0;--y) {
            	if(Blocks.BEDROCK==primer.getBlockState(i, y, j).getBlock()) {
            		primer.setBlockState(i, y, j, AIR);
            	}
            }
        }
    }
}

public void populate(int cx, int cz)
{
	try {
	//debug(cx,cz,1);
    BlockFalling.fallInstantly = true;
    final int sx = cx<<4;
    final int sz = cz<<4;
    final BlockPos blockpos = new BlockPos(sx, 0, sz);
    final Biome biome = this.world.getBiome(blockpos.add(16, 0, 16));
    this.rand.setSeed(this.world.getSeed());
    long k = this.rand.nextLong() / 2L * 2L + 1L;
    long l = this.rand.nextLong() / 2L * 2L + 1L;
    this.rand.setSeed((long)cx * k + (long)cz * l ^ this.world.getSeed());
    boolean flag = false;
    final ChunkPos chunkpos = new ChunkPos(cx, cz);

    net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.world, this.rand, cx, cz, flag);

    if (this.mapFeaturesEnabled)
    {
        if (this.settings.useMineShafts)
        {
            this.mineshaftGenerator.generateStructure(this.world, this.rand, chunkpos);
        }
/*
        if (this.settings.useVillages)
        {
            flag = this.villageGenerator.generateStructure(this.world, this.rand, chunkpos);
        }

        if (this.settings.useStrongholds)
        {
            this.strongholdGenerator.generateStructure(this.world, this.rand, chunkpos);
        }
*/
        if (this.settings.useTemples)
        {
            this.scatteredFeatureGenerator.generateStructure(this.world, this.rand, chunkpos);
        }

/*        if (this.settings.useMonuments)
        {
            this.oceanMonumentGenerator.generateStructure(this.world, this.rand, chunkpos);
        }*/

        if (this.settings.useMansions)
        {
            this.woodlandMansionGenerator.generateStructure(this.world, this.rand, chunkpos);
        }
    }

    if (biome != Biomes.DESERT && biome != Biomes.DESERT_HILLS && this.settings.useWaterLakes && !flag && this.rand.nextInt(this.settings.waterLakeChance) == 0)
    if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.rand, cx, cz, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAKE))
    {
        int i1 = this.rand.nextInt(16) + 8;
        int j1 = this.rand.nextInt(128)+64;
        int k1 = this.rand.nextInt(16) + 8;
        (new WorldGenLakes(Blocks.WATER)).generate(this.world, this.rand, blockpos.add(i1, j1, k1));
    }

    if (!flag && this.rand.nextInt(this.settings.lavaLakeChance / 10) == 0 && this.settings.useLavaLakes)
    if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.rand, cx, cz, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAVA))
    {
        int i2 = this.rand.nextInt(16) + 8;
        int l2 = this.rand.nextInt(this.rand.nextInt(64) + 64);
        int k3 = this.rand.nextInt(16) + 8;

        if (l2 < this.world.getSeaLevel() || this.rand.nextInt(this.settings.lavaLakeChance / 8) == 0)
        {
            (new WorldGenLakes(Blocks.LAVA)).generate(this.world, this.rand, blockpos.add(i2, l2, k3));
        }
    }

    if (this.settings.useDungeons)
    if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.rand, cx, cz, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.DUNGEON))
    {
    	// TODO fix too much dungeon
        //for (int j2 = 0; j2 < this.settings.dungeonChance; ++j2)
    	//boolean t1=(1.0D/((double)(this.settings.dungeonChance)))<this.rand.nextDouble();
    	if((1.0D/((double)(this.settings.dungeonChance)))<this.rand.nextDouble())
        {
            int i3 = this.rand.nextInt(16) + 8;
            int l3 = this.rand.nextInt(64)+64;
            int l1 = this.rand.nextInt(16) + 8;
            (new WorldGenDungeons()).generate(this.world, this.rand, blockpos.add(i3, l3, l1));
        }
    }

    boolean genFalls=biome.decorator.generateFalls;
//    BlockPos tmp2 = new BlockPos(i, 0, j);
    biome.decorator.generateFalls=false;
    biome.decorate(this.world, this.rand,blockpos);
    biome.decorator.generateFalls=genFalls;

    if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.rand, cx, cz, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ANIMALS))
    WorldEntitySpawner.performWorldGenSpawning(this.world, biome, sx + 8, sz + 8, 16, 16, this.rand);
    BlockPos blockpos4 = blockpos.add(8, 0, 8);

    if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.rand, cx, cz, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ICE))
    {
    for (int gx = 0; gx < 16; ++gx)
    {
        for (int gz = 0; gz < 16; ++gz)
        {
            BlockPos blockpos1 = this.world.getPrecipitationHeight(blockpos4.add(gx, 0, gz));
            BlockPos blockpos2 = blockpos1.down();
            if(64>blockpos2.getY()) {
            	continue;
            }

            if (this.world.canBlockFreezeWater(blockpos2))
            {
                this.world.setBlockState(blockpos2, ICE, 2);
            }

            if (this.world.canSnowAt(blockpos1, true))
            {
                this.world.setBlockState(blockpos1, SNOW_LAYER, 2);
            }
        }
    }
    }//Forge: End ICE

//    if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.rand, cx, cz, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.CUSTOM))
    {
    	int gx,gy,gz;
    	boolean ga=false;

    	for(gx=0;gx<16;++gx) {
        	for(gz=0;gz<16;++gz) {

            	for(gy=0;gy<64;++gy) {
            		BlockPos t1=new BlockPos(gx|sx,gy,gz|sz);
            		// patch 2 : flag 16 : fix inflate generate
            		this.world.setBlockState(t1,AIR, 2|16);
            	}

            	for(gy=64;gy<256;++gy) {
            		BlockPos t1=new BlockPos(gx|sx,gy,gz|sz);
            		IBlockState gb = this.world.getBlockState(t1);
            		Block gc=gb.getBlock();
            		if(gc instanceof BlockFalling) {
            			if(ga) {
            				//BlockFalling gd=(BlockFalling) gc;
            				//gd.updateTick(this.world,new BlockPos(gx|sx,gy,gz|sz), gb, this.rand);
            				
            				// patch 2 : flag 16 : fix inflate generate
            				this.world.setBlockState(t1,AIR,2|16);
            			}
            		}else{
            			ga=(Material.AIR==gb.getMaterial());
            		}
            	}

        	}
    	}
    	// finish cleanup
    }

    if (genFalls)
    {
    	Random random=this.rand;
    	World worldIn=this.world;
    	net.minecraft.util.math.ChunkPos forgeChunkPos = new net.minecraft.util.math.ChunkPos(blockpos);

        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(worldIn, random, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.LAKE_WATER))
        for (int k5 = 0; k5 < 4; ++k5)
        {
            int i10 = random.nextInt(16) + 8;
            int l13 = random.nextInt(16) + 8;

                //int k19 = random.nextInt(random.nextInt(128)+1)+64;
            int k19 = random.nextInt(32)+144;
                BlockPos blockpos6 = blockpos.add(i10, k19, l13);

                (new WorldGenLiquids(Blocks.FLOWING_WATER)).generate(worldIn, random, blockpos6);
        }

        if(net.minecraftforge.event.terraingen.TerrainGen.decorate(worldIn, random, forgeChunkPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.LAKE_LAVA))
//        for (int l5 = 0; l5 < 20; ++l5)
//        	if(0==this.rand.nextInt(2))
        {
            int j10 = random.nextInt(16) + 8;
            int i14 = random.nextInt(16) + 8;
            int j17 = random.nextInt(32)+80;
            BlockPos blockpos3 = blockpos.add(j10, j17, i14);
            (new WorldGenLiquids(Blocks.FLOWING_LAVA)).generate(worldIn, random, blockpos3);
        }

    }

    net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.world, this.rand, cx, cz, flag);

    BlockFalling.fallInstantly = false;
    //debug(cx,cz,-1);
	}catch(Throwable e) {
		System.err.print("c 1 : ");
		System.err.print(e.getMessage());
		e.printStackTrace(System.err);
		System.err.flush();
		throw e;
	}
}

public boolean generateStructures(Chunk chunkIn, int x, int z)
{
	return false;
/*    boolean flag = false;

    if (this.settings.useMonuments && this.mapFeaturesEnabled && chunkIn.getInhabitedTime() < 3600L)
    {
        flag |= this.oceanMonumentGenerator.generateStructure(this.world, this.rand, new ChunkPos(x, z));
    }

    return flag;*/
}

public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
{
    Biome biome = this.world.getBiome(pos);

    if (this.mapFeaturesEnabled)
    {
        if (creatureType == EnumCreatureType.MONSTER && this.scatteredFeatureGenerator.isSwampHut(pos))
        {
            return this.scatteredFeatureGenerator.getMonsters();
        }

/*        if (creatureType == EnumCreatureType.MONSTER && this.settings.useMonuments && this.oceanMonumentGenerator.isPositionInStructure(this.world, pos))
        {
            return this.oceanMonumentGenerator.getMonsters();
        }*/
    }

    return biome.getSpawnableList(creatureType);
}

public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos)
{
    if (!this.mapFeaturesEnabled){
        return false;
    }
    switch(structureName) {
    case "Mansion":
    	return this.woodlandMansionGenerator != null&&this.woodlandMansionGenerator.isInsideStructure(pos);
    case "Mineshaft":
    	return this.mineshaftGenerator != null&&this.mineshaftGenerator.isInsideStructure(pos);
    case "Temple":
    	return this.scatteredFeatureGenerator != null&&this.scatteredFeatureGenerator.isInsideStructure(pos);
    default:
    	return false;
/*
    case "Village":
    	return this.villageGenerator != null&&this.villageGenerator.isInsideStructure(pos);
*/
    }
/*
 * collected unused
 *     else if ("Stronghold".equals(structureName) && this.strongholdGenerator != null)
    {
        return this.strongholdGenerator.isInsideStructure(pos);
    } else if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null)
    {
        return this.oceanMonumentGenerator.isInsideStructure(pos);
    }
    */
}

@Nullable
public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored)
{
    if (!this.mapFeaturesEnabled){
        return null;
    }

    switch(structureName) {
    case "Mansion":
    	return this.woodlandMansionGenerator==null?null:this.woodlandMansionGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
    case "Mineshaft":
    	return this.mineshaftGenerator==null?null:this.mineshaftGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
    case "Temple":
    	return this.scatteredFeatureGenerator==null?null:this.scatteredFeatureGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
    default:
    	return null;
/*
    case "Village":
    	return this.villageGenerator==null?null:this.villageGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
 */
    }
    
/*   
    else if ("Stronghold".equals(structureName) && this.strongholdGenerator != null)
    {
        return this.strongholdGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
    }
    if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null)
    {
        return this.oceanMonumentGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
    }
*/
}

public void recreateStructures(Chunk chunkIn, int x, int z)
{
    if (!this.mapFeaturesEnabled){
        return;
    }
        if (null!=this.mineshaftGenerator)
        {
            this.mineshaftGenerator.generate(this.world, x, z, (ChunkPrimer)null);
        }
/*
        if (null!=this.villageGenerator)
        {
            this.villageGenerator.generate(this.world, x, z, (ChunkPrimer)null);
        }

        if (this.settings.useStrongholds)
        {
            this.strongholdGenerator.generate(this.world, x, z, (ChunkPrimer)null);
        }
*/
        if (null!=this.scatteredFeatureGenerator)
        {
            this.scatteredFeatureGenerator.generate(this.world, x, z, (ChunkPrimer)null);
        }

/*        if (this.settings.useMonuments)
        {
            this.oceanMonumentGenerator.generate(this.world, x, z, (ChunkPrimer)null);
        }*/

        if (null!=this.woodlandMansionGenerator)
        {
            this.woodlandMansionGenerator.generate(this.world, x, z, (ChunkPrimer)null);
        }

}
/*
	protected void swap_biome_map() {
	Biome[] tmp1=this.biomesForGeneration;
	this.biomesForGeneration=this.vp1_biome_exchange;
	this.vp1_biome_exchange=tmp1;
}
	protected Biome[] vp1_biome_exchange;
*/
    public Chunk generateChunk(int x, int z)
    {
        this.rand.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
        ChunkPrimer chunkprimer = new ChunkPrimer();
        
        this.biomesForGeneration=this.world.getBiomeProvider().getBiomes(this.biomesForGeneration,x<<4,z<<4, 16, 16,true);
        this.setBlocksInChunk(x, z, chunkprimer);

        this.replaceBiomeBlocks(x, z, chunkprimer, this.biomesForGeneration);

/*        if (this.settings.useCaves)
        {
            this.caveGenerator.generate(this.world, x, z, chunkprimer);
        }*/

/*        if (this.settings.useRavines)
        {
            this.ravineGenerator.generate(this.world, x, z, chunkprimer);
        }*/

        if (this.mapFeaturesEnabled)
        {
            if (null!=this.mineshaftGenerator)
            {
                this.mineshaftGenerator.generate(this.world, x, z, chunkprimer);
            }
/*
            if (null!=this.villageGenerator)
            {
                this.villageGenerator.generate(this.world, x, z, chunkprimer);
            }

            if (this.settings.useStrongholds)
            {
                this.strongholdGenerator.generate(this.world, x, z, chunkprimer);
            }
*/
            if (null!=this.scatteredFeatureGenerator)
            {
                this.scatteredFeatureGenerator.generate(this.world, x, z, chunkprimer);
            }

/*            if (this.settings.useMonuments)
            {
                this.oceanMonumentGenerator.generate(this.world, x, z, chunkprimer);
            }*/

            if (null!=this.woodlandMansionGenerator)
            {
                this.woodlandMansionGenerator.generate(this.world, x, z, chunkprimer);
            }
        }

        Chunk chunk = new Chunk(this.world, chunkprimer, x, z);
        byte[] abyte = chunk.getBiomeArray();

        for (int i = 0; i < abyte.length; ++i)
        {
            abyte[i] = (byte)Biome.getIdForBiome(this.biomesForGeneration[i]);
        }

        chunk.generateSkylightMap();
        return chunk;
    }
	
    public static double[] noiseBaseCache=null;
    
	public void setBlocksInChunk(int cx, int cz, ChunkPrimer chunk) {
/*
		this.swap_biome_map();
        this.biomesForGeneration = this.world.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, cx * 4 - 2, cz * 4 - 2, 10, 10);
        this.generateHeightmap(cx * 4, 0, cz * 4);
        this.swap_biome_map();
*/
        // <- double [x][z][y]
        double[] noise=this.noiseBase.generateNoiseOctaves(noiseBaseCache,cx*16,0,cz*16,16,128,16,4,8,4);
        noiseBaseCache=noise;

for(int x=0;x<16;x++){
for(int z=0;z<16;z++){
//chunk.setBlockState(x,4,z,STONE);
	
Biome biome=this.biomesForGeneration[z|(x<<4)];
if(TempCategory.OCEAN==biome.getTempCategory()){
continue;
}

for(int y=0;y<64;++y){
if(Math.abs(32-y)*2-noise[(x<<11)|(z<<7)|y]<=this.noise_threshold_0){
chunk.setBlockState(x,y+64,z,STONE);
}
}

for(int y=64;y<128;++y){
if(Math.abs(96-y)*2-noise[(x<<11)|(z<<7)|y]<=this.noise_threshold_0){
chunk.setBlockState(x,y+64,z,STONE);
}
}

}
}

	}

	// original: -0.4D
	protected double noise_threshold_0=-96D;
	
	public NoiseGeneratorOctaves noiseBase;
	
	protected static final IBlockState SNOW_LAYER = Blocks.SNOW_LAYER.getDefaultState();
	protected static final IBlockState AIR= Blocks.AIR.getDefaultState();
	protected static final IBlockState STONE = Blocks.STONE.getDefaultState();
	protected static final IBlockState ICE=Blocks.ICE.getDefaultState();

	public static void load(String p1) {
		String v1=p1+"Is";
		MapGenScatteredFeature.load(v1);
		MapGenMineshaft.load(v1);
		WoodlandMansion.load(v1);
	}
/*
	private static class Checkpoint {
		int x,z;
		final int start=1;
		final int end=-1;
		boolean s=false;
		int o=0;
		String m="";
		
		public void callback(int cx,int cz,int op,String msg) {
			
			System.err.print("checkpoint ");
			System.err.print(op);
			System.err.print(" , cx= ");
			System.err.print(cx);
			System.err.print(" , cz= ");
			System.err.print(cz);
			System.err.print(" .");
			System.err.println();
			System.err.flush();
			
			
			switch(op) {
			case start:
				if(s) {
					this.on_error(cx,cz,op,msg);
				}
				
				m=msg;
				o=op;
				x=cx;
				z=cz;
				s=true;
				break;
			case end:
				if(!s) {
					this.on_error(cx,cz,op,msg);
				}
				
				m=msg;
				o=op;
				x=cx;
				z=cz;
				s=false;
				break;
			default:
				m=msg;
				o=op;
				x=cx;
				z=cz;
				break;
			}
		}
		
		private void on_error(int cx,int cz,int op,String msg) {
			System.err.print("error detect, last checkpoint (o=");
			System.err.print(o);
			if(null!=this.m&&(!this.m.isEmpty())) {
			System.err.print(",m=");
			System.err.print(m);
			}
			System.err.print(",x=");
			System.err.print(x);
			System.err.print(",z=");
			System.err.print(z);
			System.err.print(") , new checkpoint (o=");
			System.err.print(op);
			if(null!=msg&&(!msg.isEmpty())) {
			System.err.print(",m=");
			System.err.print(m);
			}
			System.err.print(",x=");
			System.err.print(cx);
			System.err.print(",z=");
			System.err.print(cz);
			System.err.print(")");
			System.err.println();
			new RuntimeException("Debug error detect").printStackTrace(System.err);
			System.err.flush();
			
		}

	}
	
	private static ThreadLocal<Checkpoint> debug_data=new ThreadLocal<>();

	public static void debug_m(int cx,int cz,int op) {
		Checkpoint v1=debug_data.get();
		if(null==v1) {
			v1=new Checkpoint();
			debug_data.set(v1);
		}
		v1.callback(cx,cz,op,null);
	}
	
	public static void debug_m(int cx,int cz,int op,String m) {
		Checkpoint v1=debug_data.get();
		if(null==v1) {
			v1=new Checkpoint();
			debug_data.set(v1);
		}
		v1.callback(cx,cz,op,m);
	}
*/
}
