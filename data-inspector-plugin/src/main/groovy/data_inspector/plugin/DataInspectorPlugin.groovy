package data_inspector.plugin

import com.android.build.gradle.AppPlugin
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.tasks.compile.JavaCompile

class DataInspectorPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    if (!project.plugins.withType(AppPlugin)) {
      throw new IllegalStateException("'android' plugin required.")
    }

    project.dependencies {
      debugCompile 'com.github.xfumihiro.data-inspector:data-inspector-runtime:0.1.2-SNAPSHOT'

      // Android dependencies
      debugCompile 'com.android.support:appcompat-v7:21.0.3'

      // AspectJ dependencies
      debugCompile 'org.aspectj:aspectjrt:1.8.6'

      // Dagger dependencies
      debugCompile 'com.google.dagger:dagger:2.0.1'

      // Third-party dependencies
      debugCompile 'com.f2prateek.rx.preferences:rx-preferences:1.0.0'
      debugCompile 'com.github.frankiesardo:auto-parcel:0.3'

      debugCompile 'com.jakewharton.rxbinding:rxbinding:0.3.0'
      debugCompile 'io.reactivex:rxjava:1.0.14'
      debugCompile 'io.reactivex:rxandroid:1.0.1'

      debugCompile 'com.android.support:recyclerview-v7:21.0.3'
    }

    final def log = project.logger
    final def variants = project.android.applicationVariants

    variants.all { variant ->
      if (!variant.buildType.isDebuggable()) {
        log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
        return;
      }

      JavaCompile javaCompile = variant.javaCompile
      javaCompile.doLast {
        String[] args = ["-showWeaveInfo",
                         "-1.5",
                         "-inpath", javaCompile.destinationDir.toString(),
                         "-aspectpath", javaCompile.classpath.asPath,
                         "-d", javaCompile.destinationDir.toString(),
                         "-classpath", javaCompile.classpath.asPath,
                         "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
        log.debug "ajc args: " + Arrays.toString(args)

        MessageHandler handler = new MessageHandler(true);
        new Main().run(args, handler);
        for (IMessage message : handler.getMessages(null, true)) {
          switch (message.getKind()) {
            case IMessage.ABORT:
            case IMessage.ERROR:
            case IMessage.FAIL:
              log.error message.message, message.thrown
              break;
            case IMessage.WARNING:
              log.warn message.message, message.thrown
              break;
            case IMessage.INFO:
              log.info message.message, message.thrown
              break;
            case IMessage.DEBUG:
              log.debug message.message, message.thrown
              break;
          }
        }
      }
    }

    // Add data-inspector-aspect library dependency according to the compileSdkVersion
    // NOTE: This solution involves behavior which has been deprecated and is scheduled to be removed in Gradle 3.0.
    project.getGradle().addListener(new DependencyResolutionListener() {
      @Override
      void beforeResolve(ResolvableDependencies resolvableDependencies) {
        if (project.android.compileSdkVersion == 'android-23') {
          project.dependencies
              .add('debugCompile', project.dependencies.create(
              'com.github.xfumihiro.data-inspector:data-inspector-aspect-v23:0.1.2-SNAPSHOT'))
        } else {
          project.dependencies
              .add('debugCompile', project.dependencies.create(
              'com.github.xfumihiro.data-inspector:data-inspector-aspect:0.1.2-SNAPSHOT'))
        }
      }

      @Override
      void afterResolve(ResolvableDependencies resolvableDependencies) {}
    })
  }
}
