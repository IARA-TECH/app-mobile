package com.mobile.app_iara.ui.profile.termsandprivacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

class TermsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_terms_and_privacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val btnPrivacidade = view.findViewById<Button>(R.id.btnPrivacidade)
        val btnVoltar = view.findViewById<ImageButton>(R.id.btnVoltar3)

        btnPrivacidade.setOnClickListener {
            findNavController().navigate(R.id.action_termsFragment_to_privacyFragment)
        }

        btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val lista = listOf(
            Terms("1. LoremIpsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit..."),
            Terms("2. LoremIpsum", "Mais texto aqui..."),
            Terms("3. LoremIpsum", "Outro parágrafo...")
        )

        val adapter = TermsAdapter(lista)
        recyclerView.adapter = adapter
    }
}