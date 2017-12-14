import android.content.Context
import android.support.v4.app.Fragment
import mformetal.kodi.core.api.ScopeRegistry
import mformetal.kodi.core.api.injection.InjectionRegistry
import mformetal.kodi.core.api.injection.KodiInjector

/**
 * @author - mbpeele on 12/14/17.
 */
abstract class KodiFragment : Fragment() {

    val injector : InjectionRegistry = KodiInjector()
    lateinit var scopeRegistry: ScopeRegistry

    override fun onAttach(context: Context) {
        val rootKodi = (context.applicationContext as KodiApp).kodi
        scopeRegistry = installModule(rootKodi)
        injector.inject(rootKodi, scopeRegistry.scope)
        super.onAttach(context)
    }

    override fun onDestroy() {
        super.onDestroy()

        scopeRegistry.unregister()
    }

    abstract fun installModule(kodi: Kodi) : ScopeRegistry
}