package org.junner.phantomschedule.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import org.junner.phantomschedule.PhantomScheduleMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PhantomSpawner.class)
public abstract class PhantomSpawnerMixin {
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void phantomSchedule$denyOutsideRealWorldWindow(ServerLevel level, boolean spawnEnemies, CallbackInfo ci) {
		if (PhantomScheduleMod.shouldDenyPhantomSpawning()) {
			ci.cancel();
			return;
		}

		if (PhantomScheduleMod.shouldAllowPhantomSpawning()
				&& !PhantomScheduleMod.shouldIgnoreSpawnMonstersGamerule()
				&& !level.getGameRules().get(GameRules.SPAWN_MONSTERS)) {
			ci.cancel();
		}
	}

	@ModifyVariable(method = "tick", at = @At("HEAD"), argsOnly = true, index = 2)
	private boolean phantomSchedule$forceSpawnEnemiesWhenConfigured(boolean spawnEnemies) {
		if (PhantomScheduleMod.shouldAllowPhantomSpawning() && PhantomScheduleMod.shouldIgnoreSpawnMonstersGamerule()) {
			return true;
		}

		return spawnEnemies;
	}

	@Redirect(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"
			)
	)
	private Object phantomSchedule$bypassPhantomGamerule(GameRules rules, GameRule<Boolean> rule) {
		if (rule == GameRules.SPAWN_PHANTOMS
				&& PhantomScheduleMod.shouldAllowPhantomSpawning()
				&& PhantomScheduleMod.shouldIgnoreSpawnPhantomsGamerule()) {
			return Boolean.TRUE;
		}

		return rules.get(rule);
	}
}
