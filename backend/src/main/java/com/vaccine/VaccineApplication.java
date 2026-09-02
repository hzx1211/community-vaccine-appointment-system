package com.vaccine;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 社区疫苗预约管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.vaccine.mapper")
@EnableTransactionManagement
public class VaccineApplication {
    public static void main(String[] args) {
        SpringApplication.run(VaccineApplication.class, args);
        System.out.println("========================================");
        System.out.println("  社区疫苗预约管理系统启动成功！");
        System.out.println("  API文档: http://localhost:8081/doc.html");
        System.out.println("========================================");
    }
}
