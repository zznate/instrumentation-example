package com.apigee.instrumentation.example;


import com.yammer.metrics.reporting.GraphiteReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Can be run from the command line via maven thusly:
 * mvn -e exec:java -Dexec.mainClass="com.apigee.instrumentation.example.InstrumentedBeanCli"
 *
 * Executes a background thread which loops over the {@link InstrumentedBean#calculateSomething(int)}
 * methods infinitely until killed via CTRL+c
 */
public class InstrumentedBeanCli {

  static Logger logger = LoggerFactory.getLogger(InstrumentedBeanCli.class);

  public static void main( String[] args ) throws Exception {

    // TODO add -t [annotated|programatic] option

    ApplicationContext ac = new ClassPathXmlApplicationContext("/appContext.xml");
    // load the graphite endpoint to fire at 1 min intervals if we are configured for such
    // the reporter hooks are a bit rough for IoC/CDI. A factory approach would be a little nicer
    final GraphiteReporter graphiteReporter;
    try {
      graphiteReporter = ac.getBean("graphiteReporter", GraphiteReporter.class);

      graphiteReporter.start(1, TimeUnit.MINUTES);
    } catch (NoSuchBeanDefinitionException nsbde) {
      logger.info("graphiteReporter config not found... use JMX to see output");
    }

    // The annotated version must come via CDI (a.k.a "IoC container")
    final InstrumentedBean atb = ac.getBean("annotatedTimedBean", InstrumentedBean.class);

    ExecutorService exec = Executors.newFixedThreadPool(1);

    final Random r = new Random();
    exec.submit(new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        while ( true ) {
          try {
            int i = r.nextInt(100);
            atb.calculateSomething(i);
            TimeUnit.MILLISECONDS.sleep(1 + i);
          } catch (Exception ex) {
            // meh
          }
        }
      }
    });

    // may add something fancy like getOpt for param changing

  }
}
