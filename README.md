# Sample
This project is used to demonstrate a simple order processing system.

## Major Components
There're three major components in this small system: Web server, order dispatcher and order processor.

When an order arrives, the web server will persist that order with SCHEDULED status, and returns user the persisted order along with its id.

Order dispatcher executes periodically and checks whether there's new order(With SCHEDULED status) or blocked order(With IN_PROGRESS/ROLLING_BACK status, but no further action taken in one minute). Order dispatcher will determine the order processing node and record this information in order's processing_node field(This field is not visible for user).

Each order processor will pick the assigned orders and starts to process it. If all steps execute correctly, the order will be marked with COMPLETE, and the order's processing_node field's value will be removed. If there's any failure, the order processor will mark the order as ROLLING_BACK state, and starts to rollback completed steps. When finished, the order will be marked as FAILED.

These three components are fully simulated by spring scheduling, defined in configuration file scheduling-context.xml. Implementation codes locates at:

https://github.com/loveis715/Sample/tree/master/core/server/src/main/java/com/ambergarden/orderprocessor/dispatcher

https://github.com/loveis715/Sample/tree/master/core/server/src/main/java/com/ambergarden/orderprocessor/processor

And test cases for them locates at:

https://github.com/loveis715/Sample/tree/master/core/server/src/test/java/com/ambergarden/orderprocessor

## Order & Order Steps status
Order and order steps are different concepts. One order with ROLLING_BACK status may contain steps with COMPLETE, ROLLING_BACK and ROLL_BACKED status. So we have two set of status. One set is for order, the other set is for order step. Users can easily observe the current status of one order, the current step of that order, and operation we're doing on that step.


Order status includes: SCHEDULED, IN_PROGRESS, COMPLETE, ROLLING_BACK and FAILED. And order step status includes: SCHEDULED, IN_PROGRESS, COMPLETE, ROLLING_BACK and ROLLBACKED.

There may be two major state change paths for one order:

SCHEDULED -> IN_PROGRESS -> COMPLETE

SCHEDULED -> IN_PROGRESS -> ROLLING_BACK -> FAILED

And there may be three major state change paths for one step:

SCHEDULED -> IN_PROGRESS -> COMPLETE

SCHEDULED -> IN_PROGRESS -> ROLLING_BACK -> ROLLBACKED

SCHEDULED -> IN_PROGRESS -> COMPLETE -> ROLLING_BACK -> ROLLBACKED


For example, one order in ROLLING_BACK status may have the following step states:

Scheduling    Pre-processing Processing     Post-processing

COMPLETE      ROLLING_BACK   ROLLBACKED     ROLLBACKED

## Functional Components
DTO(data transfer object) is used to transfer information between client and server. The object stored in databases always contains some implementation details, such as order's processing_node field. Exposing these model objects directly is risky. All relative codes are in rest-stub project. It uses jaxb to generate DTO classes from xsd files.

DAL(data access layer) is used to access databases. All relative codes are in dal project. We have also add some converter to convert model objects retrieved from data access layer to data transfer objects.

We have provide audit logs in both order's CRUD, dispatching and processing. It is very useful for tracking, debugging and security.

We have provice global application exception handler, which can convert specific exceptions into responses with correct http status. It can also help to hide the internal stacktraces from users, which secures our service.

Thread scheduling is archieved by using spring scheduling. These class are ThreadPoolTaskExecutor(Run 50 threads for order processing) and scheduler configuration element(Which internally uses ThreadPoolTaskScheduler)

## Technical Decisions
Some of the technical decisions are made under the pressure of limited time. So I've add this section to indicate some future enhancements we can take.

We've used Spring framework in this sample project. I know it is a heavy weight framework with a hard learning curve. I'm familiar with it, and just using it for safe(since I have only three days).

One plan is to transmit ThreadPoolTaskScheduler into Spring Integration, which can help to really separate our web server, dispatcher and order processor. Spring Integration is a implementation of Enterprise Integration Pattern. It is very suitable for asynchronized, message-based systems. Please reference my blogs for detail:

http://www.cnblogs.com/loveis715/p/5185332.html

We've used RDBMS instead of NoSQL database. I can image that this is a high throughput system, and the write ratio will be high. Since the order steps will not be shared by orders, so RDBMS' normalization is not useful at all. As a result, a denomalized model will be a better solution. That's why NoSQL databases like Cassandra etc. will be a better choice. Spring also provides support for NoSQL databases via Spring Data Cassandra, Spring Data MongoDB etc. Please reference my blog post for more detail:

http://www.cnblogs.com/loveis715/p/5299495.html