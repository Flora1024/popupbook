package com.flora.popupbook.item.skyeye;

import com.flora.popupbook.registry.ModDataComponentTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.*;

public class SkyEyeItem extends Item {

    /** 附身最远距离（方块），保证目标在玩家客户端的渲染距离内 */
    private static final double POSSESS_RANGE = 64.0;

    public SkyEyeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            player.getItemInHand(hand).set(ModDataComponentTypes.POSSESSED, entity.getUUID());
            player.displayClientMessage(Component.translatable("message.popupbook.sky_eye.bound", entity.getDisplayName()), true);
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide())
            return InteractionResultHolder.sidedSuccess(stack, true);

        ServerPlayer serverPlayer = (ServerPlayer) player;
        UUID entityId = stack.get(ModDataComponentTypes.POSSESSED);

        if (checkPossessionPermission(stack, serverPlayer, entityId))
            return InteractionResultHolder.sidedSuccess(stack, false);

        stack.set(ModDataComponentTypes.ISPOSSESSING, Boolean.TRUE);

        Entity entity = serverPlayer.serverLevel().getEntity(entityId);
        serverPlayer.connection.send(new ClientboundSetCameraPacket(entity));
        serverPlayer.displayClientMessage(Component.translatable("message.popupbook.sky_eye.possess", entity.getDisplayName()), true);
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    /** 目标死亡/消失或玩家死亡、下线时自动结束附身 */
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        // 状态只维护在服务端
        if (level.isClientSide()) return;
        if (!(entity instanceof ServerPlayer player)) return;
        if (!Boolean.TRUE.equals(stack.get(ModDataComponentTypes.ISPOSSESSING))) return;

        UUID targetId = stack.get(ModDataComponentTypes.POSSESSED);
        Entity target = targetId == null ? null : player.serverLevel().getEntity(targetId);
        boolean targetOk = target instanceof LivingEntity living && living.isAlive()
                && living.level().dimension() == player.level().dimension();
        if (player.isDeadOrDying() || !targetOk) {
            exitPossession(player, stack);
        }
    }

    /** 校验附身权限 **/
    private static boolean checkPossessionPermission(ItemStack stack, ServerPlayer serverPlayer, UUID entityId) {
        Boolean isPossessing = stack.get(ModDataComponentTypes.ISPOSSESSING);
        if (Boolean.TRUE.equals(isPossessing)) {
            exitPossession(serverPlayer, stack);
            serverPlayer.displayClientMessage(Component.translatable("message.popupbook.sky_eye.unpossess"), true);
            return true;
        }
        if (Objects.isNull(entityId)){
            serverPlayer.displayClientMessage(Component.translatable("message.popupbook.sky_eye.not_bound"), true);
            return true;
        }
        Optional<Entity> entity = Optional.ofNullable(serverPlayer.serverLevel().getEntity(entityId));
        if (entity.isEmpty() || !(entity.get() instanceof LivingEntity living) || !living.isAlive()) {
            serverPlayer.displayClientMessage(Component.translatable("message.popupbook.sky_eye.target_gone"), true);
            return true;
        }
        if (living.level().dimension() != serverPlayer.level().dimension()) {
            serverPlayer.displayClientMessage(Component.translatable("message.popupbook.sky_eye.wrong_dimension"), true);
            return true;
        }
        if (serverPlayer.distanceToSqr(living) > POSSESS_RANGE * POSSESS_RANGE) {
            serverPlayer.displayClientMessage(Component.translatable("message.popupbook.sky_eye.too_far"), true);
            return true;
        }
        return false;
    }

    /** 退出附身状态 **/
    private static void exitPossession(ServerPlayer player, ItemStack stack) {
        player.connection.send(new ClientboundSetCameraPacket(player));
        stack.set(ModDataComponentTypes.ISPOSSESSING, Boolean.FALSE);
    }
}
