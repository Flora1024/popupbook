package com.flora.popupbook.item.staff.client;

import com.flora.popupbook.PopupBook;
import com.flora.popupbook.item.staff.StaffItem;
import com.flora.popupbook.item.staff.StaffTarget;
import com.flora.popupbook.item.staff.StaffTargeting;
import com.flora.popupbook.registry.ModDataComponentTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;

/** A camera-facing reticle drawn once per frame, without particles or shared target state. */
@EventBusSubscriber(modid = PopupBook.MODID, value = Dist.CLIENT)
public final class StaffTargetRenderer {
    private StaffTargetRenderer() {
    }

    @SubscribeEvent
    static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.screen != null || mc.options.hideGui
                || mc.player.isSpectator() || mc.getCameraEntity() != mc.player) {
            return;
        }
        ItemStack staff = mc.player.getMainHandItem();
        if (!(staff.getItem() instanceof StaffItem)) {
            staff = mc.player.getOffhandItem();
        }
        if (!(staff.getItem() instanceof StaffItem)) {
            return;
        }

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        StaffTarget lock = staff.get(ModDataComponentTypes.STAFF_TARGET.get());
        boolean aiming = mc.player.isShiftKeyDown();
        LivingEntity target = aiming
                ? StaffTargeting.findAimTarget(mc.player, partialTick)
                : findLockedTarget(mc, lock);
        // The aim ray already checks the visible part of the body; eye-to-eye visibility
        // would incorrectly hide previews when only the creature's legs are visible.
        if (target == null || (!aiming && !mc.player.hasLineOfSight(target))) {
            return;
        }
        boolean locked = lock != null && lock.uuid().equals(target.getUUID())
                && lock.dimension().equals(mc.level.dimension());

        Vec3 camera = event.getCamera().getPosition();
        Vec3 center = target.getPosition(partialTick).add(0, target.getBbHeight() * 0.5, 0);
        // Place the reticle just in front of the body, keeping normal block depth testing.
        Vec3 towardCamera = camera.subtract(center).normalize();
        double offset = Math.hypot(target.getBbWidth(), target.getBbHeight()) * 0.5 + 0.05;
        Vec3 position = center.add(towardCamera.scale(Math.min(offset, camera.distanceTo(center) * 0.5)));
        float size = Math.max(0.25F, Math.min(0.65F, target.getBbWidth() * 0.6F));

        PoseStack poses = event.getPoseStack();
        poses.pushPose();
        poses.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z);
        poses.mulPose(event.getCamera().rotation());
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        RenderType renderType = RenderType.debugQuads();
        VertexConsumer vertices = buffers.getBuffer(renderType);
        drawReticle(vertices, poses.last(), size, locked);
        buffers.endBatch(renderType);
        poses.popPose();
    }

    @Nullable
    private static LivingEntity findLockedTarget(Minecraft mc, @Nullable StaffTarget lock) {
        if (lock == null || !lock.dimension().equals(mc.level.dimension())) {
            return null;
        }
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity.getUUID().equals(lock.uuid()) && entity instanceof LivingEntity living
                    && StaffTargeting.canTarget(living, mc.player)) {
                return living;
            }
        }
        return null;
    }

    private static void drawReticle(VertexConsumer vertices, PoseStack.Pose pose, float radius, boolean locked) {
        float thickness = radius * 0.075F;
        float arm = radius * 0.45F;
        int alpha = locked ? 255 : 180;
        for (int xSign : new int[]{-1, 1}) {
            for (int ySign : new int[]{-1, 1}) {
                float x = xSign * radius;
                float y = ySign * radius;
                rectangle(vertices, pose, x, y, x - xSign * arm, y - ySign * thickness, alpha);
                rectangle(vertices, pose, x, y, x - xSign * thickness, y - ySign * arm, alpha);
            }
        }
        if (locked) {
            // The central cross distinguishes a confirmed lock from an aim preview.
            rectangle(vertices, pose, -arm * 0.5F, -thickness * 0.5F, arm * 0.5F, thickness * 0.5F, alpha);
            rectangle(vertices, pose, -thickness * 0.5F, -arm * 0.5F, thickness * 0.5F, arm * 0.5F, alpha);
        }
    }

    private static void rectangle(VertexConsumer vertices, PoseStack.Pose pose, float x1, float y1, float x2, float y2, int alpha) {
        float left = Math.min(x1, x2);
        float right = Math.max(x1, x2);
        float bottom = Math.min(y1, y2);
        float top = Math.max(y1, y2);
        vertices.addVertex(pose, left, bottom, 0).setColor(255, 32, 32, alpha);
        vertices.addVertex(pose, right, bottom, 0).setColor(255, 32, 32, alpha);
        vertices.addVertex(pose, right, top, 0).setColor(255, 32, 32, alpha);
        vertices.addVertex(pose, left, top, 0).setColor(255, 32, 32, alpha);
    }
}
