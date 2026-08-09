package org.junner.phantomschedule.mixin;

import java.util.List;
import net.minecraft.world.level.CustomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.server.level.ServerLevel.class)
public interface ServerLevelAccessor {
	@Accessor("customSpawners")
	List<CustomSpawner> phantomSchedule$getCustomSpawners();
}
