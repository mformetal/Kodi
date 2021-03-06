package mformetal.kodi.core.api

import mformetal.kodi.core.Kodi
import mformetal.kodi.core.api.builder.KodiBuilder

interface ScopeModule {

    fun dependsOn() : Scope = Kodi.ROOT_SCOPE

    fun with() : Scope

    fun providers() : KodiBuilder.() -> Unit
}

fun ScopeModule.providerBuilder(block: KodiBuilder.() -> Unit) = block