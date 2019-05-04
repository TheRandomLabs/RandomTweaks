package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import squeek.applecore.api.food.FoodEvent;
import squeek.applecore.api.hunger.ExhaustionEvent;

public class RTFoodStats extends FoodStats {
	public static class AppleCoreEventHandler {
		@SubscribeEvent
		public static void onFoodStatsAddition(FoodEvent.FoodStatsAddition event) {
			event.setCanceled(true);

			final FoodStats stats = event.player.getFoodStats();
			final Tuple<Integer, Float> newStats = addStats(
					stats.foodLevel,
					stats.foodSaturationLevel,
					event.foodValuesToBeAdded.hunger,
					event.foodValuesToBeAdded.saturationModifier
			);

			stats.foodLevel = newStats.getFirst();
			stats.foodSaturationLevel = newStats.getSecond();
		}

		@SubscribeEvent
		public static void onExhaustionAddition(ExhaustionEvent.ExhaustionAddition event) {
			event.deltaExhaustion *= RTConfig.Hunger.exhaustionMultiplier;
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
		final Tuple<Integer, Float> stats = addStats(
				this.foodLevel,
				foodSaturationLevel,
				foodLevel,
				foodSaturationModifier
		);

		this.foodLevel = stats.getFirst();
		foodSaturationLevel = stats.getSecond();
	}

	@Override
	public void addExhaustion(float exhaustion) {
		super.addExhaustion(exhaustion * (float) RTConfig.Hunger.exhaustionMultiplier);
	}

	public static Tuple<Integer, Float> addStats(int originalFoodLevel, float originalSaturation,
			int foodLevel, float foodSaturationModifier) {
		int newFoodLevel = originalFoodLevel + foodLevel;
		float newSaturation = originalSaturation + foodLevel * foodSaturationModifier * 2.0F;

		if(newFoodLevel > RTConfig.Hunger.maximumHungerLevel) {
			if(RTConfig.Hunger.carryExcessHungerToSaturation) {
				newSaturation += originalFoodLevel - RTConfig.Hunger.maximumHungerLevel;
			}

			newFoodLevel = RTConfig.Hunger.maximumHungerLevel;
		}

		float maxSaturationLevel = newFoodLevel + (float) RTConfig.Hunger.saturationLimit;

		//In the unlikely event of overflow
		if(maxSaturationLevel == Float.NEGATIVE_INFINITY) {
			maxSaturationLevel = Float.MAX_VALUE;
		}

		newSaturation = Math.min(newSaturation, maxSaturationLevel);

		return new Tuple<>(newFoodLevel, newSaturation);
	}
}
