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
import org.springframework.web.servlet.ModelAndView;

import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;
import cn.yunrui.common.util.YunRuiCommonUtil;
import cn.yunrui.intfirectrlsys.entity.AttachmentBean;
import cn.yunrui.intfirectrlsys.service.DrillingProgramService;

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


@Controller
@RequestMapping("/drillingProgramControl")
public class DrillingProgramControl {
	
	
private static boolean iflocal = true;
	
	@RequestMapping(value = "/init.htm", method = RequestMethod.GET)
	public String wg(HttpServletRequest request, HttpServletResponse response) {
		return "ifcs/drillingProgram";
	}
	@Resource(name = "drillingProgramService")
	private DrillingProgramService drillingProgramService;
	
	
	
	/*@RequestMapping(value = "/aaa.htm",method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView  aaa(HttpServletRequest request , HttpServletResponse response)  throws UnsupportedEncodingException, ParseException {
		
		ModelAndView mav = new ModelAndView("MyJsp1"); 
		
		System.out.println( mav);
		System.out.println("进入action方法了22");
		Map<String,Object> order = new HashMap();
		order.put("a", "yu");
		order.put("b", "rrr");
		order.put("c", "gfgg");
		System.out.println(order);
	    mav.addObject("order", order); 
	    //定义一个成员变量
	    
		return mav;
	}
	*/
	
	
	
	/**
	 * 查询演练方案
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/queryYLFA.htm")
	public void queryYLFA(HttpServletRequest request , HttpServletResponse response)  throws UnsupportedEncodingException, ParseException {
		request.setCharacterEncoding("utf-8");
		String xfyh_id = request.getParameter("xfyh_id");
		String xfyh_name = request.getParameter("xfyh_name");
		System.out.println("xfyh_id=="+xfyh_id);
		System.out.println("xfyh_name=="+xfyh_name);
		List<Map<String,Object>> list = new ArrayList<Map<String , Object>>();
		list = drillingProgramService.queryYLFAMB(xfyh_id);
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
	 * 按时间条件查询演练方案
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/tjqueryYLFA.htm")
	public void tjqueryYLFA(HttpServletRequest request , HttpServletResponse response)  throws UnsupportedEncodingException, ParseException {
		request.setCharacterEncoding("utf-8");
		String xfyh_id = request.getParameter("xfyh_id");
		String xfyh_name = request.getParameter("xfyh_name");
		String sj = request.getParameter("sj");
		String endtime =request.getParameter("endtime");
		System.out.println("xfyh_id=="+xfyh_id);
		System.out.println("xfyh_name=="+xfyh_name);
		System.out.println("start=="+sj);
		System.out.println("end=="+endtime);
		List<Map<String,Object>> list = new ArrayList<Map<String , Object>>();
		list = drillingProgramService.tjqueryYLFA(xfyh_id, sj, endtime);
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
	 * 新增演练方案主表
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws ParseException
	 */
	
