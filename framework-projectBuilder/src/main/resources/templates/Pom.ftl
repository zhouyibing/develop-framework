<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
		 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<groupId>${object.projectGroup}</groupId>
	<artifactId>${object.projectArtifact}</artifactId>
	<packaging>jar</packaging>
	<version>${object.projectVersion!1.0-SNAPSHOT}</version>
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.0.3.RELEASE</version>
	</parent>
	<properties>
		<develop-framework-version>1.0-SNAPSHOT</develop-framework-version>
		<maven-plugin-version>2.5</maven-plugin-version>
		<spring-boot-version>2.0.3.RELEASE</spring-boot-version>
	</properties>

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>com.yipeng.develop.framework</groupId>
				<artifactId>framework-core</artifactId>
				<version>${r'${develop-framework-version}'}</version>
			</dependency>
			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-starter</artifactId>
				<version>${r'${spring-boot-version}'}</version>
			</dependency>
			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-starter-web</artifactId>
				<version>${r'${spring-boot-version}'}</version>
			</dependency>
		</dependencies>
	</dependencyManagement>

	<dependencies>
		<dependency>
			<groupId>com.yipeng.develop.framework</groupId>
			<artifactId>framework-core</artifactId>
		</dependency>
		<!-- 引入spring-boot-starter-parent后，默认会使用mysl-cononector-java.5.14   这里需要显示配置mysql-connector-java版本-->
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>6.0.6</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<!-- 插件使用练习 -->
			<!-- 清理插件的使用，maven3.0.4会默认使用2.4.1版本的clean插件 -->
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-clean-plugin</artifactId>
				<version>${r'${maven-plugin-version}'}</version>
				<executions>
					<execution>
						<id>auto-clean</id>
						<!-- clean生命周期clean阶段 -->
						<phase>clean</phase>
						<goals>
							<!-- 执行clean插件的clean目标 -->
							<goal>clean</goal>
						</goals>
					</execution>
				</executions>
			</plugin>

			<!-- maven-resources-plugin在maven3.0.4中默认使用2.5版本的resources -->

			<!-- 编译插件的使用，maven3.0.4会默认使用2.3.2版本的compile插件 -->
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>${r'${maven-plugin-version}'}</version>
				<configuration>
					<!-- 源代码使用的jdk版本 -->
					<source>1.8</source>
					<!-- 构建后生成class文件jdk版本 -->
					<target>1.8</target>
				</configuration>
			</plugin>

			<!-- maven-surefire-plugin插件，maven3.0.4默认使用2.10版本的surefire插件 -->
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<version>${r'${maven-plugin-version}'}</version>
				<configuration>
					<!-- 改变测试报告生成目录 ，默认为target/surefire-reports -->
					<!-- project.build.directory表示maven的属性，这里指的是构建的目录下面test-reports，project.build.directory就是pom标签的值 -->
					<reportsDirectory>${r'${project.build.directory}'}/test-reports</reportsDirectory>
				</configuration>
			</plugin>

			<!-- maven-install-plugin插件一般不需要配置,maven3.0.4默认使用2.3.1版本的install插件 -->

			<!-- 部署插件的使用，maven3.0.4会默认使用2.7版本的deploy插件 -->
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-deploy-plugin</artifactId>
				<version>${r'${maven-plugin-version}'}</version>
				<configuration>
					<!-- 更新元数据 -->
					<updateReleaseInfo>true</updateReleaseInfo>
				</configuration>
			</plugin>

			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
</project>