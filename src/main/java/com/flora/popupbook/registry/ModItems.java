package com.flora.popupbook.registry;

import com.flora.popupbook.PopupBook;
import com.flora.popupbook.item.StarryBucketItem;
import com.flora.popupbook.item.skyeye.SkyEyeItem;
import com.flora.popupbook.item.staff.StaffItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, PopupBook.MODID);

    public static final Supplier<StaffItem> STAFF = ITEMS.register("staff",
            () -> new StaffItem(new Item.Properties().stacksTo(1)));
    public static final Supplier<SkyEyeItem> SKY_EYE = ITEMS.register("sky_eye",
            () -> new SkyEyeItem(new Item.Properties().stacksTo(1)));
    public static final Supplier<StarryBucketItem> STARRY_BUCKET = ITEMS.register("starry_bucket",
            () -> new StarryBucketItem(ModFluids.STARRY_FLUID_SOURCE.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
}
