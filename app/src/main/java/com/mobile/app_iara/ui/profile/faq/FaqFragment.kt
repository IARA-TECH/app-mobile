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
import com.mobile.app_iara.databinding.FragmentFaqBinding
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.ui.status.LoadingApiFragment
import com.mobile.app_iara.util.NetworkUtils

class FaqFragment : Fragment() {

    private var _binding: FragmentFaqBinding? = null
    private val binding get() = _binding!!

    private val newsViewModel: NewsViewModel by viewModels()

    private lateinit var adapterPopulares: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFaqBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .add(R.id.loading_container, LoadingApiFragment.newInstance())
                .commit()
        }

        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            val intent = Intent(requireContext(), WifiErrorActivity::class.java)
            startActivity(intent)
            activity?.finish()
            return
        }

        binding.btnVoltar4.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.recyclerPopulares.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        adapterPopulares = NewsAdapter(emptyList())
        binding.recyclerPopulares.adapter = adapterPopulares

        observeViewModel()

        binding.loadingContainer.visibility = View.VISIBLE
        newsViewModel.fetchNews(listOf("indústria avícola", "inovação industrial", "automatização de linha de produção"))

        binding.recyclerDuvidas.layoutManager = LinearLayoutManager(requireContext())

        val listaDuvidas = listOf(
            FaqQuestion("Como posso realizar meu primeiro acesso ao aplicativo?", "Para efetuar o primeiro login, clique em \"Acessando pela primeira vez? Clique aqui\" na tela inicial. Em seguida, insira o e-mail cadastrado pelo administrador da sua indústria e defina uma nova senha de acesso."),
            FaqQuestion("O que devo fazer se esquecer minha senha?", "Na tela inicial, selecione \"Esqueceu sua senha?\" e informe o e-mail ou CPF vinculado à sua conta. Um e-mail será enviado com as instruções para redefinição da senha. Basta seguir as orientações e cadastrar uma nova senha para voltar a acessar o aplicativo."),
            FaqQuestion("Quais métodos de login estão disponíveis no aplicativo?", "Atualmente, o acesso pode ser realizado por e-mail, CPF ou conta Google."),
            FaqQuestion("Posso fazer login com uma conta Microsoft?", "Não. No momento, o aplicativo permite login apenas por e-mail, CPF ou conta Google."),
            FaqQuestion("Um usuário excluído ainda consegue acessar o aplicativo?", "Não. Após a exclusão, o acesso à conta é imediatamente bloqueado, impossibilitando qualquer novo login com as credenciais anteriores."),
            FaqQuestion("Enviei um e-mail para o suporte, mas não recebi retorno.", "Verifique se o e-mail utilizado para o envio é o mesmo cadastrado no aplicativo. Caso não seja, o suporte não poderá identificar sua solicitação. Confirme também se o e-mail foi enviado para o endereço correto: \"IaraAppSuporte@gmail.com\". Se os dados estiverem corretos e o prazo de 5 dias úteis já tiver passado, reenvie o e-mail."),
            FaqQuestion("Como cadastrar novos usuários da indústria?", "Para cadastrar um novo usuário, é necessário possuir permissão de administrador. Acesse a aba \"Gestão\" no aplicativo, clique no ícone \"+\" localizado ao lado direito da barra de busca e preencha as informações do colaborador conforme solicitado."),
            FaqQuestion("É possível recuperar um usuário excluído?", "Sim. Para recuperar uma conta excluída, um administrador da fábrica deve entrar em contato com o suporte do Iara pelo e-mail \"IaraAppSuporte@gmail.com\", utilizando seu e-mail corporativo cadastrado no aplicativo. O prazo para retorno é de até 5 dias úteis."),
            FaqQuestion("Como posso alterar as informações da fábrica?", "As alterações nas informações da fábrica devem ser solicitadas por um administrador por meio do e-mail de suporte \"IaraAppSuporte@gmail.com\", utilizando seu e-mail corporativo cadastrado no aplicativo. O prazo para retorno é de até 5 dias úteis."),
            FaqQuestion("Como eu posso renovar ou cancelar o plano da indústria?", "Para renovar ou cancelar o plano da indústria você deve se administrador da indústria e entrar em contato com o suporte do Iara pelo e-mail \"IaraAppSuporte@gmail.com\", utilizando seu e-mail corporativo cadastrado no aplicativo. O prazo para retorno é de até 5 dias úteis."),
            FaqQuestion("Como posso excluir um usuário?", "Para excluir um usuário do aplicativo é necessário possuir permissão de administrador. Acesse a aba \"Gestão\", selecione o usuário que deseja remover e clique no ícone de lixeira localizado no canto superior direito. Confirme a exclusão somente se tiver certeza, pois a recuperação da conta só é possível mediante solicitação ao suporte do IARA."),
            FaqQuestion("Como posso acessar a página de FAQ?", "Para acessar a página de FAQ, clique na aba \"Perfil\" e selecione a opção \"FAQ\". Nessa página, você encontrará as perguntas frequentes e também poderá visualizar as notícias mais recentes."),
            FaqQuestion("Onde posso encontrar as informações da fábrica?", "Para visualizar as informações da fábrica, acesse a aba \"Perfil\" e selecione \"Fábrica\". Nessa página, são exibidos o nome, CNPJ, domínio e endereço da fábrica."),
            FaqQuestion("Onde posso encontrar notícias sobre a indústria avícola?", "Para acessar notícias em tempo real sobre a indústria avícola, entre na aba \"Perfil\" e clique em \"FAQ\". As notícias estarão disponíveis na parte superior da página."),
            FaqQuestion("Onde posso encontrar os termos e condições?", "Para acessar os termos e condições do aplicativo, clique na aba \"Perfil\" e selecione a opção \"Termos e Condições\"."),
            FaqQuestion("Como posso alterar minha senha?", "Para alterar sua senha, acesse \"Perfil\", depois \"Configuração\" e selecione \"Senha\". Insira seu e-mail empresarial cadastrado no aplicativo e siga as instruções enviadas por e-mail para redefinir a senha."),
            FaqQuestion("Os dados da indústria estão seguros?", "Sim. Os dados das indústrias estão protegidos no aplicativo. O Iara não compartilha informações com terceiros."),
            FaqQuestion("É possível alterar os horários dos turnos?", "Não. Os horários dos turnos são definidos diretamente no sistema e não podem ser alterados manualmente."),
            FaqQuestion("Como cadastrar um novo ábaco?", "Para cadastrar um novo ábaco, é necessário possuir permissão de administrador ou supervisor. Na página inicial \"Home\", clique em \"Ábacos\" e depois no ícone \"+\" localizado no canto superior direito. Em seguida, preencha as informações solicitadas, como nome, descrição, colunas e linhas do ábaco."),
            FaqQuestion("Qual a diferença entre condenas de falha técnicas e condenas pela granja?", "As condenas por falhas técnicas são aquelas que ocorrem dentro da indústria, resultantes de problemas no processo industrial, como falhas operacionais ou de equipamentos. Já as condenas pela granja são causadas por fatores externos, relacionados aos fornecedores, como problemas de manejo, transporte ou condições inadequadas das aves antes da chegada à indústria."),
            FaqQuestion("Como posso cadastrar uma nova contagem?", "Para cadastrar uma nova contagem, é necessário possuir permissão de solicitante. Acesse a página inicial \"Home\", clique no ícone de scanner no canto inferior direito e selecione o ábaco correspondente. Tire uma foto nítida contendo todas as colunas e miçangas do ábaco ou envie uma imagem da galeria. Após o envio, o sistema realizará a contagem automaticamente. Verifique se os valores de cada linha estão corretos e faça os ajustes, se necessário. O supervisor da área será notificado para aprovar ou recusar sua solicitação."),
            FaqQuestion("O que devo fazer caso o scanner não consiga identificar o ábaco na foto?", "Caso o scanner não consiga identificar o ábaco, verifique a iluminação do ambiente, a nitidez da imagem e a conexão com a internet. Se o problema persistir, entre em contato com o suporte do Iara pelo e-mail \"IaraAppSuporte@gmail.com\", utilizando seu e-mail corporativo cadastrado no aplicativo. O prazo para retorno é de até 5 dias úteis."),
            FaqQuestion("Como posso aceitar uma solicitação de contagem de ábaco?", "Para aceitar ou recusar uma solicitação, é necessário possuir permissão de supervisor. Acesse a página de notificações clicando no ícone de sino no canto superior direito da página inicial. Lá, você encontrará as solicitações pendentes e as já respondidas."),
            FaqQuestion("Como posso visualizar os resultados das contagens?", "Os resultados das contagens podem ser visualizados na aba \"Dados\", que apresenta gráficos, insights e informações relevantes sobre a indústria. Caso prefira acessar os dados brutos, entre na área \"Planilhas\" na página inicial \"Home\", onde estarão disponíveis as planilhas de cada turno e de cada ábaco."),
            FaqQuestion("Qual é a diferença entre os registros da página \"Histórico\" e da página \"Planilhas\"?", "Na página \"Planilhas\", as informações são organizadas por turnos, enquanto na aba \"Histórico\" os registros correspondem a cada contagem individual realizada. Em \"Planilhas\", as contagens individuais são agrupadas por turno e por ábaco."),
            FaqQuestion("Quais informações posso encontrar na página \"Dados\"?", "Na página \"Dados\", estão disponíveis informações e análises sobre as condenas por falhas técnicas e as condenas pela granja. Esses dados são essenciais para a indústria, pois ajudam a identificar a origem das perdas e a entender se elas estão relacionadas ao processo interno ou aos fornecedores. Com isso, é possível aprimorar a produção, reduzir desperdícios e aumentar a eficiência operacional.")
        )

        val adapterDuvidas = FaqQuestionAdapter(listaDuvidas)
        binding.recyclerDuvidas.adapter = adapterDuvidas
    }

    private fun observeViewModel() {
        newsViewModel.news.observe(viewLifecycleOwner) { listaDeNoticias ->
            binding.loadingContainer.visibility = View.GONE
            adapterPopulares.updateData(listaDeNoticias)
        }

        newsViewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            binding.loadingContainer.visibility = View.GONE
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
