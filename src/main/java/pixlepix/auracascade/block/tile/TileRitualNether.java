package pixlepix.auracascade.block.tile;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenHell;
import net.minecraft.world.chunk.Chunk;
import pixlepix.auracascade.AuraCascade;
import pixlepix.auracascade.data.CoordTuple;
import scala.collection.mutable.ArrayStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by localmacaccount on 2/8/15.
 */
public class TileRitualNether extends TileEntity {
    LinkedList<CoordTuple> toSearch = new LinkedList<CoordTuple>();
    BiomeGenBase targetBiome;
    boolean started = true;

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote && !(worldObj.getBiomeGenForCoords(xCoord, zCoord) instanceof BiomeGenHell)) {
            //Coordtuples are used for convenience, but y-values are irrelavent
            toSearch.addFirst(new CoordTuple(this));
            targetBiome = worldObj.getBiomeGenForCoords(xCoord, zCoord);
            started = true;
        }
        int count = 0;
        if (!worldObj.isRemote && toSearch.size() == 0 && started) {
            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        }
        while (!worldObj.isRemote && toSearch.size() > 0) {
            CoordTuple tuple = toSearch.getFirst();
            toSearch.removeFirst();
            int x = tuple.getX();
            int z = tuple.getZ();
            Chunk chunk = worldObj.getChunkFromBlockCoords(x, z);
            byte[] biomeData = chunk.getBiomeArray();
            //8 is hardcoded value for hell biome
            biomeData[(z & 15) << 4 | (x & 15)] = 8;
            boolean particle = true;
            for (int y = 0; y < 255; y++) {
                Block b = getMappedBlock(worldObj.getBlock(x, y, z));
                if (b != null) {
                    worldObj.setBlock(x, y, z, b, 0, 2);
                    if (particle) {
                        particle = false;
                        AuraCascade.proxy.addBlockDestroyEffects(new CoordTuple(x, y, z));
                    }
                }
            }
            if (worldObj.getBiomeGenForCoords(x + 1, z) == targetBiome
                    && !toSearch.contains(new CoordTuple(x + 1, tuple.getY(), z))) {
                toSearch.addLast(new CoordTuple(x + 1, tuple.getY(), z));
            }
            if (worldObj.getBiomeGenForCoords(x - 1, z) == targetBiome
                    && !toSearch.contains(new CoordTuple(x - 1, tuple.getY(), z))) {
                toSearch.addLast(new CoordTuple(x - 1, tuple.getY(), z));
            }
            if (worldObj.getBiomeGenForCoords(x, z + 1) == targetBiome
                    && !toSearch.contains(new CoordTuple(x, tuple.getY(), z + 1))) {
                toSearch.addLast(new CoordTuple(x, tuple.getY(), z + 1));
            }
            if (worldObj.getBiomeGenForCoords(x, z - 1) == targetBiome
                    && !toSearch.contains(new CoordTuple(x + 1, tuple.getY(), z - 1))) {
                toSearch.addLast(new CoordTuple(x, tuple.getY(), z - 1));
            }
            count++;
            if (count > 30) {
                break;

            }
        }

    }

    public Block getMappedBlock(Block b) {
        if (b == Blocks.stone) {
            return Blocks.netherrack;
        }
        if (b == Blocks.grass || b == Blocks.dirt) {
            return new Random().nextInt(3) == 0 ? Blocks.soul_sand : Blocks.netherrack;
        }
        if (b == Blocks.log || b == Blocks.log2 || b == Blocks.leaves || b == Blocks.leaves2) {
            return Blocks.glowstone;
        }
        if (b == Blocks.tallgrass) {
            return Blocks.nether_wart;
        }
        if (b == Blocks.gravel || b == Blocks.sand) {
            return Blocks.soul_sand;
        }
        if (b == Blocks.water || b == Blocks.flowing_water) {
            return Blocks.lava;
        }
        if (b == Blocks.snow || b == Blocks.snow_layer) {
            return Blocks.air;

        }
        return null;
    }
}