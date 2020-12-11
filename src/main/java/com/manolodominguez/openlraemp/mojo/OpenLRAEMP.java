/*
 * Copyright (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manolodominguez.openlraemp.mojo;

import com.manolodominguez.openlrae.analysis.LicenseRiskAnalysisEngine;
import com.manolodominguez.openlrae.analysis.RiskAnalysisResult;
import com.manolodominguez.openlrae.analysis.riskanalysers.AbstractRiskAnalyser;
import com.manolodominguez.openlrae.analysis.riskanalysers.RiskAnalyserComponentsLicensesIncompatibleWithProjectLicenses;
import com.manolodominguez.openlrae.analysis.riskanalysers.RiskAnalyserComponentsLicensesMisalignedFromProjectLicenses;
import com.manolodominguez.openlrae.analysis.riskanalysers.RiskAnalyserHeterogeneousComponentsLicenses;
import com.manolodominguez.openlrae.analysis.riskanalysers.RiskAnalyserLimitedSetOfPotentialComponentsLicenses;
import com.manolodominguez.openlrae.analysis.riskanalysers.RiskAnalyserLimitedSetOfPotentialProjectLicenses;
import com.manolodominguez.openlrae.analysis.riskanalysers.RiskAnalyserObsoleteComponentsLicenses;
import com.manolodominguez.openlrae.analysis.riskanalysers.RiskAnalyserObsoleteProjectLicenses;
import com.manolodominguez.openlrae.analysis.riskanalysers.RiskAnalyserScarcelySpreadComponentsLicenses;
import com.manolodominguez.openlrae.analysis.riskanalysers.RiskAnalyserScarcelySpreadProjectLicenses;
import com.manolodominguez.openlrae.analysis.riskanalysers.RiskAnalyserUnfashionableComponentsLicenses;
import com.manolodominguez.openlrae.analysis.riskanalysers.RiskAnalyserUnfashionableProjectLicenses;
import com.manolodominguez.openlrae.arquitecture.Project;
import com.manolodominguez.openlrae.baseofknowledge.basevalues.SupportedRisks;
import com.manolodominguez.openlrae.reporting.ReportsFactory;
import com.manolodominguez.openlrae.reporting.SupportedVerbosityLevel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import mjson.Json;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * This class implementas a Mojo that, using OpenLRAE library, do licensing
 * risks analysis using your project's bill of component as a base of the
 * analysis. It adds a new goal "analyse" that is bound to the compile phase of
 * Maven by default.
 *
 * @author manolodd
 */
@Mojo(name = "analyse", defaultPhase = LifecyclePhase.COMPILE, threadSafe = true)
public class OpenLRAEMP extends AbstractMojo {

    @Parameter(property = "analyse.projectDefinitionFile", defaultValue = "project.json")
    private String projectDefinitionFile;

    @Parameter(property = "analyse.saveReport", defaultValue = "false")
    private boolean saveReport;

    @Parameter(property = "analyse.showReport", defaultValue = "false")
    private boolean showReport;

    @Parameter(property = "analyse.reportLanguage", defaultValue = "en")
    private String reportLanguage;

    @Parameter(property = "analyse.risksThresholds")
    private HashMap<String, String> riskThresholds;

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private String pathForReports;

