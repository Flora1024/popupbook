package com.flora.popupbook;

import com.flora.popupbook.block.forgery.ForgeryBlockEntity;
import com.flora.popupbook.block.forgery.client.ForgeryGeometryLoader;
import com.flora.popupbook.block.musicbox.client.MusicBoxBlockEntityRenderer;
import com.flora.popupbook.entity.renderer.SphereEntityRenderer;
import com.flora.popupbook.particle.MagicSphereTrailParticle;
import com.flora.popupbook.registry.ModBlocks;
import com.flora.popupbook.registry.ModBlockEntityTypes;
import com.flora.popupbook.registry.ModEntities;
import com.flora.popupbook.registry.ModParticles;
import com.flora.popupbook.registry.ModFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = PopupBook.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = PopupBook.MODID, value = Dist.CLIENT)
public class PopupBookClient {
    public PopupBookClient(IEventBus modEventBus, ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(PopupBookClient::registerGeometryLoaders);
        modEventBus.addListener(PopupBookClient::registerBlockColors);
        modEventBus.addListener(PopupBookClient::registerBlockEntityRenderers);
        modEventBus.addListener(PopupBookClient::registerFluidExtensions);
        modEventBus.addListener(PopupBookClient::setupFluidRendering);
    }

    private static void registerFluidExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private final ResourceLocation still = ResourceLocation.fromNamespaceAndPath(PopupBook.MODID, "block/starry_still");
            private final ResourceLocation flowing = ResourceLocation.fromNamespaceAndPath(PopupBook.MODID, "block/starry_flowing");

            @Override
            public ResourceLocation getStillTexture() {
                return still;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return flowing;
            }
        }, ModFluids.STARRY_FLUID_TYPE.get());
    }

    private static void setupFluidRendering(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModFluids.STARRY_FLUID_SOURCE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.STARRY_FLUID_FLOWING.get(), RenderType.translucent());
        });
    }

    private static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntityTypes.MUSIC_BOX.get(),
                ctx -> new MusicBoxBlockEntityRenderer());
    }

    private static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ResourceLocation.fromNamespaceAndPath(PopupBook.MODID, "forgery"), ForgeryGeometryLoader.INSTANCE);
    }

    private static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.getBlockColors().register((state, level, pos, tintIndex) -> {
            if (level == null || pos == null) {
                return -1;
            }
            if (level.getBlockEntity(pos) instanceof ForgeryBlockEntity forgery) {
                BlockState targetState = forgery.getAuthenticity().defaultBlockState();
                if (targetState.is(ModBlocks.FORGERY.get())) {
                    return -1;
                }
                return event.getBlockColors().getColor(targetState, level, pos, tintIndex);
            }
            return -1;
        }, ModBlocks.FORGERY.get());
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        PopupBook.LOGGER.info("HELLO FROM CLIENT SETUP");
        PopupBook.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MAGIC_SPHERE.get(), SphereEntityRenderer::new);
    }

    @SubscribeEvent
    static void onRegisterParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.MAGIC_SPHERE_TRAIL_PARTICLES.get(), MagicSphereTrailParticle.Provider::new);
    }

    @SubscribeEvent
    static void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getCameraEntity() != null && mc.getCameraEntity() != mc.player) {
            event.setCanceled(true);   // 附身中：不渲染主手/副手
        }
    }
}
