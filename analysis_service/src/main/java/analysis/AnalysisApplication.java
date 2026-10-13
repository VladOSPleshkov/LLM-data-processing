package analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "analysis.entity")

public class AnalysisApplication {
    public static void main(String[] args) { SpringApplication.run(AnalysisApplication.class, args); }
}
