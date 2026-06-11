Feature: Discount Calculation
  As a checkout system
  I want to calculate discounts accurately
  So that passengers are charged the correct total amount

  Scenario: Calculate 1+1 discount correctly
    Given a product with a base price of 100.0 and a sale of "1+1"
    When the passenger buys 3 of this product
    Then the calculated cost should be 200.0

  Scenario: Calculate percentage discount correctly
    Given a product with a base price of 50.0 and a sale of "20% off"
    When the passenger buys 2 of this product
    Then the calculated cost should be 80.0
