package plugins

class RequestValidationException(
    val reasons: List<String>,
) : IllegalArgumentException() {
    override val message: String
        get() = "Request did not pass validation"
}

data class ValidationRequirement(
    val description: String,
    val validate: suspend () -> Boolean,
) {
    companion object Builder {
        fun validate(
            description: String,
            body: suspend () -> Boolean,
        ): ValidationRequirement =
            ValidationRequirement(
                description = description,
                validate = body,
            )
    }
}

interface RequiresValidation {
    val requirements: List<ValidationRequirement>

    private suspend fun getFailedReasons(): List<String> =
        requirements.mapNotNull { (description, validate) ->
            val isValid = validate()
            when (isValid) {
                true -> null
                false -> description
            }
        }

    suspend fun validate() {
        val failedReasons = getFailedReasons()
        if (failedReasons.isNotEmpty()) {
            throw RequestValidationException(reasons = failedReasons)
        }
    }
}
