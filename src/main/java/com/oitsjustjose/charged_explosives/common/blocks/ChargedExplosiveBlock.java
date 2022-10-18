package com.oitsjustjose.charged_explosives.common.blocks;

import com.oitsjustjose.charged_explosives.ChargedExplosives;
import com.oitsjustjose.charged_explosives.common.tile.ChargedExplosiveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
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
    public @NotNull VoxelShape getCollisionShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return this.getShape(state, level, pos, context);
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
        be.load(tag);
    }

    @Override
    public void onRemove(BlockState oldState, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean moving) {
        if (!newState.is(oldState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof ChargedExplosiveBlockEntity cebe) {
                ChargedExplosives.getInstance().proxy.endPreviewExplosion(cebe.getCorners());
            }
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
        if (player.isCreative()) {
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
        if (level.isClientSide()) {
            player.swing(hand);
            return InteractionResult.SUCCESS;
        }
        this.explode(level, pos);
        return InteractionResult.CONSUME;
    }


    private void explode(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ChargedExplosiveBlockEntity cebe) {
            level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.0F, 1.0F);
            ChargedExplosives.getInstance().proxy.endPreviewExplosion(cebe.getCorners());
            // ↥ WARNING: the below block will *destroy* cebe, so anything using should go here ↥
            cebe.getExplosions().forEach(p -> {

//                level.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
                ChargedExplosives.getInstance().proxy.spawnExplosionParticle(p);
            });
        }
    }
}

