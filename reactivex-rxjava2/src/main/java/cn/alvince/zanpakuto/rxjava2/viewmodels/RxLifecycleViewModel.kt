package cn.alvince.zanpakuto.rxjava2.viewmodels

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import cn.alvince.zanpakuto.lifecycle.LifecycleAppViewModel
import cn.alvince.zanpakuto.lifecycle.LifecycleViewModel
import cn.alvince.zanpakuto.rxjava2.DisposableLifecycleRegistry
import io.reactivex.disposables.Disposable

/**
 * Created by alvince on 2021/5/22
 *
 * @author alvince.zy@gmail.com
 */
open class RxLifecycleViewModel : LifecycleViewModel() {

    private var disposableRegistry: DisposableLifecycleRegistry? = null

    override fun onMonitorLifecycle(owner: LifecycleOwner) {
        super.onMonitorLifecycle(owner)
        if (owner is Fragment) {
            owner.viewLifecycleOwnerLiveData.observe(owner) {
                disposableRegistry = DisposableLifecycleRegistry(it)
            }
        } else {
            disposableRegistry = DisposableLifecycleRegistry(owner)
        }
    }

    fun Disposable.bindUntil(until: Lifecycle.Event) {
        disposableRegistry?.bindUntil(this, until)
    }

    fun Disposable.bindUntilViewDestroy() {
        disposableRegistry?.bindUntil(this, Lifecycle.Event.ON_DESTROY)
    }
}

open class RxLifecycleAppViewModel(application: Application) : LifecycleAppViewModel(application) {

    private var disposableRegistry: DisposableLifecycleRegistry? = null

    override fun onMonitorLifecycle(owner: LifecycleOwner) {
        super.onMonitorLifecycle(owner)
        if (owner is Fragment) {
            owner.viewLifecycleOwnerLiveData.observe(owner) {
                disposableRegistry = DisposableLifecycleRegistry(it)
            }
        } else {
            disposableRegistry = DisposableLifecycleRegistry(owner)
        }
    }

    fun Disposable.bindUntil(until: Lifecycle.Event) {
        disposableRegistry?.bindUntil(this, until)
    }

    fun Disposable.bindUntilViewDestroy() {
        disposableRegistry?.bindUntil(this, Lifecycle.Event.ON_DESTROY)
    }
}
