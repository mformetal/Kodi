package mformetal.kodi.core.provider

typealias Provider<T> = () -> T

class LazyProvider<out T>(private val provider: Provider<T>) : Provider<T> {

    private val value by lazy { provider() }

    override fun invoke(): T = value

}

class ValueProvider<out T>(private val value: T) : Provider<T> {

    override fun invoke(): T = value

}