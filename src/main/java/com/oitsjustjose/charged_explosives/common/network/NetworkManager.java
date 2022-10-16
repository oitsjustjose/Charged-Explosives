package com.oitsjustjose.charged_explosives.common.network;

import com.oitsjustjose.charged_explosives.ChargedExplosives;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkManager {
    public SimpleChannel networkWrapper;
    private static final String PROTOCOL_VERSION = "1";

    public NetworkManager(String nm) {
        this.networkWrapper = NetworkRegistry.newSimpleChannel(new ResourceLocation(ChargedExplosives.MODID, nm), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    }
}
