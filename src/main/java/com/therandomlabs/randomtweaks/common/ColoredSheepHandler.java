package com.therandomlabs.randomtweaks.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.therandomlabs.randomtweaks.base.RTConfig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;

public final class ColoredSheepHandler {
	private static final Map<Integer, EnumDyeColor> QUEUE = new ConcurrentHashMap<>();

	public static void onSheepSpawn(EntitySheep sheep) {
		if(sheep.isChild()) {
			return;
		}

		final Map<EnumDyeColor, Double> weights = RTConfig.SheepColorWeights.WEIGHTS;
		double weightTotal = 0.0;

		for(double weight : weights.values()) {
			weightTotal += weight;
		}

		final double result = sheep.getRNG().nextDouble() * weightTotal;
		weightTotal = 0.0;

		for(Map.Entry<EnumDyeColor, Double> weight : weights.entrySet()) {
			weightTotal += weight.getValue();

			if(weightTotal >= result) {
				QUEUE.put(sheep.getEntityId(), weight.getKey());
				break;
			}
		}
	}

	public static void onSheepTick(EntitySheep sheep) {
		final int id = sheep.getEntityId();
		final EnumDyeColor color = QUEUE.get(id);

		if(color != null) {
			sheep.setFleeceColor(color);
			QUEUE.remove(id);
		}
	}
}
