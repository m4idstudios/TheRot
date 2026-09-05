package dev.doctorm4id.rot.systems

import dev.doctorm4id.m4id.util.M4idBlockUtil
import dev.doctorm4id.rot.content.ModContent
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState

class VirtualSurfaceInfectorCursor(level: ServerLevel) : VirtualCursor(level) {

	override fun isTarget(level: Level, pos: BlockPos): Boolean {
		val blockState = getWorld().getBlockState(pos)
		return !blockState.`is`(ModContent.BlockTags.ROT_FAMILY) && shouldInfest(level, pos)
	}

	override fun changeBlock(pos: BlockPos) {
		InfestationSystem.infestPosition(getWorld() as ServerLevel, pos)
	}

	override fun isObstructed(state: BlockState, pos: BlockPos): Boolean {

		if (M4idBlockUtil.isAir(state)) return true

		if (visitedPositions.contains(pos.asLong())) return true

		if ((state.`is`(Blocks.WATER) || state.`is`(Blocks.BUBBLE_COLUMN))) return true

		if (M4idBlockUtil.isNotSolid(pos, getWorld()) && false) return true

		return false
	}

	fun shouldInfest(level: Level, pos: BlockPos): Boolean {
		val neighbors = M4idBlockUtil.getNeighborsCube(pos, false).filterNotNull()

		val rotNeighbors = neighbors.count { level.getBlockState(it).`is`(ModContent.BlockTags.ROT_FAMILY) }
		val exposed = if (M4idBlockUtil.isExposedToAir(pos, level)) 1.0 else 0.2
		val wetBonus = if (level.getFluidState(pos).isSource) 0.2 else 0.0
		val distance = if (M4idBlockUtil.getBlockDistanceSquared(origin, pos) < 10 * 10) 1.0 else 0.5

		val base = 0.15
		val chance = (((base + (rotNeighbors * 0.3).coerceIn(0.0, 1.0)) * exposed) * distance)

		val willInfest = level.random.nextDouble() < chance.coerceIn(0.0, 0.95)

		return willInfest
	}
}
