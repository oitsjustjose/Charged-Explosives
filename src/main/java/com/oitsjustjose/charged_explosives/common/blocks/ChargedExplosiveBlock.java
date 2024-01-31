package com.oitsjustjose.charged_explosives.common.blocks;

import com.oitsjustjose.charged_explosives.ChargedExplosives;
import com.oitsjustjose.charged_explosives.common.TickScheduler;
import com.oitsjustjose.charged_explosives.common.config.CommonConfig;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class ChargedExplosiveBlock extends FaceAttachedHorizontalDirectionalBlock implements EntityBlock {


    protected static final VoxelShape CEILING_AABB_X = Block.box(4D, 13D, 2D, 12D, 16D, 14D);
    protected static final VoxelShape CEILING_AABB_Z = Block.box(2D, 13D, 4D, 14D, 16D, 12D);
    protected static final VoxelShape FLOOR_AABB_X = Block.box(4D, 0D, 2D, 12D, 3D, 14D);
    protected static final VoxelShape FLOOR_AABB_Z = Block.box(2D, 0D, 4D, 14D, 3D, 12D);
    protected static final VoxelShape SOUTH_AABB = Block.box(2D, 4D, 0D, 14D, 12D, 3D);
    protected static final VoxelShape NORTH_AABB = Block.box(2D, 4D, 13D, 14D, 12D, 16D);
    protected static final VoxelShape WEST_AABB = Block.box(13D, 4D, 2D, 16D, 12D, 14D);
    protected static final VoxelShape EAST_AABB = Block.box(0D, 4D, 2D, 3D, 12D, 14D);

    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    public ChargedExplosiveBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_GREEN).dynamicShape().noOcclusion().isValidSpawn((w, x, y, z) -> false).isRedstoneConductor((x, y, z) -> false).isSuffocating((x, y, z) -> false).isViewBlocking((x, y, z) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL).setValue(ACTIVATED, false));
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
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return this.getShape(state, level, pos, context);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> Builder) {
        Builder.add(FACING, FACE, ACTIVATED);
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
        if (placer.getOffhandItem().getItem() == Items.REDSTONE_TORCH) {
            this.explode(level, pos);
        } else if (level.hasNeighborSignal(pos)) {
            this.explode(level, pos);
        }
    }

    @Override
    public void onRemove(@NotNull BlockState oldState, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean moving) {
        // Updating state? keep it there.
        if (oldState.getBlock() == newState.getBlock()) {
            return;
        }

        if (level.getBlockEntity(pos) instanceof ChargedExplosiveBlockEntity ent) {
            ChargedExplosives.getInstance().PROXY.endPreviewExplosion(ent.getCorners());
            ItemStack stack = new ItemStack(ChargedExplosives.getInstance().REGISTRY.CeItem.get());
            ent.saveToItem(stack);
            ItemEntity itemEntity = new ItemEntity(level, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, stack);
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }

        level.removeBlockEntity(pos);
        super.onRemove(oldState, level, pos, newState, moving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ChargedExplosiveBlockEntity(pos, state);
    }

    @Override
    public @NotNull InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (level.isClientSide()) {
            player.swing(hand);
            return InteractionResult.SUCCESS;
        }
        this.explode(level, pos);
        return InteractionResult.CONSUME;
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        if (level.hasNeighborSignal(pos)) {
            if (level.getBlockEntity(pos) instanceof ChargedExplosiveBlockEntity) {
                if (!state.getValue(ACTIVATED)) {
                    this.explode(level, pos);
                }
            }
        }
    }

    private void explode(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ChargedExplosiveBlockEntity ent) {
            BlockState state = level.getBlockState(pos);
            if (state.getValue(ACTIVATED)) {
                return;
            } else {
                level.setBlock(pos, state.setValue(ACTIVATED, true), 2 | 16);
            }


            ent.addScheduledTask(ChargedExplosives.getInstance().SCHEDULER.addTask(new TickScheduler.ScheduledTask(() -> this.explodeTask(level, pos), CommonConfig.EXPLOSION_COUNTDOWN_TIME.get())));
            for (int i = 0; i < CommonConfig.NUM_BEEPS.get(); i++) {
                ent.addScheduledTask(ChargedExplosives.getInstance().SCHEDULER.addTask(new TickScheduler.ScheduledTask(() -> {
                    if (level.getBlockEntity(pos) instanceof ChargedExplosiveBlockEntity) { // check that it's still there
                        level.playSound(null, pos, ChargedExplosives.getInstance().REGISTRY.BeepSound.get(), SoundSource.BLOCKS, 0.75F, 1.0F);
                    }
                }, (CommonConfig.EXPLOSION_COUNTDOWN_TIME.get() / CommonConfig.NUM_BEEPS.get()) * i)));
            }
        }

    }


    private void explodeTask(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ChargedExplosiveBlockEntity ent) {

            level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.0F, 1.0F);
            ChargedExplosives.getInstance().PROXY.endPreviewExplosion(ent.getCorners());
            level.removeBlockEntity(pos);
            level.removeBlock(pos, false);

            ent.getExplosions().forEach(p -> {
                BlockState state = level.getBlockState(p);
                if (!state.hasBlockEntity()) {
                    /* indestructible blocks will return -1.0F */
                    if (state.getBlock().defaultDestroyTime() >= 0.0 && !state.isAir()) {
                        try {
                            explodeBlock(level, p);
                        } catch (ArrayIndexOutOfBoundsException ignored) { // it.unimi.dsi.fastutil crash prevention
                            ChargedExplosives.getInstance().SCHEDULER.addTask(new TickScheduler.ScheduledTask(() -> explodeBlock(level, p), level.getRandom().nextInt(3) + 1));
                        }
                    }
                }
            });

            AABB aabb = new AABB(ent.getCorners().getA(), ent.getCorners().getB()).inflate(CommonConfig.CONCUSSIVE_DAMAGE_PADDING.get());
            for (int x = (int) aabb.minX; x < (int) aabb.maxX; x++) {
                for (int y = (int) aabb.minY; y < (int) aabb.maxY; y++) {
                    for (int z = (int) aabb.minZ; z < (int) aabb.maxZ; z++) {
                        if (level.getRandom().nextInt(4) == 3) {
                            ChargedExplosives.getInstance().PROXY.spawnExplosionParticle(new BlockPos(x, y, z));
                        }
                    }
                }
            }

            float dmgAmt = ent.calculateConcussiveDamage();
            List<LivingEntity> entities = level.getEntities(EntityTypeTest.forClass(LivingEntity.class), aabb, Objects::nonNull);
            entities.forEach(x -> x.hurt(level.damageSources().generic(), dmgAmt));
        }
    }

    private void explodeBlock(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        level.removeBlock(pos, false);
        /* Ignore the yellow squiggles below - p_49886 is passed to a func where it's Nullable */
        if (CommonConfig.EXPLOSION_DROPS.get()) {
            Block.dropResources(state, level, pos, null, null, ItemStack.EMPTY);
        }
    }
}

