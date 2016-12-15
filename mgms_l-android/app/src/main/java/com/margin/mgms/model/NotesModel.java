package com.margin.mgms.model;

import com.margin.camera.models.Note;

import java.util.ArrayList;

/**
 * Created on May 12, 2016.
 *
 * @author Marta.Ginosyan
 */
public class NotesModel {

    public static final ArrayList<Note> NOTES_LIST;
    /**
     * For testing purposes.
     * <p>
     * TODO: remove later
     */

    private static final String[] TYPES = new String[]{"Holes", "Water Damage", "Torn", "Cut",
            "Opened", "Ripped", "Holes", "Water Damage"};
    private static final int[] SEVERITY = new int[]{13, 4, 98, 52, 75, 32, 34, 99};

    static {
        NOTES_LIST = new ArrayList<>();

        Note note;
        for (int i = 0; i < TYPES.length; ++i) {
            note = new Note();
            note.setType(TYPES[i]);
            note.setSeverity(SEVERITY[i]);
            NOTES_LIST.add(note);
        }
    }
}
