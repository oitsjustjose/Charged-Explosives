package com.oitsjustjose.charged_explosives.common.blocks;

import com.oitsjustjose.charged_explosives.common.tile.ChargedExplosiveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChargedExplosiveBlock extends FaceAttachedHorizontalDirectionalBlock implements EntityBlock {


    protected static final VoxelShape CEILING_AABB_X = Block.box(4D, 13D, 2D, 12D, 16D, 14D);
    protected static final VoxelShape CEILING_AABB_Z = Block.box(2D, 13D, 4D, 14D, 16D, 12D);
    protected static final VoxelShape FLOOR_AABB_X = Block.box(4D, 0D, 2D, 12D, 3D, 14D);
    protected static final VoxelShape FLOOR_AABB_Z = Block.box(2D, 0D, 4D, 14D, 3D, 12D);
    protected static final VoxelShape SOUTH_AABB = Block.box(2D, 4D, 0D, 14D, 12D, 3D);
    protected static final VoxelShape NORTH_AABB = Block.box(2D, 4D, 13D, 14D, 12D, 16D);
    protected static final VoxelShape WEST_AABB = Block.box(13D, 4D, 2D, 16D, 12D, 14D);
    protected static final VoxelShape EAST_AABB = Block.box(0D, 4D, 2D, 3D, 12D, 14D);

    public ChargedExplosiveBlock() {
        super(BlockBehaviour.Properties.of(Material.EXPLOSIVE, MaterialColor.COLOR_RED).dynamicShape().noOcclusion().isValidSpawn((w, x, y, z) -> false).isRedstoneConductor((x, y, z) -> false).isSuffocating((x, y, z) -> false).isViewBlocking((x, y, z) -> false));

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        switch (state.getValue(FACE)) {
            case FLOOR:
                if (direction.getAxis() == Direction.Axis.X) {
                    return FLOOR_AABB_X;
                }

                return FLOOR_AABB_Z;
            case WALL:
                return switch (direction) {
                    case EAST -> EAST_AABB;
                    case WEST -> WEST_AABB;
                    case SOUTH -> SOUTH_AABB;
                    case DOWN -> FLOOR_AABB_X;
                    case UP -> CEILING_AABB_X;
                    case NORTH -> NORTH_AABB;
                };
            case CEILING:
            default:
                if (direction.getAxis() == Direction.Axis.X) {
                    return CEILING_AABB_X;
                } else {
                    return CEILING_AABB_Z;
                }
        }
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        switch (state.getValue(FACE)) {
            case FLOOR:
                if (direction.getAxis() == Direction.Axis.X) {
                    return FLOOR_AABB_X;
                }

                return FLOOR_AABB_Z;
            case WALL:
                return switch (direction) {
                    case EAST -> EAST_AABB;
                    case WEST -> WEST_AABB;
                    case SOUTH -> SOUTH_AABB;
                    case DOWN -> FLOOR_AABB_X;
                    case UP -> CEILING_AABB_X;
                    case NORTH -> NORTH_AABB;
                };
            case CEILING:
            default:
                if (direction.getAxis() == Direction.Axis.X) {
                    return CEILING_AABB_X;
                } else {
                    return CEILING_AABB_Z;
                }
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> Builder) {
        Builder.add(FACING, FACE);
    }


    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        ChargedExplosiveBlockEntity be = (ChargedExplosiveBlockEntity) level.getBlockEntity(pos);
        if (be == null) {
            return;
        }

        CompoundTag tag = stack.getOrCreateTag().copy();
        if (!tag.contains("explosionWidth") || !tag.contains("explosionHeight") || !tag.contains("explosionDepth")) {
            tag.putInt("explosionWidth", 0);
            tag.putInt("explosionHeight", 0);
            tag.putInt("explosionDepth", 0);
        }

        be.load(tag);
    }

    @Override
    public void onRemove(BlockState oldState, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean moving) {
        if (!newState.is(oldState.getBlock())) {
            level.removeBlockEntity(pos);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ChargedExplosiveBlockEntity(pos, state);
    }
}

