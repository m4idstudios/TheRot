package dev.doctorm4id.rot.platform.fabric.datagen

//? fabric {

import dev.doctorm4id.rot.TheRot
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelTemplates

class FabricModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {

	override fun generateBlockStateModels(blockStateModelGenerator: BlockModelGenerators?) {

/*		blockStateModelGenerator?.createTrivialCube(ModRegistry.ROTTED_LEAVES())
		blockStateModelGenerator?.family(ModRegistry.ROTTED_BLOCK())
			?.fence(ModRegistry.ROTTED_FENCE())
			?.slab(ModRegistry.ROTTED_SLAB())
			?.stairs(ModRegistry.ROTTED_STAIR())
			?.wall(ModRegistry.ROTTED_WALL())

		blockStateModelGenerator?.createTrivialCube(ModRegistry.NULL_CYAN())
		blockStateModelGenerator?.createTrivialCube(ModRegistry.NULL_GREEN())
		blockStateModelGenerator?.createTrivialCube(ModRegistry.NULL_RED())
		blockStateModelGenerator?.createTrivialCube(ModRegistry.NULL_WHITE())
		blockStateModelGenerator?.createTrivialCube(ModRegistry.NULL_PINK())
		blockStateModelGenerator?.createTrivialCube(ModRegistry.NULL_YELLOW())
		blockStateModelGenerator?.createTrivialCube(ModRegistry.NULL_BLUE())*/

	}

	override fun generateItemModels(itemModelGenerator: ItemModelGenerators?) {
/*		itemModelGenerator?.generateFlatItem(ModRegistry.CURSOR_WAND(), ModelTemplates.FLAT_ITEM)
		itemModelGenerator?.generateFlatItem(ModRegistry.INFESST_WAND(), ModelTemplates.FLAT_ITEM)*/
	}

	override fun getName(): String {

		return TheRot.MOD_FRIENDLY_NAME+" Model Provider"
	}
}

//? }
