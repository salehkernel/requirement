package ir.salmanian.utils;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.IOException;

/**
 * DocumentUtils is a utility class for handling some actions on a word document or its components
 * like paragraphs, tables and etc
 */
public class DocumentUtils {
    /**
     * This method is used to merge some cells of row in a table
     * @param table the intended table
     * @param row the intended row
     * @param fromCell the starting cell for merging.
     * @param toCell the end cell for merging
     */
    public static void mergeHorizontally(XWPFTable table, int row, int fromCell, int toCell) {
        for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {
            XWPFTableCell cell = table.getRow(row).getCell(cellIndex);
            if (cellIndex == fromCell) {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            } else {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    /**
     * This method is used to set the direction of a table rtl
     * @param table the intended table
     */
    public static void setTableR2L(XWPFTable table) {
        if (table.getCTTbl().getTblPr() == null) {
            table.getCTTbl().addNewTblPr().addNewBidiVisual().setVal(STOnOff1.ON);
        } else {
            table.getCTTbl().getTblPr().addNewBidiVisual().setVal(STOnOff1.ON);
        }
        XWPFParagraph paragraph;
        for (int i = 0; i < table.getNumberOfRows(); i++) {
            for (int j = 0; j < table.getRow(i).getTableCells().size(); j++) {
                if (table.getRow(i).getCell(j).getParagraphs().size() > 0) {
                    paragraph = table.getRow(i).getCell(j).getParagraphs().get(0);
                    setParagraphRTL(paragraph);
                }
            }
        }
    }

    /**
     * This method is used to set the direction of a paragraph rtl.
     * @param paragraph the intended paragraph
     */
    public static void setParagraphRTL(XWPFParagraph paragraph) {
        CTP ctp = paragraph.getCTP();
        CTPPr ctppr = ctp.getPPr();
        if (ctppr == null) ctppr = ctp.addNewPPr();
        ctppr.addNewBidi().setVal(STOnOff1.ON);
    }

    /**
     * This method is used to set a paragraph to not split
     * @param paragraph the intended paragraph
     */
    public static void setParagraphNotSplit(XWPFParagraph paragraph) {
        paragraph.getCTP().addNewPPr().addNewKeepLines().setVal(STOnOff1.ON);
        paragraph.getCTP().getPPr().addNewKeepNext().setVal(STOnOff1.ON);
    }

    /**
     * This method is used to set the font of whole document.
     * @param document the intended document
     * @param fontName the name of font family
     */
    public static void setDocumentFont(XWPFDocument document, String fontName) throws IOException, XmlException {
        XWPFStyles styles = document.createStyles();
        CTFonts fonts = CTFonts.Factory.newInstance();
        fonts.setEastAsia(fontName);
        fonts.setHAnsi(fontName);
        styles.setDefaultFonts(fonts);
    }

    /**
     * This method is used to set the font of a run.
     * @param run the intended run.
     * @param fontFamily the name of font family.
     * @param fontSize the size of font
     * @param bold if the run should be bold or not
     * @param italic if the run should be italic or not
     */
    public static void setRunFont(XWPFRun run, String fontFamily, int fontSize, boolean bold, boolean italic) {
        run.setFontFamily(fontFamily);
        run.setFontSize(fontSize);
        run.setBold(bold);
        run.setItalic(italic);
    }

    /**
     * This method is used to set the font for a table.
     * @param table the intended table.
     * @param fontFamily the name of font family.
     * @param fontSize the size of font
     * @param bold if the run should be bold or not
     * @param italic if the run should be italic or not
     */
    public static void setTableFont(XWPFTable table, String fontFamily, int fontSize, boolean bold, boolean italic) {
        for (int i = 0; i < table.getNumberOfRows(); i++) {
            for (int j = 0; j < table.getRow(i).getTableCells().size(); j++) {
                for (XWPFParagraph paragraph : table.getRow(i).getCell(j).getParagraphs()) {
                    paragraph.getRuns().forEach(run -> setRunFont(run, fontFamily, fontSize, bold, italic));
                }
            }
        }
    }
}
