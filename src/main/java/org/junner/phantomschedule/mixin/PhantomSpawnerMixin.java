package org.junner.phantomschedule.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import org.junner.phantomschedule.PhantomScheduleMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PhantomSpawner.class)
public abstract class PhantomSpawnerMixin {
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void phantomSchedule$denyOutsideRealWorldWindow(ServerLevel level, boolean spawnEnemies, CallbackInfo ci) {
		if (spawnEnemies && !PhantomScheduleMod.canPhantomSpawnNow()) {
			ci.cancel();
		}
	}
}
