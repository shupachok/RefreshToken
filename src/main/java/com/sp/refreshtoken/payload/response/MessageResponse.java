package com.sp.refreshtoken.payload.response;

import lombok.Data;

@Data
public class MessageResponse {

    private String message;
    private Object data;
    private String status;

    public MessageResponse(String message,String code,Object data) {
        this.message = message;
        this.data = data;
        this.status = status;
    }
}