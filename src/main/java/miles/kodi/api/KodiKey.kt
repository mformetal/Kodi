package miles.kodi.api

import kotlin.reflect.KClass

data class KodiKey(private val kclass: KClass<*>, private val tag: String = "")

inline fun <reified T : Any> toKey(tag: String = "") = KodiKey(T::class, tag)