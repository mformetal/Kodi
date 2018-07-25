package mformetal.kodi.core.api.builder

import mformetal.kodi.core.api.generics
import mformetal.kodi.core.provider.LazyProvider
import mformetal.kodi.core.provider.Provider
import mformetal.kodi.core.provider.ValueProvider
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * Created using mbpeele on 10/11/17.
 */
interface KodiBuilder {

    fun child(builder: KodiBuilder.() -> Unit)

    fun <T : Any> bind(tag: String = "", type: KClass<T>, generics: Array<Type>, provider: Provider<T>)

    fun <T : Any> get(tag: String = "", type: KClass<T>, generics: Array<Type>) : T
}

inline fun <reified T : Any> KodiBuilder.provide(tag: String = "", noinline block: () -> T)
        = bind(tag, T::class, generics<T>(), block)

inline fun <reified T : Any> KodiBuilder.singleton(tag: String = "", noinline block: () -> T)
        = bind(tag, T::class, generics<T>(), LazyProvider(block))

inline fun <reified T : Any> KodiBuilder.value(tag: String = "", instance: T)
        = bind(tag, T::class, generics<T>(), ValueProvider(instance))

inline fun <reified T : Any> KodiBuilder.get(key: String = "") : T = get(key, T::class, generics<T>())