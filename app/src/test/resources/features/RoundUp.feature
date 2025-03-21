@steps:roundUp
Feature: Calculate a Round-up amount for an account
# TODO
  Rule: TODO how is the roundup amount calculated?

  # TODO (the one where) all transactions are negative/outgoing???
  Scenario: Example
    Given the following transactions in an account
      |-4.35|
      |-5.20|
      |-0.87|
    When the round up amount is calculated
    Then the result will be 1.58