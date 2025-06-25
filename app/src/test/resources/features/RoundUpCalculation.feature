Feature: Calculations

  Rule: A round-up period is the last seven days including today.

    Scenario Outline: Start of the round-up period is calculated from current time
      Given the current local date and time is <now>
      When the round-up period is evaluated
      Then the round-up period should start at <start>
      Examples:
        | now                 | start               | note                       |
        | 2025-05-11T12:15+01 | 2025-05-05T00:00+01 | local time in BST (UTC+01) |
        | 2025-03-10T08:20Z   | 2025-03-04T00:00Z   | local time in GMT (UTC)    |

  Rule: The round-up amount for an account is the sum of round-up amounts of spending transactions
  dated within the round-up period.

    Scenario: Accounts and Round-ups summary is generated
      Given I have the following accounts
        | account number | currency | balance |
        | 1              | GBP      | 100.10  |
        | 2              | EUR      | 2000.20 |
      And the round-up period starts at 2025-03-10T08:20Z
      And I have these transactions
        | account number | round-up | is spending |
        | 1              | 0.65     | yes         |
        | 1              | 0.80     | yes         |
        | 1              | 0.13     | yes         |
        | 2              | 0.65     | yes         |
        | 2              | 0.80     | yes         |
        | 2              | 0.13     | no          |
      When I'm presented with the Accounts and Round-ups summary
      Then the summary should include the following account details
        | account number | currency | balance | round-up |
        | 1              | GBP      | 100.10  | 1.58     |
        | 2              | EUR      | 2000.20 | 1.45     |
      And the summary should report the round-up period start as 2025-03-10T08:20Z

  Rule: A round up of transaction t is: ceiling(t.amount) - t.amount)

    Scenario: The transaction round-up amount is calculated
      Given a transaction with amount of 4.35
      When the transaction round-up is calculated
      Then the result should be 0.65

  Rule: A spending transaction is outbound, settled, and from an external source.
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
