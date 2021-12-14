package cn.alvince.zanpakuto.sample.databinding

import android.app.Application
import android.content.Context

/**
 * Created by alvince on 2021/9/1
 *
 * @author alvince.zy@gmail.com
 */
class SampleApp : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        cn.alvince.zanpakuto.core.C.bindApp(this)
    }
}
