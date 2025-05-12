Feature: Calculations

  Rule: The round-up amount for an account is the sum of round-up amounts of spending transactions
  dated within a week.
  A week period is the last seven days including today.
  A spending transaction is outbound, settled, and from an external source.
  A round up of transaction t is: ceiling(t.amount) - t.amount)

#    TODO automate
    @manual
    Scenario: the round-up amount of an account is calculated
      Given an account with transactions such as:
        | with tx round-up | is spending | is within a week |
        | 0.65             | yes         | yes              |
        | 0.80             | yes         | yes              |
        | 0.13             | yes         | yes              |
      Then the result should be 1.58

#    TODO automate
    @manual
    Scenario: the round-up amount of an account is calculated
      Given an account with transactions such as:
        | with tx round-up | is spending | is within a week |
        | 0.65             | yes         | yes              |
        | 0.80             | yes         | yes              |
        | 0.13             | no          | yes              |
      Then the result should be 1.45

#    TODO automate
    @manual
    Scenario: the round-up amount of an account is calculated
      Given an account with transactions such as:
        | with tx round-up | is spending | is within a week |
        | 0.65             | yes         | no               |
        | 0.80             | yes         | yes              |
        | 0.13             | yes         | yes              |
      Then the result should be 0.93

#      TODO for 4.35 should return 0.65
    Scenario: the round-up amount of a transaction is calculated

#    TODO remove - covered by the above scenarios
    Scenario: All account transactions for a period are spending ones
      Given an account has transactions for a period with the following amounts and is spending flags:
        | amount | is spending |
        | 4.35   | yes         |
        | 5.20   | yes         |
        | 0.87   | yes         |
      When the account round-up amount for the period is calculated
      Then the result should be 1.58

#    TODO remove - covered by the above scenarios
    Scenario: Not all account transactions for a period are spending ones
      Given an account has transactions for a period with the following amounts and is spending flags:
        | amount | is spending |
        | 4.35   | yes         |
        | 5.20   | yes         |
        | 0.87   | no          |
      When the account round-up amount for the period is calculated
      Then the result should be 1.45

#      TODO instead of requested use a word such as "evaluate" or "calculated" because
#      we do not spcify the requesting behaviour here.
    Scenario Outline: account transactions within the last seven days including today are requested
      Given the current local date and time is <now>
      When account transactions are requested
      Then the ones <since> date and time should be requested
      Examples:
        | now                    | since                  | note                       |
        | 2025-05-11T12:15:08+01 | 2025-05-05T00:00:00+01 | Local time in BST (UTC+01) |

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
