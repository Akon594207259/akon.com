package cn.yunrui.intfirectrlsys.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;
import cn.yunrui.intfirectrlsys.domain.CodeEntity;
import cn.yunrui.intfirectrlsys.domain.ResultMessage1;
import cn.yunrui.intfirectrlsys.domain.TaskSettingVo;
import cn.yunrui.intfirectrlsys.domain.TaskSettingwj;
import cn.yunrui.intfirectrlsys.domain.TsInsContentVo;
import cn.yunrui.intfirectrlsys.domain.TsPersonnelVo;
import cn.yunrui.intfirectrlsys.service.Timing_taskService;
import cn.yunrui.intfirectrlsys.util.PinYinUtil;
import cn.yunrui.intfirectrlsys.util.Util;

/**
 * 定时任务模块
 * @author Administrator
 *
 */
@Service
@Controller
@RequestMapping("/timingTask")
public class TimingTaskControl {

	private static boolean iflocal = false;
	
	@RequestMapping(value = "/init.htm", method = RequestMethod.GET)
	public String wg(HttpServletRequest request, HttpServletResponse response) {
		return "ifcs/timingTask";
	}

	String x=Util.getDateTime();

	int a = 0;
	@Resource(name = "timing_taskservice")
	private Timing_taskService timing_taskService;

