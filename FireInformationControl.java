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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;
import cn.yunrui.intfirectrlsys.domain.FireInformation;
import cn.yunrui.intfirectrlsys.service.FireInformationService;

@Controller
@RequestMapping("/fireInformation")
public class FireInformationControl {

	@Resource(name = "fireInformationService")
	private FireInformationService fiService;
	private static boolean iflocal = true;

	/**
	 * 新增火警信息
	 * 
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/addFireInformation.htm")
	public void addFireInformation(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		FireInformation fi = new FireInformation();
		fi.setDesc(request.getParameter("desc"));
		fi.setDevId(request.getParameter("devId"));
		fi.setDevName(request.getParameter("devName"));
		fi.setLevel(request.getParameter("level"));
		fi.setLevelName(request.getParameter("levelName"));
		fi.setName(request.getParameter("name"));
		fi.setSubburo(request.getParameter("subburo"));
		fi.setTypeName(request.getParameter("typeName"));
		fi.setGive_an_alarm(request.getParameter("time"));
		fiService.addFireInformation(fi);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询未处理的火警
	 * 
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/getFireInformation.htm")
	public void getFireInformation(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		List list = fiService.getFireInformation();

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(list).toString())
							.toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 记录进度
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/updateObject.htm")
	public void updateObject(HttpServletRequest request,
			HttpServletResponse response) {

		FireInformation fi = new FireInformation();
        System.err.println("值班人："+request.getParameter("watchmen"));
		fi.setWatchmen(request.getParameter("watchmen"));
		fi.setDuty_calls(request.getParameter("duty_calls"));
		fi.setUnit_person(request.getParameter("unit_person"));
		fi.setUnit_telephone(request.getParameter("unit_telephone"));
		fi.setRemarks(request.getParameter("remarks"));
		fi.setAlert_time( request.getParameter("alert_time"));
		fi.setNotice_time(request.getParameter("notice_time"));
		fi.setUpload_time(request.getParameter("upload_time"));
		fi.setPolice_time(request.getParameter("police_time"));
		fi.setProcessing_time(request.getParameter("processing_time"));
		fi.setPlace_on_file(request.getParameter("place_on_file"));
		fi.setFi_id(request.getParameter("fi_id") );
		fi.setSchedule(request.getParameter("schedule"));

		boolean list = fiService.updateObject(fi);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(list).toString())
							.toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取消控室值班人员姓名和电话
	 * 
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/getOnDuty.htm")
	public void getOnDuty(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		String companyid = request.getParameter("companyid");

		Map map = fiService.getOnDuty(companyid);

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(map).toString())
							.toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////查看火警///////////////////////////////

	/**
	 * 新增处理信息
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/queryObject.htm")
	public void queryObject(HttpServletRequest request,
			HttpServletResponse response) {
		FireInformation fi = new FireInformation();
		fi.setSubburo(request.getParameter("subburo"));
		String start = request.getParameter("start");
		String end = request.getParameter("end");
		List list = fiService.queryObject(fi, start, end);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(list).toString())
							.toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询用户
	 * 
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/fiList.htm")
	public void fiList(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		Authentication auth = RequestContextSecurity.getAuthentication();
		String buro = auth.getBureauNo();

		List list = fiService.fiList(buro);

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(list).toString())
							.toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询消防用户ID
	 * 
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/networkedUsers.htm")
	public void networkedUsers(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {

		request.setCharacterEncoding("utf-8");
		String subburo = request.getParameter("subburo");
		List list = fiService.networkedUsers(subburo);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(list).toString())
							.toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 查询消防用户ID
	 * 
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/networkedDate.htm")
	public void networkedDate(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {

		request.setCharacterEncoding("utf-8");
		String id = request.getParameter("id");
		List list = fiService.networkedDate(id);
		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(list).toString())
							.toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取初始坐标
	 * 
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/getInitialCoordinates.htm")
	public void getInitialCoordinates(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {

		request.setCharacterEncoding("utf-8");
		Authentication auth = RequestContextSecurity.getAuthentication();
		String buro = auth.getBureauNo();
		String org = auth.getOrgNo();
		System.err.println("+++++++++++++用户编号用户编号用户编号用户编号用户编号用户编号 ++++++++++++++++++++"+org);
		List list = fiService.getInitialCoordinates(org, buro);
		System.out.println("查询出来的list是是是是是是是是是是是是是是是是是是是是是是是是是是"+list);

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(list).toString())
							.toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取消防主管信息
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/getExecutiveDirector.htm")
	public void getExecutiveDirector(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {

		request.setCharacterEncoding("utf-8");
		String buro = request.getParameter("subburo");

		JSONObject list = fiService.getExecutiveDirector(buro);

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(list).toString())
							.toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取单位信息
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/getUnitsInfo.htm")
	public void getUnitsInfo(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {

		request.setCharacterEncoding("utf-8");
		String buro = request.getParameter("subburo");
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		list = fiService.getUnitsInfo(buro);

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(list).toString())
							.toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取火警信息
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/getWarningInformation.htm")
	public void getWarningInformation(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {

		request.setCharacterEncoding("utf-8");
		String buro = request.getParameter("subburo");
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		list = fiService.getWarningInformation(buro);

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(list).toString())
							.toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取有火警信息的单位
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/getHasAlarmInformation.htm")
	public void getHasAlarmInformation(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {

		request.setCharacterEncoding("utf-8");
	
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		list = fiService.getHasAlarmInformation();

		try {
			response.setContentType("application/json; charset=utf-8");
			if (iflocal) {
				response.setHeader("Access-Control-Allow-Origin",
						"http://localhost:3000");
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(
					JSONArray.fromObject(JSONArray.fromObject(list).toString())
							.toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
