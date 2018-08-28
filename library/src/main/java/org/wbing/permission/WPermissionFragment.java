package org.wbing.permission;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wangbing
 * @date 2018/8/27
 */
public class WPermissionFragment extends Fragment {
    private static final int REQUEST_CODE = 110;
    private String[] value;
    private WPermissionResult result;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CODE) {
            return;
        }
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0, count = grantResults.length; i < count; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                list.add(permissions[i]);
            }
        }

        if (list.isEmpty()) {
            detach();
            result.success();
        } else {
            Log.e("TAG", list.toString());
            result.fail(list.toArray(new String[0]));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE) {
            return;
        }
        if (result != null && value != null) {
            ArrayList<String> list = new ArrayList<>();
            for (String mPermission : value) {
                if (!checkPermission(getActivity(), mPermission)) {
                    list.add(mPermission);
                }
            }
            if (list.isEmpty()) {
                detach();
                result.success();
            } else {
                Log.e("TAG", list.toString());
                result.fail(list.toArray(new String[0]));
            }
        }
    }

    //解绑fragment
    private void detach() {
        if (!isAdded()) return;
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.detach(this);
        fragmentTransaction.remove(this);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void setPermissionsAndExecute(String[] value, WPermissionResult wPermissionResult) {
        setPermissions(value, wPermissionResult).execute();
    }

    /**
     * 设置请求参数
     *
     * @param value
     * @param wPermissionResult
     * @return
     */
    public WPermissionFragment setPermissions(String[] value, WPermissionResult wPermissionResult) {
        Log.e("WPermission", "requestPermissions:" + Arrays.toString(value));
        this.value = value;
        this.result = wPermissionResult;
        return this;
    }

    public void execute() {
        if (value == null || value.length == 0) {
            throw new WPermissionException("WPermissions 未设置任何权限");
        }
        if (!checkManifestPermission()) {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkPermissions()) {
            if (result != null) {
                result.success();
            }
            return;
        }

        //提取权限列表里面没通过的
        String[] per = getNeedRequestPermissions(value);

        //申请权限
        try {
//            mFragmentCallback.setRequestTime();
            requestPermissions(per, REQUEST_CODE);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            for (String aPer : per) {
                String permissionName = getPermissionName(getActivity(), aPer);
                if (!permissionName.isEmpty()) {
                    sb.append(" [")
                            .append(permissionName)
                            .append("] ");
                }
            }
            String permissionList = sb.toString();
            String s = permissionList.replaceAll("(\\s\\[.*\\]\\s)\\1+", "$1");
            openSettingActivity(
                    getResources().getString(R.string.w_permission_should_show_rationale, s));
        }
    }

    /**
     * 检查动态权限是否都在清单文件注册
     */
    private boolean checkManifestPermission() {
        List<String> notRegPermissions = getNotRegPermissions();
        boolean empty = notRegPermissions.isEmpty();
        if (empty) {
            return true;
        }
        StringBuilder sb = new StringBuilder();
        for (String notRegPermission : notRegPermissions) {
            sb.append(" [")
                    .append(notRegPermission)
                    .append("] ");
        }
        sb.append(getActivity().getString(R.string.w_permission_not_reg_in_manifest));
        String permissionList = sb.toString();
        String s = permissionList.replaceAll("(\\s\\[.*\\]\\s)\\1+", "$1");
        Log.e("MagicPermission Error: ", s);
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 获取动态申请却没有在清单文件注册的权限
     */
    private List<String> getNotRegPermissions() {
        String[] requiredPermissions = getRequiredPermissions();
        List<String> list = Arrays.asList(requiredPermissions);
        List<String> notReg = new ArrayList<>();
        for (String permission : value) {
            if (!list.contains(permission)) {
                notReg.add(permission);
            }
        }
        return notReg;
    }

    /**
     * 获取清单文件中注册的权限
     */
    private String[] getRequiredPermissions() {
        try {
            PackageInfo info = getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    /**
     * 检查权限列表是否全部通过
     *
     * @return 权限列表是否全部通过
     */
    private boolean checkPermissions() {
        for (String mPermission : value) {
            if (!checkPermission(mPermission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查权限
     *
     * @param permission 权限列表
     * @return 权限是否通过
     */
    private boolean checkPermission(String permission) {
        //检查权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int checkSelfPermission =
                ContextCompat
                        .checkSelfPermission(getActivity(), permission);
        return checkSelfPermission == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 静态检查权限
     *
     * @param context    上下文
     * @param permission 权限列表
     * @return 权限是否通过
     */
    private static boolean checkPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int checkSelfPermission =
                ContextCompat
                        .checkSelfPermission(context, permission);
        return checkSelfPermission == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 获取权限的名称,自动按设备语言显示
     *
     * @param context    上下文
     * @param permission 权限
     * @return 权限名称
     */
    private static String getPermissionName(Context context, String permission) {
        String permissionName = "";
        PackageManager pm = context.getPackageManager();
        try {
            PermissionInfo permissionInfo = pm.getPermissionInfo(permission, 0);
            PermissionGroupInfo groupInfo = pm.getPermissionGroupInfo(permissionInfo.group, 0);
            permissionName = groupInfo.loadLabel(pm).toString();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return permissionName;
    }

    /**
     * 筛选出需要申请的权限
     *
     * @param permissions 权限列表
     * @return 需要申请的权限
     */
    private String[] getNeedRequestPermissions(String[] permissions) {
        List<String> list = new ArrayList<>();
        for (String p : permissions) {
            if (!checkPermission(getActivity(), p)) {
                list.add(p);
            }
        }
        String[] needRequest = new String[list.size()];
        return list.toArray(needRequest);
    }

    /**
     * 打开应用权限设置界面
     */
    public void openSettingActivity(String message) {
        showMessageOKCancel(message, (dialog, which) -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, REQUEST_CODE);
        }, (dialog, which) -> result.fail(value));
    }

    /**
     * 弹出对话框
     *
     * @param message    消息内容
     * @param okListener 点击回调
     */
    private void showMessageOKCancel(String message,
                                     DialogInterface.OnClickListener okListener,
                                     DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.w_permission_dialog_granted), okListener)
                .setNegativeButton(getString(R.string.w_permission_dialog_denied), cancelListener)
                .create()
                .show();
    }
}
