package com.dkondratov.opengame.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dkondratov.opengame.R;
import com.dkondratov.opengame.network.UserRegisterRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dkondratov.opengame.util.ApplicationUserData;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.dialogs.VKCaptchaDialog;

import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

import static com.dkondratov.opengame.network.NetworkUtilities.networkUtilities;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserId;
import static com.dkondratov.opengame.util.ApplicationUserData.saveUserId;
import static android.text.TextUtils.isEmpty;
import static android.widget.Toast.makeText;
import static com.dkondratov.opengame.util.ApplicationUserData.saveUserName;
import static com.dkondratov.opengame.util.ApplicationUserData.saveUserSurname;

public class RegistrationFragment extends Fragment implements View.OnClickListener {

    private EditText name;
    private EditText surname;
    private RegistrationFragmentCallbacks mCallbacks;
    private LinearLayout regLayout;
    private ProgressBar progressBar;

    private View vk, fb, tw;

    private CallbackManager callbackManager = CallbackManager.Factory.create();
    private VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
            Log.v("vksdk","onCaptchaDialog");
            //Toast.makeText(ProfileActivity.this, "onCapthaDialog",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(sMyScope);
            Log.v("vksdk", "onTokenExpired");
            //Toast.makeText(ProfileActivity.this, "onTokenExpired",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            Log.v("vksdk","onAccessDenied");
            //Toast.makeText(ProfileActivity.this, "onAccessDenied",Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(getActivity())
                    .setMessage(authorizationError.errorMessage)
                    .show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            Log.v("vksdk","newToken");
            newToken.saveTokenToSharedPreferences(getActivity(), sTokenKey);
            //Toast.makeText(ProfileActivity.this, "onReceiveToken",Toast.LENGTH_SHORT).show();
            //SocialNetworksData.setDefaulthNetwork(SocialNetworksData.Network.VK, ProfileActivity.this);
            //addSocialNetwork("vk",newToken.userId);
            //vk.setAlpha(255);
            //promoVK.setVisibility(View.INVISIBLE);
            //tracker.trackEvent(AnalyticsTracker.CATEGORY_PROFILE_SCREEN, AnalyticsTracker.ACTION_VK_AUTH, newToken.userId);
            loadUserVK();
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            loadUserVK();
        }
    };

    private static String consumerKey = "jAze3iB9UxAde1Z2131J5O1ir";
    private static String consumerSecret = "lfX0iEq0o0DOIrJ6E3K7yrsXJRPWwg57fSd5JDXzBJQS6tZReu";


    public static String sTokenKey = "cATHfLFP47MTv44tTHFZ";
    private static String[] sMyScope = new String[]{VKScope.FRIENDS, VKScope.WALL, VKScope.PHOTOS, VKScope.NOHTTPS};

    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private TwitterAuthClient mTwitterAuthClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKSdk.initialize(sdkListener, "4819193", VKAccessToken.tokenFromSharedPreferences(getActivity(), sTokenKey));
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        VKUIHelper.onCreate(getActivity());
        mTwitterAuthClient = new TwitterAuthClient();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {

                ApplicationUserData.saveUserImageUrl(getActivity(), currentProfile.getProfilePictureUri(500, 500).toString());
                ApplicationUserData.saveUserName(getActivity(), currentProfile.getFirstName());
                ApplicationUserData.saveUserSurname(getActivity(), currentProfile.getLastName());

                register(currentProfile.getFirstName(), currentProfile.getLastName());
            }
        };
        setUpFb();
    }


    private void loadUserVK() {
        VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.OWNER_ID, "1,2"));
        request.addExtraParameter("fields","photo_200");
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.v("user", response.json.toString());
                VKApiUser user = new VKApiUser();
                user = user.parse(response.json.optJSONArray("response").optJSONObject(0));
                ApplicationUserData.saveUserName(getActivity(), user.first_name);
                ApplicationUserData.saveUserSurname(getActivity(), user.last_name);
                ApplicationUserData.saveUserImageUrl(getActivity(), user.photo_200);
                register(user.first_name, user.last_name);
            }
        });

        request.start();
    }

    private void setUpFb() {


        //LoginManager.getInstance().logInWithReadPermissions(ProfileActivity.this, Arrays.asList("public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        //   Toast.makeText(getApplicationContext(), "onSuccess",Toast.LENGTH_SHORT).show();
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        //  Toast.makeText(getApplicationContext(), "onCancel",Toast.LENGTH_SHORT).show();
                        // App code
                    }

                    @Override
                    public void onError(FacebookException e) {
                        //    Toast.makeText(getApplicationContext(), "onError",Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        VKUIHelper.onResume(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        VKUIHelper.onDestroy(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(getActivity(), requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(getActivity(), "on activity result", Toast.LENGTH_SHORT).show();
    }

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (RegistrationFragmentCallbacks) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_registration, container, false);

        vk = fragmentView.findViewById(R.id.vk);
        fb = fragmentView.findViewById(R.id.fb);
        tw = fragmentView.findViewById(R.id.twitter_button);
        regLayout = (LinearLayout) fragmentView.findViewById(R.id.reg_layout);
        progressBar = (ProgressBar) fragmentView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        final Button registerButton = (Button) fragmentView.findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);
        name = (EditText) fragmentView.findViewById(R.id.reg_name);
        surname = (EditText) fragmentView.findViewById(R.id.reg_surname);

        vk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKSdk.authorize(sMyScope);
            }
        });
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
            }
        });
        tw.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mTwitterAuthClient.authorize(getActivity(), new com.twitter.sdk.android.core.Callback<TwitterSession>() {

                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
                        twitterSessionResult.data.getUserName();
                        register(twitterSessionResult.data.getUserName().toString(), "");
                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });


            }
        });

        return fragmentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                if (isEmpty(name.getText()) || isEmpty(surname.getText())) {
                    makeText(getActivity(), R.string.reg_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                hideKeyboard();

                register(name.getText().toString(), surname.getText().toString());
                break;
        }
    }

    private void register(final String name, final String surname) {
        progressBar.setVisibility(View.VISIBLE);

        networkUtilities(getActivity())
                .addToRequestQueue(new UserRegisterRequest(new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        if (TextUtils.isEmpty(ApplicationUserData.loadUserId(getActivity())))
                            saveUserId(getActivity(), response);
                        saveUserName(getActivity(), name);
                        saveUserSurname(getActivity(), surname);
                        mCallbacks.onUserRegistered();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VolleyError", "Register user error!");
                        progressBar.setVisibility(View.GONE);
                        makeText(getActivity(), R.string.check_network, Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    private void hideKeyboard() {
        InputMethodManager inputManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public interface RegistrationFragmentCallbacks {
        void onUserRegistered();
    }
}
