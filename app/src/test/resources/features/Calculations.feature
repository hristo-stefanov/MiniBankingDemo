Feature: Calculations

  Rule: The suggested round-up amount is the sum of the difference between each transaction's
  rounded-up amount and its original value.
  FEEL expression:  sum(for t in transactions return ceiling(t) - t)

    @steps:roundUpCalculation
    Scenario: Suggested round-up is calculated
      Given the following eligible transactions, with these amounts:
        | 4.35 |
        | 5.20 |
        | 0.87 |
      When the suggested round-up amount is calculated
      Then the result should be 1.58

  Rule: A spending transaction is outbound, settled, and from an external source

    Scenario Outline: A transaction is classified as spending or non-spending
      Given I have a transaction from <source> that is <status> and <direction>
      When the transaction is evaluated
      Then the transaction should be classified as <evaluation>
      Examples:
        | source   | status    | direction | evaluation   | notes                    |
        | external | settled   | outbound  | spending     | all criteria are covered |
        | internal | settled   | outbound  | non-spending | not external             |
        | external | unsettled | outbound  | non-spending | not settled              |
        | external | settled   | inbound   | non-spending | not outbound             |



#    Scenario Outline: Transaction eligibility for round-up suggestion is calculated
