/*package cn.yunrui.intfirectrlsys.action;

import cn.cc.cisp.code.entity.Code;
import cn.cc.cisp.code.util.CodeUtil;
import cn.cc.cisp.security.Authentication;
import cn.cc.cisp.security.web.context.RequestContextSecurity;
import cn.cc.cisp.util.StringUtils;
import cn.cc.cisp.nostruct.manager.FileManagerFactory;
import cn.cc.cisp.nostruct.manager.IFileManager;
import cn.cc.cisp.nostruct.manager.NostructException;
import cn.yunrui.common.util.UUID;
import cn.yunrui.common.util.YunRuiCommonUtil;
import cn.yunrui.intfirectrlsys.minifirestationentity.AttachEntity;
import cn.yunrui.intfirectrlsys.service.CommonAttachmentService;
import com.i380v.openservices.core.codec.JacksonSupport;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Controller
@RequestMapping({"/commonAttachment"})
public class CommonAttachmentAction
{
  public static final String ATTACHMENT = "ATTACHMENT";
  private static final String ATTACH_TYPE = "ATTACH_TYPE";
  private static Logger logger = Logger.getLogger(CommonAttachmentAction.class);

  @Resource(name="commonAttachmentService")
  private CommonAttachmentService service;
  
  private static IFileManager fileManager = FileManagerFactory.getInstance().getFileManager("2");

  @RequestMapping(value={"/saveAttachment.htm"}, method={RequestMethod.POST}, produces={"text/html;charset=UTF-8"})
  @ResponseBody
  public String saveAttachment(@RequestParam("file") CommonsMultipartFile file, HttpServletRequest request, HttpSession httpSession)
  {
    HashMap<String, Object> resultMap = new HashMap<String, Object>();
    JsonConfig jsonConfig = new JsonConfig();
    resultMap.put("result", Boolean.valueOf(false));
    resultMap.put("msg", "上传失败");
    
    String attachId = request.getParameter("attachId");
    String attachType = request.getParameter("attachType");
    String type = request.getParameter("type");
    String dirId = getDirIdByAttachId(attachType, attachId);
    String filename = file.getOriginalFilename();
    String name = filename;
    String ext = "";
    try {
      name = filename.substring(0, filename.lastIndexOf("."));
      ext = filename.substring(filename.lastIndexOf(".") + 1);
    } catch (Exception e) {
      e.printStackTrace();
    }
    Authentication auth = RequestContextSecurity.getAuthentication();
    try {
      System.out.println("想打人" + name);
      String fileId = fileManager.saveFile(file.getBytes(), dirId, ext, name, auth.getUserName(), filename);
      System.out.println("猪队友" + name);
      Code code = null;
      if (ATTACH_TYPE.equals(YunRuiCommonUtil.nullToEmpty(attachType))) {
        List<Code> codeList = CodeUtil.getCodeList(ATTACH_TYPE);
        for (int i = 0; i < codeList.size(); i++) {
          if (attachId.split("-")[0].equals(((Code)codeList.get(i)).getValue())) {
            code = (Code)codeList.get(i);
          }
        }
      }

      String url = YunRuiCommonUtil.nullToEmpty(fileManager.getImageUrl(fileId, 500));
      if ("T".equals(CodeUtil.getCodeName("ENV_TYPE", "IS_PRODUCE"))) {
        url = StringUtils.replace(url, "-internal", "");
      }
      if (code != null) {
        boolean flagW = (!"".equals(YunRuiCommonUtil.nullToEmpty(code.getContent1()))) && 
          (!"-1".equals(YunRuiCommonUtil.nullToEmpty(code.getContent1()))) && 
          (!"0".equals(YunRuiCommonUtil.nullToEmpty(code.getContent1())));
        boolean flagH = (!"".equals(YunRuiCommonUtil.nullToEmpty(code.getContent2()))) && 
          (!"-1".equals(YunRuiCommonUtil.nullToEmpty(code.getContent2()))) && 
          (!"0".equals(YunRuiCommonUtil.nullToEmpty(code.getContent2())));
        url = url.replace(",w_500,limit_1", (flagW ? ",w_" + code.getContent1() : "") + (
          flagH ? ",m_fixed,h_" + code.getContent2() : "") + ",limit_0");
      }
      logger.debug("url==" + url);
      String uuid = new UUID().toString();
      if (service.addAttachment(uuid, attachType, attachId, url, fileId, name, ext, type, auth.getUserName())) {
        resultMap.put("uuid", uuid);
        resultMap.put("name", name);
        resultMap.put("result", Boolean.valueOf(true));
        resultMap.put("msg", "上传成功");
      } else {
        fileManager.remove(fileId, true);
        System.out.println("神对手" + name);
      }
    } catch (Exception e) {
      e.printStackTrace();
      if (e.getMessage().equals("存在同名的文件")) {
        resultMap.put("msg", e.getMessage());
      }
      return JSONArray.fromObject(resultMap, jsonConfig).toString();
    }
    return JSONArray.fromObject(resultMap, jsonConfig).toString();
  }
  @RequestMapping(value="/deleteAttach.htm")
  public @ResponseBody String deleteAttach(String attachType, String attachId,HttpServletResponse response) {
    boolean result = false;
    try {
        AttachEntity ae = service.queryAttachmentByUuid(attachType, attachId);
        System.out.println("猪猪猪1111111111111111111" + YunRuiCommonUtil.nullToEmpty(ae.getFile_id()));
        System.out.println("猪猪猪" + JacksonSupport.toJsonString(ae));
        if (!"".equals(YunRuiCommonUtil.nullToEmpty(ae.getFile_id()))) {
          fileManager.remove(ae.getFile_id(), true);
          System.out.println("猪猪猪2222222222222222222");
        }
        result = service.delAttachment(attachType, attachId);
        return "{\"result\":" + result + "}";
    } catch (Exception e) {
      e.printStackTrace();
    }return "{\"result\":" + result + "}";
  }

  @RequestMapping(value="/deleteAttach.htm")
  public @ResponseBody String deleteAttach(String attachType, String attachId, String uuids, HttpServletResponse response) {
    boolean result = false;
    try {
    	String[] idArray = uuids.split(",");
        for (String uuid : idArray) {
      	System.out.println("uuid" + uuid);  
          AttachEntity ae = service.queryAttachmentByUuid(attachType, attachId, uuid);
          System.out.println("1111111111111111111" + YunRuiCommonUtil.nullToEmpty(ae.getFile_id()));
          System.out.println("" + JacksonSupport.toJsonString(ae));
          if (!"".equals(YunRuiCommonUtil.nullToEmpty(ae.getFile_id()))) {
            fileManager.remove(ae.getFile_id(), true);
            System.out.println("2222222222222222222");
          }
        }
      result = service.delMuchAttachment(attachType, attachId, uuids);
      return "{\"result\":" + result + "}";
    } catch (Exception e) {
      e.printStackTrace();
    }return "{\"result\":" + result + "}";
  }
  @RequestMapping(value="/deleteMuchAttach.htm")
  public @ResponseBody String deleteMuchAttach(String attachType, String attachId, String uuids, HttpServletResponse response) {
    boolean result = false;
    try {
      String[] idArray = uuids.split(",");
      for (String uuid : idArray) {
    	System.out.println("" + uuid);  
        AttachEntity ae = service.queryAttachmentByUuid(attachType, attachId, uuid);
        System.out.println("1111111111111111111" + YunRuiCommonUtil.nullToEmpty(ae.getFile_id()));
        System.out.println("" + JacksonSupport.toJsonString(ae));
        if (!"".equals(YunRuiCommonUtil.nullToEmpty(ae.getFile_id()))) {
          fileManager.remove(ae.getFile_id(), true);
          System.out.println("2222222222222222222");
        }
      }
      result = service.delMuchAttachment(attachType, attachId, uuids);
      return "{\"result\":" + result + "}";
    } catch (Exception e) {
      e.printStackTrace();
    }return "{\"result\":" + result + "}";
  }
  @RequestMapping(value={"/downAttachLocal.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public void downAttachLocal(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    String uuid = request.getParameter("uuid");
    System.out.println("下载uuid"+uuid);
    String attachType = request.getParameter("attachType");
    System.out.println("下载attachType"+attachType);
    String attachId = request.getParameter("attachId");
    System.out.println("下载attachType"+attachType);
    AttachEntity ae = service.queryAttachmentByUuid(attachType, attachId);
    logger.debug(JacksonSupport.toJsonString("获取ae" + ae));
    byte[] arrayOfByte = fileManager.getContentByFileId(ae.getFile_id());
    System.out.println("dffgdarrayOfByte" + arrayOfByte);
    String file_name = ae.getFile_name() + "." + ae.getExt_name();
    downLoadFromByte(arrayOfByte, file_name, response);
  }

  public void downLoadFromUrl(String urlStr, String fileName, HttpServletResponse response) throws IOException
  {
    URL url = new URL(urlStr);
    InputStream inputStream = null;
    OutputStream outputStream = null;
    HttpURLConnection conn = null;
    try {
      conn = (HttpURLConnection)url.openConnection();

      conn.setConnectTimeout(3000);

      conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

      inputStream = conn.getInputStream();

      response.setContentType("application/x-msdownload");
      response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
      outputStream = response.getOutputStream();
      byte[] buf = new byte[4096];
      int len = 0;
      if (inputStream != null) {
        while ((len = inputStream.read(buf)) != -1) {
          outputStream.write(buf, 0, len);
        }
        outputStream.flush();
        closeInputStream(inputStream);
      }
      inputStream = null;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      closeOutputStream(outputStream);
      closeInputStream(inputStream);
    }
  }

  public void downLoadFromByte(byte[] arrayOfByte, String fileName, HttpServletResponse response) throws IOException {
    System.out.println("dshfdfhd" + arrayOfByte);
    InputStream inputStream = null;
    OutputStream outputStream = null;
    try
    {
      inputStream = new ByteArrayInputStream(arrayOfByte);

      response.setContentType("application/x-msdownload");
      response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
      outputStream = response.getOutputStream();
      byte[] buf = new byte[4096];
      int len = 0;
      if (inputStream != null) {
        while ((len = inputStream.read(buf)) != -1) {
          outputStream.write(buf, 0, len);
        }
        outputStream.flush();
        closeInputStream(inputStream);
      }
      inputStream = null;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      closeOutputStream(outputStream);
      closeInputStream(inputStream);
    }
  }

  private void closeOutputStream(OutputStream outputStream) {
    if (outputStream != null)
      try {
        outputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
  }

  private void closeInputStream(InputStream inputStream)
  {
    if (inputStream != null)
      try {
        inputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
  }

  private String getDirIdByAttachId(String attachType, String attachId)
  {
    boolean isExistFolder = fileManager.isExistFolderKeyword(null, 
      attachType);
    String parentFolderId = null;
    if (isExistFolder)
      parentFolderId = fileManager.getFolderIdByKeyword(null, 
        attachType);
    else {
      parentFolderId = fileManager.createFolder(CodeUtil.getCodeName("ATTACHMENT", attachType), null, 
        attachType);
    }
    boolean isExist = fileManager.isExistFolderKeyword(parentFolderId, 
      attachId);
    if (isExist) {
      return fileManager.getFolderIdByKeyword(parentFolderId, attachId);
    }
    return saveFolder(parentFolderId, CodeUtil.getCodeName("ATTACHMENT", attachType) + "(" + attachId + ")", 
      attachId);
  }

  private String saveFolder(String parentFolderId, String name, String keyword)
  {
    String folderId = null;
    try {
      folderId = fileManager.createFolder(name, parentFolderId, keyword);
    } catch (NostructException localNostructException) {
    }
    return folderId;
  }
}*/