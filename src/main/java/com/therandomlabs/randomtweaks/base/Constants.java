package com.therandomlabs.randomtweaks.base;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Loader;

public final class Constants {
	public static final boolean APPLECORE_LOADED = Loader.isModLoaded("applecore");
	public static final boolean COLORFUL_SHEEP_LOADED = Loader.isModLoaded("colorful-sheep");
	public static final boolean COMFORTS_LOADED = Loader.isModLoaded("comforts");
	public static final boolean DING_LOADED = Loader.isModLoaded("ding");
	public static final boolean DYNAMIC_SURROUNDINGS_LOADED = Loader.isModLoaded("dsurround");
	public static final boolean EXPANDED_BONEMEAL_LOADED = Loader.isModLoaded("expandedbonemeal");
	public static final boolean HEADCRUMBS_LOADED = Loader.isModLoaded("headcrumbs");
	public static final boolean HELPFIXER_LOADED = Loader.isModLoaded("helpfixer");
	public static final boolean PLAYERS_DROP_HEADS_LOADED = Loader.isModLoaded("playersdropheads");
	public static final boolean QUARK_LOADED = Loader.isModLoaded("quark");
	public static final boolean OCEAN_FLOOR_LOADED = Loader.isModLoaded("samsocean");
	public static final boolean RANDOMPATCHES_LOADED = Loader.isModLoaded("randompatches");

	public static final boolean IS_DEOBFUSCATED =
			(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

	private Constants() {}
}
