package analysis.repository;

import analysis.util.AnalysisTextProvider;
import contract.Repository;
import di.annotation.Component;
import di.annotation.Value;

@Component(name = "analysisRepository")
public class AnalysisRepository implements Repository {

    @Value(key = "ANALYSIS_DB_URL")
    private String dbUrl;

    @Value(key = "ANALYSIS_DB_USER")
    private String dbUser;

    @Override
    public String execute() {
        System.out.println("  AnalysisRepository: " + dbUrl + " user=" + dbUser);
        return AnalysisTextProvider.repositoryText();
    }
}