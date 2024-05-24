package com.projetointegrador.smartlocker

data class CreditCard(
    val cardNumber: String = "",
    val cardCpf: String = "",
    val cardCvv: String = "",
    val cardValidity: String = "",
    val cardName: String = ""
)