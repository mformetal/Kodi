package mformetal.kodi

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import mformetal.kodi.core.Kodi
import mformetal.kodi.core.api.builder.*
import mformetal.kodi.core.api.scoped
import mformetal.kodi.core.api.toKey
import org.junit.Test
import java.util.*

/**
 * @author - mbpeele on 10/11/17.
 */
class KodiTest {

    @Test
    fun testInitializingKodi() {
        val kodi = Kodi.init {

        }

        assert(kodi.root.parent == null)
        assert(kodi.root.scope == Kodi.ROOT_SCOPE)
        assert(kodi.root.module.providers.isEmpty())
        assert(kodi.root.children.isEmpty())
    }

    @Test
    fun testTreeStructureWhenScopingOnRoot() {
        val kodi = Kodi.init {

        }

        kodi.scopeBuilder {
            build(scoped<Activity>()) { }
        }

        assert(kodi.root.children.size).isEqualTo(1)
        kodi.root.children.first().run {
            assert(module.providers.size).isEqualTo(0)
            assert(parent).isEqualTo(kodi.root)
            assert(scope).isEqualTo(scoped<Activity>())
        }
    }

    @Test
    fun testRetrievingDependenciesFromRoot() {
        val kodi = Kodi.init {
            provide { DependencyOne() }
            provide { SmallThing(get()) }
        }

        kodi.root.module.providers.run {
            val key = toKey<SmallThing>()
            val provider = get(key)
            val smallThing = provider!!()
            kotlin.assert(smallThing is SmallThing)
        }
    }

    @Test
    fun testChildNodeRetrievingDependencies() {
        val kodi = Kodi.init {
            provide { DependencyOne() }
            provide { SmallThing(get()) }
        }

        kodi.scopeBuilder {
            build(scoped<Activity>()) {
                provide { DependencyTwo() }
                provide { MediumThing(get(), get()) }
            }
        }

        kodi.root.search { it.scope == scoped<Activity>() }!!
                .run {
                    val key = toKey<MediumThing>()
                    val provider = module.providers[key]
                    val mediumThing = provider!!()
                    kotlin.assert(mediumThing is MediumThing)
                }
    }

    @Test(expected = IllegalStateException::class)
    fun testRetrievingDependenciesOfDifferentScope() {
        class First
        class Second

        val kodi = Kodi.init {
            provide { DependencyOne() }
            provide { SmallThing(get()) }
        }

        kodi.scopeBuilder {
            build(scoped<First>()) {
                provide { DependencyTwo() }
            }
        }

        kodi.scopeBuilder {
            build((scoped<Second>())) {
                provide { MediumThing(get(), get()) }
            }
        }

        kodi.root.search { it.scope == scoped<Second>() }!!
                .run {
                    module.providers[toKey<MediumThing>()]!!()
                }
    }

    @Test
    fun testChildNodeRetrievingSingletonDependencies() {
        val kodi = Kodi.init {
            singleton { DependencyOne() }
            provide { SmallThing(get()) }
        }

        kodi.scopeBuilder {
            build(scoped<Activity>()) {
                provide { DependencyTwo() }
                provide { MediumThing(get(), get()) }
            }
        }

        kodi.root.search { it.scope == scoped<Activity>() }!!
                .run {
                    val key = toKey<MediumThing>()
                    val provider = module.providers[key]
                    val mediumThing = provider!!() as MediumThing
                    val mediumThingDependencyOne = mediumThing.dependencyOne
                    val rootDependencyOne = kodi.root.module.providers[toKey<DependencyOne>()]!!() as DependencyOne
                    assert(mediumThingDependencyOne).isEqualTo(rootDependencyOne)
                    assert(mediumThingDependencyOne.id).isEqualTo(rootDependencyOne.id)
                }
    }

    @Test
    fun testUnregisteringRemovesNodeFromTree() {
        val kodi = Kodi.init {
            singleton { DependencyOne() }
            provide { SmallThing(get()) }
        }

        val registry = kodi.scopeBuilder {
            build(scoped<Activity>()) {
                provide { DependencyTwo() }
                provide { MediumThing(get(), get()) }
            }
        }

        registry.unregister()

        val result = kodi.root.search { it.scope == scoped<Activity>() }
        assert(result).isNull()
    }

    @Test
    fun testBindingSameClassInSameScopeInDifferentPlacesWithoutTag() {
        val first = "1"
        val second = "2"

        val kodi = Kodi.init {
            provide { DependencyOne(first) }
        }

        val registry = kodi.scopeBuilder {
            build(scoped<Activity>()) {
                provide { DependencyOne(second) }
            }
        }

        val instance = kodi.instance<DependencyOne>(scoped<Activity>())
        assert(instance.id).isEqualTo(second)
    }

    @Test
    fun testBindingSameClassInSameScopeInDifferentPlacesWithTag() {
        val first = "1"
        val second = "2"

        val kodi = Kodi.init {
            provide("app") {
                DependencyOne(first)
            }
        }

        kodi.scopeBuilder()
                .build(scoped<Activity>()) {
                    provide { DependencyOne(second) }
                }

        val instance = kodi.instance<DependencyOne>(scoped<Activity>(), "app")
        assert(instance.id).isEqualTo(first)
    }

    class Activity

    class DependencyOne(val id: String = UUID.randomUUID().toString())
    class DependencyTwo
    class SmallThing(val dependencyOne: DependencyOne)
    class MediumThing(val dependencyOne: DependencyOne, val dependencyTwo: DependencyTwo)
}