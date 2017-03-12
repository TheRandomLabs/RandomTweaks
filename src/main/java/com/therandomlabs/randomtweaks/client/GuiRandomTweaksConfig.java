package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.common.ConfigurationHandler;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiRandomTweaksConfig extends GuiConfig {
	public GuiRandomTweaksConfig(GuiScreen parentScreen) {
		super(parentScreen, ConfigurationHandler.getConfigElements(), RandomTweaks.MODID, false,
				false, "RandomTweaks Config");
	}
}
