/*package cn.yunrui.intfirectrlsys.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import cn.cc.cisp.code.entity.Code;
import cn.cc.cisp.code.util.CodeUtil;
import cn.cc.cisp.nostruct.entity.NostructHead;
import cn.cc.cisp.nostruct.manager.FileManagerFactory;
import cn.cc.cisp.nostruct.manager.IFileManager;
import cn.cc.cisp.nostruct.manager.NostructException;
import cn.cc.cisp.priv.util.OrgUtil;
import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;
import cn.cc.cisp.util.StringUtils;
import cn.yunrui.common.entity.AttachmentBean;
import cn.yunrui.common.service.YunRuiCommonService;
import cn.yunrui.common.util.UUID;
import cn.yunrui.common.util.YunRuiCommonUtil;
import cn.yunrui.intfirectrlsys.util.DownLoadManager;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping("/ifcsOssAttachment")
public class IFcsOssAttachmentAction {

	public static final String ATTACHMENT = "ATTACHMENT";
	private static final String ATTACH_TYPE = "ATTACH_TYPE";
	private static final String CODETYPE_ENV_TYPE = "ENV_TYPE";
	private static final String IS_PRODUCE = "IS_PRODUCE";
	
	private static Logger logger = Logger.getLogger(IFcsOssAttachmentAction.class);  

	@RequestMapping("/init.htm")
	public String init(Model model){
		Authentication auth = RequestContextSecurity.getAuthentication();
		model.addAttribute("isBuro", !"".equals(YunRuiCommonUtil.nullToEmpty(OrgUtil.getOrg(auth.getBureauNo()).getParentNo())));
		model.addAttribute("buro", auth.getBureauNo());
		return "loginBasis/picImpMain";
	}
	
	@Resource(name="yunRuiCommonService")
	private YunRuiCommonService commonService;

	private static IFileManager fileManager = FileManagerFactory.getInstance().getFileManager("2");
	
	@RequestMapping(value="/getAttachIdsList.htm",produces="text/plain;charset=UTF-8")
	public @ResponseBody String getAttachIdsList(){
		List<Code> codeList = CodeUtil.getCodeList(ATTACH_TYPE);

		List<HashMap<String,String>> resultList = new ArrayList<HashMap<String,String>>();
		List<HashMap<String,String>> optionList = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> resultMap;
		for(int i=0;i<codeList.size();i++){
			resultMap = new HashMap<String,String>();
			resultMap.put("id", codeList.get(i).getValue());
			resultMap.put("name", codeList.get(i).getName());
			resultMap.put("content1", codeList.get(i).getContent1());
			resultMap.put("content2", codeList.get(i).getContent2());
			resultMap.put("content3", codeList.get(i).getContent3());
			optionList.add(resultMap);
		}
		if (optionList.size()!=1){
			resultMap = new HashMap<String,String>();
			resultMap.put("id", "");
			resultMap.put("name", "全部");
			resultMap.put("content1", "");
			resultMap.put("content2", "");
			resultMap.put("content3", "");
			resultList.add(resultMap);
		}
		resultList.addAll(optionList);
		JsonConfig jsonConfig = new JsonConfig();
		return JSONArray.fromObject(resultList,jsonConfig).toString();
	}
	@RequestMapping(value="/queryAttachmentData.htm",produces="text/plain;charset=UTF-8")
	public @ResponseBody String queryAttachmentData(HttpServletRequest request,HttpSession httpSession) throws Exception{
		HashMap<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		resultMap.put("total", 0);
		resultMap.put("results", resultList);
		JsonConfig jsonConfig = new JsonConfig();
		String attachType=request.getParameter("attachType");
		String attachId=request.getParameter("attachId");
		String startStr=YunRuiCommonUtil.nullToEmpty(request.getParameter("start"));
		String limitStr=YunRuiCommonUtil.nullToEmpty(request.getParameter("limit"));
		int start=-1;
		int limit=-1;
		if(!"".equals(startStr)){
			start=Integer.valueOf(startStr);
		}
		if(!"".equals(limitStr)){
			limit=Integer.valueOf(limitStr);
		}
		if (!YunRuiCommonUtil.nullToEmpty(attachId).equals("") 
				&& !YunRuiCommonUtil.nullToEmpty(attachType).equals("")) {
			List<AttachmentBean> list=commonService.queryAttachmentList(attachType, attachId, start, limit);
			Map<String,Object> dataMap;
			for(int i=0;i<list.size();i++){
				dataMap = new HashMap<String,Object>();
				dataMap.put("uuid", list.get(i).getUuid());
				dataMap.put("name", list.get(i).getFileName());
				dataMap.put("url", list.get(i).getAttachUrl());
				resultList.add(dataMap);
			}
			resultMap.put("total", list.size());
		}
		resultMap.put("results", resultList);
		return JSONArray.fromObject(resultMap,jsonConfig).toString();
	}
	

	
	@RequestMapping(value="/saveAttachment.htm",method = RequestMethod.POST,produces="text/html;charset=UTF-8")
	public @ResponseBody String saveAttachment(@RequestParam("file") CommonsMultipartFile file,HttpServletRequest request,HttpSession httpSession){
		HashMap<String,Object> resultMap = new HashMap<String,Object>();
		JsonConfig jsonConfig = new JsonConfig();
		resultMap.put("result", false);
		resultMap.put("msg", "上传失败");
		String attachId=(String)request.getParameter("attachId");
		String attachType=(String)request.getParameter("attachType");
		String dirId = getDirIdByAttachId(attachType, attachId);
		String filename=file.getOriginalFilename();
		String name = filename;
		String ext = "";
		try{
			name = filename.substring(0, filename.lastIndexOf("."));
		    ext = filename.substring(filename.lastIndexOf(".") + 1);
		}catch(Exception e){
			e.printStackTrace();
		}
		Authentication auth = RequestContextSecurity.getAuthentication();
		try {
			String fileId=fileManager.saveFile(file.getBytes(), dirId, ext, name, auth.getUserName(), filename);
			Code code=null;
			if(ATTACH_TYPE.equals(YunRuiCommonUtil.nullToEmpty(attachType))){
				List<Code> codeList = CodeUtil.getCodeList(ATTACH_TYPE);
				for(int i=0;i<codeList.size();i++){
					if(attachId.split("-")[0].equals(codeList.get(i).getValue())){
						code=codeList.get(i);
					}
				}
			}
			
			String url=YunRuiCommonUtil.nullToEmpty(fileManager.getImageUrl(fileId, 500));
			if("T".equals(CodeUtil.getCodeName(CODETYPE_ENV_TYPE, IS_PRODUCE))){
				url=StringUtils.replace(url, "-internal", "");
			}
			if(code!=null){
				boolean flagW=!"".equals(YunRuiCommonUtil.nullToEmpty(code.getContent1())) 
						&& !"-1".equals(YunRuiCommonUtil.nullToEmpty(code.getContent1())) 
						&& !"0".equals(YunRuiCommonUtil.nullToEmpty(code.getContent1()));
				boolean flagH=!"".equals(YunRuiCommonUtil.nullToEmpty(code.getContent2())) 
						&& !"-1".equals(YunRuiCommonUtil.nullToEmpty(code.getContent2())) 
						&& !"0".equals(YunRuiCommonUtil.nullToEmpty(code.getContent2()));
				url=url.replace(",w_500,limit_1",(flagW?",w_"+code.getContent1():"")
						+(flagH?",m_fixed,h_"+code.getContent2():"")+",limit_0");
			}
		    logger.debug("url=="+url);
			String uuid=new UUID().toString();
			if(commonService.addAttachment(uuid,attachType, attachId, url,fileId,name)){
				resultMap.put("uuid", uuid);
				resultMap.put("name", name);
				resultMap.put("result", true);
				resultMap.put("msg", "上传成功");
			}else{
				fileManager.remove(fileId, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getMessage().equals("存在同名的文件")){
				resultMap.put("msg", e.getMessage());
			}
			return JSONArray.fromObject(resultMap,jsonConfig).toString();
		}
		return JSONArray.fromObject(resultMap,jsonConfig).toString();
	}
	
	@RequestMapping(value="/downAttach.htm")
	public @ResponseBody String downAttach(String attachType,String attachId,String uuid,HttpServletRequest request){
		try{
			//System.out.println("downAttach-id"+id);
			AttachmentBean bean =commonService.queryAttachmentByUuid(attachType, attachId, uuid);
			if(!"".equals(YunRuiCommonUtil.nullToEmpty(bean.getFileId()))){
			    NostructHead localNostructHead = fileManager.getFileById(bean.getFileId());
			    byte[] arrayOfByte = fileManager.getContentByFileId(bean.getFileId());
				String url= DownLoadManager.getInstance().downLoadFile(arrayOfByte, localNostructHead.getName(), localNostructHead.getExt());
				return "{\"result\":true,\"url\":\""+request.getContextPath()+url+"\"}";
			}else{
				return "{\"result\":false}";
			}
		}catch (Exception e) {
			e.printStackTrace();
			return "{\"result\":false}";
		}
	}
	
	@RequestMapping(value="/deleteAttach.htm")
	public @ResponseBody String deleteAttach(String attachType,String attachId,String uuids,HttpServletResponse response){
		boolean result=false;
		try{
			String[] idArray = uuids.split(",");
			for (String uuid : idArray){
				AttachmentBean bean =commonService.queryAttachmentByUuid(attachType, attachId, uuid);
				if(!"".equals(YunRuiCommonUtil.nullToEmpty(bean.getFileId()))){
					fileManager.remove(bean.getFileId(), true);
				}
		    }
			result=commonService.delAttachment(attachType,attachId,uuids);
			return "{\"result\":"+result+"}";
		}catch (Exception e) {
			e.printStackTrace();
			return "{\"result\":"+result+"}";
		}
	}
	

	private String getDirIdByAttachId(String attachType, String attachId) {
		boolean isExistFolder = fileManager.isExistFolderKeyword(null,
				attachType);
		String parentFolderId = null;
		if (isExistFolder) {
			parentFolderId = fileManager.getFolderIdByKeyword(null,
					attachType);
		} else {
			parentFolderId = fileManager.createFolder(CodeUtil.getCodeName(ATTACHMENT, attachType), null,
					attachType);
		}
		boolean isExist = fileManager.isExistFolderKeyword(parentFolderId,
				attachId);
		if (isExist) {
			return fileManager.getFolderIdByKeyword(parentFolderId, attachId);
		} else {
			// 如果为空，则先创建一个目录
			return saveFolder(parentFolderId, CodeUtil.getCodeName(ATTACHMENT, attachType) + "(" + attachId + ")",
					attachId);
		}
	}
	
	private String saveFolder(String parentFolderId, String name, String keyword) {
		String folderId = null;
		try {
			folderId = fileManager.createFolder(name, parentFolderId, keyword);
		} catch (NostructException e) {
			//Browser.execClientScript("showAlert(\"" + e.getMessage() + "\");");
		}
		return folderId;
	}
}*/