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

		removeVeinsAttachedTo(level, pos)
		level.setBlockAndUpdate(pos, newState)
		placeVeinAroundBlock(level, pos)
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

	private fun removeVeinsAttachedTo(level: ServerLevel, pos: BlockPos) {
		for (direction in Direction.entries) {
			val veinPos = pos.relative(direction)
			val veinState = level.getBlockState(veinPos)

			if (!veinState.`is`(ModContent.ROTTED_VEIN) && !veinState.`is`(Blocks.SCULK_VEIN)) continue

			val face = MultifaceBlock.getFaceProperty(direction.opposite)

			if (!veinState.getValue(face)) continue

			val newState = veinState.setValue(face, false)

			if (Direction.entries.any { newState.getValue(MultifaceBlock.getFaceProperty(it)) }) {
				level.setBlock(veinPos, newState, Block.UPDATE_ALL)
			} else {
				level.setBlock(veinPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL)
			}
		}
	}

	fun placeVeinAroundBlock(level: ServerLevel, pos: BlockPos) {
		for (direction in Direction.entries) {
			val adjacentPos = pos.relative(direction)
			val adjacentState = level.getBlockState(adjacentPos)

			if (M4idBlockUtil.isAir(adjacentState)) continue
			if (adjacentState.`is`(ModContent.BlockTags.ROT_FAMILY)) continue

			placeVeinsOnBlock(level, adjacentPos, direction)
		}
	}

	private fun placeVeinsOnBlock(level: ServerLevel, blockPos: BlockPos, sourceDirection: Direction) {
		for (face in Direction.entries) {
			if (face == sourceDirection) continue

			val supportState = level.getBlockState(blockPos)
			if (!supportState.isFaceSturdy(level, blockPos, face)) continue

			val veinPos = blockPos.relative(face)
			val veinState = level.getBlockState(veinPos)

			if (!M4idBlockUtil.isAir(veinState) && !veinState.`is`(ModContent.ROTTED_VEIN)) continue

			val property = MultifaceBlock.getFaceProperty(face.opposite)

			val newState = if (veinState.`is`(ModContent.ROTTED_VEIN)) {
				veinState.setValue(property, true)
			} else {
				ModContent.ROTTED_VEIN.defaultBlockState().setValue(property, true)
			}

			level.setBlockAndUpdate(veinPos, newState)
		}
	}
}
