package com.bwfcwalshy.flarebot.music.extractors;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.bwfcwalshy.flarebot.MessageUtils;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class YouTubeExtractor implements Extractor {
    public static final String YOUTUBE_URL = "https://www.youtube.com";
    public static final String PLAYLIST_URL = "https://www.youtube.com/playlist?list=";
    public static final String WATCH_URL = "https://www.youtube.com/watch?v=";

    @Override
    public Class<? extends AudioSourceManager> getSourceManagerClass() {
        return YoutubeAudioSourceManager.class;
    }

    @Override
    public void process(String input, Player player, IMessage message, IUser user) throws Exception {
        AudioItem item = player.resolve(input);
        List<AudioTrack> tracks = new ArrayList<>();
        String name;
        if (item instanceof AudioPlaylist) {
            AudioPlaylist audioPlaylist = (AudioPlaylist) item;
            tracks.addAll(audioPlaylist.getTracks());
            name = audioPlaylist.getName();
        } else {
            AudioTrack track = (AudioTrack) item;
            tracks.add(track);
            name = track.getInfo().title;
        }
        if(name != null) {
            for (AudioTrack t : tracks) {
                player.queue(t);
            }
            EmbedBuilder builder = MessageUtils.getEmbed(user);
            builder.withDesc(String.format("%s added the playlist [`%s`](%s)", user, name, input));
            builder.appendField("Song count:", String.valueOf(tracks.size()), true);
            MessageUtils.editMessage("", builder.build(), message);
        }
    }

    @Override
    public boolean valid(String input) {
        try {
            URL url = new URL(input);
            Document doc = Jsoup.connect(input).get();
            return doc.title().endsWith("YouTube") && !doc.title().equals("YouTube");
        } catch (IOException e) {
            return false;
        }
    }
}
