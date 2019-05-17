//package cn.yunrui.intfirectrlsys.action;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import cn.yunrui.intfirectrlsys.request.IFCSUserInfoRequest;
//import cn.yunrui.intfirectrlsys.response.IFCSUserInfoResponse;
//import cn.yunrui.intfirectrlsys.service.ClassificationAlarmService;
//
//@Service
//@Controller
//@RequestMapping("/ClassificationAlarmAction")
//public class ClassificationAlarmAction {
//	
//	
//	@Resource(name="classificationAlarmService") 
//	private  ClassificationAlarmService service;
//	
//	
//	@RequestMapping(value = "/queryIFCSUserInfo.htm", method = RequestMethod.POST)
//	@ResponseBody
//	public String queryIFCSUserInfo(HttpServletRequest request, HttpServletResponse response){	
//            String buro = request.getParameter("buro");
//            String org = request.getParameter("org");
//            String type = request.getParameter("type");
//            String dayType = request.getParameter("dayType");
//            String dqhzmore =request.getParameter("dqhzmore");
//            
//            IFCSUserInfoResponse rsp = new IFCSUserInfoResponse();
//            IFCSUserInfoRequest req = new IFCSUserInfoRequest();
//            req.setBuro(buro);
//            req.setOrg(org);
//            req.setType(type);
//            req.setDayType(dayType);
//            req.setDqhzmore(dqhzmore);
//	  		if ("flgj".equalsIgnoreCase(type)){//分类告警
//	  			rsp= service.queryIFCSFlgjInfo(req);
//	  		}
//  		    System.out.println("----分类告警--------"+rsp.toJson());		
//  		    return rsp.toJson();	
//	}
//
//}
