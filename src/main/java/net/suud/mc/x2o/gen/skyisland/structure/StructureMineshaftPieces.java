package net.suud.mc.x2o.gen.skyisland.structure;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class StructureMineshaftPieces
{
    public static void load(String p1)
    {
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Corridor.class, p1+"Corridor");
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Cross.class, p1+"Crossing");
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Room.class, p1+"Room");
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Stairs.class, p1+"Stairs");
    }

    private static StructureMineshaftPieces.Peice createRandomShaftPiece(List<StructureComponent> listIn, Random rand, int x, int y, int z, @Nullable EnumFacing face, int nbtTypeP, StructureMineshaftType type)
    {
        int i = rand.nextInt(100);

        if (i >= 80)
        {
            StructureBoundingBox structureboundingbox = StructureMineshaftPieces.Cross.findCrossing(listIn, rand, x, y, z, face);

            if (structureboundingbox != null)
            {
                return new StructureMineshaftPieces.Cross(nbtTypeP, rand, structureboundingbox, face, type);
            }
        }
        else if (i >= 70)
        {
            StructureBoundingBox structureboundingbox1 = StructureMineshaftPieces.Stairs.findStairs(listIn, rand, x, y, z, face);

            if (structureboundingbox1 != null)
            {
                return new StructureMineshaftPieces.Stairs(nbtTypeP, rand, structureboundingbox1, face, type);
            }
        }
        else
        {
            StructureBoundingBox structureboundingbox2 = StructureMineshaftPieces.Corridor.findCorridorSize(listIn, rand, x, y, z, face);

            if (structureboundingbox2 != null)
            {
                return new StructureMineshaftPieces.Corridor(nbtTypeP, rand, structureboundingbox2, face, type);
            }
        }

        return null;
    }

    private static StructureMineshaftPieces.Peice generateAndAddPiece(StructureComponent componentIn, List<StructureComponent> listIn, Random random, int x, int y, int z, EnumFacing face, int nbtType)
    {
        if (nbtType > 8)
        {
            return null;
        }
        else if (Math.abs(x - componentIn.getBoundingBox().minX) <= 80 && Math.abs(z - componentIn.getBoundingBox().minZ) <= 80)
        {
            StructureMineshaftType mapgenmineshaft$type = ((StructureMineshaftPieces.Peice)componentIn).mineShaftType;
            StructureMineshaftPieces.Peice structuremineshaftpieces$peice = createRandomShaftPiece(listIn, random, x, y, z, face, nbtType + 1, mapgenmineshaft$type);

            if (structuremineshaftpieces$peice != null)
            {
                listIn.add(structuremineshaftpieces$peice);
                structuremineshaftpieces$peice.buildComponent(componentIn, listIn, random);
            }

            return structuremineshaftpieces$peice;
        }
        else
        {
            return null;
        }
    }

    public static class Corridor extends StructureMineshaftPieces.Peice
        {
            private boolean hasRails;
            private boolean hasSpiders;
            private boolean spawnerPlaced;
            private int sectionCount;

            public Corridor()
            {
            }

            protected void writeStructureToNBT(NBTTagCompound tagCompound)
            {
                super.writeStructureToNBT(tagCompound);
                tagCompound.setBoolean("hr", this.hasRails);
                tagCompound.setBoolean("sc", this.hasSpiders);
                tagCompound.setBoolean("hps", this.spawnerPlaced);
                tagCompound.setInteger("Num", this.sectionCount);
            }

            protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_)
            {
                super.readStructureFromNBT(tagCompound, p_143011_2_);
                this.hasRails = tagCompound.getBoolean("hr");
                this.hasSpiders = tagCompound.getBoolean("sc");
                this.spawnerPlaced = tagCompound.getBoolean("hps");
                this.sectionCount = tagCompound.getInteger("Num");
            }

            public Corridor(int nbtTypeP, Random rand, StructureBoundingBox structbb, EnumFacing face, StructureMineshaftType type)
            {
                super(nbtTypeP, type);
                this.setCoordBaseMode(face);
                this.boundingBox = structbb;
                this.hasRails = rand.nextInt(3) == 0;
                this.hasSpiders = !this.hasRails && rand.nextInt(23) == 0;

                if (this.getCoordBaseMode().getAxis() == EnumFacing.Axis.Z)
                {
                    this.sectionCount = structbb.getZSize() / 5;
                }
                else
                {
                    this.sectionCount = structbb.getXSize() / 5;
                }
            }

            public static StructureBoundingBox findCorridorSize(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing)
            {
                StructureBoundingBox structureboundingbox = new StructureBoundingBox(x, y, z, x, y + 2, z);
                int i;

                for (i = rand.nextInt(3) + 2; i > 0; --i)
                {
                    int j = i * 5;

                    switch (facing)
                    {
                        case NORTH:
                        default:
                            structureboundingbox.maxX = x + 2;
                            structureboundingbox.minZ = z - (j - 1);
                            break;
                        case SOUTH:
                            structureboundingbox.maxX = x + 2;
                            structureboundingbox.maxZ = z + (j - 1);
                            break;
                        case WEST:
                            structureboundingbox.minX = x - (j - 1);
                            structureboundingbox.maxZ = z + 2;
                            break;
                        case EAST:
                            structureboundingbox.maxX = x + (j - 1);
                            structureboundingbox.maxZ = z + 2;
                    }

                    if (StructureComponent.findIntersecting(listIn, structureboundingbox) == null)
                    {
                        break;
                    }
                }

                return i > 0 ? structureboundingbox : null;
            }

            public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand)
            {
                int nbtType = this.getComponentType();
                int j = rand.nextInt(4);
                EnumFacing enumfacing = this.getCoordBaseMode();

                if (enumfacing != null)
                {
                    switch (enumfacing)
                    {
                        case NORTH:
                        default:

                            if (j <= 1)
                            {
                                StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, enumfacing, nbtType);
                            }
                            else if (j == 2)
                            {
                                StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, EnumFacing.WEST, nbtType);
                            }
                            else
                            {
                                StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, EnumFacing.EAST, nbtType);
                            }

                            break;
                        case SOUTH:

                            if (j <= 1)
                            {
                                StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, enumfacing, nbtType);
                            }
                            else if (j == 2)
                            {
                                StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ - 3, EnumFacing.WEST, nbtType);
                            }
                            else
                            {
                                StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ - 3, EnumFacing.EAST, nbtType);
                            }

                            break;
                        case WEST:

                            if (j <= 1)
                            {
                                StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, enumfacing, nbtType);
                            }
                            else if (j == 2)
                            {
                                StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, EnumFacing.NORTH, nbtType);
                            }
                            else
                            {
                                StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, EnumFacing.SOUTH, nbtType);
                            }

                            break;
                        case EAST:

                            if (j <= 1)
                            {
                                StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, enumfacing, nbtType);
                            }
                            else if (j == 2)
                            {
                                StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, EnumFacing.NORTH, nbtType);
                            }
                            else
                            {
                                StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, EnumFacing.SOUTH, nbtType);
                            }
                    }
                }

                if (nbtType < 8)
                {
                    if (enumfacing != EnumFacing.NORTH && enumfacing != EnumFacing.SOUTH)
                    {
                        for (int i1 = this.boundingBox.minX + 3; i1 + 3 <= this.boundingBox.maxX; i1 += 5)
                        {
                            int j1 = rand.nextInt(5);

                            if (j1 == 0)
                            {
                                StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, i1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, nbtType + 1);
                            }
                            else if (j1 == 1)
                            {
                                StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, i1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, nbtType + 1);
                            }
                        }
                    }
                    else
                    {
                        for (int k = this.boundingBox.minZ + 3; k + 3 <= this.boundingBox.maxZ; k += 5)
                        {
                            int l = rand.nextInt(5);

                            if (l == 0)
                            {
                                StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, k, EnumFacing.WEST, nbtType + 1);
                            }
                            else if (l == 1)
                            {
                                StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, k, EnumFacing.EAST, nbtType + 1);
                            }
                        }
                    }
                }
            }

            protected boolean generateChest(World worldIn, StructureBoundingBox structurebb, Random randomIn, int x, int y, int z, ResourceLocation loot)
            {
                BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
                BlockPos blockposd=blockpos.down();

                if (structurebb.isVecInside(blockpos) && worldIn.getBlockState(blockpos).getMaterial() == Material.AIR && worldIn.getBlockState(blockposd).isSideSolid(worldIn,blockposd, EnumFacing.UP))
                {
                	// hook: MinecartChest
                    IBlockState iblockstate = Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, randomIn.nextBoolean() ? BlockRailBase.EnumRailDirection.NORTH_SOUTH : BlockRailBase.EnumRailDirection.EAST_WEST);
                    this.setBlockState(worldIn, iblockstate, x, y, z, structurebb);
                    EntityMinecartChest entityminecartchest = new EntityMinecartChest(worldIn, (double)((float)blockpos.getX() + 0.5F), (double)((float)blockpos.getY() + 0.5F), (double)((float)blockpos.getZ() + 0.5F));
                    entityminecartchest.setLootTable(loot, randomIn.nextLong());
                    worldIn.spawnEntity(entityminecartchest);
                    return true;
                }
                else
                {
                    return false;
                }
            }

            public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
            {
                if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
                {
                    return false;
                }
                else
                {
                    int i1 = this.sectionCount * 5 - 1;
                    IBlockState iblockstate = this.mineShaftType.getBlockPlanks();
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 2, 1, i1, AIR, AIR, false);
                    this.generateMaybeBox(worldIn, structureBoundingBoxIn, randomIn, 0.8F, 0, 2, 0, 2, 2, i1, AIR, AIR, false, 0);

                    if (this.hasSpiders)
                    {
                        this.generateMaybeBox(worldIn, structureBoundingBoxIn, randomIn, 0.6F, 0, 0, 0, 2, 1, i1, this.mineShaftType.getBlockWeb(), AIR, false, 8);
                    }
                    
                    for (int l2 = 0; l2 <= 2; ++l2)
                    {
                        for (int i3 = 0; i3 <= i1; ++i3)
                        {
                            IBlockState iblockstate3 = this.getBlockStateFromPos(worldIn, l2, -1, i3, structureBoundingBoxIn);

                            if (iblockstate3.getMaterial() == Material.AIR)
                            {
                                this.setBlockState(worldIn, iblockstate, l2, -1, i3, structureBoundingBoxIn);
                            }
                        }
                    }

                    for (int j1 = 0; j1 < this.sectionCount; ++j1)
                    {
                        int k1 = 2 + j1 * 5;
                        this.placeSupport(worldIn, structureBoundingBoxIn, 0, 0, k1, 2, 2, randomIn);
                        this.placeCobWeb(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 0, 2, k1 - 1);
                        this.placeCobWeb(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 2, 2, k1 - 1);
                        this.placeCobWeb(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 0, 2, k1 + 1);
                        this.placeCobWeb(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 2, 2, k1 + 1);
                        this.placeCobWeb(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 0, 2, k1 - 2);
                        this.placeCobWeb(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 2, 2, k1 - 2);
                        this.placeCobWeb(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 0, 2, k1 + 2);
                        this.placeCobWeb(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 2, 2, k1 + 2);

                        if (randomIn.nextInt(100) == 0)
                        {
                            this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 2, 0, k1 - 1, LootTableList.CHESTS_ABANDONED_MINESHAFT);
                        }

                        if (randomIn.nextInt(100) == 0)
                        {
                            this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 0, 0, k1 + 1, LootTableList.CHESTS_ABANDONED_MINESHAFT);
                        }

                        if (this.hasSpiders && !this.spawnerPlaced)
                        {
                            int l1 = this.getYWithOffset(0);
                            int i2 = k1 - 1 + randomIn.nextInt(3);
                            int j2 = this.getXWithOffset(1, i2);
                            int k2 = this.getZWithOffset(1, i2);
                            BlockPos blockpos = new BlockPos(j2, l1, k2);

                            if (structureBoundingBoxIn.isVecInside(blockpos))
                            {
                                this.spawnerPlaced = true;
                                // hook: MobSpawner
                                worldIn.setBlockState(blockpos, Blocks.MOB_SPAWNER.getDefaultState(), 2);
                                TileEntity tileentity = worldIn.getTileEntity(blockpos);

                                if (tileentity instanceof TileEntityMobSpawner)
                                {
                                    ((TileEntityMobSpawner)tileentity).getSpawnerBaseLogic().setEntityId(this.mineShaftType.getMobSpawnerEntity());
                                }
                            }
                        }
                    }

                    if (this.hasRails)
                    {
                    	// hook: Rail
                        IBlockState iblockstate1 = Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH);

                        for (int j3 = 0; j3 <= i1; ++j3)
                        {
                            IBlockState iblockstate2 = this.getBlockStateFromPos(worldIn, 1, -1, j3, structureBoundingBoxIn);

                            if (iblockstate2.getMaterial() != Material.AIR && iblockstate2.isFullBlock())
                            {
                                float chance = this.getSkyBrightness(worldIn, 1, 0, j3, structureBoundingBoxIn) < EnumSkyBlock.SKY.defaultLightValue ? 0.95F : 0.75F;
                                this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, chance, 1, 0, j3, iblockstate1);
                            }
                        }
                    }

                    return true;
                }
            }

            private void placeSupport(World p_189921_1_, StructureBoundingBox p_189921_2_, int p_189921_3_, int p_189921_4_, int p_189921_5_, int p_189921_6_, int p_189921_7_, Random p_189921_8_)
            {
                if (this.isSupportingBox(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_7_, p_189921_6_, p_189921_5_))
                {
                    IBlockState iblockstate = this.mineShaftType.getBlockPlanks();
                    IBlockState iblockstate1 = this.mineShaftType.getBlockFence();
                    IBlockState iblockstate2 = AIR;
                    this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_4_, p_189921_5_, p_189921_3_, p_189921_6_ - 1, p_189921_5_, iblockstate1, iblockstate2, false);
                    this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_7_, p_189921_4_, p_189921_5_, p_189921_7_, p_189921_6_ - 1, p_189921_5_, iblockstate1, iblockstate2, false);

                    if (p_189921_8_.nextInt(4) == 0)
                    {
                        this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_6_, p_189921_5_, p_189921_3_, p_189921_6_, p_189921_5_, iblockstate, iblockstate2, false);
                        this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_7_, p_189921_6_, p_189921_5_, p_189921_7_, p_189921_6_, p_189921_5_, iblockstate, iblockstate2, false);
                    }
                    else
                    {
                        this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_6_, p_189921_5_, p_189921_7_, p_189921_6_, p_189921_5_, iblockstate, iblockstate2, false);
                        this.randomlyPlaceBlock(p_189921_1_, p_189921_2_, p_189921_8_, 0.05F, p_189921_3_ + 1, p_189921_6_, p_189921_5_ - 1, this.mineShaftType.getTorchNorth());
                        this.randomlyPlaceBlock(p_189921_1_, p_189921_2_, p_189921_8_, 0.05F, p_189921_3_ + 1, p_189921_6_, p_189921_5_ + 1, this.mineShaftType.getTorchSouth());
                    }
                }
            }

            private void placeCobWeb(World world, StructureBoundingBox bb, Random p_189922_3_, float p_189922_4_, int x, int y, int z)
            {
                this.randomlyPlaceBlock(world, bb, p_189922_3_, p_189922_4_, x, y, z, this.mineShaftType.getBlockWeb());
            }
        }

    public static class Cross extends StructureMineshaftPieces.Peice
        {
            private EnumFacing corridorDirection;
            private boolean isMultipleFloors;

            public Cross()
            {
            }

            protected void writeStructureToNBT(NBTTagCompound tagCompound)
            {
                super.writeStructureToNBT(tagCompound);
                tagCompound.setBoolean("tf", this.isMultipleFloors);
                tagCompound.setInteger("D", this.corridorDirection.getHorizontalIndex());
            }

            protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_)
            {
                super.readStructureFromNBT(tagCompound, p_143011_2_);
                this.isMultipleFloors = tagCompound.getBoolean("tf");
                this.corridorDirection = EnumFacing.getHorizontal(tagCompound.getInteger("D"));
            }

            public Cross(int p_i47139_1_, Random p_i47139_2_, StructureBoundingBox p_i47139_3_, @Nullable EnumFacing p_i47139_4_, StructureMineshaftType p_i47139_5_)
            {
                super(p_i47139_1_, p_i47139_5_);
                this.corridorDirection = p_i47139_4_;
                this.boundingBox = p_i47139_3_;
                this.isMultipleFloors = p_i47139_3_.getYSize() > 3;
            }

            public static StructureBoundingBox findCrossing(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing)
            {
                StructureBoundingBox structureboundingbox = new StructureBoundingBox(x, y, z, x, y + 2, z);

                if (rand.nextInt(4) == 0)
                {
                    structureboundingbox.maxY += 4;
                }

                switch (facing)
                {
                    case NORTH:
                    default:
                        structureboundingbox.minX = x - 1;
                        structureboundingbox.maxX = x + 3;
                        structureboundingbox.minZ = z - 4;
                        break;
                    case SOUTH:
                        structureboundingbox.minX = x - 1;
                        structureboundingbox.maxX = x + 3;
                        structureboundingbox.maxZ = z + 3 + 1;
                        break;
                    case WEST:
                        structureboundingbox.minX = x - 4;
                        structureboundingbox.minZ = z - 1;
                        structureboundingbox.maxZ = z + 3;
                        break;
                    case EAST:
                        structureboundingbox.maxX = x + 3 + 1;
                        structureboundingbox.minZ = z - 1;
                        structureboundingbox.maxZ = z + 3;
                }

                return StructureComponent.findIntersecting(listIn, structureboundingbox) != null ? null : structureboundingbox;
            }

            public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand)
            {
                int i = this.getComponentType();

                switch (this.corridorDirection)
                {
                    case NORTH:
                    default:
                        StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                        StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, i);
                        StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, i);
                        break;
                    case SOUTH:
                        StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                        StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, i);
                        StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, i);
                        break;
                    case WEST:
                        StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                        StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                        StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, i);
                        break;
                    case EAST:
                        StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                        StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                        StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, i);
                }

                if (this.isMultipleFloors)
                {
                    if (rand.nextBoolean())
                    {
                        StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                    }

                    if (rand.nextBoolean())
                    {
                        StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.WEST, i);
                    }

                    if (rand.nextBoolean())
                    {
                        StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.EAST, i);
                    }

                    if (rand.nextBoolean())
                    {
                        StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                    }
                }
            }

            public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
            {
                if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
                {
                    return false;
                }
                else
                {
                    IBlockState iblockstate = this.mineShaftType.getBlockPlanks();

                    if (this.isMultipleFloors)
                    {
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ, AIR, AIR, false);
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ - 1, AIR, AIR, false);
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.maxY - 2, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, AIR, AIR, false);
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.maxY - 2, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, AIR, AIR, false);
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY + 3, this.boundingBox.minZ + 1, this.boundingBox.maxX - 1, this.boundingBox.minY + 3, this.boundingBox.maxZ - 1, AIR, AIR, false);
                    }
                    else
                    {
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, AIR, AIR, false);
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, AIR, AIR, false);
                    }

                    this.placeSupportPillar(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
                    this.placeSupportPillar(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);
                    this.placeSupportPillar(worldIn, structureBoundingBoxIn, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
                    this.placeSupportPillar(worldIn, structureBoundingBoxIn, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);

                    for (int i = this.boundingBox.minX; i <= this.boundingBox.maxX; ++i)
                    {
                        for (int j = this.boundingBox.minZ; j <= this.boundingBox.maxZ; ++j)
                        {
                            if (this.getBlockStateFromPos(worldIn, i, this.boundingBox.minY - 1, j, structureBoundingBoxIn).getMaterial() == Material.AIR)
                            {
                                this.setBlockState(worldIn, iblockstate, i, this.boundingBox.minY - 1, j, structureBoundingBoxIn);
                            }
                        }
                    }

                    return true;
                }
            }

            private void placeSupportPillar(World p_189923_1_, StructureBoundingBox p_189923_2_, int p_189923_3_, int p_189923_4_, int p_189923_5_, int p_189923_6_)
            {
                if (this.getBlockStateFromPos(p_189923_1_, p_189923_3_, p_189923_6_ + 1, p_189923_5_, p_189923_2_).getMaterial() != Material.AIR)
                {
                    this.fillWithBlocks(p_189923_1_, p_189923_2_, p_189923_3_, p_189923_4_, p_189923_5_, p_189923_3_, p_189923_6_, p_189923_5_, this.mineShaftType.getBlockPlanks(), AIR, false);
                }
            }
        }

    abstract static class Peice extends StructureComponent
        {
            protected StructureMineshaftType mineShaftType;

            public Peice()
            {
            }

            public Peice(int nbtType, StructureMineshaftType type)
            {
                super(nbtType);
                this.mineShaftType = type;
            }

            protected void writeStructureToNBT(NBTTagCompound tagCompound)
            {
                tagCompound.setInteger("MST", this.mineShaftType.getId());
            }

            protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_)
            {
                this.mineShaftType = StructureMineshaftTypeRegistry.type_get(tagCompound.getInteger("MST"));
            }
