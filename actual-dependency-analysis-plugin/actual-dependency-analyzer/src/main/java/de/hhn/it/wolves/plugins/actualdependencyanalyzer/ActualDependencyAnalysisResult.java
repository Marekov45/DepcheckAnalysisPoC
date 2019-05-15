package de.hhn.it.wolves.plugins.actualdependencyanalyzer;

import de.hhn.it.wolves.domain.AnalysisResult;
import de.hhn.it.wolves.domain.RepositoryInfo;

public class ActualDependencyAnalysisResult extends AnalysisResult {

    public ActualDependencyAnalysisResult(RepositoryInfo repositoryInformation) {
        super(repositoryInformation);
    }
}
