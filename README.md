# Phantom Schedule

I still want to achieve all advancements on my SMP, but I do not want phantoms every night. This Fabric server-side mod keeps vanilla phantom spawning behavior available for advancement progress, while limiting phantoms to real-world night or configured holidays.

## Config

The mod creates `config/phantom-schedule.properties` on first server start:

```properties
enabled=true
start=19:00
end=06:00
zone=system
holidays=
```

`start` and `end` use `HH:mm` 24-hour real-world time. Windows that pass midnight are   supported, so `19:00` to `06:00` means 7 PM through 5:59 AM.

`zone=system` uses the server machine timezone. You can also set a Java ZoneId such as `Asia/Taipei` or `America/New_York`.

`holidays` is a comma-separated allow-list. Use `MM-dd` for annual holidays or `yyyy-MM-dd` for one specific date:

```properties
holidays=01-01,10-31,12-25,2026-08-09
```

Changes are picked up automatically when phantom spawn rules are checked. No server reload is needed.
