package mformetal.kodi

import mformetal.kodi.core.api.Scope
import mformetal.kodi.core.api.ScopeModule
import mformetal.kodi.core.api.builder.KodiBuilder
import mformetal.kodi.core.api.builder.bind
import mformetal.kodi.core.api.providerBuilder
import mformetal.kodi.core.api.scoped
import mformetal.kodi.core.provider.component
import org.junit.Test
import assertk.assert
import Kodi
import mformetal.kodi.internal.Module

class InstallKodiScopeTest {

    val testScope = scoped<InstallKodiScopeTest>()

    @Test
    fun testInstallingScopeModule() {
        val kodi = Kodi.init { }
        kodi.installScope(SomeModule())

        val instance = kodi.instance<String>(testScope)
        assert(instance).isEqualTo("anything")
    }

    @Test
    fun testInstallingModuleAndUsingBuilderResultInSameState() {
        val builderKodi = Kodi.init {  }
        builderKodi.scopeBuilder()
                .build(testScope, {
                    bind<String>() using component("anything")
                })

        val moduleKodi = Kodi.init {  }
        moduleKodi.installScope(SomeModule())

        takeBoth(builderKodi, moduleKodi, { first, second ->
            assert(first.instance<String>(testScope)).isEqualTo(second.instance(testScope))
            assert(first.root.children.size).isEqualTo(second.root.children.size)

            takeBoth(first.root.children.first(), second.root.children.first(), { node1, node2 ->
                assert(node1.scope).isEqualTo(node2.scope)
                assert(node1.parent!!.scope).isEqualTo(node2.parent!!.scope)
                kotlin.assert(node1.children.size == 0)
                assert(node1.children.size).isEqualTo(node2.children.size)

                takeBoth(node1.module, node2.module, { module1, module2 ->
                    assert(module1.providers.size).isEqualTo(module2.providers.size)
                    assert(module1.providers.keys).isEqualTo(module2.providers.keys)
                    assert(module1.values()).isEqualTo(module2.values())
                })
            })
        })
    }

    private fun Module.values() : List<*> {
        return providers.values.map { it.provide() }
    }

    private fun <A, B> takeBoth(a: A, b: B, doubleLet: (A, B) -> Unit) {
        doubleLet.invoke(a, b)
    }
}

class SomeModule : ScopeModule {

    override fun with(): Scope = scoped<InstallKodiScopeTest>()

    override fun providers(): KodiBuilder.() -> Unit {
        return providerBuilder {
            bind<String>() using component("anything")
        }
    }
}