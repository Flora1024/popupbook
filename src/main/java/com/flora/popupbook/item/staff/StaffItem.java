package com.flora.popupbook.item.staff;

import com.flora.popupbook.entity.AbstractMagicSphereEntity;
import com.flora.popupbook.registry.ModEntities;
import com.flora.popupbook.registry.ModDataComponentTypes;
import com.flora.popupbook.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StaffItem extends Item {

    public StaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        if (player.isShiftKeyDown()) {
            LivingEntity aimedTarget = StaffTargeting.findAimTarget(player, 1.0F);
            if (aimedTarget == null) {
                player.displayClientMessage(Component.translatable("message.popupbook.staff.no_aim_target"), true);
            } else {
                stack.set(ModDataComponentTypes.STAFF_TARGET.get(), StaffTarget.of(aimedTarget));
                player.displayClientMessage(Component.translatable("message.popupbook.staff.locked", aimedTarget.getDisplayName()), true);
            }
            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        StaffTarget lock = stack.get(ModDataComponentTypes.STAFF_TARGET.get());
        if (lock == null) {
            player.displayClientMessage(Component.translatable("message.popupbook.staff.not_locked"), true);
            return InteractionResultHolder.sidedSuccess(stack, false);
        }
        if (!lock.dimension().equals(level.dimension())) {
            player.displayClientMessage(Component.translatable("message.popupbook.staff.wrong_dimension"), true);
            return InteractionResultHolder.sidedSuccess(stack, false);
        }
        Entity entity = serverLevel.getEntity(lock.uuid());
        if (!(entity instanceof LivingEntity target) || !StaffTargeting.canTarget(target, player)) {
            // An unloaded entity may return later; do not erase its persistent lock.
            if (entity != null) {
                stack.remove(ModDataComponentTypes.STAFF_TARGET.get());
            }
            player.displayClientMessage(Component.translatable("message.popupbook.staff.target_unavailable"), true);
            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        AbstractMagicSphereEntity sphere = new AbstractMagicSphereEntity(ModEntities.MAGIC_SPHERE.get(), level);
        sphere.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
        sphere.setDeltaMovement(player.getLookAngle().scale(0.5));
        sphere.setOwner(player);
        sphere.setTarget(target);
        level.addFreshEntity(sphere);
        serverLevel.sendParticles(ParticleTypes.ASH, player.getX(), player.getY(), player.getZ(), 1, 0, 0, 0, 0);
        level.playSound(null, player.blockPosition(), ModSounds.ORB_LAUNCH.get(), SoundSource.PLAYERS, 0.1F, 1.0F);
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    @Override
    public @NotNull InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        // Sneaking over a nearby block must still confirm the aim, rather than activate the block.
        if (player != null && player.isShiftKeyDown()) {
            return use(context.getLevel(), player, context.getHand()).getResult();
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        StaffTarget lock = stack.get(ModDataComponentTypes.STAFF_TARGET.get());
        tooltip.add(lock == null
                ? Component.translatable("tooltip.popupbook.staff.no_target")
                : Component.translatable("tooltip.popupbook.staff.target", lock.name()));
        tooltip.add(Component.translatable("tooltip.popupbook.staff.aim", (int) StaffTargeting.AIM_RANGE));
        tooltip.add(Component.translatable("tooltip.popupbook.staff.cast"));
    }
}
