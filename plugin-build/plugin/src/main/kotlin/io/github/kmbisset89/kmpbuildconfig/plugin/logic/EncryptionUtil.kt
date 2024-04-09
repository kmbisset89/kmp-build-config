package io.github.kmbisset89.kmpbuildconfig.plugin.logic

internal object EncryptionUtil {
    fun encrypt(input: String, keyWord: String): String {
        val shift = keyWord.sumOf { it.code } % 26 // Calculate shift based on keyWord
        return input.map { char ->
            when (char) {
                in 'A'..'Z' -> 'A' + (char.code - 'A'.code + shift) % 26
                in 'a'..'z' -> 'a' + (char.code - 'a'.code + shift) % 26
                else -> char
            }
        }.joinToString("")
    }
}
