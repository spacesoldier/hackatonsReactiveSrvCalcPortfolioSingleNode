# hackatonsReactiveSrvCalcPortfolioSingleNode
Yes, I could probably find a better name for the project, but who cares


## Intro
This project was written during the hackaton at the beginning of December 2021. The case was formulated as following:
* This service must provide a list of user's balance by every position of his shares
* The information about updates of user's balance is provided in unsorted manner in form of "balance snapshots" and "balance increments" in one topic in Kafka by several unknown systems
* The topic in Kafka have 6 partitions, the key of the message is client's id
* The actual price of the shares is published in Redis by unknown system in form of ticker
* The service must compose the actual quantity of shares in user's portfolio
* The service must calculate the total cost by every position from user's portfolio using the latest value from tickers
* The user will provide his login and the service must call another service (user catalog) by Http to obtain client's id to match the message stream in Kafka topic with corresponding user's login
* The service will be deployed in Kubernetes cluster in scale of up to 3 pods
* You have a chance to reconfigure the internal logic in same code base using the information about scale. For obvuious reasons this "feature" was skipped. It's not a good practice to implement microservice system based on monolythic code base. But just for historic consistency let's say that information about scale is provided to the service instance through the environment variables and could be:
** this instance is running being "single" in scale of 1
** this instance is running being "scaled" in scale of 2
** as a result you may have two different services using same code base, only one of them will be able to receive http requests
 

# service structure
The service implemented as a multilayer composition of elements. Each element works as a granulated part of the logic with defined interface which allows to connect them together by the wish of the architect/lead developer/you as a man who wears all the hats one by one during the development process. Of course, the structure is far from the perfectness, so now it's just provided as is in this repository.

Due to the requirements the main structure of the service is implemented as several chains of subscripers to reactive streams. The subscribers receive the objects of defined format and produce some new objects which are delivered to other subscribers. Moreover there is a way to send a message to defined reactive stream using some common format of the message and stream name.

The structure could be divided into five layers:
* Http API
* The main logic
* Data layer
* Steams and internal cache management


# Http API
Implements the communication with user. It's a very simple part and this layer contain only a couple elements. The service runs on top of Netty, using spring-boot-starter-webflux. The first elements communicates with Netty. His logic is the following:
* Receive the requests
* Give the request body and all the information about it to internal logic by calling a handler function
* Receive a Mono object provided by a handler function
* Return that Mono object to Netty

## Handlers
This element implements the communication between Http API and the internal logic of the service. Its logic looks the following way:
* Receive a request from API
* Generate an unique id for this request (uses UUID)
* Register a Mono object in Mono cache (will be described late)
* Wrap the received request in a message object of defined format
* Drop this message into the 
* Return this mono to API


# The main logic

This is a main element of the service and could be treated as the separate layer. It communicates with the data sources and with request handlers using the reactive streams by subscription to the named pairs of Sinks and Flux objects. The parts of the logic layer assembled in chains of such subscribers and could be treated as sublayers or submodules of the service. Their structure allow to build several separate microservices using the same basic idea of the current implementation. Every one of such a services will simply have less logic inside.
The logics part of the service consists of the following reactive streams (along with their subscribers):

## "updates" stream
The main purpose is to receive updates from whatever datasource. The list of subscribers to this stream looks like this:
* "saveUpdatesToCaches" element, which does the following:
  * receive an update message
  * decide which internal cache storage to put it to
  * apply some optional logic
  * write the received update to the dedicated cache

## "user_catalog_call" stream
The main purpose is to perform calls to the external API in separate thread pool. There are two elements subscribed to the stream:
* **prepareCallSpec** element, which does the following
  * receive a message which contains the request data and the aggregation object
  * prepare a WebClient.RequestBodySpec object
  * wrap WebClient.RequestBodySpec with the aggregation object into a message of defined format
  * drop this message downstream
* **performCatalogCall** element which invokes the request to an external API which provides the user data. The logic is the following:
  * receive a message which contains a WebClient.RequestBodySpec
*  initiate a call using retrieve() method of WebClient.RequestBodySpec
  * connect the handler function for processing the following response statuses:
    * success - drops a message with aggregate object to the provided success handler
    * error 3xx - drops a message with aggregate object to the provided fail handler
    * error 4xx - drops a message with aggregate object to the provided fail handler
    * error 5xx - drops a message with aggregate object to the provided fail handler
