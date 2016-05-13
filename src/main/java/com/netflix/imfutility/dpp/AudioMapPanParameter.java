package com.netflix.imfutility.dpp;

import com.netflix.imfutility.dpp.audiomap.AudioMap;
import com.netflix.imfutility.dpp.audiomap.EBUTrackType;
import com.netflix.imfutility.dpp.metadata.AudioTrackLayoutDmAs11Type;

import java.util.*;

/**
 * Created by Alexandr on 5/13/2016.
 * <p>
 * Class to generate "pan" filter parameter to map audio channels.
 */
public class AudioMapPanParameter {

    /**
     * Return string pan parameter like:
     *
     * @param layout          passed in metadata.xml audio layout.
     * @param audioMap        loaded from audiomap.xml object
     * @param sequencedTracks CPL virtual trackId->channelCount map with order as they are sequenced by IMF tool
     * @return pan parameter like "4c|c0=c0|c1=c1|c2=0*c0|c3=0*c0"
     */
    public static String getPanParameter(AudioTrackLayoutDmAs11Type layout, AudioMap audioMap, LinkedHashMap<String, Integer> sequencedTracks) {
        //How many channels we need to map
        Integer channelCountToMap = getChannelCountToMap(layout, audioMap);

        //Build reverse map of mapped channels.
        Map<Integer, EBUTrackType> mappedChannels = new HashMap<Integer, EBUTrackType>();
        for (Iterator<EBUTrackType> ebuTrack = audioMap.getEBUTrack().iterator(); ebuTrack.hasNext(); ) {
            EBUTrackType ebuTrackItem = ebuTrack.next();
            mappedChannels.put(ebuTrackItem.getNumber(), ebuTrackItem);
        }

        //We need build the following ffmpeg parameters
        // ffmpeg -i test_output.wav -i test_output2.wav -filter_complex "[0:a][1:a]amerge,pan=4c|c0=c0|c1=c1|c2=0*c0|c3=0*c0[aout]" -map "[aout]" -acodec pcm_s24le -ar 48000 output_map.wav
        //"[0:a][1:a]" should be built with:
        //      <dynamicParameter name="amergeMap" concat="true">[%{seq.num}:a]</dynamicParameter>
        //"pan" part ("4c|c0=c0|c1=c1|c2=0*c0|c3=0*c0") should be build with this logic

        //Get order of merged virtual track channels before pan filter
        List<String> sequencedTrackChannelNumbers = new ArrayList<String>();
        sequencedTracks.forEach((String trackId, Integer trackChannelCount) -> {
            for (Integer i = 0; i < trackChannelCount; i++) {
                sequencedTrackChannelNumbers.add(getIntermediateKey(trackId, i));
            }
        });

        // Create pan parameter
        StringBuilder panParameter = new StringBuilder();
        panParameter.append(channelCountToMap).append("c");
        for (Integer i = 0; i < channelCountToMap; i++) {
            panParameter.append("|c").append(i).append("=");

            //get sequenced channel number
            EBUTrackType ebuTrack = mappedChannels.get(i + 1);
            if (ebuTrack == null
                    || ebuTrack.getCPLVirtualTrackId() == null
                    || ebuTrack.getCPLVirtualTrackChannel() == null) {
                panParameter.append("0*c0");
                continue;
            }

            int sequencedChannel = sequencedTrackChannelNumbers.indexOf(
                    getIntermediateKey(ebuTrack.getCPLVirtualTrackId(), ebuTrack.getCPLVirtualTrackChannel() - 1));
            if (sequencedChannel == -1) {
                throw new RuntimeException(
                        String.format(
                                "Audio Virtual TrackId \"%s\" with channel number \"%i\" was not found in CPL.",
                                ebuTrack.getCPLVirtualTrackId(), ebuTrack.getCPLVirtualTrackChannel()));
            }

            panParameter.append("c").append(sequencedChannel);
        }

        return panParameter.toString();
    }

    private static String getIntermediateKey(String trackId, Integer channelNumber) {
        return trackId + ":" + channelNumber.toString();
    }

    private static Integer getChannelCountToMap(AudioTrackLayoutDmAs11Type layout, AudioMap audioMap) {
        //Count mapped audio channels
        Integer mappedChannelCount = audioMap.getEBUTrack().size();

        //How many channels we need to map
        Integer channelCountToMap = 0;
        switch (layout) {
            case EBU_R_48_2_A:
            case EBU_R_48_4_B:
            case EBU_R_48_4_C:
                if (mappedChannelCount > 4) {
                    throw new RuntimeException(
                            String.format(
                                    "metadata.xml defined audio layout as \"%s\" that has 4 tracks. Mapped channel count is greater than 4.",
                                    layout.value()));
                }
                channelCountToMap = 4;
                break;
            case EBU_R_123_16_C:
            case EBU_R_123_16_D:
            case EBU_R_123_16_F:
                if (mappedChannelCount > 16) {
                    // Never must be here since we have validation at XSD level
                    throw new RuntimeException(
                            String.format(
                                    "metadata.xml defined audio layout as \"%s\" that has 16 tracks. Mapped channel count is greater than 16.",
                                    layout.value()));
                }
                channelCountToMap = 16;
                break;
            default:
                // Unknown layout
                throw new RuntimeException(
                        String.format("metadata.xml defined unknown audio layout as \"%s\".", layout.value()));
        }
        return channelCountToMap;
    }
}
