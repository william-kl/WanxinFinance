package cn.itcast.wanxinp2p.account.service;


import cn.itcast.wanxinp2p.account.common.AccountErrorCode;
import cn.itcast.wanxinp2p.common.domain.BusinessException;
import cn.itcast.wanxinp2p.common.domain.CommonErrorCode;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import cn.itcast.wanxinp2p.common.util.OkHttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${sms.url}")//get url from application.properties
    private String smsURL;

    @Value("${sms.enable}")
    private Boolean smsEnable;

    /**
     * send and obtain code
     * @param mobile
     * @return
     * http://localhost:56085/sailing/generate?effectiveTime=300&name=sms
     */
    public RestResponse getSMSCode(String mobile) {
        if (smsEnable) {//如果走短信验证码
            return OkHttpUtil.post(smsURL + "/generate?effectiveTime=300&name=sms", "{\"mobile\":" + mobile + "}");

        }
        return RestResponse.success();
    }

    /**
     * verify the code
     * @param key 校验标识,redis中的键
     * @param code 短信验证码
     * http://localhost:56085/sailing/verify?names=sms&verificationKey=xxx&verificationCode=xxx
     */
    public void verifySmsCode(String key,String code){
        if (smsEnable) {
            StringBuilder params = new StringBuilder("/verify?name=sms");
            params.append("&verificationKey=").append(key).append("&verificationCode=").append(code);
            RestResponse smsResponse = OkHttpUtil.post(smsURL + params, "");
            if (smsResponse.getCode() != CommonErrorCode.SUCCESS.getCode() || smsResponse.getResult().toString().equalsIgnoreCase("false")) {
                throw new BusinessException(AccountErrorCode.E_140152);
            }
        }
    }


}
