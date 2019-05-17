package cn.yunrui.intfirectrlsys.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.yunrui.intfirectrlsys.common.CommonMethods;
import cn.yunrui.intfirectrlsys.domain.DataMaintainanceVo;
import cn.yunrui.intfirectrlsys.domain.Header;
import cn.yunrui.intfirectrlsys.domain.ResultMessage;
import cn.yunrui.intfirectrlsys.domain.ifcs_fp_p_personnel;
import cn.yunrui.intfirectrlsys.service.DataMaintainanceService;
import cn.yunrui.intfirectrlsys.util.ExcelUtil;

@Controller
@RequestMapping("/maintainance")
public class DataMaintainanceControl {
	@Resource(name="dataMaintainanceservice") 
	private  DataMaintainanceService   dataMaintainanceService ;
	private static boolean iflocal = true;
	public DataMaintainanceService getDataMaintainanceService() {
		return dataMaintainanceService;
	}
	public void setDataMaintainanceService(DataMaintainanceService dataMaintainanceService) {
		this.dataMaintainanceService = dataMaintainanceService;
	}
	@RequestMapping(value = "/init.htm",method = RequestMethod.GET)
	public String init(Model model){	
	    return "/ifcs/pxxxwh";
	}
	
	//查询巡视任务单
	 @RequestMapping(value={"/queryMissionStatement.htm"})
	  public void queryMissionStatement(HttpServletRequest request, HttpServletResponse response)
	  {
	   String   qb_type=request.getParameter("qb_type");
	   String  tm_name=request.getParameter("tm_name");
	   String  qb_category=request.getParameter("qb_category");
	    
	    int pageSize = Integer.parseInt(request.getParameter("pageSize"));
	    int current = Integer.parseInt(request.getParameter("current"));
	    int start = (current - 1) * pageSize;
	    int limit = pageSize;
	    List  MissionStatementVolist =dataMaintainanceService.queryMissionStatementInfo(qb_type,tm_name, qb_category, start, limit);
	    String rst = JSONArray.fromObject(MissionStatementVolist).toString();

	    int total = dataMaintainanceService.queryMissionStatementInfoTotal(qb_type,tm_name, qb_category);
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
	 
	 //新增单向选择题

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
					String   qb_subject=request.getParameter("qb_subject");
					String   qb_id=CommonMethods.getUUID();
					String a_correct = request.getParameter("a_correct");
					String b_correct = request.getParameter("b_correct");
					String  c_correct=request.getParameter("c_correct");
					String   d_correct=request.getParameter("d_correct");
					String   o_correct=request.getParameter("o_correct");
					String   qb_category=request.getParameter("qb_category");
					//封装对象
					DataMaintainanceVo  vo  =new DataMaintainanceVo();
					vo.setA_correct(a_correct);
					vo.setB_correct(b_correct);
					vo.setC_correct(c_correct);
					vo.setD_correct(d_correct);
				   vo.setO_correct(o_correct);
				   vo.setQb_id(qb_id);
				   vo.setQb_subject(qb_subject);
				   vo.setQb_type("single");
				   vo.setQb_category(qb_category);
				
					ResultMessage<?> rm = new ResultMessage<Object>();
					rm = dataMaintainanceService.addTaskSetting(vo);
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
				
				//新增判断题
				@RequestMapping(value = "/addPanduan.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
				public void addPanduan(HttpServletRequest request,HttpServletResponse response){
					try {
						request.setCharacterEncoding("utf-8");
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
					response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
					//获取参数
					String   qb_subject=request.getParameter("qb_subject");
					String   qb_id=CommonMethods.getUUID();
					String   o_correct=request.getParameter("o_correct");
					String   qb_category=request.getParameter("qb_category");
					//封装对象
					DataMaintainanceVo  vo  =new DataMaintainanceVo();
				   vo.setO_correct(o_correct);
				   vo.setQb_id(qb_id);
				   vo.setQb_subject(qb_subject);
				   vo.setQb_type("judge");
				   vo.setQb_category(qb_category);
				   
					vo.setA_correct("");
					vo.setB_correct("");
					vo.setC_correct("");
					vo.setD_correct("");
				
				
					ResultMessage<?> rm = new ResultMessage<Object>();
					rm = dataMaintainanceService.addTaskPanduan(vo);
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
				
				//新增多选题
				@RequestMapping(value = "/addDuoXuan.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
				public void addDuoXuan(HttpServletRequest request,HttpServletResponse response){
					try {
						request.setCharacterEncoding("utf-8");
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
					response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
					//获取参数
					String   qb_subject=request.getParameter("qb_subject");
					String   qb_id=CommonMethods.getUUID();
					String a_correct = request.getParameter("a_correct");
					String b_correct = request.getParameter("b_correct");
					String  c_correct=request.getParameter("c_correct");
					String   d_correct=request.getParameter("d_correct");
					String   o_correct=request.getParameter("o_correct");
					String   qb_category=request.getParameter("qb_category");
					//封装对象
				
					String[]  o_correct1;
					List pamList = new ArrayList();
                if (null != o_correct && !"".equals(o_correct)&&!"undefined".equals(o_correct)) {
                	   o_correct1 = o_correct.split(",");
						for (int i = 0; i < o_correct1.length; i = i + 1) {
							 if(null !=o_correct1[i]&&!"".equals(o_correct1[i])){
								 DataMaintainanceVo  vo  =new DataMaintainanceVo();
								 //tsPersonnelVo.setPer_id(per_name1[i]);
								    vo.setO_correct(o_correct1[i]);
								    vo.setQb_id(qb_id);
								    pamList.add(vo);
								  
							 }
								
							}
						}
        
            	DataMaintainanceVo  vo  =new DataMaintainanceVo();
            	    vo.setA_correct(a_correct);
				    vo.setB_correct(b_correct);
				    vo.setC_correct(c_correct);
				    vo.setD_correct(d_correct);
				    vo.setQb_id(qb_id);
				    vo.setQb_subject(qb_subject);
				    vo.setQb_type("much");
				    vo.setQb_category(qb_category);
					
					
					
				
					ResultMessage<?> rm = new ResultMessage<Object>();
					rm = dataMaintainanceService.addTaskDuo(vo,pamList);
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
				
				
				
				
				//编辑前根据id查询
				   //根据对象id查询对象
				   @RequestMapping(value={"/queryCommunityInfo.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
					  public void queryMaintainanceInfo(HttpServletRequest request, HttpServletResponse response)
					  {
					   String  qb_id=request.getParameter("qb_id");
					
					  
					   List<DataMaintainanceVo> cm =dataMaintainanceService.queryCommunity(qb_id);
					  
					    String rst = JSONArray.fromObject(cm).toString();
					    
					   
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
				
				
			
				//修改单选题
				/**
				 * 编辑对象
				 * @param request
				 * @param response
				 * @return
				 */
				@RequestMapping(value = "/editCommunity.htm",produces = "application/json; charset=utf-8",method = RequestMethod.POST)
				@ResponseBody
				public void editCommunity(HttpServletRequest request, HttpServletResponse response){	
					try {
						request.setCharacterEncoding("utf-8");
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
					response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
					ResultMessage<String> rMessage = new ResultMessage<String>();//返回信息
					DataMaintainanceVo  vo  =new DataMaintainanceVo();
					String  qb_id=request.getParameter("qb_id");
					String   qb_subject=request.getParameter("qb_subject");
					String a_correct = request.getParameter("a_correct");
					String b_correct = request.getParameter("b_correct");
					String  c_correct=request.getParameter("c_correct");
					String   d_correct=request.getParameter("d_correct");
					String   o_correct=request.getParameter("o_correct");
					String   qb_category=request.getParameter("qb_category");
					
					vo.setA_correct(a_correct);
					vo.setB_correct(b_correct);
					vo.setC_correct(c_correct);
					vo.setD_correct(d_correct);
				   vo.setO_correct(o_correct);
				   vo.setQb_subject(qb_subject);
				   vo.setQb_type("single");
				   vo.setQb_category(qb_category);
					rMessage = dataMaintainanceService.editCommunityInfo(qb_id,vo);
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
				
				//修改判断题
				/**
				 * 编辑对象
				 * @param request
				 * @param response
				 * @return
				 */
				@RequestMapping(value = "/editPanDuan.htm",produces = "application/json; charset=utf-8",method = RequestMethod.POST)
				@ResponseBody
				public void editPanDuan(HttpServletRequest request, HttpServletResponse response){	
					try {
						request.setCharacterEncoding("utf-8");
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
					response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
					ResultMessage<String> rMessage = new ResultMessage<String>();//返回信息
					DataMaintainanceVo  vo  =new DataMaintainanceVo();
					String  qb_id=request.getParameter("qb_id");
					String   qb_subject=request.getParameter("qb_subject");
					String a_correct = request.getParameter("a_correct");
					String b_correct = request.getParameter("b_correct");
					String  c_correct=request.getParameter("c_correct");
					String   d_correct=request.getParameter("d_correct");
					String   o_correct=request.getParameter("o_correct");
					String   qb_category=request.getParameter("qb_category");
					
					vo.setA_correct("");
					vo.setB_correct("");
					vo.setC_correct("");
					vo.setD_correct("");
				   vo.setO_correct(o_correct);
				   vo.setQb_subject(qb_subject);
				   vo.setQb_type("judge");
				   vo.setQb_category(qb_category);
					rMessage = dataMaintainanceService.editPan(qb_id,vo);
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
				
				
				//修改多选题
				/**
				 * 编辑对象
				 * @param request
				 * @param response
				 * @return
				 */
				@RequestMapping(value = "/editDuoXuan.htm",produces = "application/json; charset=utf-8",method = RequestMethod.POST)
				@ResponseBody
				public void editDuoXuan(HttpServletRequest request, HttpServletResponse response){	
					try {
						request.setCharacterEncoding("utf-8");
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
					response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
					ResultMessage<String> rMessage = new ResultMessage<String>();//返回信息
					DataMaintainanceVo  vo  =new DataMaintainanceVo();
					String  qb_id=request.getParameter("qb_id");
					String   qb_subject=request.getParameter("qb_subject");
					String a_correct = request.getParameter("a_correct");
					String b_correct = request.getParameter("b_correct");
					String  c_correct=request.getParameter("c_correct");
					String   d_correct=request.getParameter("d_correct");
					String   o_correct=request.getParameter("o_correct");
					String   qb_category=request.getParameter("qb_category");
					
					String[]  o_correct1;
					List pamList = new ArrayList();
                if (null != o_correct && !"".equals(o_correct)&&!"undefined".equals(o_correct)) {
                	   o_correct1 = o_correct.split(",");
						for (int i = 0; i < o_correct1.length; i = i + 1) {
							 if(null !=o_correct1[i]&&!"".equals(o_correct1[i])){
								 DataMaintainanceVo  vo1  =new DataMaintainanceVo();
								 //tsPersonnelVo.setPer_id(per_name1[i]);
								    vo1.setO_correct(o_correct1[i]);
								    vo1.setQb_id(qb_id);
								    pamList.add(vo1);
								  
							 }
								
							}
						}
					
					vo.setA_correct(a_correct);
					vo.setB_correct(b_correct);
					vo.setC_correct(c_correct);
					vo.setD_correct(d_correct);
				   vo.setQb_subject(qb_subject);
				   vo.setQb_type("much");
				   vo.setQb_category(qb_category);
					rMessage = dataMaintainanceService.editDuoXuanInfo(qb_id,vo,pamList);
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
	
				
				//单选导出
				//获取导出文件头
				   public List<Header> getYhZhmxHeader(){  
					List<Header> header = new ArrayList<Header>();
					String names[] = new String[]{"编号","题目","类型","选项","正确答案"};
					String index[] = new String[]{"id","qb_subject","qb_type","hebing","o_correct"};
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
					   String  tm_name=request.getParameter("tm_name");
					   String  qb_category=request.getParameter("qb_category");
					   String  qb_type=request.getParameter("qb_type");
						  //jttype
						    String filename = "单选导出";
							
							List<Header> header = getYhZhmxHeader();
							List list = dataMaintainanceService.cpxxexport(tm_name,qb_category,qb_type);
							try {
								ExcelUtil.toExcel(filename, header,list, false, response);
							} catch (IOException e) {
								e.printStackTrace();
							}				
					}
				   
				   
					//导出操作
				   @RequestMapping(value = "/yhzhmxexport2.htm", method = RequestMethod.GET)
					public void yhzhmxexport2(HttpServletRequest request, HttpServletResponse response){				
					   String  tm_name=request.getParameter("tm_name");
					   String  qb_category=request.getParameter("qb_category");
					   String  qb_type=request.getParameter("qb_type");
						  //jttype
						    String filename = "多选导出";
							
							List<Header> header = getYhZhmxHeader();
							List list = dataMaintainanceService.cpxxexport2(tm_name,qb_category,qb_type);
							try {
								ExcelUtil.toExcel(filename, header,list, false, response);
							} catch (IOException e) {
								e.printStackTrace();
							}				
					}
				   
				   
				   
					//导出操作
				   @RequestMapping(value = "/yhzhmxexport3.htm", method = RequestMethod.GET)
					public void yhzhmxexport3(HttpServletRequest request, HttpServletResponse response){				
					   String  tm_name=request.getParameter("tm_name");
					   String  qb_category=request.getParameter("qb_category");
					   String  qb_type=request.getParameter("qb_type");
						  //jttype
						    String filename = "判断题导出";
							
							List<Header> header = getYhZhmxHeader();
							List list = dataMaintainanceService.cpxxexport3(tm_name,qb_category,qb_type);
							try {
								ExcelUtil.toExcel(filename, header,list, false, response);
							} catch (IOException e) {
								e.printStackTrace();
							}				
					}
				
				   
				   //删除操作
				   //删除临时巡查表
				   @RequestMapping(value={"/deleteDuty.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
				   public void deleteDuty(HttpServletRequest request, HttpServletResponse response)
				   {
						String  qb_id=request.getParameter("qb_id");
				     ResultMessage rMessage = dataMaintainanceService.deleteData(qb_id);
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
						String  qb_id=request.getParameter("qb_id");
				     ResultMessage rMessage = dataMaintainanceService.deleteDaAn(qb_id);
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
				
	
}
