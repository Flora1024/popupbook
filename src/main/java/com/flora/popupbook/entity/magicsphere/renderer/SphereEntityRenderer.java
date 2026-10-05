package com.flora.popupbook.entity.magicsphere.renderer;

import com.flora.popupbook.PopupBook;
import com.flora.popupbook.entity.magicsphere.AbstractMagicSphereEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SphereEntityRenderer extends EntityRenderer<AbstractMagicSphereEntity> {

    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(PopupBook.MODID, "textures/entity/light_sphere.png");
    private static final RenderType RENDER_TYPE;

    public SphereEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(@NotNull AbstractMagicSphereEntity abstractMagicSphereEntity) {
        return TEXTURE_LOCATION;
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, float x, int y, int u, int v) {
        consumer.addVertex(pose, x - 0.5F, (float)y - 0.25F, 0.0F).setColor(-1).setUv((float)u, (float)v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public void render(AbstractMagicSphereEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

        // ========== 新增：绕 Z 轴旋转 ==========
        // entity.tickCount + partialTick 得到平滑连续时间
        // * 4.0F 控制旋转速度（每 tick 转 4 度，20 tick = 80 度/秒，约 4.5 秒一圈）
//        float angle = (entity.tickCount + partialTick) * 8.0F;
//        poseStack.mulPose(Axis.ZP.rotationDegrees(angle));

        PoseStack.Pose posestack$pose = poseStack.last();

        VertexConsumer vertexconsumer = buffer.getBuffer(RENDER_TYPE);
        vertex(vertexconsumer, posestack$pose, packedLight, 0.0F, 0, 0, 1);
        vertex(vertexconsumer, posestack$pose, packedLight, 1.0F, 0, 1, 1);
        vertex(vertexconsumer, posestack$pose, packedLight, 1.0F, 1, 1, 0);
        vertex(vertexconsumer, posestack$pose, packedLight, 0.0F, 1, 0, 0);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    static {
        RENDER_TYPE = RenderType.eyes(TEXTURE_LOCATION);
    }
}
