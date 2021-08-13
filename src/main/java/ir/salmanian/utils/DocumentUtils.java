package ir.salmanian.utils;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.IOException;

public class DocumentUtils {
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
                    CTP ctp = paragraph.getCTP();
                    CTPPr ctppr = ctp.getPPr();
                    if (ctppr == null) ctppr = ctp.addNewPPr();
                    ctppr.addNewBidi().setVal(STOnOff1.ON);
                }
            }
        }
    }

    public static void setParagraphRTL(XWPFParagraph paragraph) {
        CTP ctp = paragraph.getCTP();
        CTPPr ctppr = ctp.getPPr();
        if (ctppr == null) ctppr = ctp.addNewPPr();
        ctppr.addNewBidi().setVal(STOnOff1.ON);
    }

    public static void setParagraphNotSplit(XWPFParagraph paragraph) {
        paragraph.getCTP().addNewPPr().addNewKeepLines().setVal(STOnOff1.ON);
        paragraph.getCTP().getPPr().addNewKeepNext().setVal(STOnOff1.ON);
    }

    public static void setDocumentFont(XWPFDocument document, String fontName) throws IOException, XmlException {
        XWPFStyles styles = document.createStyles();
        CTFonts fonts = CTFonts.Factory.newInstance();
        fonts.setEastAsia(fontName);
        fonts.setHAnsi(fontName);
        styles.setDefaultFonts(fonts);
    }
}
