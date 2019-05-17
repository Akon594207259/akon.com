package cn.yunrui.intfirectrlsys.action;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class HeaderFooter  extends PdfPageEventHelper {
	 public void onEndPage (PdfWriter writer, Document document) {
	        Rectangle rect = writer.getBoxSize("art");
	        switch(writer.getPageNumber() % 2) {
	        case 0:
	            ColumnText.showTextAligned(writer.getDirectContent(),
	                    Element.ALIGN_RIGHT, new Phrase(""),
	                    rect.getRight(), rect.getTop()-35, 0);
	            break;
	        case 1:
	            ColumnText.showTextAligned(writer.getDirectContent(),
	                    Element.ALIGN_LEFT, new Phrase(""),
	                    rect.getLeft(), rect.getTop()-35, 0);
	            break;
	        }
	        ColumnText.showTextAligned(writer.getDirectContent(),
	                Element.ALIGN_CENTER, new Phrase(String.format(" %d", writer.getPageNumber())),
	                (rect.getLeft() + rect.getRight()) / 2, rect.getBottom() - 35, 0);
	    }
	}


