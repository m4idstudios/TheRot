package dev.doctorm4id.rot

import dev.doctorm4id.m4id.data.DataFile
import java.io.File

object TheRotConfig {
	private val file = DataFile.forServer(File("config/therot.json"))

	var meow by file.of<String> { "cat" }

	fun init() {
		file.save()
	}
}
