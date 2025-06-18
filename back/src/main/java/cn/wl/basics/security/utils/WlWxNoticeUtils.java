package cn.wl.basics.security.utils;

import cn.wl.data.utils.WlNullUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 郑为中
 * CSDN: Designer 小郑
 */
@ApiOperation(value = "企微消息发送消息工具类")
public class WlWxNoticeUtils {

    private static final String BASE_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";

    private static final String USER_ID_ERR = "81013";

    @ApiModelProperty(value = "朗世企业ID")
    public static final String YH_CORPID = "wwf94bb44e76e308f8";

    @ApiOperation(value = "发送文本消息")
    public static String sendInputMessage(int company,String userId,String content,String token){
        if(content == null || WlNullUtils.isNull(content)) {
            return "NULL";
        }

        String json = JSON.toJSONString(new WlWeChatNoticeInput(userId,"text",YH_CORPID,new WlWeChatNoticeInputItem(content),0,1));
        String s= WeiChatUtils.httpsRequest(BASE_URL + token,"POST",json);
        System.out.println(s);
        JSONObject ans1 = JSONObject.parseObject(s);
        String jsonStr2 = ans1.getString("errcode");
        if(jsonStr2.equals("0")){
            return ans1.getString("msgid");
        } else if(jsonStr2.equals("81013")) {
            return USER_ID_ERR;
        }
        return "FAIL";
    }

    @ApiOperation(value = "企微上传文件")
    public static String uploadWeChatFile(String path, String fileType,String token) {
        try {
            JSONObject jsonObject = WeChatUploadMeidaUtils.UploadMeida(fileType,path,token);
            System.out.println(jsonObject.toJSONString());
            String errcode = jsonObject.getString("errcode");
            if(errcode.equals("0")) {
                System.out.println("文件上传成功");
                System.out.println(jsonObject.getString("media_id"));
                return jsonObject.getString("media_id");
            }
            return "文件上传失败";
        } catch (Exception e) {
            return "文件上传失败";
        }
    }

    @ApiOperation(value = "发送图片消息")
    public static String sendImageMessage(int company,String userId,String mediaId,String token) {
        WlWeiChatNoticeImage image = new WlWeiChatNoticeImage(userId,"image",YH_CORPID,new WlWeChatNoticeImageItem(mediaId),0,1);
        String json = JSON.toJSONString(image);
        String s= WeiChatUtils.httpsRequest(BASE_URL + token,"POST",json);
        JSONObject ans1 = JSONObject.parseObject(s);
        String errcode = ans1.getString("errcode");
        if(errcode.equals("0")){
            return ans1.getString("msgid");
        } else if(errcode.equals("81013")) {
            return USER_ID_ERR;
        }
        return "FAIL";
    }

    @ApiOperation(value = "发送视频消息")
    public static String sendVideoMessage(int company,String userId,String mediaId,String title,String description,String token) {
        WlWeiChatNoticeVideo video = new WlWeiChatNoticeVideo(userId,"video",YH_CORPID,new WlWeChatNoticeVideoItem(mediaId,title,description),0,1);
        String json = JSON.toJSONString(video);
        String s= WeiChatUtils.httpsRequest(BASE_URL + token,"POST",json);
        JSONObject ans1 = JSONObject.parseObject(s);
        String errcode = ans1.getString("errcode");
        if(errcode.equals("0")){
            return ans1.getString("msgid");
        } else if(errcode.equals("81013")) {
            return USER_ID_ERR;
        }
        return "FAIL";
    }

    @ApiOperation(value = "发送文件消息")
    public static String sendFileMessage(int company,String userId,String mediaId,String token) {
        WlWeiChatNoticeFile file = new WlWeiChatNoticeFile(userId,"file",YH_CORPID,new WlWeChatNoticeFileItem(mediaId),0,1);
        String json = JSON.toJSONString(file);
        String s= WeiChatUtils.httpsRequest(BASE_URL + token,"POST",json);
        JSONObject ans1 = JSONObject.parseObject(s);
        String errcode = ans1.getString("errcode");
        if(errcode.equals("0")){
            return ans1.getString("msgid");
        } else if(errcode.equals("81013")) {
            return USER_ID_ERR;
        }
        return "FAIL";
    }

    @ApiOperation(value = "发送文本卡片消息")
    public static String sendTextCardMessage(int company,String userId,String title,String description,String url,String btntxt,String token) {
        WlWeiChatNoticeTextCard file = new WlWeiChatNoticeTextCard(userId,"textcard",YH_CORPID,new WlWeChatNoticeTextCardItem(title,description,url,btntxt),0,1);
        String json = JSON.toJSONString(file);
        String s= WeiChatUtils.httpsRequest(BASE_URL + token,"POST",json);
        JSONObject ans1 = JSONObject.parseObject(s);
        String errcode = ans1.getString("errcode");
        if(errcode.equals("0")){
            return ans1.getString("msgid");
        } else if(errcode.equals("81013")) {
            return USER_ID_ERR;
        }
        return "FAIL";
    }

