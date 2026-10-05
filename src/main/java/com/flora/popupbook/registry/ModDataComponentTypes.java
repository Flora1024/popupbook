package com.flora.popupbook.registry;

import com.flora.popupbook.PopupBook;
import com.flora.popupbook.item.staff.StaffTarget;
import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;
import java.util.function.Supplier;

public class ModDataComponentTypes {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, PopupBook.MODID);

    /**
     * 法杖目标
     */
    public static final Supplier<DataComponentType<StaffTarget>> STAFF_TARGET =
            DATA_COMPONENT_TYPES.register("staff_target",
                    () -> DataComponentType.<StaffTarget>builder()
                            .persistent(StaffTarget.CODEC)
                            .networkSynchronized(StaffTarget.STREAM_CODEC)
                            .build());

    /** 真品方块 **/
    public static final Supplier<DataComponentType<Block>> AUTHENTICITY =
            DATA_COMPONENT_TYPES.register("authenticity",
                    () -> DataComponentType.<Block>builder()
                            .persistent(BuiltInRegistries.BLOCK.byNameCodec())
                            .networkSynchronized(ByteBufCodecs.idMapper(BuiltInRegistries.BLOCK))
                            .build());

    /** 所属视角生物UUID & 是否处于附身状态 **/
    public static final Supplier<DataComponentType<UUID>> POSSESSED =
            DATA_COMPONENT_TYPES.register("possessed",
                    () -> DataComponentType.<UUID>builder()
                            .persistent(UUIDUtil.CODEC)
                            .networkSynchronized(UUIDUtil.STREAM_CODEC)
                            .build());
    public static final Supplier<DataComponentType<Boolean>> ISPOSSESSING =
            DATA_COMPONENT_TYPES.register("ispossessing",
                    () -> DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL)
                            .build());
}
