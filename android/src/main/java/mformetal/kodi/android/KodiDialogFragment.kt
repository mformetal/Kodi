package mformetal.kodi.android

import android.os.Bundle
import android.support.v4.app.DialogFragment
import mformetal.kodi.core.Kodi
import mformetal.kodi.core.api.ScopeRegistry
import mformetal.kodi.core.api.injection.InjectionRegistry
import mformetal.kodi.core.api.injection.KodiInjector

abstract class KodiDialogFragment : DialogFragment() {

    val injector : InjectionRegistry = KodiInjector()
    lateinit var scopeRegistry: ScopeRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        val rootKodi = (activity!!.application as KodiApp).kodi
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