package miles.kodi.api

import miles.kodi.internal.BindingBuilder
import miles.kodi.provider.LazyProvider
import miles.kodi.provider.Provider
import kotlin.reflect.KClass

/**
 * Created using peelemil on 10/11/17.
 */
interface KodiBuilder {

    fun child(builder: KodiBuilder.() -> Unit)

    fun <T : Any> bind(tag: String = "", type: KClass<T>) : BindingBuilder<T>

    infix fun <T> BindingBuilder<T>.using(provider: Provider<T>)

    fun <T : Any> get(tag: String = "", type: KClass<T>) : T
}

inline fun <reified T : Any> KodiBuilder.bind(tag: String = "") : BindingBuilder<T> =
    bind(tag, T::class)

inline fun <reified T : Any> KodiBuilder.get(key: String = "") = get(key, T::class)

inline fun <T> provider(crossinline block: () -> T) =
        object : Provider<T> {
            override fun provide() = block.invoke()
        }

inline fun <T> singleton(crossinline block: () -> T) : Provider<T> {
    val provider = provider(block)
    return LazyProvider(provider)
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> component(instance: T) : Provider<T> = provider { instance }