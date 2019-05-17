package cn.yunrui.intfirectrlsys.action;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.yunrui.intfirectrlsys.util.ExcelUtil;
import cn.yunrui.intfirectrlsys.util.PDFUtil;
import cn.yunrui.intfirectrlsys.util.Util;
import com.i380v.openservices.utils.ApplicationContextUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import cn.cc.cisp.code.entity.Code;
import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;

import cn.yunrui.intfirectrlsys.common.CommonMethods;
import cn.yunrui.intfirectrlsys.common.CommonValues;
import cn.yunrui.intfirectrlsys.domain.AutoOutFire;
import cn.yunrui.intfirectrlsys.domain.CodeEntity;
import cn.yunrui.intfirectrlsys.domain.DqhzGjDetail;
import cn.yunrui.intfirectrlsys.domain.DrmRoom;
import cn.yunrui.intfirectrlsys.domain.DryFireFightingInfo;
import cn.yunrui.intfirectrlsys.domain.ELAEISInfo;
import cn.yunrui.intfirectrlsys.domain.FireDetectInfo;
import cn.yunrui.intfirectrlsys.domain.FireProofDoorInfo;
import cn.yunrui.intfirectrlsys.domain.FireProtectInfo;
import cn.yunrui.intfirectrlsys.domain.FireSystemInfo;
import cn.yunrui.intfirectrlsys.domain.FoamFireFightingInfo;
import cn.yunrui.intfirectrlsys.domain.GasFireExtinguishInfo;
import cn.yunrui.intfirectrlsys.domain.Header;
import cn.yunrui.intfirectrlsys.domain.OnDuty;
import cn.yunrui.intfirectrlsys.domain.OnDutyVo;
import cn.yunrui.intfirectrlsys.domain.Params;
import cn.yunrui.intfirectrlsys.domain.ResultMessage;
import cn.yunrui.intfirectrlsys.domain.SmokeControlInfo;
import cn.yunrui.intfirectrlsys.domain.TreeNode;
import cn.yunrui.intfirectrlsys.domain.WaterMistInfo;
import cn.yunrui.intfirectrlsys.domain.WaterSprayInfo;
import cn.yunrui.intfirectrlsys.service.FireDetectService;


@Controller
@RequestMapping("/firedetect")
public class FireDetectControl {
	
	private static boolean iflocal = Util.ifbd;
	

	
	
