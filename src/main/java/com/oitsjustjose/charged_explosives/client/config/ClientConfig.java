package com.oitsjustjose.charged_explosives.client.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class ClientConfig {
    public static final ForgeConfigSpec CLIENT_CONFIG;
    private static final Builder CLIENT_BUILDER = new Builder();

    public static ForgeConfigSpec.BooleanValue ENABLE_EXPLOSION_PREVIEW_RENDER;
    private static final String CATEGORY_CLIENT = "client_configs";

    static {
        init();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        spec.setConfig(configData);
    }

    private static void init() {
        CLIENT_BUILDER.comment("Client Configuration Settings").push(CATEGORY_CLIENT);
        ENABLE_EXPLOSION_PREVIEW_RENDER = CLIENT_BUILDER.comment("Enable a red box showing which blocks will be destroyed?").define("enableExplosionPreviewRender", true);
        CLIENT_BUILDER.pop();
    }
}