Feature: Encourage users to save money

#  TODO can use tagged hooks to setup
  Background:
    Given I am registered for online banking
    Given I am logged in

  Scenario: The app encourages users to save round-ups
    Given the calculated round-up for my account is 1.0
    When I view this account
    Then I should be offered to save "£1.00"
    And I should be able to transfer the offered amount to a savings goal