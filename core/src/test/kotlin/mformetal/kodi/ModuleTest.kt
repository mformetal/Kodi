package mformetal.kodi

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import mformetal.kodi.core.api.builder.provide
import mformetal.kodi.core.api.builder.singleton
import mformetal.kodi.core.api.builder.get
import mformetal.kodi.core.internal.module
import org.junit.Test
import java.util.*
import kotlin.test.assertFailsWith

/**
 * @author - mbpeele on 10/7/17.
 */
class ModuleTest {

    @Test
    fun testRetrievingDependency() {
        val module = module {
            provide { "bro" }
        }

        val dependency = module.get<String>()
        assert(dependency).isEqualTo("bro")
    }

    @Test(expected = IllegalStateException::class)
    fun testBindingSameDependencyClass() {
        module {
            provide { Thing() }
            provide { Thing() }
        }
    }

    @Test
    fun testRetrievingNewInstanceProvider() {
        val module = module {
            provide { Thing() }
        }

        val first = module.get<Thing>()
        val second = module.get<Thing>()
        assert(first).isNotEqualTo(second)
    }

    @Test
    fun testRetrievingusingSingletonProvider() {
        val module = module {
            singleton { Thing(string = UUID.randomUUID().toString()) }
        }

        val first = module.get<Thing>()
        val second = module.get<Thing>()
        assert(first).isEqualTo(second)
    }

    @Test
    fun testAddingChildModules() {
        val root = module {
            child {
                provide { 5 }
            }
        }

        val dependency = root.get<Int>()
        assert(dependency).isEqualTo(5)
    }

    @Test
    fun testFactoryCreation() {
        val module = module {
            provide { "bro" }
            provide { 5 }
            provide { Thing(get(), get()) }
        }

        with (module.get<Thing>()) {
            assert(string).isEqualTo("bro")
            assert(int).isEqualTo(5)
        }
    }

    @Test(expected = NullPointerException::class)
    fun testFactoryCreationWithoutNecessaryDependencies() {
        val module = module {
            provide { "bro" }
            provide { Thing(get(), get()) }
        }

        module.get<Thing>()
    }

    @Test
    fun testFactoryCreationWithTags() {
        val module = module {
            provide("first") { "bro" }
            provide { 5 }
            provide { Thing(get("first"), get()) }
        }

        with (module.get<Thing>()) {
            assert(string).isEqualTo("bro")
            assert(int).isEqualTo(5)
        }
    }

    @Test
    fun testRetrievingInterface() {
        val module = module {
            provide<Api> { ThingImpl() }
        }

        with (module.get<Api>()) {
            assert(this).isNotNull()
            assert(this).isInstanceOf(ThingImpl::class)
        }
    }

    @Test
    fun testRetrievingImplementationOfInterfaceButTypedAsInterface() {
        assertFailsWith(java.lang.NullPointerException::class) {
            val module = module {
                provide<Api> { ThingImpl() }
            }

            module.get<ThingImpl>()
        }
    }

    @Test
    fun testRetrievingSuperClass() {
        val module = module {
            provide<SuperThing> { SmallThing() }
        }

        val superThing = module.get<SuperThing>()
        assert(superThing).isInstanceOf(SmallThing::class)
    }

    @Test
    fun testRetrievingSubClassButTypedAsSuperClass() {
        assertFailsWith(java.lang.NullPointerException::class) {
            val module = module {
                provide<SuperThing> { SmallThing() }
            }

            module.get<SmallThing>()
        }
    }

    @Test
    fun testRetrievingTypedInterface() {
        val module = module {
            provide<TypedApi<String>> {
                object : TypedApi<String> {
                    override fun get(): String = "THIS"
                }
            }
            provide<TypedApi<Int>> {
                object : TypedApi<Int> {
                    override fun get(): Int = 50
                }
            }
        }

        val stringApi = module.get<TypedApi<String>>()
        val intApi = module.get<TypedApi<Int>>()
        assert(stringApi.get()).isEqualTo("THIS")
        assert(intApi.get()).isEqualTo(50)
    }

    class Thing(val string: String = "", val int: Int = 0)
    interface Api
    class ThingImpl : Api
    open class SuperThing
    class SmallThing : SuperThing()
    interface TypedApi<out T> {
        fun get() : T
    }
}