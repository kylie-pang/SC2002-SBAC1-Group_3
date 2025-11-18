import java.time.LocalDate;
import java.util.Objects;

public class InternshipApplication {
    private final String applicationID;
    private final Student student;
    private final InternshipOpportunity opportunity;
    private ApplicationStatus status;
    private final LocalDate dateApplied;
    private String remarks;
    private ApplicationStatus previousStatus; // to restore after withdrawal rejection

    public InternshipApplication(Student student, InternshipOpportunity opportunity) {
        this.applicationID = "APP-" + System.currentTimeMillis();
        this.student       = Objects.requireNonNull(student);
        this.opportunity   = Objects.requireNonNull(opportunity);
        this.status        = ApplicationStatus.PENDING;
        this.dateApplied   = LocalDate.now();
        this.remarks       = "";
    }

    public String getApplicationID()           { return applicationID; }
    public Student getStudent()                { return student; }
    public InternshipOpportunity getOpportunity() { return opportunity; }
    public ApplicationStatus getStatus()       { return status; }
    public LocalDate getDateApplied()          { return dateApplied; }
    public String getRemarks()                 { return remarks; }

    public void setStatus(ApplicationStatus status) { this.status = status; }
    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? "" : remarks.trim();
    }

    @Override
    public String toString() {
        return "Application %s | Student: %s | Internship: %s | Status: %s | Date: %s"
                .formatted(applicationID, student.getUserID(), opportunity.getInternshipID(),
                           status, dateApplied);
    }

    
    public ApplicationStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(ApplicationStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

}

