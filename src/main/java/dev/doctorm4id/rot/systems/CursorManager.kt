package dev.doctorm4id.rot.systems

import dev.doctorm4id.rot.TheRot
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel

object CursorManager {
	private val virtualCursor = VirtualCursorList()
	private var virtualCursorIndex = 0
	private var cleanCooldown = 0

	val list: List<ICursor> get() = virtualCursor.list

	fun createSurfaceInfectorVirtualCursor(level: ServerLevel, pos: BlockPos): VirtualSurfaceInfectorCursor {
		val cursor = VirtualSurfaceInfectorCursor(level)
		cursor.moveTo(pos.x, pos.y, pos.z)
		addVirtualCursor(cursor)
		return cursor
	}

	fun addVirtualCursor(entity: ICursor) {
		virtualCursor.insetCursor(entity)
	}

	fun clear() {
		virtualCursor.clear()
		virtualCursorIndex = 0
		cleanCooldown = 0
	}

	fun tick() {
		if (list.isEmpty()) return

		val maxCursors = 1128

		if (cleanCooldown-- <= 0) {
			virtualCursor.clean()
			cleanCooldown = 20

			TheRot.LOGGER.debug("Cursor Size: $maxCursors")
		}

		val toProcess = list.filter { !it.isExpired() }.take(maxCursors)

		for (list in toProcess) {
			list.tick()
		}

		//TheRot.LOGGER.info("Cursor Size: ${toProcess.size}")
	}
}

class VirtualCursorList {
	var list = ArrayList<ICursor>()
	val size: Int get() = list.size

	fun clear() {
		list.clear()
	}

	fun clean() {
		list.removeIf { it.isExpired() }
	}

	private fun ICursor.longId(): Long {
		val uuid = getID()
		return uuid.leastSignificantBits
	}

	fun insetCursor(entity: ICursor) {
		val targetId = entity.longId()
		var index = list.binarySearch { it.longId().compareTo(targetId) }

		if (index < 0) {
			index = -index - 1
		} else {
			return
		}

		list.add(index, entity)
	}
}
