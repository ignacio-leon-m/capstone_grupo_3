package org.duocuc.capstonebackend.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import java.util.UUID

class CustomUserDetails(
    val id: UUID,
    val email: String,
    password: String,
    authorities: Collection<GrantedAuthority>
) : User(email, password, authorities)