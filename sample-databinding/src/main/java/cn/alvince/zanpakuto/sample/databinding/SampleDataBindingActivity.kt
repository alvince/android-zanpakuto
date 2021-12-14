package cn.alvince.zanpakuto.sample.databinding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.alvince.zanpakuto.databinding.ActivityBinding
import cn.alvince.zanpakuto.databinding.ActivityBindingHolder
import cn.alvince.zanpakuto.sample.databinding.databinding.SampleDataBindingActivityBinding

class SampleDataBindingActivity : AppCompatActivity(),
    ActivityBindingHolder<SampleDataBindingActivityBinding> by ActivityBinding(R.layout.sample_data_binding_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflate {
            it.tvContent.text = "This is data-binding sample"
        }
    }
}
