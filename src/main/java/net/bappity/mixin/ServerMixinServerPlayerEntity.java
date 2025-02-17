package net.bappity.mixin;

import net.bappity.BappityPlayerDataAccessor;
import net.bappity.ModStatusEffects;
import net.bappity.SleepManager;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.util.Either;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerMixinServerPlayerEntity implements BappityPlayerDataAccessor {
    private boolean bappityIrregular;

    @Inject(
        method = "trySleep",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;trySleep(Lnet/minecraft/util/math/BlockPos;)Lcom/mojang/datafixers/util/Either;"
        ),
        cancellable = true
    )
    private void onTrySleep(BlockPos pos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // Check for insomnia status effect
        if (player.hasStatusEffect(ModStatusEffects.INSOMNIA_ENTRY)) {
            // Allow entering bed but prevent sleep completion
            player.setSpawnPoint(player.getWorld().getRegistryKey(), pos, player.getYaw(), false, true);

            // Reapply insomnia effect
            player.addStatusEffect(new StatusEffectInstance(
                ModStatusEffects.INSOMNIA_ENTRY, // Use RegistryEntry<StatusEffect>
                1200, // Duration in ticks (60 seconds)
                0, // Amplifier
                false, // No particles
                true, // Show icon
                true // Show ambient effect
            ));

            // Prevent sleep completion
            cir.setReturnValue(Either.left(PlayerEntity.SleepFailureReason.OTHER_PROBLEM));
        }
    }

    @Inject(
        method = "trySleep",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;updateSleepingPlayers()V",
            shift = At.Shift.AFTER
        )
    )
    private void onTrySleepSuccess(BlockPos pos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // Only trigger if sleep succeeded
        if (cir.getReturnValue() != null && cir.getReturnValue().right().isPresent()) {
            SleepManager.handleSuccessfulSleep(player, pos);
        }
    }

        @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound tag, CallbackInfo ci) {
        if (tag.contains("BappityIrregular")) {
            this.bappityIrregular = tag.getBoolean("BappityIrregular");
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeCustomDataToNbt(NbtCompound tag, CallbackInfo ci) {
        tag.putBoolean("BappityIrregular", this.bappityIrregular);
    }

    @Override
    public boolean isBappityIrregular() {
        return bappityIrregular;
    }

    @Override
    public void setBappityIrregular(boolean irregular) {
        bappityIrregular = irregular;
    }
}