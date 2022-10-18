package com.oitsjustjose.charged_explosives.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ExplosionParticlePacket {
    public BlockPos pos;

    public ExplosionParticlePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }

    public ExplosionParticlePacket(BlockPos pos) {
        this.pos = pos;
    }

    public static ExplosionParticlePacket decode(FriendlyByteBuf buf) {
        return new ExplosionParticlePacket(buf);
    }

    public static void encode(ExplosionParticlePacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
    }

    public void handleServer(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }
}
