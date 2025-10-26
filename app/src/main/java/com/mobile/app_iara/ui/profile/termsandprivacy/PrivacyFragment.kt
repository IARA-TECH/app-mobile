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

class PrivacyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_privacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val btnTermos = view.findViewById<Button>(R.id.btnTermos)
        val btnVoltar = view.findViewById<ImageButton>(R.id.btnVoltar3)

        btnTermos.setOnClickListener {
            findNavController().navigate(R.id.action_privacyFragment_to_termsFragment)
        }

        btnVoltar.setOnClickListener {
            findNavController().navigate(R.id.action_privacyFragment_to_profileFragment)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val lista = listOf(
            Terms("1. Introdução", "Esta Política de Privacidade descreve como o IARA coleta, usa e trata suas informações pessoais e operacionais."),
            Terms("2. Informações que Coletamos", "Coletamos os seguintes tipos de informações:\n" +
                    "\n" +
                    "- Informações de Identificação Pessoal: Nome completo , E-mail , CPF (para login) , Senha , Gênero , Data de Nascimento , Cargo e Foto de Perfil.\n" +
                    "\n" +
                    "- Informações da Empresa: Nome da empresa, CNPJ, Domínio e Endereço da fábrica.\n" +
                    "\n" +
                    "- Informações Operacionais: Fotos de ábacos (capturadas pela câmera ou acessadas da galeria ), dados extraídos dessas fotos , e planilhas geradas.\n" +
                    "\n" +
                    "- Informações de Uso: Histórico de registros (incluindo nome do ábaco, nome de quem tirou a foto, nome de quem aprovou, data e horário) , mensagens enviadas e recebidas pelo Chatbot , e notificações recebidas.\n" +
                    "\n" +
                    "- Informações de Login: Podemos coletar informações de autenticação de serviços terceiros, como o Google, caso você opte por essa forma de login."),
            Terms("3. Como Usamos Suas Informações", "- Para Fornecer o Serviço: Para validar seu login , identificar você no aplicativo (exibindo seu nome e foto) , e permitir a recuperação de senha.\n" +
                    "\n" +
                    "- Para Automatizar Processos: Usamos as fotos e dados dos ábacos para gerar planilhas e alimentar os dashboards de análise.\n" +
                    "\n" +
                    "- Para Gestão de Equipe: Supervisores utilizam seus dados (nome, cargo, e-mail) para gerenciar usuários na plataforma.\n" +
                    "\n" +
                    "- Para Comunicação: Para enviar notificações do sistema (como aprovação de fotos) e responder às suas solicitações via Chatbot.\n" +
                    "\n" +
                    "- Para Análise de Dados: Os dados de condenações são usados para gerar dashboards comparativos (entre turnos, fábricas, tipos de condena)."),
            Terms("4. Compartilhamento de Informações", "- Dentro da sua Organização: Suas informações (nome, foto, cargo) são visíveis para os supervisores da sua fábrica na tela \"Gestão\". O nome do operador que tira a foto e do supervisor que a aprova fica visível no \"Histórico\".\n" +
                    "\n" +
                    "- Não compartilhamos suas informações pessoais com terceiros fora da sua organização, exceto para o funcionamento do login (ex: Google ) ou conforme necessário para operar o serviço."),
            Terms("5. Controle de Suas Informações", "- Você pode visualizar suas informações (e-mail, nome, gênero, data de nascimento) na tela \"Configuração\".\n" +
                    "\n" +
                    "- O sistema permite que você altere seu e-mail e senha.\n" +
                    "\n" +
                    "- Supervisores podem editar o e-mail, cargo e gênero dos colaboradores.\n" +
                    "\n" +
                    "- Você pode optar por \"manter-se conectado\" ou realizar o logout."),
            Terms("6. Alterações na Política", "Esta política pode ser atualizada. O app disponibiliza essa respectiva tela para que veja a versão sempre mais atualizada possível.")
        )

        val adapter = PrivacyAdapter(lista)
        recyclerView.adapter = adapter
    }
}
