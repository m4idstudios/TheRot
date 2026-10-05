package dev.doctorm4id.rot.platform.fabric

//? fabric {

import dev.doctorm4id.rot.event.ExampleEventHandler
import dev.doctorm4id.rot.systems.CursorManager
import dev.doctorm4id.rot.systems.InfestationSystem
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity

class FabricEventSubscriber {

	fun registerEvents() {
		//? != 1.20.1 {
		ServerLivingEntityEvents.AFTER_DAMAGE.register(ServerLivingEntityEvents.AfterDamage { entity: LivingEntity?, source: DamageSource?, baseDamage: Float, damageTaken: Float, blocked: Boolean ->
			if (entity is ServerPlayer && damageTaken > 0) {
				ExampleEventHandler().onPlayerHurt(entity)
			}
		})
		//?}

		ServerLifecycleEvents.SERVER_STOPPING.register {
			CursorManager.clear()
		}

		ServerTickEvents.START_SERVER_TICK.register(ServerTickEvents.StartTick { server ->
			CursorManager.tick()
        })
	}
}

//? }
