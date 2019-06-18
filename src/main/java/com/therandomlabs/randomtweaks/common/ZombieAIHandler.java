package com.therandomlabs.randomtweaks.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.monster.EntityZombie;

public final class ZombieAIHandler {
	public static final class RTEntityAIZombieAttack extends EntityAIZombieAttack {
		public RTEntityAIZombieAttack(EntityZombie zombie, double speed, boolean longMemory) {
			super(zombie, speed, longMemory);
		}

		@Override
		public boolean shouldExecute() {
			final EntityLivingBase target = attacker.getAttackTarget();

			if(target == null || !target.isEntityAlive() ||
					target.dimension != attacker.dimension) {
				return false;
			}

			if(--delayCounter <= 0) {
				path = attacker.getNavigator().getPathToEntityLiving(target);

				if(path != null) {
					return true;
				}

				delayCounter = Math.min(30, (int) Math.max(
						attacker.getDistanceSq(
								target.posX, target.getEntityBoundingBox().minY, target.posZ
						),
						10.0
				));
			}

			return false;
		}
	}

	public static void onZombieJoinWorld(EntityZombie zombie) {
		if(!RTConfig.Misc.zombieTargetDetectionImprovements) {
			return;
		}

		final List<EntityAITasks.EntityAITaskEntry> overrides = new ArrayList<>();
		final Iterator<EntityAITasks.EntityAITaskEntry> it = zombie.tasks.taskEntries.iterator();

		while(it.hasNext()) {
			final EntityAITasks.EntityAITaskEntry entry = it.next();

			if(entry.action instanceof EntityAIZombieAttack &&
					!(entry.action instanceof RTEntityAIZombieAttack)) {
				final EntityAIZombieAttack action = (EntityAIZombieAttack) entry.action;

				overrides.add(zombie.tasks.new EntityAITaskEntry(
						entry.priority,
						new RTEntityAIZombieAttack(
								zombie, action.speedTowardsTarget, action.longMemory
						)
				));

				it.remove();
			}
		}

		zombie.tasks.taskEntries.addAll(overrides);
	}
}
