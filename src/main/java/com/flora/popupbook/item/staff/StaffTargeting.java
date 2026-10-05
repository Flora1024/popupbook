package com.flora.popupbook.item.staff;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/** Shared ray selection keeps the client preview and server lock rules identical. */
public final class StaffTargeting {
    public static final double AIM_RANGE = 32.0;
    private static final double AIM_MARGIN = 0.15;

    private StaffTargeting() {
    }

    public static boolean canTarget(LivingEntity target, Player player) {
        return target != player && target.isAlive() && !target.isSpectator()
                && !(target instanceof ArmorStand);
    }

    @Nullable
    public static LivingEntity findAimTarget(Player player, float partialTick) {
        Vec3 start = player.getEyePosition(partialTick);
        Vec3 end = start.add(player.getViewVector(partialTick).scale(AIM_RANGE));
        HitResult blockHit = player.level().clip(new ClipContext(
                start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (blockHit.getType() != HitResult.Type.MISS) {
            end = blockHit.getLocation();
        }

        double closestDistance = start.distanceToSqr(end);
        LivingEntity closest = null;
        AABB searchBox = new AABB(start, end).inflate(1.0);
        for (LivingEntity candidate : player.level().getEntitiesOfClass(
                LivingEntity.class, searchBox, target -> canTarget(target, player))) {
            AABB hitBox = candidate.getBoundingBox().inflate(AIM_MARGIN + candidate.getPickRadius());
            Vec3 hit = hitBox.contains(start) ? start : hitBox.clip(start, end).orElse(null);
            if (hit != null && start.distanceToSqr(hit) < closestDistance) {
                closestDistance = start.distanceToSqr(hit);
                closest = candidate;
            }
        }
        return closest;
    }
}
