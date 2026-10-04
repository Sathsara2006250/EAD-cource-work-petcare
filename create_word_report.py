import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn

def set_cell_background(cell, hex_color):
    """Set cell background color in hex format (e.g. '0F766E')"""
    shading_elm = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{hex_color}"/>')
    cell._tc.get_or_add_tcPr().append(shading_elm)

def set_cell_margins(cell, top=100, bottom=100, left=150, right=150):
    """Set cell margins in dxa (1 pt = 20 dxa)"""
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = OxmlElement('w:tcMar')
    for margin_name, val in [('top', top), ('bottom', bottom), ('left', left), ('right', right)]:
        node = OxmlElement(f'w:{margin_name}')
        node.set(qn('w:w'), str(val))
        node.set(qn('w:type'), 'dxa')
        tcMar.append(node)
    tcPr.append(tcMar)

def add_styled_heading(doc, text, level):
    h = doc.add_heading(text, level=level)
    run = h.runs[0]
    if level == 1:
        run.font.name = 'Segoe UI'
        run.font.size = Pt(17)
        run.font.bold = True
        run.font.color.rgb = RGBColor(15, 118, 110) # Teal Dark
        h.paragraph_format.space_before = Pt(20)
        h.paragraph_format.space_after = Pt(8)
    elif level == 2:
        run.font.name = 'Segoe UI'
        run.font.size = Pt(13)
        run.font.bold = True
        run.font.color.rgb = RGBColor(30, 41, 59) # Slate Dark
        h.paragraph_format.space_before = Pt(14)
        h.paragraph_format.space_after = Pt(6)
    elif level == 3:
        run.font.name = 'Segoe UI'
        run.font.size = Pt(11)
        run.font.bold = True
        run.font.color.rgb = RGBColor(51, 65, 85)
        h.paragraph_format.space_before = Pt(10)
        h.paragraph_format.space_after = Pt(4)
    return h

def add_p(doc, text, bold_prefix="", space_after=6):
    p = doc.add_paragraph()
    p.paragraph_format.space_after = Pt(space_after)
    p.paragraph_format.line_spacing = 1.15
    if bold_prefix:
        r_prefix = p.add_run(bold_prefix)
        r_prefix.font.name = 'Segoe UI'
        r_prefix.font.size = Pt(10)
        r_prefix.font.bold = True
        r_prefix.font.color.rgb = RGBColor(15, 23, 42)
    r = p.add_run(text)
    r.font.name = 'Segoe UI'
    r.font.size = Pt(10)
    r.font.color.rgb = RGBColor(51, 65, 85)
    return p

def add_code_block(doc, code_text):
    table = doc.add_table(rows=1, cols=1)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False
    
    cell = table.cell(0, 0)
    cell.width = Inches(6.5)
    set_cell_background(cell, "0F172A") # Dark Navy/Slate
    set_cell_margins(cell, top=140, bottom=140, left=180, right=180)
    
    p = cell.paragraphs[0]
    p.paragraph_format.space_before = Pt(2)
    p.paragraph_format.space_after = Pt(2)
    p.paragraph_format.line_spacing = 1.15
    
    run = p.add_run(code_text)
    run.font.name = 'Consolas'
    run.font.size = Pt(8.5)
    run.font.color.rgb = RGBColor(241, 245, 249) # Light text
    
    # spacing after table
    p_after = doc.add_paragraph()
    p_after.paragraph_format.space_after = Pt(6)

def add_callout(doc, title, text):
    table = doc.add_table(rows=1, cols=1)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False
    
    cell = table.cell(0, 0)
    cell.width = Inches(6.5)
    set_cell_background(cell, "F0FDFA") # Light Mint
    set_cell_margins(cell, top=140, bottom=140, left=180, right=180)
    
    p = cell.paragraphs[0]
    p.paragraph_format.space_after = Pt(3)
    r_t = p.add_run(title + "\n")
    r_t.font.name = 'Segoe UI'
    r_t.font.bold = True
    r_t.font.size = Pt(10)
    r_t.font.color.rgb = RGBColor(15, 118, 110)
    
    r_c = p.add_run(text)
    r_c.font.name = 'Segoe UI'
    r_c.font.size = Pt(9.5)
    r_c.font.color.rgb = RGBColor(51, 65, 85)
    
    p_after = doc.add_paragraph()
    p_after.paragraph_format.space_after = Pt(6)

