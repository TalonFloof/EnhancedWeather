package sh.talonfox.enhancedweather.weather;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import sh.talonfox.enhancedweather.Enhancedweather;
import sh.talonfox.enhancedweather.particles.CloudParticle;
import sh.talonfox.enhancedweather.particles.ParticleRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cloud extends Weather {
    public int Layer = 0;
    public int Water = 0;
    public int Intensity = 0;
    public boolean Precipitating = false;
    public boolean Placeholder = false;
    public boolean Expanding = true;
    public float Angle = Float.MIN_VALUE;
    private int ticks = 0;
    private int ticksClient = 0;
    public Random rand;
    @Environment(EnvType.CLIENT)
    public List<Particle> ParticlesCloud = new ArrayList<Particle>();

    public Cloud(Manager manager, Vec3d pos) {
        HostManager = manager;
        Position = pos;
        rand = new Random();
        Size = 50;
        Intensity = Enhancedweather.CONFIG.Weather_DefaultCloudIntensity;
    }

    public void tickClient() {
        if (Placeholder)
            return;
        ticksClient += 1;
        if ((ticksClient % ((Math.max(1, (int)(100F / Size))))) == 0) {
            assert MinecraftClient.getInstance().player != null;
            Vec3i playerPos = new Vec3i(MinecraftClient.getInstance().player.getX(), Position.y, MinecraftClient.getInstance().player.getZ());
            Vec3i spawnPos = new Vec3i(Position.x + (Math.random() * Size) - (Math.random() * Size), Position.y, Position.z + (Math.random() * Size) - (Math.random() * Size));
            if (ParticlesCloud.size() < Size && playerPos.getManhattanDistance(spawnPos) < Enhancedweather.CONFIG.Client_CloudParticleRenderDistance) {
                CloudParticle newParticle = (CloudParticle) MinecraftClient.getInstance().particleManager.addParticle(ParticleRegister.CLOUD, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), Precipitating ? 0.2F : 0.7F, Precipitating ? 0.2F : 0.7F, Precipitating ? 0.2F : 0.7F);
                assert newParticle != null;
                newParticle.setVelocity(-Math.sin(Math.toRadians(Enhancedweather.CLIENT_WIND.AngleGlobal)) * Enhancedweather.CLIENT_WIND.SpeedGlobal * 0.1D, 0D, Math.cos(Math.toRadians(Enhancedweather.CLIENT_WIND.AngleGlobal)) * Enhancedweather.CLIENT_WIND.SpeedGlobal * 0.1D);
                if(Intensity > 1 && newParticle.ID % 20 < 5) {
                    newParticle.setMaxAge(Size+rand.nextInt(100));
                } else if(Intensity > 0) {
                    newParticle.setMaxAge((Size/2)+rand.nextInt(100));
                }
                ParticlesCloud.add(newParticle);
            }
        }
        for (int i = 0; i < ParticlesCloud.size(); i++) {
            CloudParticle ent = (CloudParticle)ParticlesCloud.get(i);
            if (!ent.isAlive()) {
                ParticlesCloud.remove(i);
                i -= 1;
            }
        }
        if(Intensity > 1) {
            Ambience.HighWindExists = true;
            for(Particle particle : ParticlesCloud) {
                CloudParticle ent = (CloudParticle) particle;
                ent.velocityDecay = true;
                double velocityX = ent.getVelocityX();
                double velocityY = ent.getVelocityY();
                double velocityZ = ent.getVelocityZ();
                double curSpeed = Math.sqrt(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ);
                double curDist = new Vec3d(ent.getX(), Position.getY(), ent.getZ()).distanceTo(Position);
                double spinSpeed = 0.4D;
                if (Intensity == 2 || Intensity == 3) {
                    spinSpeed = 0.4D * 0.05D;
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
                    if (Intensity >= 4) {
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
            if (Precipitating && Intensity == 0) {
                Water = Math.max(0, Water - 3);
                if (Water == 0)
                    Precipitating = false;
            } else if(Intensity >= 1) {
                Precipitating = true;
            } else if(Intensity == 0) {
                if ((Water >= Enhancedweather.CONFIG.Weather_MinimumWaterToPrecipitate && rand.nextInt(Enhancedweather.CONFIG.Weather_PrecipitationChance) == 0)) {
                    Precipitating = true;
                }
            }
        }
        ///// WIND /////
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

    @Override
    public NbtCompound generateUpdate() {
        NbtCompound data = super.generateUpdate();
        data.putInt("Layer",Layer);
        data.putInt("Water",Water);
        data.putInt("Intensity",Intensity);
        data.putBoolean("Precipitating",Precipitating);
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
        Intensity = data.getInt("Intensity");
        Precipitating = data.getBoolean("Precipitating");
        Placeholder = data.getBoolean("Placeholder");
        Expanding = data.getBoolean("Expanding");
        Angle = data.getFloat("Angle");
    }

    @Override
    public JsonObject generateSaveDataJson() {
        JsonObject json = super.generateSaveDataJson();
        json.put("Layer",new JsonPrimitive(Layer));
        json.put("Water",new JsonPrimitive(Water));
        json.put("Intensity",new JsonPrimitive(Intensity));
        json.put("Precipitating",new JsonPrimitive(Precipitating));
        json.put("Placeholder",new JsonPrimitive(Placeholder));
        json.put("Expanding",new JsonPrimitive(Expanding));
        json.put("Angle", new JsonPrimitive(Angle));
        return json;
    }

    @Override
    public void applySaveDataJson(JsonObject json) {
        super.applySaveDataJson(json);
        Layer = json.getInt("Layer",0);
        Water = json.getInt("Water",0);
        Intensity = json.getInt("Intensity",0);
        Precipitating = json.getBoolean("Precipitating",false);
        Placeholder = json.getBoolean("Placeholder",false);
        Expanding = json.getBoolean("Expanding",false);
        Angle = json.getFloat("Angle",0F);
    }
}
