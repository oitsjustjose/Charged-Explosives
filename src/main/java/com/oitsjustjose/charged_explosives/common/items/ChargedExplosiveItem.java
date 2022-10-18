package com.oitsjustjose.charged_explosives.common.items;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.oitsjustjose.charged_explosives.ChargedExplosives;
import com.oitsjustjose.charged_explosives.common.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ChargedExplosiveItem extends BlockItem {
    public ChargedExplosiveItem(Block block) {
        super(block, new Item.Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        // Set NBT if it hasn't been already
        ItemStack stack = player.getItemInHand(hand);
        stack.getOrCreateTag();
        ChargedExplosives.getInstance().PROXY.openExplosiveGui(player, stack);
        return super.use(level, player, hand);
    }

    public static boolean isNbtInvalid(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = Objects.requireNonNull(stack.getTag());
            return !tag.contains("explosionWidth") || !tag.contains("explosionHeight") || !tag.contains("explosionDepth");
        }
        return true;
    }

    @Override
    public @NotNull InteractionResult place(BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        if (isNbtInvalid(stack)) {
            Objects.requireNonNull(context.getPlayer()).displayClientMessage(Util.translateOrFallback(ChargedExplosives.MODID + ".needs_init"), true);
            return InteractionResult.FAIL;
        }
        return super.place(context);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(@Nonnull BlockPos p, @Nonnull Level l, @Nullable Player pl, @Nonnull ItemStack s, @Nonnull BlockState st) {
        return true;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        if (!stack.hasTag()) {
            return;
        }

        CompoundTag tag = Objects.requireNonNull(stack.getTag());
        if (isNbtInvalid(stack)) {
            components.add(Util.translateOrFallback(ChargedExplosives.MODID + ".not_yet_initialized"));
            return;
        } else {
            int w = tag.getInt("explosionWidth");
            int d = tag.getInt("explosionDepth");
            int h = tag.getInt("explosionHeight");
            try {
                TranslatableContents wc = new TranslatableContents(ChargedExplosives.MODID + ".width_tooltip", w);
                components.add(wc.resolve(null, null, 0));
                TranslatableContents dc = new TranslatableContents(ChargedExplosives.MODID + ".depth_tooltip", d);
                components.add(dc.resolve(null, null, 0));
                TranslatableContents hc = new TranslatableContents(ChargedExplosives.MODID + ".height_tooltip", h);
                components.add(hc.resolve(null, null, 0));
            } catch (CommandSyntaxException ignored) {
            }
        }
    }
}
