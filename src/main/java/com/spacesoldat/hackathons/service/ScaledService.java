package com.spacesoldat.hackathons.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

@ConditionalOnExpression("'${app.mode}' == 'scaled'") // Будет только в scaled приложении
@Service
public class ScaledService {

}
