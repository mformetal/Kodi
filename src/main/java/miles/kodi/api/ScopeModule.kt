package miles.kodi.api

import miles.kodi.Kodi
import miles.kodi.api.builder.KodiBuilder
import miles.kodi.internal.KodiScopeBuilder
import miles.kodi.internal.Module
import miles.kodi.provider.Provider

interface ScopeModule {

    fun dependsOn() : Scope = Kodi.ROOT_SCOPE

    fun with() : Scope

    fun providers() : KodiBuilder.() -> Unit
}

fun ScopeModule.providerBuilder(block: KodiBuilder.() -> Unit) = block