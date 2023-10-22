create database if not exists nacos_config default charset utf8 collate utf8_unicode_ci;
use nacos_config;

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info   */
/******************************************/
CREATE TABLE `config_info` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                               `data_id` varchar(255) NOT NULL COMMENT 'data_id',
                               `group_id` varchar(128) DEFAULT NULL,
                               `content` longtext NOT NULL COMMENT 'content',
                               `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
                               `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                               `src_user` text COMMENT 'source user',
                               `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
                               `app_name` varchar(128) DEFAULT NULL,
                               `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
                               `c_desc` varchar(256) DEFAULT NULL,
                               `c_use` varchar(64) DEFAULT NULL,
                               `effect` varchar(64) DEFAULT NULL,
                               `type` varchar(64) DEFAULT NULL,
                               `c_schema` text,
                               `encrypted_data_key` text NOT NULL COMMENT '秘钥',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info_aggr   */
/******************************************/
CREATE TABLE `config_info_aggr` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                    `data_id` varchar(255) NOT NULL COMMENT 'data_id',
                                    `group_id` varchar(128) NOT NULL COMMENT 'group_id',
                                    `datum_id` varchar(255) NOT NULL COMMENT 'datum_id',
                                    `content` longtext NOT NULL COMMENT '内容',
                                    `gmt_modified` datetime NOT NULL COMMENT '修改时间',
                                    `app_name` varchar(128) DEFAULT NULL,
                                    `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='增加租户字段';


/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info_beta   */
/******************************************/
CREATE TABLE `config_info_beta` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                    `data_id` varchar(255) NOT NULL COMMENT 'data_id',
                                    `group_id` varchar(128) NOT NULL COMMENT 'group_id',
                                    `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
                                    `content` longtext NOT NULL COMMENT 'content',
                                    `beta_ips` varchar(1024) DEFAULT NULL COMMENT 'betaIps',
                                    `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
                                    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `src_user` text COMMENT 'source user',
                                    `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
                                    `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
                                    `encrypted_data_key` text NOT NULL COMMENT '秘钥',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_beta';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info_tag   */
/******************************************/
CREATE TABLE `config_info_tag` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                   `data_id` varchar(255) NOT NULL COMMENT 'data_id',
                                   `group_id` varchar(128) NOT NULL COMMENT 'group_id',
                                   `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
                                   `tag_id` varchar(128) NOT NULL COMMENT 'tag_id',
                                   `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
                                   `content` longtext NOT NULL COMMENT 'content',
                                   `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
                                   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                   `src_user` text COMMENT 'source user',
                                   `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_tag';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_tags_relation   */
/******************************************/
CREATE TABLE `config_tags_relation` (
                                        `id` bigint(20) NOT NULL COMMENT 'id',
                                        `tag_name` varchar(128) NOT NULL COMMENT 'tag_name',
                                        `tag_type` varchar(64) DEFAULT NULL COMMENT 'tag_type',
                                        `data_id` varchar(255) NOT NULL COMMENT 'data_id',
                                        `group_id` varchar(128) NOT NULL COMMENT 'group_id',
                                        `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
                                        `nid` bigint(20) NOT NULL AUTO_INCREMENT,
                                        PRIMARY KEY (`nid`),
                                        UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
                                        KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_tag_relation';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = group_capacity   */
