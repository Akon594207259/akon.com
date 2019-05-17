package cn.yunrui.intfirectrlsys.action;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.yunrui.intfirectrlsys.domain.FireInformation;
import cn.yunrui.intfirectrlsys.entity.NameValueEntity;
import cn.yunrui.intfirectrlsys.request.IFCSMonitorRequest;
import cn.yunrui.intfirectrlsys.response.IFCSMonitorResponse;
import cn.yunrui.intfirectrlsys.service.FireInformationService;
import cn.yunrui.intfirectrlsys.service.IFCUService;
import cn.yunrui.intfirectrlsys.service.JumpTheUserService;
import cn.yunrui.intfirectrlsys.util.Util;

import com.i380v.openservices.event.IAlarmDataService;
import com.i380v.openservices.measure.model.EventDataInfo;


@Controller
@RequestMapping("/jumpTheUser")
public class JumpTheUser {
	@Resource(name = "jumpTheUserService")
	private JumpTheUserService jumpTheUserService;
	
	@Resource(name = "fireInformationService")
	private FireInformationService fiService;
	
	@Resource(name="intfirectrlsys_alarm.ds")
	private IAlarmDataService service4;
	
	@Resource(name="intfirectrlsysservice")
	private IFCUService service;


	private static boolean iflocal = Util.ifbd;
	

	  @Resource(name="ifcs_jdbcTemplate")
	  private JdbcTemplate jdbcTemplate;
	
