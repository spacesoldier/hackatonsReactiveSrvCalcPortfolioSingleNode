package com.spacesoldat.hackathons;

import com.spacesoldat.hackathons.implementation.execution.routing.kstream.Predicates;
import com.spacesoldat.hackathons.streaming.manage.FluxWiresManager;
import com.spacesoldat.hackathons.streaming.transformers.kafka.LogicUnitKeyValueMapper;
import com.spacesoldat.hackathons.streaming.transformers.kafka.LogicUnitValueMapper;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@SpringBootApplication
public class ServiceApplication {

    @Bean
    public void envVarPrinter(){
        String unitName = "envVar-printer";
        Logger logger = LoggerFactory.getLogger(unitName);

        Map<String, String> env = System.getenv();

        logger.info("========== AAARRRRGZZZZZZZZZZZZZZZZ ===========");

        env.forEach(
                (varName, varVal) -> logger.info(
                                                    String.format(
                                                            "[ENV]: %s = %s",
                                                            varName,
                                                            varVal
                                                )
                )
        );

    }

    @Autowired @Qualifier("parseUpdateMessage")
    private LogicUnitKeyValueMapper parseUpdateMessage;

    @Autowired @Qualifier("handleLimitsUpdates")
    private LogicUnitValueMapper handleLimitsUpdates;

   // @Bean
    public Function<KStream<String,String>, KStream<Object,Object>[]> securityLimitsSubscribe(){
        return limitsUpdatesStream -> limitsUpdatesStream
                                        .flatMap(           parseUpdateMessage      )
                                        .flatMapValues(     handleLimitsUpdates     )
                                        .branch(
                                                Predicates.isLogMessage,
                                                Predicates.isError
                                        );
    }


    @Autowired @Qualifier("FluxWiringManager")
    private FluxWiresManager fluxManager;

    // log messages output
   // @Bean
    public Supplier<Flux<Message<String>>> logMessageAsyncOutput(){
        return () -> fluxManager.getStream("logMessageOutAdapter");
    }

    // error messages output
    //@Bean
    public Supplier<Flux<Message<String>>> errorMessageAsyncOutput(){
        return () -> fluxManager.getStream("errorMessageOutAdapter");
    }

    public static void main(String[] args) {

        // let's set a random string value to append it in the config
        // I decide to init it here because when you set it as ${random.uuid}
        // in application.yml it will be generated every time you read it from there
        // nevertheless, the test runs show that both approaches work the same way
        // (spring cloud stream kafka streams binder reads the applicationId value only once)
        // but, let's make sure it's not gonna change anyway

        // this trick is useful when we wanna ensure the topic will be read from the very beginning
        // each time the application starts

        System.setProperty("APP_RUN_UID", UUID.randomUUID().toString());



        SpringApplication application = new SpringApplicationBuilder(ServiceApplication.class).build();
        application.run(args);
    }

}