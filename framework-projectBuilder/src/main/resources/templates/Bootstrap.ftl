package ${object.basePackage};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
* @author ${base.authorName}
* email:${base.authorEmail}
* AUTO-GENERATE BY PROJECT-BUILDER
**/
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.yipeng.framework","${object.basePackage}"})
@MapperScan(basePackages = {"${object.basePackage}.mapper"})
public class ${object.applicationName}Application {
    public static void main(String[] args) {
        SpringApplication.run(${object.applicationName}Application.class, args);
    }
}
