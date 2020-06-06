package com.example.aaa;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private ImageDAO imageDAO;
    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        imageDAO = db.getImageDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Image image = new Image("testImage", "holder", "xxxxxx");
        List<MarkerEntity> markers = new ArrayList<>();
        MarkerEntity marker = new MarkerEntity(12, 2, "xx");
        MarkerEntity marker2 = new MarkerEntity(13, 23, "xxx");
        markers.add(marker);
        markers.add(marker2);
        imageDAO.insertImageWithMarkerList(image,markers);
        List<Image> result = imageDAO.getAllImagesWithMarker();
        Log.d("testDebug", result.toString());
        Log.d("testDebug", result.get(0).getMarkerList().toString());
        List<Image> resultImage = imageDAO.getImageWithShareLink("xxxxxx");
        Log.d("testDebug", "useAppContext: " + resultImage);
        Log.d("testDebug", "useAppContext: " + result.get(0).getShareLink());
        assertEquals(result.get(0).getImageName(), "testImage");
    }
}
