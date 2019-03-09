package com.therandomlabs.randomtweaks.config;

import com.therandomlabs.randomlib.config.ConfigManager;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;

public class RTGuiConfig extends GuiConfig {
	public RTGuiConfig(GuiScreen parentScreen) {
		super(
				parentScreen,
				ConfigManager.getConfigElements(RTConfig.class),
				RandomTweaks.MOD_ID,
				RandomTweaks.MOD_ID,
				false,
				false,
				ConfigManager.getPathString(RTConfig.class)
		);
	}
}
