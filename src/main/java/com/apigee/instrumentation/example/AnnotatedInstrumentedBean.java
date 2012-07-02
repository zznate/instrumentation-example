package com.apigee.instrumentation.example;

import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import com.yammer.metrics.annotation.ExceptionMetered;
import com.yammer.metrics.annotation.Metered;
import com.yammer.metrics.annotation.Timed;
import org.apache.commons.lang.RandomStringUtils;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Shows example usage of Metrics annotation functionality
 *
 * @author zznate
 */
final class AnnotatedInstrumentedBean implements InstrumentedBean {

  private final Random random = new Random();
  protected final AtomicLong exceptionTracker;
  protected final long exceptionModulo;

  /**
   * Create this AnnotatedInstrumentedBean
   * @param exceptionModulo the number of times the main loop executes before an
   *                        exception is thrown
   */
  AnnotatedInstrumentedBean(long exceptionModulo) {
    this.exceptionModulo = exceptionModulo;
    exceptionTracker = new AtomicLong();
  }

  /**
   * Run a hash function on some randomly generated strings
   * @param modifier used as a primer to run the hash function a random number
   */
  @Timed(name = "TimedBean_Timed", group = "Annotated")
  @Metered(name = "TimeBean_Metered", group = "Annotated")
  @ExceptionMetered(name = "TimedBean_exceptions", group = "Annotated")
  public void calculateSomething(int modifier) {
    Preconditions.checkArgument(modifier > 0, "modifier must be greater than zero");

    long maybeThrow = exceptionModulo == 0 ? 0 : exceptionTracker.incrementAndGet();
    // give some additional probability of exceptions
    long loopCount = random.nextInt(modifier * 10);

    for (int x = 1; x<=loopCount; x++) {
      if ( maybeThrow > 0 && maybeThrow % exceptionModulo == 0 ) {
        throw new RuntimeException("ouch");
      }
      // should give some mild variance in performance of the hasher
      int v = ((x << 5) & 0xFF) + 1;
      Hashing.md5().newHasher()
             .putInt(x)
             .putString(RandomStringUtils.random(v))
             .hash();
    }
  }

}
