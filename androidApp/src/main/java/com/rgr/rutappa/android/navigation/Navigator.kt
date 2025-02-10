package com.rgr.rutappa.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rgr.rutappa.android.screen.DetailRoute
import com.rgr.rutappa.android.screen.LoginRoute
import com.rgr.rutappa.android.screen.MainRoute
import com.rgr.rutappa.android.screen.PartnerRoute
import com.rgr.rutappa.android.screen.SplashRoute
import com.rgr.rutappa.app.navigation.Routes


@Composable
fun Navigator(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Splash.name
    ) {
        composable(Routes.Splash.name) {
            SplashRoute(
                navigateTo = { navController.navigate(it.name)}
            )
        }
        composable(Routes.Login.name) {
            LoginRoute(
                navigateToMain = { navController.navigate(Routes.Main.name) }
            )
        }
        composable(Routes.Main.name) {
            MainRoute(
                navigateToDetail = {
                    navController.navigate(Routes.Detail.getDetail(it))
                },
                navigateToLogin = {
                    navController.navigate(Routes.Login.name)
                },
                navigateToPartners = {
                    navController.navigate(Routes.Partners.name)
                }
            )
        }
        composable(
            route = Routes.Detail.name,
            arguments = listOf(navArgument("tapa") { type = NavType.StringType })
        ) { backStackEntry ->
            val tapaId = backStackEntry.arguments?.getString("tapa") ?: ""
            DetailRoute(
                tapaId = tapaId,
                navigateToTapas = {
                    navController.navigate(Routes.Main.name)
                },
                navigateToPartners = {
                    navController.navigate(Routes.Partners.name)
                },
                navigateToLogout = {
                    navController.navigate(Routes.Login.name)
                }
            )
        }
        composable(
            route = Routes.Partners.name
        ) {
            PartnerRoute(
                navigateToTapa = {
                    navController.navigate(Routes.Main.name)
                },
                navigateToLogout = {
                    navController.navigate(Routes.Login.name)
                }
            )
        }
    }
}