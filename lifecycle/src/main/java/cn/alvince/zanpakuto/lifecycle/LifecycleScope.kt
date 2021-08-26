package cn.alvince.zanpakuto.lifecycle

/**
 * [androidx.lifecycle.Lifecycle] scope enum
 *
 * Created by alvince on 2021/4/15
 *
 * @author alvince.zy@gmail.com
 */
enum class LifecycleScope {

    /**
     * component lifecycle scope
     *
     * eg. [androidx.activity.ComponentActivity], [androidx.fragment.app.Fragment], or any implementation of [androidx.lifecycle.LifecycleOwner]
     */
    COMPONENT,

    /**
     * component view lifecycle scope
     *
     * specified [androidx.fragment.app.Fragment]'s view lifecycle
     */
    VIEW,
}
