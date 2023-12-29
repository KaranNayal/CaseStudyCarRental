package com.crs.controller;
import com.crs.dao.ICarLeaseRepository;
import com.crs.dao.ICarLeaseRepositoryImpl;
import com.crs.entity.Car;
import com.crs.entity.Customer;
import com.crs.entity.Lease;
import com.crs.entity.Payment;
import com.crs.exception.CarNotFoundException;
import com.crs.exception.CustomerNotFoundException;
import com.crs.exception.LeaseNotFoundException;
import java.util.Date;
import java.util.List;

public class CarRentalController {
    private ICarLeaseRepository carLeaseRepository;

    public CarRentalController() {
        this.carLeaseRepository = new ICarLeaseRepositoryImpl();
    }

    public void addCar(Car car) {
        try {
            carLeaseRepository.addCar(car);
            System.out.println("Car added successfully.");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void removeCar(int carID) {
        try {
            carLeaseRepository.removeCar(carID);
            System.out.println("Car removed successfully.");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public List<Car> listAvailableCars() {
        try {
            return carLeaseRepository.listAvailableCars();
        } catch (Exception e) {
            handleException(e);
            return null; // Handle gracefully in your application
        }
    }

    public void addCustomer(Customer customer) {
        try {
            carLeaseRepository.addCustomer(customer);
            System.out.println("Customer added successfully.");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void removeCustomer(int customerID) {
        try {
            carLeaseRepository.removeCustomer(customerID);
            System.out.println("Customer removed successfully.");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public List<Customer> listCustomers() {
        try {
            return carLeaseRepository.listCustomers();
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }
    public Lease findLeaseById(int leaseID) throws LeaseNotFoundException {
            return carLeaseRepository.findLeaseById(leaseID);
        
        
    }
    public Lease createLease( int customerID, int carID, Date startDate, Date endDate) {
        try {
            return carLeaseRepository.createLease( customerID, carID, startDate, endDate);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public Lease returnCar(int leaseID) throws LeaseNotFoundException {
        
            return carLeaseRepository.returnCar(leaseID);
       
    }
    public Customer findCustomerById(int customerId) throws CustomerNotFoundException {
      
           return carLeaseRepository.findCustomerById(customerId);
     
    }
    public List<Car> listRentedCars() {
        List<Car> rentedCars = carLeaseRepository.listRentedCars();
       
		return rentedCars;
    }
    public Car findCarById(int carID) throws CarNotFoundException {
		return  carLeaseRepository.findCarById(carID);
	}

    public List<Lease> listActiveLeases() {
        try {
            return carLeaseRepository.listActiveLeases();
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public void recordPayment(Lease lease, double amount) {
        try {
            carLeaseRepository.recordPayment(lease, amount);
            System.out.println("Payment recorded successfully.");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public List<Payment> retrievePaymentHistory(int customerID) {
        try {
            return carLeaseRepository.retrievePaymentHistory(customerID);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public double calculateTotalRevenue() {
        try {
            return carLeaseRepository.calculateTotalRevenue();
        } catch (Exception e) {
            handleException(e);
            return 0.0;
        }
    }
    public List<Lease> listLeaseHistory() {
        try {
            return ((ICarLeaseRepositoryImpl) carLeaseRepository).listLeaseHistory();
        } catch (Exception e) {
            handleException(e);
            return null; // Handle gracefully in your application
        }
    }

    private void handleException(Exception e) {
        System.out.println("An error occurred: " + e.getMessage());
    }

	
}
