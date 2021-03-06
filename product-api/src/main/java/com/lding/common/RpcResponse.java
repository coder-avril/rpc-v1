package com.lding.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * RPC通信消息的响应数据规则
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse implements Serializable {
    //响应的消息id
    private String responseId;
    //请求的消息id
    private String requestId;
    // 响应的消息是否成功
    private boolean success;
    // 响应的数据结果
    private Object result;
    // 如果有异常信息,在该对象中记录异常信息
    private Throwable throwable;
}
