package dev.doctorm4id.rot.systems

import dev.doctorm4id.rot.TheRot
import dev.doctorm4id.rot.content.ModContent
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.chunk.LevelChunk
import kotlin.random.Random

class VirtualChunkCursor(level: ServerLevel, private var chunkPos: ChunkPos) : VirtualCursor(level) {
	private companion object {
		const val BLOCKS_PER_TICK = 256
	}

	private var chunk: LevelChunk = level.getChunk(chunkPos.x, chunkPos.z)

	private val minX = 0
	private val minZ = 0
	private val maxX = 16
	private val maxZ = 16

	private val minY = level.minBuildHeight
	private val maxY = level.maxBuildHeight

	private var x = 0
	private var y = minY
	private var z = 0

	private val mutablePos = BlockPos.MutableBlockPos()

/*	init {
		generateSurfacePositions()
	}*/

/*	private fun generateSurfacePositions() {
		for (x in minX until maxX) {
			for (z in minZ until maxZ) {

				var y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1

				while (y > minY && level.getBlockState(BlockPos(x, y, z)).`is`(ModContent.ROTTED_VEIN)) {
					y--
				}

				val pos = BlockPos(x, y, z)
				val state = level.getBlockState(pos)

				if (M4idBlockUtil.isAir(state)) continue

				if (!state.`is`(ModContent.BlockTags.ROT_FAMILY)) {
					unrottedSurface += pos
				}
			}
		}
	}*/

	override fun tick() {
		//updateSurface()

/*		if (unrottedSurface.isEmpty()) {
			finish()
			return
		}*/

		repeat(BLOCKS_PER_TICK) {
			if (y >= maxY) {
				finish()
				return
			}

			//val pos = BlockPos(x, y, z)

			val section = chunk.getSection(level.getSectionIndex(y))

			if (section.hasOnlyAir()) {
				y = ((y shr 4) + 1) shl 4
				return@repeat
			}

			val state = section.getBlockState(
				x,
				y and 15,
				z
			)

			mutablePos.set(chunkPos.minBlockX + x, y, chunkPos.minBlockZ + z)

/*			if (!state.`is`(ModContent.BlockTags.ROT_FAMILY) && !state.isAir && !state.`is`(Blocks.GLASS)) {
				level.setBlockAndUpdate(mutablePos, Blocks.BEDROCK.defaultBlockState())
			} else if (state.isAir) {
				level.setBlockAndUpdate(mutablePos, Blocks.GLASS.defaultBlockState())
			}*/

			//level.setBlockAndUpdate(mutablePos, Blocks.AIR.defaultBlockState())

			//InfestationSystem.infestPosition(level, mutablePos)

			if (Random.nextFloat() < 0.3f) {
				InfestationSystem.infestPosition(level, mutablePos)
				//CursorManager.createSurfaceInfectorVirtualCursor(level, mutablePos)
			}/* else {
				level.setBlockAndUpdate(mutablePos, Blocks.BEDROCK.defaultBlockState())
			}*/

			advance()
		}

		//updateSurface()

/*		if (unrottedSurface.isEmpty()) {
			finish()
		}*/
	}

	private fun advance() {
		x++

		if (x == 16) {
			x = 0
			z++

			if (z == 16) {
				z = 0
				y++
			}
		}
	}

/*	private fun updateSurface() {
		unrottedSurface.removeIf {
			pos -> level.getBlockState(pos).`is`(ModContent.BlockTags.ROT_FAMILY)
		}
	}*/

/*	private fun canInfect(pos: BlockPos): Boolean {
		val state = level.getBlockState(pos)

		if (M4idBlockUtil.isAir(state))
			return false

		return !state.`is`(ModContent.BlockTags.ROT_FAMILY)
	}*/

	private fun finish() {
		val neighbors = arrayOf(
			ChunkPos(chunkPos.x + 1, chunkPos.z),
			ChunkPos(chunkPos.x - 1, chunkPos.z),
			ChunkPos(chunkPos.x, chunkPos.z + 1),
			ChunkPos(chunkPos.x, chunkPos.z - 1)
		)

		val next = neighbors
			.filter { level.hasChunkAt(BlockPos(it.middleBlockX, 0, it.middleBlockZ)) }
			.randomOrNull()

		if (next != null) {
/*			CursorManager.addVirtualCursor(
				VirtualChunkCursor(level, next)
			)*/

			//TheRot.LOGGER.info(next.toString())

			chunkPos = next
			chunk = level.getChunk(next.x, next.z)

			x = minX
			y = minY
			z = minZ
		} else {
			setExpired()
		}

		//setExpired()
	}
}
