package mformetal.kodi.core.api.injection

import mformetal.kodi.core.Kodi
import mformetal.kodi.core.api.KodiKey
import mformetal.kodi.core.api.Scope
import mformetal.kodi.core.internal.InjectNotCalledException
import java.lang.reflect.Type
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Created by peelemil on 10/11/17.
 */
class KodiInjector : InjectionRegistry {

    private val injectionProperties: MutableSet<InjectionProperty<*>> = mutableSetOf()

    override fun <T : Any> register(tag: String, type: KClass<T>, generics: Array<Type>): ReadOnlyProperty<Any, T> {
        val key = KodiKey(type, generics, tag)
        val injection = InjectionProperty(key)
        injectionProperties.add(injection)
        return injection
    }

    override fun inject(kodi: Kodi, scope: Scope) {
        injectionProperties.forEach { it.provide(kodi, scope) }
        injectionProperties.clear()
    }

    private class InjectionProperty<T : Any>(private val kodiKey: KodiKey<T>) : ReadOnlyProperty<Any, T> {

        var value : T ?= null

        override fun getValue(thisRef: Any, property: KProperty<*>): T = value ?: throw InjectNotCalledException()

        fun provide(kodi: Kodi, scope: Scope) {
            value = kodi.instance(scope, kodiKey)
        }
    }
}