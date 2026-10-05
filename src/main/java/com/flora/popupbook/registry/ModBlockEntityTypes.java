package com.flora.popupbook.registry;

import com.flora.popupbook.PopupBook;
import com.flora.popupbook.block.forgery.ForgeryBlockEntity;
import com.flora.popupbook.block.musicbox.MusicBoxBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, PopupBook.MODID);

    public static final Supplier<BlockEntityType<ForgeryBlockEntity>> FORGERY = BLOCK_ENTITIES.register("forgery",
            () -> BlockEntityType.Builder.of(ForgeryBlockEntity::new, ModBlocks.FORGERY.get()).build(null));

    public static final Supplier<BlockEntityType<MusicBoxBlockEntity>> MUSIC_BOX = BLOCK_ENTITIES.register("music_box",
            () -> BlockEntityType.Builder.of(MusicBoxBlockEntity::new, ModBlocks.MUSIC_BOX.get()).build(null));
}
