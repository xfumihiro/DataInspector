package data_inspector.aspect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import data_inspector.DataInspector;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@SuppressWarnings("unused") @Aspect public class DataInspectorAspect {
  private static final int OVERLAY_PERMISSION_CALL = 1;

  private DataInspector dataInspector;
  private boolean isRequestingOverlayPermission = false;
  private boolean isRestarting = false;

  public DataInspectorAspect() {
    dataInspector = DataInspector.create();
  }

  @Pointcut("within(android.app.Activity+)") public void withinActivityClass() {
  }

  @Pointcut("execution(void onCreate(..)) && withinActivityClass()")
  public void activityOnCreatedCall() {
  }

  @Around("activityOnCreatedCall()")
  public Object injectDataInspector(ProceedingJoinPoint joinPoint) throws Throwable {
    Log.d(DataInspector.TAG, "injectDataInspector");
    Context context = (Context) joinPoint.getThis();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (Settings.canDrawOverlays(context)) {
        dataInspector.onCreate(context);
        isRestarting = false;
      } else {
        isRequestingOverlayPermission = true;
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        ((Activity) context).startActivityForResult(intent, OVERLAY_PERMISSION_CALL);
      }
    } else {
      dataInspector.onCreate(context);
    }

    return joinPoint.proceed();
  }

  @Pointcut("execution(void onResume()) && withinActivityClass()")
  public void activityOnResumeCall() {
  }

  @Around("activityOnResumeCall()") public Object showDataInspector(ProceedingJoinPoint joinPoint)
      throws Throwable {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Context context = (Context) joinPoint.getThis();
      if (isRequestingOverlayPermission) {
        if (Settings.canDrawOverlays(context)) {
          // relaunching the app for deploying Probe features
          Intent intent =
              context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          ((Activity) context).finish();
          isRequestingOverlayPermission = false;
          isRestarting = true;
          context.startActivity(intent);
        }
      } else {
        dataInspector.onResume();
      }
    } else {
      dataInspector.onResume();
    }

    return joinPoint.proceed();
  }

  @Pointcut("execution(void onPause()) && withinActivityClass()")
  public void activityOnPauseCall() {
  }

  @Around("activityOnPauseCall()") public Object hideDataInspector(ProceedingJoinPoint joinPoint)
      throws Throwable {
    if (!isRequestingOverlayPermission && !isRestarting) {
      dataInspector.onPause();
    }
    return joinPoint.proceed();
  }

  @Pointcut("execution(void onDestroy()) && withinActivityClass()")
  public void activityOnDestroyCall() {
  }

  @Around("activityOnDestroyCall()")
  public Object destroyDataInspector(ProceedingJoinPoint joinPoint) throws Throwable {
    if (!isRequestingOverlayPermission && !isRestarting) {
      Context context = (Context) joinPoint.getThis();
      dataInspector.onDestroy(context);
    }
    return joinPoint.proceed();
  }
}
