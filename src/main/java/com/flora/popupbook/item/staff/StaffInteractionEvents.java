package com.flora.popupbook.item.staff;

import com.flora.popupbook.PopupBook;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** Vanilla mob interactions precede Item.use, so route close-range clicks to the staff as well. */
@EventBusSubscriber(modid = PopupBook.MODID)
public final class StaffInteractionEvents {
    private StaffInteractionEvents() {
    }

    @SubscribeEvent
    static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getItemStack().getItem() instanceof StaffItem staff
                && !event.getEntity().getCooldowns().isOnCooldown(staff)) {
            event.setCancellationResult(staff.use(event.getLevel(), event.getEntity(), event.getHand()).getResult());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getItemStack().getItem() instanceof StaffItem staff
                && !event.getEntity().getCooldowns().isOnCooldown(staff)) {
            event.setCancellationResult(staff.use(event.getLevel(), event.getEntity(), event.getHand()).getResult());
            event.setCanceled(true);
        }
    }
}
