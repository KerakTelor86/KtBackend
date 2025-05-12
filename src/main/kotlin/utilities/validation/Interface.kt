package me.keraktelor.utilities.validation

data class ValidationRequirement(
    val description: String,
    val validate: () -> Boolean,
) {
    companion object Builder {
        fun verify(
            description: String,
            body: () -> Boolean,
        ): ValidationRequirement =
            ValidationRequirement(
                description = description,
                validate = body,
            )
    }
}

interface RequiresValidation {
    val requirements: List<ValidationRequirement>

    fun getFailedReasons(): List<String> =
        requirements.mapNotNull { (description, validate) ->
            val isValid = validate()
            when (isValid) {
                true -> null
                false -> description
            }
        }
}
