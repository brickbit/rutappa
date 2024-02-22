package com.rgr.rutappa.app.navigation

sealed class Routes(val name: String) {
    data object Splash : Routes("Splash")
    data object Login : Routes("Login")
    data object Main : Routes("Main")
    data object Detail : Routes("Detail/{tapa}") {
        fun getTapa(tapa:String): String = tapa
        fun getDetail(tapa: String): String = "Detail/$tapa"
    }
}