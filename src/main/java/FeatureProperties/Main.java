package FeatureProperties;


import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.pos.CPos;

public class Main {
    //Start is the seed to start at, totalThreads is the amount of threads. More is better but make sure it doesn't cap out your CPU.
    public static long STRUCTURE_SEED_START = 1277000000000L;
    public static int TOTAL_THREADS = 12;


    public static void main (String[] args) {

        //Paste the output in here to have it turn the structure seeds into world seeds
        long[][] viableStructureSeeds = new long[][]{
        };

        // If there's stuff in the viableStructureSeeds array that means we have to convert structure seeds to world seeds. Otherwise, just generate more structure seeds.
        if(viableStructureSeeds.length>0) {
            //If multiple seeds, just run all of em with one thread each and exit
            if(viableStructureSeeds.length>1) {
                generateAllSeeds(viableStructureSeeds, TOTAL_THREADS);
            } else {
                //If a single seed, spend all computing power on that one seed
                for(int offset = 0; offset< TOTAL_THREADS; offset++) {
                    Runnable myThread = new WorldSeedThread(offset, TOTAL_THREADS,viableStructureSeeds[0][0], (int)viableStructureSeeds[0][1],(int)viableStructureSeeds[0][2]);
                    new Thread(myThread).start();
                }
            }
        } else {
            //It might take a while before finding any structure seeds. The criteria are quite strict:
            //  2+   W I D E   ravines in a 3*3 chunk box around the spawn
            //  that intersect
            //  at a small angle
            //
            // The output will be {structureSeed, intersectX, intersectY}. You can paste this in the viableStructureSeeds array up above to generate a world seed
            // for it. This might take a few minutes because our restraints are pretty strict on this as well.
            // Also, once every 1.000.000.000 seeds it'll post an update on how far it is, if you stop the program you can use these to
            // set the int start to, so that it knows where to resume.
            // (Don't include these in the viableStructureSeeds array, they're just for knowing where the program is rn)
            for (int offset = 0; offset < TOTAL_THREADS; offset++) {
                Runnable myThread = new StructureSeedThread(STRUCTURE_SEED_START, offset, TOTAL_THREADS);
                new Thread(myThread).start();
            }
        }
    }
    public static void generateAllSeeds(long[][] allSeeds, int totalThreads){
        for(long[] curr : allSeeds){
            //Start only 1 each
            Runnable myThread = new WorldSeedThread(0, 1,curr[0], (int)curr[1],(int)curr[2]);
            new Thread(myThread).start();
            //System.out.println(generateSeed(curr[0],(int)curr[1],(int)curr[2], (int)curr[3],(int)curr[4])+": "+curr[1]+","+curr[2]);
        }
    }
    //You can call this method with a structureseed to find all info about ravines in the structureseed
    public static void analyseRavines(long structureSeed){
        ChunkRand chunkRand = new ChunkRand();
        for(int x=-1;x<=1;x++) {
            for (int z = -1; z <= 1; z++) {
                RavineProperties rp = new RavineProperties(structureSeed, new CPos(x, z));
                if (rp.generate(chunkRand)) {
                    System.out.println(rp);
                }
            }
        }
    }
}