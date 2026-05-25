package com.gzhennaxia.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 全局配置（分页、自动填充），供 web 聚合启动与各业务模块共用。
 *
 * @author Gzhennaxia
 * @date 2026-05-20
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 分页插件（与当前数据源一致：PostgreSQL）。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        return interceptor;
    }

    /**
     * 自动填充创建/更新时间，兼容 {@code createdTime}/{@code updatedTime} 与 {@code createTime}/{@code updateTime} 命名。
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                strictInsertFill(metaObject, "createdTime", LocalDateTime.class, now);
                strictInsertFill(metaObject, "updatedTime", LocalDateTime.class, now);
                strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
                strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                strictUpdateFill(metaObject, "updatedTime", LocalDateTime.class, now);
                strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);
            }
        };
    }
}
