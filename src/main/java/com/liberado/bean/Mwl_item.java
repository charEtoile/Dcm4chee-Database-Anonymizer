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

/**
 * Created by LiberadM on 28/06/2017.
 */
public class Mwl_item {
    private long pk = 0;
    private long patient_fk;
    private int sps_status;
    private String sps_id = "";
    private Timestamp start_datetime;
    private String station_aet = "";
    private String station_name = "";
    private String modality = "";
    private String perf_physician = "";
    private String perf_phys_i_name = "";
    private String perf_phys_p_name = "";
    private String req_proc_id = "";
    private String accession_no = "";
    private String study_iuid = "";
    private Timestamp created_time;
    private Timestamp updated_time;
    private byte[] item_attrs;

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public long getPatient_fk() {
        return patient_fk;
    }

    public void setPatient_fk(long patient_fk) {
        this.patient_fk = patient_fk;
    }

    public int getSps_status() {
        return sps_status;
    }

    public void setSps_status(int sps_status) {
        this.sps_status = sps_status;
    }

    public String getSps_id() {
        return sps_id;
    }

    public void setSps_id(String sps_id) {
        this.sps_id = sps_id;
    }

    public Timestamp getStart_datetime() {
        return start_datetime;
    }

    public void setStart_datetime(Timestamp start_datetime) {
        this.start_datetime = start_datetime;
    }

    public String getStation_aet() {
        return station_aet;
    }

    public void setStation_aet(String station_aet) {
        this.station_aet = station_aet;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getPerf_physician() {
        return perf_physician;
    }

    public void setPerf_physician(String perf_physician) {
        this.perf_physician = perf_physician;
    }

    public String getPerf_phys_i_name() {
        return perf_phys_i_name;
    }

    public void setPerf_phys_i_name(String perf_phys_i_name) {
        this.perf_phys_i_name = perf_phys_i_name;
    }

    public String getPerf_phys_p_name() {
        return perf_phys_p_name;
    }

    public void setPerf_phys_p_name(String perf_phys_p_name) {
        this.perf_phys_p_name = perf_phys_p_name;
    }

    public String getReq_proc_id() {
        return req_proc_id;
    }

    public void setReq_proc_id(String req_proc_id) {
        this.req_proc_id = req_proc_id;
    }

    public String getAccession_no() {
        return accession_no;
    }

    public void setAccession_no(String accession_no) {
        this.accession_no = accession_no;
    }

    public String getStudy_iuid() {
        return study_iuid;
    }

    public void setStudy_iuid(String study_iuid) {
        this.study_iuid = study_iuid;
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

    public byte[] getItem_attrs() {
        return item_attrs;
    }

    public void setItem_attrs(byte[] item_attrs) {
        this.item_attrs = item_attrs;
    }

    public Mwl_item() {}

    @Override
    public String toString() {
        return "MWL_ITEM : " + this.getSps_id();
    }

    public String getDecodedItem_attrs(){
        return Metadata.getDataset(this.item_attrs);
    }
}
