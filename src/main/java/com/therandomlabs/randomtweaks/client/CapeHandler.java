package com.therandomlabs.randomtweaks.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.therandomlabs.randomlib.TRLUtils;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.io.IOUtils;

@Mod.EventBusSubscriber(Side.CLIENT)
public final class CapeHandler {
	public static final ResourceLocation CAPE_LOCATION =
			new ResourceLocation(RandomTweaks.MOD_ID, "textures/cape.png");

	public static final String CONTRIBUTORS_URL =
			"https://raw.githubusercontent.com/TheRandomLabs/RandomTweaks/misc/contributors.txt";

	private static List<String> contributors = Collections.emptyList();

	@SubscribeEvent
	public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
		if(!RTConfig.Client.contributorCapes) {
			return;
		}

		final AbstractClientPlayer player = (AbstractClientPlayer) event.getEntityPlayer();

		if(!hasCape(player)) {
			return;
		}

		NetworkPlayerInfo info = player.getPlayerInfo();

		if(info == null) {
			info = new NetworkPlayerInfo(player.getGameProfile());
			//So onRenderPlayerPost knows to set playerInfo back to null
			info.responseTime = Integer.MIN_VALUE;
			player.playerInfo = info;
		}

		if(info.playerTextures.get(MinecraftProfileTexture.Type.CAPE) == null) {
			info.playerTextures.put(MinecraftProfileTexture.Type.CAPE, CAPE_LOCATION);
			info.playerTextures.put(MinecraftProfileTexture.Type.ELYTRA, CAPE_LOCATION);
		}
	}

	@SubscribeEvent
	public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
		if(!RTConfig.Client.contributorCapes) {
			return;
		}

		final AbstractClientPlayer player = (AbstractClientPlayer) event.getEntityPlayer();

		if(hasCape(player) && player.playerInfo != null &&
				player.playerInfo.responseTime == Integer.MIN_VALUE) {
			player.playerInfo = null;
		}
	}

	public static void downloadPlayers() {
		new Thread(() -> {
			try {
				final HttpURLConnection connection =
						(HttpURLConnection) new URL(CONTRIBUTORS_URL).openConnection();
				connection.setConnectTimeout(1000);

				contributors = IOUtils.readLines(
						connection.getInputStream(), StandardCharsets.UTF_8
				);

				connection.disconnect();
			} catch(IOException ex) {
				RandomTweaks.LOGGER.error("Failed to download conbtributor list", ex);
			}
		}).start();
	}

	public static boolean hasCape(EntityPlayer player) {
		return TRLUtils.IS_DEOBFUSCATED || contributors.contains(player.getUniqueID().toString());
	}
}
