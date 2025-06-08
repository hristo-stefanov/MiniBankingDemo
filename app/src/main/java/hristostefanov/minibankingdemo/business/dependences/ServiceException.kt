package hristostefanov.minibankingdemo.business.dependences

open class ServiceException(message: String?): Exception(message)

class AuthException(message: String? = null): ServiceException(message)

class NetworkException(message: String?): ServiceException(message)

class APIException(message: String?): ServiceException(message)