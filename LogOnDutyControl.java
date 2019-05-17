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
import cn.yunrui.intfirectrlsys.entity.OnDuty;
import cn.yunrui.intfirectrlsys.entity.ShiftInformation;
import cn.yunrui.intfirectrlsys.entity.SystemOperation;
import cn.yunrui.intfirectrlsys.service.LogOnDutyService;
import cn.yunrui.intfirectrlsys.util.Util;

@Controller
@RequestMapping("/logBook")
public class LogOnDutyControl {

	@Resource(name = "LogOnDutyServiceImpl")
	private LogOnDutyService logOnDutyService;
	
	private static boolean iflocal = Util.ifbd;
	
	@RequestMapping("/init.htm")
	public String init(Model model){
		return "/ifcs/logbook";
	}
	
	
	/**
	 * 获取消防用户
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getXfyhListAtion1.htm",method = RequestMethod.POST)
	public void getXfyhListCommon(HttpServletRequest request, HttpServletResponse response){	
		    Authentication auth = RequestContextSecurity.getAuthentication();		
		    
			ArrayList<CodeEntity> xfyhlist = (ArrayList<CodeEntity>) logOnDutyService
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
	 * 通过 消防用户获取到该用户下的消防室信息
	 */
	@RequestMapping(value = "/getControlRoomAtion1.htm")
	public void getControlRoomAtion(HttpServletRequest request,
			HttpServletResponse response) {

		String id = request.getParameter("id");
		ArrayList<Map> xfyhlist = (ArrayList<Map>) logOnDutyService
				.getControlRoomCommon(id);
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
	
	/**
	 * 通过 消防用户,消防室获取值班班次信息
	 */
	@RequestMapping(value = "/getDutyOnDuty1.htm")
	@SuppressWarnings("unchecked")
	public void getDutyOnDutyAtion(HttpServletRequest request,
			HttpServletResponse response) {
		
		String xksid = request.getParameter("xksid");
		
		ArrayList<Map> xfyhlist = (ArrayList<Map>) logOnDutyService
				.getDutyOnDutyAtion(xksid);
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
	
	/**
	 * 获取当前人员信息
	 * 
	 */
	@RequestMapping(value = "/getPersonnel.htm")
	public void getPersonnelAction(HttpServletRequest request,
			HttpServletResponse response){
		
		String xksid = request.getParameter("xksid");
		String shift_id = request.getParameter("shift_id");
		String current_date = request.getParameter("current_date");
		
		ArrayList<Map> xfyhlist = (ArrayList<Map>) logOnDutyService
				.getPersonnel(xksid,shift_id,current_date);
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
	
	/**
	 * 获取交班人员信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getShiftStaff.htm")
	public void getyShiftStaffAction(HttpServletRequest request,
			HttpServletResponse response){
		
		String current_date = request.getParameter("current_date");
		String shift_id = request.getParameter("shift_id");
		String xksid = request.getParameter("xksid");
		String start = request.getParameter("start");

		ArrayList<Map> xfyhlist = (ArrayList<Map>) logOnDutyService
				.geyShiftStaff(current_date,shift_id,xksid,start);
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
	
	/**
	 * 获取接班人员信息
	 * 
	 */
	@RequestMapping(value = "/getySuccessor.htm")
	public void getySuccessorAction(HttpServletRequest request,
			HttpServletResponse response){
		
		String current_date = request.getParameter("current_date");
		String shift_id = request.getParameter("shift_id");
		String xksid = request.getParameter("xksid");
		String end = request.getParameter("end");
		
		ArrayList<Map> xfyhlist = (ArrayList<Map>) logOnDutyService
				.getySuccessor(current_date,shift_id,xksid,end);
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
	
	
	/**
	 * 新增交接班信息
	 * @throws UnsupportedEncodingException 
	 * 
	 */
	@RequestMapping(value = "/addInformation.htm")
	public void addInformationAction(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		request.setCharacterEncoding("utf-8");// 必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。
	
		
		String shiftContent = request.getParameter("shiftContent");
		String notice = request.getParameter("notice");
		String theSuccessor = request.getParameter("theSuccessor");
		String start_date = request.getParameter("start_date");
		String end_date = request.getParameter("end_date");
		String shiftTime = request.getParameter("shiftTime");
		String bid = request.getParameter("bid");
		String fId = request.getParameter("fId");
		String peopleId = request.getParameter("get_people_id");
		
		
		ShiftInformation sf = new ShiftInformation();
		
		sf.setShiftContent(shiftContent);
		sf.setNotice(notice);
        sf.setTheSuccessor(theSuccessor);
        sf.setStart_date(start_date);
        sf.setEnd_date(end_date);
        sf.setShiftTime(shiftTime);
        sf.setoId(bid);
        sf.setfId(fId);
        sf.setPersonnel(peopleId);
        
        boolean xfyhlist = logOnDutyService
				.addInformation(sf);
        
    	response.setContentType("text/html;charset=utf-8");
		String rst = JSONArray.fromObject(xfyhlist).toString();
		try {
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 新增值班记录头信息
	 */
	@RequestMapping(value = "/addOnDutyAtion.htm")
	public void addOnDutyAtion(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException { // 乱码问题解决方案
		request.setCharacterEncoding("utf-8");// 必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。
		response.setContentType("text/html;charset=utf-8");// 设置传过去的页面显示的编码

		String normal = request.getParameter("normal");
		String fault = request.getParameter("fault");
		String fire_alarm = request.getParameter("fire_alarm");
		String misreport = request.getParameter("misreport");
		String fault_alarm = request.getParameter("fault_alarm");
		String supervision = request.getParameter("supervision");
		String false_positives = request.getParameter("false_positives");
		String treatment_situation = request
				.getParameter("treatment_situation");
		String fail_to_report = request.getParameter("fail_to_report");
		String self_check = request.getParameter("self_check");
		String silencer = request.getParameter("silencer");
		String reset = request.getParameter("reset");
		String main_power_supply = request.getParameter("main_power_supply");
		String spare = request.getParameter("spare");
		String problem_handling = request.getParameter("problem_handling");
		String cId = request.getParameter("cId");
		String bId = request.getParameter("bId");
		String shiftTime = request.getParameter("shiftTime");
		String name = request.getParameter("name");
		String start_date = request.getParameter("start_date");
		String end_date = request.getParameter("end_date");
		


		OnDuty onDuty = new OnDuty();

		onDuty.setNormal(normal);
		onDuty.setFault(fault);
		onDuty.setFire_alarm(fire_alarm);
		onDuty.setMisreport(misreport);
		onDuty.setFault_alarm(fault_alarm);
		onDuty.setSupervision(supervision);
		onDuty.setFalse_positives(false_positives);
		onDuty.setTreatment_situation(treatment_situation);
		onDuty.setSelf_check(self_check);
		onDuty.setSilencer(silencer);
		onDuty.setReset(reset);
		onDuty.setMain_power_supply(main_power_supply);
		onDuty.setbId(bId);
		onDuty.setFail_to_report(fail_to_report);
		onDuty.setProblem_handling(problem_handling);
		onDuty.setShiftTime(shiftTime);
		onDuty.setcId(cId);
		onDuty.setName(name);
		onDuty.setStart_date(start_date);
		onDuty.setEnd_date(end_date);

		boolean yang = logOnDutyService.addOnDutyCommon(onDuty);

		try {
			String ss = "dd";
			response.getWriter().write(ss);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 新增值班记录行信息
	 */
	@RequestMapping(value = "/addOperoyionData.htm")
	public void addOperoyionDataAtion(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException { // 乱码问题解决方案
		request.setCharacterEncoding("utf-8");// 必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。
		response.setContentType("text/html;charset=utf-8");// 设置传过去的页面显示的编码
		String shiftInformation = request.getParameter("shiftInformation");
		String shiftTime = request.getParameter("shiftTime");
		String cId = request.getParameter("cId");
		String bId = request.getParameter("bId");
		String start_date = request.getParameter("start_date");
		String end_date = request.getParameter("end_date");
		
		
		JSONArray array_personalinfo = JSONArray.fromObject(shiftInformation);  
        List<SystemOperation> personalinfoBean_list=new ArrayList<SystemOperation>();
        JSONObject jsonObject1;
        for (int i = 0; i < array_personalinfo.size(); i++){  
            jsonObject1 = array_personalinfo.getJSONObject(i);  
            personalinfoBean_list.add((SystemOperation)JSONObject.toBean(jsonObject1, SystemOperation.class));  
        }
		

		SystemOperation so = new SystemOperation();

		so.setShiftTime(shiftTime);
		so.setcId(cId);
		so.setoId(bId);
		so.setStart_date(start_date);
		so.setEnd_date(end_date);
		

		boolean yang = logOnDutyService.addOperoyionData(personalinfoBean_list,so);

		try {
	/*		response.getWriter().write();*/
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 获取值班记录信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getLogInformation.htm")
	public void getLogInformationAction(HttpServletRequest request,
			HttpServletResponse response){
		
		String current_date = request.getParameter("current_date");
		String bid = request.getParameter("bid");
		String cid = request.getParameter("xksid");
		
		ArrayList<Map> xfyhlist = (ArrayList<Map>) logOnDutyService
				.getLogInformation(current_date,bid,cid);
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
