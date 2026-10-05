package com.flora.popupbook.item.skyeye.client;

import com.flora.popupbook.PopupBook;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/**
 * 附身/退出附身时视角切换的“眨眼”过渡。
 *
 * <p>纯客户端事件实现，无 mixin：客户端 tick 内收到 ClientboundSetCameraPacket 后相机实体立刻切换，
 * 本帧在 ClientTickEvent.Post 检测到变化并启动动画，随后同一帧的 RenderGuiEvent.Post 画全屏黑
 * 盖住切换瞬间，再在 FADE_MS 内淡出（“睁眼”）。
 */
@EventBusSubscriber(modid = PopupBook.MODID, value = Dist.CLIENT)
public final class PossessionBlink {

    /** 全黑到完全透明的时长（毫秒） */
    private static final long FADE_MS = 750L;

    /** 动画起始时间戳，-1 表示未在播放 */
    private static long animStartMillis = -1L;
    private static Entity lastCameraEntity;
    private static boolean initialized;

    private PossessionBlink() {
    }

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Entity camera = mc.getCameraEntity();
        if (camera == null) {
            return;
        }
        if (!initialized) {
            initialized = true;
            lastCameraEntity = camera;
            return;
        }
        if (lastCameraEntity != camera) {
            animStartMillis = Util.getMillis();
        }
        lastCameraEntity = camera;
    }

    @SubscribeEvent
    static void onRenderGui(RenderGuiEvent.Post event) {
        if (animStartMillis < 0L) {
            return;
        }
        long elapsed = Util.getMillis() - animStartMillis;
        if (elapsed >= FADE_MS) {
            animStartMillis = -1L;
            return;
        }
        // 1 -> 0 的透明度，平方缓出，接近“睁眼”的感觉
        float progress = 1.0F - elapsed / (float) FADE_MS;
        int alpha = (int) (255.0F * progress * progress);
        GuiGraphics guiGraphics = event.getGuiGraphics();
        guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), alpha << 24);
    }
}