	@Resource(name="firedetectservice") 
	private  FireDetectService fireDetectService;

	
	@RequestMapping(value = "/init.htm",method = RequestMethod.GET)
	public String init(Model model){	
	    return "/ifcs/zbrzcx";
	}
	
	
	/**
	 * 获取树的根节点
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/treeheader.htm",produces = "application/json; charset=utf-8",method = RequestMethod.POST)
	@ResponseBody
	public void treeheader(HttpServletRequest request, HttpServletResponse response){	
	    TreeNode header = new TreeNode();
	    header = fireDetectService.getTreeheader();
        ArrayList<TreeNode> hl =new ArrayList<TreeNode>();
        hl.add(header);
        String rst = JSONArray.fromObject(hl).toString();
        System.out.println("------------"+rst);		
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){				  
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	//获取消防用户信息
	@RequestMapping(value = "/getXfyhListCommon.htm",method = RequestMethod.POST)
	public void getXfyhListCommon(HttpServletRequest request, HttpServletResponse response){	
		    Authentication auth = RequestContextSecurity.getAuthentication();		   		    
            ArrayList<CodeEntity> xfyhlist =fireDetectService.getXfyhListCommon(auth.getBureauNo(),auth.getOrgNo());		
  	        String rst = JSONArray.fromObject(xfyhlist).toString();                     
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){				  
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
	
	
	
	
	
	
	//分页查询
	@RequestMapping(value = "/dqhzgriddata.htm", method = RequestMethod.POST)
	public void dqhzgriddata(HttpServletRequest request, HttpServletResponse response){	
		    try {
				request.setCharacterEncoding("utf-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    
		  Authentication auth = RequestContextSecurity.getAuthentication();          
		  int pageSize = Integer.parseInt(request.getParameter("pageSize"));
		  int current = Integer.parseInt(request.getParameter("current"));
		  int star = (current-1)*pageSize;
		  int limit = pageSize;
		    String companyId = request.getParameter("companyId");
		    System.out.println("2809"+companyId);
		    String Name = request.getParameter("peopleid");
		    String roomId =request.getParameter("roomId");
		    System.out.println("2809"+Name);
		    String start=request.getParameter("start");
		    String end =request.getParameter("end");
		    System.out.println("2815"+start);
		    System.out.println("2816"+end);
		    System.out.println("28317"+roomId);
		    
		    Params params=new Params();
		    params.setCompanyId(companyId);
		    params.setEnd(end);
		    params.setStart(start);
		    params.setName(Name);
		    params.setStar(star);
		    params.setLimit(limit);
		    params.setRoomId(roomId);
		   
		  //查询所有条目
		  ArrayList <DqhzGjDetail> rst = (ArrayList<DqhzGjDetail>) fireDetectService.dqhzgriddata(params);
		  System.out.println("======="+params.getStart());
		  System.out.println("====="+params.getEnd());
		  System.out.println("----------88888"+rst);
		  //获取总记录数
		  int total = fireDetectService.dqhzgriddataTotal(params);
		  System.out.println("------"+total);
		  
		  String datastr = JSONArray.fromObject(rst).toString();
	      String rststr = "{\"data\": "+datastr+", \"current\": "+current+", \"total\": "+total+"}";
		 
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){
					  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
					  response.setHeader("Access-Control-Allow-Credentials", "true");
					}
				//
				response.getWriter().write(rststr);
				response.getWriter().flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}					
	}
	
	// 查询值班人
	/**
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/queryName.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
	public void queryName(HttpServletRequest request,HttpServletResponse response){
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		 HashMap<String,String> param = Util.getrequestParams(request);
	        System.out.println("224"+param);
			List<DrmRoom> list=fireDetectService.queryName(param);
			String rst = JSONArray.fromObject(list).toString();   
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){				  
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	//获取导出文件头
   public List<Header> getYhZhmxHeader(){
	List<Header> header = new ArrayList<Header>();
	String names[] = new String[]{"编号","所属运维公司","消防用户","消控室编号","值班人员","班次","值班开始时间"};
	String index[] = new String[]{"id","fixCompany","companyName","roomnumber","theSuccessor","classType","start"};
	for (int i=0;i<names.length;i++){
		Header hd = new Header();
		hd.setName(names[i]);
		hd.setDataIndex(index[i]);
		hd.setWidth(80);
		header.add(hd);		
	}
	return header;
}
	//导出操作
   @RequestMapping(value = "/yhzhmxexport.htm", method = RequestMethod.GET)
	public void yhzhmxexport(HttpServletRequest request, HttpServletResponse response){				
		  HashMap<String,String> param = Util.getrequestParams(request);
		  System.out.println("257"+param);
		  //jttype
		    String filename = "值班查询";
			
			List<Header> header = getYhZhmxHeader();
			List list = fireDetectService.cpxxexport(param);
			System.out.println("263"+list);
			try {
				ExcelUtil.toExcel(filename, header,list, false, response);
			} catch (IOException e) {
				e.printStackTrace();
			}				
	}
   

   //查询值班日志详情
   /**
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/queryduty.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
	public void queryduty(HttpServletRequest request,HttpServletResponse response){
		
		String cid = request.getParameter("cid");
		String bid = request.getParameter("bid");
		String shiftTime = request.getParameter("shiftTime");
		
		System.out.println("+++++++++"+"cid="+cid+"bid="+bid+"shiftTime="+shiftTime);
	
			@SuppressWarnings("unchecked")
			List<OnDutyVo> od=fireDetectService.queryduty(cid,bid,shiftTime);
			String rst = JSONArray.fromObject(od).toString();   
			System.out.println("0000000"+rst);
		  try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){				  
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	//获取导出文件头
	   public List<Header> getHeader(){
		List<Header> header = new ArrayList<Header>();
		String names[] = new String[]{"编号","正常","故障","火警","误报","故障报警","监管报警","误报","漏报","问题处理情况","自检","消音","复位","主电源","备用电源","时间","问题处理","设备名称","自动","手动","正常","故障","备注","值班人员"};
		String index[] = new String[]{"id","normal","fault","fire_alarm","misreport","fault_alarm","supervision","false_positives","fail_to_report","treatment_situation","self_check","silencer","reset","main_power_supply","spare","shiftTime","problem_handling","device_name","automatic","manual","system_normal","system_fault","remarks","name"};
		for (int i=0;i<names.length;i++){
			Header hd = new Header();
			hd.setName(names[i]);
			hd.setDataIndex(index[i]);
			hd.setWidth(80);
			header.add(hd);		
		}
		return header;
	}
	   
	   
	   
	   
	
	 //下载值班日志pdf
	 @RequestMapping(value = "/zbrzexport.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
	public @ResponseBody String zbrzexport(HttpServletRequest request,HttpServletResponse response) {
		String pdf = null;
		File pdfFile = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			Authentication auth = RequestContextSecurity.getAuthentication();

			pdf = this.writeElecOptimizeReport(request);
//			pdf="D:\\apache-tomcat-6.0.18\\webapps\\Ifcs\\upload\\AddTableExample.pdf";
			System.out.println("pdf:"+pdf);
			
			

			if ( pdf != null) {
				pdfFile = new File(pdf);
			}
			if (pdfFile != null && pdfFile.exists()) {
				inputStream = new FileInputStream(pdfFile);
			}

			String fileName = "值班日志详情.pdf";
			fileName = new String(fileName.getBytes("GBK"), "ISO8859-1");
			

			response.setContentType("application/x-msdownload");
			response.addHeader("Content-Disposition", "attachment;filename="
					+ fileName);

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
			this.closeInputStream(inputStream);
			this.deleteFile(pdf);
		}
		return pdf;

	}
	   
	   
	   
	   
		

	   private void closeOutputStream(OutputStream outputStream) {
			if (outputStream != null) {
				try {
					System.out.println("-------33333---:"+outputStream);
					outputStream.close();
					System.out.println("----22222------");
				} catch (IOException e) {
					System.out.println("----------");
					e.printStackTrace();
				}catch (Exception e) {
					System.out.println("----------1111");
					e.printStackTrace();
				}
				
				System.out.println("----============---");
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
	
				
		
	
	
	
	
	
	 /* 通过 消防用户获取到该用户下的消防室信息
	 */
	@RequestMapping(value = "/getControlRoomAtion.htm")
	public void getControlRoomAtion(HttpServletRequest request,
			HttpServletResponse response) {

		String id = request.getParameter("companyId");
		@SuppressWarnings("unchecked")
		ArrayList<Map> xfyhlist = (ArrayList<Map>)
				fireDetectService
				.getControlRoomCommon(id);
		String rst = JSONArray.fromObject(xfyhlist).toString();
		System.out.println("控制层打印"+rst);
		try {
			response.setContentType("application/json; charset=utf-8");
			
			 if(iflocal){ response.setHeader("Access-Control-Allow-Origin",
			  "http://localhost:3000");
			 response.setHeader("Access-Control-Allow-Credentials", "true"); }
			 
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//下载pdf
	private static String getUploadPath(HttpServletRequest request) {
		String basePath = request.getSession().getServletContext().getRealPath("/");
		System.out.println("basePath:"+basePath);
		String uploadPath = basePath + "upload" + File.separator;
		System.out.println("uploadPath:"+uploadPath);
		File dir = new File(uploadPath);
		System.out.println("dir:"+dir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return uploadPath;
	}
	
	private static String getFontPath(HttpServletRequest request) {
		String basePath = request.getSession().getServletContext().getRealPath("/");
		String fontPath = basePath + "energyReport" + File.separator + "font" + File.separator + "PingFang Medium.ttf";
		File file = new File(fontPath);
		if (file.exists()) {
			return fontPath;
		}
		return null;
	}

	
	
	

	public  String    writeElecOptimizeReport(HttpServletRequest   request){
		Document document = new Document();
		String uploadPath = "";
		  HashMap<String,String> param = Util.getrequestParams(request);
		  System.out.println("------2222"+param);
			List<OnDutyVo> list =  fireDetectService.zbrzexport(param);
			System.out.println("111111111111"+list);
			System.out.println("第一个List"+list.get(0));
			
//			List<?> list1=list.get(0);
//			for(int i=0; i<list1.size();i++){
//				int str=  (Integer) list1.get(i);
//				System.out.println("第一个"+str);
//			}
//			OnDutyVo entity=new OnDutyVo();
//			if(list.size()>0){
//				entity=list.get(0);
//			}
			
//			OnDutyVo entity=list.get(0);
			
	    try {
	    
	    	BaseFont bfChinese = BaseFont.createFont("STSongStd-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
	    	Font titleFont = new Font(bfChinese, 18, Font.NORMAL, BaseColor.BLACK);
			Font f8 = new Font(bfChinese, 8, Font.NORMAL, BaseColor.BLACK);
			Font f12 = new Font(bfChinese, 12, Font.NORMAL, BaseColor.BLACK);
			
			uploadPath = getUploadPath(request);// 文件上传路径
	    	
//	    	 PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("AddTableExample.pdf"));
	    	 PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(uploadPath+"AddTableExample.pdf"));
	            document.open();
	            
//	            Paragraph title_z=new Paragraph("但是绝对的角度讲111111",titleFont);
//				title_z.setAlignment(1);
//				document.add(title_z);

	        PdfPTable table = new PdfPTable(14); // 14 columns.
	        table.setWidthPercentage(100); // Width 100%
	        table.setSpacingBefore(10f); // Space before table
	        table.setSpacingAfter(10f); // Space after table

	        // Set Column widths
	        float[] columnWidths = {2f,2f,2f,2f,2f,2f,2f,5f,3f,3f,3f,3f,3f,4f};
	        table.setWidths(columnWidths);
          //第一行
	        PdfPCell cell1 = new PdfPCell(new Paragraph("               值班记录电子表格                           值班人员姓名 ："+list.get(0).getName()+"                              值班时间："+list.get(0).getShiftTime(),f8));
	        cell1.setColspan(14);
	        cell1.setBorderColor(BaseColor.BLACK );
	        cell1.setPaddingLeft(10);
	        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell(cell1);
	        

	      //第二行
	        PdfPCell cell2 = new PdfPCell(new Paragraph("火灾报警器运行情况(含消防联动控制器、手动控制盘、消防电话主机、消防应急广播)",f8));
	        cell2.setColspan(7);
	        cell2.setBorderColor(BaseColor.BLACK );
	        cell2.setPaddingLeft(10);
	        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell(cell2);
	      
	        PdfPCell cell3 = new PdfPCell(new Paragraph("报警，故障部位，原因及处理：",f8));
	        cell3.setBorderColor(BaseColor.BLACK );
	        cell3.setPaddingLeft(10);
	        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell(cell3);
	        
	        
	        PdfPCell cell4 = new PdfPCell(new Paragraph("火灾报警器日常检查情况(含消防联动控制器、手动控制盘、消防电话主机、消防应急广播)",f8));
	        cell4.setColspan(5);
	        cell4.setBorderColor(BaseColor.BLACK );
	        cell4.setPaddingLeft(10);
	        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell(cell4);
	       
	        
	        PdfPCell cell5 = new PdfPCell(new Paragraph("发现问题及处理情况：",f8));
	        cell5.setBorderColor(BaseColor.BLACK );
	        cell5.setPaddingLeft(10);
	        cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell(cell5);
	       
	        
	       
	        
	        //第三行
	        PdfPCell cell6 = new PdfPCell(new Paragraph("正常",f8));
	        cell6.setRowspan(2);
	        cell6.setBorderColor(BaseColor.BLACK );
	        cell6.setPaddingLeft(10);
	        cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        
	        table.addCell(cell6);
	        
	        
	        PdfPCell cell61 = new PdfPCell(new Paragraph("故障",f8));
	        cell61.setRowspan(2);
	        cell61.setBorderColor(BaseColor.BLACK);
	        cell61.setPaddingLeft(10);
	        cell61.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell61.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell(cell61);
	        
	        
	        PdfPCell cell62 = new PdfPCell(new Paragraph("火警",f8));
	        cell62.setColspan(2);
	        cell62.setBorderColor(BaseColor.BLACK );
	        cell62.setPaddingLeft(10);
	        cell62.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell62.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell(cell62);
	     
	        
	        PdfPCell cell7 = new PdfPCell(new Paragraph("故障火警",f8));
	        cell7.setRowspan(2);
	        cell7.setBorderColor(BaseColor.BLACK );
	        cell7.setPaddingLeft(10);
	        cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell( cell7);
	        
	        PdfPCell cell71 = new PdfPCell(new Paragraph("监管报警",f8));
	        cell71.setRowspan(2);
	        cell71.setBorderColor(BaseColor.BLACK );
	        cell71.setPaddingLeft(10);
	        cell71.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell71.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell( cell71);
	        
	        PdfPCell cell72 = new PdfPCell(new Paragraph("漏报",f8));
	        cell72.setRowspan(2);
	        cell72.setBorderColor(BaseColor.BLACK );
	        cell72.setPaddingLeft(10);
	        cell72.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell72.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell( cell72);
	        
	        
	        PdfPCell cell8 = new PdfPCell(new Paragraph(""+list.get(0).getTreatment_situation(),f8));
	        cell8.setRowspan(3);
	        cell8.setBorderColor(BaseColor.BLACK );
	        cell8.setPaddingLeft(10);
	        cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell( cell8);
	        
	        
	        
	        PdfPCell cell9 = new PdfPCell(new Paragraph("自检",f8));
	        cell9.setRowspan(2);
	        cell9.setBorderColor(BaseColor.BLACK );
	        cell9.setPaddingLeft(10);
	        cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell( cell9);
	        
	        PdfPCell cell91 = new PdfPCell(new Paragraph("消音",f8));
	        cell91.setRowspan(2);
	        cell91.setBorderColor(BaseColor.BLACK );
	        cell91.setPaddingLeft(10);
	        cell91.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell91.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell( cell91);
	        
	        PdfPCell cell92 = new PdfPCell(new Paragraph("复位",f8));
	        cell92.setRowspan(2);
	        cell92.setBorderColor(BaseColor.BLACK );
	        cell92.setPaddingLeft(10);
	        cell92.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell92.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell( cell92);
	        
	        PdfPCell cell93 = new PdfPCell(new Paragraph("主电源",f8));
	        cell93.setRowspan(2);
	        cell93.setBorderColor(BaseColor.BLACK );
	        cell93.setPaddingLeft(10);
	        cell93.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell93.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell( cell93);
	        
	        PdfPCell cell94 = new PdfPCell(new Paragraph("备用电源",f8));
	        cell94.setRowspan(2);
	        cell94.setBorderColor(BaseColor.BLACK );
	        cell94.setPaddingLeft(10);
	        cell94.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell94.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell( cell94);
	        
	        PdfPCell cell10 = new PdfPCell(new Paragraph(""+list.get(0).getProblem_handling(),f8));
	        cell10.setRowspan(3);
	        cell10.setBorderColor(BaseColor.BLACK );
	        cell10.setPaddingLeft(10);
	        cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell( cell10);
	        
	      
	        //第四行
	        
	        PdfPCell cell41 = new PdfPCell(new Paragraph("火警",f8));
	        cell41.setBorderColor(BaseColor.BLACK );
	        cell41.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell41.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell(cell41);
	        
	        PdfPCell cell42 = new PdfPCell(new Paragraph("误报",f8));
	        cell42.setBorderColor(BaseColor.BLACK );
	        cell42.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell42.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell(cell42);
	        
	        //---dddddd
	        
	        
	        if("true".equals(list.get(0).getNormal())){
	        	
	        //第五行
	        PdfPCell cell7_1 = new PdfPCell(new Paragraph("√",f8));
	        cell7_1.setBorderColor(BaseColor.BLACK );
	        cell7_1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell7_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        table.addCell(cell7_1);
	        }else {
	        	 PdfPCell cell7_1 = new PdfPCell(new Paragraph("",f8));
	        	 cell7_1.setBorderColor(BaseColor.BLACK );
	 	        cell7_1.setHorizontalAlignment(Element.ALIGN_CENTER);
	 	        cell7_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
	 	        table.addCell(cell7_1);
			}
	        
	       if("true".equals(list.get(0).getFault())){
	    	   PdfPCell cell7_2 = new PdfPCell(new Paragraph("√",f8));
		        cell7_2.setBorderColor(BaseColor.BLACK );
		        cell7_2.setHorizontalAlignment(Element.ALIGN_CENTER);
		        cell7_2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		        table.addCell(cell7_2);
	       }else {
	    	   PdfPCell cell7_2 = new PdfPCell(new Paragraph(""));
		        cell7_2.setBorderColor(BaseColor.BLACK);
		        cell7_2.setHorizontalAlignment(Element.ALIGN_CENTER);
		        cell7_2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		        table.addCell(cell7_2);
		}
	        
	       
	        if("true".equals(list.get(0).getFire_alarm())){
	        	PdfPCell cell7_3 = new PdfPCell(new Paragraph("√",f8));
		        cell7_3.setBorderColor(BaseColor.BLACK);
		        cell7_3.setHorizontalAlignment(Element.ALIGN_CENTER);
		        cell7_3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		        table.addCell(cell7_3);
	        }else {
	        	 PdfPCell cell7_3 = new PdfPCell(new Paragraph(""));
	 	        cell7_3.setBorderColor(BaseColor.BLACK);
	 	        cell7_3.setHorizontalAlignment(Element.ALIGN_CENTER);
	 	        cell7_3.setVerticalAlignment(Element.ALIGN_MIDDLE);
	 	        table.addCell(cell7_3);
			}
	       
	        if("true".equals(list.get(0).getFalse_positives())){
	        	 PdfPCell cell7_4 = new PdfPCell(new Paragraph("√",f8));
	 	        cell7_4.setBorderColor(BaseColor.BLACK);
	 	        cell7_4.setHorizontalAlignment(Element.ALIGN_CENTER);
	 	        cell7_4.setVerticalAlignment(Element.ALIGN_MIDDLE);
	 	        table.addCell(cell7_4);
	        }else {
	        	  PdfPCell cell7_4 = new PdfPCell(new Paragraph(""));
	  	        cell7_4.setBorderColor(BaseColor.BLACK);
	  	        cell7_4.setHorizontalAlignment(Element.ALIGN_CENTER);
	  	        cell7_4.setVerticalAlignment(Element.ALIGN_MIDDLE);
	  	        table.addCell(cell7_4);
			}
	      
	        if("true".equals(list.get(0).getFire_alarm())){
	        	 PdfPCell cell7_5 = new PdfPCell(new Paragraph("√",f8));
	 	        cell7_5.setBorderColor(BaseColor.BLACK);
	 	        cell7_5.setHorizontalAlignment(Element.ALIGN_CENTER);
	 	        cell7_5.setVerticalAlignment(Element.ALIGN_MIDDLE);
	 	        table.addCell(cell7_5);
	        }else {
	        	PdfPCell cell7_5 = new PdfPCell(new Paragraph(""));
		        cell7_5.setBorderColor(BaseColor.BLACK);
		        cell7_5.setHorizontalAlignment(Element.ALIGN_CENTER);
		        cell7_5.setVerticalAlignment(Element.ALIGN_MIDDLE);
		        table.addCell(cell7_5);
			}
	        
	        if("true".equals(list.get(0).getSupervision())){
	        	  PdfPCell cell7_6 = new PdfPCell(new Paragraph("√",f8));
	  	        cell7_6.setBorderColor(BaseColor.BLACK);
	  	        cell7_6.setHorizontalAlignment(Element.ALIGN_CENTER);
	  	        cell7_6.setVerticalAlignment(Element.ALIGN_MIDDLE);
	  	        table.addCell(cell7_6);
	        }else {
	        	 PdfPCell cell7_6 = new PdfPCell(new Paragraph(""));
	 	        cell7_6.setBorderColor(BaseColor.BLACK);
	 	        cell7_6.setHorizontalAlignment(Element.ALIGN_CENTER);
	 	        cell7_6.setVerticalAlignment(Element.ALIGN_MIDDLE);
	 	        table.addCell(cell7_6);
			}
	       
	        
	        if("true".equals(list.get(0).getFail_to_report())){
	        	 PdfPCell cell7_7 = new PdfPCell(new Paragraph("√",f8));
	 	        cell7_7.setBorderColor(BaseColor.BLACK);
	 	        cell7_7.setHorizontalAlignment(Element.ALIGN_CENTER);
	 	        cell7_7.setVerticalAlignment(Element.ALIGN_MIDDLE);
	 	        table.addCell(cell7_7);
	        }else {
	        	PdfPCell cell7_7 = new PdfPCell(new Paragraph(""));
		        cell7_7.setBorderColor(BaseColor.BLACK);
		        cell7_7.setHorizontalAlignment(Element.ALIGN_CENTER);
		        cell7_7.setVerticalAlignment(Element.ALIGN_MIDDLE);
		        table.addCell(cell7_7);
			}
	        
	        
	        if("true".equals(list.get(0).getSelf_check())){
	        	 PdfPCell cell7_8 = new PdfPCell(new Paragraph("√",f8));
	 	        cell7_8.setBorderColor(BaseColor.BLACK);
	 	        cell7_8.setHorizontalAlignment(Element.ALIGN_CENTER);
	 	        cell7_8.setVerticalAlignment(Element.ALIGN_MIDDLE);
	 	        table.addCell(cell7_8);
	        }else {
	        	PdfPCell cell7_8 = new PdfPCell(new Paragraph(""));
		        cell7_8.setBorderColor(BaseColor.BLACK);
		        cell7_8.setHorizontalAlignment(Element.ALIGN_CENTER);
		        cell7_8.setVerticalAlignment(Element.ALIGN_MIDDLE);
		        table.addCell(cell7_8);
			}
	       
	        if("true".equals(list.get(0).getSilencer())){
	        	PdfPCell cell7_9 = new PdfPCell(new Paragraph("√",f8));
		        cell7_9.setBorderColor(BaseColor.BLACK);
		        cell7_9.setHorizontalAlignment(Element.ALIGN_CENTER);
		        cell7_9.setVerticalAlignment(Element.ALIGN_MIDDLE);
		        table.addCell(cell7_9);
	        }else {
	        	  PdfPCell cell7_9 = new PdfPCell(new Paragraph(""));
	  	        cell7_9.setBorderColor(BaseColor.BLACK);
	  	        cell7_9.setHorizontalAlignment(Element.ALIGN_CENTER);
	  	        cell7_9.setVerticalAlignment(Element.ALIGN_MIDDLE);
	  	        table.addCell(cell7_9);
			}
	      
	        if("true".equals(list.get(0).getReset())){
	        	PdfPCell cell7_10= new PdfPCell(new Paragraph("√",f8));
		        cell7_10.setBorderColor(BaseColor.BLACK);
		        cell7_10.setHorizontalAlignment(Element.ALIGN_CENTER);
		        cell7_10.setVerticalAlignment(Element.ALIGN_MIDDLE);
		        table.addCell(cell7_10);
	        }else {
	        	 PdfPCell cell7_10= new PdfPCell(new Paragraph(""));
	 	        cell7_10.setBorderColor(BaseColor.BLACK);
	 	        cell7_10.setHorizontalAlignment(Element.ALIGN_CENTER);
	 	        cell7_10.setVerticalAlignment(Element.ALIGN_MIDDLE);
	 	        table.addCell(cell7_10);
			}
	       
	        if("true".equals(list.get(0).getMain_power_supply())){
	        	 PdfPCell cell7_11 = new PdfPCell(new Paragraph("√",f8));
	 	        cell7_11.setBorderColor(BaseColor.BLACK);
	 	        cell7_11.setHorizontalAlignment(Element.ALIGN_CENTER);
	 	        cell7_11.setVerticalAlignment(Element.ALIGN_MIDDLE);
	 	        table.addCell(cell7_11);
	        }else {
	        	PdfPCell cell7_11 = new PdfPCell(new Paragraph(""));
		        cell7_11.setBorderColor(BaseColor.BLACK);
		        cell7_11.setHorizontalAlignment(Element.ALIGN_CENTER);
		        cell7_11.setVerticalAlignment(Element.ALIGN_MIDDLE);
		        table.addCell(cell7_11);
			}
	        
	        if("true".equals(list.get(0).getMain_power_supply())){
	        	PdfPCell cell7_12 = new PdfPCell(new Paragraph("√",f8));
		        cell7_12.setBorderColor(BaseColor.BLACK);
		        cell7_12.setHorizontalAlignment(Element.ALIGN_CENTER);
		        cell7_12.setVerticalAlignment(Element.ALIGN_MIDDLE);
		        table.addCell(cell7_12);
	        }else {
	        	  PdfPCell cell7_12 = new PdfPCell(new Paragraph(""));
	  	        cell7_12.setBorderColor(BaseColor.BLACK);
	  	        cell7_12.setHorizontalAlignment(Element.ALIGN_CENTER);
	  	        cell7_12.setVerticalAlignment(Element.ALIGN_MIDDLE);
	  	        table.addCell(cell7_12);
			}
	      
	        
	        
	        
	            int ooo=5%2;
		       System.out.println("ooo:"+ooo);
		       
		       
		      
		        System.out.println("list.size():"+list.size());
		        for(int i=0;i<list.size();i++){
		        	System.out.println("1111111:");
		        	PdfPTable table_i = new PdfPTable(8); // 3 columns.
		        	table_i.setWidthPercentage(45); // Width 100%

			        float[] columnWidths_i = {2f,2f,2f,2f,2f,2f,2f,5f};
			        table_i.setWidths(columnWidths_i);
			        //第一行
			        PdfPCell cell_i_1 = new PdfPCell(new Paragraph("控制室内其他消防系统运行情况",f8));
			        cell_i_1.setColspan(7);
			        cell_i_1.setBorderColor(BaseColor.BLACK);
			        cell_i_1.setHorizontalAlignment(Element.ALIGN_CENTER);
			        cell_i_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			        table_i.addCell(cell_i_1);
			        
			        PdfPCell cell_i_0 = new PdfPCell(new Paragraph("报警，故障部位，原因及处理",f8));
			        cell_i_0.setColspan(1);
			        cell_i_0.setBorderColor(BaseColor.BLACK);
			        cell_i_0.setHorizontalAlignment(Element.ALIGN_CENTER);
			        cell_i_0.setVerticalAlignment(Element.ALIGN_MIDDLE);
			        table_i.addCell(cell_i_0);
			        
			        //第二行
			        PdfPCell cell_i_2 = new PdfPCell(new Paragraph("消防系统相关设备及名称",f8));
			        cell_i_2.setColspan(1);
			        cell_i_2.setBorderColor(BaseColor.BLACK);
			        cell_i_2.setHorizontalAlignment(Element.ALIGN_CENTER);
			        cell_i_2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			        table_i.addCell(cell_i_2);
			        
			        PdfPCell cell_i_3 = new PdfPCell(new Paragraph("控制状态",f8));
			        cell_i_3.setColspan(3);
			        cell_i_3.setBorderColor(BaseColor.BLACK);
			        cell_i_3.setHorizontalAlignment(Element.ALIGN_CENTER);
			        cell_i_3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			        table_i.addCell(cell_i_3);
			        
			        PdfPCell cell_i_4 = new PdfPCell(new Paragraph("运行状态",f8));
			        cell_i_4 .setColspan(3);
			        cell_i_4 .setBorderColor(BaseColor.BLACK);
			        cell_i_4 .setHorizontalAlignment(Element.ALIGN_CENTER);
			        cell_i_4 .setVerticalAlignment(Element.ALIGN_MIDDLE);
			        table_i.addCell(cell_i_4 );
			        
			        PdfPCell cell_i_8 = new PdfPCell(new Paragraph(""+list.get(i).getTreatment_situation(),f8));
			        cell_i_8 .setColspan(3);
			        cell_i_8.setRowspan(3);
			        cell_i_8 .setBorderColor(BaseColor.BLACK);
			        cell_i_8 .setHorizontalAlignment(Element.ALIGN_CENTER);
			        cell_i_8 .setVerticalAlignment(Element.ALIGN_MIDDLE);
			        table_i.addCell(cell_i_8 );
			        
			 
			      
			        //第三行
			        
			        PdfPCell cell_i_5 = new PdfPCell(new Paragraph(""+list.get(i).getDevice_name(),f8));
			        cell_i_5.setRowspan(2);
			        cell_i_5 .setBorderColor(BaseColor.BLACK);
			        cell_i_5 .setHorizontalAlignment(Element.ALIGN_CENTER);
			        cell_i_5 .setVerticalAlignment(Element.ALIGN_MIDDLE);
			        table_i.addCell(cell_i_5 );
			        
			        
			        PdfPCell cell_i_6 = new PdfPCell(new Paragraph("自动",f8));
			        cell_i_6.setBorderColor(BaseColor.BLACK);
			        cell_i_6.setHorizontalAlignment(Element.ALIGN_CENTER);
			        cell_i_6.setVerticalAlignment(Element.ALIGN_MIDDLE);
			        table_i.addCell(cell_i_6);


			        PdfPCell cell_i_7 = new PdfPCell(new Paragraph("手动",f8));
			        cell_i_7.setColspan(2);
			        cell_i_7.setBorderColor(BaseColor.BLACK);
			        cell_i_7.setHorizontalAlignment(Element.ALIGN_CENTER);
			        cell_i_7.setVerticalAlignment(Element.ALIGN_MIDDLE);
			        table_i.addCell(cell_i_7);
			        
			        PdfPCell cell_i_6_1 = new PdfPCell(new Paragraph("正常",f8));
			        cell_i_6_1.setBorderColor(BaseColor.BLACK);
			        cell_i_6_1.setHorizontalAlignment(Element.ALIGN_CENTER);
			        cell_i_6_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			        table_i.addCell(cell_i_6_1);


			        PdfPCell cell_i_7_1 = new PdfPCell(new Paragraph("故障",f8));
			        cell_i_7_1.setColspan(2);
			        cell_i_7_1.setBorderColor(BaseColor.BLACK);
			        cell_i_7_1.setHorizontalAlignment(Element.ALIGN_CENTER);
			        cell_i_7_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			        table_i.addCell(cell_i_7_1);
			        
			        //第四行
			        if("true".equals(list.get(i).getAutomatic())){
			        	 PdfPCell cell_i_6_2 = new PdfPCell(new Paragraph("√",f8));
					        cell_i_6_2.setBorderColor(BaseColor.BLACK);
					        cell_i_6_2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        cell_i_6_2.setVerticalAlignment(Element.ALIGN_MIDDLE);
					        table_i.addCell(cell_i_6_2);
			        }else {
			        	   PdfPCell cell_i_6_2 = new PdfPCell(new Paragraph("",f8));
					        cell_i_6_2.setBorderColor(BaseColor.BLACK);
					        cell_i_6_2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        cell_i_6_2.setVerticalAlignment(Element.ALIGN_MIDDLE);
					        table_i.addCell(cell_i_6_2);
					}
			     

                    if("true".equals(list.get(i).getManual())){
                    	 PdfPCell cell_i_7_2 = new PdfPCell(new Paragraph("√",f8));
     			        cell_i_7_2.setColspan(2);
     			        cell_i_7_2.setBorderColor(BaseColor.BLACK);
     			        cell_i_7_2.setHorizontalAlignment(Element.ALIGN_CENTER);
     			        cell_i_7_2.setVerticalAlignment(Element.ALIGN_MIDDLE);
     			        table_i.addCell(cell_i_7_2);
                    }else {
                    	 PdfPCell cell_i_7_2 = new PdfPCell(new Paragraph("",f8));
      			        cell_i_7_2.setColspan(2);
      			        cell_i_7_2.setBorderColor(BaseColor.BLACK);
      			        cell_i_7_2.setHorizontalAlignment(Element.ALIGN_CENTER);
      			        cell_i_7_2.setVerticalAlignment(Element.ALIGN_MIDDLE);
      			        table_i.addCell(cell_i_7_2);
					}
			       
			        if("true".equals(list.get(i).getSystem_normal())){
			        	 PdfPCell cell_i_8_3 = new PdfPCell(new Paragraph("√",f8));
					        cell_i_8_3.setBorderColor(BaseColor.BLACK);
					        cell_i_8_3.setHorizontalAlignment(Element.ALIGN_CENTER);
					        cell_i_8_3.setVerticalAlignment(Element.ALIGN_MIDDLE);
					        table_i.addCell(cell_i_8_3);
			        }else {
			        	PdfPCell cell_i_8_3 = new PdfPCell(new Paragraph("",f8));
				        cell_i_8_3.setBorderColor(BaseColor.BLACK);
				        cell_i_8_3.setHorizontalAlignment(Element.ALIGN_CENTER);
				        cell_i_8_3.setVerticalAlignment(Element.ALIGN_MIDDLE);
				        table_i.addCell(cell_i_8_3);
					}
			        

                    if("true".equals(list.get(i).getFault())){
                    	 PdfPCell cell_i_9_4 = new PdfPCell(new Paragraph("√",f8));
     			        cell_i_9_4.setColspan(2);
     			        cell_i_9_4.setBorderColor(BaseColor.BLACK);
     			        cell_i_9_4.setHorizontalAlignment(Element.ALIGN_CENTER);
     			        cell_i_9_4.setVerticalAlignment(Element.ALIGN_MIDDLE);
     			        table_i.addCell(cell_i_9_4);
                    }else {
                    	 PdfPCell cell_i_9_4 = new PdfPCell(new Paragraph("",f8));
     			        cell_i_9_4.setColspan(2);
     			        cell_i_9_4.setBorderColor(BaseColor.BLACK);
     			        cell_i_9_4.setHorizontalAlignment(Element.ALIGN_CENTER);
     			        cell_i_9_4.setVerticalAlignment(Element.ALIGN_MIDDLE);
     			        table_i.addCell(cell_i_9_4);
					}
			       
			        
			        if(list.size()%2>0){
			        	if(i==list.size()-1){                                                                                                                                    
			        		PdfPCell cell_spl = new PdfPCell(table_i);
					        cell_spl.setColspan(8);
					        cell_spl.setBorderColor(BaseColor.BLACK);
					        cell_spl.setHorizontalAlignment(Element.ALIGN_CENTER);
					        cell_spl.setVerticalAlignment(Element.ALIGN_MIDDLE);
					        table.addCell(cell_spl);
					        
					        PdfPCell cell_null = new PdfPCell();
					        cell_null.setColspan(6);
					        cell_null.setBorderColor(BaseColor.BLACK);
					        cell_null.setHorizontalAlignment(Element.ALIGN_CENTER);
					        cell_null.setVerticalAlignment(Element.ALIGN_MIDDLE);
					        table.addCell(cell_null);
			        	}else{
			        		if(i%2>0){
			        			PdfPCell cell_spl = new PdfPCell(table_i);
						        cell_spl.setColspan(6);
						        cell_spl.setBorderColor(BaseColor.BLACK);
						        cell_spl.setHorizontalAlignment(Element.ALIGN_CENTER);
						        cell_spl.setVerticalAlignment(Element.ALIGN_MIDDLE);
						        table.addCell(cell_spl);
			        		}else{
			        			
						        PdfPCell cell_spl = new PdfPCell(table_i);
						        cell_spl.setColspan(8);
						        cell_spl.setBorderColor(BaseColor.BLACK);
						        cell_spl.setHorizontalAlignment(Element.ALIGN_CENTER);
						        cell_spl.setVerticalAlignment(Element.ALIGN_MIDDLE);
						        table.addCell(cell_spl);
		        		}
		        	}
		        }else{
		        	if(i%2>0){
	        			PdfPCell cell_spl = new PdfPCell(table_i);
				        cell_spl.setColspan(6);
				        cell_spl.setBorderColor(BaseColor.BLACK);
				        cell_spl.setHorizontalAlignment(Element.ALIGN_CENTER);
				        cell_spl.setVerticalAlignment(Element.ALIGN_MIDDLE);
				        table.addCell(cell_spl);
	        		}else{
					        PdfPCell cell_spl = new PdfPCell(table_i);
					        cell_spl.setColspan(8);
					        cell_spl.setBorderColor(BaseColor.BLACK);
					        cell_spl.setHorizontalAlignment(Element.ALIGN_CENTER);
					        cell_spl.setVerticalAlignment(Element.ALIGN_MIDDLE);
					        table.addCell(cell_spl);
	        		}
	        	}
		        
//		        document.add(table_i);
	        }
	        
	        document.add(table);
	        
	        
	        

	        document.close();
	        writer.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return uploadPath+"AddTableExample.pdf";
		
	}

	
}
