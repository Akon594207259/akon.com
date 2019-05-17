package cn.yunrui.intfirectrlsys.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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

import com.i380v.openservices.client.codec.JacksonSupport;

import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;
import cn.yunrui.intfirectrlsys.domain.ResultMessage1;
import cn.yunrui.intfirectrlsys.examentity.QuestionMap;
import cn.yunrui.intfirectrlsys.service.ExamService;
import cn.yunrui.intfirectrlsys.util.Util;

@Controller
@RequestMapping(value="/exam")
public class ExamControl {
	
	private static boolean iflocal = Util.ifbd;
	
	@Resource(name="examservice")
	private ExamService service;
	
	@RequestMapping("/init.htm")
	public String init(Model model){
		return "/ifcs/examIndex";
	}
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
		        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
		        response.setHeader("Access-Control-Allow-Credentials", "true");
		      }
		      response.getWriter().write(rst);
		      response.getWriter().flush();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
	}
	@RequestMapping(value="query.htm",method=RequestMethod.POST)
	public void query(HttpServletRequest request,HttpServletResponse response){
		String peopleId=request.getParameter("peopleId");
		String category=request.getParameter("category");
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		int pageSize = Integer.parseInt(request.getParameter("pageSize"));
	    int current = Integer.parseInt(request.getParameter("current"));
	    int start = (current - 1) * pageSize;
	    int limit = pageSize;
	    List<HashMap<String,String>> examList = service.query(peopleId, category,startTime,endTime,start, limit);
	    String rst = JSONArray.fromObject(examList).toString();  
	    int total = service.queryTotal(peopleId,category,startTime,endTime);
	    String rststr = "{\"data\": "+rst+", \"current\": "+current+", \"total\": "+total+"}";
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rststr);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	@RequestMapping(value="/initPaper.htm",method=RequestMethod.POST)
	public void initPaper(HttpServletRequest request,HttpServletResponse response){
		String category=request.getParameter("category");
		QuestionMap qm=service.initPaper(category);
		try{
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){	
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JacksonSupport.toJsonString(qm));
			response.getWriter().flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@RequestMapping(value="/saveScore.htm",method=RequestMethod.POST)
	public void saveScore(HttpServletRequest request,HttpServletResponse response){
		String peopleId=request.getParameter("peopleId");
		String peopleName=request.getParameter("peopleName");
		String score=request.getParameter("score");
		String category=request.getParameter("category");
		ResultMessage1 result=service.saveScore(peopleId,peopleName,score,category);
		try{
			response.setContentType("application/json;charset=utf-8");
			if(iflocal){
				response.setHeader("Access-Control-Allow-Origin","http://localhost:8888");
				response.setHeader("Access-Control-Allow-Credentials","true");
			}
			response.getWriter().write(result.toJson());
			response.getWriter().flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@RequestMapping(value="/delete.htm", method=RequestMethod.POST)
	public void delete(HttpServletRequest request,HttpServletResponse response){
		String id=request.getParameter("id");
		ResultMessage1 rm=service.delete(id);
		try{
			response.setContentType("application/json;charset=utf-8");
			if(iflocal){
				response.setHeader("Access-Control-Allow-Origin","http://localhost:8888");
				response.setHeader("Access-Control-Allow-Credentials","true");
			}
			response.getWriter().write(rm.toJson());
			response.getWriter().flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
