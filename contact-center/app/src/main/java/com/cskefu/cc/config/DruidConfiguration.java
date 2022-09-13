/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass(com.alibaba.druid.pool.DruidDataSource.class)
@ConditionalOnProperty(name = "spring.datasource.type", havingValue = "com.alibaba.druid.pool.DruidDataSource", matchIfMissing = true)
public class DruidConfiguration {
	
	@SuppressWarnings("unchecked")
    protected <T> T createDataSource(DataSourceProperties properties,
                                     Class<? extends DataSource> type) {
        return (T) properties.initializeDataSourceBuilder().type(type).build();
    }

    /**
     * @see org.springframework.boot.autoconfigure.jdbc.DataSourceConfiguration.Tomcat 仿写的你可以去了解
     * @param properties 读入的配置
     * @return DruidDataSource
     */
    @Bean
    @ConfigurationProperties("spring.datasource.druid")
    public com.alibaba.druid.pool.DruidDataSource dataSource(DataSourceProperties properties) {

        com.alibaba.druid.pool.DruidDataSource dataSource = createDataSource(properties, com.alibaba.druid.pool.DruidDataSource.class);

        DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(properties.determineUrl());

        String validationQuery = databaseDriver.getValidationQuery();
        if (validationQuery != null) {
            dataSource.setTestOnBorrow(true);
            dataSource.setValidationQuery(validationQuery);
        }

        return dataSource;
    }

    
	/**
	 * 注册一个StatViewServlet
	 * @return
	 */
	@Bean
	public ServletRegistrationBean DruidStatViewServle2(){
		//org.springframework.boot.context.embedded.ServletRegistrationBean提供类的进行注册.
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(),"/druid/*");
		//添加初始化参数：initParams
		//白名单：
//		servletRegistrationBean.addInitParameter("allow","127.0.0.1");
		//IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
//		servletRegistrationBean.addInitParameter("deny","192.168.1.73");
		//登录查看信息的账号密码.
		servletRegistrationBean.addInitParameter("loginUsername","admin");
		servletRegistrationBean.addInitParameter("loginPassword","123456");
		//是否能够重置数据.
		servletRegistrationBean.addInitParameter("resetEnable","false");
		return servletRegistrationBean;
	}

	/**
	 * 注册一个：filterRegistrationBean
	 * @return
	 */

	@Bean
	public FilterRegistrationBean druidStatFilter2(){
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
		//添加过滤规则.
		filterRegistrationBean.addUrlPatterns("/*");
		//添加不需要忽略的格式信息.
		filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid2/*");
		return filterRegistrationBean;
	}
}