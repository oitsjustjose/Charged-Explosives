package com.oitsjustjose.charged_explosives.client.render;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.oitsjustjose.charged_explosives.client.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;

import java.util.Set;

public class BlockDestroyRenderer {
    protected Set<Tuple<BlockPos, BlockPos>> highlightedBlocks = Sets.newConcurrentHashSet();

    public void addExplosion(Tuple<BlockPos, BlockPos> corners) {
        this.highlightedBlocks.add(corners);
    }

    public void removeExplosion(Tuple<BlockPos, BlockPos> corners) {
        this.highlightedBlocks.removeIf(pair -> pair.getA().equals(corners.getA()) && pair.getB().equals(corners.getB()));
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderLevelStageEvent event) {
        if (ClientConfig.ENABLE_EXPLOSION_PREVIEW_RENDER.get()) {
            this.renderSelectedBlocks(event);
        }
    }

    protected void renderSelectedBlocks(RenderLevelStageEvent event) {
        if (this.highlightedBlocks.isEmpty() || event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        this.highlightedBlocks.stream().filter(x -> x.getA().distToCenterSqr(mc.gameRenderer.getMainCamera().getPosition()) < 256D).forEach(pos -> {
            PoseStack stack = event.getPoseStack();
            MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();
            VertexConsumer builder = buf.getBuffer(BlockOutlineRenderType.OVERLAY_LINES);

            stack.pushPose();

            Vec3 proj = mc.gameRenderer.getMainCamera().getPosition();
            stack.translate(-proj.x, -proj.y, -proj.z);

            Matrix4f p = stack.last().pose();
            buildBlockOutline(builder, p, pos.getA(), pos.getB());

            stack.popPose();
            RenderSystem.disableDepthTest();
            buf.endBatch(BlockOutlineRenderType.OVERLAY_LINES);
        });
    }

    public static void buildBlockOutline(VertexConsumer buffer, Matrix4f positionMatrix, BlockPos start, BlockPos end) {
        buffer.vertex(positionMatrix, start.getX(), start.getY(), start.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, end.getX(), start.getY(), start.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, start.getX(), start.getY(), start.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, start.getX(), end.getY(), start.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, start.getX(), start.getY(), start.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, start.getX(), start.getY(), end.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, end.getX(), end.getY(), end.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, start.getX(), end.getY(), end.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, end.getX(), end.getY(), end.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, end.getX(), start.getY(), end.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, end.getX(), end.getY(), end.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, end.getX(), end.getY(), start.getZ()).color(1F, 0F, 0F, 1F).endVertex();

        buffer.vertex(positionMatrix, start.getX(), end.getY(), start.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, start.getX(), end.getY(), end.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, start.getX(), end.getY(), start.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, end.getX(), end.getY(), start.getZ()).color(1F, 0F, 0F, 1F).endVertex();

        buffer.vertex(positionMatrix, end.getX(), start.getY(), start.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, end.getX(), start.getY(), end.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, end.getX(), start.getY(), start.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, end.getX(), end.getY(), start.getZ()).color(1F, 0F, 0F, 1F).endVertex();

        buffer.vertex(positionMatrix, start.getX(), start.getY(), end.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, end.getX(), start.getY(), end.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, start.getX(), start.getY(), end.getZ()).color(1F, 0F, 0F, 1F).endVertex();
        buffer.vertex(positionMatrix, start.getX(), end.getY(), end.getZ()).color(1F, 0F, 0F, 1F).endVertex();
    }
}
