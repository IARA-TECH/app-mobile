package com.mobile.app_iara.ui.profile.faq

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils

class FaqFragment : Fragment() {

    private val newsViewModel: NewsViewModel by viewModels()

    private lateinit var adapterPopulares: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_faq, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            val intent = Intent(requireContext(), WifiErrorActivity::class.java)
            startActivity(intent)
            activity?.finish()
            return
        }

        val btnVoltar = view.findViewById<ImageButton>(R.id.btnVoltar4)
        val recyclerViewDuvidas = view.findViewById<RecyclerView>(R.id.recycler_duvidas)
        val recyclerViewPopulares = view.findViewById<RecyclerView>(R.id.recycler_populares)

        btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        recyclerViewPopulares.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        adapterPopulares = NewsAdapter(emptyList())
        recyclerViewPopulares.adapter = adapterPopulares

        observeViewModel()

        newsViewModel.fetchNews(listOf("indústria avícola", "inovação industrial", "automatização de linha de produção"))

        recyclerViewDuvidas.layoutManager = LinearLayoutManager(requireContext())

        val listaDuvidas = listOf(
            FaqQuestion("O que é o IARA?", "O IARA é um aplicativo desenvolvido para automatizar o registro de dados em matadouros de frangos. Ele utiliza a câmera do seu celular para \"ler\" as informações de um ábaco, transformando essa foto em planilhas e análises de forma automática, eliminando o registro manual."),
            FaqQuestion("Como faço meu primeiro login no aplicativo?", "Na tela de Login , selecione a opção \"Primeiro acesso\". Você precisará inserir seu e-mail e a senha temporária que foi fornecida pelo seu gestor. Em seguida, o sistema pedirá que você cadastre uma nova senha pessoal."),
            FaqQuestion("Como cadastro um novo tipo de ábaco que minha fábrica usa?", "Na tela \"Ábacos\" , clique no botão de \"+\". Você será levado à tela \"Cadastrar ábaco\" , onde poderá informar o número de linhas, colunas, nomes das condenas e as cores das contas (unidades, dezenas, centenas)."),
            FaqQuestion("(Para Supervisores) Como adiciono um novo funcionário ao aplicativo?", "Acesse a tela \"Gestão\" (disponível no menu footer ). Clique no botão \"+\" para ir à tela \"Cadastrar colaborador\". Lá, você deverá preencher o nome, e-mail, gênero e cargo do novo usuário e confirmar a ação."),
            FaqQuestion("O que são os Dashboards?", "Os Dashboards são telas de análise (geralmente para supervisores) que mostram gráficos e totais sobre as operações, como \"Condenas de falhas técnicas\" , \"Condenas pela granja\" e \"Comparativo entre turnos\""),
            FaqQuestion("Como faço para cadastrar minha fábrica no aplicativo?", "Na segunda tela de apresentação do app , clique no botão \"Cadastrar sua fábrica\". Isso irá abrir automaticamente o seu aplicativo de e-mail (cliente de e-mail) com uma mensagem pré-preenchida. Você só precisa editar com os dados da sua fábrica e enviar."),
            FaqQuestion("Tirei a foto. O que acontece agora?", "Após tirar a foto, o sistema mostrará os dados que escaneou. Você passará por duas telas de \"Confirmar dados\": a primeira para \"condenas parciais\" (1/2) e a segunda para \"condenas totais\" (2/2). Somente após sua confirmação final , o sistema irá gerar a planilha."),
            FaqQuestion("O aplicativo não reconheceu meu ábaco (erro \"Ábaco não detectado\"). O que devo fazer?", "O aplicativo exibe um aviso se o ábaco não for detectado. Para corrigir, certifique-se de que o ábaco está posicionado corretamente dentro da grade exibida na tela da câmera. Verifique também se a iluminação está adequada; se necessário, utilize o botão de flash."),
        )

        val adapterDuvidas = FaqQuestionAdapter(listaDuvidas)
        recyclerViewDuvidas.adapter = adapterDuvidas
    }

    private fun observeViewModel() {
        newsViewModel.news.observe(viewLifecycleOwner) { listaDeNoticias ->
            adapterPopulares.updateData(listaDeNoticias)
        }

        newsViewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
        }
    }
}