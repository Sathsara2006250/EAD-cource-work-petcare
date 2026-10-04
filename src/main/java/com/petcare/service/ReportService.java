package com.petcare.service;

import com.petcare.dao.ReportDAO;
import com.petcare.exception.DatabaseException;
import com.petcare.model.ReportRow;
import com.petcare.util.DatabaseConnection;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Service for generating, viewing, and exporting Jasper Reports (Multi-table join).
 */
public class ReportService {

    private final ReportDAO reportDAO;

    public ReportService() {
        this.reportDAO = new ReportDAO();
    }

    public ReportService(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    public List<ReportRow> getReportData(String speciesFilter, String statusFilter) throws DatabaseException {
        return reportDAO.getComprehensiveVeterinaryReport(speciesFilter, statusFilter);
    }

    public void viewJasperReport() throws Exception {
        viewJasperReport("All Species", "All Statuses");
    }

    /**
     * Generates and compiles JasperReport with user-selected filters, then opens in interactive JasperViewer.
     */
    public void viewJasperReport(String speciesFilter, String statusFilter) throws Exception {
        JasperPrint jasperPrint = generateJasperPrint(speciesFilter, statusFilter);
        JasperViewer.viewReport(jasperPrint, false);
    }

    public void exportJasperReportToPdf(File targetPdfFile) throws Exception {
        exportJasperReportToPdf(targetPdfFile, "All Species", "All Statuses");
    }

    /**
     * Generates and exports JasperReport directly to a PDF file with active filters.
     */
    public void exportJasperReportToPdf(File targetPdfFile, String speciesFilter, String statusFilter) throws Exception {
        JasperPrint jasperPrint = generateJasperPrint(speciesFilter, statusFilter);
        JasperExportManager.exportReportToPdfFile(jasperPrint, targetPdfFile.getAbsolutePath());
    }

    private JasperPrint generateJasperPrint(String speciesFilter, String statusFilter) throws Exception {
        InputStream reportStream = getClass().getResourceAsStream("/reports/VeterinaryServiceReport.jrxml");
        if (reportStream == null) {
            throw new IllegalStateException("Report template /reports/VeterinaryServiceReport.jrxml not found in classpath.");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        Connection conn = DatabaseConnection.getInstance().getConnection();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("SPECIES_FILTER", (speciesFilter != null && !speciesFilter.trim().isEmpty()) ? speciesFilter.trim() : "All Species");
        parameters.put("STATUS_FILTER", (statusFilter != null && !statusFilter.trim().isEmpty()) ? statusFilter.trim() : "All Statuses");

        return JasperFillManager.fillReport(jasperReport, parameters, conn);
    }

    /**
     * Exports report data to a clean CSV file.
     */
    public void exportToCsv(List<ReportRow> data, File targetCsvFile) throws Exception {
        try (FileWriter writer = new FileWriter(targetCsvFile)) {
            writer.write("Appointment Code,Date,Owner Name,Phone,Email,Pet Name,Species,Breed,Veterinarian,Diagnosis,Procedure,Medication Cost,Treatment Cost,Total Amount,Status\n");
            for (ReportRow r : data) {
                writer.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%.2f,%.2f,%.2f,\"%s\"\n",
                    r.getAppointmentCode(),
                    r.getAppointmentDate(),
                    r.getCustomerName(),
                    r.getCustomerPhone(),
                    r.getCustomerEmail(),
                    r.getPetName(),
                    r.getSpecies(),
                    r.getBreed(),
                    r.getVetDoctor(),
                    r.getDiagnosis().replace("\"", "\"\""),
                    r.getProcedureDetails().replace("\"", "\"\""),
                    r.getMedicationCost(),
                    r.getTreatmentCost(),
                    r.getTotalAmount(),
                    r.getPaymentStatus()
                ));
            }
        }
    }
}