The handlers defined above are implemented as a separate elements which have a routing purpose:
**success handler** acts the following way:
* saves the received user data into the internal cache of the servide 
* drop the aggregate object (which is the original request received by the service) into the "serve" stream
** fail handler ** does the following:
* generates an error message which contains the information about error and the aggregate objeect
* drops the error message into the "serve" stream

## "serve" stream
This is the chain which implements the main logic service. It consists of the elements which purpose is to build the result using the data from the internal service caches and to provide it to the communication layer by publishing it into the Sink objects (coupled with Mono objects which are already provided to Netty). Let's describe all the elements of this chain one by one:
* **checkUserData** forwards the requests downstream for the users who's client id is already cached, otherwise it generates a request to the 
* **calculatePortfolio** does the following:
  * takes the last "snapshot" of the user's portfolio from internal cache
  * takes the last "increment" of the user's portfolio 
  * compares the timestamps
  * if the "increment" was received after the last "snapshot" then treat it as an update of the balance and add the difference to the values from a "snapshot"
  * return the new "snapshot" of the portfolio
* **calcTotalForPositions** not implemented due missing integration with tickers from Redis (lack of documentation and time for investigations during hackathoon)
* **sendResponseBodyToOutput** takes the results of the previous stages and provides them to the communication layer by publishing the responses into Sinks (backed by Mono objects which are provided to Netty). In more details:
  * successfully calculated results are converted into strings
  * then they are published into sinks corresponding to the request id
  * take the aggregate object provided in request and:
    ** set the Http status OK
    ** set the provided Mono object as a parameter of response's writeWith() method
  not implemented yet, but definitely should (as the failed requests are sent into stream from the fail handler of the user catalog call chain)
  * fail results are published into sinks corresponding to the request id
  * take the aggregate object provided in request and:
    ** set the Http status 404 in case when user Id was not found in catalog or 400 in other cases
    ** set the provided Mono object as a parameter of response's writeWith() method
* **routeToFluxByName** receives the "envelope" messages drops the payload objects into other reactive streams of this service according to their names written on the "envelope"

# Data layer

## integration with Kafka
This part of the service is implemented as a subscription to the topic with user balance updates and increments. In contain no logic: the incoming message stream is forwarded to "updates" reactive stream of the service. Also this layer has two outputs for logging events and error messages for monitoring/troubleshooting purposes. Unfortunately it was useless because the Kafka instance was created in same test deployment and died every time when the "test" session expired. Which was completely wrond decision by organizers: they could provide separate input topics for test streams but leave a chance to developers to monitor their services and give any idea what happens in test scenarios

## integration with Redis
This part of the service was not implemented due to lack of documentation provided by hackathon organizers. The way it was supposed to use is a sort of subscription to keyspace notifications. But due to inability to view logs of application and no chance to explore the connection to actual Redis instance provided in test environment this part will be implemented later, I believe in my own test environment and maybe in different case

## integration with user catalog (external Http API)
Implemented as a part of internal logic. Needs more abstraction from the main logic level


# Streams and internal caches management

This layer of the service implementation could be treated as a microframework or as a set of the basic elements for bulding reactive services for different purposes. It contains several elements, let's look at them a bit closer.

## Managing Sink-Mono objects
Firstly we take a look at the Mono objects. It's a source of events which could have a number of subscribers. The main feature of Mono objects is the fact they could emit only one event in a lifetime. We use them to provide a results asynchronously. For example, we provide a Mono object as a result of the Http request handler call. Then we need to store this Mono somewhere internally to be able to publish the response when it is ready.

To have the ability to publish the results of the service execution, we construct Mono objects as an "output" of the Sink object. This Sink object and the link to its mono could be treated together as an one-off disposable "wire", so the corresponding class could be found in project as MonoWire. To delegate create/get operations the MonoWiresManager was designed.

## Managing Sink-Flux objects
Now about Flux objects. The principle is the same: to obtain a Sink (Sink.Many) object for publishing some data and provide a Flux to subscribers. Lets say it look like long living "wires" and call them FluxWires. The operations with such stuff are provided by FluxWiresManager

## Managing internal caches
Every part of the internal service logic has an option to use caches. They are provided like a wrap around the concurrent hash maps with a little amount of logic. Such am implementation called EntityStorage. Every entity storage could know the type (a class) of the objects stored inside the hash map, can store  the objects with several different policies. For example: rewrite the object if its key is already in map, or add it to the list of the objects stored the hash map.
The caching may be improved in future, I would appreciate any ideas


