package com.margin.mgms.model;

import com.margin.camera.models.Note;
import com.margin.camera.models.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on May 16, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoMock {

    public static final ArrayList<Photo> PHOTOS_LIST;
    /**
     * For test only.
     * <p>
     * TODO: remove later.
     */

    private static final String SEVERITY_TYPE_1 = "TYPE1";
    private static final String SEVERITY_TYPE_2 = "TYPE2";
    private static final String SEVERITY_TYPE_3 = "TYPE3";
    private static final String[] PHOTO_URLS = new String[]{
            "PhotoCapture/photo?image=ORD-6RR3118-PVG_115512&index=696784&gateway=ORD",
            "PhotoCapture/photo?image=ORD-6RR3118-PVG_115512&index=696785&gateway=ORD",
            "PhotoCapture/photo?image=ORD-6CN4853-ICN_115508&index=696772&gateway=ORD",
            "PhotoCapture/photo?image=ORD-6CN4853-ICN_115508&index=696773&gateway=ORD",
            "PhotoCapture/photo?image=ORD-6CN4853-ICN_115508&index=696774&gateway=ORD",
            "PhotoCapture/photo?image=ORD-6CN4851-ICN_115509&index=696775&gateway=ORD",
            "PhotoCapture/photo?image=ORD-6CN4851-ICN_115509&index=696776&gateway=ORD"
    };
    private static final int[][] SEVERITIES = new int[][]{
            {30, 31, 85, 34, 32, 14}, // G R G
            {14, 52, 85, 96, 26, 75}, // Y R R
            {24, 25, 63, 62, 17, 14}, // G Y G
            {47, 34, 74, 96, 12, 34}, // Y R Y
            {64, 36, 42, 74, 14, 25}, // Y R G
            {12, 14, 12, 41, 12, 96}, // G Y R
            {65, 18, 85, 96, 2, 9}    // Y R G
    };
    private static final String[] COMMENTS = new String[]{
            "Comment1",
            "Comment2",
            "Comment3",
            "Comment4",
            "Comment5",
            "Comment6",
            "Comment7",
    };

    static {
        PHOTOS_LIST = new ArrayList<>();

        for (int i = 0; i < SEVERITIES.length; ++i) {
            Note note1 = new Note();
            note1.setType(SEVERITY_TYPE_1);
            note1.setSeverity(SEVERITIES[i][0]);

            Note note2 = new Note();
            note2.setType(SEVERITY_TYPE_1);
            note2.setSeverity(SEVERITIES[i][1]);

            Note note3 = new Note();
            note3.setType(SEVERITY_TYPE_2);
            note3.setSeverity(SEVERITIES[i][2]);

            Note note4 = new Note();
            note4.setType(SEVERITY_TYPE_2);
            note4.setSeverity(SEVERITIES[i][3]);

            Note note5 = new Note();
            note5.setType(SEVERITY_TYPE_3);
            note5.setSeverity(SEVERITIES[i][4]);

            Note note6 = new Note();
            note6.setType(SEVERITY_TYPE_3);
            note6.setSeverity(SEVERITIES[i][5]);

            List<Note> noteList = new ArrayList<Note>() {{
                add(note1);
                add(note2);
                add(note3);
                add(note4);
                add(note5);
                add(note6);
            }};

            Photo photo = new Photo();
            photo.setNotes(noteList);
            photo.setUrl(PHOTO_URLS[i]);
            photo.setComment(COMMENTS[i]);
            photo.setLocationCode("ORD");
            PHOTOS_LIST.add(photo);
        }
    }
}
