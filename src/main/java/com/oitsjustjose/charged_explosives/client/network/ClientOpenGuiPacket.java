package com.oitsjustjose.charged_explosives.client.network;

import com.oitsjustjose.charged_explosives.client.gui.ExplosionConfigScreen;
import com.oitsjustjose.charged_explosives.common.network.OpenGuiPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientOpenGuiPacket {
    public static void handleClient(OpenGuiPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            ctx.get().enqueueWork(() -> Minecraft.getInstance().setScreen(new ExplosionConfigScreen(pkt.stack)));
            ctx.get().setPacketHandled(true);
        }
    }
}
