Feature: Calculations

  Rule: The round-up amount for an account is the sum of round-up amounts of spending transactions
  dated within a week.
  A week-long period is the last seven days including today.
  A spending transaction is outbound, settled, and from an external source.
  A round up of transaction t is: ceiling(t.amount) - t.amount)

    Scenario: the transaction round-up amount is calculated
      Given a transaction with amount of 4.35
      When the transaction round-up is calculated
      Then the result should be 0.65

    Scenario: all transactions are spending and dated within a week
      Given an account with these transactions:
        | round-up | is spending | is dated within a week |
        | 0.65     | yes         | yes                    |
        | 0.80     | yes         | yes                    |
        | 0.13     | yes         | yes                    |
      When the account round-up is calculated
      Then the result should be 1.58

    Scenario: a transaction is excluded if it is not spending
      Given an account with these transactions:
        | round-up | is spending | is dated within a week |
        | 0.65     | yes         | yes                    |
        | 0.80     | yes         | yes                    |
        | 0.13     | no          | yes                    |
      When the account round-up is calculated
      Then the result should be 1.45

    Scenario: a transaction is excluded if it is not dated within a week
      Given an account with these transactions:
        | round-up | is spending | is dated within a week |
        | 0.65     | yes         | no                     |
        | 0.80     | yes         | yes                    |
        | 0.13     | yes         | yes                    |
      When the account round-up is calculated
      Then the result should be 0.93

    Scenario Outline: start of week-long period is calculated from current time
      Given the current local date and time is <now>
      When the week-long period is evaluated
      Then the start of the period should be <since> date and time
      Examples:
        | now                 | since               | note                       |
        | 2025-05-11T12:15+01 | 2025-05-05T00:00+01 | Local time in BST (UTC+01) |
        | 2025-03-10T08:20Z   | 2025-03-04T00:00Z   | Local time in GMT (UTC)    |

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
