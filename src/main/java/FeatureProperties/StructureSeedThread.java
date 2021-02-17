package FeatureProperties;

import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.CPos;
import kaptainwutax.seedutils.util.math.DistanceMetric;
import kaptainwutax.seedutils.util.math.Vec3i;

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
        ChunkRand myCR = new ChunkRand();

        ArrayList<Long> structureSeeds = new ArrayList<>();
        // brute force through every structure seed

        for (long structureSeed = this.start+this.offset; structureSeed < 1L << 48; structureSeed+=this.totalThreads) {
            if(structureSeed%1000000000==0){
                System.out.println(structureSeed+"L");
            }
            int ravineCounter=0;
            int[][] ravines = new int[10][2];
            //Check for ravines in the chunks arond spawn (so x=[-1,0,1] z=[-1,0,1])
            for(int x=-1;x<=1;x++){
                for(int z=-1;z<=1;z++) {
                    //Spawn a ravineProperties class at our coordinates
                    RavineProperties rp = new RavineProperties(structureSeed, new CPos(x, z));
                    Float myWidth = rp.getWidth(myCR);
                    //Skip anything that either isn't a ravine or is less than 5.6 wide (max width: 6.0)
                    if (myWidth == null || myWidth <5.6) continue;
                    //Add the x,z chunk coordinates to a list, and increase the number of ravines found (i) by 1
                    ravines[ravineCounter][0] = x;
                    ravines[ravineCounter][1] = z;
                    ravineCounter++;
                }
            }
            //If there are 2 or more ravines of our width
            if(ravineCounter>1) {
                ArrayList<Float[]> yaws = new ArrayList<>();
                //Go through all the ravines and add the x, z of their starts and the yaw (angle in radians) to an array
                for (int j = 0; j < ravineCounter; j++) {
                    RavineProperties rp = new RavineProperties(structureSeed, new CPos(ravines[j][0], ravines[j][1]));
                    BPos position = rp.getPosition(myCR);
                    //Skip if the ravine is too low because of high chance of lava
                    if(position.getY()<35){continue;}

                    Float x = Integer.valueOf(position.getX()).floatValue();
                    Float z = Integer.valueOf(position.getZ()).floatValue();
                    //Add the info we need for the intersection check to an array
                    yaws.add(new Float[]{rp.getYaw(myCR), x, z});
                }
                //If there are less than 2 left we can discard this structure seed and try the next one
                if(yaws.size()<2) {
                    continue;
                }
                for (int yaw1index=0;yaw1index<yaws.size();yaw1index++) {
                    for (int yaw2index=yaw1index+1;yaw2index<yaws.size();yaw2index++) {
                        Float[] yaw1 = yaws.get(yaw1index);
                        Float[] yaw2 = yaws.get(yaw2index);
                        AtomicInteger x = new AtomicInteger(-1000);
                        AtomicInteger z = new AtomicInteger(-1000);
                        //Check if both ravines intersect and if they are relatively close to being parralel
                        if (intersect(yaw1[1], yaw1[2], yaw2[1], yaw2[2], yaw1[0], yaw2[0], 35, x, z) &&
                                Math.abs(yaw1[0]-yaw2[0])<0.5) {
                            //Make sure we print only once for every structure seed
                            if (!structureSeeds.contains(structureSeed)) {
                                //Print the structure seed and intersection coordinates
                                structureSeeds.add(structureSeed);
                                System.out.println("{" + structureSeed + "L," + x.get() + "," + z.get() +"},");
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
