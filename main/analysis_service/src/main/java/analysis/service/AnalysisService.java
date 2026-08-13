package analysis.service;

import contract.Repository;
import contract.Service;
import di.annotation.Autowire;
import di.annotation.Component;
import di.annotation.Qualifier;

@Component(name = "analysisService")
public class AnalysisService implements Service {

    private Repository database;

    @Autowire
    public void setDatabase(
            @Qualifier("analysisDatabase") Repository database) {
        this.database = database;
    }

    @Override
    public String execute() {
        return database.execute();
    }
}