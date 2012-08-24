
# Introduction
Application instrumentation is one of those things, like test driven development, which I've come to consider as mission critical in writing good software. And, just like test driven development, it seems to always end up getting short-cutted, de-prioritized, or otherwise man-handled when crunch time comes. 

I've been around long enough now to know that when you need to do something like instrumenation, you just need to draw a line in the sand and execute. I did this recently at Apigee on the Usergrid platform, and I decided to write up instructions in a hope that folks will realize how easy this is to do now and how valueable it is in understanding how to scale and manage your applications. 

So stop what you are working on, create a new branch, and get ready to follow along at home. To get immediate return on time spent should not take more than an afternoon of hackery. Really the biggest bottleneck I found, was scoring the time from our very good by already overworked ops team to pull the trigger getting on a monitoring solution in place for us. 

# Conceptual Background
Instrumenting application behavior at the code level provides a critical feedback loop to both application developers and operations personnel about the runtime health of a system.

When coupled with a monitoring system which stores historical data, code instrumentation provides valuable information into application performance over time. With this information, it becomes easier to spot performance regressions and application hotspots as load and traffic patterns change (assuming those are monitored as well).

TODO historical info on the term instrumentation pre-software

# Getting Started With Instrumentation
As a first step, it's best to start with common dispatch mechanisms or system entry points, instrumenting them for execution time. For example, lets assume we have just instrumented the "authenticate" method of a UserService. This hypothetical UserService encapsulates the interactions with several distinct systems, retrieving user information from a data store and verifying a certificate against a key store.

Over time, we will build up a fairly reliable idea of the mean execution time and standard deviation of this method, 5ms with occasional spikes into 11ms let's say. We can now use this information to trigger events in our monitoring system. If we saw a gradual degradation which brought the average outside of standard deviation, we could make an assumption that there is a problem lurking under the hood - perhaps a missing index on a database lookup where the table has continued to grow. A warning can be raised to the monitoring system.

Additionally, if there were an immediate spike in execution time, say to 90ms, we can assume given this anomalous behavior of an order of magnitude execution time increase, there is a partial failure of one or more systems involved. We can trigger an alarm condition on our monitoring system and begin immediate diagnosis.

With exception metering, we gain powerful insights into the behavior of the system in failure situations and can respond pro-actively to services which are in a failure mode. For example, if the class returning a web resource begin to throw 500 errors, we could again raise immediate alarms, and have essentially real-time notification of component failure.

Along these same lines, the immediate presence of a large number UserPasswordMismatchExceptions thrown from our UserService might mean a brute force password attack was underway. The monitoring system could notify operations that evasive action was needed.

# Instrumentation Package
Within the [Usergrid](http://apigee.com/about/products/usergrid) platform at Apigee, we have chosen to use the following API for instrumentation:
[http://metrics.codahale.com/](http://metrics.codahale.com/)

Metrics was selected for the following reasons:

*  modular, well-structured API
*  annotation-driven instrumentation
*  plug-able interface for common monitoring systems (including graphite and munin)

# Instrumentation Example Project
A sample project has been created to demonstrate the most common and immediately useful features of instrumentation. The project consists of a main class that executes some hashing operations repeatedly to generate execution data. The operations are encapsulated into two different classes: one which uses the method-level annotations from the metrics api and the other which use the metrics API classes directly.

The only requirements for running the application are a JVM, git and a recent version of maven installed. The project is available on github at the following URL:
[project URL]

## Running the example application
1. Clone the git repository  
`git clone https://github.com/zznate/instrumentation-example` 

2. cd into the directory
3. Execute the sample application via maven with the following command:  
`mvn -e exec:java -Dexec.mainClass="com.apigee.instrumentation.example.InstrumentedBeanCli" `

4. __ctrl+c__ to kill the program (but only when you are satisfied with the results of the analysis of the application as described below).

## Analysing Instrumentation Output
For demonstration purposes, we are using JMX via one of the console utilities in the JDK to go over instrumentation output. In an actual deployment environment, one would typically configure a visualization system - we use graphite at Apigee - to store the output and collect data for trend analysis.

Analysing this data in pre-production environments (testing, staging, etc.) also makes it easier to spot performance regressions or degradations before a production deploy occurs. We have graphite servers in both production and staging environments, for example. 

An ideal place to be (we are not there yet unfortunately) would be to have performance information from the nightly smoke testing going into a dedicated graphite server as well. I'd be nice to see this in the morning and look for potential hotspots before they get any farther. 

Unfortunatley, setting up graphite is outside the scope of this doc and is uneccessary to get your head around the fundamentals. We'll use Java Visual VM to get started. 


## Using Java VisualVM
Java VisualVM (jvisualvm )should be available via command line with any recent JDK download. To use __jvisualvm__, type the following on the command line:

`$ jvisualvm`

The MBean inspector is not present by default so we have to configure its plugin before we can use it:

1. In the __Tools__ menu select __Plugins__
2. Go to the __Available Plugins__ tab
3. Select __VisualVM-MBeans__ (though we won’t use it in this tutorial, __Visual GC__ is handy as well)
4. Restart __jvisualvm__

Once __jvisualvm__ is running with the __VisiaulVM-MBeans__ plugin installed, start the demo application and attach __jvisualvm__ to the demo process.
[process title]


### Examining Output of the Annotations
Open up the details for: [detail tree] ...


## How to Add metrics To Your Code
As previously mentioned, the Metrics API can be annotation driven. Dropping this into crucial parts of your code via such will therefore be straightforward. It really should not take you more than an hour to tackle the most critical paths of your code base. There are currently four different annotations avaialble in the Metrics API:

- Metered
- Timed (contains metered)
- ExceptionMetered
- Counter (not shown, but simple enough) 

The actual metric types available from the Metrics API are slightly different than the annotations. I'm not going into detail on them because they are already [well documented](http://metrics.codahale.com/manual/core/) and I'm of the school of thought that instrumentation should be as minimially invasive as possible. Thus our focus on the annotations. TODO ref. back to original scope of instrumentation in industry if possible


### A Note for CDI/IoC Users
For folks using IoC containers - specifically Spring Framework or Google Guice - those packages have been moved out of the Metrics code base and are now being maintained by third parties. We'll focus on SpringFramework for the purposes of this tutorial since it's what we use on Usergrid.  

- Spring IoC injected (can use Guice as well)
- Deps required
- metrics namespace in context file
