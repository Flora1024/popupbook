package com.flora.popupbook.block.musicbox.client;

import com.flora.popupbook.block.musicbox.MusicBoxBlockEntity;
import com.flora.popupbook.registry.ModBlockEntityTypes;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 负责在世界中绘制 music_box 的 GeckoLib 渲染器。
 * <p>
 * 传入 BlockEntityType 后，模型/动画/贴图会自动按
 * popupbook:music_box 查找 geo/block 与 animations/block 下的同名资产。
 */
public class MusicBoxBlockEntityRenderer extends GeoBlockRenderer<MusicBoxBlockEntity> {

    public MusicBoxBlockEntityRenderer() {
        super(ModBlockEntityTypes.MUSIC_BOX.get());
    }
}
