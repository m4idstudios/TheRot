package dev.doctorm4id.rot.platform.fabric

//? fabric {

import dev.doctorm4id.rot.TheRot
import dev.doctorm4id.rot.content.ModContent
//? <= 1.20.1 {
/*import dev.doctorm4id.rot.core.ModRenderLayers
*///? }
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.renderer.RenderType

@Entrypoint("client")
class FabricClientEntrypoint : ClientModInitializer {

	override fun onInitializeClient() {
		TheRot().onInitializeClient()

		BlockRenderLayerMap.INSTANCE.putBlock(
			ModContent.ROTTED_GRASS,
			RenderType.cutout()
		)

		BlockRenderLayerMap.INSTANCE.putBlock(
			ModContent.ROTTED_VEIN,
			RenderType.cutout()
		)
	}
}

//? }
