package hristostefanov.starlingdemo.presentation.dependences

interface TokenStore {
    // TODO null means no token or mock service?
    var token: String?
}