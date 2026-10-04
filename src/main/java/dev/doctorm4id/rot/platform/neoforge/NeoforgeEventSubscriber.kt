package dev.doctorm4id.rot.platform.neoforge

//? neoforge {

/*import dev.doctorm4id.rot.content.ModContent
import dev.doctorm4id.rot.event.ExampleEventHandler
import dev.doctorm4id.rot.systems.CursorManager
import dev.doctorm4id.m4id.registration.M4idRegistrar
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import net.neoforged.neoforge.registries.RegisterEvent

@EventBusSubscriber
object NeoforgeEventSubscriber {

	@JvmStatic
	@SubscribeEvent
	fun onPlayerDamage(event: LivingDamageEvent.Post) {
		if (event.entity is ServerPlayer && event.newDamage > 0) {

			val player = event.entity as ServerPlayer
			ExampleEventHandler().onPlayerHurt(player)
		}
	}

	@JvmStatic
	@SubscribeEvent
	fun registerRegistryContent(evt: RegisterEvent) {
		M4idRegistrar[ModContent].content.forEach { entry ->
			evt.register(entry.registry.key() as ResourceKey<out Registry<Any>>) {
				it.register(ResourceLocation.fromNamespaceAndPath(ModContent.getId(), entry.itemId), entry.item.value!!)
			}
		}
	}


	@JvmStatic
	@SubscribeEvent
	fun serverTick(event: ServerTickEvent.Post) {
		CursorManager.tick()
	}
}

*///? }
