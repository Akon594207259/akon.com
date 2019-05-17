package cn.yunrui.intfirectrlsys.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;
import cn.yunrui.intfirectrlsys.domain.CodeEntity;
import cn.yunrui.intfirectrlsys.domain.CodeEntitywj;
import cn.yunrui.intfirectrlsys.service.SmokeMapService;
import cn.yunrui.intfirectrlsys.service.impl.DlygServiceImpl;
import cn.yunrui.intfirectrlsys.util.Util;

/**
 * 独立烟感
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/smokeMap1")
public class SmokeMapComtrol1 {
	

	@Resource(name = "smokeMapService")
	private SmokeMapService SmokeMap;
	
	/*@RequestMapping(value = "/init.htm", method = RequestMethod.GET)
	public String init(HttpServletRequest request, HttpServletResponse response) {
		return "ifcs/SmokeInduction";
	}*/
	
	
	//////////////////////////////////////////////////////
	@Resource(name="ifcsdlygservice") 
	private  DlygServiceImpl service;
	
	

	//////////////////////////////////
	//进入独立烟感地图jsp
	@RequestMapping(value = "/init.htm", method = RequestMethod.GET)
	
	public String smokemapinit(Model model) {
		
		Authentication auth = RequestContextSecurity.getAuthentication();
        ArrayList<CodeEntitywj> xfyhlist =service.getXfyhList2(auth.getBureauNo(),auth.getOrgNo());		
        String rst = JSONArray.fromObject(xfyhlist).toString();  
        //model.addAttribute("xfyhlist", rst);
       
        String bureauNo = auth.getBureauNo();
        String orgNo = auth.getOrgNo();
        ArrayList<CodeEntity> xfyhlist1 =(ArrayList<CodeEntity>) SmokeMap.getBureauNo(bureauNo,orgNo);	
        String rst1 = JSONArray.fromObject(xfyhlist1).toString();
        //烟感联网用户列表
        
        model.addAttribute("xfyhlist1", rst1);
        //烟感数量
        model.addAttribute("ygsl", xfyhlist1.size());
		String buro = auth.getBureauNo();
	//	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		
		List yh =SmokeMap.querySmokeMap1(buro);
		System.out.println("打印打印打印打印打印打印打印打印打印打印打印打印打印打印打印打印");
		System.out.println("buro"+buro);
		System.out.println("++++++++++++++xfyhlist====++++++++++++"+yh+"++++++++++++++++++++++++++++++++++++++++++");
		
		model.addAttribute("lwyh", yh.size());
		String yh1 = JSONArray.fromObject(yh).toString();
		model.addAttribute("xfyhlist", yh1);

		System.out.println("-------------------------xfyhlist111====-------"+yh1+"------------------------------------------------");
		
		return "ifcs/SmokeInduction";
    }
	
	

	//告警详情
	@RequestMapping(value = "/maptodqhzgjcx.htm", method = RequestMethod.GET)
	public String maptodqhzgjcx(Model model,HttpServletRequest request) {
		//String viewmode = request.getParameter("viewmode");	
		String kssj = Util.getminusDate(-7);
		String jssj = Util.getDate();
		String sscq = request.getParameter("sscq");
		model.addAttribute("viewmode", "T");
		model.addAttribute("daytype", "month");
		model.addAttribute("kssj", kssj);
		model.addAttribute("sscq", sscq);
		model.addAttribute("jssj", jssj);
		return "ifcs/dqhzgjcx";
    }
	
	//消防用户列表
	@RequestMapping(value = "/dqhzmaporig.htm", method = RequestMethod.GET)
	public String dqhzmaporig(Model model) {
		
		
		Authentication auth = RequestContextSecurity.getAuthentication();
        ArrayList<CodeEntitywj> xfyhlist =service.getXfyhList2(auth.getBureauNo(),auth.getOrgNo());		
        String rst = JSONArray.fromObject(xfyhlist).toString();  
        //model.addAttribute("xfyhlist", rst);
        
        String bureauNo = auth.getBureauNo();
        String orgNo = auth.getOrgNo();
        ArrayList<CodeEntity> xfyhlist1 =(ArrayList<CodeEntity>) SmokeMap.getBureauNo(bureauNo,orgNo);	
        String rst1 = JSONArray.fromObject(xfyhlist1).toString();
        //烟感联网用户列表
        model.addAttribute("xfyhlist1", rst1);
        
        //烟感数量
        model.addAttribute("ygsl", xfyhlist1.size());
        String buro = auth.getBureauNo();
        List yh =SmokeMap.querySmokeMap1(buro);
		System.out.println("-------------------------xfyhlist====-------"+yh+"------------------------------------------------");
		model.addAttribute("lwyh", yh.size());
		/*model.addAttribute("xfyhlist", yh);*/
		
		String yh1 = JSONArray.fromObject(yh).toString();
		
		model.addAttribute("xfyhlist", yh1);

		System.out.println("-------------------------xfyhlist1111====-------"+yh1+"------------------------------------------------");
        
		return "ifcs/map";
    }
	
	
	
	
	/**
	 * 查询消防用户
	 * @return
	 */
	@RequestMapping(value="/querySmokeMap.htm",produces = "application/json; charset=utf-8")
	public @ResponseBody String querySmokeMap(){
		Authentication auth = RequestContextSecurity.getAuthentication();
		String buro = auth.getBureauNo();
		String org = auth.getOrgNo();
		return SmokeMap.querySmokeMap(buro, org).toString();
	}
	
	/**
	 * 查询独立烟感
	 * @return
	 */
	@RequestMapping(value="/querySmoke.htm",produces = "application/json; charset=utf-8")
	public @ResponseBody String querySmoke(){
		Authentication auth = RequestContextSecurity.getAuthentication();
		String buro = auth.getBureauNo();
		return SmokeMap.querySmoke(buro).toString();
	}
	
	//获取独立烟感用户列表 可查用户数量
	@RequestMapping(value = "/getBureauNo.htm")
	public void getBureauNo(HttpServletRequest request, HttpServletResponse response){	
		    Authentication auth = RequestContextSecurity.getAuthentication();
		    String bureauNo = auth.getBureauNo();
		    String orgNo = auth.getOrgNo();
            ArrayList<CodeEntity> xfyhlist =(ArrayList<CodeEntity>) SmokeMap.getBureauNo(bureauNo,orgNo);	
           
            
  	        String rst = JSONArray.fromObject(xfyhlist).toString();                     
			try {
				response.setContentType("application/json; charset=utf-8");
			
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
	
	//获取独立烟感
	@RequestMapping(value = "/getDutyOnDutyAtion.htm")
	public void getDutyOnDutyAtion(HttpServletRequest request, HttpServletResponse response){	
		  String deviceId = request.getParameter("re_xfyh_id");
            ArrayList xfyhlist = (ArrayList) SmokeMap.getDutyOnDutyAtion(deviceId);		
  	        String rst = JSONArray.fromObject(xfyhlist).toString(); 
  	        
			try {
				response.setContentType("application/json; charset=utf-8");
			
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
	
	@RequestMapping(value = "/getDutyOnDutyAtion1.htm")
	public void getDutyOnDutyAtion1(HttpServletRequest request, HttpServletResponse response){	
            ArrayList xfyhlist = (ArrayList) SmokeMap.getDutyOnDutyAtion1();		
  	        String rst = JSONArray.fromObject(xfyhlist).toString();                     
			try {
				response.setContentType("application/json; charset=utf-8");
			
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
	
}
