package com.liberado.dao.concrete;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.liberado.bean.Mwl_item;
import com.liberado.dao.DAO;

/**
 * Created by LiberadM on 28/06/2017.
 */
public class Mwl_itemDAO extends DAO<Mwl_item> {

    @Override
    public Mwl_item create(Mwl_item obj) {
        /*try {

            //Vu que nous sommes sous postgres, nous allons chercher manuellement
            //la prochaine valeur de la séquence correspondant à l'id de notre table
            ResultSet result = this	.connect
                    .createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE
                    ).executeQuery(
                            "SELECT NEXTVAL('langage_lan_id_seq') as id"
                    );
            if(result.first()){
                long id = result.getLong("id");
                PreparedStatement prepare = this	.connect
                        .prepareStatement(
                                "INSERT INTO langage (lan_id, lan_nom) VALUES(?, ?)"
                        );
                prepare.setLong(1, id);
                prepare.setString(2, obj.getNom());

                prepare.executeUpdate();
                obj = this.find(id);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        return obj;
    }

/*
            if (c.isValid(2000)) {
        // On peut essayer de lire des entrées dans la database maintenant.
        Statement stmt = c.createStatement();
        String sql = "select * from patient where pk < 10;";
        ResultSet rs = stmt.executeQuery(sql);

        // sortir un tableau
        System.out.println("FAMILY;GIVEN;MIDDLE;PREFIX;SUFFIX");
        while (rs.next()) {
            // Lire le blob dans la db, le convertir en base64, en faire une String et extraire les tags DICOM
            byte[] blobDicom = rs.getBytes("pat_attrs");
            byte[] baDicom = Base64.encodeBase64(blobDicom);
            String data = null;
            try {
                data = new String(baDicom, "US-ASCII");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            getDataset(data);
        }

        rs.close();
        stmt.close();
    }
        c.close();
*/


    @Override
    public Mwl_item find(long id) {
        Mwl_item mwl_item = new Mwl_item();
        try {
            ResultSet result = this.connect.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE)
                    .executeQuery("SELECT * FROM mwl_item WHERE pk = " + id);
            if (result.first()) {
                mwl_item.setPk(result.getLong("pk"));
                mwl_item.setPatient_fk(result.getLong("patient_fk"));
                mwl_item.setSps_status(result.getInt("sps_status"));
                mwl_item.setSps_id(result.getString("sps_id"));
                mwl_item.setStart_datetime(result.getTimestamp("start_datetime"));
                mwl_item.setStation_aet(result.getString("station_aet"));
                mwl_item.setStation_name(result.getString("station_name"));
                mwl_item.setModality(result.getString("modality"));
                mwl_item.setPerf_physician(result.getString("perf_physician"));
                mwl_item.setPerf_phys_i_name(result.getString("perf_phys_i_name"));
                mwl_item.setPerf_phys_p_name(result.getString("perf_phys_p_name"));
                mwl_item.setReq_proc_id(result.getString("req_proc_id"));
                mwl_item.setAccession_no(result.getString("accession_no"));
                mwl_item.setStudy_iuid(result.getString("study_iuid"));
                mwl_item.setCreated_time(result.getTimestamp("created_time"));
                mwl_item.setUpdated_time(result.getTimestamp("updated_time"));
                mwl_item.setItem_attrs(result.getBytes("item_attrs"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mwl_item;
    }

    @Override
    public Mwl_item update(Mwl_item obj) {

        String stmt = "UPDATE mwl_item SET (patient_fk, spsStatus, sps_id, start_datetime, station_aet, " +
                "station_name, modality, perf_physician, perf_phys_i_name, perf_phys_p_name, req_proc_id, " +
                "accession_no, study_iuid, created_time, updated_time, item_attrs) = " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) WHERE pk = ?;";

        try {
            /*this.connect.createStatement(
                                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                                    ResultSet.CONCUR_UPDATABLE).executeUpdate(stmt);*/
            PreparedStatement ps = this.connect.prepareStatement(stmt, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setLong(1, obj.getPatient_fk());
            ps.setInt(2, obj.getSps_status());
            ps.setString(3, obj.getSps_id());
            ps.setTimestamp(4, obj.getStart_datetime());
            ps.setString(5, obj.getStation_aet());
            ps.setString(6, obj.getStation_name());
            ps.setString(7, obj.getModality());
            ps.setString(8, obj.getPerf_physician());
            ps.setString(9, obj.getPerf_phys_i_name());
            ps.setString(10, obj.getPerf_phys_p_name());
            ps.setString(11, obj.getReq_proc_id());
            ps.setString(12, obj.getAccession_no());
            ps.setString(13, obj.getStudy_iuid());
            ps.setTimestamp(14, obj.getCreated_time());
            // Manually set the updated_time to the current time and also set it in the object
            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
            obj.setUpdated_time(currentTimestamp);
            ps.setTimestamp(15, currentTimestamp);
            ps.setBytes(16, obj.getItem_attrs());
            ps.setLong(17, obj.getPk());
            ps.executeUpdate();
            ps.close();

            obj = this.find(obj.getPk());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return obj;
    }

    @Override
    public void delete(Mwl_item obj) {
        /*try {

            this    .connect
                    .createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE
                    ).executeUpdate(
                    "DELETE FROM langage WHERE lan_id = " + obj.getId()
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public List<Mwl_item> findAll() {
        List<Mwl_item> mwl_items = new ArrayList<Mwl_item>();

        try {
            ResultSet result = this.connect.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE)
                    .executeQuery("SELECT * FROM mwl_item");

            while (result.next()) {
                Mwl_item mwl_item = new Mwl_item();
                mwl_item.setPk(result.getLong("pk"));
                mwl_item.setPatient_fk(result.getLong("patient_fk"));
                mwl_item.setSps_status(result.getInt("sps_status"));
                mwl_item.setSps_id(result.getString("sps_id"));
                mwl_item.setStart_datetime(result.getTimestamp("start_datetime"));
                mwl_item.setStation_aet(result.getString("station_aet"));
                mwl_item.setStation_name(result.getString("station_name"));
                mwl_item.setModality(result.getString("modality"));
                mwl_item.setPerf_physician(result.getString("perf_physician"));
                mwl_item.setPerf_phys_i_name(result.getString("perf_phys_i_name"));
                mwl_item.setPerf_phys_p_name(result.getString("perf_phys_p_name"));
                mwl_item.setReq_proc_id(result.getString("req_proc_id"));
                mwl_item.setAccession_no(result.getString("accession_no"));
                mwl_item.setStudy_iuid(result.getString("study_iuid"));
                mwl_item.setCreated_time(result.getTimestamp("created_time"));
                mwl_item.setUpdated_time(result.getTimestamp("updated_time"));
                mwl_item.setItem_attrs(result.getBytes("item_attrs"));
                mwl_items.add(mwl_item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mwl_items;
    }
}
