package de.hhn.it.wolves.domain;

import java.io.Serializable;

public abstract class AnalysisResult implements Serializable {

    private final RepositoryInfo repositoryInformation; //The repo that was analysed


    public AnalysisResult(RepositoryInfo repositoryInformation) {
        this.repositoryInformation = repositoryInformation;

    }

    public RepositoryInfo getRepositoryInformation() {
        return repositoryInformation;
    }


}

