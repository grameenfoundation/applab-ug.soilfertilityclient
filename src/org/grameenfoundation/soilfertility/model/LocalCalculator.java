package org.grameenfoundation.soilfertility.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.grameenfoundation.soilfertility.ui.CalculationResults;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (c) 2013 AppLab, Grameen Foundation
 * Created by: David
 * <p/>
 * This is a local implementation of the calculations involved
 * in optimizing soil fertilzers needed for crops.
 * Because the mathematical calculations/equations involved are not
 * ported to java (as libraries), this is only a test implementation.
 * The actual code is over a webservice
 */
public class LocalCalculator extends AsyncTask<Optimizer, Void, Optimizer> {

    // Network connection and read timeout (in milliseconds)
    public static final int NETWORK_TIMEOUT = 30 * 1000;
    private Optimizer details;
    private Context context;
    private ProgressDialog mDialog;
    private String url;

    public LocalCalculator(Context context, String url) {
        this.url = url;
        this.context = context;
    }

    @Override
    protected void onPostExecute(Optimizer optimizer) {
        super.onPostExecute(optimizer);    //To change body of overridden methods use File | Settings | File Templates.
        //end user progress
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = ProgressDialog.show(context, null, "optimising, please wait...", true, false);
    }

    @Override
    protected Optimizer doInBackground(Optimizer... params) {
        Optimizer details = params[0];
        try {
            Gson gson = new Gson();
            String json = gson.toJson(details);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = getHttpPost(json);

            HttpResponse esponseBody = httpclient.execute(httppost);
            String response = EntityUtils.toString(esponseBody.getEntity());
            Logger.getAnonymousLogger().log(Level.WARNING, "response follows:");
            Logger.getAnonymousLogger().log(Level.WARNING, response);
            response = extractJsonResponse(response);

            details = gson.fromJson(response, Optimizer.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            details = null;
        } catch (Exception e) {
            e.printStackTrace();
            details = null;
        }

        //pass results to the display activity
        if (details != null) {
            Intent resultsIntent = new Intent(context, CalculationResults.class);
            resultsIntent.putExtra("result", details);
            mDialog.dismiss();
            context.startActivity(resultsIntent);
        } else {
            mDialog.dismiss();
            //Toast.makeText(context, "operation failed, try again later", Toast.LENGTH_SHORT).show();
        }
        return details;
    }

    /**
     * the webservice call returns json enclosed in a XML document
     * This method extracts this json
     *
     * @param response the XML document response string from the server
     * @return json string from within the XML
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private String extractJsonResponse(String response) throws ParserConfigurationException, SAXException, IOException {
        //response is XML, parse
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new InputSource(new StringReader(response)));
        doc.getDocumentElement().normalize();

        NodeList nodes = doc.getChildNodes();
        response = nodes.item(0).getTextContent();

        Logger.getAnonymousLogger().log(Level.WARNING, "json follows:");
        Logger.getAnonymousLogger().log(Level.WARNING, response);
        return response;
    }

    /**
     * creates an HTTP post object to the URL provided and with the json data
     * passed as a parameter
     *
     * @param json the json data to pass as parameter to the server
     * @return <code>HttpPost</code> object for calling the webservice
     * @throws UnsupportedEncodingException
     */
    private HttpPost getHttpPost(String json) throws UnsupportedEncodingException {
        HttpPost httppost = new HttpPost(url);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("json", json));
        httppost.setEntity(new UrlEncodedFormEntity(pairs));
        return httppost;
    }

    @Override
    protected void onCancelled(Optimizer optimizer) {
        super.onCancelled(optimizer);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
