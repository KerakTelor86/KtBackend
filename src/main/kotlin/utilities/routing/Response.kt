@file:Suppress("unused")

package utilities.routing

import io.ktor.http.*
import kotlin.reflect.KClass

object StatusCode {
    @Target(AnnotationTarget.CLASS)
    annotation class Ok

    @Target(AnnotationTarget.CLASS)
    annotation class Created

    @Target(AnnotationTarget.CLASS)
    annotation class BadRequest

    @Target(AnnotationTarget.CLASS)
    annotation class Unauthorized

    @Target(AnnotationTarget.CLASS)
    annotation class Forbidden

    @Target(AnnotationTarget.CLASS)
    annotation class Conflict

    @Target(AnnotationTarget.CLASS)
    annotation class TooManyRequests

    @Target(AnnotationTarget.CLASS)
    annotation class InternalServerError
}

private val annotationStatusCodes = mapOf(
    StatusCode.Ok::class to HttpStatusCode.OK,
    StatusCode.Created::class to HttpStatusCode.Created,
    StatusCode.BadRequest::class to HttpStatusCode.BadRequest,
    StatusCode.Unauthorized::class to HttpStatusCode.Unauthorized,
    StatusCode.Forbidden::class to HttpStatusCode.Forbidden,
    StatusCode.Conflict::class to HttpStatusCode.Conflict,
    StatusCode.TooManyRequests::class to HttpStatusCode.TooManyRequests,
    StatusCode.InternalServerError::class to HttpStatusCode.InternalServerError,
)

@PublishedApi
internal fun <T : Any> getStatusCode(kClass: KClass<T>): HttpStatusCode {
    for (annotation in kClass.annotations) {
        val code = annotationStatusCodes[annotation.annotationClass]
        if (code != null) {
            return code
        }
    }

    throw IllegalArgumentException(
        "Class ${
            kClass.qualifiedName
        } does not support automatic documentation",
    )
}
