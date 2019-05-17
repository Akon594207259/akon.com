package cn.yunrui.intfirectrlsys.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import cn.yunrui.common.util.YunRuiCommonUtil;
import cn.yunrui.intfirectrlsys.domain.CodeEntity;
import cn.yunrui.intfirectrlsys.domain.ResultMessage1;
import cn.yunrui.intfirectrlsys.entity.AttachmentBean;
import cn.yunrui.intfirectrlsys.minifirestationentity.CertificateEntity;
import cn.yunrui.intfirectrlsys.minifirestationentity.EquipmentEntity;
import cn.yunrui.intfirectrlsys.minifirestationentity.FireAttachEntity;
import cn.yunrui.intfirectrlsys.minifirestationentity.FireStationEntity;
import cn.yunrui.intfirectrlsys.minifirestationentity.PersonEntity;
import cn.yunrui.intfirectrlsys.minifirestationentity.StationEntity;
import cn.yunrui.intfirectrlsys.service.MiniFireStationService;
import cn.yunrui.intfirectrlsys.util.ExcelUtil;
import cn.yunrui.intfirectrlsys.util.Header;
import cn.yunrui.intfirectrlsys.util.Util;

@Controller
@RequestMapping({"/minifirestation"})
public class MiniFireStationControl
{
  private static boolean iflocal = Util.ifbd;

  @Resource(name="minifirestationservice")
  private MiniFireStationService service;

  @RequestMapping({"/init.htm"})
  public String init(Model model)
  {
    return "/ifcs/miniFireStationIndex";
  }

