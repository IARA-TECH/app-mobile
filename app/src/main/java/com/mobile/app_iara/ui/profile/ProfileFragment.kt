package com.mobile.app_iara.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.profile.faq.FaqActivity
import com.mobile.app_iara.ui.profile.termsandprivacy.TermsActivity
import com.mobile.app_iara.ui.start.LoginActivity

class ProfileFragment : Fragment() {

    // Instancia o ViewModel usando o delegate KTX 'by viewModels()'
    private val viewModel: ProfileViewModel by viewModels()

    // Declarações dos componentes da UI
    private lateinit var fotoPerfil: ShapeableImageView
    private lateinit var btnTrocarFoto: ImageButton

    // ActivityResultLauncher para selecionar uma imagem da galeria
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Carrega a nova imagem localmente para feedback imediato
            Glide.with(this).load(it).into(fotoPerfil)
            // TODO: Chamar o ViewModel para fazer o upload da nova foto para o servidor
            // Exemplo: viewModel.updateUserPhoto(FirebaseAuth.getInstance().currentUser!!.uid, it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Inicializa os componentes da UI
        val userName = view.findViewById<TextView>(R.id.textView13)
        val userCargo = view.findViewById<TextView>(R.id.textView16)

        // Configura informações estáticas do usuário (nome, email)
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            userName.text = it.displayName ?: "Usuário"
            userCargo.text = it.email ?: "Cargo não definido"
        }

        // Configura os listeners dos botões
        setupButtonClickListeners(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Associa as variáveis aos componentes da view
        fotoPerfil = view.findViewById(R.id.fotoPerfil)
        btnTrocarFoto = view.findViewById(R.id.btnTrocarFoto)

        btnTrocarFoto.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Inicia o carregamento dos dados dinâmicos (foto do perfil)
        loadUserProfileData()

        // Configura os observadores para reagir às mudanças de dados do ViewModel
        setupObservers()
    }

    private fun loadUserProfileData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            // Pede ao ViewModel para carregar a foto
            viewModel.loadUserPhoto(userId)
        } else {
            // Lidar com o caso de usuário não logado, se necessário
            Toast.makeText(context, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        // Observa o LiveData 'photoUrl'. Quando o valor muda, este bloco é executado.
        viewModel.photoUrl.observe(viewLifecycleOwner) { photoUrl ->
            if (!photoUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_user) // Imagem mostrada enquanto carrega
                    .error(R.drawable.ic_user)       // Imagem mostrada em caso de erro
                    .into(fotoPerfil)
            }
        }

        // Observa o LiveData 'error'. Quando o valor muda, mostra um Toast com o erro.
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupButtonClickListeners(view: View) {
        view.findViewById<MaterialCardView>(R.id.btnConfig).setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_configuracao)
        }
        view.findViewById<MaterialCardView>(R.id.btnFabrica).setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_fabrica)
        }
        view.findViewById<MaterialCardView>(R.id.btnSair).setOnClickListener {
            confirmarSaida()
        }
        view.findViewById<MaterialCardView>(R.id.btnFaq).setOnClickListener {
            startActivity(Intent(requireContext(), FaqActivity::class.java))
        }
        view.findViewById<MaterialCardView>(R.id.btnTermsandconditions).setOnClickListener {
            startActivity(Intent(requireContext(), TermsActivity::class.java))
        }
    }

    private fun confirmarSaida() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_exit_confirmation, null)
        builder.setView(dialogView)

        val dialog = builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
            val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
            window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        dialogView.findViewById<Button>(R.id.btnSairDialog).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnCancelarDialog).setOnClickListener {
            dialog.dismiss()
        }
    }
}