package com.oitsjustjose.charged_explosives.common.network;


import com.oitsjustjose.charged_explosives.ChargedExplosives;
import com.oitsjustjose.charged_explosives.common.items.ChargedExplosiveItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class UpdateNbtPacket {

    public final ItemStack stack;

    public UpdateNbtPacket(FriendlyByteBuf buf) {
        this.stack = buf.readItem();
    }

    public UpdateNbtPacket(ItemStack stack) {
        this.stack = stack;
    }

    public static UpdateNbtPacket decode(FriendlyByteBuf buf) {
        return new UpdateNbtPacket(buf);
    }

    public static void encode(UpdateNbtPacket pkt, FriendlyByteBuf buf) {
        buf.writeItem(pkt.stack);
    }

    public static void handle(UpdateNbtPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
            ctx.get().enqueueWork(() -> {
                if (!pkt.stack.hasTag()) {
                    ChargedExplosives.getInstance().LOGGER.warn("ItemStack from packet had NO NBT to update with. NOOP");
                    return;
                }

                CompoundTag newTag = Objects.requireNonNull(pkt.stack.getTag());
                ItemStack main = Objects.requireNonNull(ctx.get().getSender()).getMainHandItem();
                ItemStack off = Objects.requireNonNull(ctx.get().getSender()).getOffhandItem();

                if (off.getItem() instanceof ChargedExplosiveItem) {
                    off.setTag(newTag);
                } else if (main.getItem() instanceof ChargedExplosiveItem) {
                    main.setTag(newTag);
                } else {
                    ChargedExplosives.getInstance().LOGGER.warn("ItemStack in players main and off hands are NOT charged explosives...");
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
