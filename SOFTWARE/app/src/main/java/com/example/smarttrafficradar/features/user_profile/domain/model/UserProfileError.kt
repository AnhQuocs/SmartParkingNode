package com.example.smarttrafficradar.features.user_profile.domain.model

sealed class UserProfileError(message: String? = null) : Exception(message) {
    object IdentifierAlreadyExists : UserProfileError("Identifier already exists")
    object IdentifierNotFound : UserProfileError("Identifier not found in organization")
    object ProfileNotFound : UserProfileError("User profile not found")
    object DebtLimitExceeded : UserProfileError("Debt limit exceeded")
    object InvalidFullName : UserProfileError("Full name is invalid")
    object NetworkError : UserProfileError("Network connection error")
    
    data class UnknownError(val msg: String) : UserProfileError(msg)
}
