/**
 * 
 */
package cn.yunrui.intfirectrlsys.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;
import cn.yunrui.intfirectrlsys.domain.CodeEntity;
import cn.yunrui.intfirectrlsys.domain.OnDuty;
import cn.yunrui.intfirectrlsys.domain.StandAlone;
import cn.yunrui.intfirectrlsys.entity.ShiftInformation;
import cn.yunrui.intfirectrlsys.entity.SystemOperation;
import cn.yunrui.intfirectrlsys.service.LogOnDutyService;
import cn.yunrui.intfirectrlsys.service.StandAloneService;
import cn.yunrui.intfirectrlsys.util.Util;


/**
 * @author 杨德志
 *
 * @date 2018-8-6 
 */

@Controller
@RequestMapping("/standAlone")
public class StandAloneControl {

	@Resource(name="standAloneService") 
	private  StandAloneService standAlone;
	
	private static boolean iflocal = true;
	
	
	@RequestMapping(value = "/init.htm", method = RequestMethod.GET)
	public String init(Model model) {
		return "/ifcs/djlt";
	}
	/**
	 * 获取消防用户
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getxfyhstandAlone.htm")
	public void getXfyhListCommon(HttpServletRequest request, HttpServletResponse response){	
		    Authentication auth = RequestContextSecurity.getAuthentication();		
		    
			ArrayList<CodeEntity> xfyhlist = (ArrayList<CodeEntity>) standAlone
					.getXfyhListCommon(auth.getBureauNo(), auth.getOrgNo());
	        String rst = JSONArray.fromObject(xfyhlist).toString();  
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		//http://localhost:3000\http://localhost:8888
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	
	}
	
	
	/**
	 * 查询传输装置
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getdjltstandAlone.htm")
	public void getdjltListCommon(HttpServletRequest request, HttpServletResponse response){	
		    Authentication auth = RequestContextSecurity.getAuthentication();		
		    String id = request.getParameter("id");
			ArrayList<CodeEntity> xfyhlist = (ArrayList<CodeEntity>) standAlone
					.getdjltListCommon(auth.getBureauNo(), id);
	        String rst = JSONArray.fromObject(xfyhlist).toString();  
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		//http://localhost:3000\http://localhost:8888
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	
	}
	
	/**
	 * 查询巡查结果
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getDjltData.htm")
	public void getDjltData(HttpServletRequest request, HttpServletResponse response){			
		    
			StandAlone sa = new StandAlone();
			sa.setWldw_id(request.getParameter("wldw_id"));
	        sa.setCszz_id(request.getParameter("devId"));
            String type = request.getParameter("type");
		    
			List djlt =  standAlone.getDjltData(sa,type);
	        String rst = JSONArray.fromObject(djlt).toString();  
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		//http://localhost:3000\http://localhost:8888
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	
	}
	
	/**
	 * 执行巡查与查岗命令
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value = "/getStandAlone.htm")
	public void getStandAlone(HttpServletRequest request, HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("utf-8");
		String type = request.getParameter("type");
		String devId = request.getParameter("devId");
		short timeout = 10;
		long commandId =0;	
	
		//new FcrmWebClient("192.168.1.13:2181");//初始化客户端工具
		
		   if(null !=type && "1".equals(type)){
				// 读用户信息传输装置运行状态，返回命令ID。通过命令ID到数据库表cisp_sys.zzcz_zcjg查询返回结果（轮询，60秒没有结果超时返回）
				 //commandId = FcrmWebClient.readTerminalStatus(devId); // 参数为蓝天盒子设备ID
		   }

		   if(null !=type && "2".equals(type)){
				// 查岗命令，返回命令ID。通过命令ID到数据库表cisp_sys.zzcz_zcjg查询返回结果（轮询，60秒没有结果超时返回）
				 //commandId = FcrmWebClient.checkStatus(devId,timeout); // 参数为蓝天盒子设备ID和查岗超时时间
		   }
	        String rst = JSONArray.fromObject(commandId).toString();  
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		//http://localhost:3000\http://localhost:8888
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	
	}
	
	/**
	 * 执行巡查与查岗命令
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value = "/getFcrmWebClient.htm")
	public void getFcrmWebClient(HttpServletRequest request, HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("utf-8");
		String type = request.getParameter("type");
		String devId = request.getParameter("devId");
		String commandId = request.getParameter("commandId");
		StandAlone sa = new StandAlone();
		sa.setWldw_id(request.getParameter("wldw_id"));
	    sa.setWldw_name(request.getParameter("wldw_name"));
        sa.setCszz_id(devId);
        sa.setCszz_name(request.getParameter("cszz_name"));
        sa.setOperation_command(request.getParameter("operation_command"));
		short timeout = 10;
		   
		   boolean num = false;
		num =  standAlone.getFcrmWebClient(commandId,sa,type); 
	        String rst = JSONArray.fromObject(num).toString();  
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		//http://localhost:3000\http://localhost:8888
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	
	}
	
}
