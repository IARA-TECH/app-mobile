package com.mobile.app_iara.ui.status

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.mobile.app_iara.R

class LoadingFragment : Fragment() {

    private lateinit var tvLoadingText: TextView
    private lateinit var animationView: LottieAnimationView
    private val handler = Handler(Looper.getMainLooper())
    private var textChangeRunnable: Runnable? = null

    private val loadingPhrases = listOf(
        "Estamos quase lá...",
        "Processando sua imagem...",
        "Analisando o ábaco...",
        "Quase terminando...",
        "Preparando resultados...",
        "Últimos ajustes...",
        "Gerando relatório...",
        "Otimizando detecção...",
        "Calculando valores...",
        "Validando dados...",
        "Processando cores...",
        "Analisando contas...",
        "Convertendo imagem...",
        "Aplicando filtros...",
        "Detectando padrões...",
        "Extraindo informações...",
        "Processando em 90%...",
        "Finalizando análise...",
        "Quase pronto...",
        "Preparando visualização...",
        "Organizando dados...",
        "Criando relatório...",
        "Salvando resultados...",
        "Última verificação...",
        "Processamento em andamento...",
        "Analisando estrutura...",
        "Verificando precisão...",
        "Otimizando qualidade...",
        "Processando contagens...",
        "Gerando estatísticas...",
        "Preparando download...",
        "Convertendo para CSV...",
        "Validando formato...",
        "Ajuste fino em progresso...",
        "Processamento concluído em 95%...",
        "Finalizando...",
        "Pronto em instantes!"
    )

    private var currentPhraseIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvLoadingText = view.findViewById(R.id.tvLoadingText)
        animationView = view.findViewById(R.id.animation_view)

        startLoadingAnimation()
    }

    private fun startLoadingAnimation() {
        updateLoadingText()

        textChangeRunnable = object : Runnable {
            override fun run() {
                currentPhraseIndex = (currentPhraseIndex + 1) % loadingPhrases.size
                updateLoadingText()

                handler.postDelayed(this, 3000)
            }
        }

        handler.postDelayed(textChangeRunnable!!, 3000)
    }

    private fun updateLoadingText() {
        tvLoadingText.text = loadingPhrases[currentPhraseIndex]

        // Adiciona uma animação simples de fade
        tvLoadingText.alpha = 0f
        tvLoadingText.animate()
            .alpha(1f)
            .setDuration(500)
            .start()
    }

    fun stopLoading() {
        textChangeRunnable?.let {
            handler.removeCallbacks(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopLoading()
    }

    companion object {
        fun newInstance(): LoadingFragment {
            return LoadingFragment()
        }
    }
}