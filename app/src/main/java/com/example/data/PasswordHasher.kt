package com.example.data

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Local-only password hashing for the prototype.
 * Production authentication should be moved to a dedicated backend provider.
 */
object PasswordHasher {
    private const val SALT_BYTES = 16
    private const val HASH_BITS = 256
    private const val ITERATIONS = 120_000

    fun newSalt(): String {
        val salt = ByteArray(SALT_BYTES)
        SecureRandom().nextBytes(salt)
        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    fun hash(password: String, saltBase64: String): String {
        val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_BITS)
        return try {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            Base64.encodeToString(factory.generateSecret(spec).encoded, Base64.NO_WRAP)
        } finally {
            spec.clearPassword()
        }
    }

    fun verify(password: String, saltBase64: String, expectedHash: String): Boolean {
        val actual = hash(password, saltBase64)
        return MessageDigest.isEqual(
            Base64.decode(actual, Base64.NO_WRAP),
            Base64.decode(expectedHash, Base64.NO_WRAP)
        )
    }
}
