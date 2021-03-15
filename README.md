# KafkaAzureCosmos

The project is an integeration workflow from Kafka topic -> Azure event hub -> Cosmos DB.

Pre requisite :

1) Kafka topic running on on prem with below configuration :
Consumer properties :
bootstrap.servers=localhost:9092
group.id=test-consumer-group

Producer properties :
bootstrap.servers=localhost:9092
security.protocol=SASL_SSL
Kafka topic name : test

2)Cosmos DB and Azure event hub configured on Azure cloud
 "EventHubConnectionString": "Endpoint=sb://lbgcosmoscassandrans.servicebus.windows.net/;SharedAccessKeyName=lbgauthname;SharedAccessKey=EFpH9bZRuLuCl1ueMyu0dl18zh9Kg6GLAfJVFbYE2Xw=;EntityPath=LBGCosmosCassandraEhub",
 "CosmosDBConnectionString": "AccountEndpoint=https://lbgcosmosdbactcassapi.documents.azure.com:443/;AccountKey=riCbyj8SusDaaK78QY48mG1ERlwOX2aRKhrr50tt3a95LijiQRaIJPrOu9CZUi0BxKIuPLlq3NZQP86BYRCTVg==;"
 
Usages:

SimpleSend.java -> When ever an event is produced on kafka topic the SimpleSend will listen to that event send to event hub.
Function.java -> The Azure function (Function class) will create an Azure function that will listen to any event published on Azure event hub and 
then will produce the output in Cosmos DB. 

