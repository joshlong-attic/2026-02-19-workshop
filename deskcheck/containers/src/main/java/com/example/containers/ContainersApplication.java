package com.example.containers;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootApplication
public class ContainersApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContainersApplication.class, args);
	}

	@Bean
	ApplicationRunner runner(JdbcClient db) {
		return args -> db //
			.sql("select * from cats") //
			.query( //
					(rs, rowNum) -> new Cat(rs.getString("name"), //
							rs.getInt("id")))
			.list()//
			.forEach(IO::println);
	}

}

record Cat(String name, int id) {
}