package cn.alvince.zanpakuto.sample.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import cn.alvince.zanpakuto.sample.databinding.HomeDemoViewExtFragmentBinding
import cn.alvince.zanpakuto.view.recyclerview.listAdapter
import cn.alvince.zanpakuto.view.recyclerview.simpleViewHolder
import cn.alvince.zanpakuto.viewbinding.FragmentBinding
import cn.alvince.zanpakuto.viewbinding.FragmentViewBinding

/**
 * Created by alvince on 2022/1/25
 *
 * @author alvincezhang@didiglobal.com
 */
class ViewExtFragment : Fragment(), FragmentViewBinding<HomeDemoViewExtFragmentBinding> by FragmentBinding() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflate({ HomeDemoViewExtFragmentBinding.inflate(inflater) }) { it.initView() }

    private fun HomeDemoViewExtFragmentBinding.initView() {
        rvContent.listAdapter<String> {
            onCreateViewHolder { parent, viewType ->
                simpleViewHolder {
                    contentView { }
                }
            }
            onBindViewHolder { holder, position, item ->

            }
        }
            .also { adapter ->
                adapter.modifySource {

                }
            }
    }
}
