package dev.doctorm4id.rot.systems

import dev.doctorm4id.m4id.util.M4idBlockUtil
import dev.doctorm4id.m4id.util.M4idTickUtil
import dev.doctorm4id.rot.content.ModContent
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import java.util.ArrayDeque
import java.util.UUID
import kotlin.random.Random

open class VirtualCursor(var level: ServerLevel) : ICursor {

	companion object {
		private val NEIGHBOR_OFFSETS: Array<BlockPos> = buildList {
			for (x in -1..1) {
				for (y in -1..1) {
					for (z in -1..1) {
						if (x != 0 || y != 0 || z != 0) {
							add(BlockPos(x, y, z))
						}
					}
				}
			}
		}.toTypedArray()
	}

	enum class State { SEARCHING, EXPLORING, FINISHED }

	private var pos: BlockPos = BlockPos.ZERO
	var origin: BlockPos = BlockPos.ZERO
	private var target: BlockPos? = BlockPos.ZERO

	private var state = State.SEARCHING
	private val id: Long = UUID.randomUUID().leastSignificantBits
	private var expired: Boolean = false

	private val creationTime: Long = getWorld().gameTime
	private val searchQueue: ArrayDeque<BlockPos> = ArrayDeque()
	private val positionsSearched: LongOpenHashSet = LongOpenHashSet()
	val visitedPositions: MutableSet<Long> = HashSet()

	// These gets overridden anyway.
	protected open fun isTarget(level: Level, pos: BlockPos): Boolean = !getWorld().getBlockState(pos).`is`(ModContent.BlockTags.ROT_FAMILY)
	protected open fun changeBlock(pos: BlockPos) { getWorld().setBlock(pos, ModContent.NULL_CYAN.defaultBlockState(), 3) }
	protected open fun isObstructed(state: BlockState, pos: BlockPos): Boolean {
		//if (BlockUtil.getBlockDistanceSquared(origin, pos) > 20 * 20) return true
		//if (StoatBlockUtil.isAir(state)) return true
		if (visitedPositions.contains(pos.asLong())) return true

		return false
	}

	private fun hasExpired(): Boolean {
		return (getWorld().gameTime - creationTime) > M4idTickUtil.convertMinutesToTicks(1)
	}

	override fun tick() {
		if (hasExpired()) {
			setState(State.FINISHED)
			return
		}

		if (origin == BlockPos.ZERO) origin = pos

		when (state) {
			State.SEARCHING -> {if (searchTick()) finishSearch(target)}
			State.EXPLORING -> {exploreTick()}
			State.FINISHED -> {setExpired()}
		}
	}

	private fun startSearch() {
		searchQueue.clear()
		positionsSearched.clear()
		target = null
		searchQueue.add(pos)
		setState(State.SEARCHING)
	}

	private fun searchTick(): Boolean {
		val iterations = 32

		repeat(iterations) {
			if (searchQueue.isEmpty()) return true

			val currentBlock = searchQueue.poll() ?: return@repeat

/*			if (!level.getBlockState(currentBlock).`is`(ModContent.BlockTags.ROT_FAMILY)) {
				level.setBlock(currentBlock, Blocks.WHITE_STAINED_GLASS.defaultBlockState(), 3)
			}*/

			if (currentBlock != pos && isTarget(level, currentBlock)) {
				target = currentBlock

				return true
			}

			val FACKYOU = getWorld().random.nextInt(NEIGHBOR_OFFSETS.size)

			for (i in NEIGHBOR_OFFSETS.indices) {
				val offset = NEIGHBOR_OFFSETS[(FACKYOU + i) % NEIGHBOR_OFFSETS.size]
				val neighbor = currentBlock.offset(offset)
				val longPos = neighbor.asLong()

				if (positionsSearched.add(longPos) && !isObstructed(getWorld().getBlockState(neighbor), neighbor)) {
					searchQueue.add(neighbor)

/*					if (!level.getBlockState(neighbor).`is`(ModContent.BlockTags.ROT_FAMILY)) {
						level.setBlock(neighbor, Blocks.LIGHT_GRAY_STAINED_GLASS.defaultBlockState(), 3)
					}*/
				}
			}
		}

		return false
	}

	private fun finishSearch(foundTarget: BlockPos?) {
		if (foundTarget == null) {
			setState(State.FINISHED)
		} else {
			visitedPositions.clear()
			setState(State.EXPLORING)
		}
	}

	private fun exploreTick() {
		val currentTarget = target

		if (currentTarget == null || currentTarget == BlockPos.ZERO) {
			startSearch()
			return
		}

		var minDistanceSq = Long.MAX_VALUE
		val bestPositions = ArrayList<BlockPos>()

		for (offset in NEIGHBOR_OFFSETS) {
			val neighbor = pos.offset(offset)

			if (isObstructed(getWorld().getBlockState(neighbor), neighbor))
				continue

			val distSq = M4idBlockUtil.getBlockDistanceSquared(
				neighbor,
				currentTarget
			)

			if (distSq < minDistanceSq) {
				minDistanceSq = distSq.toLong()
				bestPositions.clear()
				bestPositions.add(neighbor)
			} else if (distSq.toLong() == minDistanceSq) {
				bestPositions.add(neighbor)
			}

			bestPositions.shuffle(Random)
		}

		if (bestPositions.isEmpty()) {
			startSearch()
			return
		}

		val next = bestPositions[
			getWorld().random.nextInt(bestPositions.size)
		]

		moveTo(next.x, next.y, next.z)

		if (isTarget(level, pos) && !isObstructed(getWorld().getBlockState(pos), pos)) {
			changeBlock(pos)
			startSearch()
		} else {
			visitedPositions.add(next.asLong())
		}
	}

	override fun getID(): UUID = UUID(0L, id)

	override fun getWorld(): Level = level
	override fun getPos(): BlockPos? = pos
	override fun moveTo(x: Int, y: Int, z: Int) { pos = BlockPos(x, y, z) }
	override fun setState(newState: State) { state = newState }

	override fun isExpired(): Boolean = expired
	override fun setExpired() { expired = true }
}
