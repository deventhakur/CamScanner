package com.igr.camscanner.com.igr.camscanner.pdfcreator;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Created by DEVEN SINGH on 1/14/2015.
 */

public class MyHeaderAndFooter extends PdfPageEventHelper{

    Font ffont = new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.ITALIC);

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
//        Phrase header = new Phrase("Cam Scanner"+writer.getPageNumber(), ffont);
        Phrase footer = new Phrase("Created by Cam Scanner(M Technovation).", ffont);
//        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
//                header,
//                (document.right() - document.left()) / 2 + document.leftMargin(),
//                document.top() + 10, 0);
//        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
//                footer,
//                (document.right() - document.left()) / 2 + document.leftMargin(),
//                document.bottom() - 10, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                footer,
                document.left() + document.leftMargin()+10,
                document.bottom() - 15, 0);
    }
}
