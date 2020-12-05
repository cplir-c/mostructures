package io.github.frqnny.mostructures.feature;

import java.util.Random;

import io.github.frqnny.mostructures.MoStructures;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;


public class LamppostFeature extends Feature<DefaultFeatureConfig> {
    public static final Identifier LAMPPOST = MoStructures.id("miscellaneous/lamppost");
    public static final Identifier NETHER_LAMPPOST = MoStructures.id("miscellaneous/netherlamppost");

    public static final Identifier ID = MoStructures.id("lamppost");

    public LamppostFeature() {
        super(DefaultFeatureConfig.CODEC);
    }

    public static BlockPos getCorrectNetherHeight(BlockPos pos, ServerWorldAccess world) {

        //It'll check for lava at lava ocean level (32) now
        BlockPos posToWorkOn = new BlockPos(pos.getX(), 30, pos.getZ());
        BlockState block = world.getBlockState(posToWorkOn);
        BlockState state = Blocks.AIR.getDefaultState();
        while (block != state) {
            posToWorkOn = posToWorkOn.up();
            block = world.getBlockState(posToWorkOn);

            if (!world.getBlockState(posToWorkOn).getFluidState().isEmpty()) {
                return null;
            }
        }

        return posToWorkOn;
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, DefaultFeatureConfig featureConfig) {
        Identifier lamppost = world.getBiome(pos).getCategory() == Biome.Category.NETHER ? NETHER_LAMPPOST : LAMPPOST;
        boolean inWater = false;
        BlockPos newPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);
        if (world.getBlockState(newPos.down()) == Blocks.WATER.getDefaultState()) {
            inWater = true;
        }
        Biome.Category category = world.getBiome(pos).getCategory();
        if (category == Biome.Category.FOREST && !inWater && random.nextBoolean()) {

            BlockRotation blockRotation = BlockRotation.random(random);
            StructurePlacementData structurePlacementData = (new StructurePlacementData()).setMirror(BlockMirror.NONE).setRotation(blockRotation).setIgnoreEntities(false).setPosition(BlockPos.ORIGIN);
            StructureManager manager = world.toServerWorld().getStructureManager();
            Structure structure = manager.getStructureOrBlank(lamppost);

            structure.place(world, newPos, BlockPos.ORIGIN, structurePlacementData, random, 3);
            return true;
        } else if (category == Biome.Category.NETHER) {
            BlockRotation blockRotation = BlockRotation.random(random);
            StructurePlacementData structurePlacementData = (new StructurePlacementData()).setMirror(BlockMirror.NONE).setRotation(blockRotation).setIgnoreEntities(false).setPosition(BlockPos.ORIGIN);
            StructureManager manager = world.toServerWorld().getStructureManager();
            Structure structure = manager.getStructureOrBlank(lamppost);

            BlockPos correctPos = getCorrectNetherHeight(pos, world);
            if (correctPos == null) {
                return false;
            }
            structurePlacementData.setPosition(correctPos);

            structure.place(world, correctPos, BlockPos.ORIGIN, structurePlacementData, random, 3);
            return true;
        }
        return false;
    }

}
