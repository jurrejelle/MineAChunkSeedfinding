package FeatureProperties;

import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.util.math.DistanceMetric;
import kaptainwutax.seedutils.util.math.Vec3i;

public class WorldSeedThread implements Runnable{
    private int offset, totalThreads, intersectx, intersectz;
    private long structureSeed;
    public WorldSeedThread(int offset, int totalThreads,long structureSeed, int intersectx, int intersectz){
        this.offset = offset;
        this.totalThreads = totalThreads;
        this.structureSeed = structureSeed;
        this.intersectx = intersectx;
        this.intersectz = intersectz;
    }
    public void run() {
        System.out.println("Started WorldSeedThread "+(offset+1)+"/"+totalThreads);
        //Go through all possible biomeseeds
        for (long biomeSeed = this.offset; biomeSeed < 1L << 16; biomeSeed=biomeSeed+this.totalThreads) {
            //Worldseed = [16 biomeSeed bits]+[48 structureSeed bits]
            long worldSeed = biomeSeed << 48 | this.structureSeed;
            //Generate the overworldBiomeSource from this world seed to check spawnpoint and biomes
            OverworldBiomeSource overworldBiomeSource = new OverworldBiomeSource(MCVersion.v1_16_1, worldSeed);
            BPos spawnPoint = overworldBiomeSource.getSpawnPoint();
            double distance = spawnPoint.distanceTo(new Vec3i(this.intersectx, 0, this.intersectz), DistanceMetric.EUCLIDEAN);
            //If the spawn is close to the intersection:
            if (distance < 60.0) {
               String biome = overworldBiomeSource.getBiome(this.intersectx, 0, this.intersectz).getCategory().getName();
               //Check if the biome of the intersection between the ravines isn't a river or ocean
                if (!(biome.equals("ocean")) && !(biome.equals("river"))){
                    String spawnBiome = overworldBiomeSource.getBiome(spawnPoint.getX(), 0, spawnPoint.getZ()).getCategory().getName();
                    //Check if we spawn in a forest
                    if(spawnBiome.equals("forest")){
                        System.out.println(worldSeed+": "+intersectx+","+intersectz);
                        return;
                    }
                }
            }
            if(biomeSeed==(1L<<16-1)){
                System.out.println("No good seeds found");
            }
        }
    }

}
