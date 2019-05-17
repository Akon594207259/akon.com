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
import cn.yunrui.intfirectrlsys.domain.Header;
import cn.yunrui.intfirectrlsys.domain.ResultMessage1;
import cn.yunrui.intfirectrlsys.dutyroomentity.CertificateEntity;
import cn.yunrui.intfirectrlsys.dutyroomentity.DutyRoomEntity;
import cn.yunrui.intfirectrlsys.dutyroomentity.PersonEntity;
import cn.yunrui.intfirectrlsys.entity.AttachmentBean;
import cn.yunrui.intfirectrlsys.service.DutyRoomService;
import cn.yunrui.intfirectrlsys.util.ExcelUtil;
import cn.yunrui.intfirectrlsys.util.Util;

@Controller
@RequestMapping("/dutyroommanager")
public class DutyRoomManagerControl {
	
	@RequestMapping("/init.htm")
	public String init(Model model){
		return "/ifcs/outfireIndex";
	}
	private static boolean iflocal = Util.ifbd;
	
	@Resource(name="dutyroommanagerservice") 
	private  DutyRoomService service;

	@RequestMapping(value = "/getXfyhListCommon.htm",method = RequestMethod.POST)
	public void getXfyhListCommon(HttpServletRequest request, HttpServletResponse response){	
		    Authentication auth = RequestContextSecurity.getAuthentication();		
            ArrayList<CodeEntity> xfyhlist =service.getXfyhListCommon(auth.getBureauNo(),auth.getOrgNo());		
  	        String rst = JSONArray.fromObject(xfyhlist).toString();  
			try {
				response.setContentType("application/json; charset=utf-8");
				if(iflocal){		
				  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
				  response.setHeader("Access-Control-Allow-Credentials", "true");
				}
				response.getWriter().write(rst);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	
	}
	
	@RequestMapping(value = "/getFixCompany.htm",method = RequestMethod.POST)
	public void getFixCompany(HttpServletRequest request, HttpServletResponse response){	
		String query_outfire_user=request.getParameter("query_outfire_user");		   		    
        List<String> fixCompanyList=service.getFixCompany(query_outfire_user);		
        String rst = JSONArray.fromObject(fixCompanyList).toString();
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	@RequestMapping(value = "/getDictionary.htm",method = RequestMethod.POST)
	public void getDictionary(HttpServletRequest request, HttpServletResponse response){	
		List<List<CodeEntity>> dictionaryList = service.getDictionary();   		    
        String rst = JSONArray.fromObject(dictionaryList).toString();  
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rst);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	@RequestMapping(value="/queryDutyRoomInfo.htm",method=RequestMethod.POST)
	public void queryDutyRoomInfo(HttpServletRequest request,HttpServletResponse response){
		String query_outfire_user=request.getParameter("query_outfire_user");
		int pageSize=Integer.parseInt(request.getParameter("pageSize"));
		int current=Integer.parseInt(request.getParameter("current"));
		int start=(current-1)*pageSize;
		int limit=pageSize;
		List<DutyRoomEntity> dutyRoomList = service.queryDutyRoomInfo(query_outfire_user,start,limit);
		String rst = JSONArray.fromObject(dutyRoomList).toString();  
		int total = this.service.queryDutyRoomInfoTotal(query_outfire_user);
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
	
	@RequestMapping(value = "/addDutyRoomInfo.htm",method = RequestMethod.POST)
	public void addDutyRoomInfo(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		//乱码问题解决方案
		request.setCharacterEncoding("utf-8");//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		//获取参数
		String id = request.getParameter("id");
		String query_outfire_user=request.getParameter("query_outfire_user");
		String fix_company=request.getParameter("fix_company");	
		String room_number=request.getParameter("room_number");	
		String is_network=request.getParameter("is_network");	
		String company_name=request.getParameter("company_name");	
		String is_main=request.getParameter("is_main");	
		String room_area=request.getParameter("room_area");	
		String room_department=request.getParameter("room_department");	
		String start_date=request.getParameter("start_date");	
		String room_location=request.getParameter("room_location");	
		System.out.println("dfsghdhfdhfdgfdgfdgfdgf"+id);
		String personDataSource=request.getParameter("personDataSource");	
		String certificateDataSource=request.getParameter("certificateDataSource");
		
		List<PersonEntity> personList=new ArrayList<PersonEntity>();
		JSONArray personArray = JSONArray.fromObject(personDataSource);
	    for (int i = 0; i < personArray.size(); i++) {
	      JSONObject jsonObject1 = personArray.getJSONObject(i);
	      personList.add((PersonEntity)JSONObject.toBean(jsonObject1, PersonEntity.class));
	    }
        
        List<CertificateEntity> certificateList=new ArrayList<CertificateEntity>();
        JSONArray certificateArray = JSONArray.fromObject(certificateDataSource);
        for (int i = 0; i < certificateArray.size(); i++) {
          JSONObject jsonObject2 = certificateArray.getJSONObject(i);
          certificateList.add((CertificateEntity)JSONObject.toBean(jsonObject2, CertificateEntity.class));
        }
        System.out.println("cvxnxgj"+JSONArray.fromObject(personList).toString());
        
        List<PersonEntity> pList = new ArrayList<PersonEntity>();
        for (int i = 0; i < personList.size(); i++) {
          PersonEntity pe = (PersonEntity)personList.get(i);
          PersonEntity pEntity = new PersonEntity();
          String uuid = pe.getId();
          String name = pe.getName();
          String IDNO = YunRuiCommonUtil.nullToEmpty(pe.getIdno());
          int age = getAge(IDNO);
          String edudegree = pe.getEdudegree();
          String phone = pe.getPhone();
          if ((("".equals(name)) && ("".equals(IDNO))) || ("".equals(uuid))) {
            continue;
          }
          pEntity.setId(uuid);
          pEntity.setName(name);
          pEntity.setIdno(IDNO);
          pEntity.setAge(age);
          System.out.println("asdg"+age);
          pEntity.setEdudegree(edudegree);
          pEntity.setPhone(phone);
          pEntity.setRoom_id(id);
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
          System.out.println("************"+JSONArray.fromObject(pEntity).toString());
          pList.add(pEntity);
        }
        
        DutyRoomEntity de = new DutyRoomEntity();
        de.setId(id);
        de.setQuery_outfire_user(query_outfire_user);
        de.setFix_company(fix_company);
        de.setRoom_number(room_number);
        de.setIs_network(is_network);
        de.setCompany_name(company_name);
        de.setIs_main(is_main);
        de.setRoom_area(room_area);
        de.setRoom_department(room_department);
        de.setStart_date(start_date);
        de.setRoom_location(room_location);
        de.setPersonList(pList);
        de.setCertificateList(certificateList);
        System.out.println("dfb12121324 "+JSONArray.fromObject(de).toString());
        ResultMessage1 rMessage=service.addDutyRoomInfo(de);
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rMessage.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	@RequestMapping(value = "/editDutyRoomInfo.htm",method = RequestMethod.POST)
	public void editDutyRoomInfo(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		//乱码问题解决方案
		request.setCharacterEncoding("utf-8");//必须写在第一位，因为采用这种方式去读取数据，否则数据会出错。 
		response.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码 
		//获取参数
		String id = request.getParameter("id");
		String query_outfire_user=request.getParameter("query_outfire_user");
		String fix_company=request.getParameter("fix_company");	
		String room_number=request.getParameter("room_number");	
		String is_network=request.getParameter("is_network");	
		String company_name=request.getParameter("company_name");	
		String is_main=request.getParameter("is_main");	
		String room_area=request.getParameter("room_area");	
		String room_department=request.getParameter("room_department");	
		String start_date=request.getParameter("start_date");	
		String room_location=request.getParameter("room_location");	

		String personDataSource=request.getParameter("personDataSource");	
		String certificateDataSource=request.getParameter("certificateDataSource");
		
		List<PersonEntity> personList=new ArrayList<PersonEntity>();
		JSONArray personArray = JSONArray.fromObject(personDataSource);
	    for (int i = 0; i < personArray.size(); i++) {
	      JSONObject jsonObject1 = personArray.getJSONObject(i);
	      personList.add((PersonEntity)JSONObject.toBean(jsonObject1, PersonEntity.class));
	    }
        
        List<CertificateEntity> certificateList=new ArrayList<CertificateEntity>();
        JSONArray certificateArray = JSONArray.fromObject(certificateDataSource);
        for (int i = 0; i < certificateArray.size(); i++) {
          JSONObject jsonObject2 = certificateArray.getJSONObject(i);
          certificateList.add((CertificateEntity)JSONObject.toBean(jsonObject2, CertificateEntity.class));
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
          String phone = pe.getPhone();

          if ((("".equals(name)) && ("".equals(IDNO))) || ("".equals(uuid))) {
            continue;
          }
          pEntity.setId(uuid);
          pEntity.setName(name);
          pEntity.setIdno(IDNO);
          pEntity.setAge(age);
          pEntity.setEdudegree(edudegree);
          pEntity.setPhone(phone);
          pEntity.setRoom_id(id);
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
        
        DutyRoomEntity dutyRoomEntity = new DutyRoomEntity();
        dutyRoomEntity.setId(id);
        dutyRoomEntity.setQuery_outfire_user(query_outfire_user);
        dutyRoomEntity.setFix_company(fix_company);
        dutyRoomEntity.setRoom_number(room_number);
        dutyRoomEntity.setIs_network(is_network);
        dutyRoomEntity.setCompany_name(company_name);
        dutyRoomEntity.setIs_main(is_main);
        dutyRoomEntity.setRoom_area(room_area);
        dutyRoomEntity.setRoom_department(room_department);
        dutyRoomEntity.setStart_date(start_date);
        dutyRoomEntity.setRoom_location(room_location);
        dutyRoomEntity.setPersonList(pList);
        dutyRoomEntity.setCertificateList(certificateList);
        ResultMessage1 rMessage=service.editDutyRoomInfo(dutyRoomEntity);
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rMessage.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	@RequestMapping (value="/deleteDutyRoom.htm", method=RequestMethod.POST)
	public void deleteDutyRoom(HttpServletRequest request,HttpServletResponse response){
	    String id = request.getParameter("id");
		ResultMessage1 rMessage=service.deleteDutyRoom(id);
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rMessage.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	@RequestMapping(value="queryDetailInfo.htm",method=RequestMethod.POST)
	public void queryDetailInfo(HttpServletRequest request,HttpServletResponse response){
		String id = request.getParameter("id");
		boolean isEdit = Boolean.valueOf(request.getParameter("isEdit")).booleanValue();
		DutyRoomEntity de = service.queryDetailInfo(id,isEdit);
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(de.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	@RequestMapping(value = "/exportOutfire.htm", method = RequestMethod.GET)
    public void exportOutfire(HttpServletRequest request, HttpServletResponse response){				
	    HashMap<String,String> param = Util.getrequestParams(request);
	    String query_outfire_user = param.get("query_outfire_user");
	    String filename = "消防用户消控室查询";
		List<Header> header = this.getExportOutfireHeader();
		List list = this.service.exportOutfire(query_outfire_user);
		String rst = JSONArray.fromObject(list).toString();  
		try {
			ExcelUtil.toExcel(filename, header,list, false, response);
		} catch (IOException e) {
			e.printStackTrace();
		}				
    }
	
	@RequestMapping (value="/deletePerson.htm", method=RequestMethod.POST)
	public void deletePerson(HttpServletRequest request,HttpServletResponse response){
		String id = request.getParameter("id");
		ResultMessage1 rMessage=this.service.deletePerson(id);
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rMessage.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	@RequestMapping (value="/deleteAttchInfo.htm", method=RequestMethod.POST)
	public void deleteAttchInfo(HttpServletRequest request,HttpServletResponse response){
		String attachType = request.getParameter("attachType");
		String attachId = request.getParameter("attachId");
		ResultMessage1 rMessage=this.service.deleteAttchInfo(attachType,attachId);
		try {
			response.setContentType("application/json; charset=utf-8");
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(rMessage.toJson());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	@RequestMapping(value="queryPictureInfo.htm",method=RequestMethod.POST)
	public void queryPictureInfo(HttpServletRequest request,HttpServletResponse response)
	  throws Exception
	{
	  HashMap<String, Object> resultMap = new HashMap();
	  List<Map<String, Object>> resultList = new ArrayList();
	  resultMap.put("total", Integer.valueOf(0));
	  resultMap.put("results", resultList);
	  String attachType = request.getParameter("attachType");
	  String attachId = request.getParameter("attachId");
	  if ((!YunRuiCommonUtil.nullToEmpty(attachId).equals("")) && (!YunRuiCommonUtil.nullToEmpty(attachType).equals("")))
	  {
	    List<AttachmentBean> list = this.service.queryPictureList(attachType, attachId);
	    for (int i = 0; i < list.size(); i++)
	    {
	      Map<String, Object> dataMap = new HashMap();
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
			if(iflocal){		
			  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8888");
			  response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.getWriter().write(JSONArray.fromObject(resultMap).toString());
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    public List<Header> getExportOutfireHeader(){
		List<Header> header = new ArrayList<Header>();
		String names[] = new String[]{"序号","所属运维公司","单位名称","消控室编号","主/从","位置"};
		String index[] = new String[]{"index","fix_company","company_name","room_number","is_main","room_location"};
		for (int i=0;i<names.length;i++){
			Header hd = new Header();
			hd.setName(names[i]);
			hd.setDataIndex(index[i]);
			hd.setWidth(80);
			header.add(hd);		
		}
		return header;
    }
   
    @RequestMapping(value={"/deletePer_Cer.htm"}, method=RequestMethod.POST)
    public void deletePer_Cer(HttpServletRequest request, HttpServletResponse response)
    {
      String personId = request.getParameter("personId");
      String certificateId = request.getParameter("certificateId");
      System.out.println("personId"+personId+"certificateId"+certificateId);
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
    
    private int getAge(String IDNO){
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
