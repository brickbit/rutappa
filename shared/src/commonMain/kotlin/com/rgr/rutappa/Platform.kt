package com.rgr.rutappa

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform