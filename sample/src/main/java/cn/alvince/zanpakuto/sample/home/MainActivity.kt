package cn.alvince.zanpakuto.sample.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.alvince.zanpakuto.lifecycle.viewModel
import cn.alvince.zanpakuto.sample.databinding.HomeMainActivityBinding
import cn.alvince.zanpakuto.sample.home.viewmodel.MainPageViewModel
import cn.alvince.zanpakuto.viewbinding.ActivityBinding
import cn.alvince.zanpakuto.viewbinding.ActivityViewBinding

/**
 * Created by alvince on 2021/9/1
 *
 * @author alvince.zy@gmail.com
 */
class MainActivity : AppCompatActivity(), ActivityViewBinding<HomeMainActivityBinding> by ActivityBinding() {

    private val viewModel: MainPageViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.inflate(HomeMainActivityBinding::class.java) {
            it.tvMain.text = "test activity"
        }
    }
}
