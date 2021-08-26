package cn.alvince.zanpakuto.lifecycle

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel

/**
 * Created by alvince on 2021/7/1
 *
 * @author alvince.zy@gmail.com
 */
class ViewModelActor {

    fun applyLifecycle(viewModel: ViewModel, lifecycleOwner: LifecycleOwner) {
        (viewModel as? LifecycleMonitor)?.monitorLifecycle(lifecycleOwner)
    }
}
