package org.junner.phantomschedule;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public final class PhantomScheduleConfig {
	private static final boolean DEFAULT_ENABLED = true;
	private static final LocalTime DEFAULT_START = LocalTime.of(19, 0);
	private static final LocalTime DEFAULT_END = LocalTime.of(6, 0);
	private static final String DEFAULT_ZONE = "system";
	private static final String DEFAULT_HOLIDAYS = "";

	private final Path path;
	private volatile Schedule schedule = new Schedule(DEFAULT_ENABLED, DEFAULT_START, DEFAULT_END, null, Set.of(), Set.of());
	private FileTime lastModified;

	public PhantomScheduleConfig(Path path) {
		this.path = path;
	}

	public Schedule currentSchedule() {
		return schedule;
	}

	public synchronized void reloadIfChanged() {
		try {
			ensureConfigExists();
			FileTime modified = Files.getLastModifiedTime(path);
			if (modified.equals(lastModified)) {
				return;
			}

			Properties properties = new Properties();
			try (Reader reader = Files.newBufferedReader(path)) {
				properties.load(reader);
			}

			schedule = parse(properties);
			lastModified = modified;
			PhantomScheduleMod.LOGGER.info("Loaded phantom schedule config from {}", path);
		} catch (IOException | IllegalArgumentException exception) {
			PhantomScheduleMod.LOGGER.warn("Could not load phantom schedule config {}; keeping previous values", path, exception);
		}
	}

	private void ensureConfigExists() throws IOException {
		if (Files.exists(path)) {
			return;
		}

		Path parent = path.getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}

		Properties defaults = new Properties();
		defaults.setProperty("enabled", Boolean.toString(DEFAULT_ENABLED));
		defaults.setProperty("start", DEFAULT_START.toString());
		defaults.setProperty("end", DEFAULT_END.toString());
		defaults.setProperty("zone", DEFAULT_ZONE);
		defaults.setProperty("holidays", DEFAULT_HOLIDAYS);

		try (Writer writer = Files.newBufferedWriter(path)) {
			defaults.store(writer, "Phantom Schedule config. Times are real-world local times. Holidays allow phantoms all day.");
		}
	}

	private Schedule parse(Properties properties) {
		boolean enabled = Boolean.parseBoolean(properties.getProperty("enabled", Boolean.toString(DEFAULT_ENABLED)).trim());
		LocalTime start = parseTime(properties.getProperty("start", DEFAULT_START.toString()), "start");
		LocalTime end = parseTime(properties.getProperty("end", DEFAULT_END.toString()), "end");
		ZoneId zone = parseZone(properties.getProperty("zone", DEFAULT_ZONE));
		HolidayConfig holidays = parseHolidays(properties.getProperty("holidays", DEFAULT_HOLIDAYS));

		return new Schedule(enabled, start, end, zone, holidays.annual(), holidays.dated());
	}

	private LocalTime parseTime(String value, String name) {
		try {
			return LocalTime.parse(value.trim());
		} catch (DateTimeException exception) {
			throw new IllegalArgumentException(name + " must use HH:mm format", exception);
		}
	}

	private ZoneId parseZone(String value) {
		String trimmed = value.trim();
		if (trimmed.isEmpty() || DEFAULT_ZONE.equalsIgnoreCase(trimmed)) {
			return null;
		}

		try {
			return ZoneId.of(trimmed);
		} catch (DateTimeException exception) {
			throw new IllegalArgumentException("zone must be system or a valid ZoneId", exception);
		}
	}

	private HolidayConfig parseHolidays(String value) {
		Set<MonthDay> annual = new HashSet<>();
		Set<LocalDate> dated = new HashSet<>();

		for (String rawEntry : value.split(",")) {
			String entry = rawEntry.trim();
			if (entry.isEmpty()) {
				continue;
			}

			try {
				if (entry.length() == 5) {
					annual.add(MonthDay.parse("--" + entry));
				} else {
					dated.add(LocalDate.parse(entry));
				}
			} catch (DateTimeException exception) {
				throw new IllegalArgumentException("holidays must use MM-dd or yyyy-MM-dd entries", exception);
			}
		}

		return new HolidayConfig(Set.copyOf(annual), Set.copyOf(dated));
	}

	private record HolidayConfig(Set<MonthDay> annual, Set<LocalDate> dated) {
	}
}
