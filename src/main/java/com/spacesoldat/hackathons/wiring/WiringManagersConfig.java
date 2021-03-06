package com.spacesoldat.hackathons.wiring;

import com.spacesoldat.hackathons.streaming.manage.FluxWiresManager;
import com.spacesoldat.hackathons.streaming.manage.MonoWiresManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class WiringManagersConfig {
    @Bean(name="FluxWiringManager")
    public FluxWiresManager initFluxManager(){
        return new FluxWiresManager();
    }

    @Bean(name="MonoWiringManager")
    public MonoWiresManager initMonoManager(){
        return new MonoWiresManager();
    }
}
