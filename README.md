Android zanpakuto kit
===

`zanpakuto` is a suite of libraries to help app-development, similar `Android-Jetpack`

__snapshot, add config:__
```groovy
repositories {
    maven {
        url 'https://s01.oss.sonatype.org/content/repositories/snapshots/'
    }
}
```

Core
---

Provides some useful infras functions

```groovy
dependencies {
    implementation 'cn.alvince.zanpakuto:core:1.0.1' // require Kotlin 1.6
    
    // or deps lower version of Kotlin 
    implementation 'cn.alvince.zanpakuto:core-stdlib1.3:1.0.0' // Kotlin 1.3
}
```

#### Animation

Fast create & play view animation

```kotlin
val animation = alphaAnimation {
    // changeAlpha(0.3F, 1F)
    from = 0.3F
    to = 1.0F
} // create an AlphaAnimation

val animation = view.alpha {
    from = 0.3F
    to = 1.0F
} // create and play animation directly
```

Lifecycle
---

Provides some `Android Lifecycle` based components, and exensions for lifecycle

```groovy
dependencies {
    implementation 'cn.alvince.zanpakuto:lifecycle:1.0.0.f' // require Kotlin 1.6
    implementation 'cn.alvince.zanpakuto:core:1.0.0.f' // deps on lib-core

    // or deps lower version of Kotlin 
    implementation 'cn.alvince.zanpakuto:lifecycle-stdlib1.3:1.0.0' // Kotlin 1.3
    implementation 'cn.alvince.zanpakuto:core-stdlib1.3:1.0.0'
}
```

View
---

Provides some `Android View` extensions

Viewbinding
---

Provides some `Android Viewbinding` extensions

```groovy
dependencies {
    implementation 'cn.alvince.zanpakuto:viewbinding:0.1-SNAPSHOT'
}
```

for Activity
```kotlin
class MyActivity : ComponentActivity(), ActivityViewBinding<MyActivityBinding> by ActivityBinding() {

    override fun onCreate(savedInstanceState: Bundle?) {
        …
        // replace setContentView(), and hold binding instance
        inflate(
            inflate = { MyActivityBinding.inflate(layoutInflater) },
            /* option: */onClear = { it.onClear() },
        ) { binding ->
            // init with binding
            …
        }
        …
    }

    // Optional: perform clear binding
    private fun MyActivityBinding.onClear() {
        …
    }

    …
}
```

for Fragment
```kotlin
class MyFragment : Fragment(), FragmentViewBinding<MyFragmentBinding> by FragmentBinding() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflate(
            inflate = { MyFragmentBinding.inflate(inflater, container, false) },
            /* option: */onClear = { it.onClear() },
        ) {
            // init binding, views and states here
        }

    // Optional: perform clear binding
    private fun MyFragmentBinding.onClear() {
         …
    }

    …
}
```

Databinding
---

Provides some `Android Databinding` extensions

```groovy
dependencies {
    implementation 'cn.alvince.zanpakuto:databinding:0.1-SNAPSHOT'
    implementation 'cn.alvince.zanpakuto:view:0.1-SNAPSHOT'
}
```

Rxjava2
---

Provides some `RxJava`2 based components and extensions

```groovy
dependencies {
    implementation 'cn.alvince.zanpakuto:core:1.0.0' // required
    implementation 'cn.alvince.zanpakuto:lifecycle:1.0.0' // required
    implementation 'cn.alvince.zanpakuto:rxjava2:1.0.0-SNAPSHOT'
}
```

License
---

Under Apache 2.0
