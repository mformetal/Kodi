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
 * @author - mbpeele on 10/11/17.
 */
class KodiInjector : InjectionRegistry {

    private val injectionProperties = mutableSetOf<InjectionProperty<*>>()

    override fun <T : Any> register(tag: String, type: KClass<T>, generics: Array<Type>,
                                    onInject: ((T) -> Unit)): ReadOnlyProperty<Any, T> {
        return KodiKey(type, generics, tag)
                .run { InjectionProperty(this, onInject) }
                .also { injectionProperty -> injectionProperties.add(injectionProperty) }
    }

    override fun inject(kodi: Kodi, scope: Scope) {
        with (injectionProperties) {
            forEach { it.provide(kodi, scope) }
            clear()
        }
    }

    private class InjectionProperty<T : Any>(private val kodiKey: KodiKey<T>,
                                             private val onInject: ((T) -> Unit)) : ReadOnlyProperty<Any, T> {

        var value : T ?= null

        override fun getValue(thisRef: Any, property: KProperty<*>): T = value ?: throw InjectNotCalledException()

        fun provide(kodi: Kodi, scope: Scope) {
            value = kodi.instance(scope, kodiKey)
            onInject.invoke(value!!)
        }
    }
}