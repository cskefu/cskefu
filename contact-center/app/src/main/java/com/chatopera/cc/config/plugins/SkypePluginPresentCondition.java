package com.chatopera.cc.config.plugins;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Skype渠道检测
 */
public class SkypePluginPresentCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return MainContext.hasModule(Constants.CSKEFU_MODULE_SKYPE);
    }
}
