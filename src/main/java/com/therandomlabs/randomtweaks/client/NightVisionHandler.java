package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.MathHelper;

//Taken and adapted from:
//https://github.com/CoFH/NoNVFlash/blob/1.12/src/main/java/cofh/nonvflash/NoNVFlash.java
public final class NightVisionHandler {
	private NightVisionHandler() {}

	public static void initialize() {
		final Minecraft mc = Minecraft.getMinecraft();

		//Avoid conflicts.
		if (mc.entityRenderer.getClass() != EntityRenderer.class) {
			return;
		}

		final SimpleReloadableResourceManager manager =
				(SimpleReloadableResourceManager) mc.getResourceManager();

		manager.reloadListeners.remove(mc.entityRenderer);

		mc.entityRenderer = new EntityRenderer(mc, mc.getResourceManager()) {
			@Override
			public float getNightVisionBrightness(EntityLivingBase entity, float partialTicks) {
				final int duration =
						entity.getActivePotionEffect(MobEffects.NIGHT_VISION).getDuration();

				if(duration > 200 || !RTConfig.NightVision.fadeOut) {
					return RTConfig.NightVision.brightness;
				}

				if(!RTConfig.NightVision.disableFlashing) {
					return 0.7F + MathHelper.sin(
							(duration - partialTicks) * (float) Math.PI * 0.2F
					) * 0.3F;
				}

				if(duration > RTConfig.NightVision.fadeOutTicks) {
					return RTConfig.NightVision.brightness;
				}

				return duration * RTConfig.NightVision.fadeOutRate;
			}
		};

		manager.registerReloadListener(mc.entityRenderer);
	}
}
