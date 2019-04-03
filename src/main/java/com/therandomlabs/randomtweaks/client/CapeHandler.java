package com.therandomlabs.randomtweaks.client;

import com.google.common.collect.ImmutableList;
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

@Mod.EventBusSubscriber(Side.CLIENT)
public final class CapeHandler {
	public static final ResourceLocation CAPE_LOCATION =
			new ResourceLocation(RandomTweaks.MOD_ID, "textures/cape.png");

	public static final ImmutableList<String> CONTRIBUTORS = ImmutableList.of(
			"de2b3ebd-c0e9-4f43-b0f7-b660d482dd51",
			"819eb301-e040-4580-9c63-3f98684f58bc",
			"fc2c6552-9a1d-4d7e-b9c1-2fef96cacc6c"
	);

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

	public static boolean hasCape(EntityPlayer player) {
		return TRLUtils.IS_DEOBFUSCATED || CONTRIBUTORS.contains(player.getUniqueID().toString());
	}
}
