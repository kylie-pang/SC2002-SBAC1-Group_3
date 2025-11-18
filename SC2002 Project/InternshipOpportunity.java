import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.time.LocalDate;

public class InternshipOpportunity {
    private final String internshipID;
    private final String title;
    private final String description;
    private final InternshipLevel level;
    private final String preferredMajor;
    private OpportunityStatus status;
    private final String companyName;
    private final String repID;
    private final int totalSlots;
    private int slotsAvailable;
    private boolean visible;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private final List<InternshipApplication> applications = new ArrayList<>();

    public InternshipOpportunity(String internshipID, String title, String description,
                                 InternshipLevel level, String preferredMajor,
                                 String companyName, String repID, int slotsAvailable, LocalDate openingDate,
                                 LocalDate closingDate) {
        this.internshipID   = Objects.requireNonNull(internshipID).trim();
        this.title          = Objects.requireNonNull(title).trim();
        this.description    = description == null ? "" : description.trim();
        this.level          = Objects.requireNonNull(level);
        this.preferredMajor = Objects.requireNonNull(preferredMajor).trim();
        this.companyName    = Objects.requireNonNull(companyName).trim();
        this.repID          = repID == null ? "" : repID.trim();
        int clampedSlots = Math.max(1, Math.min(slotsAvailable, 10));
        this.totalSlots     = clampedSlots;
        this.slotsAvailable = clampedSlots;
        this.status         = OpportunityStatus.PENDING_APPROVAL;
        this.visible        = false;
        if (openingDate == null || closingDate == null) {
            throw new IllegalArgumentException("Opening and closing dates are required.");
        }
        if (closingDate.isBefore(openingDate)) {
            throw new IllegalArgumentException("Closing date cannot be before opening date.");
        }
        this.openingDate = openingDate;
        this.closingDate = closingDate;
    }

    public void addApplication(InternshipApplication app) {
        if (app == null) return;
        applications.add(app);
        }
 

    public String getInternshipID()         { return internshipID; }
    public String getTitle()                { return title; }
    public String getDescription()          { return description; }
    public InternshipLevel getLevel()       { return level; }
    public String getPreferredMajor()       { return preferredMajor; }
    public OpportunityStatus getStatus()    { return status; }
    public void setStatus(OpportunityStatus status) { this.status = status; }
    public String getCompanyName()          { return companyName; }
    public String getRepID()                { return repID; }
    public int getSlotsAvailable()          { return slotsAvailable; }
    public int getTotalSlots()              {return totalSlots;}
    public boolean isVisible()              { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public List<InternshipApplication> getApplications() { return applications; }

    public void recalculateSlotsAndStatus() {
        int confirmedCount = 0;
        for (InternshipApplication app : applications) {
            if (app.getStatus() == ApplicationStatus.CONFIRMED) {
                confirmedCount++;
            }
        }

        slotsAvailable = Math.max(0, totalSlots - confirmedCount);

        if (status == OpportunityStatus.APPROVED || status == OpportunityStatus.FILLED) {
            if (slotsAvailable == 0) {
                status = OpportunityStatus.FILLED;
            } else {
                status = OpportunityStatus.APPROVED;
            }
        }
    }
    
    public LocalDate getOpeningDate() { return openingDate; }
    public LocalDate getClosingDate() { return closingDate; }
    
    @Override
    public String toString() {
        return "[%s] %s | Level: %s | Major: %s | Company: %s | Slots: %d | Status: %s | Visible: %s | Closing Date: %s"
                .formatted(internshipID, title, level, preferredMajor, companyName,
                           slotsAvailable, status, visible, closingDate);
    }
}

