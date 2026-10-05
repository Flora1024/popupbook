package com.flora.popupbook.registry;

import com.flora.popupbook.PopupBook;
import com.flora.popupbook.entity.AbstractMagicSphereEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, PopupBook.MODID);

    public static final Supplier<EntityType<AbstractMagicSphereEntity>> MAGIC_SPHERE =
            ENTITIES.register("magic_sphere", () ->
                    EntityType.Builder.of(AbstractMagicSphereEntity::new, MobCategory.MISC)
                            .sized(1.0f, 1.0f)
                            .build(PopupBook.MODID + ":magic_sphere")
            );
}