	@RequestMapping(value = "/insertYLFA.htm")
	public void insertYLFA(HttpServletRequest request , HttpServletResponse response ) throws UnsupportedEncodingException, ParseException{
		request.setCharacterEncoding("utf-8");
		
		String rs_id =  request.getParameter("rs_id");
		//主键随机
		
		
		//消防用户ID
		String rs_xfyh_id = request.getParameter("rs_xfyh_id");
		//消防用户名称
		String rs_xfyh_name = request.getParameter("rs_xfyh_name");
		//预案说明
		String rs_explain = request.getParameter("rs_explain");
		//上传时间 rs_upload_time
		Date currentTime = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String rs_upload_time = sdf.format(currentTime); 

		//组织指挥部地点
		String or_command_site=request.getParameter("or_command_site");
		String or_phone=request.getParameter("or_phone");
		String or_zhbzzh=request.getParameter("or_zhbzzh");
		String or_zhbfzzh=request.getParameter("or_zhbfzzh");
		String or_mhfzr=request.getParameter("or_mhfzr");
		String or_mhcy=request.getParameter("or_mhcy");
		String or_ssfzr=request.getParameter("or_ssfzr");
		String or_sscy=request.getParameter("or_sscy");
		String or_txfzr=request.getParameter("or_txfzr");
		String or_txcy=request.getParameter("or_txcy");
		String or_aqfzr=request.getParameter("or_aqfzr");
		String or_aqcy=request.getParameter("or_aqcy");
		String or_yjfzr=request.getParameter("or_yjfzr");
		String or_yjcy=request.getParameter("or_yjcy");
		String or_hqfzr=request.getParameter("or_hqfzr");
		String or_hqcy=request.getParameter("or_hqcy");	
		int i=drillingProgramService.insertYLFA(rs_id, rs_xfyh_id,rs_xfyh_name, rs_explain, rs_upload_time, or_command_site, or_phone, or_zhbzzh, or_zhbfzzh, or_mhfzr,
				or_mhcy, or_ssfzr, or_sscy, or_txfzr, or_txcy, or_aqfzr, or_aqcy, or_yjfzr, or_yjcy, or_hqfzr, or_hqcy);	
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
	 * 根据ID查看方案内容 详情
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/queryYLFA_By_ID.htm")
	public void queryYLFA_By_ID(HttpServletRequest request , HttpServletResponse response)  throws UnsupportedEncodingException, ParseException {
		request.setCharacterEncoding("utf-8");
		String rs_id =request.getParameter("rs_id");
		System.out.println("rs_id"+ rs_id);
		List<Map<String,Object>> list = new ArrayList<Map<String , Object>>();
		list = drillingProgramService.queryYLFA_By_ID(rs_id);
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
	 * 编辑演练方案
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/updateYLFA.htm")
	public void updateYLFA(HttpServletRequest request , HttpServletResponse response ) throws UnsupportedEncodingException, ParseException{
		request.setCharacterEncoding("utf-8");
		System.out.println("编辑  编辑吧就编辑加不加编辑编辑编辑就");
		//消防用户ID
		String rs_id= request.getParameter("rs_id");
		String rs_xfyh_id = request.getParameter("rs_xfyh_id");
		//消防用户名称
		String rs_xfyh_name = request.getParameter("rs_xfyh_name");
		//预案说明
		String rs_explain = request.getParameter("rs_explain");
		//上传时间 rs_upload_time
		Date currentTime = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String rs_upload_time = sdf.format(currentTime); 

		//组织指挥部地点
		String or_command_site=request.getParameter("or_command_site");
		String or_phone=request.getParameter("or_phone");
		String or_zhbzzh=request.getParameter("or_zhbzzh");
		String or_zhbfzzh=request.getParameter("or_zhbfzzh");
		String or_mhfzr=request.getParameter("or_mhfzr");
		String or_mhcy=request.getParameter("or_mhcy");
		String or_ssfzr=request.getParameter("or_ssfzr");
		String or_sscy=request.getParameter("or_sscy");
		String or_txfzr=request.getParameter("or_txfzr");
		String or_txcy=request.getParameter("or_txcy");
		String or_aqfzr=request.getParameter("or_aqfzr");
		String or_aqcy=request.getParameter("or_aqcy");
		String or_yjfzr=request.getParameter("or_yjfzr");
		String or_yjcy=request.getParameter("or_yjcy");
		String or_hqfzr=request.getParameter("or_hqfzr");
		String or_hqcy=request.getParameter("or_hqcy");	
		int i=drillingProgramService.updateYLFA(rs_xfyh_id,rs_xfyh_name, rs_explain, rs_upload_time, or_command_site, or_phone, or_zhbzzh, or_zhbfzzh, or_mhfzr,
				or_mhcy, or_ssfzr, or_sscy, or_txfzr, or_txcy, or_aqfzr, or_aqcy, or_yjfzr, or_yjcy, or_hqfzr, or_hqcy,rs_id);	
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
	 * 编辑演练方案 只编辑方案 不编辑内容
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/updateonlyYLFA.htm")
	public void updateonlyYLFA(HttpServletRequest request , HttpServletResponse response ) throws UnsupportedEncodingException, ParseException{
		request.setCharacterEncoding("utf-8");
		
		//消防用户ID
		String rs_id= request.getParameter("rs_id");
		String rs_xfyh_id = request.getParameter("rs_xfyh_id");
		//消防用户名称
		String rs_xfyh_name = request.getParameter("rs_xfyh_name");
		//预案说明
		String rs_explain = request.getParameter("rs_explain");
		//上传时间 rs_upload_time
		Date currentTime = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String rs_upload_time = sdf.format(currentTime); 

		//组织指挥部地点
		String or_command_site=request.getParameter("or_command_site");
		String or_phone=request.getParameter("or_phone");
		String or_zhbzzh=request.getParameter("or_zhbzzh");
		String or_zhbfzzh=request.getParameter("or_zhbfzzh");
		String or_mhfzr=request.getParameter("or_mhfzr");
		String or_mhcy=request.getParameter("or_mhcy");
		String or_ssfzr=request.getParameter("or_ssfzr");
		String or_sscy=request.getParameter("or_sscy");
		String or_txfzr=request.getParameter("or_txfzr");
		String or_txcy=request.getParameter("or_txcy");
		String or_aqfzr=request.getParameter("or_aqfzr");
		String or_aqcy=request.getParameter("or_aqcy");
		String or_yjfzr=request.getParameter("or_yjfzr");
		String or_yjcy=request.getParameter("or_yjcy");
		String or_hqfzr=request.getParameter("or_hqfzr");
		String or_hqcy=request.getParameter("or_hqcy");	
		int i=drillingProgramService.updateonlyYLFA(rs_xfyh_id, rs_xfyh_name, rs_explain, rs_upload_time, or_command_site, or_phone, or_zhbzzh, or_zhbfzzh, or_mhfzr, or_mhcy, or_ssfzr, or_sscy, or_txfzr, or_txcy, or_aqfzr, or_aqcy, or_yjfzr, or_yjcy, or_hqfzr, or_hqcy, rs_id);	
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
	 * 导出演练预案详情
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	//导出演练预案详情===========
	@RequestMapping(value = "/exportPDF.htm")
	@ResponseBody
	public String exportPDF(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
						response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
			request.setCharacterEncoding("utf-8");
			
			//组织指挥部地点
			String or_command_site=request.getParameter("or_command_site");
			String or_phone=request.getParameter("or_phone");
			String or_zhbzzh=request.getParameter("or_zhbzzh");
			String or_zhbfzzh=request.getParameter("or_zhbfzzh");
			String or_mhfzr=request.getParameter("or_mhfzr");
			String or_mhcy=request.getParameter("or_mhcy");
			String or_ssfzr=request.getParameter("or_ssfzr");
			String or_sscy=request.getParameter("or_sscy");
			String or_txfzr=request.getParameter("or_txfzr");
			String or_txcy=request.getParameter("or_txcy");
			String or_aqfzr=request.getParameter("or_aqfzr");
			String or_aqcy=request.getParameter("or_aqcy");
			String or_yjfzr=request.getParameter("or_yjfzr");
			String or_yjcy=request.getParameter("or_yjcy");
			String or_hqfzr=request.getParameter("or_hqfzr");
			String or_hqcy=request.getParameter("or_hqcy");	
			
			
			String pdf = null;
			File pdfFile = null;
			InputStream inputStream = null;
			FileInputStream onputStream  = null;
			OutputStream outputStream = null;

			try {
				
				Authentication auth = RequestContextSecurity.getAuthentication();

				pdf = this.writeElecOptimizeReport2(request,or_command_site, or_phone, or_zhbzzh, or_zhbfzzh, or_mhfzr, or_mhcy, or_ssfzr, or_sscy, or_txfzr, or_txcy, or_aqfzr, or_aqcy, or_yjfzr, or_yjcy, or_hqfzr, or_hqcy);
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
				
				
				String fileName = "演练预案.pdf";
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
	
	public static void setWatermark(  BufferedOutputStream bos , String input, String waterMarkName, int permission) throws DocumentException, IOException {  
  
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
		
		
	//演练方案 记录导出
	public  String writeElecOptimizeReport2(HttpServletRequest request,String or_command_site,String or_phone,String or_zhbzzh,String or_zhbfzzh,String or_mhfzr,String or_mhcy,
			String or_ssfzr,String or_sscy,String or_txfzr ,String or_txcy,String or_aqfzr ,String or_aqcy,String or_yjfzr ,String or_yjcy,String or_hqfzr ,String or_hqcy
			){
		Document document = new Document(PageSize.A4, 20, 20, 20, 20);
		  String uploadPath = "";
		
	    try {
	    	
			 uploadPath = getUploadPath(request);// 文件上传路径
	    	
	    	 PdfWriter writer = PdfWriter.getInstance(document,
	                    new FileOutputStream(uploadPath+"YLYA.pdf"));
	            document.open();

	    
	            BaseFont baseFont = BaseFont.createFont("STSongStd-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);  
				   Font fontbt = new Font(baseFont);    
		            fontbt.setSize(20);
		            Font fontzw = new Font(baseFont); 
		            fontzw.setSize(13);
		         Paragraph t = new Paragraph("灭火和应急疏散预案",fontbt);
		         t.setAlignment(1);
		         t.setAlignment(Element.ALIGN_CENTER);
		            document.add(t);
		            
		           PdfPTable table11 = new PdfPTable(1); 
		           
		           table11.getDefaultCell().setBorder(PdfPCell.NO_BORDER);  
				   table11.addCell(new Paragraph("指导思想", fontzw)); 

				   table11.addCell(new Paragraph("         以《消防法》和《机关、团体、企业、事业单位消防安全管理规定》为依据，牢固确立以人为本的主导思想，按照“救人重于救物”的原则，旨在客观分析火灾规律，合理配置人力资源，建立完善灭火和应急疏散组织机构，明确应急疏散及控制、扑救初期火灾的程序、措施，进而构建单位内部的应急保障体系，提升自防自救的整体能力。",fontzw));
				   table11.addCell(new Paragraph("目标", fontzw)); 
				   table11.addCell(new Paragraph("         通过定期组织演练,增强单位全员消防安全意识，实现岗位、职能、措施的协调统一，提高单位自主应变能力，达到灭火疏散组织工作的程序化、系统化、职责化，预防发生群死群伤火灾事故，有效减少火灾事故中的人员伤亡。", fontzw));
				   table11.addCell(new Paragraph("一、组织机构，人员分工", fontzw));
				   table11.addCell(new Paragraph("1、组织指挥部 ", fontzw));
				   
				   
				   PdfPCell pdfCell = new PdfPCell();
				   pdfCell.setMinimumHeight(30);
				   /*pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				   pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);*/
				   pdfCell = new PdfPCell(new Phrase("                                                                      组织指挥部", fontzw));
				   pdfCell.setColspan(2);
		           table11.addCell(pdfCell);
		           
				   PdfPTable table1 = new PdfPTable(2);      
				   table1.addCell(new Paragraph("                                   总指挥 ", fontzw));     
				   table1.addCell(new Paragraph(or_zhbzzh ,fontzw));
				   table1.addCell(new Paragraph("                                   副总指挥 ", fontzw));     
				   table1.addCell(new Paragraph(or_zhbfzzh ,fontzw));
				   table1.addCell(new Paragraph("                                   指挥部地点 ", fontzw));     
				   table1.addCell(new Paragraph(or_command_site ,fontzw));
				   table1.addCell(new Paragraph("                                   电话 ", fontzw));     
				   table1.addCell(new Paragraph(or_phone ,fontzw));
				   table1.addCell(new Paragraph("                                   职责 ", fontzw));     
				   table1.addCell(new Paragraph("指挥单位灭火、应急疏散行动的展开" ,fontzw));
				   
				   
				   PdfPTable table12 = new PdfPTable(1); 
		           table12.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		           table12.addCell(new Paragraph("2、灭火行动组 ", fontzw));
		           
		           PdfPCell pdfCell2 = new PdfPCell();
	               pdfCell2 = new PdfPCell(new Phrase("                                                                      灭火行动组", fontzw));
				   pdfCell2.setColspan(2);
				   
				   table12.addCell(pdfCell2);
				   PdfPTable table2 = new PdfPTable(2);
				   table2.addCell(new Paragraph("                                   负责人 ",fontzw)); 
				   table2.addCell(new Paragraph(or_mhfzr,fontzw)); 
				   table2.addCell(new Paragraph("                                   成员 ",fontzw));
				   table2.addCell(new Paragraph(or_mhcy,fontzw));
				   table2.addCell(new Paragraph("                                   职责 ", fontzw));     
				   table2.addCell(new Paragraph("扑灭初期火灾、防止火势蔓延、救助被困人员" ,fontzw));
				   
				   
				   PdfPTable table13 = new PdfPTable(1); 
		           table13.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		           table13.addCell(new Paragraph("3、疏散引导组", fontzw));
		           PdfPCell pdfCell3 = new PdfPCell();
	               pdfCell3 = new PdfPCell(new Phrase("                                                                      疏散引导组", fontzw)); 
	               
				   table13.addCell(pdfCell3);
				   PdfPTable table3 = new PdfPTable(2);
				   table3.addCell(new Paragraph("                                   负责人 ",fontzw)); 
				   table3.addCell(new Paragraph(or_ssfzr,fontzw)); 
				   table3.addCell(new Paragraph("                                   成员 ",fontzw));
				   table3.addCell(new Paragraph(or_sscy,fontzw));
				   table3.addCell(new Paragraph("                                   职责 ", fontzw));     
				   table3.addCell(new Paragraph("组织人员疏散，掌握人员疏散情况，及时向指挥部汇报" ,fontzw));
				   
				   
				   PdfPTable table14 = new PdfPTable(1); 
		           table14.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		           table14.addCell(new Paragraph("4、通讯联络组", fontzw));
		           
		           PdfPCell pdfCell4 = new PdfPCell();
				   pdfCell4 = new PdfPCell(new Phrase("                                                                      通讯联络组", fontzw)); 
				   
				   table14.addCell(pdfCell4);
				   PdfPTable table4 = new PdfPTable(2);
				   table4.addCell(new Paragraph("                                   负责人 ",fontzw)); 
				   table4.addCell(new Paragraph(or_txfzr,fontzw)); 
				   table4.addCell(new Paragraph("                                   成员 ",fontzw));
				   table4.addCell(new Paragraph(or_txcy,fontzw));
				   table4.addCell(new Paragraph("                                   职责 ", fontzw));     
				   table4.addCell(new Paragraph("保证各组与指挥部的通讯联络及情况的反馈" ,fontzw));
				   
				   
				   PdfPTable table15 = new PdfPTable(1); 
		           table15.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		           table15.addCell(new Paragraph("5、安全防护组", fontzw));
		           
		           PdfPCell pdfCell5 = new PdfPCell();
				   pdfCell5 = new PdfPCell(new Phrase("                                                                      安全防护组", fontzw)); 
				   
				   table15.addCell(pdfCell5);
				   PdfPTable table5 = new PdfPTable(2);
				   table5.addCell(new Paragraph("                                   负责人 ",fontzw)); 
				   table5.addCell(new Paragraph(or_aqfzr,fontzw)); 
				   table5.addCell(new Paragraph("                                   成员 ",fontzw));
				   table5.addCell(new Paragraph(or_aqcy,fontzw));
				   table5.addCell(new Paragraph("                                   职责 ", fontzw));     
				   table5.addCell(new Paragraph("守护现场警戒区，防止坏人进行破坏" ,fontzw));
				   
				   
				   PdfPTable table16 = new PdfPTable(1); 
		           table16.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		           table16.addCell(new Paragraph("6、应急救护组", fontzw));
		           
		           PdfPCell pdfCell6 = new PdfPCell();
				   pdfCell6 = new PdfPCell(new Phrase("                                                                      应急救护组", fontzw)); 
				   
				   table16.addCell(pdfCell6);
				   PdfPTable table6 = new PdfPTable(2);
				   table6.addCell(new Paragraph("                                   负责人 ",fontzw)); 
				   table6.addCell(new Paragraph(or_yjfzr,fontzw)); 
				   table6.addCell(new Paragraph("                                   成员 ",fontzw));
				   table6.addCell(new Paragraph(or_yjcy,fontzw));
				   table6.addCell(new Paragraph("                                   职责 ", fontzw));     
				   table6.addCell(new Paragraph("紧急救护受伤人员，避免二次伤害" ,fontzw));
				   
				   PdfPTable table17 = new PdfPTable(1); 
		           table17.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		           table17.addCell(new Paragraph("7、后勤保障组", fontzw));
		           
		           
		           PdfPCell pdfCell7 = new PdfPCell();
				   pdfCell7 = new PdfPCell(new Phrase("                                                                      后勤保障组", fontzw)); 
				   
				   table17.addCell(pdfCell7);
				   PdfPTable table7 = new PdfPTable(2);
				   table7.addCell(new Paragraph("                                   负责人 ",fontzw)); 
				   table7.addCell(new Paragraph(or_hqfzr,fontzw)); 
				   table7.addCell(new Paragraph("                                   成员 ",fontzw));
				   table7.addCell(new Paragraph(or_hqcy,fontzw));
				   table7.addCell(new Paragraph("                                   职责 ", fontzw));     
				   table7.addCell(new Paragraph("保障消防设施、设备正常运转，并向火场运送灭火器、抢险工具等" ,fontzw));
				   
				   
		           PdfPTable table99 = new PdfPTable(1); 
		           table99.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		           
		           table99.addCell(new Paragraph("二、灭火、应急预案的相关程序和措施",fontzw));
				   table99.addCell(new Paragraph("（一）报警、接警处置程序",fontzw));
				   table99.addCell(new Paragraph("         1、火警信号",fontzw));
				   table99.addCell(new Paragraph("        （1）任何员工发现火情应立即向消防控制室报告，方法为拨打消防控制室电话报警或启动手动火灾报警按钮。",fontzw));
				   table99.addCell(new Paragraph("        （2）火灾报警控制器发出的火警信号。",fontzw));
				   table99.addCell(new Paragraph("         2、火警确认",fontzw));
				   table99.addCell(new Paragraph("         消防控制室值班人员对任何火灾信号都必须尽快核实。",fontzw));
				   table99.addCell(new Paragraph("        （1）员工以电话或无线对讲机的报警信号应立即视为火警确实。",fontzw));
				   table99.addCell(new Paragraph("        （2）当收到固定火灾报警设施信号时，控制中心应立即使用对讲机或其他通讯设备通知报警点附近警卫或报警部位员工进行现场核实，根据语音反馈信息确认火警。",fontzw));
				   table99.addCell(new Paragraph("         3、报警及进入应急程序",fontzw));
				   table99.addCell(new Paragraph("         确认火情后应立即向公安消防队“119”报警（告知本单位发生火灾的部位、层数、燃烧的物质、是否有人员被困等已掌握的情况），并通知领导及指挥部其他成员到消防控制室集合。",fontzw));
				   table99.addCell(new Paragraph("（二）灭火、应急预案的展开",fontzw));
				   table99.addCell(new Paragraph("         1、指挥部应急程序和措施",fontzw));
				   table99.addCell(new Paragraph("        （1）指挥部成员（成员不在岗的，由带班主管代替）接到通知后立即到消防控制室集合（根据具体情况可直接在火场适当位置组成临时指挥部，以便迅速有效的指挥）。",fontzw));
				   table99.addCell(new Paragraph("        （2）调集灭火行动组控制火点消灭火灾，防止火势蔓延。",fontzw));
				   table99.addCell(new Paragraph("        （3）下达疏导人员指令。根据现场火情情况，利用应急广播或电话通知疏导引导组到现场疏散人员撤离现场。",fontzw));
				   table99.addCell(new Paragraph("        （4）下达启动消防设施指令。",fontzw));
				   table99.addCell(new Paragraph("        （5）根据现场情况配合公安部门开展灭火救援工作。",fontzw));
				   table99.addCell(new Paragraph("         2、灭火行动组应急程序和措施",fontzw));
				   table99.addCell(new Paragraph("        （1）灭火区域的义务消防队员要立即用灭火器、消火栓扑救初期火灾。",fontzw));
				   table99.addCell(new Paragraph("        （2）灭火行动组成员接到通知后立即到消防控制室携带防护设施及灭火器材前往灭火部位，展开灭火行动，并由负责人向指挥部反馈情况。",fontzw));
				   table99.addCell(new Paragraph("        （3）将易燃易爆物品及时搬到安全地点防止火势蔓延。",fontzw));
				   table99.addCell(new Paragraph("        （4）消防控制室根据火场情况和指挥部的指令进行启动相应的消防设施。",fontzw));
				   table99.addCell(new Paragraph("         3、疏散引导组的应急程序和措施",fontzw));
				   table99.addCell(new Paragraph("        （1）发生火灾后，疏散组负责人和着火部位疏散组成员立即到火灾现场组织疏散。",fontzw));
				   table99.addCell(new Paragraph("        （2）疏散引导员要尽快引导人员疏散自救，确保人员安全快速疏散。",fontzw));
				   table99.addCell(new Paragraph("        （3）到各出入口维护疏散秩序，防止有人再次进入现场警戒区。",fontzw));
				   table99.addCell(new Paragraph("        （4）消防控制室启动应急广播进行疏散（广播词附后）。",fontzw));
				   table99.addCell(new Paragraph("         4、通讯联络组的应急程序和措施",fontzw));
				   table99.addCell(new Paragraph("        （1）通讯联络组成员随时向指挥部转达火场信息和传达指挥部指令。",fontzw));
				   table99.addCell(new Paragraph("        （2）通讯联络组成员留存指挥部和各行动组负责人联系电话以便随时联系。",fontzw));
				   table99.addCell(new Paragraph("         5、安全防护组应急程序和措施",fontzw));
				   table99.addCell(new Paragraph("        （1）发生火灾后，安全防护组负责人及组员立即到指定地点布置人员进行警戒，防止外人进入火场。",fontzw));
				   table99.addCell(new Paragraph("        （2）帮助人员撤离火场，并进行安置。",fontzw));
				   table99.addCell(new Paragraph("        （3）指挥引导消防车进入火场外围，停靠至适合灭火位置。",fontzw));
				   table99.addCell(new Paragraph("        （4）疏导围观群众，不要堵塞道路。",fontzw));
				   table99.addCell(new Paragraph("        （5）及时通知车主将车辆开离现场以免损坏。",fontzw));
				   table99.addCell(new Paragraph("         6、救护组应急程序和措施",fontzw));
				   table99.addCell(new Paragraph("        （1）对受伤人员及时抢救。",fontzw));
				   table99.addCell(new Paragraph("        （2）对受伤严重的要及时拨打“120”急救中心电话。",fontzw));
				   table99.addCell(new Paragraph("         7、后勤保障组的应急程序和措施",fontzw));
				   table99.addCell(new Paragraph("        （1）向火场运送灭火器以及其他所用工具。火场烟雾大时，应向灭火抢救人员提供湿毛巾、防烟面具等防护器材。",fontzw));
				   table99.addCell(new Paragraph("        （2）保证消防设施正常运行。",fontzw));
				   table99.addCell(new Paragraph("        （3）及时排除消防设施故障。",fontzw));
				   table99.addCell(new Paragraph("三、要求",fontzw));
				   table99.addCell(new Paragraph("         1、在抢险救火过程中，所有人员都要积极发挥主动作用，做好配合灭火和疏散工作。",fontzw));
				   table99.addCell(new Paragraph("         2、所有参战人员都要听从指挥部的统一指挥，接到命令迅速到达指定地点。",fontzw));
				   table99.addCell(new Paragraph("         3、各级人员要协同合作，共同完成紧急情况下的各项任务。",fontzw));
				   table99.addCell(new Paragraph("         4、烟雾较大时抢险人员应用随身带的湿毛巾、防烟面具将口、鼻捂住并身体贴近地面行走，撤离火场时要沿着安全出口指示灯的方向撤离。",fontzw));
				   table99.addCell(new Paragraph("         5、本预案中指挥及各负责人员如未在班，由代班人员履行职责。",fontzw));
				   table99.addCell(new Paragraph("         6、消防控制室应留存各组负责人员联系电话，以便及时联系。",fontzw));
				   table99.addCell(new Paragraph("四、注意事项",fontzw));
				   table99.addCell(new Paragraph("         1、预案贯彻，单位员工及各责任人要认真学习本预案，熟悉各自的职责和任务。",fontzw));
				   table99.addCell(new Paragraph("         2、预案各组的组成。本预案要求各组的组员也是上一级灭火组的组成人员。",fontzw));
				   table99.addCell(new Paragraph("         3、预案启动。在火警发生时，立即投入灭火，并根据具体情况逐级启动灭火和疏散预案，全力将火灾控制在初期阶段。如火灾难以控制，立即启动本预案。附：消防应急广播词",fontzw));
				   table99.addCell(new Paragraph("                   ")); 

				   table11.setSplitLate(false);
		   		   table11.setSplitRows(true);
				   
					document.add(table11);
					document.add(table1);
					document.add(table12);    
				   	document.add(table2);
					document.add(table13);    
				   	document.add(table3);
					document.add(table14);    
				   	document.add(table4);
					document.add(table15);    
				   	document.add(table5);
					document.add(table16);    
				   	document.add(table6);
					document.add(table17);    
				   	document.add(table7);
				   	document.add(table99);
		         
				   	document.close();
		            writer.close();
	        
	        document.close();
	        writer.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return uploadPath+"YLYA.pdf";
		
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
	
	/**
	 * 编辑时查询附件
	 * @param request
	 * @param response
	 * @throws Exception
	 */
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
	      List<AttachmentBean> list = drillingProgramService.queryPictureList(attachType, attachId);
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
