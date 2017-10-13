package miles.kodi.api.builder

import miles.kodi.api.generics
import miles.kodi.internal.BindingBuilder
import miles.kodi.provider.Provider
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * Created using peelemil on 10/11/17.
 */
interface KodiBuilder {

    fun child(builder: KodiBuilder.() -> Unit)

    fun <T : Any> bind(tag: String = "", type: KClass<T>, generics: Array<Type>) : BindingBuilder<T>

    infix fun <T : Any> BindingBuilder<T>.using(provider: Provider<T>)

    fun <T : Any> get(tag: String = "", type: KClass<T>, generics: Array<Type>) : T
}

inline fun <reified T : Any> KodiBuilder.bind(tag: String = "") : BindingBuilder<T> = bind(tag, T::class, generics<T>())

inline fun <reified T : Any> KodiBuilder.get(key: String = "") : T = get(key, T::class, generics<T>())