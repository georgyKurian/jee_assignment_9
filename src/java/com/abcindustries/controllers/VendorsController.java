/*
 * Copyright 2015 Len Payne <len.payne@lambtoncollege.ca>.
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
package com.abcindustries.controllers;

import com.abcindustries.entities.Product;
import com.abcindustries.entities.Vendor;
import java.util.ArrayList;
import com.abcindustries.entities.Vendor;
import com.abcindustries.utilities.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

/**
 *
 * @author <ENTER YOUR NAME HERE>
 */
@ApplicationScoped
public class VendorsController {

    List<Vendor> vendorList;

    public VendorsController() {
        retrieveAllVendors();
    }

    public List<Vendor> getVendorList() {
        return vendorList;
    }

    public void setVendorList(List<Vendor> vendorList) {
        this.vendorList = vendorList;
    }

    public void retrieveAllVendors() {
        try {
            vendorList = new ArrayList<>();
            String sql = "SELECT * FROM vendors";
            Connection conn = DBUtils.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Vendor pro = new Vendor(
                        rs.getInt("vendorId"),
                        rs.getString("name"),
                        rs.getString("contactName"),
                        rs.getString("phoneNumber"));
                vendorList.add(pro);
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(VendorsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addToDb(Vendor p) {
        try {
            String sql = "INSERT INTO vendors (VendorId, Name, contactName,phoneNumber) VALUES (?, ?, ?, ?)";
            Connection conn = DBUtils.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, p.getVendorId());
            pstmt.setString(2, p.getName());
            pstmt.setString(3, p.getContactName());
            pstmt.setString(4, p.getPhoneNumber());

            pstmt.executeUpdate();
            if (p.getVendorId() <= 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                rs.next();
                int id = rs.getInt(1);
                p.setVendorId(id);
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(VendorsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeFromDb(Vendor p) {
        try {
            String sql = "DELETE FROM vendors WHERE vendorId= ?";
            Connection conn = DBUtils.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, p.getVendorId());
            pstmt.executeUpdate();

            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(VendorsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateToDb(Vendor p) {
        try {
            String sql = "UPDATE vendors SET Name = ?, contactName = ?, phoneNumber = ? WHERE VendorId = ?";
            Connection conn = DBUtils.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, p.getName());
            pstmt.setString(2, p.getContactName());
            pstmt.setString(3, p.getPhoneNumber());
            pstmt.setInt(4, p.getVendorId());

            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(VendorsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Vendor> getAll() {
        return vendorList;
    }

    public JsonArray getAllByJSON() {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (Vendor v : vendorList) {
            jsonArrayBuilder.add(v.toJson());
        }
        return jsonArrayBuilder.build();
    }

    public Vendor getById(int id) {
        Vendor result = null;
        for (Vendor p : vendorList) {
            if (p.getVendorId() == id) {
                result = p;
            }
        }
        return result;
    }

    public JsonObject getByIdJson(int id) {
        return getById(id).toJson();
    }

    public JsonArray getBySearchJson(String query) {
        JsonArrayBuilder json = Json.createArrayBuilder();
        for (Vendor ven : vendorList) {
            if (ven.getName().contains(query)) {
                json.add(ven.toJson());
            }
        }
        return json.build();
    }

    public JsonObject addJson(JsonObject json) {
        Vendor p = new Vendor(json);
        addToDb(p);
        vendorList.add(p);
        return p.toJson();
    }

    public JsonObject editJson(int id, JsonObject json) {
        // TODO: Input the JsonObject at the specified id if it already exists
        Vendor pro = new Vendor(json);
        for (Vendor p : vendorList) {
            if (p.getVendorId() == pro.getVendorId()) {
                p.setName(pro.getName());
                p.setContactName(pro.getContactName());
                p.setPhoneNumber(pro.getPhoneNumber());
                updateToDb(p);
                return p.toJson();
            }
        }
        return null;
    }

    public JsonObject delete(int id) {
        for (Vendor p : vendorList) {
            if (p.getVendorId() == id) {
                vendorList.remove(p);
                removeFromDb(p);
                return p.toJson();
            }
        }
        return null;
    }

}
