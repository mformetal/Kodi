package mformetal.kodi.core.api.injection

import mformetal.kodi.core.Kodi
import mformetal.kodi.core.api.Scope
import mformetal.kodi.core.api.generics
import java.lang.reflect.Type
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

/**
 * Created by mbpeele on 10/11/17.
 */
interface InjectionRegistry {

    fun <T : Any> register(tag: String = "", type: KClass<T>, generics: Array<Type>): ReadOnlyProperty<Any, T>

    fun inject(kodi: Kodi, scope: Scope)
}

inline fun <reified T : Any> InjectionRegistry.register(tag: String = "") = register(tag, T::class, generics<T>())