# SC2002-SBAC1-Group_3 - Internship Management System

A command-line Java application for managing university internship workflows between **Students**, **Company Representatives**, and **Career Center Staff**.  

This project was developed as part of **SC2002 – Object-Oriented Design & Programming**, and showcases clean object-oriented design, role-based behaviour, and realistic modelling of an internship lifecycle.

---

## 🚀 Features

### 👨‍🎓 Student

- Login with student ID and default password (`password` on first login)
- View **only eligible** internship opportunities:
  - Approved by Career Center Staff  
  - Visible (`visible == true`)  
  - Matching student’s **major**  
  - Matching **InternshipLevel** eligibility (e.g. Y1–Y2 only BASIC)
- Apply for internships (max **3 active PENDING** applications)
- View application history and statuses:
  - `PENDING`, `SUCCESSFUL`, `UNSUCCESSFUL`, `WITHDRAW_REQUESTED`, `WITHDRAWN`, `CONFIRMED`
- Request withdrawal of applications (pending staff approval)
- Confirm an internship offer:
  - Only for `SUCCESSFUL` applications
  - Automatically withdraws all other active applications
  - Marks a **single accepted placement** per student
- Change password

---

### 🏢 Company Representative

- Self-registration with:
  - Name, company email (used as login ID), company name, department, position  
- Login only after **Career Center Staff approval**
- Create internship opportunities (max **5 active** per rep):
  - Title, description, preferred major, `InternshipLevel`, number of slots
  - Starts as `PENDING_APPROVAL` and `visible = false`
- Manage own internship listings:
  - **Edit** and **delete** opportunities **only while** `PENDING_APPROVAL`
  - Toggle listing **visibility** (on/off)
- Review student applications to their internships:
  - View all applications for a selected opportunity
  - Approve (`SUCCESSFUL`) or reject (`UNSUCCESSFUL`) applications
  - **Cannot** approve/reject applications that are `WITHDRAWN` or `WITHDRAW_REQUESTED`
  - Attach optional remarks when updating status
- View all opportunities they own regardless of visibility
- Edit their own profile (name, email, company name)
- Change password

---

### 🧑‍💼 Career Center Staff

- Login with staff ID
- Approve or reject:
  - **Company Representative accounts**
  - **Internship opportunities**
- Control internship visibility:
  - Approving an opportunity automatically makes it visible to eligible students
- Manage **student withdrawal requests**:
  - View all `WITHDRAW_REQUESTED` applications
  - Approve withdrawal → status becomes `WITHDRAWN`
  - Clear `acceptedPlacement` if the withdrawn application was confirmed
- Generate and filter reports on internship opportunities:
  - Filter by `OpportunityStatus`, `preferredMajor`, `InternshipLevel`, company name, etc.
- Change password

---

## 🧱 Project Structure

> Note: This is a simple console project using the default package.

```text
src/
├── Main.java                    # Entry point
├── SystemController.java        # Main controller: login, menus, flows
├── User.java                    # Abstract base class for all users
├── Student.java                 # Student user & application logic
├── CompanyRepresentative.java   # Company rep user & internship management
├── CareerCenterStaff.java       # Staff user & approval/withdrawal logic
│
├── InternshipOpportunity.java   # Internship listing (status, level, slots, visibility)
├── InternshipApplication.java   # Student’s application to an opportunity
├── ApplicationStatus.java       # Enum for application lifecycle
├── OpportunityStatus.java       # Enum for opportunity lifecycle
├── InternshipLevel.java         # Enum: BASIC / INTERMEDIATE / ADVANCED
│
├── UserManager.java             # Manages all users (students, reps, staff)
├── OpportunityManager.java      # Stores & queries internship opportunities
├── ApplicationManager.java      # Stores & queries internship applications
│
├── CsvReader.java               # Low-level CSV reader (generic)
├── FileHandler.java             # Example data-access helpers (optional)
├── StudentLoader.java           # Loads students from CSV
├── StaffLoader.java             # Loads staff from CSV
└── CompanyRepLoader.java        # (Optional) Load company reps from CSV
