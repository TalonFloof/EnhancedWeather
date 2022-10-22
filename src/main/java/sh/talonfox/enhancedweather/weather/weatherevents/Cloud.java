package sh.talonfox.enhancedweather.weather.weatherevents;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import sh.talonfox.enhancedweather.Enhancedweather;
import sh.talonfox.enhancedweather.common.particles.CloudParticle;
import sh.talonfox.enhancedweather.common.particles.ParticleRegister;
import sh.talonfox.enhancedweather.weather.Ambience;
import sh.talonfox.enhancedweather.weather.FunnelParameters;
import sh.talonfox.enhancedweather.weather.Manager;
import sh.talonfox.enhancedweather.weather.Weather;

import java.util.*;

public class Cloud extends Weather {
    public int Layer = 0;
    public int Water = 0;
    public boolean Precipitating = false;
    public boolean Thundering = false;
    public int HailIntensity = 0;
    public int MaxHailIntensity = 0;
    public float HailProgression = 0F;
    public int WindIntensity = 0;
    public boolean Supercell = false;
    public boolean SquallLineControlled = false;
    public int TornadoStage;
    public boolean Placeholder = false;
    public boolean Expanding = true;
    public float Angle = Float.MIN_VALUE;
    private int ticks = 0;
    private int ticksClient = 0;
    private int GroundY = 50;
    public Random rand;
    public HashMap<UUID, Integer> EntityAirTime = new HashMap<UUID, Integer>();
    protected static List<FunnelParameters> FunnelParametersList;
    @Environment(EnvType.CLIENT)
    public List<Particle> ParticlesCloud = new ArrayList<Particle>();
    @Environment(EnvType.CLIENT)
    public List<Particle> ParticlesFunnel = new ArrayList<Particle>();

    @Override
    public Identifier getID() {
        return new Identifier("enhancedweather","cloud");
    }

    public Cloud(Manager manager, Vec3d pos) {
        HostManager = manager;
        Position = pos;
        rand = new Random();
        Size = 50;
        Thundering = false;
        Supercell = false;
        TornadoStage = Integer.MIN_VALUE;
        MaxHailIntensity = rand.nextInt(0,3);
    }

    static {
        initFunnelParameters();
    }

    protected static void initFunnelParameters() {
        FunnelParametersList = new ArrayList<>();

        FunnelParameters fConf = new FunnelParameters(); // Forming
        fConf.InitialSpeed = 0.2F;
        fConf.PullRate = 0.04F;
        fConf.LiftRate = 0.05F;
        fConf.RelativeSize = 0;
        fConf.BaseSize = 3;
        fConf.WidthScale = 1.0F;
        fConf.GrabDistance = 40D;
        FunnelParametersList.add(fConf);

        fConf = new FunnelParameters(); // F1
        fConf.InitialSpeed = 0.2F;
        fConf.PullRate = 0.04F;
        fConf.LiftRate = 0.05F;
        fConf.RelativeSize = -20;
        fConf.BaseSize = 3;
        fConf.WidthScale = 1.5F;
        FunnelParametersList.add(fConf);

        fConf = new FunnelParameters(); // F2
        fConf.InitialSpeed = 0.2F;
        fConf.PullRate = 0.04F;
        fConf.LiftRate = 0.06F;
        fConf.RelativeSize = -30;
        fConf.BaseSize = 6;
        fConf.WidthScale = 1.5F;
        FunnelParametersList.add(fConf);

        fConf = new FunnelParameters(); // F3
        fConf.InitialSpeed = 0.2F;
        fConf.PullRate = 0.04F;
        fConf.LiftRate = 0.07F;
        fConf.RelativeSize = -40;
        fConf.BaseSize = 10;
        fConf.WidthScale = 1.9F;
        FunnelParametersList.add(fConf);

        fConf = new FunnelParameters(); // F4
        fConf.InitialSpeed = 0.2F;
        fConf.PullRate = 0.04F;
        fConf.LiftRate = 0.08F;
        fConf.RelativeSize = -50;
        fConf.BaseSize = 10;
        fConf.WidthScale = 1.9F;
        FunnelParametersList.add(fConf);

        fConf = new FunnelParameters(); // F5
        fConf.InitialSpeed = 0.15F;
        fConf.PullRate = 0.04F;
        fConf.LiftRate = 0.09F;
        fConf.RelativeSize = -60;
        fConf.BaseSize = 25;
        fConf.WidthScale = 2.5F;
        FunnelParametersList.add(fConf);
    }

