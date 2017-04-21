/*
 * Copyright 2016 Len Payne <len.payne@lambtoncollege.ca>.
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
package com.abcindustries.websockets;

import com.abcindustries.controllers.ProductsController;
import com.abcindustries.controllers.VendorsController;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.OnMessage;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Georgi
 */
@ServerEndpoint("/vendorsSocket")
@ApplicationScoped
public class VendorsWebSocket {

    List<Session> sessionList = new ArrayList<>();

    @Inject
    VendorsController vendors;

    // TODO: Inject the VendorsController as well
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {

        if (!sessionList.contains(session)) {
            sessionList.add(session);
        }

        String output = "";
        JsonObject json = Json.createReader(new StringReader(message)).readObject();
        if (json.containsKey("get") && json.getString("get").equals("vendors")) {

            if (json.containsKey("id")) {
                output = vendors.getById(json.getInt("id")).toJson().toString();
            } else if (json.containsKey("search")) {
                output = vendors.getBySearchJson(json.getString("search")).toString();
            } else {
                output = vendors.getAllByJSON().toString();
            }

        } else if (json.containsKey("post") && json.getString("post").equals("vendors")) {

            JsonObject productJson = json.getJsonObject("data");
            vendors.addJson(productJson);
            output = vendors.getAllByJSON().toString();

        } else if (json.containsKey("put") && json.getString("put").equals("vendors")) {

            JsonObject productJson = json.getJsonObject("data");
            int id = productJson.getInt("vendorId");

            if (vendors.getById(id) == null) {
                vendors.addJson(productJson);
            } else {
                vendors.editJson(id, productJson);
            }

            output = vendors.getAllByJSON().toString();

        } else if (json.containsKey("delete") && json.getString("delete").equals("vendors")) {

            if (json.containsKey("id")) {
                vendors.delete(json.getInt("id"));
                output = vendors.getAllByJSON().toString();
            }

        } else {

            output = Json.createObjectBuilder()
                    .add("error", "Invalid Request")
                    .add("original", json)
                    .build().toString();

        }
        // TODO: Return the output string to the user that sent the message
        RemoteEndpoint.Basic basic = session.getBasicRemote();
        basic.sendText(output);

    }
}
