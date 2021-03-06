package mformetal.kodi.core.api.builder

import mformetal.kodi.core.api.Scope

/**
 * Created by peelemil on 10/11/17.
 */
interface ScopeBuilder {

    fun dependsOn(scope: Scope) : ScopeBuilder

    fun build(scope: Scope, block: KodiBuilder.() -> Unit)

}