package cn.yunrui.intfirectrlsys.action;
import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;
import cn.yunrui.intfirectrlsys.common.CommonMethods;
import cn.yunrui.intfirectrlsys.domain.ContentFix;
import cn.yunrui.intfirectrlsys.domain.MissionStatement;
import cn.yunrui.intfirectrlsys.domain.XunChaVo;
import cn.yunrui.intfirectrlsys.domain.ifcs_fp_p_personnel;
import cn.yunrui.intfirectrlsys.domain.ifcs_fp_patrol;
import cn.yunrui.intfirectrlsys.domain.ifcs_fp_patrol_content;
import cn.yunrui.intfirectrlsys.domain.CodeEntity;
import cn.yunrui.intfirectrlsys.domain.Header;
import cn.yunrui.intfirectrlsys.domain.ResultMessage;
import cn.yunrui.intfirectrlsys.domain.TaskSetting;
import cn.yunrui.intfirectrlsys.entity.AttachmentBean;
import cn.yunrui.intfirectrlsys.service.FirePatrolService;
import cn.yunrui.intfirectrlsys.util.ExcelUtil;
import cn.yunrui.intfirectrlsys.util.Util;

@Controller
@RequestMapping("/firepatrol")
public class FirePatrolControl {
	@Resource(name="firePatrolservice") 
	private  FirePatrolService   firePatrolService ;
	public FirePatrolService getFirePatrolService() {
		return firePatrolService;
	}
	public void setFirePatrolService(FirePatrolService firePatrolService) {
		this.firePatrolService = firePatrolService;
	}
	private static boolean iflocal = false;
	
