package analysis.controller;

import analysis.util.AnalysisTextProvider;
import contract.Controller;
import contract.Service;
import di.annotation.Autowire;
import di.annotation.Component;
import di.annotation.Qualifier;
import di.annotation.Value;

@Component(name = "analysisController")
public class AnalysisController implements Controller {

    private Service service;

    @Value(key = "ANALYSIS_MODULE_NAME")
    private String moduleName;

    @Autowire
    public void setService(@Qualifier("analysisService") Service service) {
        this.service = service;
        System.out.println("  AnalysisController: внедрение через сеттер");
    }

    @Override
    public String execute() {
        return AnalysisTextProvider.controllerText(service.execute());
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }
}
