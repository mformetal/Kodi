package miles.kodi.api.injection

import miles.kodi.Kodi
import miles.kodi.api.Scope
import miles.kodi.api.generics
import java.lang.reflect.Type
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

/**
 * Created by peelemil on 10/11/17.
 */
interface InjectionRegistry {

    fun <T : Any> register(tag: String = "", type: KClass<T>, generics: Array<Type>): ReadOnlyProperty<Any, T>

    fun inject(kodi: Kodi, scope: Scope)
}

inline fun <reified T : Any> InjectionRegistry.register(tag: String = "") = register(tag, T::class, generics<T>())