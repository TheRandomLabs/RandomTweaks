package com.therandomlabs.randomtweaks.common;

import java.util.HashMap;
import java.util.Map;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RandomTweaks.MOD_ID)
public final class ColoredSheepHandler {
	private static final Map<Integer, EnumDyeColor> queue = new HashMap<>();

	public static void onSheepSpawn(EntitySheep sheep) {
		final double result = sheep.getRNG().nextDouble() * RTConfig.SheepColorWeights.totalWeight;
		double totalWeight = 0.0;

		for(Map.Entry<EnumDyeColor, Double> weight :
				RTConfig.SheepColorWeights.weights.entrySet()) {
			totalWeight += weight.getValue();

			if(totalWeight >= result) {
				queue.put(sheep.getEntityId(), weight.getKey());
				break;
			}
		}
	}

	public static void onSheepTick(EntitySheep sheep) {
		final int id = sheep.getEntityId();
		final EnumDyeColor color = queue.get(id);

		if(color != null) {
			sheep.setFleeceColor(color);
			queue.remove(id);
		}
	}
}
