import db.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Accessing the Singleton Kiosk
        Kiosk myKiosk = Kiosk.getInstance();
        myKiosk.startTransaction();

    }
}