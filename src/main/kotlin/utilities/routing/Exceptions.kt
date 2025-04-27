package me.keraktelor.utilities.routing

class MissingParameterException(paramNames: List<String>) :
    Exception("Missing required parameter(s): $paramNames")

class NoResponseNeededException : Exception()
