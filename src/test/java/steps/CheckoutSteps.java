package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import model.Product;
import process.CalcDiscount;
import static org.junit.Assert.assertEquals;

public class CheckoutSteps {

    private Float basePrice;
    private String sale;
    private int quantityBought;
    private float calculatedCost;

    @Given("a product with a base price of {float} and a sale of {string}")
    public void a_product_with_a_base_price_of_and_a_sale_of(Float price, String saleRule) {
        this.basePrice = price;
        this.sale = saleRule;
    }

    @When("the passenger buys {int} of this product")
    public void the_passenger_buys_of_this_product(Integer quantity) {
        this.quantityBought = quantity;
        this.calculatedCost = CalcDiscount.calcDiscount(basePrice, sale, quantityBought);
    }

    @Then("the calculated cost should be {float}")
    public void the_calculated_cost_should_be(Float expectedCost) {
        assertEquals(expectedCost, calculatedCost, 0.01);
    }
}
