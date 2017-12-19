/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author user
 */
public class Playlist {

    private List<Track> tracks;
    private int current;
    private boolean shuffle;
    private boolean repeatTrack;
    private boolean repeatAll;

    public Playlist(String path) {
        tracks = new ArrayList<>();
        File root = new File(path);
        walkFolders(root);
        current = 0;
        shuffle = false;
        repeatAll = false;
        repeatTrack = false;
    }

    private void walkFolders(File root) {
        for (File f : root.listFiles()) {
            if (f.isDirectory()) {
                walkFolders(f);
            } else {
                if (f.getName().endsWith(".mp3")) {
                    tracks.add(new Track(f.getAbsolutePath()));
                }
            }
        }
    }

    public boolean next() {
        if (repeatTrack) {
            return true;
        }
        if (shuffle) {
            int rand = current;
            while (rand == current) {
                rand = ThreadLocalRandom.current().nextInt(0, tracks.size());
            }
            current = rand;
            return true;
        }

        if (current < tracks.size() - 1) {
            current++;
            return true;
        } else if (repeatAll) {
            current = 0;
            return true;
        }
        return false;
    }

    public boolean prev() {
        if (current > 0) {
            current--;
            return true;
        }
        return false;
    }

    public boolean setCurrent(int i) {
        if (i > 0 && i < tracks.size()) {
            current = i;
            return true;
        }
        return false;
    }

    public int getCurrent() {
        return current;
    }

    public Track getCurrentTrack() {
        return tracks.get(current);
    }

    public List<Track> getAll() {
        return tracks;
    }

    public void switchShuffle() {
        shuffle = !shuffle;
    }

    public void switchRepeatTrack() {
        repeatTrack = !repeatTrack;
    }

    public void switchRepeatAll() {
        repeatAll = !repeatAll;
    }
}
