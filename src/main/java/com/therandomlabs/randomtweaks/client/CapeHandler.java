package com.therandomlabs.randomtweaks.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import com.therandomlabs.randomtweaks.util.Compat;
import com.therandomlabs.randomtweaks.util.Utils;
import com.therandomlabs.randomtweaks.util.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.io.IOUtils;

@EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public final class CapeHandler {
	public static final Method GET_PLAYER_INFO = Compat.findMethod(
			AbstractClientPlayer.class, "getPlayerInfo", "func_175155_b");
	public static final Field PLAYER_TEXTURES = ReflectionHelper.findField(NetworkPlayerInfo.class,
			"playerTextures", "field_187107_a");
	public static final String CONTRIBUTORS_URL =
			"https://raw.githubusercontent.com/TheRandomLabs/RandomTweaks/misc/contributors.txt";
	public static final ResourceLocation CAPE_LOCATION =
			new ResourceLocation(RandomTweaks.MODID, "textures/cape.png");

	private static List<String> players;

	@SubscribeEvent
	public static void entityJoinWorld(EntityJoinWorldEvent event) {
		if(!RTConfig.client.contributorCapes) {
			return;
		}

		final Entity entity = event.getEntity();
		if(entity instanceof AbstractClientPlayer) {
			if(!shouldHaveCape((AbstractClientPlayer) entity)) {
				return;
			}

			Minecraft.getMinecraft().addScheduledTask(() -> {
				try {
					setCape(entity);
				} catch(Exception ex) {
					RandomTweaks.LOGGER.error(
							"Failed to set cape for player: " + entity.getName(), ex);
				}
			});
		}
	}

	public static boolean shouldHaveCape(AbstractClientPlayer player) {
		//Always have a cape in a development environment
		if(Utils.isDeobfuscated()) {
			return true;
		}

		return players != null && players.contains(player.getUniqueID().toString());
	}

	private static void setCape(Entity entity) throws Exception {
		final Wrapper<Exception> exception = new Wrapper<>();

		if(!actuallySetCape(entity)) {
			new Thread(() -> {
				try {
					for(int i = 0; i < 3; i++) {
						if(actuallySetCape(entity)) {
							break;
						}

						Thread.sleep(2500L);
					}
				} catch(Exception ex) {
					exception.set(ex);
				}
			}).start();
		}

		if(exception.get() != null) {
			RandomTweaks.LOGGER.error("Failed to set cape", exception.get());
		}
	}

	private static boolean actuallySetCape(Entity entity) throws Exception {
		final NetworkPlayerInfo info = (NetworkPlayerInfo) GET_PLAYER_INFO.invoke(entity);

		//Usually because the client has sent too many requests within a certain amount of time
		//or because the player UUID is invalid (e.g. if you're in a development environment)
		if(info == null) {
			return false;
		}

		@SuppressWarnings("unchecked")
		final Map<MinecraftProfileTexture.Type, ResourceLocation> playerTextures =
				(Map<Type, ResourceLocation>) PLAYER_TEXTURES.get(info);

		playerTextures.put(MinecraftProfileTexture.Type.CAPE, CAPE_LOCATION);
		playerTextures.put(MinecraftProfileTexture.Type.ELYTRA, CAPE_LOCATION);

		return true;
	}

	public static void downloadPlayers() {
		new Thread(() -> {
			try {
				final HttpURLConnection connection =
						(HttpURLConnection) new URL(CONTRIBUTORS_URL).openConnection();
				connection.setConnectTimeout(1000);
				players = IOUtils.readLines(connection.getInputStream(), StandardCharsets.UTF_8);
				connection.disconnect();
			} catch(Exception ex) {
				RandomTweaks.LOGGER.error("Could not download cape player list", ex);
			}
		}).start();
	}
}
