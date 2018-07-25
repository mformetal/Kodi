package mformetal.kodi.core.provider

class LazyProvider<out T>(private val provider: Provider<T>) : Provider<T> {

    private val value by lazy { provider() }

    override fun invoke(): T = value

}