package io.github.kmbisset89.kmpbuildconfig.plugin.logic

internal object EncryptionUtil {
    fun encrypt(input: String, keyWord: String): String {
        val shift = keyWord.sumOf { it.code }.mod(26) // Calculate shift using .mod
        return input.map { char ->
            when (char) {
                in 'A'..'Z' -> 'A' + ((char.code - 'A'.code + shift).mod(26))
                in 'a'..'z' -> 'a' + ((char.code - 'a'.code + shift).mod(26))
                else -> char
            }.toChar()
        }.joinToString("")
    }
}
