package cn.yunrui.intfirectrlsys.action;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;
import cn.yunrui.intfirectrlsys.domain.CodeEntity;
import cn.yunrui.intfirectrlsys.service.DutyLogService1;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

@Controller
@RequestMapping("/dutyLogControl")
public class DutyLogControl1 {
	private static boolean iflocal = false;
	
	@RequestMapping(value = "/init.htm", method = RequestMethod.GET)
	public String wg(HttpServletRequest request, HttpServletResponse response) {
		return "ifcs/dutylog1";
	}
	@Resource(name = "dutylogservice1")
	private DutyLogService1 dutylogservice1;
	
	
	
		
	//导出
	@RequestMapping(value = "/exportPDF.htm")
	@ResponseBody
	public String exportPDF(HttpServletRequest request,HttpServletResponse response) throws Exception {
			request.setCharacterEncoding("utf-8");
			String zbrq = request.getParameter("zbrq");
			
			String xfzCode = request.getParameter("xfzCode");
			
			String sqName =  request.getParameter("sqName");
			
			String ryName = request.getParameter("ryName");
			
			String editDFJLR =  request.getParameter("editDFJLR");
			
			String editDFZBJL = request.getParameter("editDFZBJL");
			
			String editDFZYSX =  request.getParameter("editDFZYSX");
		
			String editDFBCMC =request.getParameter("editDFBCMC");
			
		
			String pdf = null;
			 
			File pdfFile = null;
			InputStream inputStream = null;
			FileInputStream onputStream  = null;
			OutputStream outputStream = null;

			try {
				Authentication auth = RequestContextSecurity.getAuthentication();

				pdf = this.writeElecOptimizeReport(request,sqName ,xfzCode , zbrq,ryName,editDFBCMC,editDFJLR,editDFZBJL,editDFZYSX);
				System.out.println("我不知道你是谁不过也马上就知道了不就是一个字符串吗小样PDF"+pdf);
				
			
				
				if ( pdf != null) {
					pdfFile = new File(pdf);
				}
				
				if (pdfFile != null && pdfFile.exists()) {
					inputStream = new FileInputStream(pdfFile);
				}
				
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(getUploadPath(request)+"shuiyin.pdf")));
				 Calendar cal = Calendar.getInstance();  
			        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			        setWatermark(bos, pdf, format.format(cal.getTime()), 16);  
				
				//	this.setWatermark( pdf, waterMarkName, permission);
				
				String fileName = "值班记录.pdf";
				fileName = new String(fileName.getBytes("GBK"), "ISO8859-1");
				

				response.setContentType("application/x-msdownload");
				response.addHeader("Content-Disposition", "attachment;filename="
						+ fileName);
				
