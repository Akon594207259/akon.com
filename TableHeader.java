package cn.yunrui.intfirectrlsys.action;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class TableHeader   extends PdfPageEventHelper {
	    String header;
	    PdfTemplate total;
	    
	    public void setHeader(String header){
	     this.header=header;
	    }
	    
	    public void onOpenDocument(PdfWriter writer,Document document){
	        total = writer.getDirectContent().createTemplate(30,16);
	    }
	    
	    public void onEndPage (PdfWriter writer, Document document) {
	     PdfPTable table = new PdfPTable(3);
	     try{
	     table.setWidths(new int[]{24,24,2});
	     table.setTotalWidth(527);
	     table.setLockedWidth(true);
	     table.getDefaultCell().setFixedHeight(20);
	     table.getDefaultCell().setBorder(Rectangle.BOTTOM);

	     table.addCell(header);
	     table.getDefaultCell().setHorizontalAlignment(
	     Element.ALIGN_RIGHT);
	     table.addCell(String.format("page %d of",writer.getPageNumber()));
	     PdfPCell cell = new PdfPCell(Image.getInstance(total));
	     cell.setBorder(Rectangle.BOTTOM);
	     table.addCell(cell);
	     table.writeSelectedRows(0,-1,34,803,writer.getDirectContent());

	     }
	     catch(DocumentException de){
	     throw new ExceptionConverter(de);
	     }
	    }
	    
	    public void onCloseDocument(PdfWriter writer,Document document){
	        ColumnText.showTextAligned(total,Element.ALIGN_LEFT,new Phrase(String.valueOf(writer.getPageNumber()-1)),2,2,0);
	     
	    }
	}
