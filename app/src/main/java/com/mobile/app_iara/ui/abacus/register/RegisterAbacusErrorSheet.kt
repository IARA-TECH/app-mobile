package com.mobile.app_iara.ui.abacus.register

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobile.app_iara.R

class RegisterAbacusErrorSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_abacus_register_error, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnTryAgain = view.findViewById<Button>(R.id.btn_try_again)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)
        val lottieView = view.findViewById<LottieAnimationView>(R.id.lottie_error)
        lottieView.setMaxProgress(0.71f)

        btnTryAgain.setOnClickListener {
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
            findNavController().popBackStack()
        }

        dialog?.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}