				inputStream = new FileInputStream(getUploadPath(request)+"shuiyin.pdf");
				outputStream = response.getOutputStream();
				byte[] buf = new byte[4096];
				int len = 0;
				if (inputStream != null) {
					while ((len = inputStream.read(buf)) != -1) {
						outputStream.write(buf, 0, len);
					}
					outputStream.flush();
					this.closeInputStream(inputStream);
				}
				inputStream = null;
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				this.closeOutputStream(outputStream);
				this.closeInputStream(onputStream);
				this.deleteFile(pdf);
			}
			return pdf;
	
		}	
	
	
	

		//导出交接班记录详情===========
		@RequestMapping(value = "/exportPDFJJB.htm")
		@ResponseBody
		public String exportPDFJJB(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
							response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
			
				request.setCharacterEncoding("utf-8");
				String zbrq = request.getParameter("zbrq");
				String xfzCode = request.getParameter("xfzCode");
				String sqName =  request.getParameter("sqName");
				//String ryName = request.getParameter("ryName");
				String editDFJLR =request.getParameter("editDFJLR");
				String editDFZBJL = request.getParameter("editDFJJBJL");
				String editDFZYSX = request.getParameter("editDFZYSX");
				String editDFJBSJ1 =request.getParameter("jbsj1");
				String editDFJBSJ2 =request.getParameter("jbsj2");
				String editDFBCMC1 =request.getParameter("editDFBCMC1");
				String editDFBCMC2 =request.getParameter("editDFBCMC2");
				String JBRMZ1 =request.getParameter("JBRMZ1");
				String JBRMZ2 =request.getParameter("JBRMZ2");
				String pdf = null;
				File pdfFile = null;
				InputStream inputStream = null;
				FileInputStream onputStream  = null;
				OutputStream outputStream = null;

				try {
					
					
					
					Authentication auth = RequestContextSecurity.getAuthentication();

					pdf = this.writeElecOptimizeReport2(request,sqName ,xfzCode , zbrq,editDFBCMC1,editDFBCMC2,editDFJLR,editDFZBJL,editDFZYSX,editDFJBSJ1,editDFJBSJ2,JBRMZ1,JBRMZ2);
					System.out.println("我不知道你是谁不过也马上就知道了不就是一个字符串吗小样PDF"+pdf);
					
				
					
					if ( pdf != null) {
						pdfFile = new File(pdf);
					}
					
					if (pdfFile != null && pdfFile.exists()) {
						inputStream = new FileInputStream(pdfFile);
					}
					
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(getUploadPath(request)+"shuiyin.pdf")));
					 Calendar cal = Calendar.getInstance();  
				        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
				        setWatermark(bos, pdf, format.format(cal.getTime()), 16);  
					
					//	this.setWatermark( pdf, waterMarkName, permission);
					
					String fileName = "交接班记录.pdf";
					fileName = new String(fileName.getBytes("GBK"), "ISO8859-1");
					

					response.setContentType("application/x-msdownload");
					response.addHeader("Content-Disposition", "attachment;filename="
							+ fileName);
					
					inputStream = new FileInputStream(getUploadPath(request)+"shuiyin.pdf");
					outputStream = response.getOutputStream();
					byte[] buf = new byte[4096];
					int len = 0;
					if (inputStream != null) {
						while ((len = inputStream.read(buf)) != -1) {
							outputStream.write(buf, 0, len);
						}
						outputStream.flush();
						this.closeInputStream(inputStream);
					}
					inputStream = null;
				
				
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					this.closeOutputStream(outputStream);
					this.closeInputStream(onputStream);
					this.deleteFile(pdf);
				}
				return pdf;
				
			
		}
				
		
		public static void setWatermark(  BufferedOutputStream bos , String input, String waterMarkName, int permission) throws DocumentException,  
	            IOException {  
	          
	        PdfReader reader = new PdfReader(input);  
	        
	        PdfStamper stamper = new PdfStamper(reader, bos);  
	      
	        int total = reader.getNumberOfPages() + 1;  
	        PdfContentByte content;  
	        
	        BaseFont base = BaseFont.createFont("STSongStd-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
	        PdfGState gs = new PdfGState();  
	        for (int i = 1; i < total; i++) {  
	            content = stamper.getOverContent(i);// 在内容上方加水印  
	            //content = stamper.getUnderContent(i);//在内容下方加水印  
	            //设置透明度
	            gs.setFillOpacity(0.1f);  
	            content.setGState(gs);  
	            content.beginText();  
	           
	            content.setFontAndSize(base, 50);  
	            content.setTextMatrix(70, 200);  
	            content.showTextAligned(Element.ALIGN_CENTER, "公司内部文件，请注意保密！", 300,350, 55);  
	            
	            
	            content.setFontAndSize(base,20);  
	            /*content.showTextAligned(Element.ALIGN_CENTER, "下载时间："  
	                    + waterMarkName + "", 300, 50, 0);*/  
	            content.endText();  
	  
	        }  
	        stamper.close();  
	    } 
		
		
		/**
		 * 删除指定的文件
		 * @param fileName 要删除的文件的完整路径
		 */
		protected void deleteFile(String fileName) {
			if (fileName == null || "".equals(fileName)) {
				return;
			}
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
		}
		
		public  String    writeElecOptimizeReport(HttpServletRequest request,String sqName ,String xfzCode , String zbrq, String ryName,String editDFBCMC,String editDFJLR,String editDFZBJL,String editDFZYSX){
			Document document = new Document(PageSize.A3, 20, 20, 20, 20);
			 String uploadPath = "";
			
		    try {
		    
		    	
				 uploadPath = getUploadPath(request);// 文件上传路径
		    	
		    	 PdfWriter writer = PdfWriter.getInstance(document,
		                    new FileOutputStream(uploadPath+"ZBJS.pdf"));
		            document.open();

		    
		            BaseFont baseFont = BaseFont.createFont("STSongStd-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
					   Font fontbt = new Font(baseFont);    
			            fontbt.setSize(20);
			            Font fontzw = new Font(baseFont); 
			            fontzw.setSize(13);
			         Paragraph t = new Paragraph("值班记录",fontbt);
			         t.setAlignment(1);
			         t.setAlignment(Element.ALIGN_CENTER);
			            document.add(t);
					   PdfPTable table1 = new PdfPTable(2);      
					   table1.getDefaultCell().setBorder(PdfPCell.NO_BORDER);      
					   table1.addCell(new Paragraph("                   "));
					   table1.addCell(new Paragraph("                   "));
					   table1.addCell(new Paragraph("                   "));
					   table1.addCell(new Paragraph("                   "));
					   table1.addCell(new Paragraph("所属社区: "+sqName , fontzw));     
					   table1.addCell(new Paragraph("消防站名称: "+xfzCode ,fontzw));
					   table1.addCell(new Paragraph("                   "));
					   table1.addCell(new Paragraph("                   "));
					   table1.addCell(new Paragraph("值班日期: "+zbrq,fontzw));     
					   table1.addCell(new Paragraph("值班人: "+ryName,fontzw));     
					   table1.addCell(new Paragraph("                   "));
					   table1.addCell(new Paragraph("                   "));
					   table1.addCell(new Paragraph("班次: "+editDFBCMC,fontzw));     
					   table1.addCell(new Paragraph("记录人: "+editDFJLR,fontzw));  
					   
					   PdfPTable table2 = new PdfPTable(1);      
					   table2.getDefaultCell().setBorder(PdfPCell.NO_BORDER); 
					   table2.addCell(new Paragraph("                   "));
					   table2.addCell(new Paragraph("值班记录: "+editDFZBJL,fontzw));
					   table2.addCell(new Paragraph("                   "));
					   
					   table2.addCell(new Paragraph("注意事项: "+editDFZYSX,fontzw));     
					
						document.add(table1);    
					   	document.add(table2);    
			
			         
					   	document.close();
			            writer.close();
		        
		        document.close();
		        writer.close();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    return uploadPath+"ZBJS.pdf";
			
		}
		
		////交接班 记录导出
		public  String    writeElecOptimizeReport2(HttpServletRequest request,String sqName ,String xfzCode , String zbrq, 
				String class_name1, String class_name2, String editDFJLR, String editDFZBJL, String editDFZYSX, String editDFJBSJ1, String editDFJBSJ2, 
				String JBRMZ1, String JBRMZ2
				
				){
			Document document = new Document(PageSize.A3, 20, 20, 20, 20);
			  String uploadPath = "";
			
		    try {
		    
		    	
				 uploadPath = getUploadPath(request);// 文件上传路径
		    	
		    	 PdfWriter writer = PdfWriter.getInstance(document,
		                    new FileOutputStream(uploadPath+"JJBJL.pdf"));
		            document.open();

		    
		            BaseFont baseFont = BaseFont.createFont("STSongStd-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);  
					   Font fontbt = new Font(baseFont);    
			            fontbt.setSize(20);
			            Font fontzw = new Font(baseFont); 
			            fontzw.setSize(13);
			         Paragraph t = new Paragraph("交接班记录",fontbt);
			         t.setAlignment(1);
			         t.setAlignment(Element.ALIGN_CENTER);
			            document.add(t);
					   PdfPTable table1 = new PdfPTable(3);      
					   table1.getDefaultCell().setBorder(PdfPCell.NO_BORDER);  
					   
					   table1.addCell(new Paragraph("                   "));
					   table1.addCell(new Paragraph("                   "));
					   table1.addCell(new Paragraph("                   ")); 
					   
					   table1.addCell(new Paragraph("                   "));
					   table1.addCell(new Paragraph("                   "));
					   table1.addCell(new Paragraph("                   "));
					   
					   table1.addCell(new Paragraph("所属社区: "+sqName , fontzw));     
					   table1.addCell(new Paragraph("消防站名称: "+xfzCode ,fontzw));
					   
					   table1.addCell(new Paragraph("                   "));
					   table1.addCell(new Paragraph("                   "));
					   table1.addCell(new Paragraph("                   "));
					   
					   PdfPTable table2 = new PdfPTable(3);
					   table2.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
					   
					   table2.addCell(new Paragraph("                   "));
					   table2.addCell(new Paragraph("                   "));
					   table2.addCell(new Paragraph("                   "));
					   
					   table2.addCell(new Paragraph("交班人: "+JBRMZ1,fontzw)); 
					   table2.addCell(new Paragraph("交班时间: "+editDFJBSJ1,fontzw)); 
					   table2.addCell(new Paragraph("班次: "+class_name1,fontzw));
					   
					   table2.addCell(new Paragraph("                   "));
					   table2.addCell(new Paragraph("                   "));
					   table2.addCell(new Paragraph("                   "));
					   
					   PdfPTable table4 = new PdfPTable(3);
					   table4.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
					   table4.addCell(new Paragraph("接班人: "+JBRMZ2,fontzw)); 
					   table4.addCell(new Paragraph("接班时间: "+editDFJBSJ2,fontzw)); 
					   table4.addCell(new Paragraph("班次: "+class_name2,fontzw));
					   
					   PdfPTable table3 = new PdfPTable(1);  
					   table3.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
					   table3.addCell(new Paragraph("                   "));
					   table3.addCell(new Paragraph("记录人: "+editDFJLR,fontzw));
					   table3.addCell(new Paragraph("                   "));
					   table3.addCell(new Paragraph("交接内容: "+editDFZBJL,fontzw));
					   table3.addCell(new Paragraph("                   "));
					   table3.addCell(new Paragraph("注意事项: "+editDFZYSX,fontzw));     
					
						document.add(table1);    
					   	document.add(table2);
					   	document.add(table4);
					   	document.add(table3);
					   	
			
			         
					   	document.close();
			            writer.close();
		        
		        document.close();
		        writer.close();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    return uploadPath+"JJBJL.pdf";
			
		}
		
		//下载pdf
		private static String getUploadPath(HttpServletRequest request) {
			String basePath = request.getSession().getServletContext().getRealPath("/");
			System.out.println("我很想看看这个basePath到底是什么"+basePath);
			String uploadPath = basePath + "upload" + File.separator;
			System.out.println("让我看看这个路劲到底啥意思怎么玩啊哈哈哈uploadPath:"+uploadPath);
			File dir = new File(uploadPath);
			System.out.println("这个就是文件的目录吧Java好神奇啊 dir:"+dir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			return uploadPath;
		}
		
		
		
	
		   private void closeOutputStream(OutputStream outputStream) {
				if (outputStream != null) {
					try {
					
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
					
				}		
			}
			
			private void closeInputStream(InputStream inputStream) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	
	
	
	// 查询所有的消防用户
	@RequestMapping(value = "/getSheQu.htm", method = RequestMethod.POST)
	public void getSheQu(HttpServletRequest request,
			HttpServletResponse response) {
		String FL = request.getParameter("FL");
		List<Map<String, Object>> hs = new ArrayList<Map<String, Object>>();
		hs = dutylogservice1.getSheQu(FL);
		String rst = JSONArray.fromObject(hs).toString();
		try {
			response.setContentType("application/json; charset=utf-8");

			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin","http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}

			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过 消防用户获取到该用户下的消防室信息
	 */
	@RequestMapping(value = "/getXFZ.htm")
	public void getControlRoomAtion(HttpServletRequest request,
			HttpServletResponse response) {

		String id = request.getParameter("id");
		List<Map<String, Object>> hs = new ArrayList<Map<String, Object>>();
		hs=  dutylogservice1.getXFZ(id);

		String rst = JSONArray.fromObject(hs).toString();
		System.out.println("传说中的消控室" + rst);
		try {
			response.setContentType("application/json; charset=utf-8");

			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}

			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	////////////////////////////////////////////////////////////////////
	//插入值班记录
	@RequestMapping(value ="/insertZBJL.htm")
	public void insertZBJL(HttpServletRequest request , HttpServletResponse response ) throws UnsupportedEncodingException, ParseException{
		request.setCharacterEncoding("utf-8");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String id = UUID.randomUUID().toString();
		String UUID = id.substring(0, 8)+id.substring(9, 13)+id.substring(14, 18)+id.substring(24);
		String sqId	= request.getParameter("sqid");
		String bcId = request.getParameter("bcid");
		String bcName = request.getParameter("bcmc");
		String xfzName = request.getParameter("xfzName");
		String xfzId = request.getParameter("xfzId");
		String jlr = request.getParameter("jlr");
		String zbjl = request.getParameter("zbjl");
		String zysx =request.getParameter("zysx");
		String str  = request.getParameter("zbrID");
		String format = request.getParameter("zbrq");
		
		System.out.println("df====="+df);
		System.out.println("dfs======"+dfs);
		
		String zbrq = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(df.parse(format));
		
        String bcsj =  dfs.format(new Date());
        
		String[] ryid= str.split(",");
	
		int i = dutylogservice1.insertZBJL( UUID , sqId ,  bcId ,bcName ,  xfzName ,xfzId ,  jlr ,zbjl , zysx , zbrq , bcsj, ryid);
		
		response.setContentType("application/json; charset=utf-8");
		
		if (iflocal) {
			response.setHeader("Access-Control-Allow-Origin","http://localhost:3000");
			response.setHeader("Access-Control-Allow-Credentials", "true");
		}

		try {
			response.getWriter().write(JSONArray.fromObject(i).toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		

	}
	//修改值班记录
	@RequestMapping(value = "/updateZBJL.htm")
	public void updateZBJL(HttpServletRequest request , HttpServletResponse response) throws IOException{
		request.setCharacterEncoding("utf-8");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		
		String id = request.getParameter("id");
		String sqId	= request.getParameter("sqid");
		String bcId = request.getParameter("bcid");
		String bcName = request.getParameter("bcmc");
		String xfzName =request.getParameter("xfzName");
		String xfzId = request.getParameter("xfzId");
		String jlr = request.getParameter("jlr");
		String zbjl = request.getParameter("zbjl");
		String zysx = request.getParameter("zysx");
		String str = request.getParameter("zbrID");
		
		String[] zbrId = str.split(",");
		String format =  request.getParameter("zbrq");
		try {
		
		String	zbrq = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(df.parse(format));
		
        String bcsj =  dfs.format(new Date());
  
		int i = dutylogservice1.updateZBJL( id , sqId ,  bcId ,bcName ,  xfzName ,xfzId ,  jlr ,zbjl , zysx , zbrq , bcsj , zbrId );
		
			response.setContentType("application/json; charset=utf-8");

			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin","http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}

			response.getWriter().write(JSONArray.fromObject(i).toString());
			response.getWriter().flush();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	
	
	}
	
	
	
	//修改交接班记录
		@RequestMapping(value = "/updateJJBJL.htm")
		public void updateJJBJL(HttpServletRequest request , HttpServletResponse response) throws IOException, ParseException{
			
			request.setCharacterEncoding("utf-8");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
			
			//交接班记录ID 
			String id = request.getParameter("id");
			//社区ID
			String sqId	= request.getParameter("sqid");
			//交班班次ID
			String bcId1 = request.getParameter("bcid1");
			//接班班次id 
			String bcId2 = request.getParameter("bcid2");
			//交班班次名称
			//String bcName1 = request.getParameter("bcmc1");
			//接班班次名称 
			//String bcName2 = request.getParameter("bcmc2");
			
			//消防站名称
			String xfzName = request.getParameter("xfzName");
			//消防站ID
			String xfzId = request.getParameter("xfzId");
			//记录人
			String jlr = request.getParameter("jlr");
			//值班记录内容(交接内容)
			
			String zbjl = request.getParameter("zbjl");
			//注意事项
			String zysx =request.getParameter("zysx");
			//值班日期
			String format = request.getParameter("zbrq");
			//交班人员ID 装字符串
			String st1  = request.getParameter("ryid1");
			//接班人员ID 装字符串
			String st2 = request.getParameter("ryid2");
			
			//交班时间
			String jbsj1= request.getParameter("jbsj1");
			//接班时间
			String jbsj2= request.getParameter("jbsj2");
			
			//人员名 装字符串
			//交班人员名
			//String  JBRMZ1=request.getParameter("JBRMZ1");
			//接班人员名
			//String  JBRMZ2=request.getParameter("JBRMZ2");
			//值班日期格式化一下
			String	zbrq = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(df.parse(format));
			
			String[] ryid1= st1.split(",");
			String[] ryid2= st2.split(",");
			
	
			//String[] rymc1= JBRMZ1.split(",");
			//String[] rymc2= JBRMZ2.split(",");
			
			
			
			int i = dutylogservice1.updateJJBJL(id, sqId, xfzName, xfzId, jlr, zbjl, zysx,  jbsj1, bcId1,  jbsj2, bcId2, zbrq,  ryid1, ryid2);
			response.setContentType("application/json; charset=utf-8");
			
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin","http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}

			try {
				response.getWriter().write(JSONArray.fromObject(i).toString());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		
		}
	
	
	/**
	 * 删除值班记录
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value="/deleteZBJL.htm")
	
	public void deleteZBJL(HttpServletRequest request , HttpServletResponse response) throws UnsupportedEncodingException{
		request.setCharacterEncoding("utf-8");
		String id = request.getParameter("id");
		int i = dutylogservice1.deleteZBJL(id);
		response.setContentType("application/json; charset=utf-8");

		if (iflocal) {
		response.setHeader("Access-Control-Allow-Origin","http://localhost:3000");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		}

		try {
			response.getWriter().write(JSONArray.fromObject(i).toString());
		} catch (IOException e) {
		e.printStackTrace();
		}
		try {
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * 查询值班记录
	 * @param request
	 * @param response
	 */
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/queryZBJL.htm")
	public void queryZBJL(HttpServletRequest request , HttpServletResponse response){
		
		String sqId = request.getParameter("sqid");
		String xfzId = request.getParameter("xfzid");
		List<Map<String,Object>> list = new ArrayList<Map<String , Object>>();
		list = dutylogservice1.queryZBJL(sqId,xfzId);
		String rst = JSONArray.fromObject(list).toString();
		try {
			response.setContentType("application/json; charset=utf-8");

			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}

			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询人员
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value = "/query_people.htm")
	public void query_people(HttpServletRequest request,
			HttpServletResponse response) {
		String xfzID = request.getParameter("xfzID");

		List<Map<String, Object>> ry = new ArrayList<Map<String, Object>>();
		ry = dutylogservice1.queryRY(xfzID);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(ry).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询班次
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value = "/query_class.htm")
	public void query_class(HttpServletRequest request,
			HttpServletResponse response) {
		String xfzID = request.getParameter("xfzID");

		List<Map<String, Object>> ry = new ArrayList<Map<String, Object>>();
		ry = dutylogservice1.queryClassId(xfzID);
		System.out.println("前台打印查询出的班次" + ry);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin","http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(ry).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	//条件查询值班记录
	
	@RequestMapping(value="/tjqueryZBJL.htm")
	public void tjqueryZBJL(HttpServletRequest request , HttpServletResponse response) throws UnsupportedEncodingException{
		request.setCharacterEncoding("utf-8");
		String sqId = request.getParameter("sqid");
		String xfzId = request.getParameter("xfzid");
		String jieshu = request.getParameter("jieshu");
		String sj = request.getParameter("sj");
		String bc = request.getParameter("bc");
		String ry = request.getParameter("ry");
		List<Map<String,Object>> list = new ArrayList<Map<String , Object>>();
		list = dutylogservice1.tjqueryZBJL(sqId,xfzId,sj,bc,ry,jieshu);
		String rst = JSONArray.fromObject(list).toString();
		try {
			response.setContentType("application/json; charset=utf-8");

			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}

			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//条件查询交接班记录
	@RequestMapping(value="/tjqueryJJBJL.htm")
	public void tjqueryJJBJL(HttpServletRequest request , HttpServletResponse response) throws UnsupportedEncodingException{
		request.setCharacterEncoding("utf-8");
		String sqId = request.getParameter("sqid");
		String xfzId = request.getParameter("xfzid");
		String jieshu = request.getParameter("jieshu");
		String sj = request.getParameter("sj");
		String bc = request.getParameter("bc");
		String ry = request.getParameter("ry");
		List<Map<String,Object>> list = new ArrayList<Map<String , Object>>();
		list = dutylogservice1.tjqueryJJBJL(sqId,xfzId,sj,bc,ry,jieshu);
		String rst = JSONArray.fromObject(list).toString();
		try {
			response.setContentType("application/json; charset=utf-8");

			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}

			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	////////////////////////////////////////////////////////////////////
	

	/**
	 * 获取社区对象
	 */
	public void getDX(HttpServletRequest request,HttpServletResponse response) {
		Authentication auth = RequestContextSecurity.getAuthentication();

		ArrayList<CodeEntity> DXlist = dutylogservice1.getDX(
				auth.getBureauNo(), auth.getOrgNo());
		String rst = JSONArray.fromObject(DXlist).toString();
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) { 
				response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 查询交接班记录
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/queryJJBJL.htm")
	public void queryJJBJL(HttpServletRequest request , HttpServletResponse response){
		
		String sqId = request.getParameter("sqid");
		String xfzId = request.getParameter("xfzid");
		List<Map<String,Object>> list = new ArrayList<Map<String , Object>>();
		list = dutylogservice1.queryJJBJL(sqId,xfzId);
		String rst = JSONArray.fromObject(list).toString();
		try {
			response.setContentType("application/json; charset=utf-8");

			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}

			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 新增插入交接班记录
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws ParseException
	 */
	@RequestMapping(value ="/insertJJBJL.htm")
	public void insertJJBJL(HttpServletRequest request , HttpServletResponse response ) throws UnsupportedEncodingException, ParseException{
		request.setCharacterEncoding("utf-8");
		//设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String id = UUID.randomUUID().toString();
		//交接班记录主键ID
		String UUID = id.substring(0, 8)+id.substring(9, 13)+id.substring(14, 18)+id.substring(24);
		//社区ID
		String sqId	= request.getParameter("sqid");
		//交班班次ID
		String bcId1 = request.getParameter("bcid1");
		//接班班次id 
		String bcId2 = request.getParameter("bcid2");
		//交班班次名称
		String bcName1 = request.getParameter("bcmc1");
		//接班班次名称
		String bcName2 = request.getParameter("bcmc2");
		
		//消防站名称
		String xfzName = request.getParameter("xfzName");
		//消防站ID
		String xfzId = request.getParameter("xfzId");
		//记录人
		String jlr = request.getParameter("jlr");
		//值班记录内容(交接内容)
		
		String zbjl = request.getParameter("zbjl");
		//注意事项
		String zysx =request.getParameter("zysx");
		//值班日期
		String format = request.getParameter("zbrq");
		//交班人员ID 装字符串
		String st1  = request.getParameter("ryid1");
		//接班人员ID 装字符串
		String st2 = request.getParameter("ryid2");
		
		//交班时间
		String jbsj1= request.getParameter("jbsj1");
		//接班时间
		String jbsj2= request.getParameter("jbsj2");
		
		//人员名 装字符串
		//交班人员名
		String  JBRMZ1=request.getParameter("JBRMZ1");
		//接班人员名
		String  JBRMZ2=request.getParameter("JBRMZ2");
		//值班日期格式化一下
		String	zbrq = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(df.parse(format));
		
		String[] ryid1= st1.split(",");
		String[] ryid2= st2.split(",");
		
	
		//String[] rymc1= JBRMZ1.split(",");
		//String[] rymc2= JBRMZ2.split(",");
		
		int i = dutylogservice1.insertJJBJL(UUID, sqId, xfzName, xfzId, jlr, zbjl, zysx,  jbsj1, bcId1, jbsj2, bcId2, zbrq,  ryid1, ryid2);
		response.setContentType("application/json; charset=utf-8");
		
		if (iflocal) {
			response.setHeader("Access-Control-Allow-Origin","http://localhost:3000");
			response.setHeader("Access-Control-Allow-Credentials", "true");
		}

		try {
			response.getWriter().write(JSONArray.fromObject(i).toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	/**
	 * 删除交接班记录
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	
	@RequestMapping(value ="/deleteJJBJL.htm")
	public void deleteJJBJL(HttpServletRequest request , HttpServletResponse response) throws UnsupportedEncodingException{
		request.setCharacterEncoding("utf-8");
		String id = request.getParameter("id");
		int i = dutylogservice1.deleteJJBJL(id);
		response.setContentType("application/json; charset=utf-8");

		if (iflocal) {
		response.setHeader("Access-Control-Allow-Origin","http://localhost:3000");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		}

		try {
			response.getWriter().write(JSONArray.fromObject(i).toString());
		} catch (IOException e) {
		e.printStackTrace();
		}
		try {
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}
