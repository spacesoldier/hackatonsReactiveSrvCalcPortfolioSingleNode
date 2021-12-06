package com.spacesoldat.hackathons.implementation.execution.logic;

import com.spacesoldat.hackathons.caching.EntitiesCache;
import com.spacesoldat.hackathons.entities.internal.ShareInfo;
import com.spacesoldat.hackathons.entities.internal.queries.BuildClientsPortfolioRequest;
import com.spacesoldat.hackathons.entities.io.ClientPortfolioResponse;
import com.spacesoldat.hackathons.entities.kafka.LimitDto;
import com.spacesoldat.hackathons.entities.kafka.SecurityLimitsRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CalculateUserPortfolioImpl {

    public static final String unitName = "calculate-stage";
    private static final Logger logger = LoggerFactory.getLogger(unitName);

    public static Function<BuildClientsPortfolioRequest, List> calculateUserPortfolio(
            EntitiesCache snapshotsCache,
            EntitiesCache incrementsCache
    ){
        Gson gson = new GsonBuilder().create();

        return calcRequest -> {
            List results = new ArrayList<>();

            String userId = calcRequest.getUserId();

            Object lastUserSnapshotObj = snapshotsCache.findAllItemsByKey().apply(userId);

            SecurityLimitsRecord lastUserSnapshot = null;

            if (lastUserSnapshotObj != null){
                lastUserSnapshot = (SecurityLimitsRecord) lastUserSnapshotObj;
            }

            Object lastUserIncrementObj = incrementsCache.findAllItemsByKey().apply(userId);
            SecurityLimitsRecord lastUserIncrement = null;

            if (lastUserIncrementObj != null){
                lastUserIncrement = (SecurityLimitsRecord) lastUserIncrementObj;
            }

            if (lastUserSnapshot != null && lastUserIncrement != null){

                String lastSnapshotDateStr = lastUserSnapshot.getTimestamp();
                String lastInrementDateStr = lastUserIncrement.getTimestamp();

                Instant lastSnapTs = Instant.parse(lastSnapshotDateStr);

                Instant lastIncTs = Instant.parse(lastInrementDateStr);

                // wtf, it is square! I know! How dare I!
                if (lastIncTs.isAfter(lastSnapTs)){
                    // update timestamp and last operationId
                    lastUserSnapshot.setTimestamp(lastUserIncrement.getTimestamp());
                    lastUserSnapshot.setOperationId(lastUserIncrement.getOperationId());

                    for (int i=0; i<lastUserIncrement.getLimits().size(); i++){
                        for (int j=0; j<lastUserSnapshot.getLimits().size(); j++){
                            if (lastUserIncrement.getLimits().get(i).getTicker().equalsIgnoreCase(
                                    lastUserSnapshot.getLimits().get(j).getTicker()
                            )){
                                // we treat the last increment as the last update
                                // and we add it to the last balance
                                // but do not let the balance fall below zero

                                int lastInc = lastUserIncrement.getLimits().get(i).getBalance();
                                int lastBal = lastUserSnapshot.getLimits().get(j).getBalance();

                                if (lastInc > 0){
                                    int sum = lastInc + lastBal;
                                    lastUserSnapshot.getLimits().get(j).setBalance(sum);
                                } else {
                                    int diff = lastInc + lastBal;
                                    lastUserSnapshot.getLimits().get(j).setBalance(
                                            diff > 0 ? diff : 0
                                    );
                                }

                            }
                        }
                    }
                }

            }

            ClientPortfolioResponse response = ClientPortfolioResponse.builder()
                    .requestId(calcRequest.getOriginalRequest().getRqId())
                    .exchange(calcRequest.getOriginalRequest().getServerWebExchange())
                    .sharesInPortfolio(
                            calcSharesCosts(lastUserSnapshot.getLimits())
                    )
                    .build();

            results.add(response);

            logger.info(String.format("[STOCKS CALC RESULT]: %s",
                        gson.toJson(response)
                    ));

            return results;
        };
    }

    private static List<ShareInfo> calcSharesCosts(List<LimitDto> limits){
        List<ShareInfo> result = new ArrayList<>();

        // need to add ticker prices subscription from Redis!
        double tickerPrice = 300.3098;

        for (int i=0; i<limits.size(); i++){
            result.add(ShareInfo.builder()
                            .ticker(limits.get(i).getTicker())
                            .balance(limits.get(i).getBalance())
                            .totalCost(
                                    limits.get(i).getBalance()*tickerPrice
                            )
                    .build()
            );
        }

        return result;
    }

}
