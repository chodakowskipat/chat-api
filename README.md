# Support chat API

The goal of the project was to create a simple Chat API for a support service.


The expected workflow:
- customer creates an inquiry,
- support agent reads the message and assigns a support case to it,
- support agent responds to the customer and provides support,
- customer is able to provide additional information by sending follow-up messages.

### Documentation
API documentation is available at `/swagger-ui/index.html`.

### Running the app
1.
    ```shell
    ./gradlew bootRun
    ```
2.
    ```shell
   ./gradlew build
   java -jar build/libs/chatAPI-0.0.1-SNAPSHOT.jar
    ```
3. 
    ```shell
   docker compose up 
    ```

## API validation
1. Create a message from “Jérémie Durand” with the content: “Hello, I have an issue with my new phone”.
   ```shell
   curl \
      --header "Content-Type: application/json" \
      --request POST \
      --data @- \
      http://localhost:8080/api/v1/messages <<EOF
      {
        "userId": "123",
        "username": "Jérémie Durand",
        "content": "Hello, I have an issue with my new phone"
      }
   EOF
   ```
2. Create a client case, with the client name “Jérémie Durand”, and having the previously created message
   in its messages list.
   ```shell
   curl \
      --header "Content-Type: application/json" \
      --request POST \
      --data @- \
      http://localhost:8080/api/v1/support/cases <<EOF
      {
        "clientReference": "Jérémie Durand",
        "messages": [{
           "id": 1
        }]
      }
   EOF
   ```

3. Create a message from “Sonia Valentin”, with the following content:
   “I am Sonia, and I will do my best to help you. What is your phone brand and model?”. This message will
   be linked to the previously created client case.
   ```shell
   curl \
      --header "Content-Type: application/json" \
      --request POST \
      --data @- \
      http://localhost:8080/api/v1/messages <<EOF
      {
        "userId": "321",
        "username": "Sonia Valentin",
        "supportCaseId": 1,
        "content": "I am Sonia, and I will do my best to help you. What is your phone brand and model?"
      }
   EOF
   ```
   
4. Modification of the client case adding the client reference “KA-18B6”.
   This will validate the client case modification feature.
   ```shell
   curl \
      --header "Content-Type: application/json" \
      --request PATCH \
      --data @- \
      http://localhost:8080/api/v1/support/cases/1 <<EOF
      {
        "clientReference": "KA-18B6"
      }
   EOF
   ```

5. Fetching of all client cases. The result will only contain one client case, the one we created before.
   ```shell
   curl http://localhost:8080/api/v1/support/cases
   ```

## API performance testing
In order to test the performance of the application, an [Apache JMeter](https://jmeter.apache.org/) test plan has been prepared. It simulates a 
scenario matching the one from provided non-functional requirements. The test plan includes:
- 4 separate `thread groups` each simulating different group of users,
- HTTP request sections performing the actual HTTP calls to the app instance,
- multiple counters iterating over the resources being created,
- JSON extractors parsing the responses and using them in following requests.

The [test plan](support_chat_test.jmx) can be viewed and edited using JMeter's GUI.

The performance test should be run in a consistent environment matching the production machine performance, where other
processes won't influence it. This can be achieved by spinning up a ephemeral EC2 instance for example or by utilizing
a Kubernetes cluster.

Additionally, a `perf-testing-data.sql` file has been prepared which contains some predefined DB entries. This allows
the test to provide consistent results and ensures the requests are valid. An additional Spring profile has been created
which uses the file as an `sql init` script.

To run the performance test:
1. Run the app with `perf-testing` profile
   ```shell
   java -Dspring.profiles.active=perf-testing -jar build/libs/chatAPI-0.0.1-SNAPSHOT.jar
   ```
2. Run the test plan with `JMeter` cli mode
   ```shell
   jmeter -n -t support_chat_test.jmx -l jmeter-run.log -e -o build/jmeter
   ```
3. Stop the application.

The results, including the response time percentiles can be now viewed in a nice dashboard:
[report](report.png)