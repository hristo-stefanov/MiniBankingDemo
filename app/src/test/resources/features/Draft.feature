@draft
Feature: Draft

  # This assumes the login process includes network activity which is not currently the case
  # If there is a feature to fetch user details, such as name, this feature will make sense.
  Rule: Should allow the user to retry auto-logging if a network error occurs

    Scenario: there is no internet connection
      Given I was logged in before exiting the app
      And there is no internet connection
      When I launch the app to access Accounts
      Then I should be prompted to retry

  Rule: Should allow the user to provide credentials if a login service error occurs

    Scenario: the app keeps an invalid token
      Given I was logged in before exiting the app
      And the app keeps an invalid token
      When I launch the app to access Accounts
      Then I should be asked to login

  Rule: Should allow the user to retry logging in after failure

  Rule: Should prompt the user to log in when their session is closed by the online banking service
