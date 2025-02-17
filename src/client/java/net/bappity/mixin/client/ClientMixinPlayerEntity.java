package net.bappity.mixin.client;

import net.bappity.BappityPlayerDataAccessor;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientMixinPlayerEntity implements BappityPlayerDataAccessor {
    private boolean bappityIrregular;

    @Override
    public boolean isBappityIrregular() {
        return this.bappityIrregular;
    }

    @Override
    public void setBappityIrregular(boolean irregular) {
        this.bappityIrregular = irregular;
    }
}