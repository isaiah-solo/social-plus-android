package me.zayz.socialplus.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * Created by zayz on 12/4/17.
 *
 * Utility class for requests.
 */
public class RequestUtil {

    /**
     * Helper function for getUser requests on Volley.
     *
     * @param uri      Uri to make the request to
     * @param response Response callback
     * @param error    Error callback
     */
    public static void get(RequestQueue queue, String uri, Response.Listener<String> response,
                     Response.ErrorListener error) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response,
                error);

        queue.add(stringRequest);
    }

    /**
     * Helper function for post requests on Volley.
     *
     * @param uri      Uri to make the request to
     * @param formData Form data parameters to post
     * @param response Response callback
     * @param error    Error callback
     */
    public static void post(RequestQueue queue, String uri, final Map<String, String> formData,
                      Response.Listener<String> response, Response.ErrorListener error) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, response,
                error) {

            @Override
            protected Map<String, String> getParams() {

                return formData;
            }
        };

        queue.add(stringRequest);
    }
}
