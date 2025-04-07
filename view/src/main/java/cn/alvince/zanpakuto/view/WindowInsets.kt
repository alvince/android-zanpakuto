package cn.alvince.zanpakuto.view

import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsAnimationCompat.BoundsCompat
import androidx.core.view.WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_CONTINUE_ON_SUBTREE
import androidx.core.view.WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_STOP
import androidx.core.view.WindowInsetsAnimationCompat.Callback.DispatchMode
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference

@DslMarker
annotation class WindowInsetDsl

inline val WindowInsetsCompat.statusBar get() = this.getInsets(WindowInsetsCompat.Type.statusBars())

inline val WindowInsetsCompat.ime get() = this.getInsets(WindowInsetsCompat.Type.ime())

inline val WindowInsetsCompat.navigationBar get() = this.getInsets(WindowInsetsCompat.Type.navigationBars())

@RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
fun View.doOnApplyWindowInsets(lifecycleOwner: LifecycleOwner, block: (view: View, insets: WindowInsets) -> WindowInsets) {
    if (lifecycleOwner.lifecycle.currentState < Lifecycle.State.INITIALIZED) {
        return
    }
    val l = View.OnApplyWindowInsetsListener(block)
    setOnApplyWindowInsetsListener(l)
    val h = WeakReference(this)
    lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_START -> h.get()?.setOnApplyWindowInsetsListener(l)
                Lifecycle.Event.ON_STOP -> h.get()?.setOnApplyWindowInsetsListener(null)
                Lifecycle.Event.ON_DESTROY -> source.lifecycle.removeObserver(this)
                else -> {
                    /* no-op */
                }
            }
        }
    })
}

@RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
inline fun WindowInsets.toInsetsCompat() = WindowInsetsCompat.toWindowInsetsCompat(this)

fun View.monitorWindowInsetsAnimation(config: WindowInsetsAnimationCallbackOptions.() -> Unit) {
    val options = WindowInsetsAnimationCallbackOptions().apply(config)
    ViewCompat.setWindowInsetsAnimationCallback(this, options.toWindowInsetsAnimationCallback())
}

fun WindowInsetsCompat.printInfo(): String = buildString {
    append("WindowInsetsCompat{\n    ")
    statusBar.also {
        append("statusBars=$it vis=${isVisible(WindowInsetsCompat.Type.statusBars())}").append("\n    ")
    }
    navigationBar.also {
        append("navigationBars=$it vis=${isVisible(WindowInsetsCompat.Type.navigationBars())}").append("\n    ")
    }
    getInsets(WindowInsetsCompat.Type.captionBar()).also {
        append("captionBar=$it vis=${isVisible(WindowInsetsCompat.Type.captionBar())}").append("\n    ")
    }
    ime.also {
        append("ime=$it vis=${isVisible(WindowInsetsCompat.Type.ime())}").append("\n    ")
    }
    getInsets(WindowInsetsCompat.Type.systemGestures()).also {
        append("systemGestures=$it vis=${isVisible(WindowInsetsCompat.Type.systemGestures())}").append("\n    ")
    }
    getInsets(WindowInsetsCompat.Type.mandatorySystemGestures()).also {
        append("mandatorySystemGestures=$it vis=${isVisible(WindowInsetsCompat.Type.mandatorySystemGestures())}").append("\n    ")
    }
    getInsets(WindowInsetsCompat.Type.tappableElement()).also {
        append("tappableElement=$it vis=${isVisible(WindowInsetsCompat.Type.tappableElement())}").append("\n    ")
    }
    getInsets(WindowInsetsCompat.Type.displayCutout()).also {
        append("displayCutout=$it vis=${isVisible(WindowInsetsCompat.Type.displayCutout())}").append("\n    ")
    }
    append("}")
}

@WindowInsetDsl
class WindowInsetsAnimationCallbackOptions {

    @DispatchMode
    private var callbackDispatchMode: Int = DISPATCH_MODE_STOP

    private var onPrepareAction: ((animation: WindowInsetsAnimationCompat) -> Unit)? = null
    private var onStartAction: ((animation: WindowInsetsAnimationCompat, bounds: BoundsCompat) -> BoundsCompat)? = null
    private var onProgressAction: ((insets: WindowInsetsCompat, runningAnimations: MutableList<WindowInsetsAnimationCompat>) -> WindowInsetsCompat)? = null
    private var onEndAction: ((animation: WindowInsetsAnimationCompat) -> Unit)? = null

    fun dispatchMode(@DispatchMode dispatchMode: Int) {
        callbackDispatchMode = if (dispatchMode == DISPATCH_MODE_CONTINUE_ON_SUBTREE) DISPATCH_MODE_CONTINUE_ON_SUBTREE else DISPATCH_MODE_STOP
    }

    fun onPrepare(block: (animation: WindowInsetsAnimationCompat) -> Unit) {
        onPrepareAction = block
    }

    fun onStart(block: (animation: WindowInsetsAnimationCompat, bounds: BoundsCompat) -> BoundsCompat) {
        onStartAction = block
    }

    fun onProgress(block: (insets: WindowInsetsCompat, runningAnimations: MutableList<WindowInsetsAnimationCompat>) -> WindowInsetsCompat) {
        onProgressAction = block
    }

    fun onEnd(block: (animation: WindowInsetsAnimationCompat) -> Unit) {
        onEndAction = block
    }

    fun toWindowInsetsAnimationCallback(): WindowInsetsAnimationCompat.Callback {
        return object : WindowInsetsAnimationCompat.Callback(callbackDispatchMode) {
            override fun onProgress(insets: WindowInsetsCompat, runningAnimations: MutableList<WindowInsetsAnimationCompat>): WindowInsetsCompat {
                return onProgressAction?.invoke(insets, runningAnimations) ?: insets
            }

            override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                super.onPrepare(animation)
                onPrepareAction?.invoke(animation)
            }

            override fun onStart(animation: WindowInsetsAnimationCompat, bounds: BoundsCompat): BoundsCompat {
                return onStartAction?.invoke(animation, bounds) ?: bounds
            }

            override fun onEnd(animation: WindowInsetsAnimationCompat) {
                super.onEnd(animation)
                onEndAction?.invoke(animation)
            }
        }
    }
}
