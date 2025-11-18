import java.util.ArrayList;
import java.util.List;

public class CompanyRepresentative extends User implements HasMenu {
    private String companyName;
    private String department;
    private String position;
    private boolean approved;

    private final List<InternshipOpportunity> myInternships = new ArrayList<>();

    public CompanyRepresentative(String userID,String name,String email,String companyName,String department,String position) {
        super(userID, name, email);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.approved = false;  //default pending
    }

    @Override
    public void displayMenu() {
        System.out.println("\n===== COMPANY REPRESENTATIVE MENU =====");
        System.out.println("1. Create Internship Opportunity");
        System.out.println("2. View My Internships");
        System.out.println("3. Review Applications (Approve / Reject)");
        System.out.println("4. Manage Internship Listings (Edit / Delete / Toggle Visibility)");
        System.out.println("5. Edit Profile");
        System.out.println("6. Change Password");
        System.out.println("7. Logout");
    }

    public boolean createInternship(InternshipOpportunity opp) {
        if (!approved) {
            System.out.println("Your account is not approved yet.");
            return false;
        }
        if (myInternships.size() >= 5) {
            System.out.println("You cannot create more than 5 internships.");
            return false;
        }
        myInternships.add(opp);
        System.out.println("Internship created (pending approval): " + opp.getTitle());
        return true;
    }

    public void viewMyInternships() {
        System.out.println("\n===== Internships created by " + getName() + " =====");
        if (myInternships.isEmpty()) {
            System.out.println("No internships created yet.");
            return;
        }
        for (InternshipOpportunity opp : myInternships) {
            System.out.println(opp);
        }
    }

    public void viewApplicationsFor(InternshipOpportunity opp) {
        System.out.println("\n===== Applications for " + opp.getTitle() + " =====");
        if (opp.getApplications().isEmpty()) {
            System.out.println("No applications yet.");
            return;
        }
        for (InternshipApplication app : opp.getApplications()) {
            System.out.println(app);
        }
    }
    
    public boolean removeInternship(InternshipOpportunity opp) {
        if (opp == null) return false;
        return myInternships.remove(opp);
    }

    public void toggleVisibility(InternshipOpportunity opp) {
        opp.setVisible(!opp.isVisible());
        System.out.println("Visibility for " + opp.getTitle() + ": " + opp.isVisible());
    }

    public boolean isApproved()          { return approved; }
    public void setApproved(boolean a)   { this.approved = a; }
    public String getCompanyName()       { return companyName; }
    public List<InternshipOpportunity> getMyInternships() { return myInternships; }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return "%s (%s, %s) @ %s [approved=%s]"
                .formatted(getName(), position, department, companyName, approved);
    }
}

