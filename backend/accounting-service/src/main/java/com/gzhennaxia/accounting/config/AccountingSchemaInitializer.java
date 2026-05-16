package com.gzhennaxia.accounting.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 启动时初始化记账表结构（IF NOT EXISTS，可重复执行）。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@Component
public class AccountingSchemaInitializer implements ApplicationRunner {

  private final DataSource dataSource;

  public AccountingSchemaInitializer(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void run(ApplicationArguments args) {
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    populator.addScript(new ClassPathResource("db/accounting.sql"));
    populator.setContinueOnError(true);
    populator.execute(dataSource);
  }
}
