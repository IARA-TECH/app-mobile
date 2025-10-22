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
            findNavController().navigate(R.id.action_termsFragment_to_profileFragment)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val lista = listOf(
            Terms("1. Introdução", "Bem-vindo ao IARA. Estes Termos e Condições regem o uso do nosso aplicativo, que se destina a automatizar o processo de registro de dados (como condenações em matadouros de frangos) a partir da leitura de ábacos por foto, gerando planilhas e análises. Ao acessar ou usar o aplicativo, você concorda em cumprir estes termos."),
            Terms("2. Contas de Usuário", "- Primeiro Acesso: Para utilizar o aplicativo, você pode precisar realizar um \"Primeiro Acesso\" , utilizando o e-mail e a senha fornecida pelo seu gestor. Neste processo, você deverá cadastrar uma nova senha pessoal.\n" +
                    "\n" +
                    "- Login Regular: O acesso subsequente requer E-mail ou CPF e Senha. O sistema também oferece a opção de login via conta Google.\n" +
                    "\n" +
                    "- Responsabilidade: Você é responsável por manter a confidencialidade de sua senha. O sistema permite ocultar ou exibir a senha durante a digitação.\n" +
                    "\n" +
                    "- Recuperação de Senha: Caso esqueça sua senha, você pode usar o link \"Esqueceu sua senha?\" , que solicitará seu e-mail para o envio de instruções de redefinição."),
            Terms("3. Uso do Serviço", "- Cadastro de Fábrica: Para cadastrar uma nova fábrica, o usuário deve selecionar a opção \"Cadastrar sua fábrica\" , que abrirá um cliente de e-mail com informações padrão para serem editadas e enviadas à empresa.\n" +
                    "\n" +
                    "- Escaneamento de Ábaco: A função principal do app é escanear ábacos. O usuário deve usar a câmera e seguir as instruções do onboarding e da grade de encaixe. O sistema pode exibir um aviso caso o ábaco não seja detectado.\n" +
                    "\n" +
                    "- Confirmação de Dados: Após a captura da foto, o usuário é responsável por revisar e confirmar os dados escaneados (condenas parciais e totais) em duas etapas.\n" +
                    "\n" +
                    "- Geração de Planilhas: Após a confirmação , o sistema processará os dados e gerará uma planilha , que será armazenada na tela \"Planilhas\"."),
            Terms("4. Funções de Gestão (Supervisores)", "- Usuários com permissão (Supervisores SIF) podem acessar a tela \"Gestão\".\n" +
                    "\n" +
                    "- Supervisores podem visualizar uma lista de todos os usuários (nome, foto, e-mail, cargo) , adicionar novos colaboradores e editar informações de contas existentes (e-mail, cargo, gênero)."),
            Terms("5. Disponibilidade do Serviço", "O sistema exibirá telas de erro específicas caso não haja conexão com a internet ou se ocorrer um problema interno. Nesses casos, o usuário será instruído a tentar novamente."),
            Terms("6. Modificações nos Termos", "Estes termos podem ser atualizados. O app disponibiliza essa respectiva tela para que veja a versão sempre mais atualizada possível."),
        )

        val adapter = TermsAdapter(lista)
        recyclerView.adapter = adapter
    }
}