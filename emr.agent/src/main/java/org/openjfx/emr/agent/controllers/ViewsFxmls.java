package org.openjfx.emr.agent.controllers;

public enum ViewsFxmls {
	LOGIN("login"),
	BASE("base"),
	HOME("home"),
	BILLING("billing"),
	PREDICTIONS("predictions"),
	SINGLE_DEFAULT("defaultsingle"),
	MULTIPLE_DEFAULT("defaultmultiple"),
	SINGLE_DIAGNOSIS("diagnosissingle"),
	MULTIPLE_DIAGNOSIS("diagnosismultiple");

    private String fileName;

    ViewsFxmls(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

}
