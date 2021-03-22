### MineAChunkSeedfinding

In this project, I use KaptainWutax's seedutils to find good seeds to run for Minecraft 1.16.1 Set-Seed Mine-A-Chunk speedruns.

My group usually runs co-op, but these seeds can be used for singleplayer aswell.


## Installation
1. Create a new IntelliJ project, with gradle support. 
2. Either use my build.gradle, or add the following to yours: 
```
repositories {
    mavenCentral()
    maven {
        url "https://jitpack.io/"
    }
}
dependencies {
    implementation 'com.github.KaptainWutax:SEED:master-SNAPSHOT'
}
```
3. Clone the src folders from this project into your IntelliJ project.
That's it! 

## Explaination
To understand this, you first need to understand the difference between Structure seeds and World seeds.\
(Things are more complicated than this, but for this oroject, this is all you need to know).\
A structure seed is the lower 48 bits of a world seed, and determines the structures that are spawned, as well as some other things including ravines.\
We use this to iterate over all 2^48 structure seeds, and find good ones where two wide ravines intersect eachother, with small angle difference.\
This maximises the amount of blocks taken out by the ravines.\
Then, once we have good structure seeds, we can generate a good world seed with it by iterating through the lower 16 bits.\
This (together with the structure seed) determines the biome, spawnpoint and some other factors.\
We try to get a good world seed so that we spawn in a forest, with a spawnpoint close to where the two ravines intersect, and the intersection point is not underwater.


## Usage
To generate structure seeds, you can leave the viableStructureSeeds array in main.java empty. Then, it will spawn a number of threads, default 12, to iterate over all seeds sequentially\
and try to find good ones. \
It will print these out to the console, this will be the output, in the format {structureseed, intersectx, intersectz}.\
It will also print how far it has come, every 1.000.000.000 seeds. This can be used to update the start variable so that you dont iterate over the same seeds multiple times.\
If you copy all the structureseeds (in the array-format that they are) into viableStructureSeeds and run the program again, it will go onto the next step:\
Finding a good world seed for that structure seed.\
It will print world seeds and intersection coordinates to the console, and then you can go into a world with that seed to see if it's any good.


Our current best chunk is in seed: 4998434179546958649\
x: 26-41\
z: 34-49\
which only has about 3687 blocks that need to be mined.

## Contact
If you have any questions, you can reach me on discord (@jurrejelle#4936), twitter (@jurrejelle), instagram (@jurrejelle), telegram (@jurrejelle) or email (jurre@jilles.com)
