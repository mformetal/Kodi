package mformetal.kodi.core.internal

import mformetal.kodi.core.api.KodiKey
import mformetal.kodi.core.api.builder.KodiBuilder
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * @author - mbpeele on 10/11/17.
 */
internal class KodiModule(internal val nodeOfModule: Node,
                          module: Module = Module()) : KodiBuilder by module {

    override fun <T : Any> get(tag: String, type: KClass<T>, generics: Array<Type>): T {
        val key = KodiKey(type, generics, tag)
        val node = nodeOfModule.searchUpToRoot { it.module.providers.contains(key) }
                ?: throw IllegalStateException("No binding with kodiKey $key exists for scopeBuilder ${nodeOfModule.scope}.")

        return node.module.get(tag, type, generics)
    }
}