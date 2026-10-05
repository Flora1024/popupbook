package com.flora.popupbook.registry;

import com.flora.popupbook.PopupBook;
import com.flora.popupbook.block.starry.StarryFluidType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;


public class ModFluids {

    /**
     * 注册表
     */
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, PopupBook.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, PopupBook.MODID);

    /**
     * 属性
     */
    private static BaseFlowingFluid.Properties starryProperties() {
        return new BaseFlowingFluid.Properties(STARRY_FLUID_TYPE, STARRY_FLUID_SOURCE, STARRY_FLUID_FLOWING)
                .bucket(ModItems.STARRY_BUCKET)
                .block(ModBlocks.STARRY);
    }

    /**
     * 注册
     */
    public static final Supplier<StarryFluidType> STARRY_FLUID_TYPE = FLUID_TYPES.register("starry",
            () -> new StarryFluidType(FluidType.Properties.create().canSwim(true).lightLevel(5)));
    public static final Supplier<FlowingFluid> STARRY_FLUID_SOURCE = FLUIDS.register("starry",
            () -> new BaseFlowingFluid.Source(starryProperties()));
    public static final Supplier<FlowingFluid> STARRY_FLUID_FLOWING = FLUIDS.register("starry_flowing",
            () -> new BaseFlowingFluid.Flowing(starryProperties()));
}
