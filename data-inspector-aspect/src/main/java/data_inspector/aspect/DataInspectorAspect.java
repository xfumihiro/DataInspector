package data_inspector.aspect;

import android.content.Context;
import android.util.Log;
import data_inspector.DataInspector;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@SuppressWarnings("unused") @Aspect public class DataInspectorAspect {
  private DataInspector dataInspector;

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
    dataInspector.onCreate((Context) joinPoint.getThis());
    return joinPoint.proceed();
  }

  @Pointcut("execution(void onResume()) && withinActivityClass()")
  public void activityOnResumeCall() {
  }

  @Around("activityOnResumeCall()") public Object showDataInspector(ProceedingJoinPoint joinPoint)
      throws Throwable {
    dataInspector.onResume();
    return joinPoint.proceed();
  }

  @Pointcut("execution(void onPause()) && withinActivityClass()")
  public void activityOnPauseCall() {
  }

  @Around("activityOnPauseCall()") public Object hideDataInspector(ProceedingJoinPoint joinPoint)
      throws Throwable {
    dataInspector.onPause();
    return joinPoint.proceed();
  }

  @Pointcut("execution(void onDestroy()) && withinActivityClass()")
  public void activityOnDestroyCall() {
  }

  @Around("activityOnDestroyCall()")
  public Object destroyDataInspector(ProceedingJoinPoint joinPoint) throws Throwable {
    dataInspector.onDestroy((Context) joinPoint.getThis());
    return joinPoint.proceed();
  }
}
