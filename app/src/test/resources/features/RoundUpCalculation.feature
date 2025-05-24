Feature: Calculations

  Rule: The round-up amount for an account is the sum of round-up amounts of spending transactions
  dated within a week.
  A week-long period is the last seven days including today.
  A spending transaction is outbound, settled, and from an external source.
  A round up of transaction t is: ceiling(t.amount) - t.amount)

    Scenario Outline: start of week-long period is calculated from current time
      Given the current local date and time is <now>
      When the week-long period is evaluated
      Then the start of the period should be <since> date and time
      Examples:
        | now                 | since               | note                       |
        | 2025-05-11T12:15+01 | 2025-05-05T00:00+01 | local time in BST (UTC+01) |
        | 2025-03-10T08:20Z   | 2025-03-04T00:00Z   | local time in GMT (UTC)    |

    Scenario: Accounts and Round-ups report is generated
      Given I have the following accounts
        | account number | currency | balance |
        | 1              | GBP      | 100.10  |
        | 2              | EUR      | 2000.20 |
      And I have these transactions
        | account number | round-up | is spending |
        | 1              | 0.65     | yes         |
        | 1              | 0.80     | yes         |
        | 1              | 0.13     | yes         |
        | 2              | 0.65     | yes         |
        | 2              | 0.80     | yes         |
        | 2              | 0.13     | no          |
      When I'm presented with Accounts and Round-ups
      Then the following information should be included
        | account number | currency | balance | round-up |
        | 1              | GBP      | 100.10  | 1.58     |
        | 2              | EUR      | 2000.20 | 1.45     |


    Scenario: the transaction round-up amount is calculated
      Given a transaction with amount of 4.35
      When the transaction round-up is calculated
      Then the result should be 0.65

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
