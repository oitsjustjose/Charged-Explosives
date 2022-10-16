package com.oitsjustjose.charged_explosives.common.blocks;

import com.oitsjustjose.charged_explosives.ChargedExplosives;
import com.oitsjustjose.charged_explosives.common.registry.Registry;
import com.oitsjustjose.charged_explosives.common.tile.ChargedExplosiveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

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
        super(BlockBehaviour.Properties.of(Material.EXPLOSIVE, MaterialColor.COLOR_GREEN).dynamicShape().noOcclusion().isValidSpawn((w, x, y, z) -> false).isRedstoneConductor((x, y, z) -> false).isSuffocating((x, y, z) -> false).isViewBlocking((x, y, z) -> false));

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
        super.onRemove(oldState, level, pos, newState, moving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ChargedExplosiveBlockEntity(pos, state);
    }

    @Override
    public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (level.isClientSide() || player.isCreative()) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ChargedExplosiveBlockEntity expBe) {
            ItemStack stack = new ItemStack(ChargedExplosives.getInstance().REGISTRY.CeItem.get());
            expBe.saveToItem(stack);
            ItemEntity ent = new ItemEntity(level, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, stack);
            ent.setDefaultPickUpDelay();
            level.addFreshEntity(ent);
        }
    }

    @Override
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof ChargedExplosiveBlockEntity cebe) {
            this.explode(level, pos, state, cebe.getExplosionWidth(), cebe.getExplosionHeight(), cebe.getExplosionDepth());
        }
        return InteractionResult.CONSUME;
    }

    private void explode(Level level, BlockPos pos, BlockState state, int width, int height, int depth) {
        Direction direction = state.getValue(FACING);
        AttachFace face = state.getValue(FACE);

        int startX, endX, startY, endY, startZ, endZ;
        switch (face) {
            case FLOOR -> {
                if (direction.getAxis() == Direction.Axis.X) {
                    startX = pos.getX() - height;
                    endX = pos.getX() + height + 1;
                    startY = pos.getY() - depth;
                    endY = pos.getY();
                    startZ = pos.getZ() - width;
                    endZ = pos.getZ() + width + 1;
                } else {
                    startX = pos.getX() - width;
                    endX = pos.getX() + width + 1;
                    startY = pos.getY() - depth;
                    endY = pos.getY();
                    startZ = pos.getZ() - height;
                    endZ = pos.getZ() + height + 1;
                }
                this._explode(level, startX, startY, startZ, endX, endY, endZ);
            }
            case WALL -> {
                switch (direction) {
                    case NORTH -> {
                        startX = pos.getX() - width;
                        endX = pos.getX() + width + 1;
                        startY = pos.getY() - height;
                        endY = pos.getY() + height + 1;
                        startZ = pos.getZ() + 1;
                        endZ = pos.getZ() + depth + 1;
                    }
                    case SOUTH -> {
                        startX = pos.getX() - width;
                        endX = pos.getX() + width + 1;
                        startY = pos.getY() - height;
                        endY = pos.getY() + height + 1;
                        startZ = pos.getZ() - depth;
                        endZ = pos.getZ();
                    }
                    case EAST -> {
                        startX = pos.getX() - depth;
                        endX = pos.getX();
                        startY = pos.getY() - height;
                        endY = pos.getY() + height + 1;
                        startZ = pos.getZ() - width;
                        endZ = pos.getZ() + width + 1;
                    }
                    case WEST -> {
                        startX = pos.getX() + 1;
                        endX = pos.getX() + depth + 1;
                        startY = pos.getY() - height;
                        endY = pos.getY() + height + 1;
                        startZ = pos.getZ() - width;
                        endZ = pos.getZ() + width + 1;
                    }
                    default -> {
                        //NOOP
                        startX = 0;
                        endX = 0;
                        startY = 0;
                        endY = 0;
                        startZ = 0;
                        endZ = 0;
                    }
                }
                this._explode(level, startX, startY, startZ, endX, endY, endZ);
            }
            case CEILING -> {
                if (direction.getAxis() == Direction.Axis.X) {
                    startX = pos.getX() - height;
                    endX = pos.getX() + height + 1;
                    startY = pos.getY();
                    endY = pos.getY() + depth + 1;
                    startZ = pos.getZ() - width;
                    endZ = pos.getZ() + width + 1;
                } else {
                    startX = pos.getX() - width;
                    endX = pos.getX() + width + 1;
                    startY = pos.getY();
                    endY = pos.getY() + depth + 1;
                    startZ = pos.getZ() - height;
                    endZ = pos.getZ() + height + 1;
                }
                this._explode(level, startX, startY, startZ, endX, endY, endZ);
            }
        }
        level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private void _explode(Level level, int startX, int startY, int startZ, int endX, int endY, int endZ) {
        for (int x = startX; x < endX; x++) {
            for (int z = startZ; z < endZ; z++) {
                for (int y = startY; y < endY; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    if (level.isClientSide()) {
                        level.addParticle(ParticleTypes.EXPLOSION, (double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, 1.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
    }
}

