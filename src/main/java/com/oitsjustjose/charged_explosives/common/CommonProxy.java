package com.oitsjustjose.charged_explosives.common;

import com.oitsjustjose.charged_explosives.common.network.NetworkManager;
import com.oitsjustjose.charged_explosives.common.network.OpenGuiPacket;
import com.oitsjustjose.charged_explosives.common.network.UpdateNbtPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;

public class CommonProxy {

    public static NetworkManager netMgr = new NetworkManager("server");

    public static int disc = 0;

    public void init() {
        CommonProxy.netMgr.networkWrapper.registerMessage(CommonProxy.disc++, OpenGuiPacket.class, OpenGuiPacket::encode, OpenGuiPacket::decode, OpenGuiPacket::handleServer);
        CommonProxy.netMgr.networkWrapper.registerMessage(CommonProxy.disc++, UpdateNbtPacket.class, UpdateNbtPacket::encode, UpdateNbtPacket::decode, UpdateNbtPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public void openExplosiveGui(Player player, ItemStack stack) {
        if (player instanceof ServerPlayer spl) {
            OpenGuiPacket pkt = new OpenGuiPacket(stack);
            CommonProxy.netMgr.networkWrapper.send(PacketDistributor.PLAYER.with(() -> spl), pkt);
        }
    }

    public void updateItemNbt(ItemStack stack) {
        // NOOP
    }

    public void addBlockToRender(Tuple<BlockPos, BlockPos> pos) {
    }

    public void removeBlockFromRender(Tuple<BlockPos, BlockPos> pos) {
    }
}