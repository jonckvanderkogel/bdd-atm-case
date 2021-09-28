Feature: Interact with ATM machine

  Scenario: As a customer I expect to be able to withdraw bills from the ATM machine
    Given I have 1000 Euros in my account
    And My account number is TestUser
    When I withdraw 90 Euros
    Then I expect the following set of bills
      | 1 | FIFTY  |
      | 2 | TWENTY |

  Scenario: As a customer I expect a faulty atm to crash on the first attempt
    Given The atm is faulty
    And I have 1000 Euros in my account
    And My account number is TestUser
    When I withdraw 90 Euros with fees
    Then I expect the atm crashed