def create_table(doc, headers, data, col_widths=None):
    table = doc.add_table(rows=len(data) + 1, cols=len(headers))
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False
    
    # Style Header
    hdr_cells = table.rows[0].cells
    for i, title in enumerate(headers):
        hdr_cells[i].text = title
        set_cell_background(hdr_cells[i], "0F766E") # Teal header
        set_cell_margins(hdr_cells[i], top=120, bottom=120, left=120, right=120)
        p = hdr_cells[i].paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.LEFT
        for run in p.runs:
            run.font.name = 'Segoe UI'
            run.font.size = Pt(9)
            run.font.bold = True
            run.font.color.rgb = RGBColor(255, 255, 255)
            
    # Populate Rows
    for r_idx, row_data in enumerate(data):
        row_cells = table.rows[r_idx + 1].cells
        bg_color = "F8FAFC" if r_idx % 2 == 1 else "FFFFFF"
        for c_idx, val in enumerate(row_data):
            row_cells[c_idx].text = str(val)
            set_cell_background(row_cells[c_idx], bg_color)
            set_cell_margins(row_cells[c_idx], top=100, bottom=100, left=120, right=120)
            p = row_cells[c_idx].paragraphs[0]
            p.alignment = WD_ALIGN_PARAGRAPH.LEFT
            for run in p.runs:
                run.font.name = 'Segoe UI'
                run.font.size = Pt(8.5)
                run.font.color.rgb = RGBColor(30, 41, 59)
                
    # Column widths
    if col_widths:
        for row in table.rows:
            for idx, width in enumerate(col_widths):
                row.cells[idx].width = width

    doc.add_paragraph().paragraph_format.space_after = Pt(6)

