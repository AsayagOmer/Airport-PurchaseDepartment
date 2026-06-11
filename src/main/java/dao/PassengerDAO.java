package dao;

import model.Passenger;
import db.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PassengerDAO {

    private static final Map<String, Passenger> MOCK_PASSENGERS = new HashMap<>();

    static {
        MOCK_PASSENGERS.put("12345", new Passenger(101, "Ofek", "Aharoni", "12345", "Israeli", "2000-01-01", 123456789, "ofek@example.com"));
        MOCK_PASSENGERS.put("67890", new Passenger(102, "Ilay", "Asayag", "67890", "Israeli", "1999-05-12", 987654321, "ilay@example.com"));
    }

    public Passenger findByPassportNumber(String number){
        String sql = "SELECT * FROM Passengers WHERE passport_number = ?";

        // Using try-with-resources to ensure the connection is closed automatically
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, number);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractPassengerFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error finding passenger, using mock fallback. Reason: " + e.getMessage());
            return MOCK_PASSENGERS.get(number);
        } catch (Exception e) {
            System.err.println("Error finding passenger, using mock fallback. Reason: " + e.getMessage());
            return MOCK_PASSENGERS.get(number);
        }
        return MOCK_PASSENGERS.get(number);
    }

    private Passenger extractPassengerFromResultSet(ResultSet rs) throws SQLException {
        return factory.ModelFactory.createPassenger(rs);
    }

}
