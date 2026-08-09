package org.junner.phantomschedule;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PhantomScheduleMod implements ModInitializer {
	public static final String MOD_ID = "phantom-schedule";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static PhantomScheduleConfig config;

	@Override
	public void onInitialize() {
		config = new PhantomScheduleConfig(FabricLoader.getInstance().getConfigDir().resolve("phantom-schedule.properties"));
		config.reloadIfChanged();
	}

	public static boolean canPhantomSpawnNow() {
		Schedule schedule = currentSchedule();
		return schedule == null || !schedule.enableMod() || schedule.allowsNow();
	}

	public static boolean shouldAllowPhantomSpawning() {
		Schedule schedule = currentSchedule();
		return schedule != null && schedule.enableMod() && schedule.allowsNow();
	}

	public static boolean shouldDenyPhantomSpawning() {
		Schedule schedule = currentSchedule();
		return schedule != null && schedule.enableMod() && !schedule.allowsNow();
	}

	public static boolean shouldIgnoreSpawnMobsGamerule() {
		Schedule schedule = currentSchedule();
		return schedule != null && schedule.enableMod() && schedule.ignoresSpawnMobsGamerule();
	}

	public static boolean shouldIgnoreSpawnMonstersGamerule() {
		Schedule schedule = currentSchedule();
		return schedule != null && schedule.enableMod() && schedule.ignoresSpawnMonstersGamerule();
	}

	public static boolean shouldIgnoreSpawnPhantomsGamerule() {
		Schedule schedule = currentSchedule();
		return schedule != null && schedule.enableMod() && schedule.ignoresSpawnPhantomsGamerule();
	}

	private static Schedule currentSchedule() {
		PhantomScheduleConfig activeConfig = config;
		if (activeConfig == null) {
			return null;
		}

		activeConfig.reloadIfChanged();
		return activeConfig.currentSchedule();
	}
}
