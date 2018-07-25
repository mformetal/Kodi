package mformetal.kodi.core.internal

import mformetal.kodi.core.api.builder.KodiBuilder
import mformetal.kodi.core.api.KodiKey
import mformetal.kodi.core.internal.AmbiguousBindingException
import mformetal.kodi.core.internal.DuplicateBindingException
import mformetal.kodi.core.provider.Provider
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

    override fun <T : Any> bind(tag: String, type: KClass<T>, generics: Array<Type>) : BindingBuilder<T> {
        val key = KodiKey(type, generics, tag)
        if (providers.keys.contains(key) && tag.isEmpty()) {
            throw AmbiguousBindingException()
        } else if (providers.keys.contains(key)) {
            throw DuplicateBindingException()
        }

        return BindingBuilder(key)
    }

    override fun <T : Any> BindingBuilder<T>.using(provider: Provider<T>) {
        providers.put(kodiKey, provider)
    }

    override fun <T: Any> get(tag: String, type: KClass<T>, generics: Array<Type>): T {
        val key = KodiKey(type, generics, tag)
        @Suppress("UNCHECKED_CAST")
        return providers[key]!!() as T
    }
}

internal fun module(block: Module.() -> Unit) = Module().apply(block)

class BindingBuilder<T : Any>(val kodiKey: KodiKey<T>)