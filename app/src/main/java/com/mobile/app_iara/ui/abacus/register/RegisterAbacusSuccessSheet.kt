package com.mobile.app_iara.ui.abacus.register

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobile.app_iara.R

class RegisterAbacusSuccessSheet(
    private val onVoltarClick: () -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_register_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnVoltar = view.findViewById<Button>(R.id.btn_voltar)
        val lottieView = view.findViewById<LottieAnimationView>(R.id.lottie_success)
        lottieView.setMaxProgress(0.71f)

        btnVoltar.setOnClickListener {
            onVoltarClick()
            dismiss()
        }

        // Deixa o fundo transparente (copiado do seu outro sheet)
        dialog?.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}