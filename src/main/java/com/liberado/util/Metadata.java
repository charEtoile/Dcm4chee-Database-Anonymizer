/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * https://www.tutorialspoint.com/postgresql/postgresql_java.htm
 * https://stackoverflow.com/questions/35505424/how-to-read-bytea-image-data-from-postgressql-with-jpa
 * https://stackoverflow.com/questions/2069541/postgresql-jdbc-and-streaming-blobs
 * https://www.jmdoudoux.fr/java/dej/chap-persistence.htm
 * https://stackoverflow.com/questions/17400497/how-to-convert-blob-to-string-and-string-to-blob-in-java
 *
 * design pattern DAO
 * http://cyrille-herby.developpez.com/tutoriels/java/mapper-sa-base-donnees-avec-pattern-dao/
 *
 * Type List vs type ArrayList in Java
 * https://stackoverflow.com/questions/2279030/type-list-vs-type-arraylist-in-java
 *
 * Anonymisation
 * http://plastimatch.org/dicom_comparison.html
 *
 */
package com.liberado.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import com.liberado.bean.Mwl_item;
import com.liberado.bean.Patient;
import com.liberado.dao.DAO;
import com.liberado.dao.concrete.Mwl_itemDAO;
import com.liberado.dao.concrete.PatientDAO;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.CharSet;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.dcm4che.data.*;
import org.dcm4che.dict.Tags;


/**
 *
 * @author Michel LIBERADO
 * https://sourceforge.net/p/dcm4che/svn/18203/tree/dcm4che14/trunk/src/java/org/dcm4che/data/Dataset.java
 */
public class Metadata {

    public static void main(String[] args) {

        // Check length of arguments passed to main
        if (args.length != 1) {
            System.out.println("Please enter the base64 string to decode as the first and only argument.");
        } else {
            //System.out.println(getDataset(args[0]));
        }

        // Get patient array
        System.out.println("--- Fetching all patients");
        List<Patient> patients = findAllPatients();
        System.out.println("Fetched " + patients.size() + " patients");

        // Get mwl_item array
        System.out.println("--- Fetching all mwl_items");
        List<Mwl_item> mwl_items = findAllMwl_items();
        System.out.println("Fetched " + mwl_items.size() + " mwl_items");

        // Process a few verifications
        for (int i = 0; i < mwl_items.size(); i++) {
            Mwl_item mwl_item_iter = mwl_items.get(i);

            // getPatient_fk should not be null
            if (mwl_item_iter.getPatient_fk() == 0) {
                System.out.println("WARNING: mwl_items[" + i + "].getPatient_fk() = 0");
            }
        }


        // Build Person Name Array

        // Mix the Person name Array


        //testPatientDAO();
        //testMwl_itemDAO();
        //testUpdatePatientInDatabase();

        // Patient Tags
        //Tags.PatientName;
        //Tags.PatientBirthDate;
        //Tags.PatientBirthName;
        //Tags.PatientBirthTime;
        //Tags.PatientAddress;
        //Tags.OtherPatientNames;
        //Tags.PatientMotherBirthName;
        //Tags.PatientPhoneNumbers;
        //Tags.ReferringPhysicianAddress
        //Tags.PersonAddress

        // Study Tags
        //Tags.StudyDate;
        //Tags.StudyTime;
        //Tags.ReferringPhysicianName;
        //Tags.NameOfPhysicianReadingStudy
        //Tags.ReferringPhysicianPhoneNumbers
        //Tags.PersonTelephoneNumbers
        //Tags.OrderCallbackPhoneNumber

        // Series Tags
        //Tags.OperatorName;
        //Tags.PerformingPhysicianName;
        //Tags.ScheduledPerformingPhysicianName;
        //Tags.HumanPerformerName;
        //Tags.VerifyingObserverName;
        //Tags.PersonName;
        //Tags.ContactDisplayName;

        //
    }

    /**
     * @param base64DICOM parameter fetched from the database, ie. select encode(pat_attrs, 'base64') from patient where pat_id = 'A10504795685'
     * @return the DICOM string
     */
    public static String getDataset(String base64DICOM) {

        // Decode the base64 data as a byte array
        byte[] baDicom = Base64.decodeBase64(base64DICOM);
        return getDataset(baDicom);
    }

