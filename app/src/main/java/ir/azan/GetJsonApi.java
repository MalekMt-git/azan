package ir.azan;

import android.os.AsyncTask;
import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetJsonApi extends AsyncTask<String, Integer, String> {
    public AsyncResponse delegate = null;
    OkHttpClient client = new OkHttpClient();
    public GetJsonApi(AsyncResponse asyncResponse) {
        delegate = asyncResponse;//Assigning call back interface through constructor
    }
    @Override
        protected String doInBackground(String...params) {
            MediaType mediaType = null;
            Request.Builder builder = new Request.Builder();
            builder.url(params[0]);
        Log.e("GetJsonApi: ", params.toString());
            if (params.length>1) { // for the request that has json input, such as "like a post"
                if (!params[1].equals("GET")) {
                    mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(mediaType, params[1]);
                    builder.method("POST", body); // params[1] is the body in the string format
                }else{
                    builder.method("GET", null);
                }
            }
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            delegate.processFinish(s);
        }
}


