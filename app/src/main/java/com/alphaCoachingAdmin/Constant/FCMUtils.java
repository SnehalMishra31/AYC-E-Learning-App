package com.alphaCoachingAdmin.Constant;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.riversun.fcm.FcmClient;
import org.riversun.fcm.model.EntityMessage;
import org.riversun.fcm.model.FcmResponse;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FCMUtils {

    public static void sendPushMessage(Context context, ArrayList<String> tokens, String subject, String subjectUrl, String pdfVideoQuizLectureName) {

        SendfeedbackJob sendfeedbackJob = new SendfeedbackJob(tokens, subject, subjectUrl, pdfVideoQuizLectureName);
        sendfeedbackJob.execute();
    }

    private static class SendfeedbackJob extends AsyncTask<String, Void, String> {
        public ArrayList<String> tokens;
        public String subject;
        public String subjectUrl;
        public String entityName;

        public SendfeedbackJob(ArrayList<String> tokn, String sub, String url, String name) {
            tokens = tokn;
            subject = sub;
            subjectUrl = url;
            entityName = name;
        }

        @Override
        protected String doInBackground(String... strings) {
            if (tokens.size() != 0) {
                for (String token : tokens
                ) {
                    FCMSendUtility client = new FCMSendUtility();
                    // You can get from firebase console.
                    // "select your project>project settings>cloud messaging"
                    client.setAPIKey("AAAAehMIr3o:APA91bGCLjcIRApQwticygjURMN-oxhVN8tdC7ZMR4Ynoei1eaczxn5QIi6CBvB-b-7GIQtA5wd-jtIyc3DfFQWQ7DeiBNCa6DlHPZ8C1uAIeiILtTUEvWSdbKJRijsSb1k7y6C0W4Dk");

                    // Data model for sending messages to specific entity(mobile devices,browser front-end apps)s
                    JSONObject object = new JSONObject();
                    JSONObject keys = new JSONObject();
                    try {
                        keys.put("subject", subject);
                        keys.put("subjectUrl", subjectUrl);
                        keys.put("entityName", entityName);

                        object.put("data", keys);

                        JSONArray array = new JSONArray();
                        array.put(token);
//                        array.put("e2etzJwoTLy1LArHDqJjYf:APA91bGvQYZgiCkbGroyEbkor2l-3AE_QxQyJV1pJWoLmvkLbWEU-NCyue0RgGuh5tW6SoRj_Fxhr8wK-Ut-68lvWdhCZP5o-CqKAZ46DKt8QRIBDR_QwwPhH9MrcPaJaJCV9SwHARjC");
//                        array.put("dbQpgZuvQuKgDiLLUDK8Rh:APA91bHmULrXetP2o6SjHbOzNZqjq7uWC_WjeRTC8xU3UbGSkciGqQOwtVeiiTjYipAwRu65mr3W152k-Uf9v5pp6AYqcccjg8mJx_uvxANqaOYG1ok04GWeLI597elmtNdExix8xcWV");
                        object.put("registration_ids", array);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // push
                    FCMResponse res = client.pushNotifyResult(object);
                    System.out.println(res);
                }

            }
            return null;
        }
    }

}
