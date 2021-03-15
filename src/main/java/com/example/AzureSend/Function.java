package com.example.AzureSend;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.Cardinality;
import com.microsoft.azure.functions.annotation.CosmosDBOutput;
import com.microsoft.azure.functions.annotation.EventHubTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    @FunctionName("processSensorData")
    public void processSensorData(
            @EventHubTrigger(
                    name = "msg",
                    eventHubName = "lbgpoceventhub1", // LBGPOCEhub blank because the value is included in the connection string
                    cardinality = Cardinality.ONE,
                    connection = "EventHubConnectionString")
                    String item,
            @CosmosDBOutput(
                    name = "databaseOutput",
                    databaseName = "lbgcosmoskespace",
                    collectionName = "lbgcassandracolln",
                    connectionStringSetting = "CosmosDBConnectionString")
                    OutputBinding<PostalAddressDTO> document,
            final ExecutionContext context) {

        context.getLogger().info("Event hub message received: " + item.toString());
        PostalAddressDTO dto=new PostalAddressDTO(item,item,1);

        document.setValue(dto);

    }

}
