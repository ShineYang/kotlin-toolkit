package org.readium.r2.navigator.media

import android.support.v4.media.session.MediaSessionCompat
import org.readium.r2.shared.publication.Publication

class SimpleMediaPlayer(
    context: MediaService,
    mediaSession: MediaSessionCompat,
    media: PendingMedia,
) : MediaPlayer {

    override var playbackRate: Double
        get() = 1.0
        set(speed) {

        }

    override var listener: MediaPlayer.Listener? = null

    override fun onDestroy() {
        TODO("Not yet implemented")
    }

}