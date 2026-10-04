package dev.doctorm4id.rot.systems

import dev.doctorm4id.m4id.util.M4idBlockUtil
import dev.doctorm4id.m4id.util.M4idPool
import dev.doctorm4id.rot.content.ModContent
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.SculkChargeParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties


object InfestationSystem {

	private val randomCyst = M4idPool<Block>().apply {
		addEntry(ModContent.ROTTED_BLOCK, 200)
		addEntry(ModContent.BLOOMING_CYST_BLOCK, 10)
	}

	private val randomFlora = M4idPool<Block>().apply {
		addEntry(Blocks.AIR, 70)
		addEntry(ModContent.ROTTED_GRASS, 10)
	}

	fun infestPosition(level: ServerLevel, pos: BlockPos) {
		level.playSound(
			null, pos,
			SoundEvents.SCULK_BLOCK_SPREAD,
			SoundSource.BLOCKS,
			2.0f, 0.4f + level.random.nextFloat() * 0.8f
		)

		level.sendParticles(
			SculkChargeParticleOptions(5.0f),
			pos.x + 0.5, pos.y + 0.5, pos.z + 0.5,
			4, 0.1, 0.1, 0.1, 0.2
		)

		placeFlora(level, pos)
		infestBlock(level, pos)
	}

	private fun infestBlock(level: ServerLevel, pos: BlockPos) {
		val cyst = randomCyst.getRandomEntry() ?: return
		val blockState = level.getBlockState(pos)

		val newState = when (level.getBlockState(pos).block) {
			is StairBlock -> ModContent.ROTTED_STAIR.defaultBlockState()
				.setValue(StairBlock.FACING, blockState.getValue(StairBlock.FACING))
				.setValue(StairBlock.HALF, blockState.getValue(StairBlock.HALF))
				.setValue(StairBlock.SHAPE, blockState.getValue(StairBlock.SHAPE))
				.setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED))

			is SlabBlock -> ModContent.ROTTED_SLAB.defaultBlockState()
				.setValue(SlabBlock.TYPE, blockState.getValue(SlabBlock.TYPE))
				.setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED))

			is LeavesBlock -> ModContent.ROTTED_LEAVES.defaultBlockState()
				.setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED))
				.setValue(LeavesBlock.PERSISTENT, true)

			is FenceBlock -> ModContent.ROTTED_FENCE.defaultBlockState()
				.setValue(BlockStateProperties.EAST, blockState.getValue(BlockStateProperties.EAST))
				.setValue(BlockStateProperties.SOUTH, blockState.getValue(BlockStateProperties.SOUTH))
				.setValue(BlockStateProperties.NORTH, blockState.getValue(BlockStateProperties.NORTH))
				.setValue(BlockStateProperties.WEST, blockState.getValue(BlockStateProperties.WEST))
				.setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED))

			is WallBlock -> ModContent.ROTTED_WALL.defaultBlockState()
				.setValue(BlockStateProperties.EAST_WALL, blockState.getValue(BlockStateProperties.EAST_WALL))
				.setValue(BlockStateProperties.WEST_WALL, blockState.getValue(BlockStateProperties.WEST_WALL))
				.setValue(BlockStateProperties.NORTH_WALL, blockState.getValue(BlockStateProperties.NORTH_WALL))
				.setValue(BlockStateProperties.SOUTH_WALL, blockState.getValue(BlockStateProperties.SOUTH_WALL))
				.setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED))

			else -> {
				if (blockState.canOcclude()) cyst.defaultBlockState() else return
			}
		}

		level.setBlockAndUpdate(pos, newState)
	}

	private fun placeFlora(level: ServerLevel, pos: BlockPos) {
		val flora = randomFlora.getRandomEntry() ?: return
		val offsetPos = pos.above()
		val blockAbove = level.getBlockState(offsetPos)

		if (level.getBlockState(offsetPos).block.defaultBlockState().isAir && !flora.defaultBlockState().isAir && M4idBlockUtil.isSolid(pos, level)) {
			level.setBlockAndUpdate(offsetPos, flora.defaultBlockState())
		} else if (flora.defaultBlockState().isAir && blockAbove.`is`(BlockTags.FLOWERS) || blockAbove.block is BushBlock || blockAbove.`is`(BlockTags.CROPS)) {
			level.destroyBlock(offsetPos, false)
		}
	}



	// Usefull for later stuff.

	fun placeVeinAroundBlock(level: ServerLevel, pos: BlockPos) {
		for (neighbor: BlockPos? in M4idBlockUtil.getNeighborsCube(pos, true)) {
			placeVeinAtBlock(level, neighbor!!)
		}
	}

	private fun placeVeinAtBlock(level: ServerLevel, pos: BlockPos) {
		var validPlacement = false;
		val blockState = level.getBlockState(pos)

		if (!M4idBlockUtil.isAir(blockState)) return

		val vein = Blocks.SCULK_VEIN
		val north = level.getBlockState(pos.north())
		val south = level.getBlockState(pos.south())
		val east = level.getBlockState(pos.east())
		val west = level.getBlockState(pos.west())
		val up = level.getBlockState(pos.above())
		val down = level.getBlockState(pos.below())

		var newBlockState = vein.defaultBlockState()

		if (north.isFaceSturdy(level, pos, Direction.SOUTH)) {
			validPlacement = true
			val property = MultifaceBlock.getFaceProperty(Direction.NORTH)
			newBlockState = newBlockState.setValue(property, true)
		}
		if (east.isFaceSturdy(level, pos, Direction.WEST)) {
			validPlacement = true
			val property = MultifaceBlock.getFaceProperty(Direction.EAST)
			newBlockState = newBlockState.setValue(property, true)
		}
		if (south.isFaceSturdy(level, pos, Direction.NORTH)) {
			validPlacement = true
			val property = MultifaceBlock.getFaceProperty(Direction.SOUTH)
			newBlockState = newBlockState.setValue(property, true)
		}
		if (west.isFaceSturdy(level, pos, Direction.EAST)) {
			validPlacement = true
			val property = MultifaceBlock.getFaceProperty(Direction.WEST)
			newBlockState = newBlockState.setValue(property, true)
		}
		if (up.isFaceSturdy(level, pos, Direction.DOWN)) {
			validPlacement = true
			val property = MultifaceBlock.getFaceProperty(Direction.UP)
			newBlockState = newBlockState.setValue(property, true)
		}
		if (down.isFaceSturdy(level, pos, Direction.UP)) {
			validPlacement = true
			val property = MultifaceBlock.getFaceProperty(Direction.DOWN)
			newBlockState = newBlockState.setValue(property, true)
		}
		if (validPlacement) {
			level.setBlockAndUpdate(pos, newBlockState)
		}
	}
}
