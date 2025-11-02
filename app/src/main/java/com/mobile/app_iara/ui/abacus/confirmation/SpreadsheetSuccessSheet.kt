package com.mobile.app_iara.ui.abacus.confirmation

import android.content.Intent
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
import com.mobile.app_iara.ui.MainActivity

class SpreadsheetSuccessSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnGoToSheets = view.findViewById<Button>(R.id.btn_go_to_sheets)
        val btnGoToHome = view.findViewById<Button>(R.id.btn_go_to_home)

        val lottieView = view.findViewById<LottieAnimationView>(R.id.lottie_success)
        lottieView.setMaxProgress(0.71f)

        btnGoToSheets.setOnClickListener {
            dismiss()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra("open_fragment", "sheets")
            startActivity(intent)
            activity?.finish()
        }

        btnGoToHome.setOnClickListener {
            dismiss()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra("open_fragment", "home")
            startActivity(intent)
            activity?.finish()
        }

        dialog?.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}