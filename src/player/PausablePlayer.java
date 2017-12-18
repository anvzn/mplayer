/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package player;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.FileInputStream;

/**
 *
 * @author user
 */
public class PausablePlayer {

    public final static int NOTSTARTED = 0;
    public final static int PLAYING = 1;
    public final static int PAUSED = 2;
    public final static int FINISHED = 3;

    private PlayerHandler trackFinishedHandler;

    // the player actually doing all the work
    private final Player player;

    // locking object used to communicate with player thread
    private final Object playerLock = new Object();

    private Thread thrd;

    // status variable what player thread is doing/supposed to do
    private int playerStatus = NOTSTARTED;

    public PausablePlayer(final FileInputStream inputStream, PlayerHandler handler) throws JavaLayerException {
        this.player = new Player(inputStream);
        this.trackFinishedHandler = handler;
    }

    /**
     * Starts playback (resumes if paused)
     */
    public void play() throws JavaLayerException {
        synchronized (playerLock) {
            switch (playerStatus) {
                case NOTSTARTED:
                    final Runnable r = new Runnable() {
                        public void run() {
                            playInternal();
                        }
                    };
                    thrd = new Thread(r);
                    //thrd.setDaemon(true);
                    thrd.setPriority(Thread.MAX_PRIORITY);
                    playerStatus = PLAYING;
                    thrd.start();
                    break;
                case PAUSED:
                    resume();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Pauses playback. Returns true if new state is PAUSED.
     */
    public boolean pause() {
        synchronized (playerLock) {
            if (playerStatus == PLAYING) {
                playerStatus = PAUSED;
            }
            return playerStatus == PAUSED;
        }
    }

    /**
     * Resumes playback. Returns true if the new state is PLAYING.
     */
    public boolean resume() {
        synchronized (playerLock) {
            if (playerStatus == PAUSED) {
                playerStatus = PLAYING;
                playerLock.notifyAll();
            }
            return playerStatus == PLAYING;
        }
    }

    public void switchPlaybackState() throws JavaLayerException {
        if (playerStatus == PLAYING) {
            pause();
        } else {
            play();
        }
    }

    /**
     * Stops playback. If not playing, does nothing
     */
    public void stop() {
        synchronized (playerLock) {
            playerStatus = FINISHED;
            playerLock.notifyAll();
            thrd.interrupt();
        }
    }

    private void playInternal() {
        while (playerStatus != FINISHED) {
            try {
                if (!player.play(1)) {
                    trackFinishedHandler.handle();
                    break;
                }
            } catch (final JavaLayerException e) {
                break;
            }
            // check if paused or terminated
            synchronized (playerLock) {
                while (playerStatus == PAUSED) {
                    try {
                        playerLock.wait();
                    } catch (final InterruptedException e) {
                        // terminate player
                        break;
                    }
                }
            }
        }
        close();
    }

    public int getStatus() {
        return playerStatus;
    }

    /**
     * Closes the player, regardless of current state.
     */
    public void close() {
        synchronized (playerLock) {
            playerStatus = FINISHED;
        }
        try {
            player.close();
        } catch (final Exception e) {
            // ignore, we are terminating anyway
        }
    }
}
