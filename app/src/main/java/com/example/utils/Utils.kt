package com.example.utils

import java.text.NumberFormat
import java.util.Locale

object FormattingUtils {
    fun formatInINR(amount: Double): String {
        return try {
            val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            formatter.maximumFractionDigits = 2
            formatter.format(amount).replace("INR", "₹").trim()
        } catch (e: Exception) {
            "₹ ${String.format(Locale.ENGLISH, "%.2f", amount)}"
        }
    }
}

object ValidationUtils {
    fun isValidIndianPhone(phone: String): Boolean {
        val cleanPhone = phone.replace(Regex("[^0-9]"), "")
        return cleanPhone.length == 10 && (cleanPhone.startsWith("6") || cleanPhone.startsWith("7") || cleanPhone.startsWith("8") || cleanPhone.startsWith("9"))
    }

    fun isValidFlatNo(flatNo: String): Boolean {
        return flatNo.isNotBlank() && flatNo.trim().length >= 2
    }
}

object GoaSocietyActCompliance {
    const val ACT_TITLE = "The Goa Cooperative Societies Act, 2001 (Act 31 of 2001)"
    
    val SUMMARY_POINTS = listOf(
        "Section 73: Right of resident member to inspect Bye-laws, register of members, and financial statements.",
        "Section 84: Obligation for prompt payment of monthly maintenance dues within 15 days of bill issuance.",
        "Rule 42: Requirement of mandatory NOC from Housing Society Managing Committee for Flat Resale or Tenancy.",
        "Section 91: Dispute settlement framework through Co-operative Registrar for housing grievances.",
        "Bye-Law 14(a): Designated visitor parking rules and mandatory vehicle sticker registration for residents."
    )
}
