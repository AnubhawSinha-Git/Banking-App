package Account;

import java.util.Scanner;
import BankApplicationDetails.LoanMenu;

public class BankAccountDetails {

    private double bal;
    private double prevTrans;
    private String customerName;
    private String customerId;

    public BankAccountDetails(String customerName, String customerId) {
        this.customerName = customerName;
        this.customerId = customerId;
    }

    void deposit(double amount) {
        if (amount > 0) {
            bal += amount;
            prevTrans = amount;
        }
    }

    void withdraw(double amt) {
        if (amt > 0 && bal >= amt) {
            bal -= amt;
            prevTrans = -amt;
        } else {
            System.out.println("❌ Insufficient balance");
        }
    }

    void getPreviousTrans() {
        if (prevTrans > 0)
            System.out.println("Deposited: " + prevTrans);
        else if (prevTrans < 0)
            System.out.println("Withdrawn: " + Math.abs(prevTrans));
        else
            System.out.println("No transaction");
    }

    private String getMaskedCustomerId() {
        return "****" + customerId.substring(customerId.length() - 2);
    }

    public void menu() {
        Scanner sc = new Scanner(System.in);
        char option;

        System.out.println("\nWelcome " + customerName);
        System.out.println("Your ID: " + getMaskedCustomerId());

        do {
            System.out.println("\n===== MENU =====\n");
            System.out.println("a) Balance");
            System.out.println("b) Deposit");
            System.out.println("c) Withdraw");
            System.out.println("d) Previous Transaction");
            System.out.println("e) Loan Section");
            System.out.println("f) Exit\n");
            System.out.print("Choose Option : ");
            

            option = sc.next().charAt(0);

            switch (option) {
                case 'a':
                    System.out.println("Balance = " + bal);
                    break;

                case 'b':
                    System.out.print("Enter amount : ");
                    deposit(sc.nextDouble());
                    break;

                case 'c':
                    System.out.print("Enter amount : ");
                    withdraw(sc.nextDouble());
                    break;

                case 'd':
                    getPreviousTrans();
                    break;

                case 'e':
                    LoanMenu loan = new LoanMenu();
                    loan.startLoanProcess();
                    break;

                case 'f':
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid option");
            }

        } while (option != 'f');
    }
}