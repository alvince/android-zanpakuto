Android zanpakuto kit
===

`zanpakuto` is a suite of libraries to help app-development, similar `Android-Jetpack`

Components
---

### core

Provides some useful infras functions

##### Animation

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

### lifecycle

Provides some `Android Lifecycle` based components, and exensions for lifecycle

### rajava2

Provides some `RxJava`2 based components and extensions

Usage
---

core
```groovy
dependencies {
    implementation 'cn.alvince.zanpakuto:core:1.0.0'
}
```

lifecycle
```groovy
dependencies {
    implementation 'cn.alvince.zanpakuto:core:1.0.0' // required
    implementation 'cn.alvince.zanpakuto:lifecycle:1.0.0'
}
```

rxjava2 exts
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
