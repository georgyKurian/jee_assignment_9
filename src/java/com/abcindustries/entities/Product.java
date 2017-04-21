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
package com.abcindustries.entities;

import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author <ENTER YOUR NAME HERE>
 */
public class Product {

    private int productId;
    private String name;
    private int vendorId;

    public Product() {
    }

    public Product(int productId, String name, int vendorId) {
        this.productId = productId;
        this.name = name;
        this.vendorId = vendorId;
    }

    public Product(JsonObject json) {
        productId = json.getInt("productId", 0);
        name = json.getString("name", "");
        vendorId = json.getInt("vendorId", 0);
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("productId", productId)
                .add("name", name)
                .add("vendorId", vendorId)
                .build();
    }
}
