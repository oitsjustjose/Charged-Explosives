package com.oitsjustjose.charged_explosives.client;

import com.oitsjustjose.charged_explosives.client.network.ClientOpenGuiPacket;
import com.oitsjustjose.charged_explosives.client.render.BlockDestroyRenderer;
import com.oitsjustjose.charged_explosives.common.CommonProxy;
import com.oitsjustjose.charged_explosives.common.network.OpenGuiPacket;
import com.oitsjustjose.charged_explosives.common.network.UpdateNbtPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkDirection;

import java.util.Optional;

public class ClientProxy extends CommonProxy {
    private BlockDestroyRenderer bdRenderer = new BlockDestroyRenderer();

    @Override
    public void init() {
        CommonProxy.netMgr.networkWrapper.registerMessage(CommonProxy.disc++, OpenGuiPacket.class, OpenGuiPacket::encode, OpenGuiPacket::decode, ClientOpenGuiPacket::handleClient);
        CommonProxy.netMgr.networkWrapper.registerMessage(CommonProxy.disc++, UpdateNbtPacket.class, UpdateNbtPacket::encode, UpdateNbtPacket::decode, UpdateNbtPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
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
    public void addBlockToRender(Tuple<BlockPos, BlockPos> pos) {
        bdRenderer.selectBlock(pos);
    }

    @Override
    public void removeBlockFromRender(Tuple<BlockPos, BlockPos> pos) {
        bdRenderer.unselectBlock(pos);
    }
}