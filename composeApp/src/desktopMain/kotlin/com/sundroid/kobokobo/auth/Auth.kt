package com.sundroid.kobokobo.auth

import dev.gitlive.firebase.auth.FirebaseUser

// This file assumes it is placed in your 'desktopMain' source set.
// It uses standard JVM libraries for networking and browser interaction.


import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.awt.Desktop
import java.net.ServerSocket
import java.net.URI
import java.net.URLEncoder
import java.security.SecureRandom
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

// --- IMPORTANT: Replace with your credentials ---
// 1. Get a "Desktop App" OAuth 2.0 Client ID from Google Cloud Console.
private const val DESKTOP_CLIENT_ID = "926103425935-6u9cpuhuhvkpdr4cosg2m9uqfiirk199.apps.googleusercontent.com"
// 2. Get the Client Secret for the "Desktop App" credential.
private const val DESKTOP_CLIENT_SECRET = "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"

private const val SCOPE = "openid email profile"
private const val AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth"
private const val TOKEN_URL = "https://oauth2.googleapis.com/token"



// The actual implementation for the Desktop target
actual class GoogleAuthUiProvider {

    /**
     * Desktop implementation using Authorization Code Flow (PKCE).
     * This starts a local HTTP server to capture the redirect from the user's browser.
     */
    actual suspend fun signIn(): FirebaseUser? {
        return withContext(Dispatchers.IO) {
            // 1. Prepare PKCE and Redirect URI
            val (codeVerifier, codeChallenge) = generatePkce()
            val state = UUID.randomUUID().toString()

            val localServer: ServerSocket
            val redirectUri: String
            try {
                // Find a free port and construct the redirect URI
                localServer = ServerSocket(0)
                val port = localServer.localPort
                redirectUri = "http://localhost:$port/callback"
            } catch (e: Exception) {
                throw IllegalStateException("Failed to start local server for OAuth redirect.", e)
            }

            // 2. Construct Authorization URL
            val authUrl = buildString {
                append(AUTH_URL)
                append("?client_id=").append(urlEncode(DESKTOP_CLIENT_ID))
                append("&redirect_uri=").append(urlEncode(redirectUri))
                append("&response_type=code")
                append("&scope=").append(urlEncode(SCOPE))
                append("&state=").append(urlEncode(state))
                append("&code_challenge=").append(urlEncode(codeChallenge))
                append("&code_challenge_method=S256")
            }

            // 3. Launch browser and listen for redirect (blocking call)
            val authCode = suspendCancellableCoroutine<String> { continuation ->
                if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    continuation.resumeWithException(UnsupportedOperationException("Desktop browser interaction is not supported."))
                    return@suspendCancellableCoroutine
                }

                try {
                    val listenerJob = Thread {
                        try {
                            Desktop.getDesktop().browse(URI(authUrl))

                            // Listen for the single connection (the redirect)
                            localServer.use { server ->
                                server.soTimeout = 60000 // 60-second timeout
                                val socket = server.accept()
                                socket.use { sock ->
                                    val reader = sock.getInputStream().bufferedReader()
                                    val writer = sock.getOutputStream().bufferedWriter()

                                    val requestLine = reader.readLine()

                                    // Check for cancellation
                                    if (continuation.isCancelled) return@Thread

                                    // Parse query parameters from the request line
                                    val query = requestLine.split(" ")[1].split("?").getOrNull(1)
                                    val params = query?.split("&")?.associate {
                                        val parts = it.split("=")
                                        urlDecode(parts[0]) to urlDecode(parts.getOrNull(1) ?: "")
                                    } ?: emptyMap()

                                    val receivedCode = params["code"]
                                    val receivedState = params["state"]

                                    // Send minimal HTTP response to show success and allow the window to close
                                    val successPage = "<html><head><title>Success</title></head><body><h1>Authentication Complete!</h1><p>You can close this window now.</p></body></html>"
                                    writer.write("HTTP/1.1 200 OK\r\n")
                                    writer.write("Content-Type: text/html; charset=utf-8\r\n")
                                    writer.write("Content-Length: ${successPage.length}\r\n")
                                    writer.write("\r\n")
                                    writer.write(successPage)
                                    writer.flush()

                                    // Validate and resume
                                    if (receivedCode != null && receivedState == state) {
                                        continuation.resume(receivedCode)
                                    } else {
                                        val error = params["error"] ?: "Authorization failed."
                                        continuation.resumeWithException(Exception("OAuth Error: $error"))
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            if (continuation.isActive) {
                                continuation.resumeWithException(e)
                            }
                        } finally {
                            // Ensure the server is closed
                            if (!localServer.isClosed) {
                                try { localServer.close() } catch (e: Exception) { /* ignore */ }
                            }
                        }
                    }
                    listenerJob.isDaemon = true
                    listenerJob.start()

                    continuation.invokeOnCancellation {
                        listenerJob.interrupt()
                        try { localServer.close() } catch (e: Exception) { /* ignore */ }
                    }
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }

            // 4. Exchange Code for Tokens
            val idToken = exchangeCodeForIdToken(authCode, codeVerifier, redirectUri)

            // 5. Authenticate with Firebase using ID Token (same as Android)
            return@withContext if (idToken != null) {
                try {
                    Firebase.auth.signInWithCredential(GoogleAuthProvider.credential(idToken, null)).user
                } catch (e: Exception) {
                    println("Firebase Sign-In Error: ${e.message}")
                    null
                }
            } else {
                null
            }
        }
    }

    /**
     * Desktop sign out implementation (only Firebase Auth sign out is necessary).
     */
    actual suspend fun signOut() {
        Firebase.auth.signOut()
    }

    // --- Utility Functions ---

    /**
     * Generates PKCE code verifier and challenge.
     */
    private fun generatePkce(): Pair<String, String> {
        val verifierBytes = ByteArray(32)
        SecureRandom().nextBytes(verifierBytes)
        // Base64 URL Safe encoding without padding
        val verifier = Base64.getUrlEncoder().withoutPadding().encodeToString(verifierBytes)

        val sha256 = java.security.MessageDigest.getInstance("SHA-256")
        val challengeBytes = sha256.digest(verifier.toByteArray(Charsets.US_ASCII))
        val challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(challengeBytes)

        return Pair(verifier, challenge)
    }

    /**
     * Exchanges the authorization code for an ID Token via HTTPS POST request.
     */
    private suspend fun exchangeCodeForIdToken(
        code: String,
        codeVerifier: String,
        redirectUri: String
    ): String? {
        val body = buildString {
            append("code=").append(urlEncode(code))
            append("&client_id=").append(urlEncode(DESKTOP_CLIENT_ID))
            append("&client_secret=").append(urlEncode(DESKTOP_CLIENT_SECRET))
            append("&redirect_uri=").append(urlEncode(redirectUri))
            append("&grant_type=authorization_code")
            append("&code_verifier=").append(urlEncode(codeVerifier))
        }

        // Using standard JVM HttpClient for the network call
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI(TOKEN_URL))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()

        return try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() == 200) {
                // Simple string parsing to extract id_token (use kotlinx.serialization in a real app)
                val json = response.body()
                val idToken = json.substringAfter("\"id_token\":\"").substringBefore("\"")
                idToken
            } else {
                println("Token Exchange failed: HTTP ${response.statusCode()} - ${response.body()}")
                null
            }
        } catch (e: Exception) {
            println("Token Exchange network error: ${e.message}")
            null
        }
    }

    private fun urlEncode(s: String): String = URLEncoder.encode(s, "UTF-8")
    private fun urlDecode(s: String): String = java.net.URLDecoder.decode(s, "UTF-8")
}
