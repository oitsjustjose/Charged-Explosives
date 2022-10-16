package com.oitsjustjose.charged_explosives.common.network;


import com.oitsjustjose.charged_explosives.ChargedExplosives;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

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

    public void handleClient(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }

    public static void handleServer(UpdateNbtPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
            ctx.get().enqueueWork(() -> {
                if (!pkt.stack.hasTag()) {
                    ChargedExplosives.getInstance().LOGGER.warn("ItemStack from packet had NO NBT to update with. NOOP");
                    return;
                }

                CompoundTag newTag = Objects.requireNonNull(pkt.stack.getTag());
                // Search for the item to update
                Optional<ItemStack> stack = Objects.requireNonNull(ctx.get().getSender()).getInventory().items.stream().filter(x -> {
                    return x.hasTag()
                            && x.getTag() != null
                            && x.getTag().contains("explosive_id")
                            && x.getTag().getUUID("explosive_id") == newTag.getUUID("explosive_id");
                }).findFirst();

                if (stack.isPresent()) {
                    // We know there's a tag present because we used it earlier in the filter()
                    CompoundTag tag = Objects.requireNonNull(stack.get().getTag());
                    tag.putInt("explosionWidth", newTag.getInt("explosionWidth"));
                    tag.putInt("explosionDepth", newTag.getInt("explosionDepth"));
                    tag.putInt("explosionHeight", newTag.getInt("explosionHeight"));
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
