package com.voicerecognition;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
/**
 * Created by ghanendra on 29/07/2017.
 */

public class VolleyHelper {

    public static void getType(final Activity cont, String filepath, String filename, String username, UserProfilesId uid, UserProfilesNames unames) {
        HttpClient httpclient = new DefaultHttpClient();
        String URL = "https://westus.api.cognitive.microsoft.com/spid/v1.0/identificationProfiles";
        try {
            HttpPost request = new HttpPost(URL);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", cont.getString(R.string.apikey));
            System.out.println(" url request " + URL);
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("locale", "en-us");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Request body
            StringEntity reqEntity = new StringEntity(jsonBody.toString());
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                try {
                    JSONObject jo = new JSONObject(EntityUtils.toString(entity));
                    System.out.println("entity utils 2" + jo.getString("identificationProfileId"));
                    String id = jo.getString("identificationProfileId");
                    System.out.println("id string" + id);
                    if (!username.contains("-"))
                        uid.setidentificationProfileId(username, id);

                    if (!username.contains("-"))
                        unames.setUsername(id, username);

                    File file = new File(filepath, filename);
                    byte[] data = new byte[Integer.parseInt(String.valueOf(file.length()))];

                    cont.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(cont, "Done", Toast.LENGTH_SHORT).show();
                        }
                    });
                    doEnrollment(cont, data, jo.getString("identificationProfileId"), file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void doEnrollment(final Activity contx, byte[] data, String id, File file) {

        try {
            HttpClient httpclient = new DefaultHttpClient();
            String URL = "https://westus.api.cognitive.microsoft.com/spid/v1.0/identificationProfiles/" + id + "/enroll";
            System.out.println("URl 2" + URL);
            HttpPost request = new HttpPost(URL);
            request.setHeader("Content-Type", "multipart/form-data");
            request.setHeader("Ocp-Apim-Subscription-Key", contx.getString(R.string.apikey));
            System.out.println(" data doenroll " + file.getAbsolutePath());
            // Request body
            FileEntity reqEntity = new FileEntity(file, "wav/audio");
            request.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(request);

            System.out.println("response volleyherlper2 " + response);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                final JSONObject jo = new JSONObject(EntityUtils.toString(entity));
                System.out.println("entity utils 3 " + jo);
                if (jo.getJSONObject("error") != null)
                    contx.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(contx, "Error! " + jo.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void getProfile(String id, final Activity contx) {
        HttpClient httpclient = new DefaultHttpClient();

        try {
            String URL = "https://westus.api.cognitive.microsoft.com/spid/v1.0/identificationProfiles/" + id;
            System.out.println("url getprofile" + URL);
            HttpGet request = new HttpGet(URL);
            request.setHeader("Ocp-Apim-Subscription-Key", contx.getString(R.string.apikey));

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            System.out.println(" entity getprofile " + entity);

            if (entity != null) {
                final JSONObject jo = new JSONObject(EntityUtils.toString(entity));
                System.out.println(" json entity " + jo.toString());
                final String status = jo.getString("enrollmentStatus");
                contx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(contx, "Status of audio clip=" + status, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getIdentification(String ids, Activity contx, File file, UserProfilesNames upro) {
        HttpClient httpclient = new DefaultHttpClient();
        String res = "nil";
        try {
            String URL = "https://westus.api.cognitive.microsoft.com/spid/v1.0/identify?identificationProfileIds=" + ids;
            System.out.println("URL get identification volleyherlper" + URL);
            HttpPost request = new HttpPost(URL);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", contx.getString(R.string.apikey));

            // Request body
            FileEntity reqEntity = new FileEntity(file, "wav/audio");
            request.setEntity(reqEntity);

            System.out.println("reqEntity" + reqEntity.toString());
            HttpResponse response = httpclient.execute(request);
            Header[] headr = response.getAllHeaders();
            for (Header h : headr) {
                System.out.println(h.getName() + "response httprequest " + h.getValue());
                if (h.getName().matches("Operation-Location")) {
                    res = getIdentificationStatus(h.getValue(), contx.getString(R.string.apikey), upro);
                    System.out.println("res volleyhelper identify " + res);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;

    }

    public static String getIdentificationStatus(String operloc, String key, UserProfilesNames uprof) {
        HttpClient httpclient = new DefaultHttpClient();
        String URL = operloc;
//        String opid= operloc.substring(operloc.lastIndexOf("/"),operloc.length());
        HttpGet request = new HttpGet(URL);
        request.setHeader("Ocp-Apim-Subscription-Key", key);

        System.out.println("identification stat" + URL);
        HttpResponse response = null;
        String stat = "nil";
        try {
            response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                System.out.println();
                JSONObject jo = new JSONObject(EntityUtils.toString(entity));
                System.out.println("json status" + jo);
                if (jo.getString("status").matches("failed")) {
                    stat = "Status:" + jo.getString("status") + " Reason: " + jo.getString("message");
                } else if (jo.getString("status").matches("succeeded")) {
                    if (jo.toString().contains("enrollmentStatus")) {
                        stat = "Status:" + jo.getString("status") + " Result: " + jo.getJSONObject("processingResult").getString("enrollmentStatus");
                        System.out.println("volleyheloer enrollmentstatus"+stat);
                    } else {
                        stat = "Status:" + jo.getString("status") + " Result: " + jo.getJSONObject("processingResult").getString("identifiedProfileId") + " UserName: " + uprof.getUsername(jo.getJSONObject("processingResult").getString("identifiedProfileId")) + " Confidence: " + jo.getJSONObject("processingResult").getString("confidence");
                        System.out.println(" volley herloer no enrollment"+stat);
                    }
                } else if (jo.getString("status").matches("running")) {
                    stat = "Status:" + jo.getString("status") + "\n Action: Click This in 5 seconds. \n#" + operloc;
                } else {
                    stat = "Status: " + jo.getString("status");
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("sending stat back volleyheler"+stat);
        return stat;
    }

}
