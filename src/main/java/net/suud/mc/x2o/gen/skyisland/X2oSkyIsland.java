package net.suud.mc.x2o.gen.skyisland;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;

public class X2oSkyIsland extends WorldType {

	@Override
	public BiomeProvider getBiomeProvider(World world) {
		return new net.minecraft.world.biome.BiomeProvider(world.getWorldInfo());
	}

	@Override
	public IChunkGenerator getChunkGenerator(World world, String generatorOptions) {
		return new ChunkGeneratorX2oSkyIsland(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), generatorOptions);
	}

	@Override
	public boolean isCustomizable() {
		return false;
	}

	public X2oSkyIsland(String name) {
		super(name);
	}
/*
    public static class Options {
    	int center_height=127;
    	
    	
        public void fromJsonString(String json) {
            try {
                JsonObject obj = gson.fromJson(json,JsonObject.class);
                if(obj.has("center_height")) {
                center_height = obj.get("center_height").getAsInt();
                }
            } catch (JsonSyntaxException e) {
                ExampleMod.logger.error(this.getClass().toString()+"Failed to load world type options");
                ExampleMod.logger.error(json);
                ExampleMod.logger.catching(e);
            }
        }

        public String toJsonString() {
        	JsonObject obj = new JsonObject();
        	obj.addProperty("center_height", center_height);
            return gson.toJson(obj);
        }
    }
*/


}
