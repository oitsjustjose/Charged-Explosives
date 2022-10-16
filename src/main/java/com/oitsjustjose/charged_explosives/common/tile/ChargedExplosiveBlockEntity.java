package com.oitsjustjose.charged_explosives.common.tile;

import com.oitsjustjose.charged_explosives.ChargedExplosives;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChargedExplosiveBlockEntity extends BlockEntity {
    private int explosionWidth = 0;
    private int explosionHeight = 0;
    private int explosionDepth = 0;

    public ChargedExplosiveBlockEntity(BlockPos pos, BlockState state) {
        super(ChargedExplosives.getInstance().REGISTRY.CeBlockEntityType.get(), pos, state);
    }

    //    saveAdditional(CompoundTag tag)
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("explosionWidth", this.explosionWidth);
        tag.putInt("explosionHeight", this.explosionHeight);
        tag.putInt("explosionDepth", this.explosionDepth);
    }

    public void load(CompoundTag tag) {
        this.explosionWidth = tag.getInt("explosionWidth");
        this.explosionHeight = tag.getInt("explosionHeight");
        this.explosionDepth = tag.getInt("explosionDepth");
    }

    @Override
    public void saveToItem(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("explosionWidth", explosionWidth);
        tag.putInt("explosionHeight", explosionHeight);
        tag.putInt("explosionDepth", explosionDepth);
        super.saveToItem(stack);
    }

    public int getExplosionWidth() {
        return explosionWidth;
    }
    public int getExplosionDepth() {
        return explosionDepth;
    }

    public int getExplosionHeight() {
        return explosionHeight;
    }
}
