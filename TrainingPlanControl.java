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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import cn.yunrui.common.util.YunRuiCommonUtil;
import cn.yunrui.intfirectrlsys.common.CommonMethods;
import cn.yunrui.intfirectrlsys.domain.CodeEntity;
import cn.yunrui.intfirectrlsys.domain.ResultMessage1;
import cn.yunrui.intfirectrlsys.entity.AttachmentBean;
import cn.yunrui.intfirectrlsys.service.TrainingPlanService;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * 演习计划模块
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/TrainingPlanControl")
public class TrainingPlanControl {
	private static boolean iflocal = true;
	
	@Resource(name="TrainingPlanService")
	private TrainingPlanService service; 

	
	//导出
			@RequestMapping(value = "/exportPDF.htm")
			@ResponseBody
			public String exportPDF(HttpServletRequest request,HttpServletResponse response) throws Exception {
					request.setCharacterEncoding("utf-8");
					String xfyhId = request.getParameter("xfyhId");
					String time = request.getParameter("date");
					
					List<Map<String, Object>> hs = new ArrayList<Map<String, Object>>();
					hs = service.queryPxjh(xfyhId, time);
					/*Map<String,Object> map = new HashMap<String,Object>(); 
					map =  hs.get(i);
		        	   System.out.println("这是map"+map);
		        	   String name = (String) map.get("name");
		        	   System.out.println("这是xh"+name);*/
					String pdf = null;
					 
					File pdfFile = null;
					InputStream inputStream = null;
					FileInputStream onputStream  = null;
					OutputStream outputStream = null;

					try {
						Authentication auth = RequestContextSecurity.getAuthentication();

						pdf = this.writeElecOptimizeReport(request,hs);
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
						
						String fileName = "培训计划.pdf";
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
			
			
//设置水印
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
	
	public  String    writeElecOptimizeReport(HttpServletRequest request, List<Map<String, Object>> hs){
		Document document = new Document(PageSize.A3.rotate(), 10, 10, 10, 10);
		 String uploadPath = "";
		
	    try {
	    
	    	
			 uploadPath = getUploadPath(request);// 文件上传路径
	    	
	    	 PdfWriter writer = PdfWriter.getInstance(document,
	                    new FileOutputStream(uploadPath+"pxjh.pdf"));
	            document.open();

	            BaseFont baseFont = BaseFont.createFont("STSongStd-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
				   Font fontbt = new Font(baseFont);    
		            fontbt.setSize(30);
		            Font fontzw = new Font(baseFont); 
		            fontzw.setSize(15);
		         Paragraph t = new Paragraph("培训计划",fontbt);
		         t.setAlignment(1);
		         t.setAlignment(Element.ALIGN_CENTER);
		         document.add(t);
		        
		         PdfPTable tables = new PdfPTable(1);      
				   tables.getDefaultCell().setBorder(PdfPCell.NO_BORDER);      
				   tables.addCell(new Paragraph("                   "));
				   tables.addCell(new Paragraph("                   "));
				   tables.addCell(new Paragraph("                   "));
				   document.add(tables); 
				   float[] widths = {02.f,06.f,03.f,03.f,07.f,08.f,03.f,03.f};
				   PdfPTable table1 = new PdfPTable(widths);      
				   
				   PdfPCell cell = new PdfPCell();  
				   cell.setBorder(PdfPCell.NO_BORDER);
				   cell = new PdfPCell(new Phrase("序号", fontzw)); 
				   cell.setBorder(PdfPCell.NO_BORDER);
				   cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				   cell.setPaddingTop(-2f);//把字垂直居中
		           cell.setPaddingBottom(8f);//把字垂直居中
		           cell.setColspan(1);//列 
		           table1.addCell(cell);  
		           cell = new PdfPCell(new Phrase("消防用户", fontzw));  
		           cell.setBorder(PdfPCell.NO_BORDER);
		           cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		           cell.setPaddingTop(-2f);//把字垂直居中
		           cell.setPaddingBottom(8f);//把字垂直居中
		           cell.setColspan(1);//列 
		           table1.addCell(cell);    
		           cell = new PdfPCell(new Phrase("开始时间", fontzw));  
		           cell.setBorder(PdfPCell.NO_BORDER);
		           cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		           cell.setPaddingTop(-2f);//把字垂直居中
		           cell.setPaddingBottom(8f);//把字垂直居中
		           cell.setColspan(1);//列 
		           table1.addCell(cell);    
		           cell = new PdfPCell(new Phrase("结束时间", fontzw)); 
		           cell.setBorder(PdfPCell.NO_BORDER);
		           cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		           cell.setPaddingTop(-2f);//把字垂直居中
		           cell.setPaddingBottom(8f);//把字垂直居中
		           cell.setColspan(1);//列 
		           table1.addCell(cell);    
		           cell = new PdfPCell(new Phrase("培训内容", fontzw));  
		           cell.setBorder(PdfPCell.NO_BORDER);
		           cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		           cell.setPaddingTop(-2f);//把字垂直居中
		           cell.setPaddingBottom(8f);//把字垂直居中
		           cell.setColspan(1);//列 
		           table1.addCell(cell);    
		           cell = new PdfPCell(new Phrase("培训人员", fontzw)); 
		           cell.setBorder(PdfPCell.NO_BORDER);
		           cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		           cell.setPaddingTop(-2f);//把字垂直居中
		           cell.setPaddingBottom(8f);//把字垂直居中
		           cell.setColspan(1);//列 
		           table1.addCell(cell);    
		           cell = new PdfPCell(new Phrase("培训地点", fontzw));  
		           cell.setBorder(PdfPCell.NO_BORDER);
		           cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		           cell.setPaddingTop(-2f);//把字垂直居中
		           cell.setPaddingBottom(8f);//把字垂直居中
		           cell.setColspan(1);//列 
		           table1.addCell(cell);    
		           cell = new PdfPCell(new Phrase("培训方式", fontzw));  
		           cell.setBorder(PdfPCell.NO_BORDER);
		           cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		           cell.setPaddingTop(-2f);//把字垂直居中
		           cell.setPaddingBottom(8f);//把字垂直居中
		           cell.setColspan(1);//列 
		           table1.addCell(cell);  
		           
		           document.add(table1); 
		        
		           
	        	   for (int i = 0; i < hs.size(); i++) {
	        		   int xh =  (Integer) hs.get(i).get("xh");
		        	   String name = (String) hs.get(i).get("name");
		        	   String start = (String) hs.get(i).get("start");
		        	   String end = (String) hs.get(i).get("end");
		        	   String pxdd = (String) hs.get(i).get("pxdd");
		        	   String pxnr = (String) hs.get(i).get("pxnr");
		        	   String pxfs = (String) hs.get(i).get("pxfs");
		        	   String pxry = (String) hs.get(i).get("pxry");
		        	  float[] width = {02.f,06.f,03.f,03.f,07.f,08.f,03.f,03.f};
		           PdfPTable tablef = new PdfPTable(width); 
		         //  tablef.getDefaultCell(). 
				   PdfPCell cells = new PdfPCell(); 
				  
				   cells = new PdfPCell(new Phrase(xh+"" , fontzw));
				   cells.setBorder(PdfPCell.NO_BORDER);
				   cells.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		           cells.setPaddingTop(-2f);//把字垂直居中
		           cells.setPaddingBottom(8f);//把字垂直居中
		           cells.setColspan(1);//列 
		           tablef.addCell(cells);  
		           
		           
		           
		           cells = new PdfPCell(new Phrase(name, fontzw));  
		           cells.setBorder(PdfPCell.NO_BORDER);
		           cells.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		           cells.setPaddingTop(-2f);//把字垂直居中
		           cells.setPaddingBottom(8f);//把字垂直居中
		           cells.setColspan(1);//列 
		           tablef.addCell(cells);    
		           
		           
		           
		           cells = new PdfPCell(new Phrase(start, fontzw)); 
		           cells.setBorder(PdfPCell.NO_BORDER);
		           cells.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		           cells.setPaddingTop(-2f);//把字垂直居中
		           cells.setPaddingBottom(8f);//把字垂直居中
		           cells.setColspan(1);//列 
		           tablef.addCell(cells);    
		           
		           
		           cells = new PdfPCell(new Phrase(end, fontzw)); 
		           cells.setBorder(PdfPCell.NO_BORDER);
		           cells.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		           cells.setPaddingTop(-2f);//把字垂直居中
		           cells.setPaddingBottom(8f);//把字垂直居中
		           cells.setColspan(1);//列 
		           tablef.addCell(cells);   
		           
		           cells = new PdfPCell(new Phrase(pxnr, fontzw));  
		           cells.setBorder(PdfPCell.NO_BORDER);
		           cells.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		           cells.setPaddingTop(-2f);//把字垂直居中
		           cells.setPaddingBottom(8f);//把字垂直居中
		           cells.setColspan(1);//列 
		           tablef.addCell(cells);   
		           
		           cells = new PdfPCell(new Phrase(pxry, fontzw)); 
		           cells.setBorder(PdfPCell.NO_BORDER);
		           cells.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		           cells.setPaddingTop(-2f);//把字垂直居中
		           cells.setPaddingBottom(8f);//把字垂直居中
		           cells.setColspan(1);//列 
		           tablef.addCell(cells); 
		           
		           cells = new PdfPCell(new Phrase(pxdd, fontzw)); 
		           cells.setBorder(PdfPCell.NO_BORDER);
		           cells.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		           cells.setPaddingTop(-2f);//把字垂直居中
		           cells.setPaddingBottom(8f);//把字垂直居中
		           cells.setColspan(1);//列 
		           tablef.addCell(cells);    
		           
		           cells = new PdfPCell(new Phrase(pxfs, fontzw));  
		           cells.setBorder(PdfPCell.NO_BORDER);
		           cells.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		           cells.setPaddingTop(-2f);//把字垂直居中
		           cells.setPaddingBottom(8f);//把字垂直居中
		           cells.setColspan(1);//列 
		           tablef.addCell(cells);    
		          
		           document.add(tablef);    
		           
		         }
			    	document.close();
		            writer.close();
	        
	        document.close();
	        writer.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return uploadPath+"pxjh.pdf";
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
	@RequestMapping(value = "/getXfyhListAtion.htm")
	public void getXfyhListAtion(HttpServletRequest request,
			HttpServletResponse response) {

		Authentication auth = RequestContextSecurity.getAuthentication();
		@SuppressWarnings("unchecked")
		ArrayList<CodeEntity> xfyhlist = (ArrayList<CodeEntity>) service
				.getXfyhListCommon(auth.getBureauNo(),auth.getOrgNo());
		String rst = JSONArray.fromObject(xfyhlist).toString();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
		@RequestMapping(value = "/insert.htm")
		public void insertPxjh(HttpServletRequest request,
				HttpServletResponse response) throws ParseException {
			try {
				request.setCharacterEncoding("utf-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-DD-MM");
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-DD-MM");
			String Id = CommonMethods.getUUID();
			String xfyhId = request.getParameter("xfyhId");
			String date = request.getParameter("date");
			String[] time = date.split(",");
			String s = time[0];
			Date da = sdf.parse(s);
			String start = sd.format(da);
			String e = time[1];
			Date d = sdf.parse(e);
			String end = sd.format(d);
			String place = request.getParameter("place");
			String content = request.getParameter("content");
			String mode = request.getParameter("mode");
			String Str = request.getParameter("pxr");
			String[] pxr = Str.split(",");
			
			
			int i  = service.insertPxjh(Id,xfyhId,start,end,place,content,mode,pxr);
			
			String rst = JSONArray.fromObject(i).toString();
			try {
				response.setContentType("application/json; charset=utf-8");

				if (iflocal) {
					response.setHeader("Access-Control-Allow-Origin",
							"http://localhost:3000");
					response.setHeader("Access-Control-Allow-Credentials", "true");
				}

				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
		
		@RequestMapping(value = "/edit.htm")
		public void editPxjh(HttpServletRequest request,
				HttpServletResponse response) throws ParseException {
			try {
				request.setCharacterEncoding("utf-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-DD-MM");
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-DD-MM");
			String Id = request.getParameter("Id");
			String xfyhId = request.getParameter("xfyhId");
			String date = request.getParameter("date");
			String[] time = date.split(",");
			String s = time[0];
			Date da = sdf.parse(s);
			String start = sd.format(da);
			String e = time[1];
			Date d = sdf.parse(e);
			String end = sd.format(d);
			String place = request.getParameter("place");
			String content = request.getParameter("content");
			String mode = request.getParameter("mode");
			String Str = request.getParameter("pxr");
			String[] pxr = Str.split(",");
			int i  = service.editPxjh(Id,xfyhId,start,end,place,content,mode,pxr);
			String rst = JSONArray.fromObject(i).toString();
			try {
				response.setContentType("application/json; charset=utf-8");

				if (iflocal) {
					response.setHeader("Access-Control-Allow-Origin",
							"http://localhost:3000");
					response.setHeader("Access-Control-Allow-Credentials", "true");
				}

				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
		
		
		@RequestMapping(value = "/delete.htm")
		public void deletePxjh(HttpServletRequest request,
				HttpServletResponse response) throws ParseException {
			;
			String Id = request.getParameter("Id");
			
			
			
			int i  = service.deletePxjh(Id);
			
			String rst = JSONArray.fromObject(i).toString();
			try {
				response.setContentType("application/json; charset=utf-8");

				if (iflocal) {
					response.setHeader("Access-Control-Allow-Origin",
							"http://localhost:3000");
					response.setHeader("Access-Control-Allow-Credentials", "true");
				}

				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
		
		//查询培训计划
		@ResponseBody
		@RequestMapping(value = "/queryPxjh.htm")
		public void queryPxjh(HttpServletRequest request,
				HttpServletResponse response) {
			String xfyhId = request.getParameter("xfyhId");
			String time = request.getParameter("date");
			
			List<Map<String, Object>> hs = new ArrayList<Map<String, Object>>();
			hs = service.queryPxjh(xfyhId, time);
			
			try {
				response.setContentType("application/json; charset=utf-8");
				if (iflocal) {
					response.setHeader("Access-Control-Allow-Origin",
							"http://localhost:3000");
					response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(JSONArray.fromObject(hs).toString());
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		 @RequestMapping(value={"/deleteAttchInfo.htm"}, method=RequestMethod.POST)
		  public void deleteAttchInfo(HttpServletRequest request, HttpServletResponse response)
		  {
		    String attachType = request.getParameter("attachType");
		    String fileName = request.getParameter("fileName");
		    ResultMessage1 rMessage = service.deleteAttchInfo(attachType, fileName);
		    try {
		      response.setContentType("application/json; charset=utf-8");
		      if (iflocal) {
		        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
		        response.setHeader("Access-Control-Allow-Credentials", "true");
		      }
		      response.getWriter().write(rMessage.toJson());
		      response.getWriter().flush();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		  }
		
		 @RequestMapping(value={"queryPictureInfo.htm"}, method=RequestMethod.POST)
		  public void queryPictureInfo(HttpServletRequest request, HttpServletResponse response)
		    throws Exception
		  {
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		    resultMap.put("total", Integer.valueOf(0));
		    resultMap.put("results", resultList);
		    String attachType = request.getParameter("attachType");
		    String attachId = request.getParameter("attachId");
		    if ((!YunRuiCommonUtil.nullToEmpty(attachId).equals("")) && (!YunRuiCommonUtil.nullToEmpty(attachType).equals("")))
		    {
		      List<AttachmentBean> list = service.queryPictureList(attachType, attachId);
		      for (int i = 0; i < list.size(); i++)
		      {
		        Map<String, String> dataMap = new HashMap<String, String>();
		        dataMap.put("uuid", ((AttachmentBean)list.get(i)).getUuid());
		        dataMap.put("name", ((AttachmentBean)list.get(i)).getFileName());
		        dataMap.put("url", ((AttachmentBean)list.get(i)).getAttachUrl());
		        resultList.add(dataMap);
		      }
		      resultMap.put("total", Integer.valueOf(list.size()));
		    }
		    resultMap.put("results", resultList);
		    try {
		      response.setContentType("application/json; charset=utf-8");
		      if (iflocal) {
		        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
		        response.setHeader("Access-Control-Allow-Credentials", "true");
		      }
		      response.getWriter().write(JSONArray.fromObject(resultMap).toString());
		      response.getWriter().flush();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		  }

}