	@RequestMapping(value = "/init.htm",method = RequestMethod.GET)
	public String init(Model model){	
	    return "/ifcs/xjgl";
	}
	
	
	//获取消防用户信息
		@RequestMapping(value = "/getXfyhListCommon.htm",method = RequestMethod.POST)
		public void getXfyhListCommon(HttpServletRequest request, HttpServletResponse response){	
			    Authentication auth = RequestContextSecurity.getAuthentication();		   		    
	            ArrayList<CodeEntity> xfyhlist =firePatrolService.getXfyhListCommon(auth.getBureauNo(),auth.getOrgNo());		
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
		
		
		//查询巡视任务单
		 @RequestMapping(value={"/queryMissionStatement.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
		  public void queryMissionStatement(HttpServletRequest request, HttpServletResponse response)
		  {
		    String companyid = request.getParameter("ts_xfyh_name");
		    String  p_property = request.getParameter("p_property");
		    String p_work_state= request.getParameter("p_work_state");
		    String kaishi=request.getParameter("kaishi");
		    String jiesu =request.getParameter("jiesu");
		    
		    
		    String per_name= request.getParameter("per_name");
		    String[] per_name1={};
		    if( per_name!=null){
		    	per_name1=per_name.split(",");
		    }
		  
		   
			System.out.println("姓名"+per_name1);

		    int pageSize = Integer.parseInt(request.getParameter("pageSize"));
		    int current = Integer.parseInt(request.getParameter("current"));
		    int start = (current - 1) * pageSize;
		    int limit = pageSize;
		    List  MissionStatementVolist =firePatrolService.queryMissionStatementInfo(companyid,p_property,p_work_state, per_name1, start, limit,kaishi,jiesu);
		    String rst = JSONArray.fromObject(MissionStatementVolist).toString();

		    int total = firePatrolService.queryMissionStatementInfoTotal(companyid,p_property,p_work_state, per_name1,kaishi,jiesu);
		    String rststr = "{\"data\": " + rst + ", \"current\": " + current + ", \"total\": " + total + "}";
		    try {
		      response.setContentType("application/json; charset=utf-8");
		      if (iflocal) {
		        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
		        response.setHeader("Access-Control-Allow-Credentials", "true");
		      }
		      response.getWriter().write(rststr);
		      response.getWriter().flush();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		  }
		
	
		 //导出
		 
		//获取导出文件头
		   public List<Header> getYhZhmxHeader(){  
			List<Header> header = new ArrayList<Header>();
			String names[] = new String[]{"编号","任务单编号","消防用户","巡查性质","巡查日期","巡查人","确定人","状态"};
			String index[] = new String[]{"id","p_number","ts_xfyh_name","p_property","fp_time","per_name","p_confirm","p_work_state"};
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
			   String companyid = request.getParameter("companyid");
			    String  p_property = request.getParameter("p_property");
			    String p_work_state= request.getParameter("p_work_state");
			    String kaishi=request.getParameter("kaishi");
			    String jiesu =request.getParameter("jiesu");
			    String per_name= request.getParameter("per_name");
			    String[] per_name1={};
			    if( per_name!=null){
			    	per_name1=per_name.split(",");
			    }
			    
				  //jttype
				    String filename = "巡查任务单";
					
					List<Header> header = getYhZhmxHeader();
					List list = firePatrolService.cpxxexport(companyid,p_property,p_work_state, per_name1,kaishi,jiesu);
					try {
						ExcelUtil.toExcel(filename, header,list, false, response);
					} catch (IOException e) {
						e.printStackTrace();
					}				
			}
		 
	
		   //前台点击浏览
		   @RequestMapping(value={"/queryStatement.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
			  public void queryStatement(HttpServletRequest request, HttpServletResponse response)
			  {
			    String p_id = request.getParameter("p_id");
			  
			    List  MissionStatementlist =firePatrolService.MissionStatementInfo(p_id);
			  
			    String rst = JSONArray.fromObject(MissionStatementlist).toString();
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
		   
		   

		   
		   
		   //点击浏览的下面表格
		   
		   //前台点击浏览
		   @RequestMapping(value={"/queryXiao.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
			  public void queryXiao(HttpServletRequest request, HttpServletResponse response)
			  {
			   
			   String p_id = request.getParameter("p_id");
			 
			  
			    List  ContentFixlist =firePatrolService.ContentFixInfo(p_id);
			  
			    String rst = JSONArray.fromObject(ContentFixlist).toString();
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
		   
		   //查询小类具体信息
		   @RequestMapping(value={"/queryLei.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
			  public void queryLei(HttpServletRequest request, HttpServletResponse response)
			  {
			   String p_id = request.getParameter("p_id");
			  
			    List  Contenlist =firePatrolService.ContentFixdata(p_id);
			  
			    String rst = JSONArray.fromObject(Contenlist).toString();
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
		   
		   
		   //删除临时巡查表
		   @RequestMapping(value={"/deleteDuty.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
		   public void deleteDuty(HttpServletRequest request, HttpServletResponse response)
		   {
		     String deletedId = request.getParameter("p_id");
		     ResultMessage rMessage = firePatrolService.deleteDuty(deletedId);
		     System.out.println(rMessage);
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
		   
		   
 
		   //删除人员子表
		   @RequestMapping(value={"/deletePersonnel.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
		   public void deletePersonnel(HttpServletRequest request, HttpServletResponse response)
		   {
		     String deletedId = request.getParameter("p_id");
		     ResultMessage rMessage = firePatrolService.deleteDutyPersonnel(deletedId);
		     System.out.println(rMessage);
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
		   
		   
		   
		   
		   //删除巡查项
		   @RequestMapping(value={"/deleteCount.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
		   public void deleteCount(HttpServletRequest request, HttpServletResponse response)
		   {
		     String deletedId = request.getParameter("p_id");
		     ResultMessage rMessage = firePatrolService.deletecount(deletedId);
		     System.out.println(rMessage);
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
		   
		   
		   
		   //新增临时工单
	
			@RequestMapping(value = "/addMissionStatement.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
			public void addMissionStatement(HttpServletRequest request,HttpServletResponse response){
				try {
					request.setCharacterEncoding("utf-8");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
				response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
				//获取参数
				Date d = new Date();  
		        System.out.println(d);  
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");  
		        String dateNowStr = sdf.format(d);  
		        
				String   per_name=request.getParameter("per_name");
				String   per_id=CommonMethods.getUUID();
				String   p_id=CommonMethods.getUUID();
				String p_number = CommonMethods.getUUID();
				p_number="ITS-"+dateNowStr+"-"+p_number.substring(p_number.length()-4);
				String ts_xfyh_name = request.getParameter("xfyhName");  
				String fp_time = request.getParameter("fp_time");
				System.out.println(fp_time);
				String ts_description = request.getParameter("ts_description");
				String   p_property=request.getParameter("p_property");
				String   xfyhID=request.getParameter("xfyhID");
				//封装对象
				TaskSetting ts = new TaskSetting();
				
				ts.setP_id(p_id);
				ts.setP_xfyh_id(xfyhID);
				ts.setP_number(p_number);
				ts.setP_property(p_property);
				ts.setTs_xfyh_name(ts_xfyh_name);
				ts.setFp_time(fp_time);
				ts.setTs_description(ts_description);
				
				ts.setPer_id(per_id);
				ts.setPer_name(per_name);
				ts.setPer_p_id(p_id);
				ResultMessage<?> rm = new ResultMessage<Object>();
				rm = firePatrolService.addTaskSetting(ts);
				System.out.println("------------"+rm.toJson());	
			
				try {
					response.setContentType("application/json; charset=utf-8");
					if(iflocal){				  
					  response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
					  response.setHeader("Access-Control-Allow-Credentials", "true");
					}
					response.getWriter().write(rm.toJson());
					response.getWriter().flush();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
			
			
		   
			
			
	
			 //巡查日志pdf
			 @RequestMapping(value = "/xuncexport.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
			 public @ResponseBody String xuncexport(HttpServletRequest request,HttpServletResponse response) {
				String pdf = null;
				File pdfFile = null;
				InputStream inputStream = null;
				OutputStream outputStream = null;
				FileInputStream onputStream = null;

				try {
					Authentication auth = RequestContextSecurity.getAuthentication();

					pdf = this.writeElecOptimizeReport(request);
					System.out.println("pdf:"+pdf);
					
					if ( pdf != null) {
						pdfFile = new File(pdf);
					}
					if (pdfFile != null && pdfFile.exists()) {
						inputStream = new FileInputStream(pdfFile);
					}
					
					
					
					 BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(getUploadPath(request) + "shuiyin.pdf")));
				      Calendar cal = Calendar.getInstance();
				      setWatermark(bos, pdf,  16);
					
				      

					String fileName = "每日巡检报告.pdf";
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
		
			 
			 
			 
			 
			 public static void setWatermark(BufferedOutputStream bos, String input, int permission)
					    throws DocumentException, IOException
					  {
					    PdfReader reader = new PdfReader(input);

					    PdfStamper stamper = new PdfStamper(reader, bos);

					    int total = reader.getNumberOfPages() + 1;
					    BaseFont base = BaseFont.createFont("STSongStd-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
					    PdfGState gs = new PdfGState();
					    for (int i = 1; i < total; i++) {
					      PdfContentByte content = stamper.getOverContent(i);
					      gs.setFillOpacity(0.1F);
					      content.setGState(gs);
					      content.beginText();

					      content.setFontAndSize(base, 50.0F);
					      content.setTextMatrix(70.0F, 200.0F);
					      content.showTextAligned(1, "公司内部文件，请注意保密！", 300.0F, 350.0F, 55.0F);

					      content.setFontAndSize(base, 20.0F);
					      content.endText();
					    }

					    stamper.close();
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
					  String p_id = request.getParameter("p_id");
					  List <MissionStatement>  MissionStatementlist = new ArrayList<MissionStatement>();
					    MissionStatementlist =firePatrolService.MissionStatementInfo(p_id);

					   List<ContentFix>  ContentFixlist =firePatrolService.ContentFixInfo(p_id);
					  
					   List<ContentFix>  Contenlist =firePatrolService.ContentFixdata(p_id);
		
			            List<XunChaVo> dd=new ArrayList<XunChaVo>();
			            System.out.println(Contenlist.size());
					   for(int i =0;i<Contenlist.size() ;i++){
						     XunChaVo  vo =new XunChaVo();
						      vo.setCf_name(Contenlist.get(i).getCf_name());
						      vo.setCf_id(Contenlist.get(i).getCf_id());
						      
						      dd.add(vo);
						      
						      List<ContentFix>  list=new ArrayList<ContentFix>();
						     for(int b =0;b<ContentFixlist.size();b++){
							   
							   if(null !=  ContentFixlist.get(b).getCf_father_id()  &&   !"".equals(Contenlist.get(i).getCf_father_id())){
								   if(ContentFixlist.get(b).getCf_father_id().equals(Contenlist.get(i).getCf_id())){
									
									   //大声到群，唐牛，dd
									   list.add(ContentFixlist.get(b)); 
									   //大师傅和发送到
									   vo.setList(list);
									   
									   
								   }
							   }
							 
		
						   }
						 
						   
					   }
					   

						
				    try {
				    
				    	BaseFont bfChinese = BaseFont.createFont("STSongStd-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
				    	Font titleFont = new Font(bfChinese, 18, Font.NORMAL, BaseColor.BLACK);
						Font f8 = new Font(bfChinese, 8, Font.NORMAL, BaseColor.BLACK);
						Font f12 = new Font(bfChinese, 12, Font.NORMAL, BaseColor.BLACK);
						uploadPath = getUploadPath(request);// 文件上传路径
				    	
				    	 PdfWriter writer = PdfWriter.getInstance(document,
				                    new FileOutputStream(uploadPath+"AddTableExample.pdf"));
				    	 
				    	    Rectangle rect = new Rectangle(36, 54, 559, 788);
				            rect.setBorderColor(BaseColor.BLACK);
				            writer.setBoxSize("art", rect);
				            HeaderFooter header=new HeaderFooter();

				            writer.setPageEvent(header);

				            document.open();

				        PdfPTable table = new PdfPTable(7); // 3 columns.
				        table.setWidthPercentage(100); // Width 100%
				        table.setSpacingBefore(10f); // Space before table
				        table.setSpacingAfter(10f); // Space after table

				        // Set Column widths
				        float[] columnWidths = {2f,4f,2f,2f,4f,2f,2f};
				        table.setWidths(columnWidths);
				        
				          BaseFont baseFont = BaseFont.createFont("STSongStd-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);  
						   Font fontbt = new Font(baseFont);    
				           fontbt.setSize(20);
				           Font fontzw = new Font(baseFont); 
				           fontzw.setSize(13);
				         Paragraph t = new Paragraph("巡查报告",fontbt);
				         t.setAlignment(1);
				         t.setAlignment(Element.ALIGN_CENTER);
				         document.add(t);
				        
			          //第一行
				        PdfPCell cell1 = new PdfPCell(new Paragraph("单位名称:",f8));
				        cell1.setColspan(1);
				        cell1.setBorderColor(BaseColor.BLACK);
				        cell1.setPaddingLeft(10);
				        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				        table.addCell(cell1);
				        
				        
				        PdfPCell cell2 = new PdfPCell(new Paragraph(" "+MissionStatementlist.get(0).getTs_xfyh_name(),f8));
				        cell2.setColspan(1);
				        cell2.setBorderColor(BaseColor.BLACK);
				        cell2.setPaddingLeft(10);
				        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
				        table.addCell(cell2);
				        
				        PdfPCell cell3 = new PdfPCell(new Paragraph("巡查人员:",f8));
				        cell3.setColspan(2);
				     
				        cell3.setBorderColor(BaseColor.BLACK);
				        cell3.setPaddingLeft(10);
				        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				        table.addCell(cell3);
				        
				        PdfPCell cell4 = new PdfPCell(new Paragraph(" "+MissionStatementlist.get(0).getPer_name(),f8));
				        cell4.setColspan(1);
				        cell4.setBorderColor(BaseColor.BLACK);
				        cell4.setPaddingLeft(10);
				        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				        table.addCell(cell4);
				        
				        PdfPCell cell5 = new PdfPCell(new Paragraph("巡查日期：",f8));
				        cell5.setColspan(1);
				        cell5.setBorderColor(BaseColor.BLACK);
				        cell5.setPaddingLeft(10);
				        cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				        cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
				        table.addCell(cell5);
				        
				        PdfPCell cell6 = new PdfPCell(new Paragraph(" "+MissionStatementlist.get(0).getP_time(),f8));
				        cell6.setColspan(1);
				        cell6.setBorderColor(BaseColor.BLACK);
				        cell6.setPaddingLeft(10);
				        cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				        cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
				        table.addCell(cell6);
				        
				        
				        
				        //第二行
				        PdfPCell cell7 = new PdfPCell(new Paragraph(" 巡查项目",f8));
				        cell7.setColspan(1);
				        cell7.setBorderColor(BaseColor.BLACK);
				        cell7.setPaddingLeft(10);
				        cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				        cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
				        table.addCell(cell7);
				        
				        PdfPCell cell8 = new PdfPCell(new Paragraph("巡查内容 ",f8));
				        cell8.setColspan(1);
				        cell8.setBorderColor(BaseColor.BLACK);
				        cell8.setPaddingLeft(10);
				        cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				        cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
				        table.addCell(cell8);
				        
				        PdfPCell cell9= new PdfPCell(new Paragraph(" 符合",f8));
				        cell9.setColspan(1);
				        cell9.setBorderColor(BaseColor.BLACK);
				        cell9.setPaddingLeft(10);
				        cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				        cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
				        table.addCell(cell9);
				        
				        PdfPCell cell10 = new PdfPCell(new Paragraph(" 不符合",f8));
				        cell10.setColspan(1);
				        cell10.setBorderColor(BaseColor.BLACK);
				        cell10.setPaddingLeft(10);
				        cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				        cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
				        table.addCell(cell10);
				        
				        PdfPCell cell12 = new PdfPCell(new Paragraph(" 具体问题处理",f8));
				        cell12.setColspan(3);
				        cell12.setBorderColor(BaseColor.BLACK);
				        cell12.setPaddingLeft(10);
				        cell12.setHorizontalAlignment(Element.ALIGN_CENTER);
				        cell12.setVerticalAlignment(Element.ALIGN_MIDDLE);
				        table.addCell(cell12);
				        
				      
				        //第三行
				          for(int i=0;i<dd.size();i++){
				        	     if(dd.get(i).getList().size()>1){
							        	PdfPCell cell13 = new PdfPCell(new Paragraph(" "+dd.get(i).getCf_name(),f8));
								        cell13.setRowspan(dd.get(i).getList().size());
								        cell13.setColspan(1);
								        cell13.setBorderColor(BaseColor.BLACK);
								        cell13.setPaddingLeft(10);
								        cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
								        cell13.setVerticalAlignment(Element.ALIGN_MIDDLE);
								        table.addCell(cell13);
							        }else {
							        	PdfPCell cell13 = new PdfPCell(new Paragraph(" "+dd.get(i).getCf_name(),f8));
								        cell13.setColspan(1);
								        cell13.setBorderColor(BaseColor.BLACK);
								        cell13.setPaddingLeft(10);
								        cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
								        cell13.setVerticalAlignment(Element.ALIGN_MIDDLE);
								        table.addCell(cell13);
									}
				        	for(int j=0;j<dd.get(i).getList().size();j++){
		                       
					        PdfPCell cell14 = new PdfPCell(new Paragraph(" "+dd.get(i).getList().get(j).getCf_name(),f8));
					      
					        cell14.setColspan(1);
					        cell14.setBorderColor(BaseColor.BLACK);
					        cell14.setPaddingLeft(10);
					        cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
					        cell14.setVerticalAlignment(Element.ALIGN_MIDDLE);
					        table.addCell(cell14);
					        
					        if(dd.get(i).getList().get(j).getPc_state().equals("0")){
					        	 PdfPCell cell15 = new PdfPCell(new Paragraph(" ",f8));
		                            cell15.setColspan(1);
		                            cell15.setBorderColor(BaseColor.BLACK);
		                            cell15.setPaddingLeft(10);
		                            cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
		                            cell15.setVerticalAlignment(Element.ALIGN_MIDDLE);
		                            table.addCell(cell15);
		                            
		                            PdfPCell cell16 = new PdfPCell(new Paragraph(" √",f8));
		                            cell16.setColspan(1);
		                            cell16.setBorderColor(BaseColor.BLACK);
		                            cell16.setPaddingLeft(10);
							        cell16.setHorizontalAlignment(Element.ALIGN_CENTER);
							        cell16.setVerticalAlignment(Element.ALIGN_MIDDLE);
							        table.addCell(cell16);
		                            
					        }else {
					        	 PdfPCell cell15 = new PdfPCell(new Paragraph("√ ",f8));
		                            cell15.setColspan(1);
		                            cell15.setBorderColor(BaseColor.BLACK);
		                            cell15.setPaddingLeft(10);
		                            cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
		                            cell15.setVerticalAlignment(Element.ALIGN_MIDDLE);
		                            table.addCell(cell15);
		                            
		                            PdfPCell cell16 = new PdfPCell(new Paragraph(" ",f8));
		                            cell16.setColspan(1);
		                            cell16.setBorderColor(BaseColor.BLACK);
		                            cell16.setPaddingLeft(10);
							        cell16.setHorizontalAlignment(Element.ALIGN_CENTER);
							        cell16.setVerticalAlignment(Element.ALIGN_MIDDLE);
							        table.addCell(cell16);
								
							}
					        if(dd.get(i).getList().get(j).getPc_state().equals("1")){
					        	   PdfPCell cell17 = new PdfPCell(new Paragraph( ""    ,f8));
		                           cell17.setColspan(3);
		                           cell17.setBorderColor(BaseColor.BLACK);
		                           cell17.setPaddingLeft(10);
		                           cell17.setHorizontalAlignment(Element.ALIGN_CENTER);
		                           cell17.setVerticalAlignment(Element.ALIGN_MIDDLE);
				                     table.addCell(cell17);
					        }else {
					        	PdfPCell cell17 = new PdfPCell(new Paragraph( " 不符合原因:"  +"      "                           +dd.get(i).getList().get(j).getPc_description() +"   "                            + "处理情况说明:"  +"    "                        +dd.get(i).getList().get(j) .getPc_situation()         ,f8));
		                           cell17.setColspan(3);
		                           cell17.setBorderColor(BaseColor.BLACK);
		                           cell17.setPaddingLeft(10);
		                           cell17.setHorizontalAlignment(Element.ALIGN_CENTER);
		                           cell17.setVerticalAlignment(Element.ALIGN_MIDDLE);
				                     table.addCell(cell17);
							}
                                
                           
				        	}
				        	
				        }
				        
				        
				        
//				        for(int n=0;n<dd.size();n++){
//				        
//				        	PdfPCell cell13 = new PdfPCell(new Paragraph(" ",f8));
//					        cell13.setRowspan(dd.size());
//					        cell13.setColspan(3);
//					        cell13.setBorderColor(BaseColor.BLUE);
//					        cell13.setPaddingLeft(10);
//					        cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        cell13.setVerticalAlignment(Element.ALIGN_MIDDLE);
//					        table.addCell(cell13);
//					        
//					        
//					        PdfPCell cell14 = new PdfPCell(new Paragraph(" ",f8));
//					        
//					        cell14.setColspan(1);
//					        cell14.setBorderColor(BaseColor.BLUE);
//					        cell14.setPaddingLeft(10);
//					        cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        cell14.setVerticalAlignment(Element.ALIGN_MIDDLE);
//					        table.addCell(cell14);
//					        
//                            PdfPCell cell15 = new PdfPCell(new Paragraph(" ",f8));
//					        
//                            cell15.setColspan(1);
//                            cell15.setBorderColor(BaseColor.BLUE);
//                            cell15.setPaddingLeft(10);
//                            cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
//                            cell15.setVerticalAlignment(Element.ALIGN_MIDDLE);
//                            table.addCell(cell15);
//					        
//					        
//                            PdfPCell cell16 = new PdfPCell(new Paragraph(" ",f8));
//					        
//                            cell16.setColspan(1);
//                            cell16.setBorderColor(BaseColor.BLUE);
//                            cell16.setPaddingLeft(10);
//					        cell16.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        cell16.setVerticalAlignment(Element.ALIGN_MIDDLE);
//					        table.addCell(cell16);
//					        
//					        
//					        
//                             PdfPCell cell17 = new PdfPCell(new Paragraph(" ",f8));
//					        
//                             cell17.setColspan(4);
//                             cell17.setBorderColor(BaseColor.BLUE);
//                             cell17.setPaddingLeft(10);
//                             cell17.setHorizontalAlignment(Element.ALIGN_CENTER);
//                             cell17.setVerticalAlignment(Element.ALIGN_MIDDLE);
//		                     table.addCell(cell17);
					        
				       // }
				        
				        document.add(table);
				        document.close();
				        writer.close();
				    } catch (Exception e) {
				        e.printStackTrace();
				    }
				    return uploadPath+"AddTableExample.pdf";
					
				}
				
				
				
				//查看附件
				
				@RequestMapping(value={"queryPictureInfo.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
				  public void queryPictureInfo(HttpServletRequest request, HttpServletResponse response)
				    throws Exception
				  {
				    HashMap resultMap = new HashMap();
				    List resultList = new ArrayList();
				    resultMap.put("total", Integer.valueOf(0));
				    resultMap.put("results", resultList);
				    String yw_uuid = request.getParameter("p_id");
				    String[] yw_uuid1={};
				    if( yw_uuid!=null){
				    	yw_uuid1=yw_uuid.split(",");
				    }
				  
				    if (!"".equals("p_id") && (!"".equals("attachType")))
				    {
				      List list = firePatrolService.queryPictureList(yw_uuid1);
				      for (int i = 0; i < list.size(); i++)
				      {
				        Map dataMap = new HashMap();
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
				
				
				
		   //新增临时工单巡查项
				
				@RequestMapping(value={"/queryInspectionItem.htm"})
				  public void queryInspectionItem(HttpServletRequest request, HttpServletResponse response)
				  {
				   String cf_id =request.getParameter("cf_id");
				  
				    Map  ContenMap =firePatrolService.queryInspectionItemInfo(cf_id);
				  
				    String rst = JSONArray.fromObject(ContenMap).toString();
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
				
				
				
				
				
				// wj 根树
				@RequestMapping(value = { "/getUserRootFilter.htm" }, produces = { "text/plain;charset=UTF-8" })
				@ResponseBody
				public void getUserRootFilter(String factory_id, HttpServletResponse response) {
					Authentication auth = RequestContextSecurity.getAuthentication();
					HashMap<String, Object> resultMap = new HashMap<String, Object>();
					JsonConfig jsonConfig = new JsonConfig();
					resultMap.put("id", auth.getOrgNo());
					resultMap.put("name", auth.getOrgName());
					resultMap.put("type", "org");
					resultMap.put("isParent", Boolean.valueOf(true));
					 String rst = JSONArray.fromObject(resultMap, jsonConfig).toString();
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
				
				// 人员树
				@RequestMapping(value={"/getUserData.htm"}, produces={"text/plain;charset=UTF-8"})
				  @ResponseBody
				  public String getUserData(String id, String object_name, String except_orgType)
				  {
				    List userList = firePatrolService.getUserTreeData(id, object_name, except_orgType);
				    StringBuffer buf = new StringBuffer();
				    buf.append("[");
				    for (int i = 0; i < userList.size(); i++) {
				      if (((String)((HashMap)userList.get(i)).get("type")).equals("user")) {
				        buf.append(",{\"id\":\"").append((String)((HashMap)userList.get(i)).get("id")).append("\",\"name\":\"").append((String)((HashMap)userList.get(i)).get("name")).append("\",\"type\":\"").append((String)((HashMap)userList.get(i)).get("type")).append("\",\"user_mobile\":\"").append((String)((HashMap)userList.get(i)).get("user_mobile")).append("\",\"iconSkin\":\"user\",\"isParent\":false").append("}");
				      }
				      else if (((String)((HashMap)userList.get(i)).get("type")).equals("dept")) {
				        buf.append(",{\"id\":\"").append((String)((HashMap)userList.get(i)).get("id")).append("\",\"name\":\"").append((String)((HashMap)userList.get(i)).get("name")).append("\",\"type\":\"").append((String)((HashMap)userList.get(i)).get("type")).append("\",\"user_mobile\":\"").append((String)((HashMap)userList.get(i)).get("user_mobile")).append("\",\"iconSkin\":\"dept\",\"isParent\":true").append("}");
				      }
				      else
				      {
				        buf.append(",{\"id\":\"").append((String)((HashMap)userList.get(i)).get("id")).append("\",\"name\":\"").append((String)((HashMap)userList.get(i)).get("name")).append("\",\"type\":\"").append((String)((HashMap)userList.get(i)).get("type")).append("\",\"user_mobile\":\"").append((String)((HashMap)userList.get(i)).get("user_mobile")).append("\",\"iconSkin\":\"org\",\"isParent\":true").append("}");
				      }

				    }

				    buf.append("]");
				    return buf.toString().replaceFirst(",", ""); } 	
				
				
				/**
				 * wj 新增任务单
				 * 
				 * @param request
				 * @param response
				 * @throws UnsupportedEncodingException
				 */
				@RequestMapping(value = "/addTaskInfo.htm", method = RequestMethod.POST)
				public void addTaskInfo(HttpServletRequest request,
						HttpServletResponse response) throws UnsupportedEncodingException {
					// 乱码问题解决方案
					request.setCharacterEncoding("utf-8");// 必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。
					response.setContentType("text/html;charset=utf-8");// 设置传过去的页面显示的编码
					// 获取参数
					String query_outfire_user = request.getParameter("query_outfire_user");

					
					Date d = new Date();  
			        System.out.println(d);  
			        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");  
			        String dateNowStr = sdf.format(d);  
					// 获取参数
					/* String editId = request.getParameter("editId"); */
					// 从前端页面获取 数据 ("xxxx")
					String p_xfyh_id = request.getParameter("xfyhID");
					String p_xfyh_name = request.getParameter("xfyhName");
					String p_description = request.getParameter("ts_description");
					String p_time = request.getParameter("fp_time");					
					String data_num = request.getParameter("bbb");
					String per_name = request.getParameter("per_name");
					String p_number = CommonMethods.getUUID();
	
					p_number="ITS-"+dateNowStr+"-"+p_number.substring(p_number.length()-4);
									
					String[] data_num1;		
					// 封装对象
					ifcs_fp_patrol taskSetting = new ifcs_fp_patrol();
					// 生成 任务单的UUID 去掉 —
					String p_id = UUID.randomUUID().toString().replace("-", "");
                     System.out.println("p_id"+p_id);
					List pamList = new ArrayList();
					List xcList = new ArrayList();					
					if (null != data_num && !"".equals(data_num)) {
					
						data_num1 = data_num.split(",");
						for (int i = 0; i < data_num1.length; i++) {
							String  pc_id=UUID.randomUUID().toString().replace("-", "");
			                  if(null !=data_num1[i]&&!"".equals(data_num1[i])){
			                	  ifcs_fp_patrol_content tsInsContentVo = new ifcs_fp_patrol_content();
			        	          tsInsContentVo.setPc_id(pc_id);
			                	  tsInsContentVo.setPc_p_id(p_id);
			                	  tsInsContentVo.setPc_content_id(data_num1[i]);			  			         
			        				xcList.add(tsInsContentVo);
			                  }
						
						}
					}					
									
					String[] per_name1;
					if (null != per_name && !"".equals(per_name)&&!"undefined".equals(per_name)) {
						
						per_name1 = per_name.split(",");
						for (int i = 0; i < per_name1.length; i = i + 2) {
							String  pc_id=UUID.randomUUID().toString().replace("-", "");
							 if(null !=per_name1[i]&&!"".equals(per_name1[i])){
								 ifcs_fp_p_personnel  tsPersonnelVo = new ifcs_fp_p_personnel();
								
								 tsPersonnelVo.setPer_p_id(p_id);
								 tsPersonnelVo.setPer_id(per_name1[i]);
								 tsPersonnelVo.setPer_name(per_name1[i+1]);
								 pamList.add(tsPersonnelVo);
							
									
							 }else {
								
							
							}
						}
					}

					
					taskSetting.setP_id(p_id);
					taskSetting.setP_number(p_number);
					taskSetting.setP_property("0");
					taskSetting.setP_work_state("0");
					taskSetting.setP_time(p_time);
					taskSetting.setP_xfyh_id(p_xfyh_id);
					taskSetting.setP_xfyh_name(p_xfyh_name);
					taskSetting.setP_description(p_description);
					

					 this.firePatrolService.addTaskSetting(
							pamList, xcList, taskSetting);
					try {
						response.setContentType("application/json; charset=utf-8");
						  if (iflocal) {
						        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
						        response.setHeader("Access-Control-Allow-Credentials", "true");
						  }
						response.getWriter().flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
}
