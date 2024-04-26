package com.projetointegrador.smartlocker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.projetointegrador.smartlocker.databinding.FragmentUnidadeInfoBinding

class UnidadeInfoFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private var _binding: FragmentUnidadeInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUnidadeInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        val nomeUnidade = arguments?.getString("nomeUnidade")

        firestore.collection("unidades de locação").document(nomeUnidade!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    binding.tvNomeUnidade.text = "Unidade ${document.getString("nome")}"
                    binding.tvHorarioFuncionamento.text = document.getString("funcionamento")
                    binding.tvStatus.text = if (document.getBoolean("isClosed") == true) "Fechado" else "Aberto"
                    binding.tvEndereco.text = document.getString("endereco")
                }
            }

        binding.btnBeginRent.setOnClickListener {
            /*
            --(COLOCAR ALGUM CHECK PRA VER SE O USUÁRIO JA TEM UM ALUGUEL)

            --INTENT PRA PROX. ATIVIDADE PARA PROSSEGUIR COM O ALUGUEL
            --FINISH() // VAI DAR CERTO DENTRO DO FRAGMENT?? SÓ DEUS SABE
            */
        }

        binding.btnVoltar.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
