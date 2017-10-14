package miles.kodi.api.builder

import miles.kodi.api.Scope

/**
 * Created by peelemil on 10/11/17.
 */
interface ScopeBuilder {

    fun dependsOn(scope: Scope) : ScopeBuilder

    fun build(scope: Scope, block: KodiBuilder.() -> Unit)

}