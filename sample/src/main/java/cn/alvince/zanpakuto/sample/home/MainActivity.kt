package cn.alvince.zanpakuto.sample.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.alvince.zanpakuto.lifecycle.viewModel
import cn.alvince.zanpakuto.sample.home.viewmodel.MainPageViewModel

/**
 * Created by alvince on 2021/9/1
 *
 * @author alvince.zy@gmail.com
 */
class MainActivity : AppCompatActivity() {

    private val viewModel: MainPageViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
