package com.flora.popupbook.item.staff;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.UUID;

/** Immutable, per-stack lock data; entity references are resolved only when needed. */
public record StaffTarget(UUID uuid, ResourceKey<Level> dimension, String name) {
    public static final Codec<StaffTarget> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(StaffTarget::uuid),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(StaffTarget::dimension),
            Codec.STRING.fieldOf("name").forGetter(StaffTarget::name)
    ).apply(instance, StaffTarget::new));

    public static final StreamCodec<ByteBuf, StaffTarget> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, StaffTarget::uuid,
            ResourceKey.streamCodec(Registries.DIMENSION), StaffTarget::dimension,
            ByteBufCodecs.STRING_UTF8, StaffTarget::name,
            StaffTarget::new);

    public static StaffTarget of(LivingEntity entity) {
        return new StaffTarget(entity.getUUID(), entity.level().dimension(), entity.getDisplayName().getString());
    }
}
