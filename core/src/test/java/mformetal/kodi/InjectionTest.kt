package mformetal.kodi

import assertk.assert
import assertk.assertions.isEqualTo
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import mformetal.kodi.core.Kodi
import mformetal.kodi.core.api.builder.bind
import mformetal.kodi.core.api.injection.InjectionRegistry
import mformetal.kodi.core.api.injection.KodiInjector
import mformetal.kodi.core.api.injection.register
import mformetal.kodi.core.api.scoped
import mformetal.kodi.core.internal.InjectNotCalledException
import mformetal.kodi.core.provider.component
import mformetal.kodi.core.provider.provider
import org.junit.Test

/**
 * @author - mbpeele on 10/11/17.
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

    @Test
    fun testLambdaInvokedOnInjection() {
        val kodi = Kodi.init {  }

        val injection = OnInjection()
        injection.onCreate(kodi)

        verify(injection.testMethodInterface).testMethod()
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

    class OnInjection {

        private val injector: InjectionRegistry = KodiInjector()

        val testMethodInterface: TestMethodInterface by injector.register { it.testMethod() }

        fun onCreate(kodi: Kodi) {
            val registry = kodi.scopeBuilder {
                build(scoped<ModuleInjection>()) {
                    bind<TestMethodInterface>() using component(mock())
                }
            }

            injector.inject(kodi, registry.scope)
        }
    }

    interface TestMethodInterface {

        fun testMethod()
    }
}