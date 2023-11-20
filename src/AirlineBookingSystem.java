import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.sql.*;

public class AirlineBookingSystem {

    private static final String JDCB_URL = "jdbc:mysql://localhost:3306/AirlineBookingSystem";
    private static final String USER = "root";
    private static final String PASSWORD = "Steelersrock1";

    private static Connection createConnection() {
        try {
            return DriverManager.getConnection(JDCB_URL, USER, PASSWORD);
        }
        catch (SQLException error) {
            error.printStackTrace();
            throw new RuntimeException("There was a failure connecting to the database, please try again.");
        }
    }

    private static void createTable(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            String createBookingTable = "CREATE TABLE IF NOT EXISTS booking ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "passenger_name VARCHAR(255),"
                    + "flight_number VARCHAR(255),"
                    + "departure_date DATE,"
                    + "departure_airport VARCHAR(255),"
                    + "arrival_airport VARCHAR(255)"
                    + ")";
            statement.executeUpdate(createBookingTable);
        }
        catch (SQLException error) {
            error.printStackTrace();
            throw new RuntimeException("Failed to create tables, try again.");
        }
    }

    private static void insertBooking(Connection connection, String passengerName, String flightNumber,
                                      String departureDateString, String departureAirport, String arrivalAirport) {
        Date departureDate = Date.valueOf(departureDateString);
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO booking (passenger_name, flight_number, departure_date, departure_airport, " +
                        "arrival_airport)"
                + "VALUES (?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, passengerName);
            preparedStatement.setString(2, flightNumber);
            preparedStatement.setDate(3, departureDate);
            preparedStatement.setString(4, departureAirport);
            preparedStatement.setString(5, arrivalAirport);

            preparedStatement.executeUpdate();
            System.out.println("Your booking was successfully added.");
        }
        catch (SQLException error) {
            error.printStackTrace();
            throw new RuntimeException("There was an error in inserting the booking information.");
        }
    }

    private static void viewBooking(Connection connection, String passengerName) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM booking WHERE passenger_name = ?")) {
            preparedStatement.setString(1, passengerName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                System.out.println("Booking Id: " + resultSet.getInt("id"));
                System.out.println("Flight Number: " + resultSet.getString("flight_number"));
                System.out.println("Departure Date: " + resultSet.getDate("departure_date"));
                System.out.println("Departure Airport: " + resultSet.getString("departure_airport"));
                System.out.println("Arrival Airport: " + resultSet.getString("arrival_airport"));
            }
        }
        catch (SQLException error) {
            error.printStackTrace();
            throw new RuntimeException("Failure to view the booking information, please try again.");
        }
    }

    private static void deleteBooking(Connection connection, int bookingId) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM booking WHERE id = ?")) {
            preparedStatement.setInt(1, bookingId);
            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Booking was deleted successfully.");
            }
            else {
                System.out.println("There is wasn't a booking found with that information, please try again."
                + bookingId);
            }
        }
        catch (SQLException error) {
            error.printStackTrace();
            throw new RuntimeException("There was a failure in the deletion process, please try again.");
        }
    }

    public static void main(String[] args) {
        Connection connection = createConnection();
        createTable(connection);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("""
                    ~~~~~~~~~~~~~~~~~~~~~~~
                    |1: Booking           |
                    |2: View Reservation  |
                    |3: Cancel Reservation|
                    |4: Exit Menu         |
                    ~~~~~~~~~~~~~~~~~~~~~~~
                    """);
            System.out.println("Please enter you choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    System.out.println("Please enter the passenger's name: ");
                    String passengerName = scanner.nextLine();

                    System.out.println("Please enter the flight number: ");
                    String flightNumber = scanner.nextLine();

                    System.out.println("Please enter the departure date (YYYY-MM-DD): ");
                    String departureDate = scanner.nextLine();

                    System.out.println("Please enter the departure airport: ");
                    String departureAirport = scanner.nextLine();

                    System.out.println("Please enter the arrival airport: ");
                    String arrivalAirport = scanner.nextLine();

                    insertBooking(connection, passengerName, flightNumber, departureDate, departureAirport,
                            arrivalAirport);
                }
                case "2" -> {
                    System.out.println("Please enter the passenger's name: ");
                    String viewPassengersName = scanner.nextLine();
                    viewBooking(connection, viewPassengersName);
                }
                case "3" -> {
                    System.out.println("Please enter the booking Id to cancel the booking: ");
                    int bookingId = Integer.parseInt(scanner.nextLine());
                    deleteBooking(connection, bookingId);
                }
                case "4" -> System.out.println("Exiting the menu, thank you for booking with us.");
                default -> System.out.println("Please enter a valid option.");
            }

            if (choice.equals("4")) {
                break;
            }
        }

        try {
            connection.close();
        }
        catch (SQLException error) {
            error.printStackTrace();
        }
    }
}
