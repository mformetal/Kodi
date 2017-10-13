package miles.kodi

import miles.kodi.api.toKey
import org.junit.Test
import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import miles.kodi.api.generics

/**
 * Created by peelemil on 10/13/17.
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
        assert { definitions.contains(String::class.java.genericSuperclass) }
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