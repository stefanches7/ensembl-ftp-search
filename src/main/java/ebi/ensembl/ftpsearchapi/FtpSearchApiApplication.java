package ebi.ensembl.ftpsearchapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FtpSearchApiApplication {
    static Logger logger = LoggerFactory.getLogger(FtpSearchApiApplication.class);

	public static void main(final String[] args) {
		SpringApplication.run(FtpSearchApiApplication.class, args);
        logger.info("Spring Boot application started!");
	}
}
