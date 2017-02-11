package com.therandomlabs.randomtweaks.common;

import java.util.Map;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class WorldCreateHandler {
	private boolean firstLoad;

	@SubscribeEvent
	public void onCreateSpawn(WorldEvent.CreateSpawnPosition event) {
		final World world = event.getWorld();
		if(!world.isRemote && world.provider.getDimension() == DimensionType.OVERWORLD.getId()) {
			firstLoad = true;
		}
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) throws Exception {
		final World world = event.getWorld();
		if(!world.isRemote && world.provider.getDimension() == DimensionType.OVERWORLD.getId() &&
				firstLoad) {
			onWorldCreate(world);
			firstLoad = false;
		}
	}

	private void onWorldCreate(World world) throws Exception {
		final GameRules gamerules = world.getGameRules();
		final Map<String, String> defaultGamerules = ConfigurationHandler.getDefaultGamerules(
				world.getWorldInfo().getGameType().getID());

		if(defaultGamerules == null) {
			world.getMinecraftServer().sendMessage(
					new TextComponentTranslation("defaultGamerules.parseFailure"));
		}

		for(String name : defaultGamerules.keySet()) {
			gamerules.setOrCreateGameRule(name, defaultGamerules.get(name));
		}
	}
}