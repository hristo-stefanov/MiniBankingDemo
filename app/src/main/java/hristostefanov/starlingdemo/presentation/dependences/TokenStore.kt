package hristostefanov.starlingdemo.presentation.dependences

interface TokenStore {
    // null value means using a mack service
    var token: String?
}