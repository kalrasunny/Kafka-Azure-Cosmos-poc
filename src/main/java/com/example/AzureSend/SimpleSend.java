package com.example.AzureSend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class SimpleSend {



    public static void main(String[] args)
            throws EventHubException, ExecutionException, InterruptedException, IOException {

        final ConnectionStringBuilder connStr = new ConnectionStringBuilder()
                .setNamespaceName("LBGPOCEhubNS")
                .setEventHubName("LBGPOCEhub")
                .setSasKeyName("LBGPOCAuthRule")
                .setSasKey("d/9VQ2A/BJnwanay2nSnp/MqR9KEZz1VARoy0PbW+9I=;EntityPath=LBGPOCEhub");
        int id = 0;
        //Code to end events
        final Gson gson = new GsonBuilder().create();

        // The Executor handles all asynchronous tasks and this is passed to the EventHubClient instance.
        // This enables the user to segregate their thread pool based on the work load.
        // This pool can then be shared across multiple EventHubClient instances.
        // The following sample uses a single thread executor, as there is only one EventHubClient instance,
        // handling different flavors of ingestion to Event Hubs here.
        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

        // Each EventHubClient instance spins up a new TCP/SSL connection, which is expensive.
        // It is always a best practice to reuse these instances. The following sample shows this.
        final EventHubClient ehClient = EventHubClient.createSync(connStr.toString(), executorService);


        while (true) {


            //load consumer data
            final Consumer<Long, String> consumer = createConsumer();
            System.out.println("Polling");


            try {
                while (true) {
                    final ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
                    for (ConsumerRecord<Long, String> cr : consumerRecords) {
                        System.out.printf("Consumer Record:(%d, %s, %d, %d)\n", cr.key(), cr.value(), cr.partition(), cr.offset());
                        String messagePayload = cr.value();// "Message " + Integer.toString(i);
                        System.out.println("got messagePayload is" + messagePayload);

                        byte[] payloadBytes = gson.toJson(messagePayload).getBytes(Charset.defaultCharset());
                        EventData sendEvent = EventData.create(payloadBytes);

                        // Send - not tied to any partition
                        // Event Hubs service will round-robin the events across all Event Hubs partitions.
                        // This is the recommended & most reliable way to send to Event Hubs.
                        ehClient.sendSync(sendEvent);

                    }
                    consumer.commitAsync();
                }
            } catch (CommitFailedException e) {
                System.out.println("CommitFailedException: " + e);
            } finally {
                consumer.close();
                System.out.println(Instant.now() + ": Send Complete...");
                System.out.println("Press Enter to stop.");
                System.in.read();
            }
        }


//                // Serialize the event into bytes
//                byte[] payloadBytes = gson.toJson(messagePayload).getBytes(Charset.defaultCharset());
//
//                // Use the bytes to construct an {@link EventData} object
//                EventData sendEvent = EventData.create(payloadBytes);
//
//                // Transmits the event to event hub without a partition key
//                // If a partition key is not set, then we will round-robin to all topic partitions
//                ehClient.sendSync(sendEvent);
//
//                //  the partitionKey will be hash'ed to determine the partitionId to send the eventData to.
//                ehClient.sendSync(sendEvent, 1);
//
//                // close the client at the end of your program
//                ehClient.closeSync();




    }
            private static Consumer<Long, String> createConsumer () {
                try {

                    int id=0;
                    final Properties properties = new Properties();
                    synchronized (TestConsumerThread.class) {
                        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "KafkaExampleConsumer#" + id);
                        id++;
                    }
                    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
                    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

                    //Get remaining properties from config file
                    properties.load(new FileReader("src/main/resources/consumer.properties"));

                    // Create the consumer using properties.
                    final Consumer<Long, String> consumer = new KafkaConsumer<>(properties);

                    // Subscribe to the topic.
                    consumer.subscribe(Collections.singletonList("test"));
                    return consumer;

                } catch (FileNotFoundException e) {
                    System.out.println("FileNoteFoundException: " + e);
                    System.exit(1);
                    return null;        //unreachable
                } catch (IOException e) {
                    System.out.println("IOException: " + e);
                    System.exit(1);
                    return null;        //unreachable
                }

            }
}


