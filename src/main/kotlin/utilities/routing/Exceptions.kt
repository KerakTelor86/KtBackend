package me.keraktelor.utilities.routing

class MissingParametersException(paramNames: List<String>) :
    Exception("Missing required parameter(s): $paramNames")

class MalformedParametersException(cause: Throwable) :
    Exception("Malformed parameters passed", cause)

class NoResponseNeededException : Exception()