    @ApiOperation(value = "发送图文消息")
    public static String sendTuWenMessage(String userId,String title,String description,String url,String picUrl,String token) {
        List<WlWeChatNoticeTuWenItemValue> tuWenList = new ArrayList<>();
        tuWenList.add(new WlWeChatNoticeTuWenItemValue(title, description, url, picUrl));
        WlWeChatNoticeTuWen file = new WlWeChatNoticeTuWen(userId,"news","1000002",new WlWeChatNoticeTuWenItem(tuWenList),0,1);
        String json = JSON.toJSONString(file);
        String s= WeiChatUtils.httpsRequest(BASE_URL + token,"POST",json);
        JSONObject ans1 = JSONObject.parseObject(s);
        String errcode = ans1.getString("errcode");
        if(errcode.equals("0")){
            return ans1.getString("msgid");
        } else if(errcode.equals("81013")) {
            return USER_ID_ERR;
        }
        return "FAIL";
    }

    @ApiOperation(value = "发送Markdown消息")
    public static String sendMarkdownMessage(int company,String userId,String content,String token){
        if(content == null || WlNullUtils.isNull(content)) {
            return "NULL";
        }
        String json = JSON.toJSONString(new WlWeChatNoticeMarkdown(userId,"markdown",YH_CORPID,new WlWeChatNoticeMarkdownItem(content),0,1));
        String s= WeiChatUtils.httpsRequest(BASE_URL + token,"POST",json);
        System.out.println(s);
        JSONObject ans1 = JSONObject.parseObject(s);
        String jsonStr2 = ans1.getString("errcode");
        if(jsonStr2.equals("0")){
            return ans1.getString("msgid");
        } else if(jsonStr2.equals("81013")) {
            return USER_ID_ERR;
        }
        return "FAIL";
    }

    /**
     * Markdown消息B类
     */
    @Data
    @AllArgsConstructor
    private static class WlWeChatNoticeMarkdown {
        private String touser;
        private String msgtype;
        private String agentid;
        private WlWeChatNoticeMarkdownItem markdown;
        private int safe;
        private int enable_duplicate_check;
    }

    /**
     * Markdown消息A类
     */
    @Data
    @AllArgsConstructor
    private static class WlWeChatNoticeMarkdownItem {
        private String content;
    }

    /**
     * 图文消息B类
     */
    @Data
    @AllArgsConstructor
    private static class WlWeChatNoticeTuWen {
        private String touser;
        private String msgtype;
        private String agentid;
        private WlWeChatNoticeTuWenItem news;
        private int safe;
        private int enable_duplicate_check;
    }

    /**
     * 图文消息A类
     */
    @Data
    @AllArgsConstructor
    private static class WlWeChatNoticeTuWenItem {
        private List<WlWeChatNoticeTuWenItemValue> articles;
    }

    /**
     * 图文消息A类
     */
    @Data
    @AllArgsConstructor
    private static class WlWeChatNoticeTuWenItemValue {
        private String title;
        private String description;
        private String url;
        private String picurl;
    }

    /**
     * 文本卡片消息B类
     */
    @Data
    @AllArgsConstructor
    private static class WlWeiChatNoticeTextCard {
        private String touser;
        private String msgtype;
        private String agentid;
        private WlWeChatNoticeTextCardItem textcard;
        private int safe;
        private int enable_duplicate_check;
    }

    /**
     * 文本卡片消息A类
     */
    @Data
    @AllArgsConstructor
    private static class WlWeChatNoticeTextCardItem {
        private String title;
        private String description;
        private String url;
        private String btntxt;
    }

    /**
     * 文件消息B类
     */
    @Data
    @AllArgsConstructor
    private static class WlWeiChatNoticeFile {
        private String touser;
        private String msgtype;
        private String agentid;
        private WlWeChatNoticeFileItem file;
        private int safe;
        private int enable_duplicate_check;
    }

    /**
     * 文件消息A类
     */
    @Data
    @AllArgsConstructor
    private static class WlWeChatNoticeFileItem {
        private String media_id;
    }

    /**
     * 视频消息B类
     */
    @Data
    @AllArgsConstructor
    private static class WlWeiChatNoticeVideo {
        private String touser;
        private String msgtype;
        private String agentid;
        private WlWeChatNoticeVideoItem video;
        private int safe;
        private int enable_duplicate_check;
    }

    /**
     * 视频消息A类
     */
    @Data
    @AllArgsConstructor
    private static class WlWeChatNoticeVideoItem {
        private String media_id;
        private String title;
        private String description;
    }

    /**
     * 图片消息B类
     */
    @Data
    @AllArgsConstructor
    private static class WlWeiChatNoticeImage {
        private String touser;
        private String msgtype;
        private String agentid;
        private WlWeChatNoticeImageItem image;
        private int safe;
        private int enable_duplicate_check;
    }

    /**
     * 图片消息A类
     */
    @Data
    @AllArgsConstructor
    private static class WlWeChatNoticeImageItem {
        private String media_id;
    }

    /**
     * 普通文本B类
     */
    @Data
    @AllArgsConstructor
    private static class WlWeChatNoticeInput {
        private String touser;
        private String msgtype;
        private String agentid;
        private WlWeChatNoticeInputItem text;
        private int safe;
        private int enable_duplicate_check;
    }

    /**
     * 普通文本A类
     */
    @Data
    @AllArgsConstructor
    private static class WlWeChatNoticeInputItem {
        private String content;
    }
}
