package com.spacesoldat.hackathons.implementation.execution.routing.kstream;

import com.spacesoldat.hackathons.entities.internal.log.LogMessage;
import org.apache.kafka.streams.kstream.Predicate;

public class Predicates {

    public static Predicate<Object, Object> isError =
            (k, v) -> v != null && v instanceof LogMessage && ((LogMessage) v).getStatusCode() < 0;;

    public static Predicate<Object, Object> isLogMessage =
            (k, v) -> v != null && v instanceof LogMessage;
}