    /**
     * @param baDicom parameter fetched from the database, ie. select encode(pat_attrs, 'base64') from patient where pat_id = 'A10504795685'
     * @return the DICOM string
     */
    public static String getDataset(byte[] baDicom) {
        // Dataset fromByteArray(byte[] data, Dataset ds);
        String result = "";

        try {
            // Get the Dataset from the byte array. Note : interface Dataset extends DcmObject, Serializable
            Dataset ds = fromByteArray(baDicom);

            if (ds != null) {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                try {
                    ds.dumpDataset(baos, null);
                    result = baos.toString();

                } catch (IOException ioe) {
                    result = "Miserie: " + ioe.getMessage();
                    System.out.println(ExceptionUtils.getFullStackTrace(ioe));
                    System.err.println(ExceptionUtils.getFullStackTrace(ioe));
                    Logger.getAnonymousLogger().severe(ExceptionUtils.getFullStackTrace(ioe));
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println(ExceptionUtils.getFullStackTrace(e));
            System.err.println(ExceptionUtils.getFullStackTrace(e));
            Logger.getAnonymousLogger().severe("Time is " + new Date(System.currentTimeMillis()));
            Logger.getAnonymousLogger().severe(ExceptionUtils.getFullStackTrace(e));
        }
        return result;
    }

    public static Dataset fromByteArray(byte[] data) {
        return fromByteArray(data, null);
    }

    public static Dataset fromByteArray(byte[] data, Dataset ds) {
        if (data == null) {
            return null;
        }
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        if (ds == null) {
            ds = DcmObjectFactory.getInstance().newDataset();
        }
        try {
            ds.readFile(bin, null, -1);
            // reset File Meta Information for Serialisation
            ds.setFileMetaInfo(null);
        } catch (IOException e) {
            throw new IllegalArgumentException("" + e);
        }
        return ds;
    }

    public static byte[] toByteArray(Dataset ds) {
        if (ds == null) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(ds.calcLength(DcmEncodeParam.EVR_LE));
        try {
            ds.writeDataset(bos, DcmEncodeParam.EVR_LE);
        } catch (IOException e) {
            throw new IllegalArgumentException("" + e);
        }
        return bos.toByteArray();
    }

    public static byte[] toByteArray(Dataset ds, String tsuid) {
        if (ds == null) {
            return null;
        }
        if (tsuid == null) {
            return toByteArray(ds);
        }
        FileMetaInfo fmi = DcmObjectFactory.getInstance().newFileMetaInfo();
        fmi.setPreamble(null);
        fmi.putUI(Tags.TransferSyntaxUID, tsuid);
        DcmEncodeParam encodeParam = DcmEncodeParam.valueOf(tsuid);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(fmi.length() + ds.calcLength(encodeParam));
        FileMetaInfo prevfmi = ds.getFileMetaInfo();
        ds.setFileMetaInfo(fmi);
        try {
            ds.writeFile(bos, encodeParam);
        } catch (IOException e) {
            throw new IllegalArgumentException("" + e);
        } finally {
            ds.setFileMetaInfo(prevfmi);
        }
        return bos.toByteArray();
    }

    /**
     * Test the patientDAO stuff
     */

    public static void testUpdatePatientInDatabase() {

        // Retrieve a Patient using the PatientDAO by an id
        DAO<Patient> patientDao = new PatientDAO();
        Patient aPatient = patientDao.find(1);

        // Print its DICOM attributes
        System.out.println("Patient DICOM attributes from database before modification:");
        System.out.println(aPatient.getDecodedPat_attrs());

        // Get the Dataset from the patient byte array. Note : interface Dataset extends DcmObject, Serializable
        // Modify some of the elements and put them back in the Dataset
        Dataset ds = fromByteArray(aPatient.getPat_attrs());
        if (ds != null) {
            PersonName personName = ds.getPersonName(Tags.PatientName);             // Extract the PersonName object
            aPatient.setPat_name(personName.get(PersonName.FAMILY));      // Modify in the column in 'clear'
            personName.set(PersonName.FAMILY, aPatient.getPat_name());              // Modify in the PersonName object
            ds.putPN(Tags.PatientName, personName);                                 // Modify in the DICOM dataset
            aPatient.setPat_attrs(toByteArray(ds));                                 // Put back the DICOM attributes
        }

        // Print its modified DICOM attributes
        System.out.println("Patient DICOM attributes in memory after modification:");
        System.out.println(aPatient.getDecodedPat_attrs());

        // Save in database the modified patient and print it again
        Patient updatedPatient = patientDao.update(aPatient);

        System.out.println("Patient DICOM attributes in database after update:");
        System.out.println(updatedPatient.getDecodedPat_attrs());
    }

    public static void testPatientDAO() {

        // Create a new Patient DAO
        DAO<Patient> patientDao = new PatientDAO();
        Patient pat = patientDao.find(1);
        // Take all patients in the database
        List<Patient> patients = patientDao.findAll();
        System.out.println("I found " + patients.size() + "elements");

        // Check the max number of dicom tags.
        int maxDicomTagsCount = 0;
        int indexWithMaxDicomTagsCount = -1;
        for (Patient aPatient : patients) {
            // Extract the dataset from the byte array and store the count of dicom tags if it is a maximum
            Dataset ds = fromByteArray(aPatient.getPat_attrs());
            if (ds.size() > maxDicomTagsCount) {
                maxDicomTagsCount = ds.size();
            }
        }
        System.out.println("The max number of DICOM tags in Patient is " + maxDicomTagsCount + "elements");

        List<String> patientsName = new ArrayList<String>();
        int i = 1;
        for (Patient aPatient : patients) {
            Dataset ds = fromByteArray(aPatient.getPat_attrs());
            String name = ds.getPersonName(Tags.PatientName).get(PersonName.FAMILY);
            patientsName.add(name);
            if (++i == 5) break;
        }

        System.out.println("Noms avant shuffle");
        System.out.println(Arrays.toString(patientsName.toArray()));


        Collections.shuffle(patientsName);
        System.out.println("Noms après shuffle");
        System.out.println(Arrays.toString(patientsName.toArray()));


        //long seed = System.nanoTime();
        //Collections.shuffle(list, new Random(seed));

        // Print its DICOM attributes
        System.out.println("Patient avant modif");
        System.out.println(pat.getDecodedPat_attrs());

        /*
        0000 (0008,0005) CS #10 *1 [ISO_IR 100] //Specific Character Set
        0018 (0010,0010) PN #22 *1 [DOE^JOHN ] //Patient's Name
        0048 (0010,0020) LO #12 *1 [A10008394368] //Patient ID
        0068 (0010,0021) LO #10 *1 [930300645 ] //Issuer of Patient ID
        0086 (0010,0030) DA #8 *1 [19621008] //Patient's Birth Date
        0102 (0010,0040) CS #2 *1 [M ] //Patient's Sex
        */


        // Get the Dataset from the patient byte array. Note : interface Dataset extends DcmObject, Serializable
        Dataset ds = fromByteArray(pat.getPat_attrs());

        if (ds != null) {

            // <mystuff>
            PersonName personName = ds.getPersonName(Tags.PatientName);
            System.out.println("avant:" + personName.toString());
            personName.set(PersonName.FAMILY, "TOTO");
            System.out.println("apres:" + personName.toString());
            ds.putCS(Tags.PatientName, personName.toString());
            pat.setPat_attrs(toByteArray(ds));

            System.out.println("Patient apres modif");
            System.out.println(pat.getDecodedPat_attrs());

            // </mystuff>
        }

        //pat.setPat_name("toto");
        //patientDao.update(pat);

        //System.out.println("Patient après modif");
        //System.out.println(pat);
        //System.out.println(pat.getDecodedPat_attrs());

        /*byte[] blobDicom = rs.getBytes("pat_attrs");
        byte[] baDicom = Base64.encodeBase64(blobDicom);
        String data = null;
        try {
            data = new String(baDicom, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Test the Mwl_itemDAO stuff
     */
    public static void testMwl_itemDAO() {

        DAO<Mwl_item> mwl_itemDAO = new Mwl_itemDAO();
        Mwl_item mwl_item = mwl_itemDAO.find(1);
        System.out.println(mwl_item.getDecodedItem_attrs());
        List<Mwl_item> mwl_items = mwl_itemDAO.findAll();
        System.out.println("I found " + mwl_items.size() + "elements");

        // Check the min, max number of dicom tags.
        int maxDicomTagsCount = -1;
        int minDicomTagsCount = -1;
        int indexWithMaxDicomTagsCount = -1;
        for (Mwl_item anMwl_item : mwl_items) {
            // Extract the dataset from the byte array and store the count of dicom tags if it is a maximum
            Dataset ds = fromByteArray(anMwl_item.getItem_attrs());
            if (minDicomTagsCount == -1 && maxDicomTagsCount == -1) {
                minDicomTagsCount = ds.size();
                maxDicomTagsCount = ds.size();
            }
            if (ds.size() > maxDicomTagsCount) {
                maxDicomTagsCount = ds.size();
            }
            if (ds.size() < minDicomTagsCount) {
                minDicomTagsCount = ds.size();
            }
        }
        System.out.println("The max number of DICOM tags in MWL_item is " + maxDicomTagsCount + "elements");
        System.out.println("The min number of DICOM tags in MWL_item is " + minDicomTagsCount + "elements");

/*
        List<String> patientsName = new ArrayList<String>();
        int i = 1;
        for (Patient aPatient : patients) {
            Dataset ds = fromByteArray(aPatient.getPat_attrs());
            String name = ds.getPersonName(Tags.PatientName).get(PersonName.FAMILY);
            patientsName.add(name);
            if (++i == 5) break;
        }

        System.out.println("Noms avant shuffle");
        System.out.println(Arrays.toString(patientsName.toArray()));


        Collections.shuffle(patientsName);
        System.out.println("Noms après shuffle");
        System.out.println(Arrays.toString(patientsName.toArray()));


        //long seed = System.nanoTime();
        //Collections.shuffle(list, new Random(seed));

        // Print its DICOM attributes
        System.out.println("Patient avant modif");
        System.out.println(pat.getDecodedPat_attrs());

        /*
        0000 (0008,0005) CS #10 *1 [ISO_IR 100] //Specific Character Set
        0018 (0010,0010) PN #22 *1 [DOE^JOHN ] //Patient's Name
        0048 (0010,0020) LO #12 *1 [A10008394368] //Patient ID
        0068 (0010,0021) LO #10 *1 [930300645 ] //Issuer of Patient ID
        0086 (0010,0030) DA #8 *1 [19621008] //Patient's Birth Date
        0102 (0010,0040) CS #2 *1 [M ] //Patient's Sex
        */

/*
        // Get the Dataset from the patient byte array. Note : interface Dataset extends DcmObject, Serializable
        Dataset ds = fromByteArray(pat.getPat_attrs());

        if (ds != null) {

            // <mystuff>
            PersonName personName = ds.getPersonName(Tags.PatientName);
            System.out.println("avant:" + personName.toString());
            personName.set(PersonName.FAMILY, "TOTO");
            System.out.println("apres:" + personName.toString());
            ds.putCS(Tags.PatientName, personName.toString());
            pat.setPat_attrs(toByteArray(ds));

            System.out.println("Patient apres modif");
            System.out.println(pat.getDecodedPat_attrs());

            // </mystuff>
        }
*/


        //pat.setPat_name("toto");
        //patientDao.update(pat);

        //System.out.println("Patient après modif");
        //System.out.println(pat);
        //System.out.println(pat.getDecodedPat_attrs());

        /*byte[] blobDicom = rs.getBytes("pat_attrs");
        byte[] baDicom = Base64.encodeBase64(blobDicom);
        String data = null;
        try {
            data = new String(baDicom, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
    }

    private static List<Patient> findAllPatients() {
        // Create a new Patient DAO and take all patients in the database
        DAO<Patient> patientDao = new PatientDAO();
        List<Patient> patients = patientDao.findAll();
        return patients;
    }

    private static List<Mwl_item> findAllMwl_items() {
        // Create a new Mwl_item DAO and take all mwl_items in the database
        DAO<Mwl_item> mwl_itemDAO = new Mwl_itemDAO();
        List<Mwl_item> mwl_items = mwl_itemDAO.findAll();
        return mwl_items;
    }
}
