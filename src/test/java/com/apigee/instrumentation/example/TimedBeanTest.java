package com.apigee.instrumentation.example;

import com.apigee.instrumentation.example.InstrumentedBean;
import com.apigee.instrumentation.example.InstrumentedBeans;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Verify arguments, parameters and configuration of our instrumentation
 * examples
 *
 * @author zznate
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "/appContext.xml")
public class TimedBeanTest {

  @Resource
  private InstrumentedBean annotatedTimedBean;

  /**
   * Verify that we throw a runtime exception at least once during call
   */
  @Test(expected = RuntimeException.class)
  public void withException() {
    for (int x=0; x<10; x++) {
      annotatedTimedBean.calculateSomething(10);
    }
  }

  /**
   * Should not get exceptions if we send 0 for exceptionModule
   */
  @Test
  public void skipException() {
    annotatedTimedBean = InstrumentedBeans.metricsAnnotated(0);
    for ( int x=0; x<10; x++) {
      try {
        annotatedTimedBean.calculateSomething(10);
      } catch (RuntimeException re) {
        fail("Threw exception when we should not have");
      }
    }
  }

  /**
   * Verify argument sanity
   */
  @Test(expected = IllegalArgumentException.class)
  public void illegalArgs() {
    InstrumentedBeans.metricsAnnotated(5).calculateSomething(0);
  }
}
