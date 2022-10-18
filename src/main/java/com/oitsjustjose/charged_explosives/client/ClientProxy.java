package com.oitsjustjose.charged_explosives.client;

import com.oitsjustjose.charged_explosives.client.network.ClientExplosionParticlePacket;
import com.oitsjustjose.charged_explosives.client.network.ClientOpenGuiPacket;
import com.oitsjustjose.charged_explosives.client.network.ClientPreviewExplosionPacket;
import com.oitsjustjose.charged_explosives.client.render.BlockDestroyRenderer;
import com.oitsjustjose.charged_explosives.common.CommonProxy;
import com.oitsjustjose.charged_explosives.common.network.ExplosionParticlePacket;
import com.oitsjustjose.charged_explosives.common.network.OpenGuiPacket;
import com.oitsjustjose.charged_explosives.common.network.PreviewExplosionPacket;
import com.oitsjustjose.charged_explosives.common.network.UpdateNbtPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkDirection;

import java.util.Optional;

public class ClientProxy extends CommonProxy {
    public static BlockDestroyRenderer bdRenderer;

    @Override
    public void init() {
        CommonProxy.netMgr.networkWrapper.registerMessage(CommonProxy.disc++, OpenGuiPacket.class, OpenGuiPacket::encode, OpenGuiPacket::decode, ClientOpenGuiPacket::handleClient);
        CommonProxy.netMgr.networkWrapper.registerMessage(CommonProxy.disc++, PreviewExplosionPacket.class, PreviewExplosionPacket::encode, PreviewExplosionPacket::decode, ClientPreviewExplosionPacket::handleClient);
        CommonProxy.netMgr.networkWrapper.registerMessage(CommonProxy.disc++, ExplosionParticlePacket.class, ExplosionParticlePacket::encode, ExplosionParticlePacket::decode, ClientExplosionParticlePacket::handleClient);
        CommonProxy.netMgr.networkWrapper.registerMessage(CommonProxy.disc++, UpdateNbtPacket.class, UpdateNbtPacket::encode, UpdateNbtPacket::decode, UpdateNbtPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        bdRenderer = new BlockDestroyRenderer();
        MinecraftForge.EVENT_BUS.register(bdRenderer);
    }

    @Override
    public void updateItemNbt(ItemStack stack) {
        UpdateNbtPacket pkt = new UpdateNbtPacket(stack);
        CommonProxy.netMgr.networkWrapper.sendToServer(pkt);
    }

    public void startPreviewExplosion(Tuple<BlockPos, BlockPos> corners) {
        if(corners != null && corners.getA() != null && corners.getB() != null) {
            bdRenderer.addExplosion(corners);
        }
    }

    public void endPreviewExplosion(Tuple<BlockPos, BlockPos> corners) {
        if(corners != null && corners.getA() != null && corners.getB() != null) {
            bdRenderer.removeExplosion(corners);
        }
    }
}