public class CareerCenterStaff extends User implements HasMenu {

    private String department;

    public CareerCenterStaff(String userID, String name, String email, String department) {
        super(userID, name, email);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department.trim();
    }

    @Override
    public void displayMenu() {
        System.out.println("\n===== CAREER CENTER STAFF MENU =====");
        System.out.println("1. Approve Company Representatives");
        System.out.println("2. Approve Internship Opportunities");
        System.out.println("3. Manage Withdrawal Requests");
        System.out.println("4. Generate Internship Reports");  
        System.out.println("5. Change Password");
        System.out.println("6. Logout");
    }

    public void approveRepresentative(CompanyRepresentative rep) {
        rep.setApproved(true);
        System.out.println("Approved company representative: " + rep.getName());
    }

    public void approveOpportunity(InternshipOpportunity opp) {
        opp.setStatus(OpportunityStatus.APPROVED);
        System.out.println("Approved internship: " + opp.getTitle());
    }

    public void rejectOpportunity(InternshipOpportunity opp) {
        opp.setStatus(OpportunityStatus.REJECTED);
        System.out.println("Rejected internship: " + opp.getTitle());
    }

    @Override
    public String toString() {
        return getName() + " (" + getUserID() + "), Dept: " + department;
    }
}

