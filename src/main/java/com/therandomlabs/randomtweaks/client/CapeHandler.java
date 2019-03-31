package com.therandomlabs.randomtweaks.client;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.therandomlabs.randomlib.TRLUtils;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MOD_ID)
public final class CapeHandler {
	public static final ResourceLocation CAPE_LOCATION =
			new ResourceLocation(RandomTweaks.MOD_ID, "textures/cape.png");

	public static final ImmutableList<String> CONTRIBUTORS = ImmutableList.of(
			"de2b3ebd-c0e9-4f43-b0f7-b660d482dd51",
			"819eb301-e040-4580-9c63-3f98684f58bc",
			"fc2c6552-9a1d-4d7e-b9c1-2fef96cacc6c"
	);

	@SubscribeEvent
	public static void entityJoinWorld(EntityJoinWorldEvent event) {
		if(!RTConfig.Client.contributorCapes) {
			return;
		}

		final Entity entity = event.getEntity();

		if(entity instanceof AbstractClientPlayer) {
			final AbstractClientPlayer player = (AbstractClientPlayer) entity;

			if(hasCape(player)) {
				Minecraft.getMinecraft().addScheduledTask(() -> setCape(player));
			}
		}
	}

	public static boolean hasCape(AbstractClientPlayer player) {
		return TRLUtils.IS_DEOBFUSCATED || CONTRIBUTORS.contains(player.getUniqueID().toString());
	}

	private static void setCape(AbstractClientPlayer player) {
		final NetworkPlayerInfo info = player.getPlayerInfo();

		//Usually because the client has sent too many requests within a certain amount of time
		//or because the player UUID is invalid
		if(info == null) {
			return;
		}

		info.playerTextures.put(MinecraftProfileTexture.Type.CAPE, CAPE_LOCATION);
		info.playerTextures.put(MinecraftProfileTexture.Type.ELYTRA, CAPE_LOCATION);
	}
}
