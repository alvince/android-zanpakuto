package cn.alvince.zanpakuto.core

import android.app.Application
import androidx.annotation.RestrictTo

/**
 * Created by alvince on 2021/6/10
 *
 * @author alvince.zy@gmail.com
 */
object C {

    internal const val TAG = "zanpakuto.core"

    private var application: Application? = null

    fun bindApp(app: Application) {
        application = app
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun requireApp(): Application {
        return application ?: throw IllegalStateException("Must bind application via bindApp() first.")
    }
}
