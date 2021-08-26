package cn.alvince.zanpakuto.rxjava2

import android.util.Log
import androidx.annotation.MainThread
import androidx.collection.ArrayMap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * [androidx.lifecycle.Lifecycle] registry for [Disposable]
 *
 * Created by alvince on 2021/4/15
 *
 * @author alvince.zy@gmail.com
 */
class DisposableLifecycleRegistry(owner: LifecycleOwner) {

    val disposed: Boolean get() = !active

    @Volatile
    private var active: Boolean = true

    @Volatile
    private var lifecycleState: Lifecycle.State = Lifecycle.State.INITIALIZED

    private val entryMap = ArrayMap<Lifecycle.Event, CompositeDisposable>()

    init {
        owner.lifecycle.addObserver(LifecycleObserverWrapper(owner) { _, event -> handleLifecycle(event) })
    }

    fun bindUntil(disposable: Disposable, until: Lifecycle.Event) {
        if (!active || until.isLifecyclePast()) {
            Log.i(TAG, "bindUntil: disposable")
            disposable.disposeSafely()
            return
        }
        synchronized(this) {
            entryMap.getOrPut(until) { CompositeDisposable() }
        }
            .add(disposable)
    }

    @MainThread
    private fun handleLifecycle(event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_DESTROY -> {
                active = false
                disposeAll()
            }
            else -> entryMap[event]?.disposeSafely()
        }
        computeLifecycleState(event) { lifecycleState = it }
    }

    @MainThread
    private fun disposeAll() {
        entryMap.apply {
            forEach { (_, composite) -> composite.disposeSafely() }
            clear()
        }
    }

    private fun Lifecycle.Event.isLifecyclePast(): Boolean {
        return computeLifecycleState(this) { state -> state > lifecycleState }
    }

    private fun <T> computeLifecycleState(event: Lifecycle.Event, block: (Lifecycle.State) -> T): T {
        return when (event) {
            Lifecycle.Event.ON_CREATE -> Lifecycle.State.CREATED
            Lifecycle.Event.ON_START -> Lifecycle.State.STARTED
            Lifecycle.Event.ON_RESUME -> Lifecycle.State.RESUMED
            Lifecycle.Event.ON_PAUSE -> Lifecycle.State.STARTED
            Lifecycle.Event.ON_STOP -> Lifecycle.State.CREATED
            Lifecycle.Event.ON_DESTROY -> Lifecycle.State.DESTROYED
            Lifecycle.Event.ON_ANY -> Lifecycle.State.INITIALIZED
        }
            .let(block)
    }
}
