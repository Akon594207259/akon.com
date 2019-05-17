package cn.yunrui.intfirectrlsys.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import cn.yunrui.intfirectrlsys.domain.CodeEntity;
import cn.yunrui.intfirectrlsys.domain.Community;
import cn.yunrui.intfirectrlsys.domain.ResultMessage;
import cn.yunrui.intfirectrlsys.domain.TaskSetting;
import cn.yunrui.intfirectrlsys.service.MicrofiremanagementService;
import cn.yunrui.intfirectrlsys.util.Util;

@Controller
@RequestMapping("/management")
public class MicrofiremanagementControl {
	@Resource(name="microfiremanagementService") 
	private  MicrofiremanagementService  microfiremanagementService;
	private static boolean iflocal = Util.ifbd;
	
	
	@RequestMapping(value = "/init.htm",method = RequestMethod.GET)
	public String init(Model model){	
	    return "/ifcs/wxwh";
	}
	
	   //新增对象
			@RequestMapping(value = "/addCommunityInfo.htm",produces = "application/json;charset=utf-8",method = RequestMethod.POST)	
			public void addCommunityInfo(HttpServletRequest request,HttpServletResponse response){
				try {
					request.setCharacterEncoding("utf-8");
				} catch (UnsupportedEncodingException e1) {
					
					e1.printStackTrace();
				}//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
				response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
				//获取参数
				String  c_id=UUID.randomUUID().toString().replace("-", "");
				String  name=request.getParameter("name");
				String  address=request.getParameter("address");
				Community  cm =new  Community();
				cm.setAddress(address);
				cm.setC_id(c_id);
				cm.setName(name);
				

				ResultMessage<?> rm = new ResultMessage<Object>();
				rm = microfiremanagementService.addCommunity(cm);
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
			
		
			    //根据对象id查询对象
			   @RequestMapping(value={"/queryStatement.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
				  public void queryStatement(HttpServletRequest request, HttpServletResponse response)
				  {
				    String c_id = request.getParameter("c_id");
				    int pageSize = Integer.parseInt(request.getParameter("pageSize"));
				    int current = Integer.parseInt(request.getParameter("current"));
				    int start = (current - 1) * pageSize;
				    int limit = pageSize;
				  
				    List  MissionStatementlist =microfiremanagementService.queryCommunity(c_id,start,limit);
				  
				    String rst = JSONArray.fromObject(MissionStatementlist).toString();
				    int total = microfiremanagementService.queryCommunityTotal(c_id);
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
			   
			   //根据ID查询对象
			   //根据对象id查询对象
			   @RequestMapping(value={"/queryCommunityInfo.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
				  public void queryCommunityInfo(HttpServletRequest request, HttpServletResponse response)
				  {
				    String c_id = request.getParameter("c_id");
				
				  
				   List<Community> cm =microfiremanagementService.queryCommunity(c_id);
				  
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
			   
			   
			   /* 通过 消防用户获取到该用户下的消防室信息
				 */
				@RequestMapping(value = "/getControlAtion.htm")
				public void getControlAtion(HttpServletRequest request,
						HttpServletResponse response) {

					String id = request.getParameter("companyId");
					@SuppressWarnings("unchecked")
					ArrayList<Map> comlist = (ArrayList<Map>)
							microfiremanagementService
							.getCommunity();
					String rst = JSONArray.fromObject(comlist).toString();
					System.out.println("控制层打印"+rst);
					try {
						response.setContentType("application/json; charset=utf-8");
						
						 if(iflocal){ response.setHeader("Access-Control-Allow-Origin",
						  "http://localhost:3000");
						 response.setHeader("Access-Control-Allow-Credentials", "true"); }
						 
						response.getWriter().write(rst);
						response.getWriter().flush();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}
				
				
				//获取下拉框里面的值，取自数据库本身
				/**
				 * 对象初始化
				 */
				@RequestMapping(value="/initObjectList.htm",method=RequestMethod.POST)
				public void initObjectList(HttpServletRequest request,HttpServletResponse response){
					String typeKey = request.getParameter("typeKey");
					List<CodeEntity> objectList = new ArrayList<CodeEntity>();
					
						objectList = microfiremanagementService.initCommunityObject();
				
					String rst = JSONArray.fromObject(objectList).toString();  
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
					Community  cm =new  Community();
					String  c_id=request.getParameter("c_id");
					String  name=request.getParameter("name");
					String  address=request.getParameter("address"); 
					cm.setAddress(address);
					cm.setName(name);
					rMessage = microfiremanagementService.editCommunityInfo(c_id,cm);
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
				
				//删除对象
				
				
				   //删除人员子表
				   @RequestMapping(value={"/deleteCommunity.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
				   public void deleteCommunity(HttpServletRequest request, HttpServletResponse response)
				   {
				     String c_id = request.getParameter("c_id");
				     ResultMessage rMessage = microfiremanagementService.deleteCommunityInfo(c_id);
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
