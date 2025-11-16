# Login session Architectural Decision Record

## Problem state

```kotlin
@LoginSessionScope
class LoginSessionData @Inject constructor() {
    val summary = MutableStateFlow<Summary?>(null)
}
```

```kotlin
@LoginSessionScope
@Subcomponent(modules = [LoginSessionModule::class])
interface LoginSessionComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(
            @AccessToken
            @BindsInstance
            token: String,
            @TokenType
            @BindsInstance
            tokenType: String
        ): LoginSessionComponent
    }

    val data: LoginSessionData
}
```

```kotlin
class LoginSessionRegistry @Inject constructor( private val loginSessionComponentFactory: LoginSessionComponent.Factory ) {
    var component: LoginSessionComponent? = null

    val requireComponent: LoginSessionComponent get() = component ?: throw IllegalStateException("No login session")

    fun createSession(token: String, tokenType: String) { component = loginSessionComponentFactory.create(token, tokenType) }

    fun close() { component = null }
}
```

1. Observing login session data is problematic, like in this view model:

```kotlin
@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val loginSessionRegistry: LoginSessionRegistry,
) : ViewModel() {
    private val summary: StateFlow<Summary?>
        get() = loginSessionRegistry.requireComponent.data.summary
}
```

* It's hard to use AccountsViewModel without a login session or when the login session is replaced with another, because
    * `requireComponent` will throw an exception when there is no login session - AccountsViewModel cannot be used
without a login session.
    * when replacing one login session data instance with another, observers will need to re-subscribe - it's AccountViewModel

2. For some interactors, having a login session is precondition which needs to be ensured before invoking it.
However if auto-closing a login session due to user inactivity is implemented, such interactors may fail
during execution.

3. The dependences provided by `LoginSessionComponent` and its modules cannot be injected directly into `ViewModel`, `Fragment` 
or `Activity`. The reason is the rigid component hierarchy of hilt which doesn't allow inserting `LoginSessionComponent`
between `SignletonComponent` and another stock components such as `ViewModelComponent`. 

For this reason we CANNOT have:
```kotlin
@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val loginSessionData: LoginSessionData,
) : ViewModel() {
}
```
and we need to access such dependencies by injecting `LoginSessionRegistry`.  

## Goal state

* It's easy and trouble-free to observe login session data in a view models.
* It's each and trouble free to implement auto-closing a login session due to user inactivity.
* Login session scoped dependencies can be injected directly


## Potential solutions and mitigations

### Having an anonymous user login session

This way `SessionRegistry.component` will not be optional/nullable.

### No LoginSessionComponent - login session data is globally scoped

This would require explicitly clearing all login session data in the *whole* dependency graph when logging out,
i.e. including in Retrofit instance and cache files (if enabled).
The `Closeable` interface can be used to help with that.

Another downside is that all session data properties will need to be optional/nullable for the sake of handling the 
case of not having a login session. Thus in expressions where more that one property is need, we will
need to handle each one's availability individually instead of just handling the availability of a login session. 

This can be mitigated by using the *for comprehension* FP technique or by having an anonymous user login session.

### Observable login session component and switchMap for observing session data properties

In Kotlin flows, the equivalent is `flatMapLatest`. No apparent downsides to this approach.
This necessitates an observable `component` property in `LoginSessionRegistry`.

Observing an observable login session data property would look like:

```kotlin
private val summary: Flow<Summary?> = loginSessionRegistry.componentFlow.flatMapLatest { component ->
    component?.data?.summary ?: flowOf(null)
}
```

A downside is some boilerplate code which can be mitigated with common extension properties or some other syntactic sugar approach. 

### Cancelling interactors when a login session is closed 

## Decision

1. Use the "Observable login session component and switchMap for observing session data properties" approach.
This seems to be a sold approach and elegant solution. Additionally it doesn't require big changes.
2. Auto-closing login sessions is not on the agenda for now, but could be
3. It's not a big deal
