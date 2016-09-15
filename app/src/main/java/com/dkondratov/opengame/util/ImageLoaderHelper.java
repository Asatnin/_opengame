package com.dkondratov.opengame.util;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by andrew on 09.04.2015.
 */
public class ImageLoaderHelper {

    private static ImageLoader imageLoader;

    public static ImageLoader imageLoader() {
        if (imageLoader == null) {
            throw new IllegalStateException("DatabaseHelper class is not set");
        }
        return imageLoader;
    }

    public static void createImageLoaderWithContext(Context ctx) {
        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(ctx));
        }
    }
}
