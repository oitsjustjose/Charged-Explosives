package com.oitsjustjose.charged_explosives.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.oitsjustjose.charged_explosives.ChargedExplosives;
import com.oitsjustjose.charged_explosives.common.Util;
import com.oitsjustjose.charged_explosives.common.config.CommonConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ScreenEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public class ExplosionConfigScreen extends Screen {

    /* Instance-related stuff that changes regularly */
    private int explosionWidth = 0;
    private int explosionHeight = 0;
    private int explosionDepth = 0;
    private final ItemStack stack;
    private boolean dirty = false;

    /* Refs to UI objects */
    private EditBox widthBox;
    private EditBox depthBox;
    private EditBox heightBox;
    private final Component widthTitle = Util.translateOrFallback(ChargedExplosives.MODID + ".explosion.width.title");
    private final Component depthTitle = Util.translateOrFallback(ChargedExplosives.MODID + ".explosion.depth.title");
    private final Component heightTitle = Util.translateOrFallback(ChargedExplosives.MODID + ".explosion.height.title");

    /* Constants */
    private final int TEXTURE_WIDTH = 176;
    private final int TEXTURE_HEIGHT = 166;
    private final int PADDING = 8;
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(ChargedExplosives.MODID, "textures/gui/background.png");

    public ExplosionConfigScreen(ItemStack stack) {
        super(Util.translateOrFallback(ChargedExplosives.MODID + ".explosion.configuration.title"));
        this.stack = stack;
        assert !this.stack.isEmpty() && this.stack.getTag() != null;

        if (this.stack.getTag().contains("explosionWidth") && this.stack.getTag().contains("explosionDepth") && this.stack.getTag().contains("explosionHeight")) {
            CompoundTag tag = this.stack.getTag();
            this.explosionWidth = tag.getInt("explosionWidth");
            this.explosionDepth = tag.getInt("explosionDepth");
            this.explosionHeight = tag.getInt("explosionHeight");
        }
    }

    @Override
    protected void init() {
        super.init();
        final Component INCR = Component.literal("+");
        final Component DECR = Component.literal("-");
        final int INCR_W = this.font.width(INCR) + PADDING;
        final int DECR_W = this.font.width(DECR) + PADDING;
        final int BG_X = (this.width - TEXTURE_WIDTH) / 2;
        final int BG_Y = (this.height - TEXTURE_HEIGHT) / 2;

        final int FW_MOD = 0;
        final int EB_MOD = 20;
        final int LW_MOD = 65;
        final int EDIT_BOX_WIDTH = 40;

        int rowWidth =/* Width of actual els */ INCR_W + DECR_W + EDIT_BOX_WIDTH + (LW_MOD - EDIT_BOX_WIDTH) /* Offset for the left btn */ + (LW_MOD - EDIT_BOX_WIDTH - EB_MOD) /* Offset for the right btn */;

        /* Width Adj */
        this.addRenderableWidget(new Button(BG_X + ((rowWidth / 2) - 1) + FW_MOD, BG_Y + 40, DECR_W, 20, DECR, onPress -> {
            if (this.explosionWidth > 1) {
                this.explosionWidth--;
                this.dirty = true;
                this.widthBox.setValue("" + this.explosionWidth);
            }
        }));
        this.widthBox = this.addRenderableWidget(new EditBox(this.font, BG_X + ((rowWidth / 2) - 1) + EB_MOD, BG_Y + 40, EDIT_BOX_WIDTH, 20, Component.literal("")));
        this.widthBox.setValue("" + this.explosionWidth);
        this.addRenderableWidget(new Button(BG_X + ((rowWidth / 2) - 1) + LW_MOD, BG_Y + 40, INCR_W, 20, INCR, onPress -> {
            if (this.explosionWidth < CommonConfig.MAX_EXPLOSION_WIDTH.get()) {
                this.explosionWidth++;
                this.dirty = true;
                this.widthBox.setValue("" + this.explosionWidth);
            }
        }));

        /* Depth Adj */
        this.addRenderableWidget(new Button(BG_X + ((rowWidth / 2) - 1) + FW_MOD, BG_Y + 85, DECR_W, 20, DECR, onPress -> {
            if (this.explosionDepth > 1) {
                this.explosionDepth--;
                this.dirty = true;
                this.depthBox.setValue("" + this.explosionDepth);
            }
        }));
        this.depthBox = this.addRenderableWidget(new EditBox(this.font, BG_X + ((rowWidth / 2) - 1) + EB_MOD, BG_Y + 85, EDIT_BOX_WIDTH, 20, Component.literal("")));
        this.depthBox.setValue("" + this.explosionDepth);
        this.addRenderableWidget(new Button(BG_X + ((rowWidth / 2) - 1) + LW_MOD, BG_Y + 85, INCR_W, 20, INCR, onPress -> {
            if (this.explosionDepth < CommonConfig.MAX_EXPLOSION_DEPTH.get()) {
                this.explosionDepth++;
                this.dirty = true;
                this.depthBox.setValue("" + this.explosionDepth);
            }
        }));

        /* Height Adj */
        this.addRenderableWidget(new Button(BG_X + ((rowWidth / 2) - 1) + FW_MOD, BG_Y + 130, DECR_W, 20, DECR, onPress -> {
            if (this.explosionHeight > 1) {
                this.explosionHeight--;
                this.dirty = true;
                this.heightBox.setValue("" + this.explosionHeight);
            }
        }));
        this.heightBox = this.addRenderableWidget(new EditBox(this.font, BG_X + ((rowWidth / 2) - 1) + EB_MOD, BG_Y + 130, EDIT_BOX_WIDTH, 20, Component.literal("")));
        this.heightBox.setValue("" + this.explosionHeight);
        this.addRenderableWidget(new Button(BG_X + ((rowWidth / 2) - 1) + LW_MOD, BG_Y + 130, INCR_W, 20, INCR, onPress -> {
            if (this.explosionHeight < CommonConfig.MAX_EXPLOSION_HEIGHT.get()) {
                this.explosionHeight++;
                this.dirty = true;
                this.heightBox.setValue("" + this.explosionHeight);
            }
        }));
    }

    @Override
    public void onClose() {
        if (dirty) {
            CompoundTag tag = Objects.requireNonNull(this.stack.getTag());
            tag.putInt("explosionWidth", explosionWidth);
            tag.putInt("explosionDepth", explosionDepth);
            tag.putInt("explosionHeight", explosionHeight);
            ChargedExplosives.getInstance().PROXY.updateItemNbt(stack);
        }
        super.onClose();
    }

    @Override
    public void tick() {
        final int WHITE = 16777215;
        final int RED = 16711680;
        /* Tick each edit box */
        try {
            this.widthBox.tick();
            int value = Integer.parseInt(this.widthBox.getValue());
            if (value <= 0 || value > CommonConfig.MAX_EXPLOSION_WIDTH.get()) {
                this.widthBox.setTextColor(RED);
            } else {
                this.widthBox.setTextColor(WHITE);
                if (value != explosionWidth) {
                    this.explosionWidth = value;
                    this.dirty = true;
                }
            }
        } catch (NumberFormatException ex) {
            this.widthBox.setTextColor(RED);
        }

        try {
            this.depthBox.tick();
            int value = Integer.parseInt(this.depthBox.getValue());
            if (value <= 0 || value > CommonConfig.MAX_EXPLOSION_DEPTH.get()) {
                this.depthBox.setTextColor(RED);
            } else {
                this.depthBox.setTextColor(WHITE);
                if (value != explosionDepth) {
                    this.explosionDepth = value;
                    this.dirty = true;
                }
            }
        } catch (NumberFormatException ex) {
            this.depthBox.setTextColor(RED);
        }

        try {
            this.heightBox.tick();
            int value = Integer.parseInt(this.heightBox.getValue());
            if (value <= 0 || value > CommonConfig.MAX_EXPLOSION_HEIGHT.get()) {
                this.heightBox.setTextColor(RED);
            } else {
                this.heightBox.setTextColor(WHITE);
                if (value != explosionHeight) {
                    this.explosionHeight = value;
                    this.dirty = true;
                }
            }
        } catch (NumberFormatException ex) {
            this.heightBox.setTextColor(RED);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        final int BG_X = (this.width - TEXTURE_WIDTH) / 2;
        final int BG_Y = (this.height - TEXTURE_HEIGHT) / 2;

        this.renderBackground(stack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        this.blit(stack, (this.width - TEXTURE_WIDTH) / 2, (this.height - TEXTURE_HEIGHT) / 2, 0, 0, TEXTURE_WIDTH, TEXTURE_WIDTH);


        this.font.draw(stack, this.title, BG_X + (float) (TEXTURE_WIDTH / 2) - (float) (this.font.width(this.title) / 2), BG_Y + PADDING, 4210752);

        /* Width Title */
        this.font.draw(stack, this.widthTitle, BG_X + (float) (TEXTURE_WIDTH / 2) - (float) (this.font.width(this.widthTitle) / 2), BG_Y + 20 + this.font.lineHeight, 7089453);
        /* Depth Title */
        this.font.draw(stack, this.depthTitle, BG_X + (float) (TEXTURE_WIDTH / 2) - (float) (this.font.width(this.widthTitle) / 2), BG_Y + 65 + this.font.lineHeight, 7089453);
        /* Height Title */
        this.font.draw(stack, this.heightTitle, BG_X + (float) (TEXTURE_WIDTH / 2) - (float) (this.font.width(this.widthTitle) / 2), BG_Y + 110 + this.font.lineHeight, 7089453);

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(@NotNull PoseStack stack, int _unused) {
        this.fillGradient(stack, 0, 0, this.width, this.height, -1072689136, -804253680);
    }
}
