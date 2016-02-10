package uk.ac.ebi.spot.efoVersionner;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.efoVersionner.utils.TypeEnum;

import java.io.*;
import java.util.*;

/**
 * Created by catherineleroy on 09/02/2016.
 */
public class VersionUpdater {

    private Properties properties;
    private String releaseCandidateFile = null;

    private String typeEnum = null;

    public VersionUpdater() throws IOException {
        properties = new Properties();

        InputStream input = null;

        String filename = "efoVersionner.properties";
        input = VersionUpdater.class.getClassLoader().getResourceAsStream(filename);
        if(input==null){
            System.out.println("Sorry, unable to find " + filename);
        }
        //load a properties file from class path, inside static method
        properties.load(input);

        releaseCandidateFile = (String) properties.get("efoVersionner.releaseCandidateFile");

        typeEnum = (String) properties.get("efoVersionner.typeEnum");


    }

    private void updateVersion() throws Exception {

        File efoFile = new File(releaseCandidateFile);

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();


        MissingImportHandlingStrategy missingImportHandlingStrategy = MissingImportHandlingStrategy.SILENT;

        OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
        config = config.setMissingImportHandlingStrategy(missingImportHandlingStrategy);

        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLOntologyDocumentSource owlOntologyDocumentSource = new FileDocumentSource(efoFile);
        OWLOntology efo = manager.loadOntologyFromOntologyDocument(owlOntologyDocumentSource, config);

        OWLAnnotationProperty owlVersionProperty = factory.getOWLVersionInfo();

        List<OWLOntologyChange> changes = new ArrayList();


        for (OWLAnnotation annotation : efo.getAnnotations()){
            if(annotation.getProperty().equals(owlVersionProperty)){

                String value = annotation.getValue().toString();
                String versionNumber = value.replace("\"^^xsd:string", "");
                versionNumber = versionNumber.replace("\"", "");
                String newVersionNumber = versionNumberIncrementer(versionNumber);

                RemoveOntologyAnnotation removeAnnotation = new RemoveOntologyAnnotation(efo,annotation);
                changes.add(removeAnnotation);

                // the content of our comment: a string and a language tag
                OWLAnnotation newVersionAnnotation = manager.getOWLDataFactory().getOWLAnnotation(owlVersionProperty,manager.getOWLDataFactory().getOWLLiteral(newVersionNumber));
                OWLAxiom ax = manager.getOWLDataFactory().getOWLAnnotationAssertionAxiom(efo.getOntologyID().getOntologyIRI(), newVersionAnnotation);
                changes.addAll(manager.addAxiom(efo,ax));

                break;
            }
        }
        manager.applyChanges(changes);
        manager.saveOntology(efo);

    }



    public String versionNumberIncrementer(String versionNumber) throws Exception {
        System.out.println("from inside incrementer = " + versionNumber);

        String[] versionNumberSplit = versionNumber.split("\\.");

        int arraySize = versionNumberSplit.length;

        if(arraySize == 1){
            throw new Exception("version number is corrupted, fix manually and restart the version number updater.");
        }

        String newVersionNumber = "";

        if(this.typeEnum.equals(TypeEnum.MAJOR.getShortname())) {
            int major = Integer.parseInt(versionNumberSplit[0]) + 1;
            newVersionNumber = "" + major + "." + "00";
        }else{
            if(this.typeEnum.equals(TypeEnum.MINOR.getShortname())){
                int major = Integer.parseInt(versionNumberSplit[0]);
                int minor = Integer.parseInt(versionNumberSplit[1]) + 1;
                newVersionNumber = major + "." + minor;

            }else {
                if(this.typeEnum.equals(TypeEnum.MOST_MINOR.getShortname())){
                    if(arraySize == 2){
                        newVersionNumber = versionNumber + ".1";
                    }else {
                        if (arraySize == 3) {
                            int mostMinor = Integer.parseInt(versionNumberSplit[2]) + 1;
                            newVersionNumber = versionNumberSplit[0] + "." + versionNumberSplit[1] + "." + mostMinor;
                        }
                    }
                }else{
                    throw new Exception("Wrong type, please chose one of mj, mn, mm");
                }
            }
        }
        return newVersionNumber;
    }



    public String getReleaseCandidateFile() {

        return releaseCandidateFile;
    }

    public void setReleaseCandidateFile(String releaseCandidateFile) {
        this.releaseCandidateFile = releaseCandidateFile;
    }

    public String getTypeEnum() {

        return typeEnum;
    }

    public void setTypeEnum(String typeEnum) {
        this.typeEnum = typeEnum;
    }

    public static void main(String[] args) throws Exception {
        VersionUpdater vu = new VersionUpdater();
//        vu.setTypeEnum("mn");
//        vu.setReleaseCandidateFile("/Users/catherineleroy/Documents/github_project/ExperimentalFactorOntology/ExFactorInOWL/releasecandidate/efo_release_candidate.owl");
        vu.updateVersion();
    }




}
