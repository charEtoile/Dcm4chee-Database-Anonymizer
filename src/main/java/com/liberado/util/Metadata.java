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
 */
package com.liberado.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import com.liberado.bean.Patient;
import com.liberado.dao.DAO;
import com.liberado.dao.concrete.PatientDAO;
import org.apache.commons.codec.binary.Base64;
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

        DAO<Patient> patientDao = new PatientDAO();
        Patient pat = patientDao.find(1);
        System.out.println("Patient avant modif");
        System.out.println(pat.getDecodedPat_attrs());

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

                // <mystuff>
                PersonName personName = ds.getPersonName(Tags.PatientName);
                System.out.println(personName.toString());

                System.out.println(personName.get(PersonName.FAMILY) + ";" +
                        personName.get(PersonName.GIVEN) + ";" +
                        personName.get(PersonName.GIVEN) + ";" +
                        personName.get(PersonName.SUFFIX));
                // </mystuff>

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
}
