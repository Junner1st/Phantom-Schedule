package org.junner.phantomschedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

public record Schedule(
		boolean enableMod,
		boolean ignoreGamerule,
		boolean ignoreGameruleSpawnMobs,
		boolean ignoreGameruleSpawnMonsters,
		boolean ignoreGameruleSpawnPhantoms,
		LocalTime start,
		LocalTime end,
		ZoneId zone,
		Set<MonthDay> annualHolidays,
		Set<LocalDate> datedHolidays
) {
	public boolean allowsNow() {
		ZonedDateTime now = ZonedDateTime.now(effectiveZone());
		if (isHoliday(now.toLocalDate())) {
			return true;
		}

		return allowsTime(now.toLocalTime());
	}

	private boolean isHoliday(LocalDate date) {
		return datedHolidays.contains(date) || annualHolidays.contains(MonthDay.from(date));
	}

	private boolean allowsTime(LocalTime now) {
		if (start.equals(end)) {
			return true;
		}

		if (start.isBefore(end)) {
			return !now.isBefore(start) && now.isBefore(end);
		}

		return !now.isBefore(start) || now.isBefore(end);
	}

	private ZoneId effectiveZone() {
		return zone == null ? ZoneId.systemDefault() : zone;
	}

	public boolean ignoresSpawnMobsGamerule() {
		return ignoreGamerule && ignoreGameruleSpawnMobs;
	}

	public boolean ignoresSpawnMonstersGamerule() {
		return ignoreGamerule && ignoreGameruleSpawnMonsters;
	}

	public boolean ignoresSpawnPhantomsGamerule() {
		return ignoreGamerule && ignoreGameruleSpawnPhantoms;
	}
}
