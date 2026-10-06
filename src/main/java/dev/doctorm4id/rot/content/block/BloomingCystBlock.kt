package dev.doctorm4id.rot.content.block

import dev.doctorm4id.rot.systems.CursorManager
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import kotlin.random.Random

class BloomingCystBlock(properties: Properties) : Block(properties) {
	override fun randomTick(blockState: BlockState, serverLevel: ServerLevel, blockPos: BlockPos, randomSource: RandomSource) {
		if (Random.nextFloat() < 0.15f) {
			CursorManager.createSurfaceInfectorVirtualCursor(serverLevel, blockPos)
		}
	}
}
