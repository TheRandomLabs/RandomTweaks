package com.therandomlabs.randomtweaks.config;

import com.therandomlabs.randomlib.config.TRLGuiConfigFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

public class RTGuiConfigFactory extends TRLGuiConfigFactory {
	@Override
	public Class<? extends GuiConfig> mainConfigGuiClass() {
		return RTGuiConfig.class;
	}
}
