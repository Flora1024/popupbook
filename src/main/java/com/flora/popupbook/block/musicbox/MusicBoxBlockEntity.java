package com.flora.popupbook.block.musicbox;

import com.flora.popupbook.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * GeckoLib 动画方块实体。
 * <p>
 * 动画资产按 BlockEntity 的注册名（popupbook:music_box）自动定位：
 * <ul>
 *     <li>模型：assets/popupbook/geo/block/music_box.geo.json</li>
 *     <li>动画：assets/popupbook/animations/block/music_box.animation.json</li>
 *     <li>贴图：assets/popupbook/textures/block/music_box.png</li>
 * </ul>
 * 用 BlockBench 的 GeckoLib 插件导出后放到上述路径即可。
 */
public class MusicBoxBlockEntity extends BlockEntity implements GeoBlockEntity {

    /** 未播放（盒盖关闭 / 静止）动画，对应 animation.json 中的 "idle" */
    public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    /** 播放中动画，对应 animation.json 中的 "playing" */
    public static final RawAnimation PLAYING_ANIM = RawAnimation.begin().thenLoop("openAndSpin");

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    public MusicBoxBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.MUSIC_BOX.get(), pos, blockState);
    }

    /** 是否处于播放状态，直接读取方块状态上的 PLAYING 属性 */
    public boolean isPlaying() {
        return getBlockState().getValue(MusicBoxBlock.PLAYING);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "music_box", 5, state ->
                state.setAndContinue(isPlaying() ? PLAYING_ANIM : IDLE_ANIM)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animatableInstanceCache;
    }
}
