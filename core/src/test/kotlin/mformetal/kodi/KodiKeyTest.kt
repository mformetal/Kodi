package mformetal.kodi

import mformetal.kodi.core.api.toKey
import org.junit.Test
import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import mformetal.kodi.core.api.generics
import sun.reflect.generics.reflectiveObjects.WildcardTypeImpl

/**
 * @author - mbpeele on 10/13/17.
 */
class KodiKeyTest {

    @Test
    fun testBasicKeyConstruction() {
        val key = toKey<String>()
        assert(key.kclass).isEqualTo(String::class)
    }

    @Test
    fun testImplementingClassKeyIsNotEqualToInterfaceKey() {
        val implKey = toKey<StringApi>()
        val interfaceKey = toKey<TypedApi<String>>()
        assert(implKey).isNotEqualTo(interfaceKey)
    }

    @Test
    fun testKeyRetrievesCorrectGenerics() {
        val definitions = generics<TypedApi<String>>()

        val upperTypeBounds = (definitions[0] as WildcardTypeImpl).upperBounds[0]

        assert(upperTypeBounds).isEqualTo(String::class.java)
    }

    @Test
    fun testSameTypeForKodiKeyIsEqual() {
        val first = toKey<TypedApi<String>>()
        val second = toKey<TypedApi<String>>()
        assert(first).isEqualTo(second)
    }

    @Test
    fun testSubclassKeyIsNotEqualToSuperClassKey() {
        val first = toKey<Subclass>()
        val second = toKey<Superclass>()
        assert(first).isNotEqualTo(second)
    }

    class StringApi : TypedApi<String> {

    }
    class IntApi : TypedApi<Int> {

    }
    interface TypedApi<out T>
    interface FurtherTypedApi<V, T> : TypedApi<T>
    open class Superclass
    class Subclass : Superclass()
}