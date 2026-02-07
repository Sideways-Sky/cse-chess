package net.sidewayssky

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform