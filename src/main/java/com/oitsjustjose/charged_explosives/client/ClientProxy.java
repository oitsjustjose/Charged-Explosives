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
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

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

    public void openExplosiveGui(Player player, ItemStack stack) {
        super.openExplosiveGui(player, stack);
    }

    @Override
    public void startPreviewExplosion(Tuple<BlockPos, BlockPos> corners) {
        bdRenderer.addExplosion(corners);
//        super.startPreviewExplosion(corners);
    }

    @Override
    public void endPreviewExplosion(Tuple<BlockPos, BlockPos> corners) {
        bdRenderer.removeExplosion(corners);
//        super.endPreviewExplosion(corners);
    }

    public void spawnExplosionParticle(BlockPos pos) {
        if (Minecraft.getInstance().level != null) {
            Minecraft.getInstance().level.addParticle(
                    ParticleTypes.EXPLOSION,
                    (double) pos.getX() + 0.5D,
                    (double) pos.getY() + 0.5D,
                    (double) pos.getZ() + 0.5D,
                    1.0D, 0.0D, 0.0D
            );
        }
    }
}