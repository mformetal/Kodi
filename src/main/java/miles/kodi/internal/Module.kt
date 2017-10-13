package miles.kodi.internal

import miles.kodi.api.builder.KodiBuilder
import miles.kodi.api.KodiKey
import miles.kodi.provider.Provider
import kotlin.reflect.KClass

/**
 * Created from mbpeele on 10/7/17.
 */
internal class Module : KodiBuilder {

    @Suppress("MemberVisibilityCanPrivate")
    internal val providers: HashMap<KodiKey, Provider<*>> = HashMap()

    override fun child(builder: KodiBuilder.() -> Unit) {
        val module = Module().apply(builder)
        providers.putAll(module.providers)
    }

    override fun <T : Any> bind(tag: String, type: KClass<T>) : BindingBuilder<T> {
        val key = KodiKey(type, tag)
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

    override fun <T: Any> get(tag: String, type: KClass<T>): T {
        val key = KodiKey(type, tag)
        @Suppress("UNCHECKED_CAST")
        return providers[key]!!.provide() as T
    }
}

internal fun module(block: Module.() -> Unit) = Module().apply(block)

class BindingBuilder<T : Any>(val kodiKey: KodiKey)