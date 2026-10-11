package com.school.management.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.school.management.entity.Attendance;
import com.school.management.entity.Student;
import com.school.management.entity.Subject;
import com.school.management.repository.AttendanceRepository;
import com.school.management.repository.StudentRepository;
import com.school.management.repository.SubjectRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.util.List;

@Service
public class ReportExportService {

    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentService studentService;

    public ReportExportService(StudentRepository studentRepository, SubjectRepository subjectRepository, AttendanceRepository attendanceRepository, StudentService studentService) {
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
        this.studentService = studentService;
    }

    public byte[] exportAttendanceExcel(Long subjectId) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Attendance Report");

            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectId));

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Student Roll No", "Student Name", "Date", "Period", "Status", "Remarks"};

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            List<Attendance> records = attendanceRepository.findBySubjectId(subjectId);
            int rowIdx = 1;
            for (Attendance a : records) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(a.getStudent().getRollNo());
                row.createCell(1).setCellValue(a.getStudent().getName());
                row.createCell(2).setCellValue(a.getDate().toString());
                row.createCell(3).setCellValue(a.getPeriod() != null ? a.getPeriod() : 1);
                row.createCell(4).setCellValue(a.getStatus().name());
                row.createCell(5).setCellValue(a.getRemark() != null ? a.getRemark() : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel report: " + e.getMessage());
        }
    }

    public byte[] exportAttendancePdf(Long subjectId) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectId));

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Paragraph title = new Paragraph("Attendance Report - " + subject.getName() + " (" + subject.getCode() + ")", titleFont);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.addCell("Roll No");
            table.addCell("Student Name");
            table.addCell("Date");
            table.addCell("Status");
            table.addCell("Remarks");

            List<Attendance> records = attendanceRepository.findBySubjectId(subjectId);
            for (Attendance a : records) {
                table.addCell(a.getStudent().getRollNo());
                table.addCell(a.getStudent().getName());
                table.addCell(a.getDate().toString());
                table.addCell(a.getStatus().name());
                table.addCell(a.getRemark() != null ? a.getRemark() : "");
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report: " + e.getMessage());
        }
    }

    public byte[] generateAttendanceCertificatePdf(String studentUsername) {
        Student student = studentRepository.findByUserUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found for username: " + studentUsername));

        double overallPct = studentService.getStudentPerformance(studentUsername).getOverallAttendancePercentage();
        if (overallPct < 75.0) {
            throw new RuntimeException("Certificate generation restricted: Student attendance (" + Math.round(overallPct) + "%) is below the required threshold of 75%.");
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(49, 46, 129));
            Paragraph header = new Paragraph("CERTIFICATE OF ACADEMIC ATTENDANCE", headerFont);
            header.setAlignment(Paragraph.ALIGN_CENTER);
            header.setSpacingAfter(30);
            document.add(header);

            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            Paragraph certBody = new Paragraph(
                    "This is to certify that " + student.getName() + " (Roll No: " + student.getRollNo() + "), " +
                    "enrolled in the Department of " + student.getDepartment() + " (Semester " + student.getSemester() + "), " +
                    "has successfully maintained an outstanding overall attendance record of " + String.format("%.2f", overallPct) + "% " +
                    "for the academic session.\n\n" +
                    "This certificate is issued in recognition of exemplary attendance and discipline.", bodyFont
            );
            certBody.setSpacingAfter(40);
            document.add(certBody);

            Paragraph sign = new Paragraph("Authorized Signature\nDean of Academic Affairs\nEduTrack Institution", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));
            sign.setAlignment(Paragraph.ALIGN_RIGHT);
            document.add(sign);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate certificate PDF: " + e.getMessage());
        }
    }
}
