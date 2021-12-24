@steps:roundUp
Feature: Calculate a Round-up amount for an account

  Scenario: Example
    Given the following transactions in an account
      |-4.35|
      |-5.20|
      |-0.87|
    When the round up amount is calculated
    Then the result will be 1.58