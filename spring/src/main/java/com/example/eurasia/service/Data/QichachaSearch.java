package com.example.eurasia.service.Data;

import org.json.JSONException;
import org.json.JSONObject;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.HttpHead;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 版权申明: 苏州朗动科技有限公司<br>
 * 项目描述: 企查查-接口平台<br>
 * 该接口调用demo仅供学习参考
 */
public class QichachaSearch {
    // 请登录http://yjapi.com/DataCenter/MyData
    // 查看我的秘钥 我的Key
    private static final String appKey = "我的接口:我的Key";
    private static final String secKey = "我的接口:我的秘钥";

    public static void getTokenJson(String keyword) {
        String reqInterNme = "http://api.qichacha.com/ECIV4/Search";
        String paramStr = "keyword=" + keyword;//e.g."keyword=新疆庆华能源集团有限公司"

        try {
            // auth header setting.请求参数(http请求头Headers)
            HttpHead reqHeader = new HttpHead();
            String[] authHeader = RandomAuthHeader();
            reqHeader.setHeader("Token", authHeader[0]);//验证加密值 Md5(key+Timespan+SecretKey) 加密的32位大写字符串)
            reqHeader.setHeader("Timespan", authHeader[1]);//精确到秒的Unix时间戳

/*
请求参数(Query)
名称 类型 是否必填 描述
key String 是 "应用APPKEY(应用详细页查询)"
keyword String 是 "搜索关键字"
type String 否 "查询维度，default(查询所有字段)， name(查询公司名，注册号和信用代码)，englishname(英文名) address(公司地址)， opername(法人)， econkind(公司类型)， scope(经营范围)， status(状态)， belongorg(登记机关)，featurelist(产品，特性)"
pageSize Int 否 "每页条数，默认为10,最大不超过20条"
pageIndex Int 否 "页码，默认第一页"
dtype String 否 "返回数据格式：json或xml，默认json"
*/
            final String reqUri = reqInterNme.concat("?key=").concat(appKey).concat("&").concat(paramStr);
            String tokenJson = "";//HttpHelper.httpGet(reqUri, reqHeader.getAllHeaders());

/*
返回参数(Return)
名称 类型 描述
KeyNo String "内部KeyNo"
Name String "公司名称"
OperName String "法人名称"
StartDate String "成立日期"
Status String "企业状态"
No String "注册号"
CreditCode String "社会统一信用代码"
Dimension String "维度(保留字段)"
*/
            // parse status from json
            String status = FormatJson(tokenJson, "Status");
            Slf4jLogUtil.get().info("{} Response:{} Status:{}", reqUri, tokenJson, status);
            if (!HttpCodeRegex.isAbnormalRequest(status)) {
                PrettyPrintJson(tokenJson);//T.B.D.
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    // 获取返回码 Res Code
    protected static class HttpCodeRegex {
/*
服务级错误码参照(error_code)
		错误码:说明
		200:查询成功
		201:查询无结果
		202:查询参数错误，请检查
		203:系统查询有异常，请联系技术人员
		204:请至少输入一个参数
		205:等待处理中
		206:无效的省份代码
		207:请求数据的条目数超过上限（5000）
		208:此接口不支持此公司类型查询
		213:参数长度不能小于2
		214:您还未购买过该接口，请先购买
		215:不支持的查询关键字
		216:国家代码不正确
		221:发票校验失败（参数与发票号码不对应）
		222:超过发票当日查验次数，请次日再试
		299:无效的请求
系统级错误码
		错误码:说明
		101:当前的KEY无效或者还未生效中
		102:当前KEY已欠费
		103:当前Key被暂停使用
		104:接口正在维护中
		105:接口已下线停用
		107:被禁止的IP或者签名错误
		108:请求格式错误，请重试
		109:请求超过系统限制
		110:当前相同查询连续出错，请等2小时后重试
		111:接口权限未开通
		112:您的账号剩余使用量已不足
		113:当前接口已被删除，请重新申请
		114:当前接口已被禁用，请联系管理员
		115:身份验证错误或者已过期
		199:系统未知错误，请联系技术客服
*/
        private static final String ABNORMAL_REGEX = "(101)|(102)";
        private static final Pattern pattern = Pattern.compile(ABNORMAL_REGEX);
        protected static boolean isAbnormalRequest(final String status) {
            return pattern.matcher(status).matches();
        }
    }

    // 获取Auth Code
    protected static final String[] RandomAuthHeader() {
        String timeSpan = String.valueOf(System.currentTimeMillis() / 1000);
        String[] authHeaders = new String[] { DigestUtils.md5Hex(appKey.concat(timeSpan).concat(secKey)).toUpperCase(), timeSpan };
        return authHeaders;
    }

    // 解析JSON
    protected static String FormatJson(String jsonString, String key) throws JSONException {
        JSONObject jObject = new JSONObject(jsonString);
        return (String) jObject.get(key);
    }

    // pretty print 返回值
    protected static void PrettyPrintJson(String jsonString) throws JSONException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object obj = mapper.readValue(jsonString, Object.class);
            String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            Slf4jLogUtil.get().info(indented);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
