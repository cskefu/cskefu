package com.cskefu.cc.config;

import de.neuland.pug4j.spring.view.PugView;
import de.neuland.pug4j.spring.view.PugViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

public class PugCskefuViewResolver extends PugViewResolver {
    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        AbstractUrlBasedView view = super.buildView(viewName);
        if (viewName.startsWith("/resource/css")) {
            PugView pugView = (PugView) view;
            pugView.setContentType("text/css ; charset=UTF-8");
        }
        return view;
    }
}
