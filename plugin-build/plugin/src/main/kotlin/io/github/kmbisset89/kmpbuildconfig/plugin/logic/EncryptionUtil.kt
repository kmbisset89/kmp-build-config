package io.github.kmbisset89.kmpbuildconfig.plugin.logic

import java.security.SecureRandom
import java.util.Base64

internal object EncryptionUtil {
    /**
     * NOTE:
     * - This is intentionally **not** "serious cryptography" (no AES/etc) because the generated decrypt code
     *   must work in KMP common code (no JVM crypto APIs).
     * - This is a fast keystream XOR with a per-value nonce and a keyed PRNG. It's substantially stronger than
     *   the previous Caesar-shift approach while staying very fast and dependency-free for consumers.
     *
     * Wire format (v1):
     *   kmbc1:<base64(nonce)>:<base64(ciphertext)>
     */
    private const val PREFIX = "kmbc1"
    private const val NONCE_SIZE_BYTES = 12
    private val rng = SecureRandom()

    fun encrypt(input: String, keyWord: String): String {
        val nonce = ByteArray(NONCE_SIZE_BYTES).also(rng::nextBytes)
        val plaintext = input.encodeToByteArray()
        val ciphertext = xorWithKeystream(plaintext, keyWord, nonce)

        val nonceB64 = Base64.getEncoder().encodeToString(nonce)
        val cipherB64 = Base64.getEncoder().encodeToString(ciphertext)
        return "$PREFIX:$nonceB64:$cipherB64"
    }

    private fun xorWithKeystream(data: ByteArray, keyWord: String, nonce: ByteArray): ByteArray {
        val out = ByteArray(data.size)

        // FNV-1a 64-bit over (keyWord UTF-8 bytes) + 0x00 + nonce bytes.
        var hash = -0x340d631b7bdddcdbL // 14695981039346656037u as signed
        val prime = 0x100000001b3L

        for (b in keyWord.encodeToByteArray()) {
            hash = hash xor (b.toLong() and 0xFFL)
            hash *= prime
        }
        hash = hash xor 0x00L
        hash *= prime
        for (b in nonce) {
            hash = hash xor (b.toLong() and 0xFFL)
            hash *= prime
        }

        fun splitMix64(x: Long): Long {
            val gamma = 0x9E3779B97F4A7C15uL.toLong()
            val mul1 = 0xBF58476D1CE4E5B9uL.toLong()
            val mul2 = 0x94D049BB133111EBuL.toLong()

            var z = x + gamma
            z = (z xor (z ushr 30)) * mul1
            z = (z xor (z ushr 27)) * mul2
            return z xor (z ushr 31)
        }

        // xoshiro256** (fast PRNG) seeded via splitmix64
        var seed = hash
        var s0 = splitMix64(seed).also { seed = it }
        var s1 = splitMix64(seed).also { seed = it }
        var s2 = splitMix64(seed).also { seed = it }
        var s3 = splitMix64(seed).also { seed = it }

        fun rotl(x: Long, k: Int): Long = (x shl k) or (x ushr (64 - k))

        fun nextLong(): Long {
            val result = rotl(s1 * 5L, 7) * 9L
            val t = s1 shl 17

            s2 = s2 xor s0
            s3 = s3 xor s1
            s1 = s1 xor s2
            s0 = s0 xor s3

            s2 = s2 xor t
            s3 = rotl(s3, 45)

            return result
        }

        var ks = 0L
        var ksIdx = 8 // force initial refill
        for (i in data.indices) {
            if (ksIdx >= 8) {
                ks = nextLong()
                ksIdx = 0
            }
            val k = ((ks ushr (ksIdx * 8)) and 0xFFL).toInt()
            out[i] = (data[i].toInt() xor k).toByte()
            ksIdx++
        }

        return out
    }
}