/*
            protected IBlockState getPlanksBlock()
            {
            	return this.mineShaftType.getBlockPlanks();
            	
                switch (this.mineShaftType)
                {
                    case NORMAL:
                    default:
                        return Blocks.PLANKS.getDefaultState();
                    case MESA:
                        return Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.DARK_OAK);
                }
                
            }

            protected IBlockState getFenceBlock()
            {
            	return this.mineShaftType.getBlockFence();

                switch (this.mineShaftType)
                {
                    case NORMAL:
                    default:
                        return Blocks.OAK_FENCE.getDefaultState();
                    case MESA:
                        return Blocks.DARK_OAK_FENCE.getDefaultState();
                }

            }
*/
            protected boolean isSupportingBox(World p_189918_1_, StructureBoundingBox p_189918_2_, int p_189918_3_, int p_189918_4_, int p_189918_5_, int p_189918_6_)
            {
                for (int i = p_189918_3_; i <= p_189918_4_; ++i)
                {
                    if (this.getBlockStateFromPos(p_189918_1_, i, p_189918_5_ + 1, p_189918_6_, p_189918_2_).getMaterial() == Material.AIR)
                    {
                        return false;
                    }
                }

                return true;
            }
            
            protected void fillWithBlocksDes(World worldIn, StructureBoundingBox boundingboxIn, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, IBlockState blockOuterExist, IBlockState blockInnerExist, IBlockState blockOuterEmpty, IBlockState blockInnerEmpty)
            {
                for (int i = yMin; i <= yMax; ++i)
                {
                    for (int j = xMin; j <= xMax; ++j)
                    {
                        for (int k = zMin; k <= zMax; ++k)
                        {
                        	boolean notAir=this.getBlockStateFromPos(worldIn, j, i, k, boundingboxIn).getMaterial() != Material.AIR;
                        	boolean notBorder=i != yMin && i != yMax && j != xMin && j != xMax && k != zMin && k != zMax;
                        	
                        	if(notAir) {
                                if (notBorder)
                                {
                                    this.setBlockState(worldIn, blockInnerExist, j, i, k, boundingboxIn);
                                }
                                else
                                {
                                    this.setBlockState(worldIn, blockOuterExist, j, i, k, boundingboxIn);
                                }
                        	}else {
                                if (notBorder)
                                {
                                    this.setBlockState(worldIn, blockInnerEmpty, j, i, k, boundingboxIn);
                                }
                                else
                                {
                                    this.setBlockState(worldIn, blockOuterEmpty, j, i, k, boundingboxIn);
                                }
                        	}
                        }
                    }
                }
            }
        }

    public static class Room extends StructureMineshaftPieces.Peice
        {
            private final List<StructureBoundingBox> connectedRooms = Lists.<StructureBoundingBox>newLinkedList();

            public Room()
            {
            }

            public Room(int nbtType, Random rand, int x, int z, StructureMineshaftType type)
            {
                super(nbtType, type);
                this.mineShaftType = type;
                this.boundingBox = new StructureBoundingBox(x,64+ 50, z, x + 7 + rand.nextInt(6),64+ 54 + rand.nextInt(6), z + 7 + rand.nextInt(6));
            }

            public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand)
            {
                int nbtType = this.getComponentType();
                int j = this.boundingBox.getYSize() - 3 - 1;

                if (j <= 0)
                {
                    j = 1;
                }

                int k;

                for (k = 0; k < this.boundingBox.getXSize(); k = k + 4)
                {
                    k = k + rand.nextInt(this.boundingBox.getXSize());

                    if (k + 3 > this.boundingBox.getXSize())
                    {
                        break;
                    }

                    StructureMineshaftPieces.Peice structuremineshaftpieces$peice = StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + k, this.boundingBox.minY + rand.nextInt(j) + 1, this.boundingBox.minZ - 1, EnumFacing.NORTH, nbtType);

                    if (structuremineshaftpieces$peice != null)
                    {
                        StructureBoundingBox structureboundingbox = structuremineshaftpieces$peice.getBoundingBox();
                        this.connectedRooms.add(new StructureBoundingBox(structureboundingbox.minX, structureboundingbox.minY, this.boundingBox.minZ, structureboundingbox.maxX, structureboundingbox.maxY, this.boundingBox.minZ + 1));
                    }
                }

                for (k = 0; k < this.boundingBox.getXSize(); k = k + 4)
                {
                    k = k + rand.nextInt(this.boundingBox.getXSize());

                    if (k + 3 > this.boundingBox.getXSize())
                    {
                        break;
                    }

                    StructureMineshaftPieces.Peice structuremineshaftpieces$peice1 = StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + k, this.boundingBox.minY + rand.nextInt(j) + 1, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, nbtType);

                    if (structuremineshaftpieces$peice1 != null)
                    {
                        StructureBoundingBox structureboundingbox1 = structuremineshaftpieces$peice1.getBoundingBox();
                        this.connectedRooms.add(new StructureBoundingBox(structureboundingbox1.minX, structureboundingbox1.minY, this.boundingBox.maxZ - 1, structureboundingbox1.maxX, structureboundingbox1.maxY, this.boundingBox.maxZ));
                    }
                }

                for (k = 0; k < this.boundingBox.getZSize(); k = k + 4)
                {
                    k = k + rand.nextInt(this.boundingBox.getZSize());

                    if (k + 3 > this.boundingBox.getZSize())
                    {
                        break;
                    }

                    StructureMineshaftPieces.Peice structuremineshaftpieces$peice2 = StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + rand.nextInt(j) + 1, this.boundingBox.minZ + k, EnumFacing.WEST, nbtType);

                    if (structuremineshaftpieces$peice2 != null)
                    {
                        StructureBoundingBox structureboundingbox2 = structuremineshaftpieces$peice2.getBoundingBox();
                        this.connectedRooms.add(new StructureBoundingBox(this.boundingBox.minX, structureboundingbox2.minY, structureboundingbox2.minZ, this.boundingBox.minX + 1, structureboundingbox2.maxY, structureboundingbox2.maxZ));
                    }
                }

                for (k = 0; k < this.boundingBox.getZSize(); k = k + 4)
                {
                    k = k + rand.nextInt(this.boundingBox.getZSize());

                    if (k + 3 > this.boundingBox.getZSize())
                    {
                        break;
                    }

                    StructureComponent structurecomponent = StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + rand.nextInt(j) + 1, this.boundingBox.minZ + k, EnumFacing.EAST, nbtType);

                    if (structurecomponent != null)
                    {
                        StructureBoundingBox structureboundingbox3 = structurecomponent.getBoundingBox();
                        this.connectedRooms.add(new StructureBoundingBox(this.boundingBox.maxX - 1, structureboundingbox3.minY, structureboundingbox3.minZ, this.boundingBox.maxX, structureboundingbox3.maxY, structureboundingbox3.maxZ));
                    }
                }
            }

            public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
            {
                if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
                {
                    return false;
                }
                else
                {
                	this.fillWithBlocksDes(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.minY, this.boundingBox.maxZ, this.mineShaftType.getBlockRoomGround(), AIR, this.mineShaftType.getBlockRoomFloating(), AIR);
                    //this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.minY, this.boundingBox.maxZ, this.mineShaftType.getBlockRoomGround(), AIR, true);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY + 1, this.boundingBox.minZ, this.boundingBox.maxX, Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY), this.boundingBox.maxZ, AIR, AIR, false);

                    for (StructureBoundingBox structureboundingbox : this.connectedRooms)
                    {
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, structureboundingbox.minX, structureboundingbox.maxY - 2, structureboundingbox.minZ, structureboundingbox.maxX, structureboundingbox.maxY, structureboundingbox.maxZ, AIR, AIR, false);
                    }

                    this.randomlyRareFillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY + 4, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ, AIR, false);
                    return true;
                }
            }

            public void offset(int x, int y, int z)
            {
                super.offset(x, y, z);

                for (StructureBoundingBox structureboundingbox : this.connectedRooms)
                {
                    structureboundingbox.offset(x, y, z);
                }
            }

            protected void writeStructureToNBT(NBTTagCompound tagCompound)
            {
                super.writeStructureToNBT(tagCompound);
                NBTTagList nbttaglist = new NBTTagList();

                for (StructureBoundingBox structureboundingbox : this.connectedRooms)
                {
                    nbttaglist.appendTag(structureboundingbox.toNBTTagIntArray());
                }

                tagCompound.setTag("Entrances", nbttaglist);
            }

            protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_)
            {
                super.readStructureFromNBT(tagCompound, p_143011_2_);
                NBTTagList nbttaglist = tagCompound.getTagList("Entrances", 11);

                for (int i = 0; i < nbttaglist.tagCount(); ++i)
                {
                    this.connectedRooms.add(new StructureBoundingBox(nbttaglist.getIntArrayAt(i)));
                }
            }
        }

    public static class Stairs extends StructureMineshaftPieces.Peice
        {
            public Stairs()
            {
            }

            public Stairs(int p_i47136_1_, Random p_i47136_2_, StructureBoundingBox p_i47136_3_, EnumFacing p_i47136_4_, StructureMineshaftType p_i47136_5_)
            {
                super(p_i47136_1_, p_i47136_5_);
                this.setCoordBaseMode(p_i47136_4_);
                this.boundingBox = p_i47136_3_;
            }

            public static StructureBoundingBox findStairs(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing)
            {
                StructureBoundingBox structureboundingbox = new StructureBoundingBox(x, y - 5, z, x, y + 2, z);

                switch (facing)
                {
                    case NORTH:
                    default:
                        structureboundingbox.maxX = x + 2;
                        structureboundingbox.minZ = z - 8;
                        break;
                    case SOUTH:
                        structureboundingbox.maxX = x + 2;
                        structureboundingbox.maxZ = z + 8;
                        break;
                    case WEST:
                        structureboundingbox.minX = x - 8;
                        structureboundingbox.maxZ = z + 2;
                        break;
                    case EAST:
                        structureboundingbox.maxX = x + 8;
                        structureboundingbox.maxZ = z + 2;
                }

                return StructureComponent.findIntersecting(listIn, structureboundingbox) != null ? null : structureboundingbox;
            }

            public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand)
            {
                int i = this.getComponentType();
                EnumFacing enumfacing = this.getCoordBaseMode();

                if (enumfacing != null)
                {
                    switch (enumfacing)
                    {
                        case NORTH:
                        default:
                            StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                            break;
                        case SOUTH:
                            StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                            break;
                        case WEST:
                            StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.WEST, i);
                            break;
                        case EAST:
                            StructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.EAST, i);
                    }
                }
            }

            public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
            {
                if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
                {
                    return false;
                }
                else
                {
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 2, 7, 1, AIR, AIR, false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 7, 2, 2, 8, AIR, AIR, false);

                    for (int i = 0; i < 5; ++i)
                    {
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, AIR, AIR, false);
                    }

                    return true;
                }
            }
        }
    
    public static final IBlockState AIR=Blocks.AIR.getDefaultState();
}

