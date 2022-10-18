package com.oitsjustjose.charged_explosives.client.network;

import com.oitsjustjose.charged_explosives.common.network.ExplosionParticlePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientExplosionParticlePacket {
    public static void handleClient(ExplosionParticlePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            ctx.get().enqueueWork(() -> {
                if (Minecraft.getInstance().level != null) {
                    Minecraft.getInstance().level.addParticle(
                            ParticleTypes.EXPLOSION,
                            (double) pkt.pos.getX() + 0.5D,
                            (double) pkt.pos.getY() + 0.5D,
                            (double) pkt.pos.getZ() + 0.5D,
                            1.0D, 0.0D, 0.0D
                    );
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
