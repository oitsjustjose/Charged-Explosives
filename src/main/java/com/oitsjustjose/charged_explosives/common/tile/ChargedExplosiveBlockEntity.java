package com.oitsjustjose.charged_explosives.common.tile;

import com.oitsjustjose.charged_explosives.ChargedExplosives;
import com.oitsjustjose.charged_explosives.common.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class ChargedExplosiveBlockEntity extends BlockEntity {
    private int explosionWidth = 0;
    private int explosionHeight = 0;
    private int explosionDepth = 0;
    private ArrayList<BlockPos> explosionPositions;
    private final ArrayList<UUID> scheduledTasks;
    private Tuple<BlockPos, BlockPos> cornerToCorner;

    public ChargedExplosiveBlockEntity(BlockPos pos, BlockState state) {
        super(ChargedExplosives.getInstance().REGISTRY.CeBlockEntityType.get(), pos, state);
        this.explosionPositions = new ArrayList<>();
        this.scheduledTasks = new ArrayList<>();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("explosionWidth", this.explosionWidth);
        tag.putInt("explosionHeight", this.explosionHeight);
        tag.putInt("explosionDepth", this.explosionDepth);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.explosionWidth = tag.getInt("explosionWidth");
        this.explosionHeight = tag.getInt("explosionHeight");
        this.explosionDepth = tag.getInt("explosionDepth");
        calculateExplosions();
        ChargedExplosives.getInstance().PROXY.startPreviewExplosion(this.cornerToCorner);
        this.setChanged();
    }

    @Override
    public void saveToItem(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("explosionWidth", explosionWidth);
        tag.putInt("explosionHeight", explosionHeight);
        tag.putInt("explosionDepth", explosionDepth);
    }

    public float calculateConcussiveDamage() {
        return (((float) this.explosionWidth / (CommonConfig.MAX_EXPLOSION_WIDTH.get() / 4F)) +
                ((float) this.explosionHeight / (CommonConfig.MAX_EXPLOSION_HEIGHT.get() / 4F)) +
                ((float) this.explosionDepth / (CommonConfig.MAX_EXPLOSION_DEPTH.get() / 4F))) *
                CommonConfig.CONCUSSIVE_DAMAGE_SCALE.get().floatValue();
    }

    public ArrayList<BlockPos> getExplosions() {
        return this.explosionPositions;
    }

    public Tuple<BlockPos, BlockPos> getCorners() {
        return this.cornerToCorner;
    }

    public void calculateExplosions() {
        BlockPos pos = this.getBlockPos();
        BlockState state = this.getBlockState();
        ArrayList<BlockPos> data = new ArrayList<>();
        Direction direction = state.getValue(HorizontalDirectionalBlock.FACING);
        AttachFace face = state.getValue(FaceAttachedHorizontalDirectionalBlock.FACE);

        int startX, endX, startY, endY, startZ, endZ;
        startX = endX = startY = endY = startZ = endZ = 0;
        switch (face) {
            case FLOOR -> {
                if (direction.getAxis() == Direction.Axis.X) {
                    startX = pos.getX() - (int) Math.floor(explosionHeight / 2F);
                    endX = pos.getX() + (int) Math.ceil(explosionHeight / 2F);
                    startY = pos.getY() - explosionDepth;
                    endY = pos.getY();
                    startZ = pos.getZ() - (int) Math.floor(explosionWidth / 2F);
                    endZ = pos.getZ() + (int) Math.ceil(explosionWidth / 2F);
                } else {
                    startX = pos.getX() - (int) Math.floor(explosionWidth / 2F);
                    endX = pos.getX() + (int) Math.ceil(explosionWidth / 2F);
                    startY = pos.getY() - explosionDepth;
                    endY = pos.getY();
                    startZ = pos.getZ() - (int) Math.floor(explosionHeight / 2F);
                    endZ = pos.getZ() + (int) Math.ceil(explosionHeight / 2F);
                }
            }
            case WALL -> {
                switch (direction) {
                    case NORTH -> {
                        startX = pos.getX() - (int) Math.floor(explosionWidth / 2F);
                        endX = pos.getX() + (int) Math.ceil(explosionWidth / 2F);
                        startY = pos.getY() - (int) Math.floor(explosionHeight / 2F);
                        endY = pos.getY() + (int) Math.ceil(explosionHeight / 2F);
                        startZ = pos.getZ() + 1;
                        endZ = pos.getZ() + explosionDepth + 1;
                    }
                    case SOUTH -> {
                        startX = pos.getX() - (int) Math.floor(explosionWidth / 2F);
                        endX = pos.getX() + (int) Math.ceil(explosionWidth / 2F);
                        startY = pos.getY() - (int) Math.floor(explosionHeight / 2F);
                        endY = pos.getY() + (int) Math.ceil(explosionHeight / 2F);
                        startZ = pos.getZ() - explosionDepth;
                        endZ = pos.getZ();
                    }
                    case EAST -> {
                        startX = pos.getX() - explosionDepth;
                        endX = pos.getX();
                        startY = pos.getY() - (int) Math.floor(explosionHeight / 2F);
                        endY = pos.getY() + (int) Math.ceil(explosionHeight / 2F);
                        startZ = pos.getZ() - (int) Math.floor(explosionWidth / 2F);
                        endZ = pos.getZ() + (int) Math.ceil(explosionWidth / 2F);
                    }
                    case WEST -> {
                        startX = pos.getX() + 1;
                        endX = pos.getX() + explosionDepth + 1;
                        startY = pos.getY() - (int) Math.floor(explosionHeight / 2F);
                        endY = pos.getY() + (int) Math.ceil(explosionHeight / 2F);
                        startZ = pos.getZ() - (int) Math.floor(explosionWidth / 2F);
                        endZ = pos.getZ() + (int) Math.ceil(explosionWidth / 2F);
                    }
                    default -> {
                    }
                }
            }
            case CEILING -> {
                if (direction.getAxis() == Direction.Axis.X) {
                    startX = pos.getX() - (int) Math.floor(explosionHeight / 2F);
                    endX = pos.getX() + (int) Math.ceil(explosionHeight / 2F);
                    startY = pos.getY();
                    endY = pos.getY() + explosionDepth;
                    startZ = pos.getZ() - (int) Math.floor(explosionWidth / 2F);
                    endZ = pos.getZ() + (int) Math.ceil(explosionWidth / 2F);
                } else {
                    startX = pos.getX() - (int) Math.floor(explosionWidth / 2F) + 1;
                    endX = pos.getX() + (int) Math.ceil(explosionWidth / 2F) + 1;
                    startY = pos.getY();
                    endY = pos.getY() + explosionDepth;
                    startZ = pos.getZ() - (int) Math.floor(explosionHeight / 2F);
                    endZ = pos.getZ() + (int) Math.ceil(explosionHeight / 2F);
                }
            }
        }

        for (int x = startX; x < endX; x++) {
            for (int z = startZ; z < endZ; z++) {
                for (int y = startY; y < endY; y++) {
                    data.add(new BlockPos(x, y, z));
                }
            }
        }

        this.explosionPositions = data;
        this.cornerToCorner = new Tuple<>(new BlockPos(startX, startY, startZ), new BlockPos(endX, endY, endZ));
    }

    public void addScheduledTask(UUID uuid) {
        this.scheduledTasks.add(uuid);
    }

    @Override
    public void setRemoved() {
        this.scheduledTasks.forEach(ChargedExplosives.getInstance().SCHEDULER::cancelTask);
        super.setRemoved();
    }
}
