package FeatureProperties;

import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.pos.CPos;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class StructureSeedThread implements Runnable{
    private int offset, totalThreads;
    private long start;
    //Check for intersections from points (x1,z1) and (x2,z2) at angles theta1, theta2 within scale blocks.
    //If they intersect,store in x,z
    public static boolean intersect(double x1, double z1, double x2, double z2, double theta1, double theta2, double scale, AtomicInteger x, AtomicInteger z){
        double s1_x = scale*Math.cos(theta1);
        double s1_y = scale*Math.sin(theta1);
        double s2_x = scale*Math.cos(theta2);
        double s2_y = scale*Math.sin(theta2);
        double d = (-s2_x * s1_y + s1_x * s2_y);
        double s = (-s1_y * (x1 - x2) + s1_x * (z1 - z2)) / d;
        double t = ( s2_x * (z1 - z2) - s2_y * (x1 - x2)) / d;
        if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
        {
            x.set((int) (x1+(t*s1_x)));
            z.set((int) (z1+(t*s1_y)));
            return true;
        }
        return false;
    }
    public StructureSeedThread(long start, int offset, int totalThreads){
        this.start = start;
        this.offset = offset;
        this.totalThreads = totalThreads;

    }
    public void run(){
        System.out.println("Started StructureSeedThread "+(offset+1)+"/"+totalThreads+" on seed nr: "+(start+offset));
        ChunkRand chunkRand = new ChunkRand();

        ArrayList<Long> structureSeeds = new ArrayList<>();
        // brute force through every structure seed

        for (long structureSeed = this.start+this.offset; structureSeed < 1L << 48; structureSeed+=this.totalThreads) {
            if(structureSeed%1000000000==0){
                System.out.println(structureSeed+"L");
            }
            ArrayList<RavineProperties> ravines = new ArrayList<>();
            //Check for ravines in the chunks arond spawn (so x=[-1,0,1] z=[-1,0,1])
            for(int x=-1;x<=1;x++){
                for(int z=-1;z<=1;z++) {
                    //Spawn a ravineProperties class at our coordinates
                    RavineProperties ravine = new RavineProperties(structureSeed, new CPos(x, z));
                    //Skip anything that either isn't a ravine or is less than 5.6 wide (max width: 6.0)
                    if(ravine.generateWithChecks(chunkRand)) {
                        //Add the ravine to a list, and increase the number of ravines found (ravineCounter) by 1
                        ravines.add(ravine);
                    }
                }
            }
            //If there are 2 or more ravines of our width
            int ravineCounter = ravines.size();
            if(ravineCounter >=2) {
                for (int i=0;i<ravineCounter-1;i++) {
                    for (int j=i+1;j<ravineCounter;j++) {
                        RavineProperties ravine1 = ravines.get(i);
                        RavineProperties ravine2 = ravines.get(j);
                        AtomicInteger intersectX = new AtomicInteger(-1000);
                        AtomicInteger intersectZ = new AtomicInteger(-1000);
                        //Check if both ravines intersect and if they are relatively close to being parralel
                        if (intersect(ravine1.blockPosition.getX(), ravine1.blockPosition.getZ(),
                                ravine2.blockPosition.getX(), ravine2.blockPosition.getZ(),
                                ravine1.yaw, ravine2.yaw, 50, intersectX, intersectZ) &&
                                Math.abs(ravine1.yaw-ravine2.yaw)<0.5) {
                            //Make sure we print only once for every structure seed
                            if (!structureSeeds.contains(structureSeed)) {
                                //Print the structure seed and intersection coordinates
                                structureSeeds.add(structureSeed);
                                System.out.println("{" + structureSeed + "L," + intersectX.get() + "," + intersectZ.get() +"},");
                            }
                        }
                    }
                }
            }
        }
        //At this point, ctrl-c once you've got enough seeds (you think) and paste the output of the program into allSeeds up top. Running it again will generate
        //actual seeds and coordinates of intersection.
    }
}
