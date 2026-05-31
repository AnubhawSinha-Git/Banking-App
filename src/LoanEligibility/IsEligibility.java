package LoanEligibility;

public class IsEligibility {

    private int score;

    public IsEligibility(int score) {
        this.score = score;
    }

    public boolean checkEligibility() {
        return score >= 650;
    }
}