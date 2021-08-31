package com.andreformento.money.user.security

import com.andreformento.money.organization.OrganizationId
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.KeyPair
import java.time.Duration
import java.time.Instant
import java.util.*


data class SignedToken(val token: String)

data class ValidatedUserIdentification(val email: String, val organizationId: OrganizationId?) {
    constructor(jws: Jws<Claims>, organizationId: OrganizationId?) : this(email = jws.body.subject, organizationId)
}

@Service
class TokenSigner(
    @Value("\${security.key.private}") private val privateKeyConfig: String,
    @Value("\${security.key.public}") private val publicKeyConfig: String
) {
    private val keyPair: KeyPair = Keys.keyPairFor(SignatureAlgorithm.RS256)

    private val privateKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKeyConfig))
    private val publicKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(publicKeyConfig))

    private val jwtBuilder: JwtBuilder = Jwts.builder()
    private val jwtParser: JwtParser = Jwts.parserBuilder().setSigningKey(keyPair.public).build()

    fun createToken(email: String): SignedToken = jwtBuilder
        .signWith(keyPair.private, SignatureAlgorithm.RS256)
        .setSubject(email)
        .setIssuer("identity")
        .setExpiration(Date.from(Instant.now().plus(Duration.ofMinutes(15))))
        .setIssuedAt(Date.from(Instant.now()))
        .compact()
        .let { SignedToken(it) }

    /**
     * Validate the JWT where it will throw an exception if it isn't valid.
     */
    fun validateIdentification(rawToken: String, organizationId: OrganizationId?): ValidatedUserIdentification? =
        try {
            ValidatedUserIdentification(jwtParser.parseClaimsJws(rawToken), organizationId)
        } catch (e: Exception) {
            // TODO metrics about invalid token
            null
        }

}
