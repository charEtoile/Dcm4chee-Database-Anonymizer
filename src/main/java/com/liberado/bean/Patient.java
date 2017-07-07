package com.liberado.bean;

/*
 Description of the patient table and other tables
 https://dcm4che.atlassian.net/wiki/display/ee2/Database+Table+Descriptions

 Description of DICOM attribute filter and custom filter
 https://dcm4che.atlassian.net/wiki/display/ee2/DICOM+Attribute+Filter

 PostgreSQL data types mapped Java classes for JDBC
 https://www.postgresql.org/message-id/AANLkTikkkxN+-UUiGVTzj8jdfS4PdpB8_tDONMFHNqHk@mail.gmail.com
 */

import java.sql.Timestamp;
import com.liberado.util.Metadata;
import org.dcm4che.data.Dataset;
import org.dcm4che.data.PersonName;
import org.dcm4che.dict.Tags;

import static com.liberado.util.Metadata.fromByteArray;
import static com.liberado.util.Metadata.toByteArray;

/**
 * Created by admin on 21/06/2017.
 */
public class Patient {

    // Manual ORM
    private long pk = 0;
    private long merge_fk = 0;
    private String pat_id = "";
    private String pat_id_issuer = "";
    private String pat_name = "";
    private String pat_i_name = "";
    private String pat_p_name = "";
    private String pat_birthdate = "";
    private String pat_sex = "";
    private String pat_custom1 = "";
    private String pat_custom2 = "";
    private String pat_custom3 = "";
    private Timestamp created_time;
    private Timestamp updated_time;
    private byte[] pat_attrs;

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public long getMerge_fk() {
        return merge_fk;
    }

    public void setMerge_fk(long merge_fk) {
        this.merge_fk = merge_fk;
    }

    public String getPat_id() {
        return pat_id;
    }

    public void setPat_id(String pat_id) {
        this.pat_id = pat_id;
    }

    public String getPat_id_issuer() {
        return pat_id_issuer;
    }

    public void setPat_id_issuer(String pat_id_issuer) {
        this.pat_id_issuer = pat_id_issuer;
    }

    public String getPat_name() {
        return pat_name;
    }

    public void setPat_name(String pat_name) {
        this.pat_name = pat_name;
    }

    public String getPat_i_name() {
        return pat_i_name;
    }

    public void setPat_i_name(String pat_i_name) {
        this.pat_i_name = pat_i_name;
    }

    public String getPat_p_name() {
        return pat_p_name;
    }

    public void setPat_p_name(String pat_p_name) {
        this.pat_p_name = pat_p_name;
    }

    public String getPat_birthdate() {
        return pat_birthdate;
    }

    public void setPat_birthdate(String pat_birthdate) {
        this.pat_birthdate = pat_birthdate;
    }

    public String getPat_sex() {
        return pat_sex;
    }

    public void setPat_sex(String pat_sex) {
        this.pat_sex = pat_sex;
    }

    public String getPat_custom1() {
        return pat_custom1;
    }

    public void setPat_custom1(String pat_custom1) {
        this.pat_custom1 = pat_custom1;
    }

    public String getPat_custom2() {
        return pat_custom2;
    }

    public void setPat_custom2(String pat_custom2) {
        this.pat_custom2 = pat_custom2;
    }

    public String getPat_custom3() {
        return pat_custom3;
    }

    public void setPat_custom3(String pat_custom3) {
        this.pat_custom3 = pat_custom3;
    }

    public Timestamp getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Timestamp created_time) {
        this.created_time = created_time;
    }

    public Timestamp getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(Timestamp updated_time) {
        this.updated_time = updated_time;
    }

    public byte[] getPat_attrs() {
        return pat_attrs;
    }

    public void setPat_attrs(byte[] pat_attrs) {
        this.pat_attrs = pat_attrs;
    }

    // Other methods
    public Patient(){}

    @Override
    public String toString() {
        return "PATIENT : pat_name = " + this.pat_name +
                " FAMILY: " + getPersonNameFieldFromDICOMAttributes(PersonName.FAMILY) +
                " GIVEN: " + getPersonNameFieldFromDICOMAttributes(PersonName.GIVEN);
    }

    public String getDecodedPat_attrs(){
        return Metadata.getDataset(this.pat_attrs);
    }

    public String getPersonNameFieldFromDICOMAttributes(int field) {

        String res = "";
        Dataset ds = fromByteArray(this.getPat_attrs());
        PersonName personName = ds.getPersonName(Tags.PatientName);

        switch (field) {
            case PersonName.FAMILY:
            case PersonName.GIVEN:
            case PersonName.MIDDLE:
            case PersonName.PREFIX:
            case PersonName.SUFFIX:
                res = personName.get(field);
                break;
        }

        return res;
    }

    public void setPersonNameFieldInDICOMAttributes(int field, String name) {

        switch (field) {
            case PersonName.FAMILY:
            case PersonName.GIVEN:
            case PersonName.MIDDLE:
            case PersonName.PREFIX:
            case PersonName.SUFFIX:
                Dataset ds = fromByteArray(this.getPat_attrs());
                PersonName personName = ds.getPersonName(Tags.PatientName);             // Extract the PersonName object
                //aPatient.setPat_name(personName.get(PersonName.FAMILY));              // Modify in the column in 'clear'
                personName.set(field, name);                                            // Modify in the PersonName object
                ds.putPN(Tags.PatientName, personName);                                 // Modify in the DICOM dataset
                setPat_attrs(toByteArray(ds));                                          // Put back the DICOM attributes
                break;
        }
    }
}