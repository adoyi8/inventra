package com.sundroid.kobokobo.core.domain

/**
 * Extension functions for converting between different error types
 */

/**
 * Converts a FirebaseError to DataError
 */
fun FirebaseError.toDataError(): DataError {
    return when (this) {
        is FirebaseError.Auth -> when (this) {
            FirebaseError.Auth.NETWORK_ERROR -> DataError.Remote.NO_INTERNET
            FirebaseError.Auth.USER_NOT_FOUND,
            FirebaseError.Auth.INVALID_CREDENTIALS,
            FirebaseError.Auth.EMAIL_ALREADY_EXISTS,
            FirebaseError.Auth.WEAK_PASSWORD,
            FirebaseError.Auth.INVALID_EMAIL,
            FirebaseError.Auth.USER_DISABLED,
            FirebaseError.Auth.TOO_MANY_ATTEMPTS -> DataError.Remote.SERVER
            FirebaseError.Auth.UNKNOWN -> DataError.Remote.UNKNOWN
        }
        is FirebaseError.Firestore -> when (this) {
            FirebaseError.Firestore.PERMISSION_DENIED,
            FirebaseError.Firestore.UNAUTHENTICATED -> DataError.Remote.EXPIRED_TOKEN
            FirebaseError.Firestore.NOT_FOUND -> DataError.Remote.SERVER
            FirebaseError.Firestore.ALREADY_EXISTS,
            FirebaseError.Firestore.FAILED_PRECONDITION,
            FirebaseError.Firestore.ABORTED,
            FirebaseError.Firestore.OUT_OF_RANGE,
            FirebaseError.Firestore.UNIMPLEMENTED,
            FirebaseError.Firestore.INTERNAL,
            FirebaseError.Firestore.UNAVAILABLE,
            FirebaseError.Firestore.DEADLINE_EXCEEDED,
            FirebaseError.Firestore.RESOURCE_EXHAUSTED,
            FirebaseError.Firestore.CANCELLED -> DataError.Remote.SERVER
            FirebaseError.Firestore.UNKNOWN -> DataError.Remote.UNKNOWN
        }
        is FirebaseError.Storage -> when (this) {
            FirebaseError.Storage.UNAUTHORIZED,
            FirebaseError.Storage.FORBIDDEN -> DataError.Remote.EXPIRED_TOKEN
            FirebaseError.Storage.NOT_FOUND,
            FirebaseError.Storage.INVALID_CHECKSUM,
            FirebaseError.Storage.UNKNOWN -> DataError.Remote.SERVER
        }
    }
}

/**
 * Converts a DataError to FirebaseError
 */
fun DataError.toFirebaseError(): FirebaseError {
    return when (this) {
        is DataError.Remote -> when (this) {
            DataError.Remote.NO_INTERNET -> FirebaseError.Auth.NETWORK_ERROR
            DataError.Remote.EXPIRED_TOKEN -> FirebaseError.Firestore.UNAUTHENTICATED
            DataError.Remote.SERVER,
            DataError.Remote.SERIALIZATION,
            DataError.Remote.REQUEST_TIMEOUT,
            DataError.Remote.TOO_MANY_REQUESTS,
            DataError.Remote.UNKNOWN -> FirebaseError.Auth.UNKNOWN
        }
        is DataError.Local -> when (this) {
            DataError.Local.DISK_FULL,
            DataError.Local.UNKNOWN -> FirebaseError.Firestore.INTERNAL
        }
    }
}

/**
 * Extension function to convert Result with FirebaseError to Result with DataError
 */
fun <T> Result<T, FirebaseError>.toDataErrorResult(): Result<T, DataError> {
    return when (this) {
        is Result.Success -> Result.Success(data)
        is Result.Error -> Result.Error(error.toDataError())
    }
}

/**
 * Extension function to convert Result with DataError to Result with FirebaseError
 */
fun <T> Result<T, DataError>.toFirebaseErrorResult(): Result<T, FirebaseError> {
    return when (this) {
        is Result.Success -> Result.Success(data)
        is Result.Error -> Result.Error(error.toFirebaseError())
    }
}

/**
 * Extension function to convert EmptyResult with FirebaseError to EmptyResult with DataError
 */
fun EmptyResult<FirebaseError>.toDataErrorEmptyResult(): EmptyResult<DataError> {
    return when (this) {
        is Result.Success -> Result.Success(Unit)
        is Result.Error -> Result.Error(error.toDataError())
    }
}

/**
 * Extension function to convert EmptyResult with DataError to EmptyResult with FirebaseError
 */
fun EmptyResult<DataError>.toFirebaseErrorEmptyResult(): EmptyResult<FirebaseError> {
    return when (this) {
        is Result.Success -> Result.Success(Unit)
        is Result.Error -> Result.Error(error.toFirebaseError())
    }
}














