package com.flora.popupbook.block.forgery;

import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ForgeryBlock extends Block implements EntityBlock {

    public ForgeryBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ForgeryBlockEntity(pos, state);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockPos targetPos = pos.below();
        Block targetBlock = level.getBlockState(targetPos).getBlock();
        ForgeryBlockEntity entity = (ForgeryBlockEntity) level.getBlockEntity(pos);
        if (entity != null) {
            entity.setAuthenticity(targetBlock);
        }

        Block storedBlock = entity.getAuthenticity();
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(storedBlock);
        String location = key.toString();

        if (level.isClientSide)
            ParticleUtils.spawnParticles(level, pos, 200, 0.6f, 1.0f, true, ParticleTypes.LARGE_SMOKE);
        return InteractionResult.SUCCESS;
    }
}
