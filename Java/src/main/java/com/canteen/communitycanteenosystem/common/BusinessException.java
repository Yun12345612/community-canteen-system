package com.canteen.communitycanteenosystem.common;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.common
 * @Author: 夏日花店
 * @CreateTime: 2026/1/30 21:23
 * @Description: 业务异常类
 * @Version: 1.0
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
