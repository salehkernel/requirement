package ir.salmanian.io;

import ir.salmanian.models.Project;
import ir.salmanian.models.Requirement;
import ir.salmanian.services.RequirementService;
import ir.salmanian.utils.DocumentUtils;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class WordExporter implements Exporter {
    private Project project;

    public WordExporter(Project project) {
        this.project = project;
    }

    @Override
    public void exportToFile() {
        try {
            File file = chooseExportedFile();

            if (file != null) {
                if (!file.getName().endsWith(".docx")) {
                    file = new File(file.getAbsolutePath() + ".docx");
                }
                XWPFDocument document = new XWPFDocument();
                FileOutputStream outputStream = new FileOutputStream(file);
                writeDocumentTitle(document);
                writeRequirementsLevelByLevel(document);
                DocumentUtils.setDocumentFont(document, "Lateef");
                document.write(outputStream);
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File chooseExportedFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(String.format("%s.docx", project.getName()));
        fileChooser.setTitle("Export requirement word");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Word Documents(.docx)", "*.docx"));
        Stage stage = new Stage();
        File file = fileChooser.showSaveDialog(stage);
        return file;
    }

    private void writeRequirementsLevelByLevel(XWPFDocument document) {
        List<Requirement> requirementList = RequirementService.getInstance().getRequirements(project);
        Set<Requirement> requirementParentSet = new LinkedHashSet<>();
        Set<Requirement> requirementChildrenSet = new LinkedHashSet<>();
        Iterator<Requirement> requirementIterator = requirementList.iterator();
        while (requirementIterator.hasNext()) {
            Requirement req = requirementIterator.next();
            if (req.getParents().isEmpty()) {
                requirementParentSet.add(req);
            }
            requirementIterator.remove();
        }
        writeToDoc(document, requirementParentSet, 1);
        int level = 2;
        while (!requirementParentSet.isEmpty()) {
            Iterator<Requirement> it = requirementParentSet.iterator();
            while (it.hasNext()) {
                Requirement parent = it.next();
                List<Requirement> requirementList1 = RequirementService.getInstance().getChildrenRequirements(parent);
                if (!requirementList1.isEmpty())
                    requirementChildrenSet.addAll(requirementList1);
                it.remove();
            }

            writeToDoc(document, requirementChildrenSet, level++);
            requirementParentSet = requirementChildrenSet;
            requirementChildrenSet = new LinkedHashSet<>();
        }
    }

    private void writeDocumentTitle(XWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.setFontFamily("IRnazanin");
        run.setFontSize(20);
        run.setText(String.format("سند نیازمندی های %s", project.getName()));
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        DocumentUtils.setParagraphRTL(paragraph);
        run.addBreak();
    }

    private void writeToDoc(XWPFDocument document, Set<Requirement> requirements, int level) {
        if (requirements.size() != 0) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            paragraph.setAlignment(ParagraphAlignment.START);
            DocumentUtils.setParagraphRTL(paragraph);
            run.setText(String.format("نیازمندی‌های سطح %d", level));
            run.addBreak();
            for (Requirement requirement : requirements) {
                DocumentUtils.setParagraphNotSplit(paragraph);
                XWPFTable table = document.createTable(5, 4);
                table.setTableAlignment(TableRowAlign.RIGHT);
                XWPFTableRow row1 = table.getRow(0);
                row1.getCell(0).setText(String.format("شماره: %s-%s", requirement.getPrefix(), requirement.getId()));
                row1.getCell(1).setText(String.format("عنوان: %s", requirement.getTitle()));
                row1.setCantSplitRow(true);
                XWPFTableRow row2 = table.getRow(1);
                row2.getCell(0).setText(String.format("اولویت: %s", requirement.getPriority() == null ? "" : requirement.getPriority().label));
                row2.getCell(1).setText(String.format("نوع نیازمندی: %s", requirement.getRequirementType() == null ? "" : requirement.getRequirementType().label));
                row2.getCell(2).setText(String.format("فاکتور کیفی: %s", requirement.getQualityFactor() == null ? "" : requirement.getQualityFactor().toString().toLowerCase()));
                row2.getCell(3).setText(String.format("تغییرات: %s", requirement.getChanges() == null ? "" : requirement.getChanges().label));
                row2.setCantSplitRow(true);
                XWPFTableRow row3 = table.getRow(2);
                row3.getCell(0).setText(String.format("وضعیت بازبینی: %s", requirement.getReviewStatus() == null ? "" : requirement.getReviewStatus().label));
                row3.getCell(1).setText(String.format("روش ارزیابی: %s", requirement.getEvaluationMethod() == null ? "" : requirement.getEvaluationMethod().label));
                row3.getCell(2).setText(String.format("وضعیت ارزیابی: %s", requirement.getEvaluationStatus() == null ? "" : requirement.getEvaluationStatus().label));
                String parentsNumbers = "";
                for (Requirement parent : requirement.getParents()) {
                    parentsNumbers += String.format("%s-%s, ", parent.getPrefix(), parent.getId());
                }
                String childrenNumbers = "";
                for (Requirement child : RequirementService.getInstance().getChildrenRequirements(requirement)) {
                    childrenNumbers += String.format("%s-%s, ", child.getPrefix(), child.getId());
                }
                row3.setCantSplitRow(true);
                XWPFTableRow row4 = table.getRow(3);
                row4.getCell(0).setText(String.format("نیازهای والد: %s", parentsNumbers));
                row4.getCell(1).setText("");
                row4.getCell(2).setText(String.format("نیازهای فرزند: %s", childrenNumbers));
                row4.setCantSplitRow(true);
                XWPFTableRow row5 = table.getRow(4);
                row5.getCell(0).setText(String.format("پیوست: %s", requirement.getAttachment()));
                row5.setCantSplitRow(true);
                DocumentUtils.mergeHorizontally(table, 0, 1, 3);
                DocumentUtils.mergeHorizontally(table, 2, 2, 3);
                DocumentUtils.mergeHorizontally(table, 3, 0, 1);
                DocumentUtils.mergeHorizontally(table, 3, 2, 3);
                DocumentUtils.mergeHorizontally(table, 4, 0, 3);
                DocumentUtils.setTableR2L(table);

                paragraph = document.createParagraph();
                run = paragraph.createRun();
                run.addBreak();
            }
            paragraph = document.createParagraph();
            run = paragraph.createRun();
            run.addBreak();

        }
    }
}
