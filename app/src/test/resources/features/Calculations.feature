Feature: Calculations

  Rule: The round-up amount for an account is the sum of the difference between each eligible transaction's
  rounded-up amount and its original value.

  FEEL expression: sum(for t in transactions
  where t.eligibility = "eligible"
  return ceiling(t.amount) - t.amount)

    Scenario: All transactions in an account are eligible for round-up
      Given an account has transactions with the following amounts and eligibility for round up:
        | amount | eligibility |
        | 4.35   | eligible    |
        | 5.20   | eligible    |
        | 0.87   | eligible    |
      When the round-up amount for the account is calculated
      Then the result should be 1.58

    Scenario: Not all transactions in an account are eligible for round-up
      Given an account has transactions with the following amounts and eligibility for round up:
        | amount | eligibility |
        | 4.35   | eligible    |
        | 5.20   | eligible    |
        | 0.87   | ineligible  |
      When the round-up amount for the account is calculated
      Then the result should be 1.45


  Rule: A transaction is eligible for round-up when it is classified as "spending"
  and dated within the last seven days including today. A spending transaction is outbound, settled,
  and from an external source.

  FEEL expression: filter(
  transactions,
  t ->
  t.source = "external" and
  t.status = "settled" and
  t.direction = "outbound" and
  t.dateTime >= date and time(today() - duration("P6D") + "T00:00:00")
  )

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
