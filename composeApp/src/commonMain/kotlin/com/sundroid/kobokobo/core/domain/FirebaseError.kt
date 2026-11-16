package com.sundroid.kobokobo.core.domain

sealed interface FirebaseError: Error {
    enum class Auth: FirebaseError {
        USER_NOT_FOUND,
        INVALID_CREDENTIALS,
        EMAIL_ALREADY_EXISTS,
        WEAK_PASSWORD,
        INVALID_EMAIL,
        USER_DISABLED,
        TOO_MANY_ATTEMPTS,
        NETWORK_ERROR,
        UNKNOWN
    }
    
    enum class Firestore: FirebaseError {
        PERMISSION_DENIED,
        NOT_FOUND,
        ALREADY_EXISTS,
        FAILED_PRECONDITION,
        ABORTED,
        OUT_OF_RANGE,
        UNIMPLEMENTED,
        INTERNAL,
        UNAVAILABLE,
        DEADLINE_EXCEEDED,
        UNAUTHENTICATED,
        RESOURCE_EXHAUSTED,
        CANCELLED,
        UNKNOWN
    }
    
    enum class Storage: FirebaseError {
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        INVALID_CHECKSUM,
        UNKNOWN
    }
}

