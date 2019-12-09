package com.example.phonite;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class KappaServerRequest {
    private final String TAG = "KappaServer";
    private static final String baseURL = "https://kappa.bucky-mobile.com/";

    private RequestQueue queue;

    // Game mechanism
    public int timeLeft = -1;
    public JSONArray usersHealth;
    public String myUsername;
    private final int startingHealth = 1000;

    // TODO
    public KappaServerRequest(String myUsername) {
        this.myUsername = myUsername;
    }

    public void createPlayer() {
        HashMap<Object, Object> request = new HashMap<>();
        request.put("usernames", myUsername);
        request.put("health", startingHealth);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, baseURL + "health", new JSONObject(request), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.toString());
                    }
                });
        queue = Volley.newRequestQueue(MainActivity.getAppContext());
        queue.add(jsonObjectRequest);
    }

    public void getPlayerCount() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, baseURL + "count", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int playerCount = (int) response.get("count");
                            Log.d(TAG, "" + playerCount);
                        } catch (JSONException e) {
                            Log.d(TAG, e.getStackTrace().toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.toString());
                    }
                });
        queue = Volley.newRequestQueue(MainActivity.getAppContext());
        queue.add(jsonObjectRequest);
    }

    public void getHealth() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, baseURL + "health", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            usersHealth = (JSONArray) response.get("instances");
                            Log.d(TAG, "Health: " + usersHealth.toString());
                        } catch (JSONException e) {
                            Log.d(TAG, e.getStackTrace().toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.toString());
                    }
                });
        queue = Volley.newRequestQueue(MainActivity.getAppContext());
        queue.add(jsonObjectRequest);
    }

    public void reduceHealth(int healthToReduce, String usernameShot){
        HashMap<Object, Object> request = new HashMap<>();
        request.put("usernames", usernameShot);
        request.put("health", healthToReduce);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PATCH, baseURL + "health", new JSONObject(request), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int healthLeft = (int) response.get("health");
                            Log.d(TAG, usernameShot + " " + healthLeft);
                        } catch (JSONException e){
                            Log.d(TAG, e.getStackTrace().toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.toString());
                    }
                });
        queue = Volley.newRequestQueue(MainActivity.getAppContext());
        queue.add(jsonObjectRequest);
    }

    public void clear() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, baseURL + "clear", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.toString());
                    }
                });
        queue = Volley.newRequestQueue(MainActivity.getAppContext());
        queue.add(jsonObjectRequest);
    }

    public void startTimer(int sec) {
        HashMap<String, Integer> request = new HashMap<>();
        request.put("set", sec);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, baseURL + "start_timer", new JSONObject(request), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            if ((boolean) response.get("request_processed")) {
                                Log.d(TAG, "Timer starts");
                            } else {
                                if ((boolean) response.get("started")) {
                                    Log.d(TAG, "Timer already started");
                                } else {
                                    Log.d(TAG, "Unknown Error! Need backup!");
                                }
                            }
                        } catch (JSONException e) {
                            Log.d(TAG, e.getStackTrace().toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.toString());
                    }
                });
        queue = Volley.newRequestQueue(MainActivity.getAppContext());
        queue.add(jsonObjectRequest);
    }

    public void getTimeLeft() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, baseURL + "start_timer", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            timeLeft = (int) response.get("time");
                            Log.d(TAG, "timeLeft: " + timeLeft);
                        } catch (JSONException e) {
                            Log.d(TAG, e.getStackTrace().toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.toString());
                    }
                });
        queue = Volley.newRequestQueue(MainActivity.getAppContext());
        queue.add(jsonObjectRequest);
    }
}