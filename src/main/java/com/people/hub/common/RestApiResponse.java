package com.people.hub.common;

import com.people.hub.common.dto.PageInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class RestApiResponse {
    private boolean success;
    private Object data;

    public RestApiResponse(boolean success, Object data) {
        this.success = success;
        this.data = data;
    }

    public static RestApiResponse success(Object data) {
        return new RestApiResponse(true, data);
    }

    public static RestApiResponse success(PageInfo pageInfo, Object content) {
        Map<String, Object> map = new HashMap<>();
        map.put("pageInfo", pageInfo);
        map.put("content", content);
        return new RestApiResponse(true, map);
    }

    public static RestApiResponse failure(Object data) {
        return new RestApiResponse(false, data);
    }

    public static RestApiResponse success() {
        return new RestApiResponse(true, null);
    }

    public static RestApiResponse failure() {
        return new RestApiResponse(false, null);
    }

    public static ResponseEntity<Map<String, Object>> responseSuccess(String message) {
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("message", message);
        successResponse.put("status", "success");
        return ResponseEntity.ok(successResponse);
    }

    public static ResponseEntity<Map<String, Object>> responseSuccess(String msg, String inputType , Object inputdata) {
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("status", "success");
        successResponse.put("message", msg);
        successResponse.put(inputType, inputdata);
        return ResponseEntity.ok(successResponse);
    }

    public static ResponseEntity<Map<String, Object>> responseFailure(String message) {
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("message", message);
        successResponse.put("status", "failure");
        return ResponseEntity.ok(successResponse);
    }

    public static ResponseEntity<Map<String, Object>> internalServer(String message) {
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("error", message);
        successResponse.put("success", false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(successResponse);
    }

    public static RestApiResponse responseJwtRefreshToken(String token, String refreshToken) {
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("token",token);
        successResponse.put("refreshToken",refreshToken);
        return new RestApiResponse(true, successResponse);
    }
}
