import java.time.LocalDate;

public class InternshipFilterSettings {

    private OpportunityStatus statusFilter;       // null = ANY
    private String preferredMajorFilter;         // null = ANY
    private InternshipLevel levelFilter;         // null = ANY
    private LocalDate closingDateBeforeFilter;   // null = ANY

    public InternshipFilterSettings() {}

    public OpportunityStatus getStatusFilter() {
        return statusFilter;
    }

    public void setStatusFilter(OpportunityStatus statusFilter) {
        this.statusFilter = statusFilter;
    }

    public String getPreferredMajorFilter() {
        return preferredMajorFilter;
    }

    public void setPreferredMajorFilter(String preferredMajorFilter) {
        if (preferredMajorFilter != null && preferredMajorFilter.isBlank()) {
            this.preferredMajorFilter = null;
        } else {
            this.preferredMajorFilter = preferredMajorFilter;
        }
    }

    public InternshipLevel getLevelFilter() {
        return levelFilter;
    }

    public void setLevelFilter(InternshipLevel levelFilter) {
        this.levelFilter = levelFilter;
    }

    public LocalDate getClosingDateBeforeFilter() {
        return closingDateBeforeFilter;
    }

    public void setClosingDateBeforeFilter(LocalDate closingDateBeforeFilter) {
        this.closingDateBeforeFilter = closingDateBeforeFilter;
    }

    public String toSummaryString() {
        return "Status=" + (statusFilter == null ? "ANY" : statusFilter) +
               ", Major=" + (preferredMajorFilter == null ? "ANY" : preferredMajorFilter) +
               ", Level=" + (levelFilter == null ? "ANY" : levelFilter) +
               ", Closing Before=" + (closingDateBeforeFilter == null ? "ANY" : closingDateBeforeFilter);
    }
}
