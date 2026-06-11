package dao;

import model.Passenger;
import db.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PassengerDAO {

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
            System.err.println("Error finding passenger by passport number: " + e.getMessage());
        }
        return null; // Return null if the product was not found
    }

    private Passenger extractPassengerFromResultSet(ResultSet rs) throws SQLException {
        return factory.ModelFactory.createPassenger(rs);
    }

}
