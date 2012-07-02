package com.apigee.instrumentation.example;

/**
 * Static factory for initializing implementations of {@link InstrumentedBean}
 *
 * @author zznate
 */
public final class InstrumentedBeans {

  /**
   * Creates a {@link InstrumentedBean} implementation using the Metrics API annotatios:
   * <a href="http://metrics.codahale.com">http://metrics.codahale.com</a>
   *
   * @param exceptionModulo see {@link AnnotatedInstrumentedBean#AnnotatedInstrumentedBean(long)} for details
   * @return an instance of {@link AnnotatedInstrumentedBean}
   */
  public static InstrumentedBean metricsAnnotated(long exceptionModulo) {
    return new AnnotatedInstrumentedBean(exceptionModulo);
  }

}