	@RequestMapping(value={"/init.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String init(Model model,HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("application/json; charset=utf-8");
		
		String bureauNo = request.getParameter("buro");
		String orgNo = request.getParameter("org");
		
	
		
		bureauNo = (bureauNo == null) ? "" : bureauNo.trim();
		orgNo = (orgNo == null) ? "" : orgNo.trim();
		
		model.addAttribute("buro", bureauNo);
		model.addAttribute("org", orgNo);
		model.addAttribute("subburo", orgNo);
		
		
		return   "/ifcs/fireIndex1" ;
	  }
	
	
	@RequestMapping({"/getInitialCoordinates.htm"})
	  public void getInitialCoordinates(HttpServletRequest request, HttpServletResponse response)
	    throws UnsupportedEncodingException
	  {
	    request.setCharacterEncoding("utf-8");
	   /* Authentication auth = RequestContextSecurity.getAuthentication();
	    String buro = auth.getBureauNo();
	    String org = auth.getOrgNo();*/
	    String buro = request.getParameter("buro");//方元安 88000704 诸暨 88001488
		String org = request.getParameter("org");//方元安 88000704 诸暨 88000704
	    //System.err.println("+++++++++++++用户编号用户编号用户编号用户编号用户编号用户编号 ++++++++++++++++++++" + buro);
	    List list = this.fiService.getInitialCoordinates(org, buro);
	    //System.out.println("查询出来的list是是是是是是是是是是是是是是是是是是是是是是是是是是" + list);
	    try
	    {
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
	//代理商下单位告警
	@RequestMapping({"/getHasAlarmInformation.htm"})
	  public void getHasAlarmInformation(HttpServletRequest request, HttpServletResponse response)
	    throws UnsupportedEncodingException
	  {
	    request.setCharacterEncoding("utf-8");

	    String buro = request.getParameter("buro");
		String org = request.getParameter("org");
	    List list = new ArrayList();
	    list = this.jumpTheUserService.getHasAlarmInformation(buro);
	    try
	    {
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
	
	
	 @RequestMapping({"/getUnitsInfo.htm"})
	  public void getUnitsInfo(HttpServletRequest request, HttpServletResponse response)
	    throws UnsupportedEncodingException
	  {
	    request.setCharacterEncoding("utf-8");
	    String buro = request.getParameter("subburo");

	    List list = new ArrayList();
	    list = this.fiService.getUnitsInfo(buro);
	    try
	    {
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
	
	 
	 
	 @RequestMapping({"/getExecutiveDirector.htm"})
	  public void getExecutiveDirector(HttpServletRequest request, HttpServletResponse response)
	    throws UnsupportedEncodingException
	  {
	    request.setCharacterEncoding("utf-8");
	    String buro = request.getParameter("subburo");

	    JSONObject list = this.fiService.getExecutiveDirector(buro);
	    try
	    {
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
	
	
	 
	 @RequestMapping({"/networkedUsers.htm"})
	  public void networkedUsers(HttpServletRequest request, HttpServletResponse response)
	    throws UnsupportedEncodingException
	  {
	    request.setCharacterEncoding("utf-8");
	    String subburo = request.getParameter("subburo");
	    List list = this.fiService.networkedUsers(subburo);
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
	 
	 
	 @RequestMapping({"/getOnDuty.htm"})
	  public void getOnDuty(HttpServletRequest request, HttpServletResponse response)
	    throws UnsupportedEncodingException
	  {
	    request.setCharacterEncoding("utf-8");
	    String companyid = request.getParameter("companyid");

	    Map map = this.fiService.getOnDuty(companyid);
	    try
	    {
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
	 
	 @RequestMapping({"/getWarningInformation.htm"})
	  public void getWarningInformation(HttpServletRequest request, HttpServletResponse response)
	    throws UnsupportedEncodingException
	  {
	    request.setCharacterEncoding("utf-8");
	    String buro = request.getParameter("subburo");
	    List list = new ArrayList();
	    list = this.fiService.getWarningInformation(buro);
	    try
	    {
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
	 
	 @RequestMapping({"/networkedDate.htm"})
	  public void networkedDate(HttpServletRequest request, HttpServletResponse response)
	    throws UnsupportedEncodingException
	  {
	    request.setCharacterEncoding("utf-8");
	    String id = request.getParameter("id");
	    List list = this.fiService.networkedDate(id);
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
	 
	 @RequestMapping({"/addFireInformation.htm"})
	  public void addFireInformation(HttpServletRequest request, HttpServletResponse response)
	    throws UnsupportedEncodingException
	  {
	  }
	 
	 //火警信息
	 @RequestMapping({"/getFireInformation.htm"})
	  public void getFireInformation(HttpServletRequest request, HttpServletResponse response)
	    throws UnsupportedEncodingException
	  {
	    request.setCharacterEncoding("utf-8");
	    String buro = request.getParameter("buro");
	    List list = this.jumpTheUserService.getFireInformation(buro);
	    try
	    {
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
	
	
	 @RequestMapping({"/updateObject.htm"})
	  public void updateObject(HttpServletRequest request, HttpServletResponse response)
	  {
	    FireInformation fi = new FireInformation();
	    //System.err.println("值班人：" + request.getParameter("watchmen"));
	    fi.setWatchmen(request.getParameter("watchmen"));
	    fi.setDuty_calls(request.getParameter("duty_calls"));
	    fi.setUnit_person(request.getParameter("unit_person"));
	    fi.setUnit_telephone(request.getParameter("unit_telephone"));
	    fi.setRemarks(request.getParameter("remarks"));
	    fi.setAlert_time(request.getParameter("alert_time"));
	    fi.setNotice_time(request.getParameter("notice_time"));
	    fi.setUpload_time(request.getParameter("upload_time"));
	    fi.setPolice_time(request.getParameter("police_time"));
	    fi.setProcessing_time(request.getParameter("processing_time"));
	    fi.setPlace_on_file(request.getParameter("place_on_file"));
	    fi.setFi_id(request.getParameter("fi_id"));
	    fi.setSchedule(request.getParameter("schedule"));

	    boolean list = this.fiService.updateObject(fi);
	    try {
	      response.setContentType("application/json; charset=utf-8");
	      if (iflocal) {
	        response.setHeader("Access-Control-Allow-Origin", 
	          "http://localhost:3000");
	        response.setHeader("Access-Control-Allow-Credentials", "true");
	      }
	      response.getWriter().write(
	        JSONArray.fromObject(JSONArray.fromObject(Boolean.valueOf(list)).toString())
	        .toString());
	      response.getWriter().flush();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	  }
	 
	 @RequestMapping(value={"/initwj.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String wg(HttpServletRequest request, HttpServletResponse response)
	  {
	    return "ifcs/View_Details";
	  }
	 
	 //饼7日趋势 折线图
	 @RequestMapping({"/queryIFCSMonitorXFH.htm"})
	  public void queryIFCSMonitorXFH(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, ParseException { 
		String buro = request.getParameter("buro");
	    List<String> subburos = getSubburo(buro);
	    List list = new ArrayList();
	    for (int i = 6; i >= 0; i--) {
	      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	      
	      Calendar calendar = Calendar.getInstance();
	      calendar.set(6, calendar.get(6) - i);
	      Date today = calendar.getTime();
	      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	      String result = format.format(today);
	      String T = result + " " + "00:00:00";
	      String M = result + " " + "23:59:59";
	      Date startTime = sdf2.parse(T);
	      Date endTime = sdf2.parse(M);
	      SimpleDateFormat yur = new SimpleDateFormat("MM");
	      String yur1 = yur.format(today);
	      SimpleDateFormat ri = new SimpleDateFormat("dd");
	      String ri1 = ri.format(today);
	      int daynum = 0;
	      for (String subburo : subburos) {
	        List events = this.service4.getAlarmDataByTime(subburo, startTime, endTime);
	        daynum += events.size();
	      }

	      if (daynum != 0) {
	        NameValueEntity nve = new NameValueEntity();
	        nve.setName(yur1 + "月" + ri1 + "日");
	        nve.setValue(daynum);
	        list.add(nve);
	      }
	    }

	    IFCSMonitorResponse res = new IFCSMonitorResponse();
	    res.setType("warntrendofmonth");
	    res.setData(list);
	    try
	    {
	      response.setContentType("application/json; charset=utf-8");
	      if (iflocal) {
	        response.setHeader("Access-Control-Allow-Origin", 
	          "http://localhost:3000");
	        response.setHeader("Access-Control-Allow-Credentials", "true");
	      }
	      response.getWriter().write(
	        JSONArray.fromObject(JSONArray.fromObject(res).toString())
	        .toString());
	      response.getWriter().flush();
	    } catch (IOException e) {
	      e.printStackTrace();
	    } }


	 private List<String> getSubburo(String buro)
	  {
	    String sql = "select DISTINCT subburo from cisp_dev.dev_powersystemresource where  subburo is not null and buro = ?";
	    return this.jdbcTemplate.query(sql, new Object[] { buro }, 
	      new RowMapper()
	    {
	      public String mapRow(ResultSet rs, int i) throws SQLException {
	        return rs.getString(1);
	      } } );
	  }
	 
	 //7日火警处理 柱状图
	 @RequestMapping({"/essentialInformation.htm"})
	  public void essentialInformation(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
	    request.setCharacterEncoding("utf-8");
	    String type = request.getParameter("type");
	    String subburo = request.getParameter("subburo");
	    String buro = request.getParameter("buro");//方元安 88000704 诸暨 88001488
		String org = request.getParameter("org");//方元安 88000704 诸暨 88000704
	    List list = this.jumpTheUserService.essentialInformation(subburo, type,buro );
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
	//分类报警
	 @RequestMapping({"/getClassifiedAlarm.htm"})
	  public void getClassifiedAlarm(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { request.setCharacterEncoding("utf-8");
	    String YHburo = request.getParameter("buro");
	    System.err.println("YHburo:" + YHburo);
	    /*Authentication auth = RequestContextSecurity.getAuthentication();
	    String buro = auth.getBureauNo();
	    String org = auth.getOrgNo();*/
	    
	    /*List subburos = new ArrayList();*/
	    
	    String buro = request.getParameter("buro");//方元安 88000704 诸暨 88001488
		String org = request.getParameter("org");//方元安 88000704 诸暨 88000704
		
		
	    List<String> subburos = getSubburo(buro);
	    if ((YHburo != null) && (!"".equals(YHburo))) {
	      subburos.add(YHburo);
	    }
	    else if (buro.equals(org))
	      subburos = getSubburo(org);
	    else {
	      subburos.add(buro);
	    }

	    System.err.println("subburos:" + subburos);

	    Calendar cal = Calendar.getInstance();
	    cal.set(11, 0);
	    cal.set(12, 0);
	    cal.set(13, 0);
	    Date startTime1 = cal.getTime();
	    cal.add(5, 1);
	    Date endTime1 = cal.getTime();
	    List list = new ArrayList();
	    int hj = 0;
	    int gz = 0;
	    int fk = 0;
	    int jg = 0;

	    int pb = 0;
	    int qt = 0;
	    for (String str : subburos)
	    {
	      System.err.println("str:" + str);
	      List list1 = this.service4.getAlarmDataByTime(str, startTime1, endTime1);

	      if ((list1 != null) && (list1.size() >= 1)) {
	        for (int i = 0; i < list1.size(); i++) {
	          if ("17".equals(String.valueOf(((EventDataInfo)list1.get(i)).getEventType())))
	            hj++;
	          else if ("18".equals(String.valueOf(((EventDataInfo)list1.get(i)).getEventType())))
	            gz++;
	          else if ("19".equals(String.valueOf(((EventDataInfo)list1.get(i)).getEventType())))
	            fk++;
	          else if ("20".equals(String.valueOf(((EventDataInfo)list1.get(i)).getEventType())))
	            jg++;
	          else if ("21".equals(String.valueOf(((EventDataInfo)list1.get(i)).getEventType())))
	            pb++;
	          else {
	            qt++;
	          }
	        }
	      }
	    }
	    Map map = new HashMap();
	    map.put("hj", Integer.valueOf(hj));
	    map.put("gz", Integer.valueOf(gz));
	    map.put("fk", Integer.valueOf(fk));
	    map.put("jg", Integer.valueOf(jg));
	    map.put("pb", Integer.valueOf(pb));
	    map.put("qt", Integer.valueOf(qt));
	    list.add(map);
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
	    } }
	 
	 
	 @RequestMapping(value={"/queryMapUsers.htm"}, produces={"application/json; charset=utf-8"})
	  @ResponseBody
	  public String queryMapUsers(HttpServletRequest request, HttpServletResponse response) {
	    response.setHeader("Access-Control-Allow-Origin", "http:localhost:3000");
	    response.setHeader("Access-Control-Allow-Credentials", "true");
	    /*Authentication auth = RequestContextSecurity.getAuthentication();
	    String buro = auth.getBureauNo();*/
	    String buro = request.getParameter("buro");//方元安 88000704 诸暨 88001488
		String org = request.getParameter("org");//方元安 88000704 诸暨 88000704
	    return this.service.queryMapUsers(buro).toString();
	  }
	 
	 
	 @RequestMapping({"/queryIFCSMonitor.htm"})
	  @ResponseBody
	  public IFCSMonitorResponse queryIFCSMonitor(IFCSMonitorRequest req, HttpServletRequest request, HttpServletResponse response) {
	    response.setHeader("Access-Control-Allow-Origin", "http:localhost:3000");
	    response.setHeader("Access-Control-Allow-Credentials", "true");
	    /*Authentication auth = RequestContextSecurity.getAuthentication();
	    String buro = auth.getBureauNo();*/
	    String buro = request.getParameter("buro");//方元安 88000704 诸暨 88001488
	  	String org = request.getParameter("org");//方元安 88000704 诸暨 88000704
	    return this.service.queryIFCSMonitor(req, buro);
	  }
	 
	
	 
	 @RequestMapping({"/mapList.htm"})
	  public void mapList(HttpServletRequest request, HttpServletResponse response)
	    throws UnsupportedEncodingException
	  {
	    request.setCharacterEncoding("utf-8");
	   /* Authentication auth = RequestContextSecurity.getAuthentication();
	    String buro = auth.getBureauNo();*/
	    String buro = request.getParameter("buro");//方元安 88000704 诸暨 88001488
		String org = request.getParameter("org");//方元安 88000704 诸暨 88000704
		
	    String name = request.getParameter("name");

	    List list = this.jumpTheUserService.mapList(name, buro);
	    try
	    {
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
	 
	 
	 @RequestMapping(value={"/breakdowninit.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String init(Model model) { 
		 return "/ifcs/xftj"; 
		 }


	 
	}


