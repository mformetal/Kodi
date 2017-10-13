package miles.kodi.internal

import miles.kodi.Kodi
import miles.kodi.api.Scope
import miles.kodi.api.builder.KodiBuilder
import miles.kodi.api.builder.ScopeBuilder

internal class KodiScopeBuilder(private val kodi: Kodi) : ScopeBuilder {

    var parentNode: Node = kodi.root
    lateinit var childNode : Node

    override fun dependsOn(scope: Scope) : ScopeBuilder {
        parentNode = kodi.root.search { it.scope == scope } ?: throw NoMatchingScopeException(scope)
        return this
    }

    override fun build(scope: Scope, block: KodiBuilder.() -> Unit) : ScopeBuilder {
        val module = Module()
        childNode = Node(module, scope)
        parentNode.addChild(childNode)
        KodiModule(childNode, module).apply(block)
        return this
    }
}