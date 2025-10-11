package com.mobile.app_iara.ui.profile.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

class FaqFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_faq, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnVoltar = view.findViewById<ImageButton>(R.id.btnVoltar4)
        val recyclerViewDuvidas = view.findViewById<RecyclerView>(R.id.recycler_duvidas)
        val recyclerViewPopulares = view.findViewById<RecyclerView>(R.id.recycler_populares)

        btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        recyclerViewPopulares.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewDuvidas.layoutManager = LinearLayoutManager(requireContext())

        val listaPopulares = listOf(
            FaqPopularQuestion("1. LoremIpsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit...Lorem ipsum dolor sit amet, consectetur adipiscing elit..."),
            FaqPopularQuestion("1. LoremIpsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit...Lorem ipsum dolor sit amet, consectetur adipiscing elit..."),
            FaqPopularQuestion("1. LoremIpsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit...Lorem ipsum dolor sit amet, consectetur adipiscing elit...")
        )

        val adapterPopulares = FaqPopularQuestionAdapter(listaPopulares)
        recyclerViewPopulares.adapter = adapterPopulares

        val listaDuvidas = listOf(
            FaqQuestion("1. LoremIpsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit...Lorem ipsum dolor sit amet, consectetur adipiscing elit..."),
            FaqQuestion("1. LoremIpsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit..."),
            FaqQuestion("1. LoremIpsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit...Lorem ipsum dolor sit amet, consectetur adipiscing elit...Lorem ipsum dolor sit amet, consectetur adipiscing elit..."),
        )

        val adapterDuvidas = FaqQuestionAdapter(listaDuvidas)
        recyclerViewDuvidas.adapter = adapterDuvidas
    }
}