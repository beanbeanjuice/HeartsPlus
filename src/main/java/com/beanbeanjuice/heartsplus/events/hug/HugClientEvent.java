package com.beanbeanjuice.heartsplus.events.hug;

import com.beanbeanjuice.heartsplus.config.HeartsPlusConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;

public class HugClientEvent {

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(HugServerEvent.HUG_PARTICLE_PACKET_ID, (client, handler, buf, sender) -> {
            BlockPos pos = buf.readBlockPos();
            client.execute(() -> spawnHeartParticles(client, pos));
        });

        ClientPlayNetworking.registerGlobalReceiver(HugServerEvent.HUG_SOUND_PACKET_ID, (client, handler, buf, sender) -> {
            BlockPos pos = buf.readBlockPos();
            float serverComputedVolume = buf.readFloat(); // Get volume from server

            client.execute(() -> {
                if (client.world != null && client.player != null) {
                    HeartsPlusConfig config = AutoConfig.getConfigHolder(HeartsPlusConfig.class).getConfig();

                    // Apply player's custom volume setting
                    float finalVolume = serverComputedVolume * config.hugVolume;

                    if (finalVolume > 0.01f) {
                        client.world.playSound(client.player, pos, HugServerEvent.HUG_SOUND_EVENT, net.minecraft.sound.SoundCategory.PLAYERS, finalVolume, 1.0f);
                    }
                }
            });
        });

    }

    private static void spawnHeartParticles(MinecraftClient client, BlockPos pos) {
        if (client.world == null) return;

        for (int i = 0; i < 5; i++) {
            double offsetX = (Math.random() - 0.5) * 0.5;
            double offsetY = Math.random() * 0.5 + 1;
            double offsetZ = (Math.random() - 0.5) * 0.5;
            client.world.addParticle(ParticleTypes.HEART, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, 0, 0.02, 0);
        }
    }

}
