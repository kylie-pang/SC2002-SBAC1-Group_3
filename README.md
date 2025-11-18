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
SC2002 PROJECT/
├── Main.java                       # Entry point – starts SystemController
│
├── SystemController.java           # High-level coordinator: login, routing to controllers
│
├── User.java                       # Abstract base user class (ID, name, email, password)
├── Student.java                    # Student entity & application-related logic
├── CompanyRepresentative.java      # Company rep entity & internship management
├── CareerCenterStaff.java          # Staff entity & approval / withdrawal actions
│
├── ApplicationStatus.java          # Enum: PENDING, SUCCESSFUL, UNSUCCESSFUL, WITHDRAWN, etc.
├── OpportunityStatus.java          # Enum: PENDING_APPROVAL, APPROVED, REJECTED, FILLED
├── InternshipLevel.java            # Enum: BASIC, INTERMEDIATE, ADVANCED
│
├── InternshipOpportunity.java      # Internship listing: status, level, preferred major, slots, visibility
├── InternshipApplication.java      # Represents a student’s application to an opportunity
│
├── UserManager.java                # Stores and finds all users (students, reps, staff)
├── OpportunityManager.java         # Stores and queries internship opportunities
├── ApplicationManager.java         # Stores and retrieves internship applications
│
├── UserController.java             # Abstract base controller for shared menu/auth behaviour
├── StudentController.java          # Handles student flows (view/apply/withdraw/confirm)
├── CompanyRepController.java       # Handles company rep flows (postings, visibility, approvals)
├── StaffController.java            # Handles staff flows (approvals, withdrawals, reporting)
│
├── HasMenu.java                    # Small helper/interface to standardise menu display
├── InternshipFilterSettings.java   # Stores per-user filter settings for viewing opportunities
│
├── CsvReader.java                  # Low-level CSV reader utility (generic)
├── FileHandler.java                # Higher-level CSV helpers (loading domain objects)
├── StudentLoader.java              # Builds Student objects from CSV
├── StaffLoader.java                # Builds CareerCenterStaff objects from CSV
├── CompanyRepLoader.java           # Builds CompanyRepresentative objects from CSV (optional)
│
├── sample_student_list.csv         # Sample student data
├── sample_staff_list.csv           # Sample staff data
└── sample_company_representative_list.csv  # Sample company rep data (if used)
