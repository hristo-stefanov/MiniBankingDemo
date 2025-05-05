Feature: Calculations

    # TODO update the expression to include eligibility
#  FEEL expression:  sum(for t in transactions return ceiling(t) - t)

  Rule: The round-up amount for an account is the sum of the difference between each eligible transaction's
  rounded-up amount and its original value.

    Scenario: All transactions in an account are eligible for round-up
      Given an account has transactions with the following amounts and eligibility for round up:
        | amount | eligibility |
        | 4.35   | eligible    |
        | 5.20   | eligible    |
        | 0.87   | eligible    |
      When the round-up amount for the account is calculated
      Then the result should be 1.58

    @debug
    Scenario: Not all transactions in an account are eligible for round-up
      Given an account has transactions with the following amounts and eligibility for round up:
        | amount | eligibility |
        | 4.35   | eligible    |
        | 5.20   | eligible    |
        | 0.87   | ineligible  |
      When the round-up amount for the account is calculated
      Then the result should be 1.45


  Rule: A transaction is eligible for round-up when it is classified as "spending"
  and dated within a week
#      TODO do we really need examples for a simple rule
    @manual
    Scenario Outline: Transaction eligibility for round-up is calculated
      Given I have a transaction that is classified as <classification> and dated as <date>
      When its eligibility for round-up is evalued
      Then the result should be <eligibility>
      Examples:
        | classification | date       | eligibility |
        | spending       | 2025-10-15 | ?           |

  Rule: A spending transaction is outbound, settled, and from an external source

    Scenario Outline: A transaction is classified as spending or non-spending
      Given I have a transaction from <source> that is <status> and <direction>
      When it is evaluated
      Then it should be classified as <classification>
      Examples:
        | source   | status    | direction | classification | notes                    |
        | external | settled   | outbound  | spending       | all criteria are covered |
        | internal | settled   | outbound  | non-spending   | not external             |
        | external | unsettled | outbound  | non-spending   | not settled              |
        | external | settled   | inbound   | non-spending   | not outbound             |


