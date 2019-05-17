package cn.yunrui.intfirectrlsys.action;

import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;
import cn.yunrui.intfirectrlsys.service.SmokeMapService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/smokeMap"})
public class SmokeMapComtrol
{

  @Resource(name="smokeMapService")
  private SmokeMapService SmokeMap;

  @RequestMapping(value={"/init.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String init(HttpServletRequest request, HttpServletResponse response)
  {
    return "ifcs/SmokeInduction";
  }
  

  //获取登录用户下的独立烟感联网单位
  @RequestMapping(value={"/querySmokeMap.htm"}, produces={"application/json; charset=utf-8"})
  @ResponseBody
  public String querySmokeMap()
  {
    Authentication auth = RequestContextSecurity.getAuthentication();
    String buro = auth.getBureauNo();
    String org = auth.getOrgNo();
    return this.SmokeMap.querySmokeMap(buro, org).toString(); } 
  
  @RequestMapping(value={"/querySmoke.htm"}, produces={"application/json; charset=utf-8"})
  @ResponseBody
  public String querySmoke() { Authentication auth = RequestContextSecurity.getAuthentication();
    String buro = auth.getBureauNo();
    return this.SmokeMap.querySmoke(buro).toString(); }

  @RequestMapping({"/getBureauNo.htm"})
  public void getBureauNo(HttpServletRequest request, HttpServletResponse response)
  {
    Authentication auth = RequestContextSecurity.getAuthentication();
    String bureauNo = auth.getBureauNo();
    String orgNo = auth.getOrgNo();
    ArrayList xfyhlist = (ArrayList)this.SmokeMap.getBureauNo(bureauNo, orgNo);

    String rst = JSONArray.fromObject(xfyhlist).toString();
    try {
      response.setContentType("application/json; charset=utf-8");

      response.getWriter().write(rst);
      response.getWriter().flush();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  @RequestMapping({"/getDutyOnDutyAtion.htm"})
  public void getDutyOnDutyAtion(HttpServletRequest request, HttpServletResponse response) {
    String deviceId = request.getParameter("re_xfyh_id");
    ArrayList xfyhlist = (ArrayList)this.SmokeMap.getDutyOnDutyAtion(deviceId);
    String rst = JSONArray.fromObject(xfyhlist).toString();
    try
    {
      response.setContentType("application/json; charset=utf-8");

      response.getWriter().write(rst);
      response.getWriter().flush();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  @RequestMapping({"/getDutyOnDutyAtion1.htm"})
  public void getDutyOnDutyAtion1(HttpServletRequest request, HttpServletResponse response) {
    ArrayList xfyhlist = (ArrayList)this.SmokeMap.getDutyOnDutyAtion1();
    String rst = JSONArray.fromObject(xfyhlist).toString();
    try {
      response.setContentType("application/json; charset=utf-8");

      response.getWriter().write(rst);
      response.getWriter().flush();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}