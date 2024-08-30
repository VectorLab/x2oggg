package net.suud.mc.x2o;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
//import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION,acceptableRemoteVersions="*")
public class ExampleMod
{
    public static final String MODID = "x2o";
    public static final String NAME = "x2o ggg";
    public static final String VERSION = "1.0";
    // Registry
    public static final String REGISTRY = "X2o";

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        net.suud.mc.x2o.gen.RegisterLoader.load(REGISTRY);
        logger.info("Copyright 2022 nebulo.cn All right reserved");
    }
/*
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        //logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
*/
}
