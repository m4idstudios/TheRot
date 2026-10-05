package dev.doctorm4id.rot.platform.fabric.datagen

//? fabric {

import dev.doctorm4id.rot.TheRot
import dev.doctorm4id.rot.content.ModContent
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelTemplates

class FabricModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {

	override fun generateBlockStateModels(blockStateModelGenerator: BlockModelGenerators?) {

		blockStateModelGenerator?.createTrivialCube(ModContent.ROTTED_LEAVES)
		blockStateModelGenerator?.family(ModContent.ROTTED_BLOCK)
			?.fence(ModContent.ROTTED_FENCE)
			?.slab(ModContent.ROTTED_SLAB)
			?.stairs(ModContent.ROTTED_STAIR)
			?.wall(ModContent.ROTTED_WALL)
	}

	override fun generateItemModels(itemModelGenerator: ItemModelGenerators?) {
		itemModelGenerator?.generateFlatItem(ModContent.CURSOR_WAND_ITEM, ModelTemplates.FLAT_ITEM)
		itemModelGenerator?.generateFlatItem(ModContent.CHUNK_WAND_ITEM, ModelTemplates.FLAT_ITEM)
		itemModelGenerator?.generateFlatItem(ModContent.INFEST_WAND_ITEM, ModelTemplates.FLAT_ITEM)
	}

	override fun getName(): String {

		return TheRot.MOD_FRIENDLY_NAME+" Model Provider"
	}
}

//? }
