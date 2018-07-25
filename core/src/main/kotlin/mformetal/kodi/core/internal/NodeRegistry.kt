package mformetal.kodi.core.internal

import mformetal.kodi.core.api.Scope
import mformetal.kodi.core.api.ScopeRegistry

/**
 * Created using mbpeele on 10/7/17.
 */
internal class NodeRegistry(private val parent: Node,
                            private val child: Node,
                            override val scope : Scope = child.scope) : ScopeRegistry {

    override fun unregister() {
        parent.removeChild(child)
    }
}