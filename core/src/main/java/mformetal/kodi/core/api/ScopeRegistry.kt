package mformetal.kodi.core.api

/**
 * Created from mbpeele on 10/7/17.
 */
interface ScopeRegistry {

    val scope : Scope

    fun unregister()

}