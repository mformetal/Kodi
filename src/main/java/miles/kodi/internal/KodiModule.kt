package miles.kodi.internal

import miles.kodi.api.builder.KodiBuilder
import miles.kodi.api.KodiKey
import kotlin.reflect.KClass

/**
 * Created by peelemil on 10/11/17.
 */
internal class KodiModule(internal val nodeOfModule: Node,
                          internal val module: Module = Module()) : KodiBuilder by module {

    override fun <T : Any> get(tag: String, type: KClass<T>): T {
        val key = KodiKey(type, tag)
        val node = nodeOfModule.searchUpToRoot { it.module.providers.contains(key) }
        @Suppress("FoldInitializerAndIfToElvis")
        if (node == null) {
            throw IllegalStateException("No binding with kodiKey $key exists for scope ${nodeOfModule.scope}.")
        }
        return node.module.get(tag, type)
    }
}