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
import org.apache.commons.lang.exception.ExceptionUtils;
import org.dcm4che.data.*;
import org.dcm4che.dict.Tags;

import java.util.concurrent.ThreadLocalRandom;


/**
 *
 * @author Michel LIBERADO
 * https://sourceforge.net/p/dcm4che/svn/18203/tree/dcm4che14/trunk/src/java/org/dcm4che/data/Dataset.java
 */
public class Metadata {

    public static void main(String[] args) throws Exception {

        // Check length of arguments passed to main
        if (args.length != 1) {
            System.out.println("Please enter the base64 string to decode as the first and only argument.");
        } else {
            //System.out.println(getDataset(args[0]));
        }

        // Build a list of some tags that we want to modify be cause they could identify a person
        List<DcmElementOccurenceCount> list = new ArrayList<DcmElementOccurenceCount>() {
            {
                add(new DcmElementOccurenceCount(Tags.PatientName));
                add(new DcmElementOccurenceCount(Tags.PatientBirthDate));
                add(new DcmElementOccurenceCount(Tags.PatientBirthName));
                add(new DcmElementOccurenceCount(Tags.PatientBirthTime));
                add(new DcmElementOccurenceCount(Tags.PersonAddress));
                add(new DcmElementOccurenceCount(Tags.OtherPatientNames));
                add(new DcmElementOccurenceCount(Tags.PatientMotherBirthName));
                add(new DcmElementOccurenceCount(Tags.PatientPhoneNumbers));
                add(new DcmElementOccurenceCount(Tags.PersonAddress));
                add(new DcmElementOccurenceCount(Tags.ReferringPhysicianAddress));
                add(new DcmElementOccurenceCount(Tags.ReferringPhysicianName));
                add(new DcmElementOccurenceCount(Tags.NameOfPhysicianReadingStudy));
                add(new DcmElementOccurenceCount(Tags.ReferringPhysicianPhoneNumbers));
                add(new DcmElementOccurenceCount(Tags.PersonTelephoneNumbers));
                add(new DcmElementOccurenceCount(Tags.OrderCallbackPhoneNumber));
                add(new DcmElementOccurenceCount(Tags.OperatorName));
                add(new DcmElementOccurenceCount(Tags.PerformingPhysicianName));
                add(new DcmElementOccurenceCount(Tags.ScheduledPerformingPhysicianName));
                add(new DcmElementOccurenceCount(Tags.HumanPerformerName));
                add(new DcmElementOccurenceCount(Tags.VerifyingObserverName));
                add(new DcmElementOccurenceCount(Tags.PersonName));
                add(new DcmElementOccurenceCount(Tags.ContactDisplayName));
            }
        };

        // Get patient array and copy it in another array
        System.out.println("--- Fetching all patients");
        List<Patient> patients = findAllPatients();
        List<Patient> patients_copy = findAllPatients();
        System.out.println("Fetched " + patients.size() + " patients");

        // Get mwl_item array
        System.out.println("--- Fetching all mwl_items");
        List<Mwl_item> mwl_items = findAllMwl_items();
        System.out.println("Fetched " + mwl_items.size() + " mwl_items");

        // Check presence of some tags in patient and mwl_item arrays and print the result
        for (int i = 0; i < patients.size(); i++) {
            Dataset ds = fromByteArray(patients.get(i).getPat_attrs());

            for (int j = 0; j < list.size(); j++) {
                if (ds.containsValue(list.get(j).getTag())) {
                    list.get(j).setCount(list.get(j).getCount() + 1);
                }
            }
        }

        for (int i = 0; i < mwl_items.size(); i++) {
            Dataset ds = fromByteArray(mwl_items.get(i).getItem_attrs());

            for (int j = 0; j < list.size(); j++) {
                if (ds.containsValue(list.get(j).getTag())) {
                    list.get(j).setCount(list.get(j).getCount() + 1);
                }
            }
        }

        for (int j = 0; j < list.size(); j++) {
            System.out.println(list.get(j).toString());
        }

        // Change FAMILY Name and GIVEN name
        // for each index, randomize another index, change something with conditions
        for (int i = 0; i < patients.size(); i++) {
            String familyName = patients.get(i).getPersonNameFieldFromDICOMAttributes(PersonName.FAMILY);
            boolean loopAgain = true;
            int randomFamilyNameIndex = 0;
            do {
                randomFamilyNameIndex = ThreadLocalRandom.current().nextInt(0, patients.size());
                String nameAtRandomFamilyNameIndex = patients.get(randomFamilyNameIndex).getPersonNameFieldFromDICOMAttributes(PersonName.FAMILY);
                if (!familyName.equals(nameAtRandomFamilyNameIndex)) {
                    patients_copy.get(i).setPersonNameFieldInDICOMAttributes(PersonName.FAMILY, nameAtRandomFamilyNameIndex);
                    loopAgain = false;
                }
            }
            while(loopAgain);

            // Change Given name only if same sex and do not pick up the same given name that was randomly picked up for family name
            String givenName = patients.get(i).getPersonNameFieldFromDICOMAttributes(PersonName.GIVEN);
            loopAgain = true;
            int randomGivenNameIndex = 0;
            do {
                randomGivenNameIndex = ThreadLocalRandom.current().nextInt(0, patients.size());
                // test if iterated patient has same sex as randomly pointed one
                if (randomGivenNameIndex != randomFamilyNameIndex &&
                        !patients.get(i).getPat_sex().equals(patients.get(randomGivenNameIndex).getPat_sex())) {
                    String nameAtRandomGivenNameIndex = patients.get(randomGivenNameIndex).getPersonNameFieldFromDICOMAttributes(PersonName.GIVEN);
                    // Apply random name to current only if different
                    if (!givenName.equals(nameAtRandomGivenNameIndex)) {
                        patients_copy.get(i).setPersonNameFieldInDICOMAttributes(PersonName.GIVEN, nameAtRandomGivenNameIndex);
                        loopAgain = false;
                    }
                }
            }
            while(loopAgain);
        }

        List<String> physicians = new ArrayList<String>();
        for (int i = 0; i < mwl_items.size(); i++) {
            Dataset ds = fromByteArray(mwl_items.get(i).getItem_attrs());
            PersonName physician = ds.getPersonName(Tags.ReferringPhysicianName);
            if (!physicians.contains(physician.toString())) {
                physicians.add(physician.toString());
            }
        }
        for (int i = 0; i < physicians.size(); i++) {
            System.out.println("=== Found : " + physicians.size() + " physicians.");
            System.out.println(physicians.get(i).toString());
        }


        System.out.println("BEFORE === VS === AFTER");
        for (int i = 0; i < patients.size() && i < 50; i++) {
            System.out.println(
                    patients.get(i).getPersonNameFieldFromDICOMAttributes(PersonName.FAMILY) + " " + patients.get(i).getPersonNameFieldFromDICOMAttributes(PersonName.GIVEN) +
                            " === VS === " +
                    patients_copy.get(i).getPersonNameFieldFromDICOMAttributes(PersonName.FAMILY) + " " + patients_copy.get(i).getPersonNameFieldFromDICOMAttributes(PersonName.GIVEN));
        }

        System.out.println("pat_name === VS === personName");
        for (int i = 0; i < patients_copy.size() && i < 50; i++) {
            System.out.println(
                    patients_copy.get(i).getPat_name() +
                            " === VS === " +
                    patients_copy.get(i).getPersonName());
        }
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

    private static void fixPatientList(List<Patient> patients) {
        for (int i = 0; i < patients.size(); i++) {
            String pat_name = patients.get(i).getPat_name();
            String family = patients.get(i).getPersonNameFieldFromDICOMAttributes(PersonName.FAMILY);
            String given = patients.get(i).getPersonNameFieldFromDICOMAttributes(PersonName.GIVEN);
            String familyAndGiven = family + "^" + given;
            if (family == null) {
                System.out.println("WARNING: patients[" + i + "].FAMILY = null - " + patients.get(i).getPat_name());
            }
            if (given == null) {
                System.out.println("WARNING: patients[" + i + "].GIVEN = null" + patients.get(i).getPat_name());
            }
        }
    }
}
