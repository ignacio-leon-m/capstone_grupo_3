package org.duocuc.capstonebackend.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtTokenService(
    @param:Value("\${jwt.secret}")
    private val secret: String,
    @param:Value("\${jwt.expiration:86400000}")
    private val expirationMillis: Long
) {
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
    }

    fun generateToken(userDetails: UserDetails): String {
        val roles = userDetails.authorities.map { it.authority }
        val now = Date(System.currentTimeMillis())
        val expiry = Date(now.time + expirationMillis)

        return Jwts.builder()
            .subject(userDetails.username)
            .claim("roles", roles)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun extractUsername(token: String): String =
        extractAllClaims(token).subject

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val claims = extractAllClaims(token)
        val username = claims.subject
        val isExpired = claims.expiration.before(Date())
        return username == userDetails.username && !isExpired
    }

    fun extractAllClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

    private fun isExpired(token: String): Boolean =
        try {
            extractAllClaims(token).expiration.before(Date())
        } catch (e: Exception) {
            true
        }
}