package com.flora.popupbook.block.forgery.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ForgeryBakedModel implements IDynamicBakedModel {

    private final BakedModel defaultModel;

    public ForgeryBakedModel(BakedModel defaultModel) {
        this.defaultModel = defaultModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random, ModelData extraData, @Nullable RenderType renderType) {
        BlockState targetState = targetState(extraData);
        return targetModel(extraData).getQuads(targetState != null ? targetState : state, side, random, extraData, renderType);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        return targetModel(data).getParticleIcon(data);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) {
        BlockState targetState = targetState(data);
        return targetModel(data).getRenderTypes(targetState != null ? targetState : state, random, data);
    }

    @Override
    public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
        BlockState targetState = targetState(data);
        return targetModel(data).useAmbientOcclusion(targetState != null ? targetState : state, data, renderType);
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        return modelData;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return defaultModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return defaultModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return defaultModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return defaultModel.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return defaultModel.getOverrides();
    }

    @Override
    public ItemTransforms getTransforms() {
        return defaultModel.getTransforms();
    }

    @Nullable
    private BlockState targetState(ModelData data) {
        return data.get(ForgeryModelData.TARGET_STATE);
    }

    private BakedModel targetModel(ModelData data) {
        BlockState targetState = targetState(data);
        if (targetState == null) {
            return defaultModel;
        }
        BakedModel targetModel = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(targetState);
        return targetModel instanceof ForgeryBakedModel ? defaultModel : targetModel;
    }
}