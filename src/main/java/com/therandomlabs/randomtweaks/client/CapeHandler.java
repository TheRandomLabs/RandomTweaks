package com.therandomlabs.randomtweaks.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import com.therandomlabs.randomtweaks.util.Utils;
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

@EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public final class CapeHandler {
	public static final Method GET_PLAYER_INFO = ReflectionHelper.findMethod(
			AbstractClientPlayer.class, "getPlayerInfo", "func_175155_b");
	public static final Field PLAYER_TEXTURES = ReflectionHelper.findField(NetworkPlayerInfo.class,
			"playerTextures", "field_187107_a");
	public static final ResourceLocation CAPE_LOCATION =
			new ResourceLocation(RandomTweaks.MODID, "textures/cape.png");

	public static final List<String> CONTRIBUTORS = Arrays.asList(
			"de2b3ebd-c0e9-4f43-b0f7-b660d482dd51",
			"819eb301-e040-4580-9c63-3f98684f58bc",
			"1dbb2583-db0a-4c8a-b187-f62bdde4595d",
			"fc2c6552-9a1d-4d7e-b9c1-2fef96cacc6c"
	);

	@SubscribeEvent
	public static void entityJoinWorld(EntityJoinWorldEvent event) {
		if(!RTConfig.client.contributorCapes) {
			return;
		}

		final Entity entity = event.getEntity();

		if(entity instanceof AbstractClientPlayer && hasCape((AbstractClientPlayer) entity)) {
			Minecraft.getMinecraft().addScheduledTask(() -> setCape(entity));
		}
	}

	public static boolean hasCape(AbstractClientPlayer player) {
		return Utils.isDeobfuscated() || CONTRIBUTORS.contains(player.getUniqueID().toString());
	}

	private static void setCape(Entity entity) {
		try {
			final NetworkPlayerInfo info = (NetworkPlayerInfo) GET_PLAYER_INFO.invoke(entity);

			//Usually because the client has sent too many requests within a certain amount of time
			//or because the player UUID is invalid
			if(info == null) {
				return;
			}

			@SuppressWarnings("unchecked")
			final Map<MinecraftProfileTexture.Type, ResourceLocation> playerTextures =
					(Map<Type, ResourceLocation>) PLAYER_TEXTURES.get(info);

			playerTextures.put(MinecraftProfileTexture.Type.CAPE, CAPE_LOCATION);
			playerTextures.put(MinecraftProfileTexture.Type.ELYTRA, CAPE_LOCATION);
		} catch(Exception ex) {
			RandomTweaks.LOGGER.error("Failed to set cape for player: " + entity.getName(), ex);
		}
	}
}
