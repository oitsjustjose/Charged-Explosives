package com.oitsjustjose.charged_explosives.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Tuple;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PreviewExplosionPacket {

    public static String ACTION_ADD = "add";
    public static String ACTION_REMOVE = "remove";
    public final Tuple<BlockPos, BlockPos> corners;
    public final String action;

    public PreviewExplosionPacket(FriendlyByteBuf buf) {
        this.corners = new Tuple<>(buf.readBlockPos(), buf.readBlockPos());
        this.action = buf.readUtf();
    }

    public PreviewExplosionPacket(BlockPos start, BlockPos end, String action) {
        assert action.equals(ACTION_ADD) || action.equals(ACTION_REMOVE);
        this.corners = new Tuple<>(start, end);
        this.action = action;
    }

    public PreviewExplosionPacket(Tuple<BlockPos, BlockPos> cornersIn, String action) {
        assert action.equals(ACTION_ADD) || action.equals(ACTION_REMOVE);
        this.corners = cornersIn;
        this.action = action;
    }

    public static PreviewExplosionPacket decode(FriendlyByteBuf buf) {
        return new PreviewExplosionPacket(buf);
    }

    public static void encode(PreviewExplosionPacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.corners.getA());
        buf.writeBlockPos(pkt.corners.getB());
        buf.writeUtf(pkt.action);
    }

    public void handleServer(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }
}
