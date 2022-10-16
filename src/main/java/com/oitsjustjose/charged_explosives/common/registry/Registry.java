package com.oitsjustjose.charged_explosives.common.registry;

import com.oitsjustjose.charged_explosives.ChargedExplosives;
import com.oitsjustjose.charged_explosives.common.blocks.ChargedExplosiveBlock;
import com.oitsjustjose.charged_explosives.common.items.ChargedExplosiveItem;
import com.oitsjustjose.charged_explosives.common.tile.ChargedExplosiveBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registry {
    public final DeferredRegister<Block> BlockRegistry = DeferredRegister.create(ForgeRegistries.BLOCKS, ChargedExplosives.MODID);
    public final DeferredRegister<Item> ItemRegistry = DeferredRegister.create(ForgeRegistries.ITEMS, ChargedExplosives.MODID);
    public final DeferredRegister<BlockEntityType<?>> BlockEntityTypeRegistry = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ChargedExplosives.MODID);

    public final RegistryObject<Block> CeBlock = BlockRegistry.register("charged_explosive", ChargedExplosiveBlock::new);
    public final RegistryObject<Item> CeItem = ItemRegistry.register("charged_explosive", () -> new ChargedExplosiveItem(CeBlock.get()));
    public final RegistryObject<BlockEntityType<ChargedExplosiveBlockEntity>> CeBlockEntityType = BlockEntityTypeRegistry.register("charged_explosive", () -> BlockEntityType.Builder.of(ChargedExplosiveBlockEntity::new, Blocks.AIR).build(null));
}
