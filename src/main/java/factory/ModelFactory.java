package factory;

import model.Passenger;
import model.Product;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A factory class responsible for instantiating domain models.
 * This pattern abstracts the object creation logic away from the Data Access Objects (DAOs).
 */
public class ModelFactory {

    /**
     * Creates a Passenger object by mapping data from the provided SQL ResultSet.
     *
     * @param rs The ResultSet pointing to the current row containing passenger data.
     * @return A newly instantiated Passenger object.
     * @throws SQLException If an error occurs while accessing the ResultSet columns.
     */
    public static Passenger createPassenger(ResultSet rs) throws SQLException {
        return new Passenger(
                rs.getInt("passenger_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("passport_number"),
                rs.getString("nationality"),
                rs.getString("date_of_birth"),
                rs.getInt("phone"),
                rs.getString("email")
        );
    }

    /**
     * Creates a Product object by mapping data from the provided SQL ResultSet.
     *
     * @param rs The ResultSet pointing to the current row containing product data.
     * @return A newly instantiated Product object.
     * @throws SQLException If an error occurs while accessing the ResultSet columns.
     */
    public static Product createProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("product_id"),
                rs.getString("name"),
                (float) rs.getDouble("price"),
                rs.getInt("stock"),
                rs.getString("tags"),
                rs.getString("sale")
        );
    }
}
