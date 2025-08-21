package com.mobile.app_iara.ui.profile.configuracao

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.app_iara.R

class ConfiguracaoFragment : Fragment() {

    companion object {
        fun newInstance() = ConfiguracaoFragment()
    }

    private val viewModel: ConfiguracaoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_configuracao, container, false)
    }
}