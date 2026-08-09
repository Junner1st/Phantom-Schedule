package org.junner.phantomschedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

public record Schedule(boolean enabled, LocalTime start, LocalTime end, ZoneId zone, Set<MonthDay> annualHolidays, Set<LocalDate> datedHolidays) {
	public boolean allowsNow() {
		if (!enabled) {
			return true;
		}

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
}
