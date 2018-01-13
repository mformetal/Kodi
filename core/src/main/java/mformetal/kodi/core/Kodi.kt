package mformetal.kodi.core

import mformetal.kodi.core.api.*
import mformetal.kodi.core.api.builder.KodiBuilder
import mformetal.kodi.core.api.builder.ScopeBuilder
import mformetal.kodi.core.internal.*


/**
 * Created by mbpeele on 10/11/17.
 */
class Kodi private constructor(internal val root: Node) {

    companion object {
        internal val ROOT_SCOPE = scoped<Kodi>()

        val EMPTY_REGISTRY = object : ScopeRegistry {
            override val scope: Scope
                get() = ROOT_SCOPE

            override fun unregister() {

            }
        }

        fun init(builder: KodiBuilder.() -> Unit) : Kodi {
            val module = Module().apply(builder)
            val rootNode = Node(module, ROOT_SCOPE)
            return Kodi(rootNode)
        }
    }

    fun scopeBuilder(builder: ScopeBuilder.() -> Unit) : ScopeRegistry {
        val scopeBuilder = KodiScopeBuilder(this).apply(builder)
        return NodeRegistry(scopeBuilder.parentNode, scopeBuilder.childNode)
    }

    fun scopeBuilder() : ScopeBuilder = KodiScopeBuilder(this)

    fun installScope(scopeModule: ScopeModule) : ScopeRegistry {
        val scopeBuilder = KodiScopeBuilder(this)
        scopeBuilder.dependsOn(scopeModule.dependsOn())
        scopeBuilder.build(scopeModule.with(), scopeModule.providers())
        return NodeRegistry(scopeBuilder.parentNode, scopeBuilder.childNode)
    }

    fun <T : Any> instance(scope: Scope, kodiKey: KodiKey<T>) : T {
        val child = root.search { it.scope == scope } ?: throw NoMatchingScopeException(scope)
        val result = child.searchUpToRoot { it.module.providers.containsKey(kodiKey) } ?: throw NoMatchingKeyException(kodiKey)
        @Suppress("UNCHECKED_CAST")
        return result.module.providers[kodiKey]!!.provide() as T
    }

    inline fun <reified T : Any> instance(scope: Scope, tag: String = "") : T = instance(scope, toKey(tag))
}