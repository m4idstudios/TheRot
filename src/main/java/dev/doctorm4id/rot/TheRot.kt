package dev.doctorm4id.rot

import dev.doctorm4id.rot.platform.Platform
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

//? fabric {
import dev.doctorm4id.rot.platform.fabric.FabricPlatform
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
//? }

//? neoforge {
/*import dev.doctorm4id.rot.platform.neoforge.NeoforgePlatform
*///? }

@SuppressWarnings("LoggingSimilarMessage")
class TheRot {

	companion object {
		const val MOD_ID: String = /*$ mod_id*/"rot";
		const val MOD_VERSION: String =  /*$ mod_version*/"0.1.0";
		const val MOD_FRIENDLY_NAME: String =  /*$ mod_name*/"The Rot";

		@JvmField
		val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

		private val PLATFORM: Platform = TheRot().createPlatformInstance()
	}

	fun onInitialize() {
		LOGGER.info( "Initializing {} on {}", MOD_ID, xplat().loader() )
		LOGGER.debug( "{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME )
	}

	fun onInitializeClient() {
		LOGGER.info( "Initializing {} Client on {}", MOD_ID, xplat().loader() )
		LOGGER.debug( "{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME )
	}

	fun xplat(): Platform { return PLATFORM }

	private fun createPlatformInstance(): Platform {
		//? fabric {
		return FabricPlatform()
		//?} neoforge {
		/*return NeoforgePlatform()
		*///?}
	}

	fun id(path: String?): ResourceLocation {
		//? > 1.20.1 {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path!!)
		//?} <= 1.20.1 {
		/*return ResourceLocation(MOD_ID, path!!)
		*///?}
	}

	fun id(namespace: String?, path: String?): ResourceLocation {
		//? > 1.20.1 {
		return ResourceLocation.fromNamespaceAndPath(namespace!!, path!!)
		//?} <= 1.20.1 {
		/*return ResourceLocation(namespace!!, path!!);
		*///?}
	}
}
