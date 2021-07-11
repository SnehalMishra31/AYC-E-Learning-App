package com.alphaCoachingAdmin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.github.barteksc.pdfviewer.PDFView;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PDFviewer extends AppCompatActivity {

    PDFView pdfView;
    String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);

        pdfView=findViewById(R.id.pdfView);
        Intent intent=getIntent();
         link=intent.getStringExtra("url");
        Log.d("PDDF", "url:"+link);
        if (isConnected()) {
            Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();



            AlertDialog.Builder builder = new AlertDialog.Builder(PDFviewer.this);

            builder.setTitle("NoInternet Connection Alert")

                    .setMessage("GO to Setting ?")

                    .setCancelable(false)

                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override

                        public void onClick(DialogInterface dialog, int which) {

                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));

                        }

                    })

                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override

                        public void onClick(DialogInterface dialog, int which) {

                            Toast.makeText(PDFviewer.this,"Go Back TO HomePage!",Toast.LENGTH_SHORT).show();

                        }

                    });

            //Creating dialog box

            AlertDialog dialog  = builder.create();

            dialog.show();

        }
       new RetrievePDFStream().execute(link);
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }





    class RetrievePDFStream extends AsyncTask<String, Void, InputStream> {



        ProgressDialog progressDialog;

        protected void onPreExecute()

        {

            Log.d("PDDF", "pre exec:"+link );

            progressDialog = new ProgressDialog(PDFviewer.this);

            progressDialog.setTitle("getting the book content...");

            progressDialog.setMessage("Please wait...");

            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.show();



        }

        @Override

        protected InputStream doInBackground(String... strings) {

            InputStream inputStream = null;



            try {



                URL urlx = new URL(strings[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) urlx.openConnection();

                if (urlConnection.getResponseCode() == 200) {

                    inputStream = new BufferedInputStream(urlConnection.getInputStream());



                }

            } catch (IOException e) {

                return null;

            }

            return inputStream;



        }



        @Override

        protected void onPostExecute(InputStream inputStream) {

            pdfView.fromStream(inputStream).load();

            progressDialog.dismiss();

        }

    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)//means home default hai kya yesok
        {
            onBackPressed();
            return true;
        }
        return false;
    }
}


