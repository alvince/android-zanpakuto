package cn.alvince.zanpakuto.lifecycle

import androidx.lifecycle.ViewModelProvider
import cn.alvince.zanpakuto.core.C

/**
 * Created by alvince on 2021/7/1
 *
 * @author alvince.zy@gmail.com
 */
object ViewModelProviders {

    private val SIMPLE_NEW_INSTANCE_FACTORY = ViewModelProvider.NewInstanceFactory()

    fun newInstanceFactory(): ViewModelProvider.Factory = SIMPLE_NEW_INSTANCE_FACTORY

    fun appInjectFactory(): ViewModelProvider.Factory = ViewModelProvider.AndroidViewModelFactory(C.requireApp())
}
