package com.flora.popupbook.entity.magicsphere;

import com.flora.popupbook.registry.ModParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class AbstractMagicSphereEntity extends Projectile {

    public double xPower = 0;
    public double yPower = 0;
    public double zPower = 0;
    @Nullable
    private LivingEntity target;
    @Nullable
    private UUID targetId;

    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(AbstractMagicSphereEntity.class, EntityDataSerializers.FLOAT);

    private int timer;
    private int lifetick;

    public AbstractMagicSphereEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
        this.timer = 0;
        this.lifetick = 200;
        this.xPower = this.random.nextIntBetweenInclusive(-1,1);
        this.yPower = this.random.nextIntBetweenInclusive(0,1);
        this.zPower = this.random.nextIntBetweenInclusive(-1,1);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DAMAGE, 0.1F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
    }

    @Override
    public void tick() {
        super.tick();

        ++timer;
        if (timer > lifetick) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, Level.ExplosionInteraction.NONE);
            this.discard();
        }

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hitResult)) {
            this.hitTargetOrDeflectSelf(hitResult);
        }
        this.checkInsideBlocks();

        // todo 跟踪
        Vec3 currentVec3 = this.getDeltaMovement();
        double speed = currentVec3.length();
        if (Objects.nonNull(target) && target.isAlive()) {
            Vec3 toTargetVec3 = new Vec3(
                    target.getX() - this.getX(),
                    target.getY() + this.target.getBbHeight() * 0.5 - this.getY(),
                    target.getZ() - this.getZ()
            );
            double dist = toTargetVec3.length();
            double steerStrength = 0.4;
            if (dist <= 1) {
                this.onHitEntity(new EntityHitResult(target));
                return;
            }   else if (dist > 1) {
                toTargetVec3 = toTargetVec3.normalize();
            }

            Vec3 currentDir = currentVec3.normalize();
            Vec3 newDir = currentDir.add(
                    toTargetVec3.x + this.xPower * steerStrength,
                    toTargetVec3.y + this.yPower * steerStrength,
                    toTargetVec3.z + this.zPower * steerStrength
            ).normalize();
            currentVec3 = newDir.scale(speed);
        }

        double d0 = this.getX() + currentVec3.x;
        double d1 = this.getY() + currentVec3.y;
        double d2 = this.getZ() + currentVec3.z;
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);

        this.setDeltaMovement(currentVec3.scale(getInertia()));
        this.setPos(d0, d1, d2);

        if (this.level().isClientSide()) {
            this.level().addParticle(ModParticles.MAGIC_SPHERE_TRAIL_PARTICLES.get(),
                    this.getX() - currentVec3.x, this.getY() - currentVec3.y + 0.15D, this.getZ() - currentVec3.z,
                    0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            onHitEntity((EntityHitResult) hitResult);
        }   else {
            onHitBlock((BlockHitResult) hitResult);
        }
    }
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.hurt(damageSources().magic(), this.getDamage());
            livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1200, 0));
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, Level.ExplosionInteraction.NONE);
            this.discard();
        }
    }

    protected void onHitBlock(BlockHitResult result) {

    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    protected float getInertia() {
        return 1.005F;
    }

    protected float getDamage() {
        return this.entityData.get(DAMAGE);
    }
}
