package com.oitsjustjose.charged_explosives.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;


public class BlockOutlineRenderType extends RenderType {
    private static final LineStateShard THICK_LINES = new LineStateShard(OptionalDouble.of(4.0D));
    public static final RenderType OVERLAY_LINES = create("overlay_lines",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES, 256, true, false,
            CompositeState.builder().setLineState(THICK_LINES)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setShaderState(RENDERTYPE_LINES_SHADER)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(NO_TEXTURE)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false));

    public BlockOutlineRenderType(String name, VertexFormat vertexFormat, VertexFormat.Mode drawMode, int bufferSize, boolean useDelegate, boolean needsSorting, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(name, vertexFormat, drawMode, bufferSize, useDelegate, needsSorting, setupTaskIn, clearTaskIn);
    }
}
