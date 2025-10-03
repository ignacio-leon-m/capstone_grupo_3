package org.duocuc.capstone_backend.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RestController
class SessionDebugController {

    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    @GetMapping("/session", produces = [MediaType.TEXT_HTML_VALUE])
    fun sessionPage(request: HttpServletRequest): String {
        val session = request.getSession(false)
        val auth: Authentication? = SecurityContextHolder.getContext().authentication
        val loggedIn = auth?.isAuthenticated == true && auth.name != "anonymousUser"

        fun li(k: String, v: String) = "<li><b>$k:</b> $v</li>"

        val sb = StringBuilder()
        sb.append("<!doctype html><html><head><meta charset='utf-8'><title>Sesión</title>")
        sb.append("<style>body{font-family:Segoe UI,Arial;margin:24px} code{background:#f6f8fa;padding:2px 4px;border-radius:4px}</style>")
        sb.append("</head><body><h1>Estado de sesión</h1>")

        if (session == null || !loggedIn) {
            sb.append("<p><strong>No hay sesión autenticada.</strong></p>")
            sb.append("<p>Haz <code>POST /api/auth/login</code> y recarga.</p>")
        } else {
            sb.append("<ul>")
            sb.append(li("Usuario", auth?.name ?: ""))
            sb.append(li("Authorities", auth?.authorities?.joinToString { it.authority } ?: ""))
            sb.append(li("JSESSIONID", session.id))
            sb.append(li("Creada", fmt.format(Instant.ofEpochMilli(session.creationTime))))
            sb.append(li("Último acceso", fmt.format(Instant.ofEpochMilli(session.lastAccessedTime))))
            sb.append(li("Timeout (seg)", session.maxInactiveInterval.toString()))
            sb.append("</ul>")
        }

        sb.append("<hr/><p>")
        sb.append("<a href='/api/auth/logout' onclick=\"fetch('/api/auth/logout',{method:'POST',credentials:'include'}).then(()=>location.reload());return false;\">Logout</a>")
        sb.append(" · <a href='/api/users/me' target='_blank'>/api/users/me</a>")
        sb.append("</p></body></html>")
        return sb.toString()
    }
}
