package net.suud.mc.x2o.gen.skyisland.structure;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

public class StructureMineshaftStart extends StructureStart
{
    private StructureMineshaftType mineShaftType;

    public StructureMineshaftStart()
    {
    }

    public StructureMineshaftStart(World world, Random rand, int chunkX, int chunkZ, StructureMineshaftType type)
    {
        super(chunkX, chunkZ);
        this.mineShaftType = type;
        StructureMineshaftPieces.Room structuremineshaftpieces$room = new StructureMineshaftPieces.Room(0, rand, (chunkX << 4) + 2, (chunkZ << 4) + 2, this.mineShaftType);
        this.components.add(structuremineshaftpieces$room);
        structuremineshaftpieces$room.buildComponent(structuremineshaftpieces$room, this.components, rand);
        this.updateBoundingBox();

        this.applyAvailableHeight(this.mineShaftType.markAvailableHeight(world, rand, this.boundingBox));
/*
        if(64>this.boundingBox.minY) {
        	System.err.print("checkpoint 1 : (");
        	System.err.print(chunkX);
        	System.err.print(",");
        	System.err.print(chunkZ);
        	System.err.print("), last apply: ");
        	System.err.print(t1);
        	System.err.print(" , new bb: ");
        	System.err.print(this.boundingBox.toString());
        	System.err.println();
        	System.err.flush();
        }
*/
    }
    
    protected void applyAvailableHeight(int j) {
        this.boundingBox.offset(0, j, 0);
        for (StructureComponent structurecomponent : this.components)
        {
            structurecomponent.offset(0, j, 0);
        }
    }

}

