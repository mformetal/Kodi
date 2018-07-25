package mformetal.kodi.core.api

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*
import kotlin.reflect.KClass

class KodiKey<T : Any>
@PublishedApi
internal constructor(
        internal val kclass: KClass<T>,
        private val parameters: Array<Type>,
        internal val tag: String = "") {

    override fun equals(other: Any?): Boolean {
        if (other !is KodiKey<*>) return false

        return kclass == other.kclass &&
                Arrays.equals(parameters, other.parameters) &&
                tag == other.tag

    }

    override fun hashCode() = kclass.hashCode()
}

inline fun <reified T : Any> toKey(tag: String = "") = KodiKey(T::class, generics<T>(), tag)

@PublishedApi
internal abstract class TypeReference<T> : Comparable<TypeReference<T>> {
    val type: Type
        get() {
            val superClass = javaClass.genericSuperclass
            return if (superClass is ParameterizedType) {
                superClass.actualTypeArguments.first()
            } else {
                superClass
            }
        }

    override fun compareTo(other: TypeReference<T>) = 0
}

@PublishedApi
internal inline fun <reified T : Any> generics() : Array<Type> {
    val type = object : TypeReference<T>() {}.type
    return if (type is ParameterizedType) type.actualTypeArguments else emptyArray()
}