/*
 * Copyright (C) 2020 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cskefu.cc.config;

import org.springframework.beans.factory.annotation.Value;
import de.neuland.pug4j.PugConfiguration;
import de.neuland.pug4j.spring.template.SpringTemplateLoader;
import de.neuland.pug4j.spring.view.PugViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;

@Configuration
public class PugConfig {
    @Value("${spring.pug4j.cache}")
    private Boolean pug4jCache;

    @Value("${spring.pug4j.template-loader-path}")
    private String templatePath;

    @Bean
    public SpringTemplateLoader templateLoader() {
        SpringTemplateLoader templateLoader = new SpringTemplateLoader();
        templateLoader.setTemplateLoaderPath(templatePath);
        templateLoader.setEncoding("UTF-8");
        templateLoader.setSuffix(".pug");
        return templateLoader;
    }

    @Bean
    public PugConfiguration pugConfiguration() {
        PugConfiguration configuration = new PugConfiguration();
        configuration.setCaching(pug4jCache);
        configuration.setTemplateLoader(templateLoader());
        return configuration;
    }

    @Bean
    public ViewResolver viewResolver() {
        PugViewResolver viewResolver = new PugCskefuViewResolver();
        viewResolver.setConfiguration(pugConfiguration());
        viewResolver.setOrder(0);
        viewResolver.setSuffix(".pug");
        return viewResolver;
    }
}
