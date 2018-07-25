package mformetal.kodi.core.api

import kotlin.reflect.KClass

/**
 * @author - mbpeele on 10/11/17.
 */
data class Scope(private val scopingClass: KClass<*>)

inline fun <reified T> scoped() = Scope(T::class)