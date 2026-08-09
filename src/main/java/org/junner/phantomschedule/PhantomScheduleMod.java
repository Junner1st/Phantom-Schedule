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
		PhantomScheduleConfig activeConfig = config;
		if (activeConfig == null) {
			return true;
		}

		activeConfig.reloadIfChanged();
		return activeConfig.currentSchedule().allowsNow();
	}
}
