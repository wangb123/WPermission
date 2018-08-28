package org.wbing.permission;

/**
 * @author wangbing
 * @date 2018/8/27
 */
public interface WPermissionResult {
    /**
     * 成功
     */
    void success();

    /**
     * 失败
     *
     * @param permission 失败权限
     */
    void fail(String[] permission);
}
