import java.util.ArrayList;
import java.util.List;

public class ApplicationManager {
    private final List<InternshipApplication> applications = new ArrayList<>();

    public void addApplication(InternshipApplication app) {
        applications.add(app);
    }

    public List<InternshipApplication> getAllApplications() {
        return applications;
    }

    public List<InternshipApplication> getApplicationsForOpportunity(InternshipOpportunity opp) {
        List<InternshipApplication> result = new ArrayList<>();
        for (InternshipApplication app : applications) {
            if (app.getOpportunity() == opp) {
                result.add(app);
            }
        }
        return result;
    }
}