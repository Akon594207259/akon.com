//package cn.yunrui.intfirectrlsys.action;
//
//import java.io.IOException;
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import cn.cc.cisp.security.Authentication;
//import cn.cc.cisp.security.web.context.RequestContextSecurity;
//import cn.yunrui.intfirectrlsys.request.IFCSMonitorRequest;
//import cn.yunrui.intfirectrlsys.request.IFCSUserInfoRequest;
//import cn.yunrui.intfirectrlsys.response.IFCSMonitorResponse;
//import cn.yunrui.intfirectrlsys.response.IFCSUserInfoResponse;
//import cn.yunrui.intfirectrlsys.service.IFCUService;
//
//@Controller
//@RequestMapping("/intfirectrlsys")
//public class IfcsControl {
//
//	@Resource(name = "intfirectrlsysservice")
//	private IFCUService service;
//
//	@RequestMapping(value = "/queryIFCSUserInfo.htm", produces = "application/json; charset=utf-8", method = RequestMethod.GET)
//	@ResponseBody
//	public String queryIFCSUserInfo(HttpServletRequest request,
//			HttpServletResponse response) {
//		String buro = request.getParameter("buro");
//		String org = request.getParameter("org");
//		String type = request.getParameter("type");
//		String dayType = request.getParameter("dayType");
//		String dqhzmore = request.getParameter("dqhzmore");
//		String sun = request.getParameter("sun");
//		Authentication auth = RequestContextSecurity.getAuthentication();
//		String buro1 = auth.getBureauNo();
//		String org1 = auth.getOrgNo();
//
//		IFCSUserInfoResponse rsp = new IFCSUserInfoResponse();
//		IFCSUserInfoRequest req = new IFCSUserInfoRequest();
//		IFCSUserInfoRequest req1 = new IFCSUserInfoRequest();
//		req.setBuro(buro);
//		req.setOrg(org);
//		req.setType(type);
//		req.setDayType(dayType);
//		req.setDqhzmore(dqhzmore);
//
//        if(null==sun || "".equals(sun)){
//    		req1.setBuro(buro1);
//    		req1.setOrg(org1);
//        }else{
//        	req1.setBuro(buro);
//    		req1.setOrg(org);
//        }
//
//		req1.setType(type);
//		req1.setDayType(dayType);
//		req1.setDqhzmore(dqhzmore);
//		if ("yhxx".equalsIgnoreCase(type)) {// 用户信息T
//			rsp = service.queryIFCSUserInfo(req);
//		} else if ("tqxx".equalsIgnoreCase(type)) {// 天气信息T
//			rsp = service.queryIFCSWeatherInfo(req);
//		} else if ("flgj".equalsIgnoreCase(type)) {// 分类告警
//			rsp = service.queryIFCSFlgjInfo(req1);
//		} else if ("sbwhl".equalsIgnoreCase(type)) {// 设备完好率T
//			rsp = service.queryIFCSXfsbwhlInfo(req);
//		} else if ("sxtsszt".equalsIgnoreCase(type)) {// 水系统实时状态T
//			rsp = service.queryWaterSysInfo(req);
//		} else if ("ywqk".equalsIgnoreCase(type)) {// 运维情况T
//			rsp = service.queryIFCSYwqkInfo(req);
//		} else if ("aqpj".equalsIgnoreCase(type)) {// 安全评价T
//			rsp = service.queryIFCSAqpjInfo(req);
//		} else if ("dqhz".equalsIgnoreCase(type)) {// 电气火灾T
//			rsp = service.queryIFCSDqhzInfo(req);
//		}
//		System.out.println("----aaaa--------" + rsp.toJson());
//		return rsp.toJson();
//	}
//
//	@RequestMapping(value = "/queryIFCSMonitor.htm")
//	public @ResponseBody
//	IFCSMonitorResponse queryIFCSMonitor(IFCSMonitorRequest req) {
//		Authentication auth = RequestContextSecurity.getAuthentication();
//		String buro = auth.getBureauNo();
//		return service.queryIFCSMonitor(req, buro);
//	}
//
//	@RequestMapping(value = "/queryMapUsers.htm", produces = "application/json; charset=utf-8")
//	public @ResponseBody
//	String queryMapUsers() {
//		Authentication auth = RequestContextSecurity.getAuthentication();
//		String buro = auth.getBureauNo();
//		return service.queryMapUsers(buro).toString();
//	}
//
//}
