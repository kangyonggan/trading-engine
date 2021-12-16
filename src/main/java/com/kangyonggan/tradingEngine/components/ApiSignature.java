package com.kangyonggan.tradingEngine.components;

import com.kangyonggan.tradingEngine.dto.RequestParams;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 签名
 *
 * @author kyg
 */
@Component
public class ApiSignature {

    private static final String SIGNATURE_METHOD_VALUE = "HmacSHA256";
    private static final String SIGNATURE_KEY = "sign";
    private static final String TIMESTAMP_KEY = "ts";

    /**
     * 验签
     *
     * @param params
     * @param secretKey
     * @return
     */
    public boolean verify(RequestParams<String, Object> params, String secretKey) {
        String sign = (String) params.get(SIGNATURE_KEY);
        if (StringUtils.isEmpty(sign)) {
            throw new BizException("验签异常");
        }
        return sign.equals(signature(params, secretKey));
    }

    /**
     * 签名
     *
     * @param params
     * @param secretKey
     * @return
     */
    public String signature(RequestParams<String, Object> params, String secretKey) {
        if (!params.containsKey(TIMESTAMP_KEY)) {
            params.put(TIMESTAMP_KEY, System.currentTimeMillis());
        }
        params.remove(SIGNATURE_KEY);
        Mac hmacSha256;
        try {
            hmacSha256 = Mac.getInstance(SIGNATURE_METHOD_VALUE);
            SecretKeySpec secKey = new SecretKeySpec(secretKey.getBytes(), SIGNATURE_METHOD_VALUE);
            hmacSha256.init(secKey);
        } catch (Exception e) {
            throw new BizException("签名异常");
        }
        String sign = new String(Hex.encodeHex(hmacSha256.doFinal(appendUrl(params).getBytes())));
        params.put(SIGNATURE_KEY, sign);
        return sign;
    }

    /**
     * 拼接url
     *
     * @param params
     * @return
     */
    public String appendUrl(RequestParams<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        for (String key : params.keySet()) {
            if (!("").equals(sb.toString())) {
                sb.append("&");
            }
            sb.append(key);
            sb.append("=");
            sb.append(urlEncode(String.valueOf(params.get(key))));
        }
        return sb.toString();
    }

    /**
     * url编码
     *
     * @param s
     * @return
     */
    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new BizException("url编码异常", e);
        }
    }
}
