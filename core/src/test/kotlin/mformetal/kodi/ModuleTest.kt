package mformetal.kodi

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import mformetal.kodi.core.api.builder.bind
import mformetal.kodi.core.api.builder.get
import mformetal.kodi.core.internal.module
import mformetal.kodi.core.provider.provider
import mformetal.kodi.core.provider.singleton
import org.junit.Test
import java.util.*

/**
 * @author - mbpeele on 10/7/17.
 */
class ModuleTest {

    @Test
    fun testRetrievingDependency() {
        val module = module {
            bind<String>() using provider { "bro" }
        }

        val dependency = module.get<String>()
        assert(dependency).isEqualTo("bro")
    }

    @Test(expected = IllegalStateException::class)
    fun testBindingSameDependencyClass() {
        module {
            bind<Thing>() using provider { Thing() }
            bind<Thing>() using provider { Thing() }
        }
    }

    @Test
    fun testRetrievingNewInstanceProvider() {
        val module = module {
            bind<Thing>() using provider { Thing() }
        }

        val first = module.get<Thing>()
        val second = module.get<Thing>()
        assert(first).isNotEqualTo(second)
    }

    @Test
    fun testRetrievingusingSingletonProvider() {
        val module = module {
            bind<Thing>() using singleton { Thing(string = UUID.randomUUID().toString()) }
        }

        val first = module.get<Thing>()
        val second = module.get<Thing>()
        assert(first).isEqualTo(second)
    }

    @Test
    fun testAddingChildModules() {
        val root = module {
            child {
                bind<Int>() using provider { 5 }
            }
        }

        val dependency = root.get<Int>()
        assert(dependency).isEqualTo(5)
    }

    @Test
    fun testFactoryCreation() {
        val module = module {
            bind<String>() using provider { "bro" }
            bind<Int>() using provider { 5 }
            bind<Thing>() using provider { Thing(get(), get()) }
        }

        with (module.get<Thing>()) {
            assert(string).isEqualTo("bro")
            assert(int).isEqualTo(5)
        }
    }

    @Test(expected = NullPointerException::class)
    fun testFactoryCreationWithoutNecessaryDependencies() {
        val module = module {
            bind<String>() using provider { "bro" }
            bind<Thing>() using provider { Thing(get(), get()) }
        }

        module.get<Thing>()
    }

    @Test
    fun testFactoryCreationWithTags() {
        val module = module {
            bind<String>("first") using provider { "bro" }
            bind<Int>() using provider { 5 }
            bind<Thing>() using provider { Thing(get("first"), get()) }
        }

        with (module.get<Thing>()) {
            assert(string).isEqualTo("bro")
            assert(int).isEqualTo(5)
        }
    }

    @Test
    fun testRetrievingInterface() {
        val module = module {
            bind<Api>() using provider { ThingImpl() }
        }

        with (module.get<Api>()) {
            assert(this).isNotNull()
            assert(this).isInstanceOf(ThingImpl::class)
        }
    }

    @Test(expected = NullPointerException::class)
    fun testRetrievingImplementationOfInterfaceButTypedAsInterface() {
        val module = module {
            bind<Api>() using provider { ThingImpl() }
        }

        module.get<ThingImpl>()
    }

    @Test
    fun testRetrievingSuperClass() {
        val module = module {
            bind<SuperThing>() using provider { SmallThing() }
        }

        val superThing = module.get<SuperThing>()
        assert(superThing).isInstanceOf(SmallThing::class)
    }

    @Test(expected = NullPointerException::class)
    fun testRetrievingSubClassButTypedAsSuperClass() {
        val module = module {
            bind<SuperThing>() using provider { SmallThing() }
        }

        module.get<SmallThing>()
    }

    @Test
    fun testRetrievingTypedInterface() {
        val module = module {
            bind<TypedApi<String>>() using provider {
                object : TypedApi<String> {
                    override fun get(): String = "THIS"
                }
            }
            bind<TypedApi<Int>>() using provider {
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