/******************************************/
CREATE TABLE `group_capacity` (
                                  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `group_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
                                  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
                                  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
                                  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
                                  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数，，0表示使用默认值',
                                  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
                                  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
                                  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='集群、各Group容量信息表';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = his_config_info   */
/******************************************/
CREATE TABLE `his_config_info` (
                                   `id` bigint(20) unsigned NOT NULL,
                                   `nid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                   `data_id` varchar(255) NOT NULL,
                                   `group_id` varchar(128) NOT NULL,
                                   `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
                                   `content` longtext NOT NULL,
                                   `md5` varchar(32) DEFAULT NULL,
                                   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   `src_user` text,
                                   `src_ip` varchar(50) DEFAULT NULL,
                                   `op_type` char(10) DEFAULT NULL,
                                   `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
                                   `encrypted_data_key` text NOT NULL COMMENT '秘钥',
                                   PRIMARY KEY (`nid`),
                                   KEY `idx_gmt_create` (`gmt_create`),
                                   KEY `idx_gmt_modified` (`gmt_modified`),
                                   KEY `idx_did` (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='多租户改造';


/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = tenant_capacity   */
/******************************************/
CREATE TABLE `tenant_capacity` (
                                   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `tenant_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Tenant ID',
                                   `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
                                   `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
                                   `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
                                   `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数',
                                   `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
                                   `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
                                   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='租户容量信息表';


CREATE TABLE `tenant_info` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                               `kp` varchar(128) NOT NULL COMMENT 'kp',
                               `tenant_id` varchar(128) default '' COMMENT 'tenant_id',
                               `tenant_name` varchar(128) default '' COMMENT 'tenant_name',
                               `tenant_desc` varchar(256) DEFAULT NULL COMMENT 'tenant_desc',
                               `create_source` varchar(32) DEFAULT NULL COMMENT 'create_source',
                               `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
                               `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
                               KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='tenant_info';

CREATE TABLE `users` (
                         `username` varchar(50) NOT NULL PRIMARY KEY,
                         `password` varchar(500) NOT NULL,
                         `enabled` boolean NOT NULL
);

CREATE TABLE `roles` (
                         `username` varchar(50) NOT NULL,
                         `role` varchar(50) NOT NULL,
                         UNIQUE INDEX `idx_user_role` (`username` ASC, `role` ASC) USING BTREE
);

CREATE TABLE `permissions` (
                               `role` varchar(50) NOT NULL,
                               `resource` varchar(255) NOT NULL,
                               `action` varchar(8) NOT NULL,
                               UNIQUE INDEX `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
);

INSERT INTO users (username, password, enabled) VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', TRUE);

INSERT INTO roles (username, role) VALUES ('nacos', 'ROLE_ADMIN');

INSERT INTO tenant_info (id, kp, tenant_id, tenant_name, tenant_desc, create_source, gmt_create, gmt_modified) VALUES (1, '1', 'cskefu', 'cskefu', '春松客服', 'nacos', 1697443534513, 1697443534513);


INSERT INTO his_config_info (id, nid, data_id, group_id, app_name, content, md5, gmt_create, gmt_modified, src_user, src_ip, op_type, tenant_id, encrypted_data_key) VALUES (1, 1, 'common.json', 'cskefu-default', '', '{
  "spring": {
    "cloud": {
      "nacos": {
        "discovery": {
          "server-addr": "nacos:8848"
        }
      }
    },
    "datasource": {
      "driver-class-name": "com.mysql.cj.jdbc.Driver",
      "password": 123456,
      "url": "jdbc:mysql://mysql:3306/cskefu?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=GMT%2B8",
      "username": "root"
    },
    "mvc": {
      "static-path-pattern": "/smart-doc/**"
    },
    "rabbitmq": {
      "addresses": "rabbitmq",
      "listener": {
        "simple": {
          "acknowledge-mode": "manual",
          "prefetch": 1
        }
      },
      "password": "guest",
      "port": 5672,
      "publisher-confirm-type": "correlated",
      "publisher-returns": true,
      "username": "guest"
    },
    "web": {
      "resources": {
        "static-locations": "classpath:/smart-doc"
      }
    }
  },
  "cskefu": {
    "private-key": "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC8TSxdMp76dZS2L8DpvJOgHNJmeI8PM30CFSeVMCHV5dEjtixipsMsSQu0ariAtVfA5M/ma1wcqEpsDO9nVQRsmsJCKMIWOcR6yBQU9f0T7Hkmil4Ckf61C1R7wLrynOjCeUCneQ7R0+dg9K25CymclPnK4ym2GInmuUjdbyQQcX4QBuIbmuY10MG8nW9ovhFML85KoBk5yxV4zVwLGP4TFJVnBmiNO3XSKL2XsX7wFFO93O8jmAnzgpWj8khsZAYG4nijIz2BciS9uQFa/lUX6yD50ykjNklbhCBIMGaQ6rr1WjnwLHDZU9sLvcYMboKPJHWz1FttvxuKSLYbf7j/AgMBAAECggEAEH1rPSrwF47kdW/vibu+uDuat7Tz3w9RpM6lc/Mn6ctoA6PLqN5hvFpJPbhHoAqn1kSZ0EJNA4imIjNsluf8rMyAs1QbN+CnMpMC7zL7jrS1IO+ze+LwVnpyufbbm3nfCE85OKSC7IbAfhHvUYOHIvQI5Zn76FTamFHxh5klpCrY1h8gcVWdRxiK0Q8AQP7KD0QnkzgROhkRmdI01639VyTCTc6Rtv5puU6J6idJwTd3JAiJXZ2qcoAwhnkSHnVnP5ktFxl66eHGJKeol6bGcJIFQRGLVmh78x6MXoqkJ2M7DRntL38TLKT5KCYViGxUpO0gED/HQBhDALyNevJZQQKBgQDr+pZDtRIQ6mMZMswKTo2+zIFEUFES2sR/bunEW5snPR3krKdgzJoEg7WERkMviSlvW6K7a6VeJ9KKO/iE07lcRkN2NQZUkBDsrNRgoOwB17hHJqrMyZ1It+hZmPQTkeyJu/BCxDkIMwbQYXxIe8J/dVYjHJVXZgzPqEXKYqeViwKBgQDMRwr7ceW+9c/GzHd7p6849s3+MbT0kzyXDTdeJkQFSDPufYapTCan/MrdJmnIRz8pq2URZrO4kDDxxeWu2Vkekv9cxVFrQVFfvuq+4BFpQvYkRHk9PDzCmgFJD/wLG8gzNTH7UahaNb7m0tPB3D1g5Q6LzZCc1h3s2K6SLS7g3QKBgE41Bof6ArrIc39ubmEcF64caNsTI0t0ZZs2TxNcqNcgUj/vWKmkJYdJf2cPQkUG2Eynug8TZgMGf6iAp6Sd5tjGEKWkfSyZcoJ95QUBUDZsIA60qfak+xOWn9LR9lJmElazirUWAzDMeH2nUWFUYumLIbkRSA1nLOfFhRvGBnRxAoGBAJvDAAjCzGBTpt77QZA0SFOzPVc6J7TmICk9lp5fpzYv3AlaBbhJrKAjDbybccWZLfxkCGjAWwG8UNXKBFzStjWt+LGQc4jJAXd0aCKrUBtnR7BX1epvaBUqwRgo7BK8WGdThI0RssE2gh4XXAhSGysq/XB0inRMf/z9K/+iHECxAoGAFlcIZ4/xXhbObUPU05hZFoj+zN5obaOLefRJn4uqjD9/VSRnI2bBv7vXKoLX6BwFF/PWvg1hzGy3O9TQ4XYPOqNGcBFDOqwxmVVevzXgRInoiRNP3xZXcqswppEbqORGbRoPLQBqLnPx1GKwbo7Fp9O5ywARVZtf4STt3pRK8PY=",
    "public-key": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvE0sXTKe+nWUti/A6byToBzSZniPDzN9AhUnlTAh1eXRI7YsYqbDLEkLtGq4gLVXwOTP5mtcHKhKbAzvZ1UEbJrCQijCFjnEesgUFPX9E+x5JopeApH+tQtUe8C68pzownlAp3kO0dPnYPStuQspnJT5yuMpthiJ5rlI3W8kEHF+EAbiG5rmNdDBvJ1vaL4RTC/OSqAZOcsVeM1cCxj+ExSVZwZojTt10ii9l7F+8BRTvdzvI5gJ84KVo/JIbGQGBuJ4oyM9gXIkvbkBWv5VF+sg+dMpIzZJW4QgSDBmkOq69Vo58Cxw2VPbC73GDG6CjyR1s9Rbbb8biki2G3+4/wIDAQAB",
    "token": {
      "duration": 3600
    }
  }
}', '071dd46c6f4ae097aefdd566b0ec47d2', '2023-10-16 08:37:00', '2023-10-16 08:37:01', 'nacos', '172.20.0.1', 'I', 'cskefu', '');
INSERT INTO his_config_info (id, nid, data_id, group_id, app_name, content, md5, gmt_create, gmt_modified, src_user, src_ip, op_type, tenant_id, encrypted_data_key) VALUES (0, 2, 'cskefu-auth-service.json', 'cskefu-default', '', '{
    
}', 'f309d0a46b7f7d41270b5684c31acd11', '2023-10-16 08:39:31', '2023-10-16 08:39:32', null, '172.20.0.1', 'I', 'cskefu', '');
INSERT INTO his_config_info (id, nid, data_id, group_id, app_name, content, md5, gmt_create, gmt_modified, src_user, src_ip, op_type, tenant_id, encrypted_data_key) VALUES (0, 3, 'cskefu-channel-wechat-service.json', 'cskefu-default', '', '{
    
}', 'f309d0a46b7f7d41270b5684c31acd11', '2023-10-16 08:39:43', '2023-10-16 08:39:44', null, '172.20.0.1', 'I', 'cskefu', '');
INSERT INTO his_config_info (id, nid, data_id, group_id, app_name, content, md5, gmt_create, gmt_modified, src_user, src_ip, op_type, tenant_id, encrypted_data_key) VALUES (0, 4, 'cskefu-web-gateway.json', 'cskefu-default', '', '{
    "spring": {
        "cloud": {
          "sentinel": {
            "transport": {
              "dashboard": "sentinel-dashboard:9850",
              "port": 9851
            }
          }
        }
    }
}', 'f309d0a46b7f7d41270b5684c31acd11', '2023-10-16 08:39:57', '2023-10-16 08:39:58', null, '172.20.0.1', 'I', 'cskefu', '');
INSERT INTO his_config_info (id, nid, data_id, group_id, app_name, content, md5, gmt_create, gmt_modified, src_user, src_ip, op_type, tenant_id, encrypted_data_key) VALUES (0, 5, 'cskefu-manager-service.json', 'cskefu-default', '', '{
    
}', 'f309d0a46b7f7d41270b5684c31acd11', '2023-10-16 08:40:12', '2023-10-16 08:40:13', null, '172.20.0.1', 'I', 'cskefu', '');
INSERT INTO his_config_info (id, nid, data_id, group_id, app_name, content, md5, gmt_create, gmt_modified, src_user, src_ip, op_type, tenant_id, encrypted_data_key) VALUES (0, 6, 'cskefu-plugin-service.json', 'cskefu-default', '', '{
    
}', 'f309d0a46b7f7d41270b5684c31acd11', '2023-10-16 08:40:27', '2023-10-16 08:40:28', null, '172.20.0.1', 'I', 'cskefu', '');
INSERT INTO his_config_info (id, nid, data_id, group_id, app_name, content, md5, gmt_create, gmt_modified, src_user, src_ip, op_type, tenant_id, encrypted_data_key) VALUES (0, 7, 'cskefu-websocket-service.json', 'cskefu-default', '', '{
    
}', 'f309d0a46b7f7d41270b5684c31acd11', '2023-10-16 08:40:57', '2023-10-16 08:40:57', null, '172.20.0.1', 'I', 'cskefu', '');

INSERT INTO config_info (id, data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema, encrypted_data_key) VALUES (1, 'common.json', 'cskefu-default', '{
  "spring": {
    "cloud": {
      "nacos": {
        "discovery": {
          "server-addr": "nacos:8848"
        }
      }
    },
    "datasource": {
      "driver-class-name": "com.mysql.cj.jdbc.Driver",
      "password": 123456,
      "url": "jdbc:mysql://mysql:3306/cskefu?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=GMT%2B8",
      "username": "root"
    },
    "mvc": {
      "static-path-pattern": "/smart-doc/**"
    },
    "rabbitmq": {
      "addresses": "rabbitmq",
      "listener": {
        "simple": {
          "acknowledge-mode": "manual",
          "prefetch": 1
        }
      },
      "password": "guest",
      "port": 5672,
      "publisher-confirm-type": "correlated",
      "publisher-returns": true,
      "username": "guest"
    },
    "web": {
      "resources": {
        "static-locations": "classpath:/smart-doc"
      }
    }
  },
  "cskefu": {
    "private-key": "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC8TSxdMp76dZS2L8DpvJOgHNJmeI8PM30CFSeVMCHV5dEjtixipsMsSQu0ariAtVfA5M/ma1wcqEpsDO9nVQRsmsJCKMIWOcR6yBQU9f0T7Hkmil4Ckf61C1R7wLrynOjCeUCneQ7R0+dg9K25CymclPnK4ym2GInmuUjdbyQQcX4QBuIbmuY10MG8nW9ovhFML85KoBk5yxV4zVwLGP4TFJVnBmiNO3XSKL2XsX7wFFO93O8jmAnzgpWj8khsZAYG4nijIz2BciS9uQFa/lUX6yD50ykjNklbhCBIMGaQ6rr1WjnwLHDZU9sLvcYMboKPJHWz1FttvxuKSLYbf7j/AgMBAAECggEAEH1rPSrwF47kdW/vibu+uDuat7Tz3w9RpM6lc/Mn6ctoA6PLqN5hvFpJPbhHoAqn1kSZ0EJNA4imIjNsluf8rMyAs1QbN+CnMpMC7zL7jrS1IO+ze+LwVnpyufbbm3nfCE85OKSC7IbAfhHvUYOHIvQI5Zn76FTamFHxh5klpCrY1h8gcVWdRxiK0Q8AQP7KD0QnkzgROhkRmdI01639VyTCTc6Rtv5puU6J6idJwTd3JAiJXZ2qcoAwhnkSHnVnP5ktFxl66eHGJKeol6bGcJIFQRGLVmh78x6MXoqkJ2M7DRntL38TLKT5KCYViGxUpO0gED/HQBhDALyNevJZQQKBgQDr+pZDtRIQ6mMZMswKTo2+zIFEUFES2sR/bunEW5snPR3krKdgzJoEg7WERkMviSlvW6K7a6VeJ9KKO/iE07lcRkN2NQZUkBDsrNRgoOwB17hHJqrMyZ1It+hZmPQTkeyJu/BCxDkIMwbQYXxIe8J/dVYjHJVXZgzPqEXKYqeViwKBgQDMRwr7ceW+9c/GzHd7p6849s3+MbT0kzyXDTdeJkQFSDPufYapTCan/MrdJmnIRz8pq2URZrO4kDDxxeWu2Vkekv9cxVFrQVFfvuq+4BFpQvYkRHk9PDzCmgFJD/wLG8gzNTH7UahaNb7m0tPB3D1g5Q6LzZCc1h3s2K6SLS7g3QKBgE41Bof6ArrIc39ubmEcF64caNsTI0t0ZZs2TxNcqNcgUj/vWKmkJYdJf2cPQkUG2Eynug8TZgMGf6iAp6Sd5tjGEKWkfSyZcoJ95QUBUDZsIA60qfak+xOWn9LR9lJmElazirUWAzDMeH2nUWFUYumLIbkRSA1nLOfFhRvGBnRxAoGBAJvDAAjCzGBTpt77QZA0SFOzPVc6J7TmICk9lp5fpzYv3AlaBbhJrKAjDbybccWZLfxkCGjAWwG8UNXKBFzStjWt+LGQc4jJAXd0aCKrUBtnR7BX1epvaBUqwRgo7BK8WGdThI0RssE2gh4XXAhSGysq/XB0inRMf/z9K/+iHECxAoGAFlcIZ4/xXhbObUPU05hZFoj+zN5obaOLefRJn4uqjD9/VSRnI2bBv7vXKoLX6BwFF/PWvg1hzGy3O9TQ4XYPOqNGcBFDOqwxmVVevzXgRInoiRNP3xZXcqswppEbqORGbRoPLQBqLnPx1GKwbo7Fp9O5ywARVZtf4STt3pRK8PY=",
    "public-key": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvE0sXTKe+nWUti/A6byToBzSZniPDzN9AhUnlTAh1eXRI7YsYqbDLEkLtGq4gLVXwOTP5mtcHKhKbAzvZ1UEbJrCQijCFjnEesgUFPX9E+x5JopeApH+tQtUe8C68pzownlAp3kO0dPnYPStuQspnJT5yuMpthiJ5rlI3W8kEHF+EAbiG5rmNdDBvJ1vaL4RTC/OSqAZOcsVeM1cCxj+ExSVZwZojTt10ii9l7F+8BRTvdzvI5gJ84KVo/JIbGQGBuJ4oyM9gXIkvbkBWv5VF+sg+dMpIzZJW4QgSDBmkOq69Vo58Cxw2VPbC73GDG6CjyR1s9Rbbb8biki2G3+4/wIDAQAB",
    "token": {
      "duration": 3600
    }
  }
}', '071dd46c6f4ae097aefdd566b0ec47d2', '2023-10-16 08:09:40', '2023-10-16 08:37:01', 'nacos', '172.20.0.1', '', 'cskefu', '', '', '', 'json', '', '');
INSERT INTO config_info (id, data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema, encrypted_data_key) VALUES (6, 'cskefu-auth-service.json', 'cskefu-default', '{
    
}', 'f309d0a46b7f7d41270b5684c31acd11', '2023-10-16 08:39:32', '2023-10-16 08:39:32', null, '172.20.0.1', '', 'cskefu', null, null, null, 'json', null, '');
INSERT INTO config_info (id, data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema, encrypted_data_key) VALUES (7, 'cskefu-channel-wechat-service.json', 'cskefu-default', '{
    
}', 'f309d0a46b7f7d41270b5684c31acd11', '2023-10-16 08:39:44', '2023-10-16 08:39:44', null, '172.20.0.1', '', 'cskefu', null, null, null, 'json', null, '');
INSERT INTO config_info (id, data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema, encrypted_data_key) VALUES (8, 'cskefu-web-gateway.json', 'cskefu-default', '{
    "spring": {
        "cloud": {
          "sentinel": {
            "transport": {
              "dashboard": "sentinel-dashboard:9850",
              "port": 9851
            }
          }
        }
    }
}', 'f309d0a46b7f7d41270b5684c31acd11', '2023-10-16 08:39:58', '2023-10-16 08:39:58', null, '172.20.0.1', '', 'cskefu', null, null, null, 'json', null, '');
INSERT INTO config_info (id, data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema, encrypted_data_key) VALUES (9, 'cskefu-manager-service.json', 'cskefu-default', '{
    
}', 'f309d0a46b7f7d41270b5684c31acd11', '2023-10-16 08:40:13', '2023-10-16 08:40:13', null, '172.20.0.1', '', 'cskefu', null, null, null, 'json', null, '');
INSERT INTO config_info (id, data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema, encrypted_data_key) VALUES (10, 'cskefu-plugin-service.json', 'cskefu-default', '{
    
}', 'f309d0a46b7f7d41270b5684c31acd11', '2023-10-16 08:40:28', '2023-10-16 08:40:28', null, '172.20.0.1', '', 'cskefu', null, null, null, 'json', null, '');
INSERT INTO config_info (id, data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema, encrypted_data_key) VALUES (11, 'cskefu-websocket-service.json', 'cskefu-default', '{
    
}', 'f309d0a46b7f7d41270b5684c31acd11', '2023-10-16 08:40:57', '2023-10-16 08:40:57', null, '172.20.0.1', '', 'cskefu', null, null, null, 'json', null, '');
