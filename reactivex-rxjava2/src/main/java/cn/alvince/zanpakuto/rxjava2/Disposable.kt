package cn.alvince.zanpakuto.rxjava2

import android.util.Log
import androidx.lifecycle.Lifecycle
import cn.alvince.zanpakuto.lifecycle.LifecycleScope
import io.reactivex.disposables.Disposable

fun Disposable.disposeSafely() {
    if (isDisposed) {
        return
    }
    dispose()
}

/**
 * Ignore [Disposable] result, without bind to any event
 */
fun Disposable.forever() {
    Log.d(TAG, "$this no dispose manually")
}

fun Disposable.bindToRegistry(disposableRegistry: DisposableLifecycleRegistry, until: Lifecycle.Event = Lifecycle.Event.ON_DESTROY) {
    disposableRegistry.bindUntil(this, until)
}

fun Disposable.bindToLifecycle(registry: RxLifecycleRegistry, until: Lifecycle.Event = Lifecycle.Event.ON_DESTROY) {
    if (!registry.attached) {
        Log.w(TAG, "bindToLifecycle: registry[$registry] not attached to lifecycle.")
        return
    }
    registry.bindToLifecycle(this, until)
}

fun Disposable.bindUntilDestroy(registry: RxLifecycleRegistry) {
    if (!registry.attached) {
        Log.w(TAG, "bindToLifecycle: registry[$registry] not attached to lifecycle.")
        return
    }
    registry.bindToLifecycle(this, Lifecycle.Event.ON_DESTROY)
}

fun Disposable.bindUntilViewDestroy(registry: RxFragmentLifecycleRegistry) {
    if (!registry.attached) {
        Log.w(TAG, "bindToLifecycle: registry[$registry] not attached to lifecycle.")
        return
    }
    registry.bindLifecycleScoped(LifecycleScope.VIEW, this, Lifecycle.Event.ON_DESTROY)
}
