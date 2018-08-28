package org.wbing.permission;

/**
 * @author wangbing
 * @date 2018/8/27
 */
public class WPermissionException extends RuntimeException {
    public WPermissionException() {
    }

    public WPermissionException(String message) {
        super(message);
    }

    public WPermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public WPermissionException(Throwable cause) {
        super(cause);
    }

    public WPermissionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
