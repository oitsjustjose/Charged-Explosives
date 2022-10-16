package com.oitsjustjose.charged_explosives;

import com.oitsjustjose.charged_explosives.client.ClientProxy;
import com.oitsjustjose.charged_explosives.common.CommonProxy;
import com.oitsjustjose.charged_explosives.common.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ChargedExplosives.MODID)
public class ChargedExplosives {
    private static ChargedExplosives instance;
    public static final String MODID = "charged_explosives";
    public final Logger LOGGER = LogManager.getLogger();
    public final Registry REGISTRY = new Registry();
    public final CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public ChargedExplosives() {
        instance = this;
        REGISTRY.BlockRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
        REGISTRY.ItemRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    public static ChargedExplosives getInstance() {
        return instance;
    }

    public void setup(final FMLCommonSetupEvent evt) {
        proxy.init();
    }

    @SubscribeEvent
    public void onSlashReload(AddReloadListenerEvent evt) {
    }

}