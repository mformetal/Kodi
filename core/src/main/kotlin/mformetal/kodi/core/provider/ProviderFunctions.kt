package mformetal.kodi.core.provider

typealias Provider<T> = () -> T

fun <T> provider(block: () -> T) = block

fun <T> singleton(block: () -> T) = LazyProvider(block)

fun <T> component(instance: T) : Provider<T> = provider { instance }