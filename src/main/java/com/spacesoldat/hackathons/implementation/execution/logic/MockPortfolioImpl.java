package com.spacesoldat.hackathons.implementation.execution.logic;


import com.spacesoldat.hackathons.entities.internal.ShareInfo;
import com.spacesoldat.hackathons.entities.io.ClientPortfolioRequest;
import com.spacesoldat.hackathons.entities.io.ClientPortfolioResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

public class MockPortfolioImpl {
    public static final String unitName = "mock-stage";
    private static final Logger logger = LoggerFactory.getLogger("mock-logic");

    public static Function<ClientPortfolioRequest, List> mockClientPortfolio(){
        String logMsgTemplate = "[%s]: mock portfolio for %s";

        Gson gson = new GsonBuilder().create();

        return request -> {
            List results = new ArrayList<>();

            ClientPortfolioResponse response = ClientPortfolioResponse.builder()
                                                                            .requestId(request.getRqId())
                                                                            .exchange(request.getServerWebExchange())
                                                                            .sharesInPortfolio(
                                                                                    getSomeShares(
                                                                                            request.getClientLogin()
                                                                                    )
                                                                            )
                                                                        .build();

            results.add(response);

            String logMsg = String.format(
                    logMsgTemplate,
                    unitName,
                    request.getClientLogin()
            );

            logger.info(logMsg);
            logger.info(String.format("[RESPONSE MOCK]: %s", gson.toJson(response.getSharesInPortfolio())));

            results.add(logMsg);

            return results;
        };
    }

    private static List<ShareInfo> getSomeShares(String login){
      List<ShareInfo> result = new ArrayList<>();



      Random random = new Random();

      // add some Gazprom shares
      int gpBal = random.nextInt(999)+1;
      int gpPrice = 346;
      result.add(
              ShareInfo.builder()
                        .balance(gpBal)
                        .ticker("GAZP")
                        .totalCost(gpBal*gpPrice)
                        .operationId(UUID.randomUUID().toString())
                      .build()
            );

        // add some Sber shares
        int sbBal = random.nextInt(999)+1;
        int sbPrice = 346;
        result.add(
                ShareInfo.builder()
                            .balance(sbBal)
                            .ticker("SBER")
                            .totalCost(sbBal*sbPrice)
                            .operationId(UUID.randomUUID().toString())
                        .build()
        );

        // add some Yandex shares
        int yaBal = random.nextInt(999)+1;
        int yaPrice = 346;
        result.add(
                ShareInfo.builder()
                            .balance(yaBal)
                            .ticker("YNDX")
                            .totalCost(yaBal*yaPrice)
                            .operationId(UUID.randomUUID().toString())
                        .build()
        );

      return result;
    }
}
