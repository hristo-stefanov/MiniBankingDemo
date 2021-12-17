Feature: Log out
  In order to protect my financial information and assets from unauthorised access
  and to be able log in with different credentials
  As a bank client
  I want to be able to log out

  Background:
    Given I am registered for online banking

    # TODO illustrate each rule with an example
#  Rule: The user can log out when logged in
#    Rule: User specific information should disappear when logging out
#  Rule: User should be asked to log in after logging out

    Scenario: logging out
      Given I am seeing my account information
      When I log out
      Then my account information should disappear
      And I should be asked to login