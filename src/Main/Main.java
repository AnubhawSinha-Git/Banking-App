package Main;

import java.sql.*;
import java.util.Scanner;

import Account.BankAccountDetails;

public class Main {

    private static final String URL = "jdbc:mysql://localhost:3306/bank_app";
    private static final String USER = "root";
    private static final String PASSWORD = "Anubhaw@123";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("===== Welcome to Banking System =====");

        while (true) {

            System.out.println("\n1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            System.out.print("\nEnter choice : ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            System.out.print("Enter Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Customer ID: ");
            String customerId = sc.nextLine();

            switch (choice) {

                case 1:
                    // LOGIN
                    System.out.println("\n⏳ Checking credentials...");
                    waitTime(1);

                    if (isCustomerRegistered(name, customerId)) {

                        System.out.println("⏳ Logging in...");
                        waitTime(1);

                        System.out.println("\n✅ Login Successful");

                        BankAccountDetails obj =
                                new BankAccountDetails(name, customerId);
                        obj.menu();

                    } else {

                        System.err.println("\n❌ Invalid credentials!");
                        System.err.println("👉 Please register first.");
                    }
                    break;

                case 2:
                    // REGISTER
                    if (isCustomerIdExists(customerId)) {
                        System.out.println("⚠ Customer ID already exists!");
                    } else {

                        if (registerCustomer(name, customerId)) {

                            System.out.println("⏳ Creating account...");
                            waitTime(3);

                            System.out.println("✅ Registered Successfully!");

                            BankAccountDetails obj =
                                    new BankAccountDetails(name, customerId);
                            obj.menu();

                        } else {
                            System.out.println("❌ Registration Failed!");
                        }
                    }
                    break;

                case 3:
                    System.out.println("👋 Exiting system...");
                    sc.close();
                    return;

                default:
                    System.out.println("❌ Invalid choice!");
            }
        }
    }

    // ⏳ DELAY METHOD
    public static void waitTime(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 🔍 LOGIN CHECK
    public static boolean isCustomerRegistered(String name, String customerId) {

        String query = "SELECT * FROM login_details WHERE username=? AND customer_id=?";

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, name);
            ps.setString(2, customerId);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 🔍 CHECK CUSTOMER EXISTS
    public static boolean isCustomerIdExists(String customerId) {

        String query = "SELECT * FROM login_details WHERE customer_id=?";

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, customerId);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 📝 REGISTER CUSTOMER
    public static boolean registerCustomer(String name, String customerId) {

        String query = "INSERT INTO login_details (username, customer_id) VALUES (?, ?)";

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, name);
            ps.setString(2, customerId);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}