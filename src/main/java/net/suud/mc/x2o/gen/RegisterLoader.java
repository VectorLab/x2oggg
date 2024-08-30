package net.suud.mc.x2o.gen;

import net.suud.mc.x2o.gen.skyisland.ChunkGeneratorX2oSkyIsland;
import net.suud.mc.x2o.gen.skyisland.X2oSkyIsland;

public final class RegisterLoader {

	public static void load(String p1) {
		ChunkGeneratorX2oSkyIsland.load(p1);
		new X2oSkyIsland("x2o_skyisland");
	}
}
