package com.oitsjustjose.charged_explosives.client.network;

import com.oitsjustjose.charged_explosives.client.ClientProxy;
import com.oitsjustjose.charged_explosives.common.network.PreviewExplosionPacket;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class ClientPreviewExplosionPacket {
    public static void handleClient(PreviewExplosionPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            ctx.get().enqueueWork(() -> {
                if (pkt.action.equals(PreviewExplosionPacket.ACTION_ADD)) {
                    try {
                        ClientProxy.bdRenderer.addExplosion(pkt.corners);
                    } catch (NoSuchElementException ignored) {
                    }
                } else if (pkt.action.equals(PreviewExplosionPacket.ACTION_REMOVE)) {
                    try {
                        ClientProxy.bdRenderer.removeExplosion(pkt.corners);
                    } catch (NoSuchElementException ignored) {
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
