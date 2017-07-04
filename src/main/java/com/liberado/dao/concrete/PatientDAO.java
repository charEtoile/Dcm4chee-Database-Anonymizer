package com.liberado.dao.concrete;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.liberado.bean.Patient;
import com.liberado.dao.DAO;

/**
 * Created by admin on 22/06/2017.
 */
public class PatientDAO extends DAO<Patient> {

    @Override
    public Patient create(Patient obj) {
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
    public Patient find(long id) {
        Patient patient = new Patient();
        try {
            ResultSet result = this.connect.createStatement(
                                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                                            ResultSet.CONCUR_UPDATABLE)
                                            .executeQuery("SELECT * FROM patient WHERE pk = " + id);
            if (result.first()) {
                patient.setPk(result.getLong("pk"));
                patient.setMerge_fk(result.getLong("merge_fk"));
                patient.setPat_id(result.getString("pat_id"));
                patient.setPat_id_issuer(result.getString("pat_id_issuer"));
                patient.setPat_name(result.getString("pat_name"));
                patient.setPat_i_name(result.getString("pat_i_name"));
                patient.setPat_p_name(result.getString("pat_p_name"));
                patient.setPat_birthdate(result.getString("pat_birthdate"));
                patient.setPat_sex(result.getString("pat_sex"));
                patient.setPat_custom1(result.getString("pat_custom1"));
                patient.setPat_custom2(result.getString("pat_custom1"));
                patient.setPat_custom3(result.getString("pat_custom1"));
                patient.setCreated_time(result.getTimestamp("created_time"));
                patient.setUpdated_time(result.getTimestamp("updated_time"));
                patient.setPat_attrs(result.getBytes("pat_attrs"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patient;
    }

    @Override
    public Patient update(Patient obj) {

        String stmt = "UPDATE patient SET (merge_fk, pat_id, pat_id_issuer, pat_name, pat_i_name, " +
                "pat_p_name, pat_birthdate, pat_sex, pat_custom1, pat_custom2, pat_custom3, " +
                "created_time, updated_time, pat_attrs) = (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) WHERE pk = ?;";

        try {
            /*this.connect.createStatement(
                                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                                    ResultSet.CONCUR_UPDATABLE).executeUpdate(stmt);*/
            PreparedStatement ps = this.connect.prepareStatement(stmt, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setNull(1, Types.BIGINT/*obj.getMerge_fk()*/);
            ps.setString(2, obj.getPat_id());
            ps.setString(3, obj.getPat_id_issuer());
            ps.setString(4, obj.getPat_name());
            ps.setString(5, obj.getPat_i_name());
            ps.setString(6, obj.getPat_p_name());
            ps.setString(7, obj.getPat_birthdate());
            ps.setString(8, obj.getPat_sex());
            ps.setString(9, obj.getPat_custom1());
            ps.setString(10, obj.getPat_custom2());
            ps.setString(11, obj.getPat_custom3());
            ps.setTimestamp(12, obj.getCreated_time());
            // Manually set the updated_time to the current time and also set it in the object
            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
            obj.setUpdated_time(currentTimestamp);
            //private Timestamp updated_time;
            ps.setTimestamp(13, currentTimestamp);
            ps.setBytes(14, obj.getPat_attrs());
            ps.setLong(15, obj.getPk());
            ps.executeUpdate();
            ps.close();

            obj = this.find(obj.getPk());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return obj;
    }

    @Override
    public void delete(Patient obj) {
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
    public List<Patient> findAll() {
        List<Patient> patients = new ArrayList<Patient>();

        try {
            ResultSet result = this.connect.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE)
                    .executeQuery("SELECT * FROM patient");

            while (result.next()) {
                Patient patient = new Patient();
                patient.setPk(result.getLong("pk"));
                patient.setMerge_fk(result.getLong("merge_fk"));
                patient.setPat_id(result.getString("pat_id"));
                patient.setPat_id_issuer(result.getString("pat_id_issuer"));
                patient.setPat_name(result.getString("pat_name"));
                patient.setPat_i_name(result.getString("pat_i_name"));
                patient.setPat_p_name(result.getString("pat_p_name"));
                patient.setPat_birthdate(result.getString("pat_birthdate"));
                patient.setPat_sex(result.getString("pat_sex"));
                patient.setPat_custom1(result.getString("pat_custom1"));
                patient.setPat_custom2(result.getString("pat_custom1"));
                patient.setPat_custom3(result.getString("pat_custom1"));
                patient.setCreated_time(result.getTimestamp("created_time"));
                patient.setUpdated_time(result.getTimestamp("updated_time"));
                patient.setPat_attrs(result.getBytes("pat_attrs"));
                patients.add(patient);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }
}