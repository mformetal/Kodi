package miles.kodi.provider

inline fun <T> provider(crossinline block: () -> T) : Provider<T> {
    return object : Provider<T> {
        override fun provide() = block.invoke()
    }
}

inline fun <T> singleton(crossinline block: () -> T) : Provider<T> {
    val provider = provider(block)
    return LazyProvider(provider)
}

fun <T> component(instance: T) : Provider<T> = provider { instance }