  @RequestMapping(value={"/initObjectList.htm"}, method=RequestMethod.POST)
  public void initObjectList(HttpServletRequest request, HttpServletResponse response)
  {
    String type = request.getParameter("type");
    List<CodeEntity> objectList = new ArrayList<CodeEntity>();
    if ("0".equals(type)) {
      objectList = service.initCommunityObject();
    } else {
      Authentication auth = RequestContextSecurity.getAuthentication();
      objectList = service.initCompanyObject(auth.getBureauNo(), auth.getOrgNo());
    }
    String rst = JSONArray.fromObject(objectList).toString();
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

  @RequestMapping(value={"queryMiniFireStation.htm"}, method=RequestMethod.POST)
  public void queryMiniFireStation(HttpServletRequest request, HttpServletResponse response)
  {
    String type_key = request.getParameter("type_key");
    String object_key = request.getParameter("object_key");
    int pageSize = Integer.parseInt(request.getParameter("pageSize"));
    int current = Integer.parseInt(request.getParameter("current"));
    int start = (current - 1) * pageSize;
    int limit = pageSize;
    List<StationEntity> stationList = service.queryMiniFireStation(type_key, object_key, start, limit);
    String rst = JSONArray.fromObject(stationList).toString();
    int total = service.queryMiniFireStationTotal(type_key, object_key);
    String result = "{\"data\": " + rst + ", \"current\":" + current + ",\"total\":" + total + "}";
    try {
      response.setContentType("application/json; charset=utf-8");
      if (iflocal) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
        response.setHeader("Access-Control-Allow-Credentials", "true");
      }
      response.getWriter().write(result);
      response.getWriter().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @RequestMapping(value={"/addMiniFireStation.htm"}, method=RequestMethod.POST)
  public void addMiniFireStation(HttpServletRequest request, HttpServletResponse response)
    throws UnsupportedEncodingException
  {
    request.setCharacterEncoding("utf-8");
    response.setContentType("text/html;charset=utf-8");

    String id = request.getParameter("id");
    String type_key = request.getParameter("type_key");
    String object_key = request.getParameter("object_key");
    String object_name = request.getParameter("object_name");
    String station_number = request.getParameter("station_number");
    String station_name = request.getParameter("station_name");
    String station_area = request.getParameter("station_area");
    String start_date = request.getParameter("start_date");
    String station_location = request.getParameter("station_location");

    String personDataSource = request.getParameter("personDataSource");
    String certificateDataSource = request.getParameter("certificateDataSource");
    String equipmentDataSource = request.getParameter("equipmentDataSource");

    List<PersonEntity> personList = new ArrayList<PersonEntity>();
    JSONArray personArray = JSONArray.fromObject(personDataSource);
    for (int i = 0; i < personArray.size(); i++) {
      JSONObject jsonObject1 = personArray.getJSONObject(i);
      personList.add((PersonEntity)JSONObject.toBean(jsonObject1, PersonEntity.class));
    }

    List<CertificateEntity> certificateList = new ArrayList<CertificateEntity>();
    JSONArray certificateArray = JSONArray.fromObject(certificateDataSource);
    for (int i = 0; i < certificateArray.size(); i++) {
      JSONObject jsonObject2 = certificateArray.getJSONObject(i);
      certificateList.add((CertificateEntity)JSONObject.toBean(jsonObject2, CertificateEntity.class));
    }
    
    List<EquipmentEntity> equipmentList = new ArrayList<EquipmentEntity>();
    JSONArray equipmentArray = JSONArray.fromObject(equipmentDataSource);
    for (int i = 0; i < equipmentArray.size(); i++) {
      JSONObject jsonObject3 = equipmentArray.getJSONObject(i);
      equipmentList.add((EquipmentEntity)JSONObject.toBean(jsonObject3, EquipmentEntity.class));
    }

    List<PersonEntity> pList = new ArrayList<PersonEntity>();
    for (int i = 0; i < personList.size(); i++) {
      PersonEntity pe = (PersonEntity)personList.get(i);
      PersonEntity pEntity = new PersonEntity();
      String uuid = pe.getId();
      String name = pe.getName();
      String IDNO = YunRuiCommonUtil.nullToEmpty(pe.getIdno());
      int age = getAge(IDNO);
      String edudegree = pe.getEdudegree();
      String post = pe.getPost();
      String phone = pe.getPhone();

      if ((("".equals(name)) && ("".equals(IDNO))) || ("".equals(uuid))) {
        continue;
      }
      pEntity.setId(uuid);
      pEntity.setName(name);
      pEntity.setIdno(IDNO);
      pEntity.setAge(String.valueOf(age));
      pEntity.setPost(post);
      pEntity.setEdudegree(edudegree);
      pEntity.setPhone(phone);
      pEntity.setStation_id(id);
      for (int j = 0; j < certificateList.size(); j++) {
        CertificateEntity ce = (CertificateEntity)certificateList.get(j);
        String certificate_name = ce.getCertificate_name();
        String certificate_number = ce.getCertificate_number();
        String certificate_level = ce.getCertificate_level();
        if ((uuid.equals(certificate_name)) && (!"".equals(certificate_number))) {
          pEntity.setCertificate_number(certificate_number);
          pEntity.setCertificate_level(certificate_level);
          break;
        }
      }
      pList.add(pEntity);
    }
    System.out.println("dfh"+JSONArray.fromObject(pList).toString());
    FireStationEntity fe = new FireStationEntity();
    fe.setId(id);
    fe.setType_key(type_key);
    fe.setStation_number(station_number);
    fe.setStation_name(station_name);
    fe.setObject_key(object_key);
    fe.setObject_name(object_name);
    fe.setStation_location(station_location);
    fe.setStation_area(station_area);
    fe.setStart_date(start_date);
    fe.setPersonList(pList);
    fe.setEquipmentList(equipmentList);
    ResultMessage1 rMessage = service.addMiniFireStation(fe);
    try {
      response.setContentType("application/json; charset=utf-8");
      if (iflocal) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
        response.setHeader("Access-Control-Allow-Credentials", "true");
      }
      response.getWriter().write(rMessage.toJson());
      response.getWriter().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @RequestMapping(value={"/editMiniFireStation.htm"}, method=RequestMethod.POST)
  public void editMiniFireStation(HttpServletRequest request, HttpServletResponse response)
    throws UnsupportedEncodingException
  {
    request.setCharacterEncoding("utf-8");
    response.setContentType("text/html;charset=utf-8");

    String id = request.getParameter("id");
    String type_key = request.getParameter("type_key");
    String object_key = request.getParameter("object_key");
    String object_name = request.getParameter("object_name");
    String station_number = request.getParameter("station_number");
    String station_name = request.getParameter("station_name");
    String station_area = request.getParameter("station_area");
    String start_date = request.getParameter("start_date");
    String station_location = request.getParameter("station_location");

    String personDataSource = request.getParameter("personDataSource");
    String certificateDataSource = request.getParameter("certificateDataSource");
    String equipmentDataSource = request.getParameter("equipmentDataSource");

    List<PersonEntity> personList = new ArrayList<PersonEntity>();
    JSONArray personArray = JSONArray.fromObject(personDataSource);
    for (int i = 0; i < personArray.size(); i++) {
      JSONObject jsonObject1 = personArray.getJSONObject(i);
      personList.add((PersonEntity)JSONObject.toBean(jsonObject1, PersonEntity.class));
    }

    List<CertificateEntity> certificateList = new ArrayList<CertificateEntity>();
    JSONArray certificateArray = JSONArray.fromObject(certificateDataSource);
    for (int i = 0; i < certificateArray.size(); i++) {
      JSONObject jsonObject2 = certificateArray.getJSONObject(i);
      certificateList.add((CertificateEntity)JSONObject.toBean(jsonObject2, CertificateEntity.class));
    }

    List<EquipmentEntity> equipmentList = new ArrayList<EquipmentEntity>();
    JSONArray equipmentArray = JSONArray.fromObject(equipmentDataSource);
    for (int i = 0; i < equipmentArray.size(); i++) {
      JSONObject jsonObject3 = equipmentArray.getJSONObject(i);
      equipmentList.add((EquipmentEntity)JSONObject.toBean(jsonObject3, EquipmentEntity.class));
    }

    List<PersonEntity> pList = new ArrayList<PersonEntity>();
    for (int i = 0; i < personList.size(); i++) {
      PersonEntity pe = (PersonEntity)personList.get(i);
      PersonEntity pEntity = new PersonEntity();
      String uuid = pe.getId();
      String name = pe.getName();
      String IDNO = YunRuiCommonUtil.nullToEmpty(pe.getIdno());
      int age = getAge(IDNO);
      String edudegree = pe.getEdudegree();
      String post = pe.getPost();
      String phone = pe.getPhone();

      if ((("".equals(name)) && ("".equals(IDNO))) || ("".equals(uuid))) {
        continue;
      }
      pEntity.setId(uuid);
      pEntity.setName(name);
      pEntity.setIdno(IDNO);
      pEntity.setAge(String.valueOf(age));
      pEntity.setPost(post);
      pEntity.setEdudegree(edudegree);
      pEntity.setPhone(phone);
      pEntity.setStation_id(id);
      for (int j = 0; j < certificateList.size(); j++) {
        CertificateEntity ce = (CertificateEntity)certificateList.get(j);
        String certificate_name = ce.getCertificate_name();
        String certificate_number = ce.getCertificate_number();
        String certificate_level = ce.getCertificate_level();
        if ((id.equals(certificate_name)) && (!"".equals(certificate_number))) {
          pEntity.setCertificate_number(certificate_number);
          pEntity.setCertificate_level(certificate_level);
          break;
        }
      }
      pList.add(pEntity);
    }
    
    FireStationEntity fe = new FireStationEntity();
    fe.setId(id);
    fe.setType_key(type_key);
    fe.setStation_number(station_number);
    fe.setStation_name(station_name);
    fe.setObject_key(object_key);
    fe.setObject_name(object_name);
    fe.setStation_location(station_location);
    fe.setStation_area(station_area);
    fe.setStart_date(start_date);
    fe.setPersonList(pList);
    fe.setEquipmentList(equipmentList);
    ResultMessage1 rMessage = service.editMiniFireStation(fe);
    try {
      response.setContentType("application/json; charset=utf-8");
      if (iflocal) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
        response.setHeader("Access-Control-Allow-Credentials", "true");
      }
      response.getWriter().write(rMessage.toJson());
      response.getWriter().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @RequestMapping(value={"/deleteMiniFireStation.htm"}, method=RequestMethod.POST)
  public void deleteMiniFireStation(HttpServletRequest request, HttpServletResponse response)
  {
    String yw_type = "minifirestation_attachment";
    String id = request.getParameter("id");
    ResultMessage1 rMessage = service.deleteMiniFireStation(yw_type, id);
    try {
      response.setContentType("application/json; charset=utf-8");
      if (iflocal) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
        response.setHeader("Access-Control-Allow-Credentials", "true");
      }
      response.getWriter().write(rMessage.toJson());
      response.getWriter().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @RequestMapping(value={"/queryDetail.htm"}, method=RequestMethod.POST)
  public void queryDetail(HttpServletRequest request, HttpServletResponse response)
  {
    String id = request.getParameter("id");
    boolean isEdit = Boolean.valueOf(request.getParameter("isEdit")).booleanValue();
    FireStationEntity fe = service.queryDetail(id, isEdit);
    try {
      response.setContentType("application/json;charset=utf-8");
      if (iflocal) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
        response.setHeader("Access-Control-Allow-Credentials", "true");
      }
      response.getWriter().write(fe.toJson());
      response.getWriter().flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @RequestMapping(value={"/exportMiniFireStation.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public void exportMiniFireStation(HttpServletRequest request, HttpServletResponse response)
  {
    String type_key = request.getParameter("type_key");
    String object_key = request.getParameter("object_key");
    String filename = "微型消防站信息表";
    List<Header> header = getExportHeaders(type_key);
    List list = service.exportMiniFireStation(type_key, object_key);
    try {
      ExcelUtil.toExcel(filename, header, list, false, response);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @RequestMapping(value={"/queryAttach.htm"}, method=RequestMethod.POST)
  public void queryAttach(HttpServletRequest request, HttpServletResponse response)
  {
	  System.out.println("fgdfg");
    String id = request.getParameter("id");
    System.out.println("fgdfg"+id);
    List<FireAttachEntity> attachList = service.queryAttach(id);
    String rst = JSONArray.fromObject(attachList).toString();
    try {
      response.setContentType("application/json;charset=utf-8");
      if (iflocal) {
        response.setHeader("Access-Control-Allow-Orgin", "http://localhost:8888");
        response.setHeader("Access-Control-Allow-Credentials", "true");
      }
      response.getWriter().write(rst);
      response.getWriter().flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @RequestMapping(value={"/returnDelete.htm"}, method=RequestMethod.POST)
  public void returnDelete(HttpServletRequest request, HttpServletResponse response)
  {
	ResultMessage1 rMessage = new ResultMessage1();
    rMessage.setMessage("删除附件失败");
    rMessage.setStatus("-1");
    
    String yw_type = "minifirestation_attachment";
    String id = request.getParameter("id");
//    boolean isEdit = Boolean.valueOf(request.getParameter("isEdit"));
    String certificateDataSource = request.getParameter("certificateDataSource");
    
    List<CertificateEntity> certificateList = new ArrayList<CertificateEntity>();
    JSONArray certificateArray = JSONArray.fromObject(certificateDataSource);
    for (int i = 0; i < certificateArray.size(); i++) {
      JSONObject jsonObject = certificateArray.getJSONObject(i);
      certificateList.add((CertificateEntity)JSONObject.toBean(jsonObject, CertificateEntity.class));
    }
    
    service.deleteAttach(yw_type, YunRuiCommonUtil.nullToEmpty(id));
    for (int i = 0; i < certificateList.size(); i++) {
	      String uuid = ((CertificateEntity)certificateList.get(i)).getCertificate_name();
	      if (YunRuiCommonUtil.nullToEmpty(uuid).equals(uuid)) {
	        service.deleteAttach(yw_type, uuid);
	      }
    }
    /*if(isEdit){
    	List<String> pList = service.getPeople(id);
    	for (int i = 0; i < certificateList.size(); i++) {
    	      String uuid = ((CertificateEntity)certificateList.get(i)).getCertificate_name();
    	      if (YunRuiCommonUtil.nullToEmpty(uuid).equals(uuid) && !pList.contains(uuid)) {
    	        service.deleteAttach(yw_type, uuid);
    	      }
	    }
    }else {
    	for (int i = 0; i < certificateList.size(); i++) {
    	      String uuid = ((CertificateEntity)certificateList.get(i)).getCertificate_name();
    	      if (YunRuiCommonUtil.nullToEmpty(uuid).equals(uuid)) {
    	        service.deleteAttach(yw_type, uuid);
    	      }
	    }
    }*/
    rMessage.setMessage("删除附件成功");
    rMessage.setStatus("1");
    try {
      response.setContentType("application/json; charset=utf-8");
      if (iflocal) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
        response.setHeader("Access-Control-Allow-Credentials", "true");
      }
      response.getWriter().write(rMessage.toJson());
      response.getWriter().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @RequestMapping(value={"queryPictureInfo.htm"}, method=RequestMethod.POST)
  public void queryPictureInfo(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    HashMap<String, Object> resultMap = new HashMap<String, Object>();
    List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
    resultMap.put("total", Integer.valueOf(0));
    resultMap.put("results", resultList);
    String attachType = request.getParameter("attachType");
    String attachId = request.getParameter("attachId");
    if ((!YunRuiCommonUtil.nullToEmpty(attachId).equals("")) && (!YunRuiCommonUtil.nullToEmpty(attachType).equals("")))
    {
      List<AttachmentBean> list = service.queryPictureList(attachType, attachId);
      for (int i = 0; i < list.size(); i++)
      {
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("uuid", ((AttachmentBean)list.get(i)).getUuid());
        dataMap.put("name", ((AttachmentBean)list.get(i)).getFileName());
        dataMap.put("url", ((AttachmentBean)list.get(i)).getAttachUrl());
        resultList.add(dataMap);
      }
      resultMap.put("total", Integer.valueOf(list.size()));
    }
    resultMap.put("results", resultList);
    try {
      response.setContentType("application/json; charset=utf-8");
      if (iflocal) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
        response.setHeader("Access-Control-Allow-Credentials", "true");
      }
      response.getWriter().write(JSONArray.fromObject(resultMap).toString());
      response.getWriter().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @RequestMapping(value={"/deleteAttchInfo.htm"}, method=RequestMethod.POST)
  public void deleteAttchInfo(HttpServletRequest request, HttpServletResponse response)
  {
    String attachType = request.getParameter("attachType");
    String attachId = request.getParameter("attachId");
    ResultMessage1 rMessage = service.deleteAttchInfo(attachType, attachId);
    try {
      response.setContentType("application/json; charset=utf-8");
      if (iflocal) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
        response.setHeader("Access-Control-Allow-Credentials", "true");
      }
      response.getWriter().write(rMessage.toJson());
      response.getWriter().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @RequestMapping(value={"/deletePer_Cer.htm"}, method=RequestMethod.POST)
  public void deletePer_Cer(HttpServletRequest request, HttpServletResponse response)
  {
    String personId = request.getParameter("personId");
    String certificateId = request.getParameter("certificateId");
    ResultMessage1 rMessage = service.deletePer_Cer(personId,certificateId);
    try {
      response.setContentType("application/json; charset=utf-8");
      if (iflocal) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
        response.setHeader("Access-Control-Allow-Credentials", "true");
      }
      response.getWriter().write(rMessage.toJson());
      response.getWriter().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private List<Header> getExportHeaders(String type_key)
  {
    List<Header> header = new ArrayList<Header>();
    String[] names = { "序号", type_key.equals("0") ? "所属社区" : "所属单位", "编号", "名称", type_key.equals("0") ? "地址" : "位置", "人员数量", "站长", "联系方式" };
    String[] indexs = { "index", "type_name", "fire_station_number", "fire_station_name", "fire_station_location", "people_count", "fire_station_leader", "fire_station_phone" };
    for (int i = 0; i < names.length; i++) {
      Header hd = new Header();
      hd.setName(names[i]);
      hd.setDataIndex(indexs[i]);
      hd.setWidth(80);
      header.add(hd);
    }
    return header;
  }
  
  private int getAge(String IDNO)
  {
    int age = 0;
    if (("".equals(IDNO)) || (IDNO == null) || (IDNO.length() < 10)) {
      return age;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
    Date date = new Date();
    int year = Integer.valueOf(sdf.format(date)).intValue();
    int birthYear = Integer.valueOf(IDNO.substring(6, 10)).intValue();
    age = year - birthYear;
    return age;
  }
}