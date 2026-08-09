package org.junner.phantomschedule.mixin;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import org.junner.phantomschedule.PhantomScheduleMod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin {
	@Shadow
	@Final
	private ServerLevel level;

	@Inject(method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V", at = @At("TAIL"))
	private void phantomSchedule$tickPhantomsWhenMobSpawningIsBypassed(ProfilerFiller profiler, long timeDiff, CallbackInfo ci) {
		if (!PhantomScheduleMod.shouldAllowPhantomSpawning() || !PhantomScheduleMod.shouldIgnoreSpawnMobsGamerule()) {
			return;
		}

		if (level.getGameRules().get(GameRules.SPAWN_MOBS)) {
			return;
		}

		for (CustomSpawner spawner : ((ServerLevelAccessor) level).phantomSchedule$getCustomSpawners()) {
			if (spawner instanceof PhantomSpawner) {
				spawner.tick(level, true);
			}
		}
	}
}
