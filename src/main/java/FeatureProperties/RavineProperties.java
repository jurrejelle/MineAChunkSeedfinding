package FeatureProperties;

import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.CPos;

public class RavineProperties {
    public final long structureSeed;
    public final CPos chunkPosition;
    public BPos blockPosition;
    public float yaw;
    public float pitch;
    public float width;
    public int maxLength;
    public final int RAVINE_LENGTH = 112;

    public RavineProperties(long worldSeed, CPos chunkPosition) {
        this.structureSeed = worldSeed;
        this.chunkPosition = chunkPosition;
    }

    public boolean generate(ChunkRand chunkRand) {
        chunkRand.setCarverSeed(structureSeed + 1, chunkPosition.getX(), chunkPosition.getZ(), MCVersion.v1_16_1);
        if (!(chunkRand.nextFloat() <= 0.02F)) return false;
        int x = chunkPosition.getX() * 16 + chunkRand.nextInt(16);
        int y = chunkRand.nextInt(chunkRand.nextInt(40) + 8) + 20;
        int z = chunkPosition.getZ() * 16 + chunkRand.nextInt(16);
        blockPosition = new BPos(x, y, z);

        yaw = chunkRand.nextFloat() * 6.2831855F;
        pitch = (chunkRand.nextFloat() - 0.5F) * 2.0F / 8.0F;
        width = (chunkRand.nextFloat() * 2.0F + chunkRand.nextFloat()) * 2.0F;

        maxLength = RAVINE_LENGTH - chunkRand.nextInt(RAVINE_LENGTH/4);
        return true;
    }


    public boolean generateWithChecks(ChunkRand chunkRand) {
        chunkRand.setCarverSeed(structureSeed + 1, chunkPosition.getX(), chunkPosition.getZ(), MCVersion.v1_16_1);
        if (!(chunkRand.nextFloat() <= 0.02F)) return false;
        int x = chunkPosition.getX() * 16 + chunkRand.nextInt(16);
        int y = chunkRand.nextInt(chunkRand.nextInt(40) + 8)+20;
        int z = chunkPosition.getZ() * 16 + chunkRand.nextInt(16);

        yaw = chunkRand.nextFloat() * 6.2831855F;
        pitch = (chunkRand.nextFloat() - 0.5F) * 2.0F / 8.0F;
        //Doing it like this because if the pitch is positive it wont go down anyways
        if(pitch<0 && y<30) return false;
        width = (chunkRand.nextFloat() * 2.0F + chunkRand.nextFloat()) * 2.0F;

        if(width<5.6) return false;

        blockPosition = new BPos(x, y, z);
        return true;
    }

    public BPos getPosition(ChunkRand chunkRand) {
        chunkRand.setCarverSeed(structureSeed + 1, chunkPosition.getX(), chunkPosition.getZ(), MCVersion.v1_16_1);
        if (!(chunkRand.nextFloat() <= 0.02F)) return null;
        int x = chunkPosition.getX() * 16 + chunkRand.nextInt(16);
        int y = chunkRand.nextInt(chunkRand.nextInt(40) + 8) + 20;
        int z = chunkPosition.getZ() * 16 + chunkRand.nextInt(16);
        return blockPosition = new BPos(x, y, z);
    }

    public Float getYaw(ChunkRand chunkRand) {
        chunkRand.setCarverSeed(structureSeed + 1, chunkPosition.getX(), chunkPosition.getZ(), MCVersion.v1_16_1);
        if (!(chunkRand.nextFloat() <= 0.02F)) return null;
        chunkRand.advance(4);
        return yaw = chunkRand.nextFloat() * 6.2831855F;
    }

    public Float getPitch(ChunkRand chunkRand) {
        chunkRand.setCarverSeed(structureSeed + 1, chunkPosition.getX(), chunkPosition.getZ(), MCVersion.v1_16_1);
        if (!(chunkRand.nextFloat() <= 0.02F)) return null;
        chunkRand.advance(5);
        return pitch = (chunkRand.nextFloat() - 0.5F) * 2.0F / 8.0F;
    }

    public Float getWidth(ChunkRand chunkRand) {
        chunkRand.setCarverSeed(structureSeed + 1, chunkPosition.getX(), chunkPosition.getZ(), MCVersion.v1_16_1);
        if (!(chunkRand.nextFloat() <= 0.02F)) return null;
        chunkRand.advance(6);
        return width = (chunkRand.nextFloat() * 2.0F + chunkRand.nextFloat()) * 2.0F;
    }

    public Integer getMaxLength(ChunkRand chunkRand) {
        chunkRand.setCarverSeed(structureSeed + 1, chunkPosition.getX(), chunkPosition.getZ(), MCVersion.v1_16_1);
        if (!(chunkRand.nextFloat() <= 0.02F)) return null;
        chunkRand.advance(7);
        return maxLength = RAVINE_LENGTH - chunkRand.nextInt(RAVINE_LENGTH / 4);
    }

    @Override
    public String toString() {
        return "Pos: " + blockPosition + "\nyaw: " + yaw + "\npitch: " + pitch + "\nwidth: " + width + "\nMaxlength: " + maxLength+"\n";
    }
}
