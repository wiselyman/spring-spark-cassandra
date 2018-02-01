package top.wisely.springsparkcassandra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import top.wisely.springsparkcassandra.repo.PersonRepository;

@SpringBootApplication
@Slf4j
public class SpringSparkCassandraApplication {
	@Bean
	CommandLineRunner commandLineRunner(PersonRepository personRepository){
		return args -> {
			log.info("-----------" + personRepository.countPerson() + "-----------");
		};

	}

	public static void main(String[] args) {
		SpringApplication.run(SpringSparkCassandraApplication.class, args);
	}
}
