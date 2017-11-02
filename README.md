# Kodi
KOtlin Dependency Injection

Kodi is a fast, type-safe dependency retrieval library. 

Heavily inspired by https://github.com/SalomonBrys/Kodein. Kudos to him/her. 

Its main draws are:

1) Lazy instantiation of dependencies 
2) Heavy usage of a DSL powered by Kotlin's sweet, sweet love
3) Declare everything manually without the need for annotation processing 

A simple example:

```
val kodi = Kodi.init {
  bind<DependencyInterface> using component(DependencyImplementation())
}

class Something(val dependencyInterface: DependencyInterface)

val homeActivityRegistry = kodi.scopeBuilder {
  // depends on ROOT scope
  build(scoped<HomeActivity>) {
     bind<Something> using provider { Something(get()) } // Kotlin will resolve the type of get() for you
  }
}

homeActivityRegistry.unregister() // When you no longer need this scope.

class FragmentSomething(val something: Something)

val homeFragmentRegistry = kodi.scopeBuilder()
  .dependsOn(scoped<HomeActivity>)
  .build(scoped<HomeFragment>) {
    bind<FragmentSomething> using lazyProvider { FragmentSomething(get()) }
  }
  
homeFragmentRegistry.unregister()

```

Currently used by one of my apps, https://github.com/mformetal/Canvas.
