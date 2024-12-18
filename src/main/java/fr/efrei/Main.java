package fr.efrei;

import fr.efrei.domain.Employee;
import fr.efrei.domain.EmployeeType;
import fr.efrei.domain.RoomType;
import fr.efrei.factory.*;
import fr.efrei.repository.*;
import fr.efrei.view.*;
import javax.swing.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.*;


public class Main {
    public static void main(String[] args) {
        IBookingRepository bookingRepository = BookingRepository.getRepository();
        ICustomerRepository customerRepository = CustomerRepository.getRepository();
        IRoomRepository roomRepository = RoomRepository.getRepository();
        IEmployeeRepository employeeRepository = EmployeeRepository.getRepository();
        CustomerView customerView = new CustomerView(customerRepository);
        EmployeeView employeeView = new EmployeeView(employeeRepository);
        RoomView roomView = new RoomView(roomRepository);
        BookingView bookingView = new BookingView(bookingRepository);

        customerRepository.create(CustomerFactory.createCustomer("John", "Doe", "john.doe@example.com", "+123456789", LocalDate.of(1990, 1, 15)));
        customerRepository.create(CustomerFactory.createCustomer("Jane", "Smith", "jane.smith@example.com", "+987654321", LocalDate.of(1985, 6, 25)));
        customerRepository.create(CustomerFactory.createCustomer("Robert", "Brown", "robert.brown@example.com", "+1122334455", LocalDate.of(1992, 9, 10)));
        customerRepository.create(CustomerFactory.createCustomer("Emily", "Davis", "emily.davis@example.com", "+441234567890", LocalDate.of(2000, 11, 30)));
        customerRepository.create(CustomerFactory.createCustomer("Michael", "Johnson", "michael.johnson@example.com", "+3311223344", LocalDate.of(1995, 3, 5)));
        employeeRepository.create(EmployeeFactory.createEmployee( "Pedro", "Gonzalez", LocalDate.of(2004, 9, 17), EmployeeType.DIRECTOR,"1234"));
        employeeRepository.create(EmployeeFactory.createEmployee( "Maria", "Lopez", LocalDate.of(1990, 3, 22), EmployeeType.FRONT_DESK_AGENT,"5678"));
        employeeRepository.create(EmployeeFactory.createEmployee( "John", "Smith", LocalDate.of(1985, 6, 15), EmployeeType.DIRECTOR,"zdc"));
        employeeRepository.create(EmployeeFactory.createEmployee( "Anna", "Taylor", LocalDate.of(1998, 12, 5), EmployeeType.STAFF_MEMBER,"abcd"));
        employeeRepository.create(EmployeeFactory.createEmployee( "James", "Brown", LocalDate.of(2000, 8, 19), EmployeeType.STAFF_MEMBER,"efgh"));
        roomRepository.create(RoomFactory.createRoom(1, RoomType.SINGLE));
        roomRepository.create(RoomFactory.createRoom(2, RoomType.DOUBLE));
        roomRepository.create(RoomFactory.createRoom(3, RoomType.SUITE));
        roomRepository.create(RoomFactory.createRoom(4, RoomType.SINGLE));
        roomRepository.create(RoomFactory.createRoom(5, RoomType.DOUBLE));
        bookingRepository.create(BookingFactory.createBooking(
                roomRepository.getAll().getFirst(), customerRepository.getAll().getFirst(), LocalDate.of(2024, 12, 15), LocalDate.of(2024, 12, 20), employeeRepository.getAll().getFirst(), 1));
        bookingRepository.create(BookingFactory.createBooking(
                roomRepository.getAll().get(1),  customerRepository.getAll().get(1), LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 15),  employeeRepository.getAll().get(1), 2));
        bookingRepository.create(BookingFactory.createBooking(
                roomRepository.getAll().get(2),  customerRepository.getAll().get(3), LocalDate.of(2025, 2, 12), LocalDate.of(2025, 2, 17),  employeeRepository.getAll().get(1), 4));
        bookingRepository.create(BookingFactory.createBooking(
                roomRepository.getAll().get(2),  customerRepository.getAll().get(2), LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 15),  employeeRepository.getAll().get(2), 2));
        bookingRepository.create(BookingFactory.createBooking(
                roomRepository.getAll().get(3),  customerRepository.getAll().get(4), LocalDate.of(2025, 4, 16), LocalDate.of(2025, 4, 21),  employeeRepository.getAll().get(3), 1));
        Map<String, String> employeeId = employeeRepository.getAll()
                .stream()
                .collect(Collectors.toMap(
                        employee -> employee.getFirstName() + " " + employee.getLastName(),
                        Employee::getEmployeeNumber
                ));

        String[] employeeNames = employeeId.keySet().toArray(new String[0]);

        int employeeLoginChoice = JOptionPane.showOptionDialog(
                null,
                "Select employee profile",
                "Employee Log in",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                employeeNames,
                employeeNames[0]
        );
        String selectedName = employeeNames[employeeLoginChoice];
        String employeeNumber = employeeId.get(selectedName);
        Employee selectedEmployee = employeeRepository.read(employeeNumber);
        int tryNumber = 0;
        String password;
        boolean exitMenu = false;
        do {
            password = JOptionPane.showInputDialog("Enter your password to log in");
            if (!password.equals(selectedEmployee.getPassword())) {
                tryNumber++;
                JOptionPane.showMessageDialog(null, "Password incorrect. Please try again.");
            }
        } while (!password.equals(selectedEmployee.getPassword()) && tryNumber < 3);

        if (tryNumber < 3) {
            JOptionPane.showMessageDialog(null, "Login successful!");
        } else {
            JOptionPane.showMessageDialog(null, "Too many incorrect attempts. Exiting...");
        }
        do{
            switch (selectedEmployee.getEmployeeType()) {
                case STAFF_MEMBER:
                    String[] staffOptions = {"Clean Room","Baggage handling","Exit Menu"};
                    int staffChoice = JOptionPane.showOptionDialog(
                            null,
                            "Select a task",
                            "Staff tasks",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            staffOptions,
                            staffOptions[0]
                    );
                    switch (staffChoice){
                        case 0 :
                            int roomId = RoomView.selectRoomId();
                            System.out.println(STR."Room : \{roomId} cleaned");
                            break;
                        case 1 :
                            System.out.println("Picking luggage");
                            break;
                        case 2 :
                            exitMenu = true;
                            break;
                    }
                    break;
                case FRONT_DESK_AGENT:
                    String[] frontDeskOptions = {"Booking Menu", "Customer Menu","Exit Menu"};
                    int frontDeskChoice = JOptionPane.showOptionDialog(
                            null,
                            "Select an option",
                            "Front desk menu",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            frontDeskOptions,
                            frontDeskOptions[0]
                    );
                    switch (frontDeskChoice) {
                        case 0 -> BookingView.bookingMenu();
                        case 1 -> CustomerView.customerMenu();
                        case 2 -> exitMenu = true;
                        default -> System.out.println("error");
                    }
                    break;
                case MANAGER:
                    String[] managerOptions = {"Booking Menu", "Customer Menu", "Employee Menu","Exit Menu"};
                    int managerChoices = JOptionPane.showOptionDialog(
                            null,
                            "Select an option",
                            "Manager menu",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            managerOptions,
                            managerOptions[0]
                    );
                    switch (managerChoices) {
                        case 0 -> BookingView.bookingMenu();
                        case 1 -> CustomerView.customerMenu();
                        case 2 -> EmployeeView.employeeMenu();
                        case 3 -> exitMenu = true;
                        default -> System.out.println("error");
                    }
                    break;
                case DIRECTOR:
                    String[] directorOptions = {"Booking Menu", "Customer Menu", "Employee Menu", "Room Menu","Exit Menu"};
                    int directorChoice = JOptionPane.showOptionDialog(
                            null,
                            "Select an option",
                            "Director menu",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            directorOptions,
                            directorOptions[0]
                    );
                    switch (directorChoice) {
                        case 0 -> BookingView.bookingMenu();
                        case 1 -> CustomerView.customerMenu();
                        case 2 -> EmployeeView.employeeMenu();
                        case 3 -> RoomView.roomMenu();
                        case 4 -> exitMenu = true;
                    }
                    break;
            }
        }while (!exitMenu);
    }
}
