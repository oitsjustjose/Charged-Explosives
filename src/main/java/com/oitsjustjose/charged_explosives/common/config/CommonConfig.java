package com.oitsjustjose.charged_explosives.common.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class CommonConfig {
    public static final ForgeConfigSpec COMMON_CONFIG;
    private static final Builder COMMON_BUILDER = new Builder();

    public static ForgeConfigSpec.DoubleValue CONCUSSIVE_DAMAGE_SCALE;
    public static ForgeConfigSpec.IntValue CONCUSSIVE_DAMAGE_PADDING;
    public static ForgeConfigSpec.IntValue MAX_EXPLOSION_WIDTH;
    public static ForgeConfigSpec.IntValue MAX_EXPLOSION_HEIGHT;
    public static ForgeConfigSpec.IntValue MAX_EXPLOSION_DEPTH;
    public static ForgeConfigSpec.IntValue EXPLOSION_COUNTDOWN_TIME;
    public static ForgeConfigSpec.IntValue NUM_BEEPS;
    public static ForgeConfigSpec.BooleanValue EXPLOSION_DROPS;
    private static final String CATEGORY_MISC = "common_configuration";

    static {
        init();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave()
                .writingMode(WritingMode.REPLACE).build();
        configData.load();
        spec.setConfig(configData);
    }

    private static void init() {
        COMMON_BUILDER.comment("Miscellaneous").push(CATEGORY_MISC);

        CONCUSSIVE_DAMAGE_SCALE = COMMON_BUILDER.comment("A percentage (decimal from 0-1) of how much concussive damage should be done by charges. 0 disables concussive damage.",
                        "Values greater than 0 will hurt anything in a radius of the blast by an amount (calculated by the explosive's width, depth and height) times this value.")
                .defineInRange("concussiveDamageScale", 0.1, 0.0, 1.0);
        CONCUSSIVE_DAMAGE_PADDING = COMMON_BUILDER.comment("The number of blocks outside of the blast radius that a blast can give concussive damage. Set to 0 to disable extra concussive damage range")
                .defineInRange("extraConcussiveDamageRange", 5, 0, 64);
        MAX_EXPLOSION_WIDTH = COMMON_BUILDER.comment("The max explosion width a user can set in their explosive").defineInRange("maxExplosionWidth", 11, 1, 128);
        MAX_EXPLOSION_HEIGHT = COMMON_BUILDER.comment("The max explosion height a user can set in their explosive").defineInRange("maxExplosionHeight", 11, 1, 128);
        MAX_EXPLOSION_DEPTH = COMMON_BUILDER.comment("The max explosion depth a user can set in their explosive").defineInRange("maxExplosionDepth", 11, 1, 128);

        EXPLOSION_COUNTDOWN_TIME = COMMON_BUILDER.comment("The amount of time between activating an explosive and when it detonates, in seconds. Setting to 0 makes it instant")
                .defineInRange("detonationCountdownTime", 5, 0, Integer.MAX_VALUE);
        NUM_BEEPS = COMMON_BUILDER.comment("The number of warning beeps given after setting an explosive off to detonate. Time is distributed evenly across the value configured in 'detonationCountdownTime'")
                .defineInRange("detonationBeepCount", 5, 0, Integer.MAX_VALUE);

        EXPLOSION_DROPS = COMMON_BUILDER.comment("Controls if a charge's explosion should provide drops. Setting this to true will drop items, false will not")
                .define("explosionsDropItems", true);

        COMMON_BUILDER.pop();
    }
}