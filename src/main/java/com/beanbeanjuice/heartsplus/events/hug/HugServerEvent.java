package com.beanbeanjuice.heartsplus.events.hug;

import com.beanbeanjuice.heartsplus.HeartsPlus;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HugServerEvent {

    public static final Identifier HUG_PARTICLE_PACKET_ID = new Identifier(HeartsPlus.MOD_ID, "hug_particle_packet");
    public static final Identifier HUG_SOUND_PACKET_ID = new Identifier(HeartsPlus.MOD_ID, "hug_sound_packet");
    public static final Identifier HUG_SOUND_ID = new Identifier(HeartsPlus.MOD_ID, "hug");
    public static final SoundEvent HUG_SOUND_EVENT = SoundEvent.of(HUG_SOUND_ID);

    public static void registerServer() {
        Registry.register(Registries.SOUND_EVENT, HUG_SOUND_ID, HUG_SOUND_EVENT);
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> handlePlayerInteraction(player, world, hand, entity));
    }

    private static ActionResult handlePlayerInteraction(PlayerEntity player, World world, Hand hand, Entity entity) {
        if (entity instanceof PlayerEntity targetPlayer) {
            if (player.isSneaking()) return ActionResult.PASS;
            if (!player.getStackInHand(hand).isEmpty()) return ActionResult.PASS;

            if (!world.isClient) {
                // Play the hug sound
                sendSoundPacket(targetPlayer);

                // Send a packet to the client to show heart particles
                sendParticlePacket(targetPlayer);
                sendParticlePacket(player);

                // Notify players
                player.sendMessage(Text.of(String.format("You hugged %s! 💋💕", targetPlayer.getName().getString())), true);
                targetPlayer.sendMessage(Text.of(String.format("%s hugged you! 💋💕", player.getName().getString())), true);
            }

            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private static void sendSoundPacket(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            BlockPos hugPos = player.getBlockPos();

            for (ServerPlayerEntity nearbyPlayer : serverPlayer.getServerWorld().getPlayers()) {
                double distance = nearbyPlayer.getPos().distanceTo(hugPos.toCenterPos());
                if (distance > 100) return;

                // Exponential drop-off calculation on the server
                float maxVolume = 1.0f; // Base server volume
                float k = 0.05f; // Adjust for drop-off rate
                float adjustedVolume = (float) (maxVolume * Math.exp(-k * distance));

                if (adjustedVolume > 0.01f) { // Ignore if too quiet
                    // Create a NEW PacketByteBuf for each player
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(hugPos);
                    buf.writeFloat(adjustedVolume); // Send precomputed volume

                    ServerPlayNetworking.send(nearbyPlayer, HUG_SOUND_PACKET_ID, buf);
                }
            }
        }
    }


    private static void sendParticlePacket(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {  // Ensure it's a server player
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(player.getBlockPos());

            ServerPlayNetworking.send(serverPlayer, HUG_PARTICLE_PACKET_ID, buf);
        }
    }

}
