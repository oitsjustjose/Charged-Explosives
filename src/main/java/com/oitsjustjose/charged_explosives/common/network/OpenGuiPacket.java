package com.oitsjustjose.charged_explosives.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenGuiPacket {

    public final ItemStack stack;

    public OpenGuiPacket(FriendlyByteBuf buf) {
        this.stack = buf.readItem();
    }

    public OpenGuiPacket(ItemStack stack) {
        this.stack = stack;
    }

    public static OpenGuiPacket decode(FriendlyByteBuf buf) {
        return new OpenGuiPacket(buf);
    }

    public static void encode(OpenGuiPacket pkt, FriendlyByteBuf buf) {
        buf.writeItem(pkt.stack);
    }

    public void handleServer(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }
}
