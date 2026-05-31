package BankApplicationDetails;

import java.util.Scanner;
import LoanEligibility.IsEligibility;
import TypesOfLoan.*;

public class LoanMenu {

    public void startLoanProcess() {

        Scanner sc = new Scanner(System.in);

        System.out.println("\n===== Loan Application =====\n");

        System.out.print("Enter Name : ");
        String name = sc.nextLine();

        System.out.print("Enter CIBIL Score : ");
        int score = sc.nextInt();

        IsEligibility check = new IsEligibility(score);

        if (check.checkEligibility()) {

            System.out.println("\nSelect Loan Type:");
            System.out.println("1. Home Loan");
            System.out.println("2. Personal Loan");
            System.out.println("3. Vehicle Loan");
            System.out.println("4. Business Loan");
            System.out.println("5. Education Loan");
            System.out.println("6. Gold Loan");
            System.out.println("7. LAP Loan");
            System.out.println("8. LAS Loan");
            System.out.println("9. Overdraft Facility\n");
            System.out.print("Select an option : ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    new HomeLoan().apply();
                    break;
                case 2:
                    new PersonalLoan().apply();
                    break;
                case 3:
                    new VehicleLoan().apply();
                    break;
                case 4:
                    new BusinessLoan().apply();
                    break;
                case 5:
                    new EducationLoan().apply();
                    break;
                case 6:
                    new GoldLoan().apply();
                    break;
                case 7:
                    new LAPLoan().apply();
                    break;
                case 8:
                    new LASLoan().apply();
                    break;
                case 9:
                    new OverdraftFacility().apply();
                    break;
                default:
                    System.out.println("Invalid choice");
            }

        } else {
        	System.err.println("\n❌ Sorry " + name + ", you are not eligible for loan.");
            System.out.println("👉 Please improve your civil score and try again.");
        }
    }
}