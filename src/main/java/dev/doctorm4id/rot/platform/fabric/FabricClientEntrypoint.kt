package dev.doctorm4id.rot.platform.fabric

//? fabric {

import dev.doctorm4id.rot.TheRot
//? <= 1.20.1 {
/*import dev.doctorm4id.rot.core.ModRenderLayers
*///? }
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.renderer.RenderType

@Entrypoint("client")
class FabricClientEntrypoint : ClientModInitializer {

	override fun onInitializeClient() {
		TheRot().onInitializeClient()
	}
}

//? }