    public void tickClient() {
        if (Placeholder)
            return;
        ticksClient += 1;
        assert MinecraftClient.getInstance().player != null;
        Vec3i playerPos = new Vec3i(MinecraftClient.getInstance().player.getX(), Position.y, MinecraftClient.getInstance().player.getZ());
        if ((ticksClient % ((Math.max(1, (int)(100F / Size))))) == 0) {
            Vec3i spawnPos = new Vec3i(Position.x + (Math.random() * Size) - (Math.random() * Size), Position.y, Position.z + (Math.random() * Size) - (Math.random() * Size));
            if (ParticlesCloud.size() < Size && playerPos.getManhattanDistance(spawnPos) < Enhancedweather.CONFIG.Client_CloudParticleRenderDistance) {
                float baseBright = Thundering?0.2F:MathHelper.lerp(Math.max(0F,Math.min(1F,((float)Water)/((float)Enhancedweather.CONFIG.Weather_MinimumWaterToPrecipitate))),1.0F,0.5F);
                CloudParticle newParticle = (CloudParticle) MinecraftClient.getInstance().particleManager.addParticle(ParticleRegister.CLOUD, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), Thundering?1:0, Math.min(1F, baseBright), Math.min(1F, baseBright));
                assert newParticle != null;
                if(Angle != Float.MIN_VALUE) {
                    newParticle.setVelocity(-Math.sin(Math.toRadians(Angle)) * 0.1D, 0D, Math.cos(Math.toRadians(Angle)) * 0.1D);
                } else {
                    newParticle.setVelocity(-Math.sin(Math.toRadians(Enhancedweather.CLIENT_WIND.AngleGlobal)) * Enhancedweather.CLIENT_WIND.SpeedGlobal * 0.1D, 0D, Math.cos(Math.toRadians(Enhancedweather.CLIENT_WIND.AngleGlobal)) * Enhancedweather.CLIENT_WIND.SpeedGlobal * 0.1D);
                }
                if(Supercell && newParticle.ID % 20 < 5) {
                    newParticle.setMaxAge(Size+rand.nextInt(100));
                } else if(Thundering) {
                    newParticle.setMaxAge((Size/2)+rand.nextInt(100));
                }
                ParticlesCloud.add(newParticle);
            }
        }
        if(TornadoStage >= 0) {
            int Intensity = this.TornadoStage+4;
            double dist = FunnelParametersList.get(Intensity-4).GrabDistance;
            PlayerEntity ent = MinecraftClient.getInstance().player;
            if(!ent.isSpectator() && !ent.isCreative()) {
                if (Math.sqrt(ent.getPos().squaredDistanceTo(Position.x, ent.getY(), Position.z)) < dist && ent.getY() < Position.y) {
                    spinEntity(ent);
                }
            }
            if((ticks % 3) == 0) {
                int loopSize = Intensity == 9 ? 10 : (Intensity == 8 ? 8 : (Intensity == 7 ? 6 : (Intensity == 6 ? 4 : 2)));
                double spawnRad = Intensity == 9 ? 200D : (Intensity == 8 ? 150D : (Intensity == 7 ? 100D : (Intensity == 6 ? 50D : (Size / 48D))));
                int maxParticles = Intensity == 9 ? 1200 : (Intensity == 8 ? 1000 : (Intensity == 7 ? 800 : 600));
                assert MinecraftClient.getInstance().world != null;
                int currentY = MinecraftClient.getInstance().world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int) Position.x, (int) Position.z);
                if (currentY == MinecraftClient.getInstance().world.getBottomY())
                    currentY = MinecraftClient.getInstance().world.getSeaLevel() + 1;
                float formationProgress = Intensity == 4 ? Math.min(1F, 1F * 2F) : 1F;
                for (int i = 0; i < loopSize; i++) {
                    if (ParticlesFunnel.size() >= maxParticles) {
                        ParticlesFunnel.get(0).markDead();
                        ParticlesFunnel.remove(0);
                    }
                    if (ParticlesFunnel.size() < maxParticles) {
                        Vec3d tryPos = new Vec3d(Position.x + (rand.nextDouble() * spawnRad) - (rand.nextDouble() * spawnRad), MathHelper.lerp(formationProgress, Position.y, currentY), Position.z + (rand.nextDouble() * spawnRad) - (rand.nextDouble() * spawnRad));
                        if (tryPos.distanceTo(Vec3d.ofCenter(playerPos)) < Enhancedweather.CONFIG.Client_CloudParticleRenderDistance) {
                            CloudParticle newParticle = (CloudParticle) MinecraftClient.getInstance().particleManager.addParticle(ParticleRegister.CLOUD, tryPos.getX(), tryPos.getY(), tryPos.getZ(), 1F, 0.3F, 0.3F);
                            assert newParticle != null;
                            newParticle.setMaxAge(150 + ((Intensity - 1) * 100) + rand.nextInt(100));
                            newParticle.setScale(250);
                            ParticlesFunnel.add(newParticle);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < ParticlesFunnel.size(); i++) {
            CloudParticle ent = (CloudParticle)ParticlesFunnel.get(i);
            if (!ent.isAlive()) {
                ParticlesFunnel.remove(i);
                i -= 1;
            } else if(ent.getY() > Position.y) {
                ent.markDead();
                ParticlesFunnel.remove(i);
                i -= 1;
            }
        }
        for (int i = 0; i < ParticlesCloud.size(); i++) {
            CloudParticle ent = (CloudParticle)ParticlesCloud.get(i);
            if (!ent.isAlive()) {
                ParticlesCloud.remove(i);
                i -= 1;
            }
        }
        if(HailIntensity > 0) {
            for(int i = 0; i < Math.max(1, 10 * (Size/300)); i++) {
                int x = (int)(Position.x + rand.nextInt(Size) - rand.nextInt(Size));
                int z = (int)(Position.z + rand.nextInt(Size) - rand.nextInt(Size));
                if(this.HostManager.getWorld().isChunkLoaded(x/16,z/16) && (this.HostManager.getWorld().getClosestPlayer(x, 50, z, 80, false) != null)) {
                    this.HostManager.getWorld().addParticle(ParticleRegister.HAIL,x,200,z,0,0,0);
                }
            }
        }
        if(Supercell) {
            Ambience.HighWindExists = true;
            for(Particle particle : ParticlesFunnel) {
                CloudParticle part = (CloudParticle)particle;
                double var16 = Position.x - part.getX();
                double var18 = Position.z - part.getZ();
                part.yaw = (float)(Math.atan2(var18, var16) * 180.0D / Math.PI) - 90.0F;
                part.yaw += part.ID % 90;
                part.pitch = -30F;
                if(TornadoStage >= 0) {
                    spinParticle(part);
                }
            }
            for(Particle particle : ParticlesCloud) {
                CloudParticle ent = (CloudParticle) particle;
                ent.velocityDecay = true;
                double velocityX = ent.getVelocityX();
                double velocityY = ent.getVelocityY();
                double velocityZ = ent.getVelocityZ();
                double curSpeed = Math.sqrt(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ);
                double curDist = new Vec3d(ent.getX(), Position.getY(), ent.getZ()).distanceTo(Position);
                double spinSpeed;
                if (Supercell && TornadoStage < 0) {
                    spinSpeed = 0.4D * 0.05D;
                } else {
                    spinSpeed = 0.4D * 0.2D;
                }
                float extraDropCalc = 0;
                if (curDist < 200 && ent.ID % 20 < 5) {
                    extraDropCalc = ((ent.ID % 20) * 15F);
                }
                double speed = spinSpeed + (rand.nextDouble() * 0.01D);
                double vecX = ent.getX() - Position.getX();
                double vecZ = ent.getZ() - Position.getZ();
                float angle = (float) (Math.atan2(vecZ, vecX) * 180.0D / Math.PI);
                angle += speed * 50D;
                angle -= (ent.ID % 10) * 3D;
                angle += rand.nextInt(10) - rand.nextInt(10);
                if (curDist > Size) {
                    angle += 40;
                }
                if (ent.ID % 20 < 5) {
                    if (TornadoStage >= 0) {
                        angle += 30 + ((ent.ID % 5) * 4);
                    } else {
                        if (curDist > 150) {
                            angle += 50 + ((ent.ID % 5) * 4);
                        }
                    }
                    double var16 = Position.getX() - ent.getX();
                    double var18 = Position.getZ() - ent.getZ();
                    ent.yaw = (float) (Math.atan2(var18, var16) * 180.0D / Math.PI) - 90.0F;
                    ent.pitch = -20F - (ent.ID % 10);
                }
                if (curSpeed < speed * 20D) {
                    velocityX += -Math.sin(Math.toRadians(angle)) * speed;
                    velocityZ += Math.cos(Math.toRadians(angle)) * speed;
                } else {
                    float cloudMoveAmp = 0.2F * (1 + Layer);

                    speed = Enhancedweather.CLIENT_WIND.SpeedGlobal * cloudMoveAmp;
                    angle = Enhancedweather.CLIENT_WIND.AngleGlobal;

                    if (ent.ID % 20 < 5) {
                        extraDropCalc = ((ent.ID % 20) * 5F);
                    }

                    if (curSpeed < speed) {
                        velocityX += -Math.sin(Math.toRadians(angle)) * speed;
                        velocityZ += Math.cos(Math.toRadians(angle)) * speed;
                    }
                }
                if (Math.abs(ent.getY() - (Position.getY() - extraDropCalc)) > 2F) {
                    if (ent.getY() < Position.getY() - extraDropCalc) {
                        velocityY += 0.1D;
                    } else {
                        velocityY -= 0.1D;
                    }
                }
                if (velocityY < -0.15F) {
                    velocityY = -0.15F;
                }
                if (velocityY > 0.15F) {
                    velocityY = 0.15F;
                }
                ent.setVelocityX(velocityX);
                ent.setVelocityY(velocityY);
                ent.setVelocityZ(velocityZ);
            }
        } else if(WindIntensity > 0) {
            Ambience.HighWindExists = true;
        }
    }
    public void tickServer() {
        ticks++;
        if((ticks % 3) == 0) {
            if (Layer != 0) {
                Size = 300;
            } else if (Size < 300 && Expanding) {
                Size++;
            }
        }
        if((ticks % 60) == 0 && !Placeholder) {
            if(TornadoStage >= 0) {
                GroundY = HostManager.getWorld().getTopY(Heightmap.Type.MOTION_BLOCKING, (int) Position.x, (int) Position.z);
                if (GroundY == HostManager.getWorld().getBottomY())
                    GroundY = HostManager.getWorld().getSeaLevel() + 1;
            }
            boolean waterCollected = false;
            if (rand.nextInt(Enhancedweather.CONFIG.Weather_WaterCollectionFromNothingChance) == 0) {
                Water += 10;
                waterCollected = true;
            }
            if(rand.nextInt(Enhancedweather.CONFIG.Weather_WaterCollectionFromBiomeChance) == 0 && !waterCollected) {
                RegistryEntry<Biome> biome = this.HostManager.getWorld().getBiome(new BlockPos(Position.x, Position.y, Position.z));
                if(biome.isIn(BiomeTags.IS_JUNGLE) || biome.matchesId(new Identifier("minecraft:swamp")) || biome.matchesId(new Identifier("minecraft:mangrove_swamp")) || biome.isIn(BiomeTags.IS_RIVER) || biome.isIn(BiomeTags.IS_OCEAN) || biome.isIn(BiomeTags.IS_DEEP_OCEAN)) {
                    Water += 10;
                    waterCollected = true;
                }
            }
            if (Water > 1000) {
                Water = 1000;
            }
            if (Precipitating && !Thundering) {
                Water = Math.max(0, Water - 3);
                if (Water == 0)
                    Precipitating = false;
            } else if(Thundering) {
                Precipitating = true;
            } else {
                if ((Water >= Enhancedweather.CONFIG.Weather_MinimumWaterToPrecipitate && rand.nextInt(Enhancedweather.CONFIG.Weather_PrecipitationChance) == 0)) {
                    Precipitating = true;
                }
            }
            if (Thundering) {
                /*if (!PeakedIntensity && (ticks % 60) == 0) {
                    if(Intensity >= MaxIntensity) {
                        PeakedIntensity = true;
                    }
                    IntensityProgression += 0.02F * (TornadoStage != Integer.MIN_VALUE ? 3 : 1);
                    if (IntensityProgression >= 0.6F) {
                        Intensity += 1;
                        IntensityProgression = 0;
                    }
                } else if(PeakedIntensity && (Hailing || Supercell) && (ticks % 60) == 0) {
                    IntensityProgression += 0.02F * (TornadoStage != Integer.MIN_VALUE ? 3 : 1) * 0.3F;
                    if(IntensityProgression >= 0.6F) {
                        Intensity -= 1;
                        IntensityProgression = 0;
                    }
                }*/
                if(HailIntensity != MaxHailIntensity && (ticks % 60) == 0) {
                    HailProgression += 0.02F * 2;
                    if(HailProgression >= 0.6F) {
                        HailIntensity += 1;
                        HailProgression = 0F;
                    }
                }
            }
        }
        if(TornadoStage >= 0) {
            double dist = FunnelParametersList.get(this.TornadoStage).GrabDistance;
            Box box = new Box(Position.x-dist, GroundY, Position.z-dist, Position.x+dist, Position.y, Position.z+dist);
            List<Entity> list = HostManager.getWorld().getEntitiesByClass(Entity.class, box, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR);
            for(Entity ent : list) {
                if(!(ent instanceof PlayerEntity) && (ent instanceof LivingEntity || ent instanceof ItemEntity)) {
                    if(Math.sqrt(ent.getPos().squaredDistanceTo(Position.x,ent.getY(),Position.z)) < dist) {
                        spinEntity(ent);
                    }
                }
            }
        } else {
            if(EntityAirTime.size() > 0)
                EntityAirTime.clear();
        }
        ///// WIND /////
        if(SquallLineControlled)
            return;
        if(Angle == Float.MIN_VALUE) {
            float angle = Enhancedweather.WIND.SpeedGlobal;
            Random rand = new Random();
            angle += (rand.nextFloat() - rand.nextFloat()) * 0.15F;
            float angleAdjust = Math.max(10, Math.min(45, 45F * 0 * 0.2F));
            float yaw = 0 > 0 ? 180 : 0;
            float bestMove = MathHelper.wrapDegrees(yaw - angle);
            if (Math.abs(bestMove) < 180) {
                if (bestMove > 0) angle -= angleAdjust;
                if (bestMove < 0) angle += angleAdjust;
            }
            double vecX = -Math.sin(Math.toRadians(angle));
            double vecZ = Math.cos(Math.toRadians(angle));
            Vec3d motion = new Vec3d(vecX * (Enhancedweather.WIND.SpeedGlobal * 0.2F), 0, vecZ * (Enhancedweather.WIND.SpeedGlobal * 0.2F));
            Position = Position.add(motion);
        } else {
            double vecX = -Math.sin(Math.toRadians(Angle));
            double vecZ = Math.cos(Math.toRadians(Angle));
            Vec3d motion = new Vec3d(vecX * (0.2F * 0.2F), 0, vecZ * (0.2F * 0.2F));
            Position = Position.add(motion);
        }
    }

    public void aimAtPlayer(PlayerEntity ent) {
        if(ent == null)
            ent = HostManager.getWorld().getClosestPlayer(Position.x,Position.y,Position.z,-1,false);
        if(ent != null) {
            Random rand = new Random();
            double var11 = ent.getX() - Position.x;
            double var15 = ent.getZ() - Position.z;
            float yaw = -(float)(Math.atan2(var11, var15) * 180.0D / Math.PI);
            int size = 0;
            if(size > 0) {
                yaw += rand.nextInt(size) - (size / 2F);
            }
            Angle = yaw;
        }
    }

    protected static float calculateEntityWeight(Entity ent, int airTime) {
        if(ent instanceof PlayerEntity) {
            if(((PlayerEntity)ent).isCreative())
                return Float.MAX_VALUE;
            return 0.5F + ((float)(airTime / 100));
        } else if(ent instanceof LivingEntity) {
            return 0.5F + ((float)(airTime / 100));
        }
        return 1F;
    }

    public void spinEntity(Entity ent) {
        FunnelParameters conf = FunnelParametersList.get(this.TornadoStage);
        double radius = 10D;
        double scale = conf.WidthScale;
        double d1 = this.Position.x - ent.getX();
        double d2 = this.Position.z - ent.getZ();
        if(this.TornadoStage==0) {
            float range = 30F * (float) Math.sin((Math.toRadians(((ent.getWorld().getTime() * 0.5F) + (ent.getId() * 50)) % 360)));
            float heightPercent = (float) (1F - ((ent.getY() - GroundY) / (Position.y - GroundY)));
            float posOffsetX = (float) Math.sin((Math.toRadians(heightPercent * 360F)));
            float posOffsetZ = (float) -Math.cos((Math.toRadians(heightPercent * 360F)));
            d1 += range*posOffsetX;
            d2 += range*posOffsetZ;
        }
        float f = (float)((Math.atan2(d2, d1) * 180D) / Math.PI) - 90F;
        float f1;

        for (f1 = f; f1 < -180F; f1 += 360F) { }

        for (; f1 >= 180F; f1 -= 360F) { }

        double distY = Position.y - ent.getY();
        double distXZ = Math.sqrt(Math.abs(d1)) + Math.sqrt(Math.abs(d2));

        if (ent.getY() - Position.y < 0.0D) {
            distY = 1.0D;
        }
        else {
            distY = ent.getY() - Position.y;
        }
        if (distY > 60) {
            distY = 60;
        }
        double grab = (10D / calculateEntityWeight(ent, EntityAirTime.computeIfAbsent(ent.getUuid(),val -> 0))) * ((Math.abs((50 - distY)) / 50));
        float pullY = 0.0F;
        if (distXZ > 5D)
        {
            grab = grab * (radius / distXZ);
        }
        pullY += conf.LiftRate / (calculateEntityWeight(ent, EntityAirTime.computeIfAbsent(ent.getUuid(),val -> 0)) / 2F);

        /*if (ent instanceof PlayerEntity)
        {
            double adjPull = 0.2D / ((calculateEntityWeight(ent) * ((distXZ + 1D) / radius)));
            pullY += adjPull;
            int airTime = EntityAirTime.computeIfAbsent(ent.getUuid(),val -> {
                return 0;
            });
            double adjGrab = (10D * (((float)(airTime / 400D))));

            if (adjGrab > 50) {
                adjGrab = 50D;
            }

            if (adjGrab < -50) {
                adjGrab = -50D;
            }

            grab = grab - adjGrab;

            if (ent.getVelocity().y > -0.8) {
                ent.fallDistance = 0F;
            }
            if(ent.isOnGround() || ent.isTouchingWater()) {
                EntityAirTime.remove(ent.getUuid());
            } else {
                airTime += 1;
                EntityAirTime.replace(ent.getUuid(),airTime);
            }
        }
        else */if (ent instanceof LivingEntity)
        {
            double adjPull = 0.005D / ((calculateEntityWeight(ent, EntityAirTime.computeIfAbsent(ent.getUuid(),val -> 0)) * ((distXZ + 1D) / radius)));
            pullY += adjPull;
            int airTime = EntityAirTime.computeIfAbsent(ent.getUuid(),val -> 0);
            double adjGrab = (10D * (((float)(airTime / 400D))));
            if (adjGrab > 50)
            {
                adjGrab = 50D;
            }
            if (adjGrab < -50)
            {
                adjGrab = -50D;
            }
            grab = grab - adjGrab;
            if (ent.getVelocity().y > -1.5) {
                ent.fallDistance = 0F;
            }

            if (ent.getVelocity().y > 0.3F) ent.setVelocity(ent.getVelocity().multiply(1D,0D,1D).add(0D,0.3D,0D));
            if(ent.isOnGround() || ent.isTouchingWater()) {
                EntityAirTime.remove(ent.getUuid());
            } else {
                airTime += 1;
                EntityAirTime.replace(ent.getUuid(),airTime);
            }
            if(!(ent instanceof PlayerEntity)) {
                ent.setOnGround(false);
            }
        }
        grab += conf.RelativeSize;
        double profileAngle = Math.max(1, (75D + grab - (10D * scale)));
        f1 = (float)((double)f1 + profileAngle);

        float f3 = (float)Math.cos(-f1 * 0.01745329F - (float)Math.PI);
        float f4 = (float)Math.sin(-f1 * 0.01745329F - (float)Math.PI);
        float f5 = conf.PullRate * 1;

        if (ent instanceof LivingEntity) {
            f5 /= (calculateEntityWeight(ent, EntityAirTime.computeIfAbsent(ent.getUuid(),val -> 0)) * ((distXZ + 1D) / radius));
        }

        if (ent instanceof PlayerEntity) {
            if (ent.isOnGround()) {
                f5 *= 10.5F;
            } else {
                f5 *= 5F;
            }
        } else if (ent instanceof LivingEntity) {
            f5 *= 1.5F;
        }

        float moveX = f3 * f5;
        float moveZ = f4 * f5;
        float str = 100F;

        pullY *= str / 100F;

        ent.addVelocity(-moveX,pullY,moveZ);
    }

    public void spinParticle(CloudParticle ent) { // Like spinEntity, but with 90% less garbage Corosauce code! (Corosauce isn't a bad developer, it's just that some of his code is difficult to understand)
        FunnelParameters conf = FunnelParametersList.get(this.TornadoStage);
        double radius = 10D;
        double scale = conf.WidthScale;
        double d1 = this.Position.x - ent.getX();
        double d2 = this.Position.z - ent.getZ();
        if(this.TornadoStage==0) {
            int groundHeight = MinecraftClient.getInstance().world.getTopY(Heightmap.Type.MOTION_BLOCKING,(int)Position.x,(int)Position.z);
            float range = 30F * (float) Math.sin((Math.toRadians(((MinecraftClient.getInstance().world.getTime() * 0.5F) + (ent.ID * 50)) % 360)));
            float heightPercent = (float) (1F - ((ent.getY() - groundHeight) / (Position.y - groundHeight)));
            float posOffsetX = (float) Math.sin((Math.toRadians(heightPercent * 360F)));
            float posOffsetZ = (float) -Math.cos((Math.toRadians(heightPercent * 360F)));
            d1 += range*posOffsetX;
            d2 += range*posOffsetZ;
        }
        float f = (float)((Math.atan2(d2, d1) * 180D) / Math.PI) - 90F;
        float f1;

        for (f1 = f; f1 < -180F; f1 += 360F) { }

        for (; f1 >= 180F; f1 -= 360F) { }

        double distY = Position.y - ent.getY();
        double distXZ = Math.sqrt(Math.abs(d1)) + Math.sqrt(Math.abs(d2));

        if (ent.getY() - Position.y < 0.0D) {
            distY = 1.0D;
        }
        else {
            distY = ent.getY() - Position.y;
        }
        if (distY > 60) {
            distY = 60;
        }
        double grab = (10D / 1D) * ((Math.abs((50 - distY)) / 50));
        float pullY = 0.0F;
        if (distXZ > 5D)
        {
            grab = grab * (radius / distXZ);
        }
        pullY += conf.LiftRate / (1D / 2F);

        grab += conf.RelativeSize;
        double profileAngle = Math.max(1, (75D + grab - (10D * scale)));
        f1 = (float)((double)f1 + profileAngle);

        float f3 = (float)Math.cos(-f1 * 0.01745329F - (float)Math.PI);
        float f4 = (float)Math.sin(-f1 * 0.01745329F - (float)Math.PI);
        float f5 = conf.PullRate * 1;

        float moveX = f3 * f5;
        float moveZ = f4 * f5;
        float str = 100F;

        pullY *= str / 100F;

        ent.addVelocity(-moveX,pullY,moveZ);
    }

    @Override
    public NbtCompound generateUpdate() {
        NbtCompound data = super.generateUpdate();
        data.putInt("Layer",Layer);
        data.putInt("Water",Water);
        data.putInt("TornadoStage",TornadoStage);
        data.putInt("HailIntensity",HailIntensity);
        data.putInt("WindIntensity",WindIntensity);
        data.putBoolean("Precipitating",Precipitating);
        data.putBoolean("Thundering",Thundering);
        data.putBoolean("Supercell", Supercell);
        data.putBoolean("Placeholder",Placeholder);
        data.putBoolean("Expanding",Expanding);
        data.putFloat("Angle",Angle);
        return data;
    }

    @Override
    public void applyUpdate(NbtCompound data) {
        super.applyUpdate(data);
        Layer = data.getInt("Layer");
        Water = data.getInt("Water");
        TornadoStage = data.getInt("TornadoStage");
        HailIntensity = data.getInt("HailIntensity");
        WindIntensity = data.getInt("WindIntensity");
        Precipitating = data.getBoolean("Precipitating");
        Thundering = data.getBoolean("Thundering");
        Supercell = data.getBoolean("Supercell");
        Placeholder = data.getBoolean("Placeholder");
        Expanding = data.getBoolean("Expanding");
        Angle = data.getFloat("Angle");
    }

    @Override
    public JsonObject generateSaveDataJson() {
        JsonObject json = super.generateSaveDataJson();
        json.put("Layer",new JsonPrimitive(Layer));
        json.put("Water",new JsonPrimitive(Water));
        json.put("HailIntensity",new JsonPrimitive(HailIntensity));
        json.put("MaxHailIntensity",new JsonPrimitive(MaxHailIntensity));
        json.put("HailProgression",new JsonPrimitive(HailProgression));
        json.put("WindIntensity",new JsonPrimitive(WindIntensity));
        json.put("TornadoStage",new JsonPrimitive(TornadoStage));
        json.put("Precipitating",new JsonPrimitive(Precipitating));
        json.put("Thundering",new JsonPrimitive(Thundering));
        json.put("Supercell",new JsonPrimitive(Supercell));
        json.put("Placeholder",new JsonPrimitive(Placeholder));
        json.put("Expanding",new JsonPrimitive(Expanding));
        json.put("SquallLineControlled",new JsonPrimitive(SquallLineControlled));
        json.put("Angle", new JsonPrimitive(Angle));
        return json;
    }

    @Override
    public void applySaveDataJson(JsonObject json) {
        super.applySaveDataJson(json);
        Layer = json.getInt("Layer",0);
        Water = json.getInt("Water",0);
        HailIntensity = json.getInt("HailIntensity",0);
        MaxHailIntensity = json.getInt("MaxHailIntensity",0);
        HailProgression = json.getFloat("HailProgression",0F);
        WindIntensity = json.getInt("WindIntensity",0);
        TornadoStage = json.getInt("TorandoStage",Integer.MIN_VALUE);
        Precipitating = json.getBoolean("Precipitating",false);
        Thundering = json.getBoolean("Thundering",false);
        Supercell = json.getBoolean("Supercell",false);
        Placeholder = json.getBoolean("Placeholder",false);
        Expanding = json.getBoolean("Expanding",false);
        SquallLineControlled = json.getBoolean("SquallLineControlled",false);
        Angle = json.getFloat("Angle",Float.MIN_VALUE);
    }
}
