package com.projetointegrador.smartlocker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.content.Context

class CardAdapter(private val cards: MutableList<CreditCard>, private val context: Context) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardNumber: TextView = itemView.findViewById(R.id.etCardNumber)
        val cardCpf: TextView = itemView.findViewById(R.id.etCardCpf)
        val cardCvv: TextView = itemView.findViewById(R.id.etCardCvv)
        val cardValidity: TextView = itemView.findViewById(R.id.etCardExpirity)
        val cardName: TextView = itemView.findViewById(R.id.etCardHolder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_lista_cartao, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.cardNumber.text = card.cardNumber
        holder.cardCpf.text = card.cardCpf
        holder.cardCvv.text = card.cardCvv
        holder.cardValidity.text = card.cardValidity
        holder.cardName.text = card.cardName
    }

    override fun getItemCount(): Int {
        return cards.size
    }
}