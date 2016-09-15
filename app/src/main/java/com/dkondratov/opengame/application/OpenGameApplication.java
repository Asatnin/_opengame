package com.dkondratov.opengame.application;

import com.crashlytics.android.Crashlytics;
import com.dkondratov.opengame.activities.SplashScreenActivity;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;
import android.app.Application;
import com.dkondratov.opengame.database.HelperFactory;
import com.dkondratov.opengame.util.ImageLoaderHelper;
import com.parse.twitter.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

public class OpenGameApplication extends Application {
    private static String consumerKey = "jAze3iB9UxAde1Z2131J5O1ir";
    private static String consumerSecret = "lfX0iEq0o0DOIrJ6E3K7yrsXJRPWwg57fSd5JDXzBJQS6tZReu";

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig =
                new TwitterAuthConfig(consumerKey,
                        consumerSecret);
        Fabric.with(this, new TwitterCore(authConfig), new Crashlytics());
        //Fabric.with(this, new Crashlytics());
        HelperFactory.setHelper(getApplicationContext());
        ImageLoaderHelper.createImageLoaderWithContext(getApplicationContext());

        //parse init
       /* String APP_KEY = "DogvDU2KlLdQRtXFgAFIO6f6omZC6LeugbapIoLl";
        String CLIENT_KEY = "a4tT42P327gbApgkjoMDJNYJXpYPFL1QOS0RNRO3";
        Parse.initialize(this, APP_KEY, CLIENT_KEY);
        PushService.setDefaultPushCallback(this, SplashScreenActivity.class);
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
*/
        Parse.initialize(this, "DogvDU2KlLdQRtXFgAFIO6f6omZC6LeugbapIoLl", "a4tT42P327gbApgkjoMDJNYJXpYPFL1QOS0RNRO3");
        ParseInstallation.getCurrentInstallation().saveInBackground();


    }

    @Override
    public void onTerminate() {
        HelperFactory.releaseHelper();
        super.onTerminate();
    }
}