	// wj 根树
	@RequestMapping(value = { "/getUserRootFilter.htm" }, produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String getUserRootFilter(String factory_id) {
		Authentication auth = RequestContextSecurity.getAuthentication();
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		JsonConfig jsonConfig = new JsonConfig();
		resultMap.put("id", auth.getOrgNo());
		resultMap.put("name", auth.getOrgName());
		resultMap.put("type", "org");
		resultMap.put("isParent", Boolean.valueOf(true));
		return JSONArray.fromObject(resultMap, jsonConfig).toString();
	}

	///////////////////////////////////////////////////////////////////////////////////
	
	
	@RequestMapping(value = "/deleteTaskSetting1.htm", method = RequestMethod.POST)
	public void deleteTaskSetting1(HttpServletRequest request,
			HttpServletResponse response) {
		String deletedId = request.getParameter("deletedId");
		ResultMessage1 rMessage = this.timing_taskService
				.deleteTaskSetting1(deletedId);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) { // http://localhost:3000//http://localhost:8888
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rMessage.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//根据设备查线路
	@RequestMapping(value = "/queryLine.htm")
	public void queryLine(HttpServletRequest request,
			HttpServletResponse response) {

		String id = request.getParameter("deviceID");
		List<Map<String, Object>> hs = new ArrayList<Map<String, Object>>();
		hs=  timing_taskService.queryLine(id);

		String rst = JSONArray.fromObject(hs).toString();
		try {
			response.setContentType("application/json; charset=utf-8");

			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}

			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 新增任务
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws ParseException
	 */
	
	@RequestMapping(value = "/insertTask.htm")
	public void insertTask(HttpServletRequest request , HttpServletResponse response ) {
		/*request.setCharacterEncoding("utf-8");*/
		String taskID =  request.getParameter("task_id");
		//主键随机
		String week = request.getParameter("week");

		String time = request.getParameter("time");
		
		String timing_details = week+","+time;
		System.out.println("timing_details的值是"+timing_details);
		
		String state = request.getParameter("state");
		String lineIDNO = request.getParameter("lineIDNO");
		String DeviceIDNO=request.getParameter("DeviceIDNO");
		String name=request.getParameter("name");
		int i=timing_taskService.insertTiming_task(taskID, week,time, state, lineIDNO, DeviceIDNO,name);	
		response.setContentType("application/json; charset=utf-8");
		if (iflocal) {
			response.setHeader("Access-Control-Allow-Origin","http://localhost:3000");
			response.setHeader("Access-Control-Allow-Credentials", "true");
		}

		try {
			response.getWriter().write(JSONArray.fromObject(i).toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	/**
	 * 根据设备ID查任务列表 
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/queryTaskList.htm")
	public void queryYLFA(HttpServletRequest request , HttpServletResponse response)  throws UnsupportedEncodingException, ParseException {
		request.setCharacterEncoding("utf-8");
		String DeviceID = request.getParameter("deviceID");
		System.out.println("deviceID=="+DeviceID);
		List<Map<String,Object>> list = new ArrayList<Map<String , Object>>();
		list = timing_taskService.queryTaskList(DeviceID);
//		
//		for (int i = 0; i < list.size(); i++) {
//			list.get(i).get("week");
//			list.get(i).get("time");
//			
//			list.get(i).
//		}
		
		
		String rst = JSONArray.fromObject(list).toString();
		try {
			response.setContentType("application/json; charset=utf-8");

			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}

			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * wj 获取设备
	 * 
	 * @param request
	 * @param response
	 */
	/**
	 * 通过 消防用户获取到设备
	 */
	@RequestMapping(value = "/queryDeviceList.htm")
	public void getControlRoomAtion(HttpServletRequest request,
			HttpServletResponse response) {

		String id = request.getParameter("id");
		List hs = new ArrayList();
		hs=  timing_taskService.queryDeviceList(id);

		String rst = JSONArray.fromObject(hs).toString();
		try {
			response.setContentType("application/json; charset=utf-8");

			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}

			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////

	/**
	 * wj 获取消防用户
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getXfyhListCommon.htm", method = RequestMethod.POST)
	public void getXfyhListCommon(HttpServletRequest request,
			HttpServletResponse response) {
		Authentication auth = RequestContextSecurity.getAuthentication();

		ArrayList<CodeEntity> xfyhlist = timing_taskService.getXfyhListCommon(
				auth.getBureauNo(), auth.getOrgNo());
		for (int i = 0; i < xfyhlist.size(); i++) {
			String name = (String) xfyhlist.get(i).getName();
			String pinyin= PinYinUtil.getPingYin(name);
			String szm= PinYinUtil.getFirstSpell(name);
			xfyhlist.get(i).setPingyin(pinyin+",");
			xfyhlist.get(i).setSuoxie(szm+",");
		}
		String rst = JSONArray.fromObject(xfyhlist).toString();
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) { // http://localhost:3000\http://localhost:8888
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//定时任务
	public void Timingtask() throws ParseException {
		//查出任务表中符合当前时间的数据
		
		
		
	}
	
	// wj 根据条件查询消防用户下的某周期的任务设置单

	/*@RequestMapping(value = "/queryTaskSetting.htm", method = RequestMethod.POST)
	public void queryTaskSetting(HttpServletRequest request,
			HttpServletResponse response) {

		String query_outfire_user = request.getParameter("query_outfire_user");
		String ts_peroid = request.getParameter("ts_peroid");

		String per_name1 = request.getParameter("per_name");
		String[] per_name={};
		if(per_name1 !=null){
			per_name = per_name1.split(",");
		}
		int pageSize = Integer.parseInt(request.getParameter("pageSize"));
		int current = Integer.parseInt(request.getParameter("current"));
		int start = (current - 1) * pageSize;
		int limit = pageSize;
		List<TaskSettingwj> taskSettingList = this.timing_taskService
				.queryTaskSetting(query_outfire_user, ts_peroid, per_name,
						start, limit);
		String rst = JSONArray.fromObject(taskSettingList).toString();
		
		// 获取总记录数
		int total = this.timing_taskService.queryTotal(query_outfire_user,
				ts_peroid);
		String rststr = "{\"data\": " + rst + ", \"current\": " + current
				+ ", \"total\": " + total + "}";
		
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) { // http://localhost:3000//http://localhost:8888
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rststr);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	*//**
	 * wj 定时新增任务单
	 * 
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 *//*
	@RequestMapping(value = "/addTaskInfo.htm", method = RequestMethod.POST)
	public void addTaskInfo(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		// 乱码问题解决方案
		request.setCharacterEncoding("utf-8");// 必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。
		response.setContentType("text/html;charset=utf-8");// 设置传过去的页面显示的编码
		// 获取参数
		String query_outfire_user = request.getParameter("query_outfire_user");

		String ts_xfyh_id = request.getParameter("query_outfire_user_id");
		String ts_xfyh_name = request.getParameter("query_outfire_user_name");
		String ts_description = request.getParameter("ts_description");
		String per_name = request.getParameter("per_name");

		String ts_peroid = request.getParameter("ts_peroid");
		String ts_start_time = request.getParameter("ts_start_time");
		String ts_end_time = request.getParameter("ts_end_time");
		String data_num = request.getParameter("xuncha");
		
		
		String[] data_num1;
	
		// 封装对象
		TaskSettingwj taskSettingwj = new TaskSettingwj();
		// 生成 任务单的UUID 去掉 —
		String ts_id = UUID.randomUUID().toString().replace("-", "");

		// 人员 id [id id]
		List pamList = new ArrayList();
		
		// 人员键值对 [id,name， id, name]
		List xcList = new ArrayList();
		
		if (null != data_num && !"".equals(data_num)) {
			data_num1 = data_num.split(",");
		
			for (int i = 0; i < data_num1.length; i++) {
                  if(null !=data_num1[i]&&!"".equals(data_num1[i])){
                		TsInsContentVo tsInsContentVo = new TsInsContentVo();
        				tsInsContentVo.setP_ts_id(ts_id);
        				tsInsContentVo.setContent_id(data_num1[i]);
        				xcList.add(tsInsContentVo);
                  }
			
			}
		}
 
		
		String[] per_name1;

		if (null != per_name && !"".equals(per_name)) {

			per_name1 = per_name.split(",");
			for (int i = 0; i < per_name1.length; i = i + 2) {
				if (null != per_name1[i] && !"".equals(per_name1[i])) {
					TsPersonnelVo tsPersonnelVo = new TsPersonnelVo();
					tsPersonnelVo.setTs_id(ts_id);
					tsPersonnelVo.setPer_id(per_name1[i]);
					tsPersonnelVo.setPer_name(per_name1[i + 1]);
					pamList.add(tsPersonnelVo);
				}
			}
		}

		taskSettingwj.setTs_id(ts_id);
		taskSettingwj.setQuery_outfire_user(query_outfire_user);
		taskSettingwj.setTs_xfyh_id(ts_xfyh_id);
		taskSettingwj.setTs_xfyh_name(ts_xfyh_name);
		taskSettingwj.setTs_description(ts_description);
		taskSettingwj.setPer_name(per_name);
		taskSettingwj.setTs_peroid(ts_peroid);
		taskSettingwj.setTs_start_time(ts_start_time);
		taskSettingwj.setTs_end_time(ts_end_time);
		ResultMessage1 rMessage = this.timing_taskService.addTaskSetting(pamList, xcList, taskSettingwj);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) { // http://localhost:3000//http://localhost:8888
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rMessage.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	*//**
	 * wj 获取消防用户的ID 传到子组件
	 *//*
	@RequestMapping(value = "/getts_xfyh_name.htm", method = RequestMethod.POST)
	public void getts_xfyh_name(HttpServletRequest request,
			HttpServletResponse response) {
		String outfire_user_id = request.getParameter("outfire_user_id");
		List<String> ts_xfyh_nameList = timing_taskService
				.getts_xfyh_name(outfire_user_id);
		String rst = JSONArray.fromObject(ts_xfyh_nameList).toString();
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	*//**
	 * wj 查看任务设置单详情
	 * 
	 * @param request
	 * @param response
	 *//*
	@RequestMapping(value = "/queryDetail.htm", method = RequestMethod.POST)
	public void queryDetail(HttpServletRequest request,
			HttpServletResponse response) {
		String detailId = request.getParameter("detailId");
		System.out.println(" ======== taskcontrol ====== " + detailId
				+ " ====== ");
		TaskSettingwj taskSettingwj = this.timing_taskService
				.queryTaskSetting(detailId);

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) { // http://localhost:3000//http://localhost:8888
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(taskSettingwj.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// wj 查询消防用户下所有的任务设置单 根据任务单ID

	@RequestMapping(value = "/queryTaskSettingByID.htm")
	public void queryTaskSettingByIDAction(HttpServletRequest request,
			HttpServletResponse response) {

		String detailId = request.getParameter("detailId");

		Map taskSetting = (Map) this.timing_taskService
				.queryTaskSettingByID(detailId);
		String rst = JSONArray.fromObject(taskSetting).toString();

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) { // http://localhost:3000//http://localhost:8888
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// wj 编辑任务单

	@RequestMapping(value = "/editTaskSetting.htm", method = RequestMethod.POST)
	public void editTaskSetting(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		// 乱码问题解决方案
		request.setCharacterEncoding("utf-8");// 必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。
		response.setContentType("text/html;charset=utf-8");// 设置传过去的页面显示的编码
		// 获取参数

		String editId = request.getParameter("editId");
		String query_outfire_user = request.getParameter("query_outfire_user");
		String ts_xfyh_id = request.getParameter("query_outfire_user_id");
		String ts_xfyh_name = request.getParameter("query_outfire_user_name");
		String ts_description = request.getParameter("ts_description");
		String per_name = request.getParameter("per_name");
		String ts_peroid = request.getParameter("ts_peroid");
		String ts_start_time = request.getParameter("ts_start_time");
		String ts_end_time = request.getParameter("ts_end_time");

		String data_num = request.getParameter("xuncha");

		String[] data_num1;
		
		// 封装对象
		TaskSettingwj taskSettingwj = new TaskSettingwj();
		// 生成 任务单的UUID 去掉 —
		
		// 人员 id [id id]
		List pamList = new ArrayList();
		
		// 人员键值对 [id,name， id, name]
		List xcList = new ArrayList();
		
		if (null != data_num && !"".equals(data_num)) {
			data_num1 = data_num.split(",");
		
			for (int i = 0; i < data_num1.length; i++) {
                  if(null !=data_num1[i]&&!"".equals(data_num1[i])){
                		TsInsContentVo tsInsContentVo = new TsInsContentVo();
        				
        				tsInsContentVo.setContent_id(data_num1[i]);
        				xcList.add(tsInsContentVo);
                  }
			
			}
		}
			
		String[] per_name1;

		if (null != per_name && !"".equals(per_name)) {

			per_name1 = per_name.split(",");
			for (int i = 0; i < per_name1.length; i = i + 2) {
				if (null != per_name1[i] && !"".equals(per_name1[i])) {
					TsPersonnelVo tsPersonnelVo = new TsPersonnelVo();

					tsPersonnelVo.setPer_id(per_name1[i]);
					System.out.println(" id-----" + per_name1[i]);
					tsPersonnelVo.setPer_name(per_name1[i + 1]);
					System.out.println(" name-----" + per_name1[i+1]);
					
					pamList.add(tsPersonnelVo);
				}
			}
		}
		
		taskSettingwj.setQuery_outfire_user(query_outfire_user);
		taskSettingwj.setTs_xfyh_id(ts_xfyh_id);
		taskSettingwj.setTs_xfyh_name(ts_xfyh_name);
		taskSettingwj.setTs_description(ts_description);
		taskSettingwj.setPer_name(per_name);
		taskSettingwj.setTs_peroid(ts_peroid);
		taskSettingwj.setTs_start_time(ts_start_time);
		taskSettingwj.setTs_end_time(ts_end_time);
		ResultMessage1 rMessage = this.timing_taskService.editTaskSetting(pamList, xcList, taskSettingwj);
		
		
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) { // http://localhost:3000//http://localhost:8888
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rMessage.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 封装对象
		TaskSettingwj taskSetting1 = new TaskSettingwj();
		taskSetting1.setTs_id(editId);
		taskSetting1.setQuery_outfire_user(query_outfire_user);
		taskSetting1.setTs_xfyh_id(ts_xfyh_id);
		taskSetting1.setTs_xfyh_name(ts_xfyh_name);
		taskSetting1.setTs_description(ts_description);
		taskSetting1.setPer_name(per_name);
		taskSetting1.setTs_peroid(ts_peroid);
		taskSetting1.setTs_start_time(ts_start_time);
		taskSetting1.setTs_end_time(ts_end_time);

		ResultMessage1 rMessage1 = this.timing_taskService
				.editTaskSetting(pamList, xcList, taskSetting1);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) { // http://localhost:3000//http://localhost:8888
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rMessage1.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// wj 删除某一个任务单

	@RequestMapping(value = "/deleteTaskSetting.htm", method = RequestMethod.POST)
	public void deleteTaskSetting(HttpServletRequest request,
			HttpServletResponse response) {
		String deletedId = request.getParameter("deletedId");
		ResultMessage1 rMessage = this.timing_taskService
				.deleteTaskSetting(deletedId);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) { // http://localhost:3000//http://localhost:8888
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rMessage.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// wj 删除某一个任务单 之 删除任务单表

	@RequestMapping(value = "/deletePersonnel.htm", method = RequestMethod.POST)
	public void deletePersonnel(HttpServletRequest request,
			HttpServletResponse response) {
		String deletedId = request.getParameter("deletedId");
		ResultMessage1 rMessage = this.timing_taskService
				.deletePersonnel(deletedId);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) { // http://localhost:3000//http://localhost:8888
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rMessage.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	// wj 删除某一个任务单 之 删除巡查内容项
	
	//定时器任务
	
	@SuppressWarnings("unchecked")
	public void task() throws ParseException {
		List<TaskSettingVo> TaskSettingVoList = timing_taskService.mobanList(Util.getDateTime());
		System.out.println("时间++++=" + Util.getDateTime());
		// 取模版List 取ID和周期

		System.out.println("taskSettingList定时任务" + TaskSettingVoList);
	
		if (TaskSettingVoList.size() > 0) {

			
			
			if(TaskSettingVoList!=null){
				a = TaskSettingVoList.size();
			}
		
			System.out.println("查到任务模版====");

			for (int i = 0; i < TaskSettingVoList.size(); i++) {
				TaskSettingVoList.get(i).getTs_id();
				TaskSettingVoList.get(i).getTs_peroid();

				if ("day".equals(TaskSettingVoList.get(i).getTs_peroid())) {
					System.out.println("--模版ID	--"+ TaskSettingVoList.get(i).getTs_id()+"--周期	--"+ TaskSettingVoList.get(i).getTs_peroid());
					System.out.println("第二天的时间----"+ Util.getNextDate(Util.getDate()));
					String dString ="D";
					
					timing_taskService.productionWork(dString,a,Util.getNextDate(Util.getDate()), TaskSettingVoList.get(i).getTs_id());
					
				}

			}

		}

		System.out.println("++++++++++++Control层  day   新增 已扫描完毕+++++++++");
	}
	
	public void weektask() throws ParseException {
		List<TaskSettingVo> TaskSettingVoList = timing_taskService.mobanList(Util.getDateTime());
		System.out.println("时间++++=" + Util.getDateTime());
	
		// 取模版List 取ID和周期
		System.out.println("taskSettingList定时任务" + TaskSettingVoList);
	
		if (TaskSettingVoList.size() > 0) {

			
			
			if(TaskSettingVoList!=null){
				a = TaskSettingVoList.size();
			}
		
			System.out.println("查到任务模版====");

			for (int i = 0; i < TaskSettingVoList.size(); i++) {
				TaskSettingVoList.get(i).getTs_id();
				TaskSettingVoList.get(i).getTs_peroid();
				if ("week".equals(TaskSettingVoList.get(i).getTs_peroid())) {
					System.out.println("--模版ID	--"+ TaskSettingVoList.get(i).getTs_id()+"--周期	--"+ TaskSettingVoList.get(i).getTs_peroid());
					
					System.out.println();
					String wString ="W";
					
					System.out.println("下一周时间----" + Util.getMinusDateTime(7));
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					
					Date date = sdf.parse(Util.getDateTime()); 
					Date date1 = sdf.parse(x); 
	
					if(date.after(date1)){
						x = Util.getMinusDateTime(7);
						
						timing_taskService.productionWork(wString,a,Util.getMinusDateTime(7),TaskSettingVoList.get(i).getTs_id());
						}
					
				}
				
			}

		}

		System.out.println("++++++++++++Control层 week   新增 已扫描完毕+++++++++");
	}

	public void monthtask() throws ParseException {
		List<TaskSettingVo> TaskSettingVoList = timing_taskService.mobanList(Util.getDateTime());
		System.out.println("当前时间++++=" + Util.getDateTime());
	

		// 取模版List 取ID和周期

		System.out.println("taskSettingList定时任务 扫描月巡检" + TaskSettingVoList);
	
		if (TaskSettingVoList.size() > 0) {

			
			
			if(TaskSettingVoList!=null){
				a = TaskSettingVoList.size();
			}
		
			System.out.println("查到任务模版====");

			for (int i = 0; i < TaskSettingVoList.size(); i++) {
				TaskSettingVoList.get(i).getTs_id();
				TaskSettingVoList.get(i).getTs_peroid();
				
				if ("month".equals(TaskSettingVoList.get(i).getTs_peroid())) {
					System.out.println("--所有模版ID	--"+ TaskSettingVoList.get(i).getTs_id()+"--所有周期	--"+ TaskSettingVoList.get(i).getTs_peroid());
					
					System.out.println();
					String mString ="M";
					System.out.println("上一个月时间----" + Util.getMinusMDateTime(-1)+"下一个月时间----" + Util.getMinusMDateTime(1));
					System.out.println();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					
					Date date = sdf.parse(Util.getDateTime()); 
					Date date1 = sdf.parse(x);
					
					if(date.after(date1)){
						x = Util.getMinusMDateTime(1);
						
						timing_taskService.productionWork(mString,a,Util.getMinusMDateTime(1), TaskSettingVoList.get(i).getTs_id());
					}
					
				}

			}

		}

		System.out.println("++++++++++++Control层  month   新增 已扫描完毕+++++++++");
	}
	
	
	public void seasontask() throws ParseException {
		List<TaskSettingVo> TaskSettingVoList = timing_taskService.mobanList(Util.getDateTime());
		System.out.println("时间++++=" + Util.getDateTime());

		// 取模版List 取ID和周期
		System.out.println("taskSettingList定时任务  季 巡检" + TaskSettingVoList);
	
		if (TaskSettingVoList.size() > 0) {

			
			
			if(TaskSettingVoList!=null){
				a = TaskSettingVoList.size();
			}
		
			System.out.println("查到任务模版====");

			for (int i = 0; i < TaskSettingVoList.size(); i++) {
				TaskSettingVoList.get(i).getTs_id();
				TaskSettingVoList.get(i).getTs_peroid();

				if ("season".equals(TaskSettingVoList.get(i).getTs_peroid())) {
					
					System.out.println("--季  所有模版ID	--"+ TaskSettingVoList.get(i).getTs_id()+"--季  所有 周期	--"+ TaskSettingVoList.get(i).getTs_peroid());
					System.out.println("上一季的时间----" + Util.getMinusMDateTime(-3)+"下一季的时间----" + Util.getMinusMDateTime(3));
					String sString ="S";
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = sdf.parse(Util.getDateTime()); 
					Date date1 = sdf.parse(x);
					if(date.after(date1)){
						x = Util.getMinusMDateTime(3);
						
						timing_taskService.productionWork(sString,a,Util.getMinusMDateTime(3), TaskSettingVoList.get(i).getTs_id());
					}
				}

			}

		}

		System.out.println("++++++++++++Control层  季   新增 已扫描完毕+++++++++");
	}
	public void yeartask() throws ParseException {
		List<TaskSettingVo> TaskSettingVoList = timing_taskService.mobanList(Util.getDateTime());
		System.out.println("时间++++=" + Util.getDateTime());
		// 取模版List 取ID和周期

		System.out.println("taskSettingList定时任务  年巡检" + TaskSettingVoList);
	
		if (TaskSettingVoList.size() > 0) {

			
			
			if(TaskSettingVoList!=null){
				a = TaskSettingVoList.size();
			}
		
			System.out.println("查到任务模版====");

			for (int i = 0; i < TaskSettingVoList.size(); i++) {
				TaskSettingVoList.get(i).getTs_id();
				TaskSettingVoList.get(i).getTs_peroid();

				System.out.println("--年  所有模版ID	--"+ TaskSettingVoList.get(i).getTs_id()+"--年  所有 周期	--"+ TaskSettingVoList.get(i).getTs_peroid());
				
				System.out.println();
				
				if ("year".equals(TaskSettingVoList.get(i).getTs_peroid())) {
					String yString ="Y";
					System.out.println("上一年的时间----" + Util.getMinusMDateTime(-12)+"下一年的时间----" + Util.getMinusMDateTime(12));
					System.out.println();
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = sdf.parse(Util.getDateTime()); 
					Date date1 = sdf.parse(x);
					if(date.after(date1)){
						x = Util.getMinusMDateTime(12);
						
						timing_taskService.productionWork(yString,a,Util.getMinusMDateTime(12), TaskSettingVoList.get(i).getTs_id());
					}
				}

			}

		}

		System.out.println("++++++++++++Control层  年   新增 已扫描完毕+++++++++");
	}
	
	
	// 前台点击浏览
	@RequestMapping(value = { "/queryStatement.htm" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public void queryStatement(HttpServletRequest request,
			HttpServletResponse response) {
		String p_id = request.getParameter("p_id");

		System.out.println(" Control 678 p_id---------"+p_id);
		List MissionStatementlist = timing_taskService
				.MissionStatementInfo(p_id);

		String rst = JSONArray.fromObject(MissionStatementlist).toString();
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 点击浏览的下面表格

	// 前台点击浏览
	@RequestMapping(value = { "/queryXiao.htm" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public void queryXiao(HttpServletRequest request,
			HttpServletResponse response) {
		String p_id = request.getParameter("detailId");
		System.out.println(" Control 704  p_id---------"+p_id);
		List ContentFixlist = timing_taskService.ContentFixInfo(p_id);

		String rst = JSONArray.fromObject(ContentFixlist).toString();
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 查询小类具体信息
	@RequestMapping(value = { "/queryLei.htm" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public void queryLei(HttpServletRequest request,
			HttpServletResponse response) {
		String p_id = request.getParameter("detailId");

		List Contenlist = timing_taskService.ContentFixdata(p_id);

		String rst = JSONArray.fromObject(Contenlist).toString();
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 查询所有大类项
	@RequestMapping(value = { "/queryDalei.htm" })
	public void queryDalei(HttpServletRequest request,
			HttpServletResponse response) {
		String cf_id = request.getParameter("cf_id");

		Map ContenMap = timing_taskService.queryInspectionItem(cf_id);

		String rst = JSONArray.fromObject(ContenMap).toString();
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 编辑查询
	@RequestMapping(value = { "/queryDalei1.htm" })
	public void queryDalei1(HttpServletRequest request,
			HttpServletResponse response) {
		String cf_id = request.getParameter("cf_id");

		Map ContenMap = timing_taskService.queryInspectionItem1(cf_id);

		String rst = JSONArray.fromObject(ContenMap).toString();
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	// 前台点击浏览内容项
		@RequestMapping(value = { "/queryDetailInfoXC.htm" })
		public void queryDetailInfoXC(HttpServletRequest request,
				HttpServletResponse response) {
			String p_id = request.getParameter("detailId");

			System.out.println(" Control 842 p_id---------"+p_id);
			Map map = timing_taskService
					.queryDetailInfoXC(p_id);

			String rst = JSONArray.fromObject(map).toString();
			try {
				response.setContentType("application/json; charset=utf-8");
				if (iflocal) {
					response.setHeader("Access-Control-Allow-Origin",
							"http://localhost:3000");
					response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	
		
		// 查询所有大类项
		@RequestMapping(value = { "/queryData.htm" })
		public void queryData(HttpServletRequest request,
				HttpServletResponse response) {
			String cf_id = request.getParameter("cf_id");
			String cf_id1 = request.getParameter("cf_id1");

			Map ContenMap = timing_taskService.queryInspectionItem(cf_id);

			String rst = JSONArray.fromObject(ContenMap).toString();
			try {
				response.setContentType("application/json; charset=utf-8");
				if (iflocal) {
					response.setHeader("Access-Control-Allow-Origin",
							"http://localhost:3000");
					response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
				
       *//**
        * 查询巡查项
        * @param request
        * @param response
        *//*
		@RequestMapping(value = "/queryTaskSettingXC.htm")
		public void queryTaskSettingXC(HttpServletRequest request,
				HttpServletResponse response) {

			String detailId = request.getParameter("detailId");

			Map taskSetting = (Map) this.timing_taskService
					.queryTaskSettingXC(detailId);
			String rst = JSONArray.fromObject(taskSetting).toString();

			try {
				response.setContentType("application/json; charset=utf-8");
				if (iflocal) { // http://localhost:3000//http://localhost:8888
					response.setHeader("Access-Control-Allow-Origin",
							"http://localhost:3000");
					response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		
}
