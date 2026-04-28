package com.pocketflow.app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Maps stable string keys (saved in Room) to Material ImageVectors used by the UI.
 *
 * We cannot persist an `ImageVector` directly, so every category / goal stores a
 * key like "FOOD" or "FLIGHT" which we resolve at the boundary.
 */
object IconRegistry {

    private val map: Map<String, ImageVector> = mapOf(
        "FOOD"        to Icons.Filled.Restaurant,
        "SHOPPING"    to Icons.Filled.ShoppingBag,
        "TRANSPORT"   to Icons.Filled.DirectionsCar,
        "BILLS"       to Icons.Filled.Bolt,
        "ENTERTAIN"   to Icons.Filled.Movie,
        "HEALTH"      to Icons.Filled.MedicalServices,
        "EDUCATION"   to Icons.Filled.School,
        "OTHER"       to Icons.Filled.Category,
        "SALARY"      to Icons.Filled.Work,
        "FREELANCE"   to Icons.Filled.Computer,
        "INVESTMENT"  to Icons.Filled.TrendingUp,
        "GIFT"        to Icons.Filled.CardGiftcard,
        "RENTAL"      to Icons.Filled.Home,
        "WALLET"      to Icons.Filled.AccountBalanceWallet,
        "BANK"        to Icons.Filled.AccountBalance,
        "FLIGHT"      to Icons.Filled.Flight,
        "LAPTOP"      to Icons.Filled.Laptop,
        "HOUSE"       to Icons.Filled.House,
        "FLAG"        to Icons.Filled.Flag
    )

    /** Resolve key to vector; defaults to OTHER. */
    fun iconFor(key: String): ImageVector = map[key] ?: Icons.Filled.Category

    /** Reverse-look-up; returns "OTHER" if not found. */
    fun keyFor(icon: ImageVector): String =
        map.entries.firstOrNull { it.value == icon }?.key ?: "OTHER"
}
