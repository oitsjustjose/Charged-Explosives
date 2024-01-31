package com.oitsjustjose.charged_explosives;

import com.oitsjustjose.charged_explosives.client.ClientProxy;
import com.oitsjustjose.charged_explosives.client.config.ClientConfig;
import com.oitsjustjose.charged_explosives.common.CommonProxy;
import com.oitsjustjose.charged_explosives.common.TickScheduler;
import com.oitsjustjose.charged_explosives.common.config.CommonConfig;
import com.oitsjustjose.charged_explosives.common.registry.Registry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ChargedExplosives.MODID)
public class ChargedExplosives {
    private static ChargedExplosives instance;
    public static final String MODID = "charged_explosives";
    public final Logger LOGGER = LogManager.getLogger();
    public final Registry REGISTRY = new Registry();
    public final CommonProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    public final TickScheduler SCHEDULER = new TickScheduler();

    public ChargedExplosives() {
        instance = this;
        REGISTRY.BlockRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
        REGISTRY.ItemRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
        REGISTRY.BlockEntityTypeRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
        REGISTRY.SoundEventRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(SCHEDULER);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        this.configSetup();
    }

    public static ChargedExplosives getInstance() {
        return instance;
    }

    public void setup(final FMLCommonSetupEvent evt) {
        PROXY.init();
    }

    private void configSetup() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);
        CommonConfig.loadConfig(CommonConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));
        ClientConfig.loadConfig(ClientConfig.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml"));
    }

    @SubscribeEvent
    public void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(REGISTRY.CeItem);
        }
    }
}