package inozemtsev.coursework.easystat.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

public class NetWorkUtils {
    private static final String EASY_STAT_API_BASE_URL = "https://easy-stat.herokuapp.com";
    private static final String SIGN_UP_NEW_USER = "/api/auth/signup";
    private static final String SIGN_IN_USER = "/api/auth/login";
    private static final String GET_ALL_QUESTIONNAIRES_WITH_ACCESS = "/api/questionnaire/all";
    private static final String GET_QUESTIONS_FOR_QUESTIONNAIRE = "/api/questionnaire/questions/";
    private static final String GET_ALL_QUESTIONNAIRES_FOR_USER = "/api/questionnaire/user/";
    private static final String GET_QUESTIONNAIRE_BY_ID = "/api/questionnaire/";
    private static final String GET_QUESTIONNAIRES_BY_NAME = "/api/questionnaire/";
    private static final String GET_QUESTIONNAIRES_STAT_FOR_USER = "/api/questionnaire/stat/";
    private static final String GET_QUESTION_STAT = "/api/questionnaire/stat/question/";
    private static final String CREATE_NEW_QUESTIONNAIRE = "/api/questionnaire/new";
    private static final String CREATE_NEW_QUESTIONS = "/api/questionnaire/new/questions";
    private static final String SEND_ANSWER = "/api/questionnaire";
    private static final String ACCESS_PARAM_NAME = "access";
    private static final String NAME_PARAM_NAME = "NAME";
    private static final String PUBLIC_ACCESS = "PUBLIC";
    private static final String PRIVATE_ACCESS = "PRIVATE";


    public static URL GenerateURL(EasyStatRequestType type, String param){

        Uri builtUri;

        switch(type){
            case SIGN_IN:{
                builtUri = Uri.parse(EASY_STAT_API_BASE_URL + SIGN_IN_USER)
                        .buildUpon()
                        .build();
                break;
            }
            case SIGN_UP:{
                builtUri = Uri.parse(EASY_STAT_API_BASE_URL + SIGN_UP_NEW_USER)
                        .buildUpon()
                        .build();
                break;
            }
            case GET_ALL_QUESTIONNAIRES_WITH_ACCESS:{
                String access;
                if(param == null){
                    access = PUBLIC_ACCESS;
                }else{
                    access = param;
                }

                builtUri = Uri.parse(EASY_STAT_API_BASE_URL + GET_ALL_QUESTIONNAIRES_WITH_ACCESS)
                        .buildUpon()
                        .appendQueryParameter(ACCESS_PARAM_NAME, access)
                        .build();
                break;
            }
            case GET_QUESTIONS_FOR_QUESTIONNAIRE:{
                if(param == null){
                    return null;
                }

                builtUri = Uri.parse(EASY_STAT_API_BASE_URL + GET_QUESTIONS_FOR_QUESTIONNAIRE + param)
                        .buildUpon()
                        .build();
                break;
            }
            case SEND_ANSWER:{
                builtUri = Uri.parse(EASY_STAT_API_BASE_URL + SEND_ANSWER)
                        .buildUpon()
                        .build();
                break;
            }
            case FIND_QUESTIONNAIRE_WITH_USER_ID:{
                if(param == null){
                    return null;
                }

                builtUri = Uri.parse(EASY_STAT_API_BASE_URL + GET_ALL_QUESTIONNAIRES_FOR_USER + param)
                        .buildUpon()
                        .build();
                break;
            }
            case FIND_QUESTIONNAIRE_BY_ID:{
                if(param == null){
                    return null;
                }

                builtUri = Uri.parse(EASY_STAT_API_BASE_URL + GET_QUESTIONNAIRE_BY_ID + param)
                        .buildUpon()
                        .build();
                break;
            }
            case FIND_QUESTIONNAIRES_WITH_NAME:{
                if(param == null){
                    return null;
                }

                builtUri = Uri.parse(EASY_STAT_API_BASE_URL + GET_QUESTIONNAIRES_BY_NAME)
                        .buildUpon()
                        .appendQueryParameter(NAME_PARAM_NAME, param)
                        .build();
                break;
            }
            case GET_STAT_FOR_USER_QUESTIONNAIRES:{
                if(param == null){
                    return null;
                }

                builtUri = Uri.parse(EASY_STAT_API_BASE_URL + GET_QUESTIONNAIRES_STAT_FOR_USER + param)
                        .buildUpon()
                        .build();
                break;
            }
            case GET_STAT_FOR_QUESTION:{
                if(param == null){
                    return null;
                }

                builtUri = Uri.parse(EASY_STAT_API_BASE_URL + GET_QUESTION_STAT + param)
                        .buildUpon()
                        .build();
                break;
            }
            case CREATE_NEW_QUESTIONNAIRE:{
                builtUri = Uri.parse(EASY_STAT_API_BASE_URL + CREATE_NEW_QUESTIONNAIRE)
                        .buildUpon()
                        .build();
                break;
            }
            case CREATE_NEW_QUESTIONS:{
                builtUri = Uri.parse(EASY_STAT_API_BASE_URL + CREATE_NEW_QUESTIONS)
                        .buildUpon()
                        .build();
                break;
            }
            default:{
                builtUri = null;
                break;
            }

        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    public static String SendGetRequest(URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream istream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(istream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (UnknownHostException exp){
            return null;
        }finally {
            urlConnection.disconnect();
        }
    }

    public static String SendPostRequest(URL url,
                                         String requestBody,
                                         String token) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);

        if(token != null){
            urlConnection.addRequestProperty("authorization", token);
        }
        urlConnection.addRequestProperty("Content-Type", "application/json");

        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(5000);

        urlConnection.connect();

        try {
            OutputStream ostream = urlConnection.getOutputStream();
            ostream.write(requestBody.getBytes());

            if(HttpURLConnection.HTTP_OK == urlConnection.getResponseCode()) {
                InputStream istream = urlConnection.getInputStream();

                Scanner scanner = new Scanner(istream);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();

                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            }else{
                return null;
            }
        } catch (UnknownHostException exp){
            return null;
        }finally {
            urlConnection.disconnect();
        }
    }

}
