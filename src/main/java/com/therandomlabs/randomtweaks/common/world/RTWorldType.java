package com.therandomlabs.randomtweaks.common.world;

import net.minecraft.world.WorldType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

public abstract class RTWorldType extends WorldType {
	public RTWorldType(String name) {
		super(name);
		WorldTypeRegistry.WORLD_TYPES.add(this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasInfoNotice() {
		return true;
	}

	public abstract boolean isEnabled();

	public void enable() {
		if(!ArrayUtils.contains(WORLD_TYPES, this)) {
			for(int i = 0; i < WORLD_TYPES.length; i++) {
				if(WORLD_TYPES[i] == null) {
					WORLD_TYPES[i] = this;
					break;
				}
			}
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
