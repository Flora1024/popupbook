package com.flora.popupbook.registry;

import com.flora.popupbook.PopupBook;
import com.flora.popupbook.block.forgery.ForgeryBlock;
import com.flora.popupbook.block.musicbox.MusicBoxBlock;
import com.flora.popupbook.block.starry.StarryBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(PopupBook.MODID);

    public static final Supplier<ForgeryBlock> FORGERY = registerBlock("forgery",
            () -> new ForgeryBlock(BlockBehaviour.Properties.of()));
    public static final Supplier<MusicBoxBlock> MUSIC_BOX = registerBlock("music_box",
            () -> new MusicBoxBlock(BlockBehaviour.Properties.of().noOcclusion()));
    public static final Supplier<StarryBlock> STARRY = BLOCKS.register("starry",
            () -> new StarryBlock(ModFluids.STARRY_FLUID_SOURCE.get(), BlockBehaviour.Properties.of().ofFullCopy(Blocks.WATER).lightLevel(state -> 30)));

    private static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> block) {
        Supplier<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    private static <T extends Block> void registerBlockItem(String name, Supplier<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
