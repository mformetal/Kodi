package miles.kodi

import miles.kodi.api.*
import miles.kodi.api.builder.KodiBuilder
import miles.kodi.api.builder.ScopeBuilder
import miles.kodi.internal.*

/**
 * Created by peelemil on 10/11/17.
 */
class Kodi private constructor(internal val root: Node) {

    companion object {
        internal val ROOT_SCOPE = scoped<Kodi>()

        fun init(builder: KodiBuilder.() -> Unit) : Kodi {
            val module = Module().apply(builder)
            val rootNode = Node(module, ROOT_SCOPE)
            return Kodi(rootNode)
        }
    }

    fun scope(builder: ScopeBuilder.() -> Unit) : ScopeRegistry {
        val scopeBuilder = KodiScopeBuilder(this).apply(builder)
        return NodeRegistry(scopeBuilder.parentNode, scopeBuilder.childNode)
    }

    fun <T : Any> instance(scope: Scope, kodiKey: KodiKey) : T {
        val child = root.search { it.scope == scope } ?: throw NoMatchingScopeException(scope)
        val result = child.searchUpToRoot { it.module.providers.containsKey(kodiKey) } ?: throw NoMatchingKeyException(kodiKey)
        @Suppress("UNCHECKED_CAST")
        return result.module.providers[kodiKey]!!.provide() as T
    }

    inline fun <reified T : Any> instance(scope: Scope, tag: String = "") : T = instance(scope, toKey<T>(tag))
}