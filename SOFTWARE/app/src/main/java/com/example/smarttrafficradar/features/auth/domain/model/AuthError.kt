package com.example.smarttrafficradar.features.auth.domain.model

sealed class AuthError : Exception() {
    class EmptyUsername : AuthError()
    class InvalidEmail : AuthError()
    class EmptyPassword : AuthError()
    class PasswordTooShort : AuthError()
    class EmptyAdminCode : AuthError()
    class EmptyUserId : AuthError()
    
    // Remote/Repository errors
    data class RemoteError(override val message: String?) : AuthError()
    class UserNotFound : AuthError()
    class WrongPassword : AuthError()
    class EmailAlreadyInUse : AuthError()
    class InvalidAdminCode : AuthError()
    class NetworkError : AuthError()
    class UnknownError : AuthError()
}