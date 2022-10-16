package com.oitsjustjose.charged_explosives.common.items;

import com.oitsjustjose.charged_explosives.ChargedExplosives;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ChargedExplosiveItem extends BlockItem {
    public ChargedExplosiveItem(Block block) {
        super(block, new Item.Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        // Set NBT if it hasn't been already
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();
        UUID uuid;
        if (tag.contains("explosive_id")) {
            uuid = tag.getUUID("explosive_id");
        } else {
            uuid = UUID.randomUUID();
            tag.putUUID("explosive_id", uuid);
        }
        ChargedExplosives.getInstance().proxy.openExplosiveGui(player, stack);
        return super.use(level, player, hand);
    }
}
