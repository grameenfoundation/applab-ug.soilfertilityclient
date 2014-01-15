package org.grameenfoundation.soilfertility.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.grameenfoundation.soilfertility.dataaccess.DatabaseHelper;
import org.grameenfoundation.soilfertility.ui.CalculationResults;
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
import java.sql.SQLException;
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
public class LocalCalculator extends AsyncTask<Calc, Void, Calc> {

    // Network connection and read timeout (in milliseconds)
    public static final int NETWORK_TIMEOUT = 30 * 1000;
    private Calc details;
    private Context context;
    private ProgressDialog mDialog;
    private String url;
    private DatabaseHelper databaseHelper = null;

    public LocalCalculator(Context context, String url) {
        this.url = url;
        this.context = context;
    }

    @Override
    protected void onPostExecute(Calc calc) {
        super.onPostExecute(calc);    //To change body of overridden methods use File | Settings | File Templates.
        //end user progress
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = ProgressDialog.show(context, null, "optimising, please wait...", true, false);
        mDialog.setCancelable(true);
    }

    @Override
    protected Calc doInBackground(Calc... params) {
        Calc details = params[0];
        try {
            try {
                if (checkInternetConnection()) {
                    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                    String json = gson.toJson(details);

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = getHttpPost(json);

                    HttpResponse esponseBody = httpclient.execute(httppost);
                    String response = EntityUtils.toString(esponseBody.getEntity());
                    response = extractJsonResponse(response);

                    details = gson.fromJson(response, Calc.class);
                    Message message = Message.obtain(handler);
                    message.obj = "preparing...";
                    handler.sendMessage(message);
                    if (details != null) {
                        details.setSolved(true);
                    }
                } else {
                    Message message = Message.obtain(handler);
                    message.obj = "No internet connection available";
                    handler.sendMessage(message);
                    context.startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                    //ACTION_WIFI_SETTINGS for wifi
                    throw new ValidationException("no internet connectivity");
                }
            } catch (ValidationException e) {
                details.setSolved(false);
                Log.e(getClass().getSimpleName(), "Failure", e);
            } catch (JsonSyntaxException e) {
                Message message = Message.obtain(handler);
                message.obj = "Try again later. Operation failed";
                handler.sendMessage(message);
                details.setSolved(false);
                Log.e(getClass().getSimpleName(), "Json Exception", e);
            } catch (Exception e) {
                Message message = Message.obtain(handler);
                message.obj = "Try again later. Operation failed";
                handler.sendMessage(message);
                details.setSolved(false);
                Log.e(getClass().getSimpleName(), "Exception", e);
            }

            //pass results to the display activity
            if (details.isSolved()) {
                //we change ratios from Kg/Ha to Kg/Acre
                for (CalcCropFertilizerRatio ratio : details.getCropFerts()) {
                    ratio.changeAmtToKgsPerAcre();
                }
                // TODO ensure prevention of duplicate requests creating multiple logs

                Intent resultsIntent = new Intent(context, CalculationResults.class);
                resultsIntent.putExtra("result", details);
                context.startActivity(resultsIntent);
            }
        } finally {
            //store transaction
            saveOrUpdateCalculation(details);
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
        return details;
    }

    public void saveOrUpdateCalculation(Calc calculation) {
        try {
            Calc db_calculation = getHelper().getCalculationsDataDao().queryForSameId(calculation);
            if (db_calculation == null) {
                getHelper().getCalculationsDataDao().create(calculation);
            } else {
                getHelper().getCalculationsDataDao().update(calculation);
            }
            saveReferenceForTransientObjects(details);
        } catch (SQLException e) {
            Log.e(getClass().getSimpleName(), "Database exception", e);
        }
    }

    /**
     * all child objects have a reference to their parent (Calc object)
     * This method also saves the child objects (done manually because saving the parent wasn't
     * saving the transient objects)
     *
     * @param details the calc object that holds the child/transient objects
     */
    private void saveReferenceForTransientObjects(Calc details) {
        for (CalcCrop crop : details.getCalcCrops()) {
            crop.setCalculation(details);
            try {
                CalcCrop calcCrop = getHelper().getCalcCropDataDao().queryForSameId(crop);
                if (calcCrop == null) {
                    getHelper().getCalcCropDataDao().create(crop);
                } else {
                    getHelper().getCalcCropDataDao().update(crop);
                }
            } catch (SQLException e) {
                Log.e(getClass().getSimpleName(), "Database exception", e);
            }
        }
        for (CalcFertilizer fertilizer : details.getCalcFertilizers()) {
            fertilizer.setCalculation(details);
            try {
                CalcFertilizer calcFert = getHelper().getCalcFertilizerDataDao().queryForSameId(fertilizer);
                if (calcFert == null) {
                    getHelper().getCalcFertilizerDataDao().create(fertilizer);
                } else {
                    getHelper().getCalcFertilizerDataDao().update(fertilizer);
                }
            } catch (SQLException e) {
                Log.e(getClass().getSimpleName(), "Database exception", e);
            }
        }
        for (CalcCropFertilizerRatio ratio : details.getCropFerts()) {
            ratio.setCalculation(details);
            try {
                CalcCropFertilizerRatio calcCropFert = getHelper().getCalcCropFertilizerRatioDao().queryForSameId(ratio);
                if (calcCropFert == null) {
                    getHelper().getCalcCropFertilizerRatioDao().create(ratio);
                } else {
                    getHelper().getCalcCropFertilizerRatioDao().update(ratio);
                }
            } catch (SQLException e) {
                Log.e(getClass().getSimpleName(), "Database exception", e);
            }
        }
    }

    /**
     * this property will help send messages to the dialog
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mDialog.setMessage((String) msg.obj);
        }
    };

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

    /**
     * checks wheather there is network connectivity
     *
     * @return <code>true</code> if there is connectivity <code>false</code> otherwise
     */
    public boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }

    /**
     * gets the helper from the manager
     *
     * @return a databasehelper instance
     */
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onCancelled(Calc calc) {
        super.onCancelled(calc);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