    @Override
    public synchronized void execute() throws MojoExecutionException, MojoFailureException {

        EnumMap<SupportedRisks, Float> convertedRiskThresholds = new EnumMap<>(SupportedRisks.class);
        Project project;
        RiskAnalyserLimitedSetOfPotentialProjectLicenses riskAnalyser1;
        RiskAnalyserObsoleteComponentsLicenses riskAnalyser2;
        RiskAnalyserUnfashionableComponentsLicenses riskAnalyser3;
        RiskAnalyserScarcelySpreadComponentsLicenses riskAnalyser4;
        RiskAnalyserComponentsLicensesIncompatibleWithProjectLicenses riskAnalyser5;
        RiskAnalyserLimitedSetOfPotentialComponentsLicenses riskAnalyser6;
        RiskAnalyserObsoleteProjectLicenses riskAnalyser7;
        RiskAnalyserUnfashionableProjectLicenses riskAnalyser8;
        RiskAnalyserScarcelySpreadProjectLicenses riskAnalyser9;
        RiskAnalyserComponentsLicensesMisalignedFromProjectLicenses riskAnalyser10;
        RiskAnalyserHeterogeneousComponentsLicenses riskAnalyser11;
        LicenseRiskAnalysisEngine riskAnalysisEngine;
        String reportPathAndFilename;
        OutputStreamWriter outputStreamWriter;

        String currentMoment = ZonedDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        reportPathAndFilename = pathForReports + File.separator + "report-" + currentMoment + "-" + projectDefinitionFile;

        if (projectDefinitionFile == null) {
            getLog().error("fileName cannot be null");
            throw new MojoFailureException("fileName cannot be null");
        }
        projectDefinitionFile = projectDefinitionFile.trim();
        if (projectDefinitionFile.isEmpty()) {
            getLog().error("fileName cannot be blank");
            throw new MojoFailureException("fileName cannot be blank");
        }
        File file = new File(projectDefinitionFile);
        if (!file.exists()) {
            getLog().error(projectDefinitionFile + " not found");
            throw new MojoFailureException(projectDefinitionFile + " not found");
        } else {
            if (!file.isFile()) {
                getLog().error(projectDefinitionFile + " is not a file");
                throw new MojoFailureException(projectDefinitionFile + " is not a file");
            } else {
                if (!file.canRead()) {
                    getLog().error(projectDefinitionFile + " is unreadable");
                    throw new MojoFailureException(projectDefinitionFile + " is unreadable");
                } else {
                    try {
                        // Convert String thresholds to Float thresholds and
                        // check whether they are valid or not.
                        convertedRiskThresholds.clear();
                        for (String riskAsString : riskThresholds.keySet()) {
                            if (isAValidRiskThresshold(riskThresholds.get(riskAsString))) {
                                if (isAValidRisk(riskAsString)) {
                                    // If a threshold is defined more than one 
                                    // time for the same risk, only the latest 
                                    // definition is taken into account.
                                    convertedRiskThresholds.put(SupportedRisks.valueOf(riskAsString), Float.valueOf(riskThresholds.get(riskAsString)));
                                } else {
                                    throw new MojoFailureException("The specified risk " + "'" + riskAsString + "' is not valid. Only values supported by this version of openlrae-maven-plugin are allowed: " + getSupportedRiskAsString());
                                }
                            } else {
                                throw new MojoFailureException("The value " + "'" + riskThresholds.get(riskAsString) + "' specified for risk '" + riskAsString + "' is not valid. It has to be a float value between 0.0 and 1.0");
                            }
                        }
                        getLog().info("Loading project definition from '" + projectDefinitionFile + "'");
                        project = new Project(Json.read(file.toURI().toURL()));
                        getLog().info("Project definition loaded!");
                        getLog().info("Configuring license risk analysers...");
                        // Define desired risk analysers we want to use for this 
                        // project in this case, all are used by default.
                        riskAnalyser1 = new RiskAnalyserLimitedSetOfPotentialProjectLicenses(project);
                        riskAnalyser2 = new RiskAnalyserObsoleteComponentsLicenses(project);
                        riskAnalyser3 = new RiskAnalyserUnfashionableComponentsLicenses(project);
                        riskAnalyser4 = new RiskAnalyserScarcelySpreadComponentsLicenses(project);
                        riskAnalyser5 = new RiskAnalyserComponentsLicensesIncompatibleWithProjectLicenses(project);
                        riskAnalyser6 = new RiskAnalyserLimitedSetOfPotentialComponentsLicenses(project);
                        riskAnalyser7 = new RiskAnalyserObsoleteProjectLicenses(project);
                        riskAnalyser8 = new RiskAnalyserUnfashionableProjectLicenses(project);
                        riskAnalyser9 = new RiskAnalyserScarcelySpreadProjectLicenses(project);
                        riskAnalyser10 = new RiskAnalyserComponentsLicensesMisalignedFromProjectLicenses(project);
                        riskAnalyser11 = new RiskAnalyserHeterogeneousComponentsLicenses(project);
                        // Define a Risk analysis engine and add these risk 
                        // analysers
                        riskAnalysisEngine = new LicenseRiskAnalysisEngine(riskAnalyser1);
                        riskAnalysisEngine.addRiskAnalyser(riskAnalyser2);
                        riskAnalysisEngine.addRiskAnalyser(riskAnalyser3);
                        riskAnalysisEngine.addRiskAnalyser(riskAnalyser4);
                        riskAnalysisEngine.addRiskAnalyser(riskAnalyser5);
                        riskAnalysisEngine.addRiskAnalyser(riskAnalyser6);
                        riskAnalysisEngine.addRiskAnalyser(riskAnalyser7);
                        riskAnalysisEngine.addRiskAnalyser(riskAnalyser8);
                        riskAnalysisEngine.addRiskAnalyser(riskAnalyser9);
                        riskAnalysisEngine.addRiskAnalyser(riskAnalyser10);
                        riskAnalysisEngine.addRiskAnalyser(riskAnalyser11);
                        // Remove those risk analysers not requested in the pom
                        // file. 
                        for (AbstractRiskAnalyser auxRiskAnalyser : riskAnalysisEngine.getRisksAnalysers()) {
                            if (!convertedRiskThresholds.containsKey(auxRiskAnalyser.getHandledRiskType())) {
                                riskAnalysisEngine.getRisksAnalysers().remove(auxRiskAnalyser);
                            }
                        }
                        getLog().info("License risk analysers configured!");
                        // Do the licensing risk analysis. 
                        getLog().info("Configuring reporting language...");
                        riskAnalysisEngine.setLanguage(Locale.forLanguageTag(reportLanguage));
                        getLog().info("Reporting language set to " + riskAnalysisEngine.getLanguage().toString());
                        getLog().info("Analysing " + riskAnalysisEngine.getRisksAnalysers().size() + " licensing risks on project '" + project.getName() + "', with " + project.getBillOfComponentBindings().size() + " components and " + project.getLicenses().size() + " project license(s)...");
                        RiskAnalysisResult[] resultSet = riskAnalysisEngine.analyse();
                        getLog().info("Licensing risks analysis finished");
                        // Check whether the thresholds definded in the pom 
                        // file, have been exceeded or not. 
                        getLog().info("Risk thresholds checking...");
                        boolean thresholdExceeded = false;
                        for (RiskAnalysisResult riskAnalysisResult : resultSet) {
                            SupportedRisks risk = riskAnalysisResult.getRiskType();
                            float riskValue = riskAnalysisResult.getRiskValue();
                            float riskThreshold = convertedRiskThresholds.get(risk);
                            if (riskValue > riskThreshold) {
                                getLog().info("\tThreshold for " + risk.toString() + " is " + riskThreshold + ". Real risk value is " + riskValue + " -- BUILD WILL FAIL!");
                                thresholdExceeded = true;
                            } else {
                                getLog().info("\tThreshold for " + risk.toString() + " is " + riskThreshold + ". Real risk value is " + riskValue + " -- OK");
                            }
                        }
                        if (thresholdExceeded) {
                            throw new MojoFailureException("At least one of the defined thresholds has been exceeded.");
                        }
                        getLog().info("Risk thresholds checking done!");
                        // Generate an internationalized licensing risk analysis
                        // report, to be shown or to be stored. 
                        if (saveReport || showReport) {
                            getLog().info("Generating JSON report...");
                            if (saveReport) {
                                getLog().info("JSON report generated!");
                                getLog().info("Saving report...");
                                try (OutputStream outputStream = new FileOutputStream(reportPathAndFilename)) {
                                    outputStreamWriter = new OutputStreamWriter(outputStream, DEFAULT_REPORT_ENCONDING);
                                    outputStreamWriter.write(ReportsFactory.getInstance(SupportedVerbosityLevel.DETAILED).getReportAsBeautifiedJSONString(project, resultSet));
                                    outputStreamWriter.close();
                                    getLog().info("JSON report saved at '" + reportPathAndFilename + "'!");
                                } catch (FileNotFoundException | SecurityException ex) {
                                    getLog().error("JSON report '" + reportPathAndFilename + "' cannot be saved. Skipping this step.");
                                }
                            }
                            if (showReport) {
                                getLog().info("JSON report generated!");
                                getLog().info("Showing report...");
                                getLog().info(ReportsFactory.getInstance(SupportedVerbosityLevel.DETAILED).getReportAsBeautifiedJSONString(project, resultSet));
                                getLog().info("JSON report finished!");
                            }
                        }
                    } catch (IOException | NumberFormatException ex) {
                        getLog().error(projectDefinitionFile + "is not a correct JSON project definition complanint with OpenLRAE JSON Schema for project definition.");
                        throw new MojoFailureException("is not a correct JSON project definition complanint with OpenLRAE JSON Schema for project definition.");
                    }
                }
            }
        }
    }

    private synchronized boolean isAValidRiskThresshold(String riskThresshold) {
        float thresshold;
        try {
            thresshold = Float.valueOf(riskThresshold);
            if ((thresshold < MIN_RISK_VALUE) || (thresshold > MAX_RISK_VALUE)) {
                return false;
            }
        } catch (NumberFormatException | NullPointerException ex) {
            return false;
        }
        return true;
    }

    private synchronized boolean isAValidRisk(String risk) {
        try {
            SupportedRisks.valueOf(risk);
        } catch (IllegalArgumentException | NullPointerException ex) {
            return false;
        }
        return true;
    }

    private synchronized String getSupportedRiskAsString() {
        String str = "";
        for (SupportedRisks risk : SupportedRisks.values()) {
            str += risk.toString() + ", ";
        }
        str = str.substring(0, str.length() - 2);
        return str;
    }

    private static final String DEFAULT_REPORT_ENCONDING = "UTF-8";
    private static final float MIN_RISK_VALUE = 0.0f;
    private static final float MAX_RISK_VALUE = 1.0f;

}
