import java.util.ArrayList;
import java.util.List;

public class OpportunityManager {

    private final List<InternshipOpportunity> opportunities = new ArrayList<>();

    // ========== ADD / STORE ==========

    public void addOpportunity(InternshipOpportunity opp) {
        if (opp != null) {
            opportunities.add(opp);
        }
    }

    // ========== REMOVE ==========

    public boolean removeOpportunity(InternshipOpportunity opp) {
        if (opp == null) return false;
        return opportunities.remove(opp);
    }

    
    // ========== ACCESS / QUERY ==========

    public List<InternshipOpportunity> getAllOpportunities() {
        return opportunities;
    }

    public InternshipOpportunity findByID(String id) {
        if (id == null) return null;
        for (InternshipOpportunity opp : opportunities) {
            if (opp.getInternshipID().equalsIgnoreCase(id)) {
                return opp;
            }
        }
        return null; // not found
    }
    
   // ========== FILTER ==========
    public List<InternshipOpportunity> filterOpportunities(
            OpportunityStatus statusFilter,
            String majorFilter,
            InternshipLevel levelFilter,
            String companyFilter
    ) {
        List<InternshipOpportunity> result = new ArrayList<>();

        for (InternshipOpportunity opp : opportunities) {
            if (opp == null) continue;

            if (statusFilter != null && opp.getStatus() != statusFilter) {
                continue;
            }

            if (majorFilter != null && !majorFilter.trim().isEmpty() &&
                !opp.getPreferredMajor().equalsIgnoreCase(majorFilter.trim())) {
                continue;
            }

            if (levelFilter != null && opp.getLevel() != levelFilter) {
                continue;
            }

            if (companyFilter != null && !companyFilter.trim().isEmpty() &&
                !opp.getCompanyName().equalsIgnoreCase(companyFilter.trim())) {
                continue;
            }

            result.add(opp);
        }

        return result;
    } 
}
