package mformetal.kodi.core.internal

import mformetal.kodi.core.api.KodiKey
import mformetal.kodi.core.api.builder.KodiBuilder
import mformetal.kodi.core.api.generics
import mformetal.kodi.core.provider.LazyProvider
import mformetal.kodi.core.provider.Provider
import mformetal.kodi.core.provider.ValueProvider
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * Created from mbpeele on 10/7/17.
 */
internal class Module : KodiBuilder {

    @Suppress("MemberVisibilityCanPrivate")
    internal val providers: HashMap<KodiKey<*>, Provider<*>> = HashMap()

    override fun child(builder: KodiBuilder.() -> Unit) {
        val module = Module().apply(builder)
        providers.putAll(module.providers)
    }

    override fun <T : Any> bind(tag: String, type: KClass<T>, generics: Array<Type>, provider: Provider<T>) {
        val key = KodiKey(type, generics, tag)
        when {
            providers.keys.contains(key) && tag.isEmpty() -> throw AmbiguousBindingException()
            providers.keys.contains(key) -> throw DuplicateBindingException()
            else -> providers[key] = provider
        }
    }

    override fun <T: Any> get(tag: String, type: KClass<T>, generics: Array<Type>): T {
        val key = KodiKey(type, generics, tag)
        @Suppress("UNCHECKED_CAST")
        return providers[key]!!() as T
    }
}

internal fun module(block: Module.() -> Unit) = Module().apply(block)