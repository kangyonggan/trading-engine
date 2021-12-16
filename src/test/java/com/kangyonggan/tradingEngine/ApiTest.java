package com.kangyonggan.tradingEngine;

import com.kangyonggan.tradingEngine.components.ApiSignature;
import com.kangyonggan.tradingEngine.components.BizException;
import com.kangyonggan.tradingEngine.constants.AppConstants;
import com.kangyonggan.tradingEngine.dto.RequestParams;
import com.kangyonggan.tradingEngine.entity.Permission;
import com.kangyonggan.tradingEngine.service.IPermissionService;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author kyg
 */
public class ApiTest extends AbstractTest {

    @Autowired
    private ApiSignature apiSignature;

    @Autowired
    private IPermissionService permissionService;

    private final static String API_KEY = "db05df4e4daf7555191dcb8c66c11cf714bc296ef2ad6d25215395eb06897435";

    @Test
    public void allOrders() {
        Permission permission = permissionService.getPermissionByApiKey(API_KEY);
        RequestParams<String, Object> params = new RequestParams<>();
        params.put("symbol", "BTCUSDT");
        params.put("orderId", 0);
        LOGGER.info("请求Api接口：{}", request("http://localhost:8080/v1/order/allOrders", "GET", params, permission.getApiKey(), permission.getSecretKey()));
    }

    /**
     * 请求
     *
     * @param url
     * @param method
     * @param params
     * @return
     */
    public String request(String url, String method, RequestParams<String, Object> params, String apiKey, String secretKey) {
        if (params == null) {
            params = new RequestParams<>();
        }
        apiSignature.signature(params, secretKey);
        url = url + "?" + apiSignature.appendUrl(params);
        Request.Builder builder = new Request.Builder().url(url).addHeader(AppConstants.HEADER_APIKEY, apiKey);
        if (method.equals("POST")) {
            builder = builder.post(RequestBody.create(MediaType.parse(AppConstants.APPLICATION_JSON), StringUtils.EMPTY));
        } else if (method.equals("PUT")) {
            builder = builder.put(RequestBody.create(MediaType.parse(AppConstants.APPLICATION_JSON), StringUtils.EMPTY));
        } else if (method.equals("DELETE")) {
            builder = builder.delete();
        }
        Request request = builder.build();
        OkHttpClient client = new OkHttpClient.Builder().build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                return response.body().string();
            } else {
                throw new BizException("请求接口失败");
            }
        } catch (Exception e) {
            throw new BizException("请求接口异常", e);
        }
    }

}
