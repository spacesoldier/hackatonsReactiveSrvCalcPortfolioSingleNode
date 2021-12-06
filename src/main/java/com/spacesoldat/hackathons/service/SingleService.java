package com.spacesoldat.hackathons.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

@ConditionalOnExpression("'${app.mode}' == 'single'") // Будет только в single приложении
@Service
public class SingleService {

}
