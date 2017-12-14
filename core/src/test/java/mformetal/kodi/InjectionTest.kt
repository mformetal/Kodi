package mformetal.kodi

import mformetal.kodi.core.api.builder.bind
import mformetal.kodi.core.api.injection.KodiInjector
import mformetal.kodi.core.api.injection.register
import mformetal.kodi.core.api.scoped
import org.junit.Test
import assertk.assert
import assertk.assertions.isEqualTo
import Kodi
import mformetal.kodi.core.api.injection.InjectionRegistry
import mformetal.kodi.internal.InjectNotCalledException
import mformetal.kodi.provider.provider

/**
 * Created by peelemil on 10/11/17.
 */
class InjectionTest {

    @Test
    fun testBaseInjection() {
        val kodi = Kodi.init {  }

        kodi.scopeBuilder {
            build(scoped<SimpleInjection>()) {
                bind<String>() using provider { "BRO" }
            }
        }

        val injectable = SimpleInjection()

        injectable.inject(kodi)

        assert(injectable.string).isEqualTo("BRO")
    }

    @Test(expected = InjectNotCalledException::class)
    fun testInjectionWithoutCallingInject() {
        val kodi = Kodi.init {  }

        kodi.scopeBuilder {
            build(scoped<SimpleInjection>()) {
                bind<String>() using provider { "BRO" }
            }
        }

        SimpleInjection().string
    }

    @Test
    fun testModuleInjection() {
        val kodi = Kodi.init {  }

        val injection = ModuleInjection()
        injection.onCreate(kodi)

        assert(injection.string).isEqualTo("DUDE")
    }

    class SimpleInjection {

        private val injector : InjectionRegistry = KodiInjector()

        val string: String by injector.register()

        fun inject(kodi: Kodi) = injector.inject(kodi, scoped<SimpleInjection>())
    }

    class ModuleInjection {

        private val injector : InjectionRegistry = KodiInjector()

        val string: String by injector.register()

        fun onCreate(kodi: Kodi) {
            val registry = kodi.scopeBuilder {
                build(scoped<ModuleInjection>()) {
                    bind<String>() using provider { "DUDE" }
                }
            }

            injector.inject(kodi, registry.scope)
        }
    }
}