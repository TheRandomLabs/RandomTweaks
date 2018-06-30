package com.therandomlabs.randomtweaks.common;

import java.util.AbstractMap;
import java.util.Map;
import com.therandomlabs.randomtweaks.base.RTConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import squeek.applecore.api.food.FoodEvent;
import squeek.applecore.api.hunger.ExhaustionEvent;

public class RTFoodStats extends FoodStats {
	public static class AppleCoreEventHandler {
		@SubscribeEvent
		public void onFoodStatsAddition(FoodEvent.FoodStatsAddition event) {
			event.setCanceled(true);

			final FoodStats stats = event.player.getFoodStats();
			final Map.Entry<Integer, Float> newStats = addStats(stats.foodLevel,
					stats.foodSaturationLevel, event.foodValuesToBeAdded.hunger,
					event.foodValuesToBeAdded.saturationModifier);

			stats.foodLevel = newStats.getKey();
			stats.foodSaturationLevel = newStats.getValue();
		}

		@SubscribeEvent
		public void onExhaustionAddition(ExhaustionEvent.ExhaustionAddition event) {
			if(RTConfig.hunger.halveExhaustion) {
				event.deltaExhaustion /= 2.0F;
			}
		}

		@SubscribeEvent
		public void onExhaustion(ExhaustionEvent.Exhausted event) {
			if(RTConfig.hunger.halveExhaustion) {
				event.deltaExhaustion /= 2.0F;
			}
		}
	}

	public RTFoodStats(FoodStats stats) {
		foodLevel = stats.foodLevel;
		foodExhaustionLevel = stats.foodExhaustionLevel;
		foodSaturationLevel = stats.foodSaturationLevel;
		foodTimer = stats.foodTimer;
	}

	@Override
	public void addStats(int foodLevel, float foodSaturationModifier) {
		final Map.Entry<Integer, Float> stats =
				addStats(this.foodLevel, foodSaturationLevel, foodLevel, foodSaturationModifier);

		this.foodLevel = stats.getKey();
		foodSaturationLevel = stats.getValue();
	}

	@Override
	public void addExhaustion(float exhaustion) {
		if(RTConfig.hunger.halveExhaustion) {
			exhaustion /= 2.0F;
		}

		super.addExhaustion(exhaustion);
	}

	@Override
	public void onUpdate(EntityPlayer player) {
		if(!RTConfig.hunger.halveExhaustion) {
			super.onUpdate(player);
			return;
		}

		final float oldExhaustion = foodExhaustionLevel;
		super.onUpdate(player);
		foodExhaustionLevel = oldExhaustion + (foodExhaustionLevel - oldExhaustion) / 2.0F;
	}

	public static Map.Entry<Integer, Float> addStats(int originalFoodLevel,
			float originalSaturation, int foodLevel, float foodSaturationModifier) {
		originalFoodLevel += foodLevel;
		float newSaturation = originalSaturation + foodLevel * foodSaturationModifier;

		if(originalFoodLevel > RTConfig.hunger.maximumHungerLevel) {
			if(RTConfig.hunger.carryExcessHungerToSaturation) {
				newSaturation += originalFoodLevel - RTConfig.hunger.maximumHungerLevel;
			}

			originalFoodLevel = RTConfig.hunger.maximumHungerLevel;
		}

		float maxSaturationLevel = originalFoodLevel + (float) RTConfig.hunger.saturationLimit;

		//In the unlikely event of overflow
		if(maxSaturationLevel == Float.NEGATIVE_INFINITY) {
			maxSaturationLevel = Float.MAX_VALUE;
		}

		originalSaturation = Math.min(newSaturation, maxSaturationLevel);

		return new AbstractMap.SimpleEntry<>(originalFoodLevel, originalSaturation);
	}
}
