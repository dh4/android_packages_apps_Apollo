/*
 * Copyright (C) 2012 Andrew Neal Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.dh4.apollo.loaders;

import android.content.Context;
import android.database.Cursor;

import com.dh4.apollo.R;
import com.dh4.apollo.model.Song;
import com.dh4.apollo.provider.RecentStore;
import com.dh4.apollo.provider.RecentStore.RecentStoreColumns;
import com.dh4.apollo.utils.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to query {@link RecentStore} and return the last listened to albums.
 * 
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public class RecentLoader extends WrappedAsyncTaskLoader<List<Song>> {

    /**
     * The result
     */
    private final ArrayList<Song> mSongsList = Lists.newArrayList();

    /**
     * The {@link Cursor} used to run the query.
     */
    private Cursor mCursor;

    /**
     * Constructor of <code>RecentLoader</code>
     * 
     * @param context The {@link Context} to use
     */
    public RecentLoader(final Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Song> loadInBackground() {
        // Create the Cursor
        mCursor = makeRecentCursor(getContext());
        // Gather the data
        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                // Copy the song id
                final long id = mCursor.getLong(mCursor
                        .getColumnIndexOrThrow(RecentStoreColumns.ID));

                // Copy the song name
                final String songName = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(RecentStoreColumns.SONGNAME));

                // Copy the artist name
                final String artist = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(RecentStoreColumns.ARTISTNAME));

                // Copy the album name
                final String album = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(RecentStoreColumns.ALBUMNAME));

                // Copy the song duration
                final int duration = mCursor.getInt(mCursor
                        .getColumnIndexOrThrow(RecentStoreColumns.DURATION));

                // Create a new album
                final Song song = new Song(id, songName, artist, album, duration);

                // Add everything up
                mSongsList.add(song);
            } while (mCursor.moveToNext());
        }
        // Close the cursor
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mSongsList;
    }

    /**
     * Creates the {@link Cursor} used to run the query.
     * 
     * @param context The {@link Context} to use.
     * @return The {@link Cursor} used to run the album query.
     */
    public static final Cursor makeRecentCursor(final Context context) {
        return RecentStore
                .getInstance(context)
                .getReadableDatabase()
                .query(RecentStoreColumns.NAME,
                        new String[] {
                                RecentStoreColumns.ID + " as _id", RecentStoreColumns.ID,
                                RecentStoreColumns.SONGNAME, RecentStoreColumns.ARTISTNAME,
                                RecentStoreColumns.ALBUMNAME, RecentStoreColumns.DURATION,
                                RecentStoreColumns.TIMEPLAYED
                        }, null, null, null, null, RecentStoreColumns.TIMEPLAYED + " DESC");
    }
}
