package org.wbing.permission;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author wangbing
 * @date 2018/8/27
 */
@Aspect
public class WPermissionAspect {
    private static final String TAG = "WPermissionFragment";
    private static final String Pointcut = "execution(@org.wbing.permission.WPermission * *(..))";

    @Pointcut(Pointcut)
    public void WPermissionMethod() {
    }

    @Around("WPermissionMethod()")
    public void beforeWPermissionMethod(final ProceedingJoinPoint point) throws Throwable {
        Object target = point.getTarget();
        FragmentActivity activity;
        if (target instanceof FragmentActivity) {
            activity = (FragmentActivity) target;
        } else if (target instanceof Fragment) {
            activity = ((Fragment) target).getActivity();
        } else {
            throw new WPermissionException("WPermissionMethod()必须写在FragmentActivity或者Fragment里");
        }

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        WPermissionFragment fragment = (WPermissionFragment) fragmentManager.findFragmentByTag(TAG);
        boolean isNewInstance = fragment == null;
        if (isNewInstance) {
            fragment = new WPermissionFragment();
            fragmentManager
                    .beginTransaction()
                    .add(fragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        WPermission permission = method.getAnnotation(WPermission.class);
        String[] value = permission.value();
        fragment.setPermissionsAndExecute(value, new WPermissionResult() {
            @Override
            public void success() {
                try {
                    point.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void fail(String[] permission) {
                Method failedCallBack = null;
                Class<?> aClass = target.getClass();
                for (Method method : aClass.getDeclaredMethods()) {
                    boolean isCallback = method.isAnnotationPresent(WPermissionFailed.class);
                    if (!isCallback) continue;
                    method.setAccessible(true);
                    failedCallBack = method;
                }

                if (failedCallBack == null) {
                    Toast.makeText(activity, activity.getText(R.string.w_permission_denied), Toast.LENGTH_SHORT).show();
                    return;
                }
                Class<?>[] types = failedCallBack.getParameterTypes();
                try {
                    if (types.length == 1 &&
                            types[0].isArray() &&
                            types[0].getComponentType() == String.class) {
                        failedCallBack.invoke(target, (Object) permission);
                    } else if (types.length == 0) {
                        failedCallBack.invoke(target);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
