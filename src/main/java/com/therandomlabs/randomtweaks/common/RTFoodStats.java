package com.therandomlabs.randomtweaks.common;

import net.minecraft.util.FoodStats;

public class RTFoodStats extends FoodStats {
	public RTFoodStats(FoodStats stats) {
		foodLevel = stats.foodLevel;
		foodExhaustionLevel = stats.foodExhaustionLevel;
		foodSaturationLevel = stats.foodSaturationLevel;
		foodTimer = stats.foodTimer;
	}

	@Override
	public void addStats(int foodLevel, float foodSaturationModifier) {
		this.foodLevel += foodLevel;
		float newSaturation = foodSaturationLevel + foodLevel * foodSaturationModifier;

		if(this.foodLevel > RTConfig.hunger.maximumHungerLevel) {
			if(RTConfig.hunger.carryExcessHungerToSaturation) {
				newSaturation += this.foodLevel - RTConfig.hunger.maximumHungerLevel;
			}

			this.foodLevel = RTConfig.hunger.maximumHungerLevel;
		}

		foodSaturationLevel = Math.min(newSaturation,
				this.foodLevel + RTConfig.hunger.saturationLimit);
	}
}
