package com.projetointegrador.smartlocker

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.room.util.findColumnIndexBySuffix
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projetointegrador.smartlocker.databinding.FragmentUnidadeInfoBinding
import kotlinx.coroutines.tasks.await

class UnidadeInfoFragment : Fragment() {
    val bundle = Bundle()

    private var statusCartao : Boolean = false
    private lateinit var firestore: FirebaseFirestore
    private var _binding: FragmentUnidadeInfoBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()

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
            // Todo: COLOCAR ALGUM CHECK PRA VER SE O USUÁRIO JA TEM UM ALUGUEL

            possuiCartaoCadastrado(nomeUnidade)


        }

        binding.btnVoltar.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }
    }
    private fun possuiCartaoCadastrado( nomeUnidade: String){

        val idDoUsuarioAutenticado = FirebaseAuth.getInstance().currentUser!!.uid
        Log.d(TAG, "successo $idDoUsuarioAutenticado")

        firestore.collection("usuarios").document(idDoUsuarioAutenticado)
            .get().addOnSuccessListener { document ->
                statusCartao = document.getBoolean("cartao")!!
                Log.d(TAG, "successo $statusCartao dentro do successo")
                if (statusCartao==true){
                    //Iniciar activity rentlocker
                    activity?.let{
                        //armazenar nome da unidade no bundle
                        bundle.putString("uni", nomeUnidade)

                        val intent = Intent(it, RentLockerActivity::class.java)
                        intent.putExtras(bundle)
                        it.startActivity(intent)
                    }
                }else{
                    activity?.let{
                        val intent = Intent(it, ChangeActivity::class.java)
                        it.startActivity(intent)
                    }
                }
            }.addOnFailureListener {
                Log.d(TAG, "erro ao pegar os dados", it)
            }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