def build_word_report():
    doc = docx.Document()
    
    # Page setup - Standard 1 inch margins
    sections = doc.sections
    for section in sections:
        section.top_margin = Inches(1.0)
        section.bottom_margin = Inches(1.0)
        section.left_margin = Inches(1.0)
        section.right_margin = Inches(1.0)
        section.different_first_page_header_footer = True
        
        # Header / Footer
        footer = section.footer
        f_p = footer.paragraphs[0]
        f_p.alignment = WD_ALIGN_PARAGRAPH.RIGHT
        f_run = f_p.add_run("PetCare Veterinary ERP — Coursework Technical Report")
        f_run.font.name = 'Segoe UI'
        f_run.font.size = Pt(8.5)
        f_run.font.color.rgb = RGBColor(148, 163, 184)

    # ----------------------------------------------------
    # COVER PAGE
    # ----------------------------------------------------
    p_badge = doc.add_paragraph()
    p_badge.paragraph_format.space_before = Pt(40)
    p_badge.paragraph_format.space_after = Pt(10)
    p_badge.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_badge = p_badge.add_run("ENTERPRISE APPLICATION DEVELOPMENT (EAD)")
    r_badge.font.name = 'Segoe UI'
    r_badge.font.size = Pt(12)
    r_badge.font.bold = True
    r_badge.font.color.rgb = RGBColor(13, 148, 136)

    p_title = doc.add_paragraph()
    p_title.paragraph_format.space_after = Pt(6)
    p_title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_title = p_title.add_run("PET CARE & VETERINARY SERVICES\nCLINICAL MANAGEMENT SYSTEM")
    r_title.font.name = 'Segoe UI'
    r_title.font.size = Pt(22)
    r_title.font.bold = True
    r_title.font.color.rgb = RGBColor(15, 118, 110)

    p_sub = doc.add_paragraph()
    p_sub.paragraph_format.space_after = Pt(40)
    p_sub.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_sub = p_sub.add_run("Desktop Enterprise Application & Technical Coursework Report")
    r_sub.font.name = 'Segoe UI'
    r_sub.font.size = Pt(13)
    r_sub.font.color.rgb = RGBColor(100, 116, 139)

    # Metadata Table
    meta_table = doc.add_table(rows=6, cols=2)
    meta_table.alignment = WD_TABLE_ALIGNMENT.CENTER
    meta_data = [
        ("Coursework Module", "Enterprise Application Development (EAD)"),
        ("Application Domain", "Pet Care & Veterinary Services Management"),
        ("Technology Stack", "Java 21 LTS, Java Swing, FlatLaf 3.4, MySQL 8.0+"),
        ("Software Architecture", "Layered Architecture / MVC & DAO Design Patterns"),
        ("Enterprise Reporting", "JasperReports 6.21.3 (4-Table Relational JOIN)"),
        ("Academic Year", "2025 / 2026")
    ]
    for idx, (label, val) in enumerate(meta_data):
        row = meta_table.rows[idx]
        row.cells[0].width = Inches(2.2)
        row.cells[1].width = Inches(3.8)
        set_cell_background(row.cells[0], "F0FDFA")
        set_cell_background(row.cells[1], "F8FAFC")
        set_cell_margins(row.cells[0], top=80, bottom=80, left=120, right=120)
        set_cell_margins(row.cells[1], top=80, bottom=80, left=120, right=120)
        
        p0 = row.cells[0].paragraphs[0]
        r0 = p0.add_run(label)
        r0.font.name = 'Segoe UI'
        r0.font.bold = True
        r0.font.size = Pt(9.5)
        r0.font.color.rgb = RGBColor(15, 118, 110)
        
        p1 = row.cells[1].paragraphs[0]
        r1 = p1.add_run(val)
        r1.font.name = 'Segoe UI'
        r1.font.size = Pt(9.5)
        r1.font.color.rgb = RGBColor(30, 41, 59)

    doc.add_page_break()

    # ----------------------------------------------------
    # EXECUTIVE SUMMARY & TABLE OF CONTENTS
    # ----------------------------------------------------
    add_styled_heading(doc, "EXECUTIVE SUMMARY", level=1)
    add_p(doc, "The Pet Care & Veterinary Services Clinical Management System is an enterprise desktop solution developed in Java Swing, backed by a relational MySQL database. The software automates clinical administration for modern veterinary clinics, eliminating scheduling collisions, unifying fragmented patient and pet records, dynamically calculating medical treatment costs, and compiling clinical audit reports using JasperReports.")
    add_p(doc, "Adhering strictly to enterprise software engineering standards, the system incorporates:")
    add_p(doc, "Layered MVC Architecture cleanly separating presentation, business logic, and database persistence.", bold_prefix="• ")
    add_p(doc, "Gang of Four (GoF) Design Patterns: Singleton Pattern for database connections and Data Access Object (DAO) Pattern for modular CRUD operations.", bold_prefix="• ")
    add_p(doc, "Role-Based Access Control (RBAC) providing tailored workflows for Managers (full clinical, financial, and reporting access) and Customers (pet registration and self-service appointment booking).", bold_prefix="• ")
    add_p(doc, "Two Complex Multi-Step Transactions: Transaction 1 (Appointment Booking with Doctor Collision Detection) and Transaction 2 (Clinical Treatment Invoicing with Dynamic Live Billing).", bold_prefix="• ")
    add_p(doc, "Comprehensive Validation & Custom Exceptions: Domain-specific checked exceptions (InvalidAppointmentException) and regex data validation.", bold_prefix="• ")
    add_p(doc, "JasperReports Multi-Table Audit Engine: Executing an advanced 4-table relational SQL JOIN (appointments, customers, pets, treatments) capable of interactive preview, PDF, and CSV export.", bold_prefix="• ")
    add_p(doc, "Automated Deployment Packaging: Includes scripts to generate an executable fat JAR and a standalone native Windows EXE application.", bold_prefix="• ")

    add_styled_heading(doc, "TABLE OF CONTENTS", level=1)
    toc_items = [
        "Chapter 1: Introduction & Domain Analysis",
        "Chapter 2: System Requirements & User Roles",
        "Chapter 3: System Architecture & Software Design Patterns",
        "Chapter 4: Relational Database Design & Schema",
        "Chapter 5: User Interface Implementation & Transactions",
        "Chapter 6: JasperReports Enterprise Reporting",
        "Chapter 7: Input Validation & System Security",
        "Chapter 8: Build Automation, Packaging & Deployment",
        "Chapter 9: Software Verification & Test Cases",
        "Chapter 10: Conclusion & Viva Voce Preparation Guide"
    ]
    for item in toc_items:
        add_p(doc, item, space_after=3)

    doc.add_page_break()

    # ----------------------------------------------------
    # CHAPTER 1: INTRODUCTION & DOMAIN ANALYSIS
    # ----------------------------------------------------
    add_styled_heading(doc, "CHAPTER 1: INTRODUCTION & DOMAIN ANALYSIS", level=1)
    add_styled_heading(doc, "1.1 Domain Background", level=2)
    add_p(doc, "Veterinary practices provide essential healthcare services, preventive medicine, surgery, diagnostics, and wellness programs for companion animals. Unlike conventional human clinics where patients articulate their medical history, veterinary practices require extensive parent (owner) profiling, animal breed/species characterization, and systematic doctor visit scheduling. In a bustling clinical environment, efficient record-keeping is vital for patient survival, medical accountability, and business profitability.")

    add_styled_heading(doc, "1.2 Problem Statement", level=2)
    add_p(doc, "Traditional paper-based records or unintegrated spreadsheet systems in veterinary clinics present significant operational bottlenecks:")
    add_p(doc, "Doctor Double Bookings: Overlapping appointments scheduled for the same veterinary doctor during identical timeslots, leading to client frustration and long waiting room delays.", bold_prefix="1. ")
    add_p(doc, "Disconnected Medical Histories: Lack of relational linkages between pet owner contacts, individual animal medical profiles, past appointments, and clinical procedures.", bold_prefix="2. ")
    add_p(doc, "Billing Discrepancies: Manual calculation of separate medication fees and procedural costs leading to revenue leakages and inaccurate invoices.", bold_prefix="3. ")
    add_p(doc, "Lack of Executive Oversight: Inability for clinic managers to audit revenue generated by specific species, doctors, or treatment procedures without tedious manual recalculations.", bold_prefix="4. ")

    add_styled_heading(doc, "1.3 Project Aims & Objectives", level=2)
    add_p(doc, "The overarching aim of this project is to develop an enterprise-grade desktop application that automates clinical operations, pet patient registration, appointment workflows, and billing management. The key objectives are:")
    add_p(doc, "Implement a responsive Java Swing user interface utilizing the modern FlatLaf design system.", bold_prefix="• ")
    add_p(doc, "Enforce Role-Based Access Control (RBAC) supporting distinct Manager and Customer roles.", bold_prefix="• ")
    add_p(doc, "Implement complete CRUD operations backed by a normalized MySQL relational database.", bold_prefix="• ")
    add_p(doc, "Implement two distinct transaction workflows enforcing ACID properties and business logic validation.", bold_prefix="• ")
    add_p(doc, "Deliver an integrated JasperReports module querying a 4-table join to output audit reports in viewer, PDF, and CSV formats.", bold_prefix="• ")
    add_p(doc, "Provide automated build scripts to compile executable JAR and native Windows EXE distribution packages.", bold_prefix="• ")

    doc.add_page_break()

    # ----------------------------------------------------
    # CHAPTER 2: SYSTEM REQUIREMENTS & USER ROLES
    # ----------------------------------------------------
    add_styled_heading(doc, "CHAPTER 2: SYSTEM REQUIREMENTS & USER ROLES", level=1)
    add_styled_heading(doc, "2.1 Functional Requirements (FR)", level=2)
    add_p(doc, "FR-01 (Authentication): The system must authenticate users via encrypted or hashed credentials and grant access according to assigned role.")
    add_p(doc, "FR-02 (Owner Management): Managers must be able to perform full CRUD operations on Pet Owners (Customers), with validation for names, unique emails, and valid phone numbers.")
    add_p(doc, "FR-03 (Pet Management): Managers and Customers must be able to register pets, linking them to an owner and specifying species, breed, age, gender, and weight.")
    add_p(doc, "FR-04 (Transaction 1 - Appointment Scheduling): The system must generate unique appointment codes, provide cascading owner-to-pet selection, prevent past dates, and reject timeslot collisions for the same doctor.")
    add_p(doc, "FR-05 (Transaction 2 - Clinical Billing): Doctors/Managers must record diagnosis, procedures, and prescribed medications for appointments, calculating total fees dynamically.")
    add_p(doc, "FR-06 (Search & Filter): Users must be able to perform real-time searches across customers, pets, appointments, and billing tables.")
    add_p(doc, "FR-07 (Dashboard Analytics): The clinic manager must have access to real-time KPI metrics (total owners, pets, appointments, treatments, and gross revenue).")
    add_p(doc, "FR-08 (Enterprise Reporting): The system must generate a formatted JasperReport combining appointments, customers, pets, and treatments.")

    add_styled_heading(doc, "2.2 Role-Based Access Control (RBAC) Matrix", level=2)
    rbac_headers = ["Module / Feature", "Manager Role", "Customer Role", "Operational Description"]
    rbac_data = [
        ["System Authentication", "Full Access", "Full Access", "Role-based credential verification & redirection"],
        ["Clinic Dashboard", "Full Access", "Restricted", "Displays clinic-wide financial KPIs and appointments"],
        ["Pet Owner Profiles", "Full CRUD", "Restricted", "Client personal contacts and residential addresses"],
        ["Patient Pet Directory", "Full CRUD", "View/Add Own", "Animal profiles, breed, weight, medical notes"],
        ["Appointment Booking (Tx 1)", "Full Management", "Book Own Pets", "Scheduling veterinary visits with conflict check"],
        ["Treatment & Billing (Tx 2)", "Full CRUD", "View Invoices", "Clinical diagnoses and dynamic billing calculations"],
        ["JasperReports Audit Engine", "Full Access", "Restricted", "4-Table join audit reports with PDF/CSV export"]
    ]
    create_table(doc, rbac_headers, rbac_data, [Inches(1.8), Inches(1.0), Inches(1.1), Inches(2.6)])

    doc.add_page_break()

    # ----------------------------------------------------
    # CHAPTER 3: SYSTEM ARCHITECTURE & SOFTWARE DESIGN PATTERNS
    # ----------------------------------------------------
    add_styled_heading(doc, "CHAPTER 3: SYSTEM ARCHITECTURE & SOFTWARE DESIGN PATTERNS", level=1)
    add_styled_heading(doc, "3.1 Layered MVC Architecture", level=2)
    add_p(doc, "The software architecture is partitioned into four distinct layers following the Model-View-Controller (MVC) paradigm to enforce high cohesion and loose coupling:")
    add_p(doc, "1. Presentation Layer (com.petcare.view): Contains all Java Swing windows and panels (LoginFrame, DashboardPanel, AppointmentManagementPanel, etc.) enhanced with FlatLaf modern UI components.")
    add_p(doc, "2. Business Service Layer (com.petcare.service): Implements domain business logic, validation rules, collision checking, and transaction orchestration.")
    add_p(doc, "3. Data Access Object (DAO) Layer (com.petcare.dao): Encapsulates raw JDBC SQL statements and PreparedStatements, translating database rows into Java models.")
    add_p(doc, "4. Domain Model Layer (com.petcare.model): Plain Old Java Objects (POJOs) representing domain entities (User, Customer, Pet, Appointment, Treatment, ReportRow).")

    add_styled_heading(doc, "3.2 Singleton Design Pattern (DatabaseConnection)", level=2)
    add_p(doc, "Opening database connections is computationally expensive. The Singleton pattern ensures that only one centralized database connection instance is instantiated and shared across all DAO operations:")
    add_code_block(doc, """public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws DatabaseException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            throw new DatabaseException("Failed to establish database connection.", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() throws DatabaseException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}""")

    add_styled_heading(doc, "3.3 Custom Exception Handling Hierarchy", level=2)
    add_p(doc, "Rather than relying on generic RuntimeExceptions or uncaught SQLExceptions, the system defines custom domain exceptions:")
    add_p(doc, "InvalidAppointmentException: Thrown when an appointment booking violates scheduling rules (past date selection, doctor double-booking).", bold_prefix="• ")
    add_p(doc, "ValidationException: Thrown when input data fails format requirements (invalid emails, malformed phone numbers, negative numbers).", bold_prefix="• ")
    add_p(doc, "DatabaseException: Wraps low-level JDBC errors and provides user-friendly error messages.", bold_prefix="• ")

    doc.add_page_break()

    # ----------------------------------------------------
    # CHAPTER 4: RELATIONAL DATABASE DESIGN & SCHEMA
    # ----------------------------------------------------
    add_styled_heading(doc, "CHAPTER 4: RELATIONAL DATABASE DESIGN & SCHEMA", level=1)
    add_styled_heading(doc, "4.1 Database Normalization Analysis (1NF, 2NF, 3NF)", level=2)
    add_p(doc, "The database schema petcare_db is fully normalized to Third Normal Form (3NF):")
    add_p(doc, "First Normal Form (1NF): All attributes contain atomic values, all tables have a designated Primary Key (id), and repeating groups are eliminated.", bold_prefix="• ")
    add_p(doc, "Second Normal Form (2NF): Every table is in 1NF and all non-key attributes are fully functionally dependent on the primary key, eliminating partial dependencies.", bold_prefix="• ")
    add_p(doc, "Third Normal Form (3NF): Every table is in 2NF and contains no transitive dependencies. Customer contact info is stored strictly in customers and referenced via customer_id in appointments.", bold_prefix="• ")

    add_styled_heading(doc, "4.2 Relational Data Dictionary", level=2)
    dict_headers = ["Table", "Field Name", "Data Type", "Constraints", "Description"]
    dict_data = [
        ["users", "id", "INT", "PK, AUTO_INCREMENT", "Unique user identifier"],
        ["users", "username", "VARCHAR(50)", "UNIQUE, NOT NULL", "Login account username"],
        ["users", "role", "ENUM", "Manager, Customer", "Role-based access permission"],
        ["customers", "id", "INT", "PK, AUTO_INCREMENT", "Unique customer identifier"],
        ["customers", "full_name", "VARCHAR(100)", "NOT NULL", "Pet owner legal full name"],
        ["customers", "email", "VARCHAR(100)", "NOT NULL", "Primary contact email address"],
        ["customers", "phone", "VARCHAR(20)", "NOT NULL", "Primary telephone number"],
        ["pets", "id", "INT", "PK, AUTO_INCREMENT", "Unique animal patient identifier"],
        ["pets", "customer_id", "INT", "FK -> customers(id)", "Associated pet owner link"],
        ["pets", "name", "VARCHAR(50)", "NOT NULL", "Pet call name"],
        ["pets", "species", "VARCHAR(30)", "NOT NULL", "Species classification (Dog, Cat, etc.)"],
        ["appointments", "id", "INT", "PK, AUTO_INCREMENT", "Unique appointment identifier"],
        ["appointments", "appointment_code", "VARCHAR(20)", "UNIQUE, NOT NULL", "Human-readable tracking code"],
        ["appointments", "vet_doctor", "VARCHAR(100)", "NOT NULL", "Attending veterinary doctor"],
        ["appointments", "status", "ENUM", "Scheduled, Completed..", "Current appointment state"],
        ["treatments", "id", "INT", "PK, AUTO_INCREMENT", "Unique clinical treatment ID"],
        ["treatments", "appointment_id", "INT", "FK -> appointments, UK", "Linked appointment reference"],
        ["treatments", "medication_cost", "DECIMAL(10,2)", "DEFAULT 0.00", "Billed medication total fee"],
        ["treatments", "treatment_cost", "DECIMAL(10,2)", "DEFAULT 0.00", "Billed medical procedure fee"],
        ["treatments", "total_amount", "DECIMAL(10,2)", "DEFAULT 0.00", "Gross billed invoice total"]
    ]
    create_table(doc, dict_headers, dict_data, [Inches(1.1), Inches(1.3), Inches(1.2), Inches(1.4), Inches(1.5)])

    doc.add_page_break()

    # ----------------------------------------------------
    # CHAPTER 5: USER INTERFACE IMPLEMENTATION & TRANSACTIONS
    # ----------------------------------------------------
    add_styled_heading(doc, "CHAPTER 5: USER INTERFACE IMPLEMENTATION & TRANSACTIONS", level=1)
    add_styled_heading(doc, "5.1 System Screen Inventory (6 Interfaces)", level=2)
    add_p(doc, "The application implements six distinct user interfaces matching all syllabus recommendations:")
    add_p(doc, "1. Login Screen (LoginFrame): Secure authentication with role-based routing.", bold_prefix="• ")
    add_p(doc, "2. Executive Clinic Dashboard (DashboardPanel): Real-time KPI summary cards and active appointment schedule.", bold_prefix="• ")
    add_p(doc, "3. Pet Owner Management (CustomerManagementPanel): Full CRUD profile forms and client directory.", bold_prefix="• ")
    add_p(doc, "4. Patient Pet Directory (PetManagementPanel): Animal profiling with species, breed, and health notes.", bold_prefix="• ")
    add_p(doc, "5. Transaction 1 – Appointment Booking (AppointmentManagementPanel): Cascading pet combo selection and doctor collision detection.", bold_prefix="• ")
    add_p(doc, "6. Transaction 2 – Treatment & Billing (TreatmentBillingPanel): Clinical diagnosis, procedure documentation, and real-time live billing.", bold_prefix="• ")

    add_styled_heading(doc, "5.2 Transaction 1: Appointment Booking with Conflict Detection", level=2)
    add_p(doc, "When scheduling an appointment, the operator selects an owner, which triggers cascading filtering: only pets belonging to that specific owner appear in the pet dropdown. When the operator submits the appointment, the service layer queries existing bookings to prevent double-booking the doctor:")
    add_code_block(doc, """if (appointmentDAO.isDoctorDoubleBooked(doctor, date, time)) {
    throw new InvalidAppointmentException(
        "Dr. " + doctor + " already has an appointment scheduled on " + date + " at " + time + "."
    );
}""")

    add_styled_heading(doc, "5.3 Transaction 2: Treatment & Live Dynamic Billing", level=2)
    add_p(doc, "In the Treatment & Billing interface, KeyListeners are attached to medication and procedure fee fields. As values are typed, the system recalculates total fees in real time without requiring a manual calculate button:")
    add_code_block(doc, """KeyAdapter calcListener = new KeyAdapter() {
    @Override
    public void keyReleased(KeyEvent e) {
        calculateTotalLive(); // Total Bill = Medication Fee + Procedure Fee
    }
};
txtMedCost.addKeyListener(calcListener);
txtTreatCost.addKeyListener(calcListener);""")

    doc.add_page_break()

    # ----------------------------------------------------
    # CHAPTER 6: JASPERREPORTS ENTERPRISE REPORTING
    # ----------------------------------------------------
    add_styled_heading(doc, "CHAPTER 6: JASPERREPORTS ENTERPRISE REPORTING", level=1)
    add_styled_heading(doc, "6.1 Multi-Table SQL Join Requirement", level=2)
    add_p(doc, "The coursework specification requires at least one Jasper Report utilizing joins from more than two tables. The system exceeds this requirement by executing a 4-table relational join:")
    add_code_block(doc, """SELECT 
    a.appointment_code, 
    a.appointment_date, 
    c.full_name AS customer_name, 
    c.phone AS customer_phone, 
    p.name AS pet_name, 
    p.species, 
    p.breed, 
    a.vet_doctor, 
    t.diagnosis, 
    t.procedure_details, 
    t.medication_cost, 
    t.treatment_cost, 
    t.total_amount, 
    t.status AS payment_status
FROM appointments a
JOIN customers c ON a.customer_id = c.id
JOIN pets p ON a.pet_id = p.id
JOIN treatments t ON t.appointment_id = a.id
ORDER BY a.appointment_date DESC;""")

    add_styled_heading(doc, "6.2 Report Architecture & Programmatic Integration", level=2)
    add_p(doc, "The report is designed in VeterinaryServiceReport.jrxml with a landscape orientation, title banner, column headers, detail rows, and an automatic sum calculation ($V{TOTAL_REVENUE}) in the Summary band. ReportService.java manages compilation, filling, and viewing:")
    add_code_block(doc, """// 1. Compile JRXML template from classpath resource
InputStream is = getClass().getResourceAsStream("/reports/VeterinaryServiceReport.jrxml");
JasperReport jasperReport = JasperCompileManager.compileReport(is);

// 2. Fill report with active database connection
Connection conn = DatabaseConnection.getInstance().getConnection();
JasperPrint print = JasperFillManager.fillReport(jasperReport, new HashMap<>(), conn);

// 3. View in interactive JasperViewer or Export directly to PDF
JasperViewer.viewReport(print, false);
JasperExportManager.exportReportToPdfFile(print, targetPdfFile.getAbsolutePath());""")

    doc.add_page_break()

    # ----------------------------------------------------
    # CHAPTER 7 & 8: VALIDATION, SECURITY & DEPLOYMENT
    # ----------------------------------------------------
    add_styled_heading(doc, "CHAPTER 7: INPUT VALIDATION & SYSTEM SECURITY", level=1)
    add_p(doc, "ValidationHelper.java enforces data integrity across all input forms:")
    add_p(doc, "Email Validation: RFC 5322 compliant regex validating mailbox and domain syntax.", bold_prefix="• ")
    add_p(doc, "Phone Validation: Enforces 7 to 15 numeric digits with optional international prefix (+).", bold_prefix="• ")
    add_p(doc, "Non-Empty Field Verification: Strips whitespace and rejects blank submissions on mandatory fields.", bold_prefix="• ")
    add_p(doc, "Numeric Validation: Enforces positive integers for pet age and positive non-negative currency amounts for fees.", bold_prefix="• ")

    add_styled_heading(doc, "CHAPTER 8: BUILD AUTOMATION, PACKAGING & DEPLOYMENT", level=1)
    add_styled_heading(doc, "8.1 Executable Fat JAR Creation (Maven Shade Plugin)", level=2)
    add_p(doc, "The maven-shade-plugin bundles all project bytecode and third-party dependencies (MySQL JDBC connector, JasperReports, FlatLaf) into an uber-jar with an embedded Main-Class manifest entry:")
    add_code_block(doc, """# Build executable Fat JAR
call mvn clean package -DskipTests

# Run the packaged application
java -jar target/PetCare-1.0-SNAPSHOT.jar""")

    add_styled_heading(doc, "8.2 Standalone Windows Native Application Packaging (jpackage)", level=2)
    add_p(doc, "Using JDK 21 jpackage, the application compiles into a native Windows executable located at dist/PetCare/PetCare.exe. This standalone package runs on client Windows machines without requiring a pre-installed Java runtime.")

    doc.add_page_break()

    # ----------------------------------------------------
    # CHAPTER 9: SOFTWARE VERIFICATION & TEST CASES
    # ----------------------------------------------------
    add_styled_heading(doc, "CHAPTER 9: SOFTWARE VERIFICATION & TEST CASES", level=1)
    add_p(doc, "The system underwent systematic Black-Box and Integration testing to verify all business rules, transactions, and UI workflows:")
    
    test_headers = ["Test ID", "Scenario Description", "Input Data", "Expected Result", "Status"]
    test_data = [
        ["TC-01", "Manager Login", "admin / admin123 (Manager)", "Login succeeds; all 6 tabs unlocked", "PASS"],
        ["TC-02", "Customer Login", "john_doe / user123 (Customer)", "Login succeeds; admin tabs hidden", "PASS"],
        ["TC-03", "Invalid Credentials", "admin / wrongpass", "Error alert displayed; access denied", "PASS"],
        ["TC-04", "Email Validation", "user@bademail", "ValidationException thrown with guidance", "PASS"],
        ["TC-05", "Phone Validation", "1234", "ValidationException thrown: 7-15 digits", "PASS"],
        ["TC-06", "Cascading Pet Combo", "Select customer 'John Doe'", "Pet combo filters strictly to John's pets", "PASS"],
        ["TC-07", "Doctor Collision Check", "Same doctor, date, and timeslot", "InvalidAppointmentException thrown", "PASS"],
        ["TC-08", "Live Billing Calc", "Med: $35.00, Proc: $65.00", "Total Bill field updates instantly to $100.00", "PASS"],
        ["TC-09", "Jasper Report View", "Click 'View Jasper Report'", "JasperViewer opens showing 4-table join", "PASS"],
        ["TC-10", "PDF Report Export", "Click 'Export PDF'", "PDF file generated and saved to disk", "PASS"]
    ]
    create_table(doc, test_headers, test_data, [Inches(0.8), Inches(1.5), Inches(1.5), Inches(2.0), Inches(0.7)])

    doc.add_page_break()

    # ----------------------------------------------------
    # CHAPTER 10: CONCLUSION & VIVA VOCE PREPARATION GUIDE
    # ----------------------------------------------------
    add_styled_heading(doc, "CHAPTER 10: CONCLUSION & VIVA VOCE PREPARATION GUIDE", level=1)
    add_styled_heading(doc, "10.1 Rubric Compliance Checklist", level=2)
    rubric_headers = ["Coursework Requirement", "Implementation Status", "Project Evidence"]
    rubric_data = [
        ["Java Swing Enterprise Application", "100% Satisfied", "Complete Desktop GUI with FlatLaf modern styling"],
        ["Pet Care & Veterinary Domain", "100% Satisfied", "Clinical appointments, pet profiling, treatments"],
        ["At least 2 User Roles", "100% Satisfied", "Manager and Customer roles with RBAC enforcement"],
        ["At least 5 Interfaces (excl. Login)", "100% Satisfied", "6 Interfaces (Dashboard, Owners, Pets, Appts, Invoicing, Reports)"],
        ["At least 2 Transaction UIs", "100% Satisfied", "Tx 1 (Appt Booking) & Tx 2 (Treatment & Live Billing)"],
        ["Input Forms with Validation", "100% Satisfied", "Regex email/phone and numeric validation in ValidationHelper"],
        ["MySQL CRUD Operations", "100% Satisfied", "Normalized schema with full CRUD DAOs"],
        ["MVC & Design Patterns", "100% Satisfied", "MVC architecture, Singleton pattern, DAO pattern"],
        ["Jasper Report (> 2 Tables Joined)", "100% Satisfied", "Relational JOIN across 4 tables (appointments, customers, pets, treatments)"],
        ["Executable JAR & EXE Deployment", "100% Satisfied", "Maven Shade Fat JAR and jpackage Windows EXE"]
    ]
    create_table(doc, rubric_headers, rubric_data, [Inches(2.2), Inches(1.3), Inches(3.0)])

    add_styled_heading(doc, "10.2 Top Anticipated Viva Voce Questions & Answers", level=2)
    
    qa_list = [
        ("Q1: Explain the software architecture of your application.",
         "Our application implements the Model-View-Controller (MVC) and Layered Architecture pattern. The View layer (com.petcare.view) handles Swing UI presentation; the Service layer (com.petcare.service) encapsulates business rules and validation logic; the DAO layer (com.petcare.dao) executes SQL queries; and the Model layer (com.petcare.model) represents domain entities."),
        
        ("Q2: Where and why did you use the Singleton design pattern?",
         "We implemented the Singleton pattern in DatabaseConnection.java. Opening database connections is resource-intensive. The Singleton pattern ensures that only one centralized connection manager instance exists throughout the application lifecycle, conserving memory and database socket connections."),
        
        ("Q3: Explain how Transaction 1 enforces data integrity.",
         "In AppointmentManagementPanel, Transaction 1 books an appointment with cascading validation. It dynamically loads pets belonging only to the selected customer, verifies that the appointment date is not in the past, and executes collision detection to ensure the doctor is not double-booked for that timeslot. If a conflict occurs, it throws our custom InvalidAppointmentException."),
        
        ("Q4: How does your Jasper Report satisfy the multi-table join requirement?",
         "Our report query joins four relational tables: appointments a JOIN customers c ON a.customer_id = c.id JOIN pets p ON a.pet_id = p.id JOIN treatments t ON t.appointment_id = a.id. This satisfies and exceeds the coursework requirement of joining more than two tables, providing a complete clinical audit from pet owner down to billing amounts."),
        
        ("Q5: What custom exception did you build, and why?",
         "We implemented InvalidAppointmentException.java extending java.lang.Exception. It is triggered when business constraints are violated—such as scheduling in the past or scheduling overlapping doctor timeslots—allowing the UI to catch the exception and display a clear, domain-specific message to the user."),
        
        ("Q6: How do you prevent SQL Injection attacks in your application?",
         "All database interactions across our DAO classes exclusively use parametrized java.sql.PreparedStatement instances rather than concatenated SQL strings. Parameter placeholders (?) ensure that user inputs are treated strictly as data literals rather than executable SQL syntax.")
    ]
    for q, a in qa_list:
        add_callout(doc, q, a)

    # Save Document
    doc.save("PetCare_Coursework_Documentation_Report.docx")
    print("SUCCESS: PetCare_Coursework_Documentation_Report.docx generated successfully!")

if __name__ == "__main__":
    build_word_report()
