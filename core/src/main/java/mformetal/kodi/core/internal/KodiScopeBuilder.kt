package mformetal.kodi.internal

import mformetal.kodi.core.Kodi
import mformetal.kodi.core.api.Scope
import mformetal.kodi.core.api.builder.KodiBuilder
import mformetal.kodi.core.api.builder.ScopeBuilder

internal class KodiScopeBuilder(private val kodi: Kodi) : ScopeBuilder {

    var parentNode: Node = kodi.root
    lateinit var childNode : Node

    override fun dependsOn(scope: Scope) : ScopeBuilder {
        parentNode = kodi.root.search { it.scope == scope } ?: throw NoMatchingScopeException(scope)
        return this
    }

    override fun build(scope: Scope, block: KodiBuilder.() -> Unit) {
        val module = Module()
        childNode = Node(module, scope)
        parentNode.addChild(childNode)
        KodiModule(childNode, module).apply(block)
    }
}