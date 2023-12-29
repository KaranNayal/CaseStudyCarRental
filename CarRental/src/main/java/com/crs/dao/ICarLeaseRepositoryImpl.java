package com.crs.dao;

import com.crs.entity.*;

import com.crs.exception.*;

import com.crs.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ICarLeaseRepositoryImpl implements ICarLeaseRepository {
    private Connection connection;

    public ICarLeaseRepositoryImpl() {
        this.connection = DBConnection.getConnection();
    }

    // Car Management

    @Override
    public void addCar(Car car) throws CarAlreadyExistsException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBConnection.getConnection();

            String insertQuery = "INSERT INTO vehicle (make, model, year, dailyRate, status, passengerCapacity, engineCapacity) VALUES (?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);

            // Set values for the parameters in the INSERT statement
            preparedStatement.setString(1, car.getMake());
            preparedStatement.setString(2, car.getModel());
            preparedStatement.setInt(3, car.getYear());
            preparedStatement.setDouble(4, car.getDailyRate());
            preparedStatement.setString(5, car.getStatus());
            preparedStatement.setInt(6, car.getPassengerCapacity());
            preparedStatement.setInt(7, car.getEngineCapacity());

            // Execute the INSERT statement
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating car failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    car.setVehicleID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating car failed, no ID obtained.");
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            // Catch the specific exception for primary key violation
            throw new CarAlreadyExistsException("Car with ID " + car.getVehicleID() + " already exists.");
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle other SQL exceptions
            throw new RuntimeException("Error adding car", e);
        } 
    }
    @Override
    public void removeCar(int carID) {
        String sql = "DELETE FROM Vehicle WHERE vehicleID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, carID);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
    }

    @Override
    public List<Car> listAvailableCars() {
        List<Car> availableCars = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Vehicle WHERE status = 'available'");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                availableCars.add(mapResultSetToCar(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
        return availableCars;
    }

    @Override
    public List<Car> listRentedCars() {
        List<Car> rentedCars = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Vehicle WHERE status = 'notAvailable'");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                rentedCars.add(mapResultSetToCar(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
        return rentedCars;
    }

    @Override
    public Car findCarById(int carID) throws CarNotFoundException {
        String sql = "SELECT * FROM Vehicle WHERE vehicleID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, carID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToCar(resultSet);
            } else {
                throw new CarNotFoundException("Car with ID " + carID + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your needs
            throw new RuntimeException("An error occurred while finding the car.");
        }
    }

    // Customer Management

    @Override
    public void addCustomer(Customer customer) throws CustomerAlreadyExistsException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBConnection.getConnection();

            String insertQuery = "INSERT INTO customer (customerID, firstName, lastName, email, phoneNumber) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertQuery);

            // Set values for the parameters in the INSERT statement
            preparedStatement.setInt(1, customer.getCustomerID());
            preparedStatement.setString(2, customer.getFirstName());
            preparedStatement.setString(3, customer.getLastName());
            preparedStatement.setString(4, customer.getEmail());
            preparedStatement.setString(5, customer.getPhoneNumber());

            // Execute the INSERT statement
            preparedStatement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            // Catch the specific exception for primary key violation
            throw new CustomerAlreadyExistsException("Customer with ID " + customer.getCustomerID() + " already exists.");
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle other SQL exceptions
            throw new RuntimeException("Error adding customer", e);
        }
    }

    @Override
    public void removeCustomer(int customerID) throws CustomerNotFoundException {
        String sql = "DELETE FROM Customer WHERE customerID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerID);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
    }

    @Override
    public List<Customer> listCustomers() {
        List<Customer> customers = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Customer");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                customers.add(mapResultSetToCustomer(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
        return customers;
    }

    @Override
    public Customer findCustomerById(int customerID) throws CustomerNotFoundException {
        String sql = "SELECT * FROM Customer WHERE customerID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToCustomer(resultSet);
            } else {
                throw new CustomerNotFoundException("Customer with ID " + customerID + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your needs
            throw new RuntimeException("An error occurred while finding the customer.");
        }
    }

    // Lease Management

    @Override
    public Lease createLease(int customerID, int carID, Date startDate, Date endDate) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBConnection.getConnection();

            // Assuming leaseID is not auto-incrementing
            String insertQuery = "INSERT INTO lease (leaseID, vehicleID, customerID, startDate, endDate, type) VALUES (?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertQuery);

            // Set values for the parameters in the INSERT statement
            preparedStatement.setInt(1, getNextLeaseID()); // Replace with your logic to get the next unique leaseID
            preparedStatement.setInt(2, carID);
            preparedStatement.setInt(3, customerID);
            preparedStatement.setDate(4, new java.sql.Date(startDate.getTime()));
            preparedStatement.setDate(5, new java.sql.Date(endDate.getTime()));
            preparedStatement.setString(6, "Daily"); // Replace with your logic for determining type

            // Execute the INSERT statement
            preparedStatement.executeUpdate();

            // Return the Lease object with the provided leaseID
            return new Lease(getNextLeaseID(), carID, customerID, startDate, endDate, "Daily"); // Replace with your logic
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception
            throw new RuntimeException("Error creating lease", e);
        } 
    }

 // Replace this method with your logic to get the next unique leaseID
    private int getNextLeaseID() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();

            // Query to find the maximum existing leaseID
            String query = "SELECT MAX(leaseID) FROM lease";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // If there are existing records, increment the maximum leaseID
                return resultSet.getInt(1) + 1;
            } else {
                // If there are no existing records, start from 1
                return 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception
            throw new RuntimeException("Error getting next leaseID", e);
        } 
    }


    @Override
    public Lease returnCar(int leaseID) throws LeaseNotFoundException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Assuming you have a table named "lease" with columns including "leaseID", "vehicleID", etc.
            // Establish a database connection
            connection = DBConnection.getConnection();

            // Query to retrieve lease information based on leaseID
            String query = "SELECT * FROM lease WHERE leaseID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, leaseID);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Map ResultSet to Lease entity (you might have a method for this)
                Lease returnedLease = mapResultSetToLease(resultSet);

                // Implement logic for returning the leased car, if needed
                // ...

                return returnedLease;
            } else {
                throw new LeaseNotFoundException("Lease not found with ID: " + leaseID);
            }
        } catch (SQLException e) {
            // Handle SQLException
            e.printStackTrace();
            throw new RuntimeException("Error retrieving lease information", e);
        } 
    }
    @Override
    public List<Lease> listActiveLeases() {
        List<Lease> activeLeases = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Lease WHERE endDate >= CURRENT_DATE");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                activeLeases.add(mapResultSetToLease(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
        return activeLeases;
    }
    public Lease findLeaseById(int leaseID) throws LeaseNotFoundException {
        try {
            String sql = "SELECT * FROM Lease WHERE leaseID = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, leaseID);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapResultSetToLease(resultSet);
                    } else {
                        throw new LeaseNotFoundException("Lease not found with ID: " + leaseID);
                    }
                }
            }
        } catch (SQLException e) {
            // Handle database-related exceptions
            e.printStackTrace();
            throw new LeaseNotFoundException("Error finding Lease with ID: " + leaseID);
        }
    }
    @Override
    public List<Lease> listLeaseHistory() {
        List<Lease> leaseHistory = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Lease");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                leaseHistory.add(mapResultSetToLease(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
        return leaseHistory;
    }

    // Payment Handling

    @Override
    public void recordPayment(Lease lease, double amount) {
        String sql = "INSERT INTO Payment (leaseID, paymentDate, amount) VALUES (?, CURRENT_DATE, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, lease.getLeaseID());
            statement.setDouble(2, amount);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
    }

    @Override
    public List<Payment> retrievePaymentHistory(int customerID) {
        List<Payment> paymentHistory = new ArrayList<>();
        String sql = "SELECT * FROM Payment p JOIN Lease l ON p.leaseID = l.leaseID WHERE l.customerID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                paymentHistory.add(mapResultSetToPayment(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
        return paymentHistory;
    }

    @Override
    public double calculateTotalRevenue() {
        double totalRevenue = 0;
        String sql = "SELECT SUM(amount) AS total FROM Payment";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                totalRevenue = resultSet.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
        return totalRevenue;
    }

    // Helper methods to map ResultSets to entities

    private Car mapResultSetToCar(ResultSet resultSet) throws SQLException {
        return new Car(
                resultSet.getInt("vehicleID"),
                resultSet.getString("make"),
                resultSet.getString("model"),
                resultSet.getInt("year"),
                resultSet.getDouble("dailyRate"),
                resultSet.getString("status"),
                resultSet.getInt("passengerCapacity"),
                resultSet.getInt("engineCapacity")
        );
    }

    private Customer mapResultSetToCustomer(ResultSet resultSet) throws SQLException {
        return new Customer(
                resultSet.getInt("customerID"),
                resultSet.getString("firstName"),
                resultSet.getString("lastName"),
                resultSet.getString("email"),
                resultSet.getString("phoneNumber")
        );
    }

    private Lease mapResultSetToLease(ResultSet resultSet) throws SQLException {
        return new Lease(
                resultSet.getInt("leaseID"),
                resultSet.getInt("vehicleID"),
                resultSet.getInt("customerID"),
                resultSet.getDate("startDate"),
                resultSet.getDate("endDate"),
                resultSet.getString("type")
        );
    }

    private Payment mapResultSetToPayment(ResultSet resultSet) throws SQLException {
        return new Payment(
                resultSet.getInt("paymentID"),
                resultSet.getInt("leaseID"),
                resultSet.getDate("paymentDate"),
                resultSet.getDouble("amount")
        );
    }
    
}
