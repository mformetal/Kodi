import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import mformetal.kodi.core.api.ScopeRegistry
import mformetal.kodi.core.api.injection.InjectionRegistry
import mformetal.kodi.core.api.injection.KodiInjector

/**
 * Created using mbpeele on 10/8/17.
 */
abstract class KodiActivity : AppCompatActivity() {

    val injector : InjectionRegistry = KodiInjector()
    lateinit var scopeRegistry: ScopeRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        val rootKodi = (application as KodiApp).kodi
        scopeRegistry = installModule(rootKodi)
        injector.inject(rootKodi, scopeRegistry.scope)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()

        scopeRegistry.unregister()
    }

    abstract fun installModule(kodi: Kodi) : ScopeRegistry
}