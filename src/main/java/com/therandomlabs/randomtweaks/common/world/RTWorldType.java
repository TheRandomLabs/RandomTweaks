package com.therandomlabs.randomtweaks.common.world;

import java.util.Arrays;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

//A lot of this code is just so the world type options can be toggled in-game
//Worth it? Maybe
public abstract class RTWorldType extends WorldType {
	public RTWorldType(String name) {
		super(name);
		WorldTypeRegistry.worldTypes.add(this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean showWorldInfoNotice() {
		return true;
	}

	public abstract boolean isEnabled();

	public void enable() {
		if(WORLD_TYPES[worldTypeId] != this) {
			if(WORLD_TYPES[worldTypeId] == null) {
				WORLD_TYPES[worldTypeId] = this;
				return;
			}

			for(int i = 0; i < WORLD_TYPES.length; i++) {
				if(WORLD_TYPES[i] == null) {
					WORLD_TYPES[i] = this;
					worldTypeId = i;
					return;
				}
			}

			final int oldLength = WORLD_TYPES.length;
			WORLD_TYPES = Arrays.copyOf(WORLD_TYPES, oldLength + 16);
			WORLD_TYPES[oldLength] = this;
			worldTypeId = oldLength;
		}
	}

	public void disable() {
		final int index = ArrayUtils.indexOf(WORLD_TYPES, this);

		if(index != ArrayUtils.INDEX_NOT_FOUND) {
			WORLD_TYPES[index] = null;
		}
	}

	public void onConfigReload() {
		if(isEnabled()) {
			enable();
		} else {
			disable();
		}
	}
}
