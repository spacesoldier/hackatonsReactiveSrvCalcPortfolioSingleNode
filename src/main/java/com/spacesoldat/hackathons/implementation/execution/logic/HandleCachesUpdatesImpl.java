package com.spacesoldat.hackathons.implementation.execution.logic;


import com.spacesoldat.hackathons.caching.EntitiesCache;
import com.spacesoldat.hackathons.entities.external.ClientProfile;
import com.spacesoldat.hackathons.entities.internal.log.LogHelper;
import com.spacesoldat.hackathons.entities.internal.log.LogMessage;
import com.spacesoldat.hackathons.entities.kafka.SecurityLimitsRecord;
import com.spacesoldat.hackathons.entities.redis.TickerValue;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class HandleCachesUpdatesImpl {

    private static final String unitName = "handle-updates";

    public static Function<SecurityLimitsRecord, LogMessage> saveSnapshotOrIncrement(
            EntitiesCache snapshotsCache,
            EntitiesCache incrementCache
    ){
        Map<String, EntitiesCache> chooseProperCache = new HashMap<>(){
                {
                    put("SNAPSHOT", snapshotsCache);
                    put("INCREMENT", incrementCache);
                }
        };

        String okStatusDescTemplate = "%s saved to cache";
        String errorStatusDescTemplate = "could not save %s to cache - unknown type";

        return update -> {

            String updateType = update.getType();

            LogMessage logMsg = LogHelper.prepareMessage(
                                                            update.getOperationId(),
                                                            unitName,
                                                            0,
                                                            String.format(okStatusDescTemplate,updateType),
                                                            "update saved"
                                                        );

            if (chooseProperCache.containsKey(updateType)){

                EntitiesCache properCache = chooseProperCache.get(updateType);

                Object existingRecordObj = properCache.findAllItemsByKey().apply(update.getClientId());

                if (existingRecordObj != null){
                    SecurityLimitsRecord existingRecord = (SecurityLimitsRecord) existingRecordObj;

                    Instant existingRecordTs = Instant.parse(existingRecord.getTimestamp());

                    Instant newRecordTs = Instant.parse(update.getTimestamp());

                    if (newRecordTs.isAfter(existingRecordTs)){
                        // we put in cache only newest records
                        properCache.addItem().accept(update.getClientId(), update);
                    }
                } else {
                    // when this is the first record for given clientId
                    properCache.addItem().accept(update.getClientId(), update);
                }
            } else {
                logMsg.setStatusCode(-100);
                logMsg.setStatusDesc(String.format(errorStatusDescTemplate, updateType));
                logMsg.setEvent("update failed");
            }

            return logMsg;
        };
    }

    public static Function<TickerValue, LogMessage> saveTicker(EntitiesCache tikersCache){

        String okStatusDescTemplate = "ticker %s saved to cache";
        String errorStatusDescTemplate = "could not save %s to cache";

        return update -> {

            String tickerName = update.getTickerName();

            LogMessage logMsg = LogHelper.prepareMessage(
                    tickerName,
                    unitName,
                    0,
                    String.format(okStatusDescTemplate,tickerName),
                    "ticker update saved"
            );

            tikersCache.addItem().accept(tickerName, update);

            return logMsg;
        };
    }


    public static Function<ClientProfile, LogMessage> saveUser(EntitiesCache usersCache){

        String okStatusDescTemplate = "user %s saved to cache";
        String errorStatusDescTemplate = "could not save %s to cache";

        return update -> {

            String userLogin = update.getClientLogin();

            LogMessage logMsg = LogHelper.prepareMessage(
                    userLogin,
                    unitName,
                    0,
                    String.format(okStatusDescTemplate,userLogin),
                    "user update saved"
            );

            usersCache.addItem().accept(userLogin, update);

            return logMsg;
        };
    }

}
