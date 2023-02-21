/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.lifecompanion.model.impl.voicesynthesizer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import okhttp3.*;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.voicesynthesizer.VoiceInfoI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.util.LangUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Class use to create text to speech with SAPI voices.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SAPIVoiceSynthesizer extends AbstractVoiceSynthesizer {
    private final Logger LOGGER = LoggerFactory.getLogger(SAPIVoiceSynthesizer.class);

    private static final MediaType MEDIA_TYPE = MediaType.get("application/text; charset=utf-8");
    private static final int PORT = 8646;
    private static final String URL = "http://localhost:" + PORT + "/";

    /**
     * C# application process
     */
    private Process synthesizerProcess;

    private OkHttpClient httpClient;
    private static final Gson GSON = new GsonBuilder().create();

    /**
     * Path to executable C# application
     */
    private final File voiceSynthesizerAppPath = new File(LCConstant.SAPI_VOICE_SYNTHESIZER2_EXE);

    /**
     * Cached configuration (ignored pitch)
     */
    private int volume = 100, rate = 0;

    /**
     * Selected voice id
     */
    private String voice;

    /**
     * Current media player (if a wav file is playing)
     */
    private MediaPlayer currentMediaPlayer;

    // Class part : "Initialization/dispose"
    //========================================================================

    /**
     * Kill running process and run the one again.
     *
     * @throws Exception if init doesn't work
     */
    private void initProcess() throws Exception {
        if (!this.voiceSynthesizerAppPath.exists()) {
            throw new FileNotFoundException("Executable SAPI application executable not found");
        }
        //Kill previous : is it necessary ?
        try {
            this.killProcess();
        } catch (Exception e) {
            this.LOGGER.warn("Can't dispose the previous synthesizer process", e);
        }
        //Init variable
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(0, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();

        // Dev debug : kill/close on launch
        if (LangUtils.safeParseBoolean(System.getProperty("org.lifecompanion.debug.kill.sapi.on.launch"))) {
            try {
                disposeRequest(httpClient.newBuilder().connectTimeout(2, TimeUnit.SECONDS).build());
            } catch (Exception e) {
                // Ignored
            }
        }

        this.voice = null;
        this.volume = 100;
        this.rate = 0;
        //Create process and in/out
        File voiceSynthesizerErrLog = new File(System.getProperty("java.io.tmpdir") + "/LifeCompanion/logs/sapi5-windows-synthesizer/" + System.currentTimeMillis() + "/err.txt");
        IOUtils.createParentDirectoryIfNeeded(voiceSynthesizerErrLog);
        this.synthesizerProcess = new ProcessBuilder()
                .command(this.voiceSynthesizerAppPath.getAbsolutePath(), "" + PORT)
                .redirectError(voiceSynthesizerErrLog)
                .start();
        // Wait for init (two line in output process)
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.synthesizerProcess.getInputStream()));
        final String l1 = reader.readLine();
        LOGGER.info("First line from SAPI after init : {}", l1);
        final String l2 = reader.readLine();
        LOGGER.info("Second line from SAPI after init : {}", l2);
        LOGGER.info("SAPI synthesizer process initialized (exe {} on port {})", this.voiceSynthesizerAppPath, PORT);
    }

    /**
     * Kill the currently running process
     *
     * @throws Exception if kill doesn't work
     */
    private void killProcess() throws Exception {
        //Kill previous process
        if (this.synthesizerProcess != null) {
            this.httpClient = null;
            this.synthesizerProcess.destroy();
            this.synthesizerProcess = null;
        }
    }
    //========================================================================


    // IMPLEMENTATION
    //========================================================================
    @Override
    public String getName() {
        return Translation.getText("sapi.voice.2.name");
    }

    @Override
    public String getDescription() {
        return Translation.getText("sapi.voice.2.description");
    }

    @Override
    public void initialize() throws Exception {
        this.initProcess();
        //Read voices
        voices.clear();
        Request request = new Request.Builder().url(URL + "get-voices").get().build();
        try (Response response = httpClient.newCall(request).execute()) {
            VoiceInfoDto[] voicesResponse = GSON.fromJson(response.body().string(), VoiceInfoDto[].class);
            for (VoiceInfoDto voiceInfoDto : voicesResponse) {
                voices.add(new VoiceInfo(voiceInfoDto.name, voiceInfoDto.name, Locale.forLanguageTag(voiceInfoDto.language), voiceInfoDto.gender));
            }
        }
        this.LOGGER.info("SAPI voice synthesizer 2 initialized");
    }

    @Override
    public void dispose() throws Exception {
        disposeRequest(httpClient);
        this.killProcess();
        this.LOGGER.info("SAPI voice synthesizer 2 disposed");
    }

    private void disposeRequest(OkHttpClient client) throws Exception {
        Request request = new Request.Builder().url(URL + "dispose").get().build();
        try (Response ignored = client.newCall(request).execute()) {
        }
    }

    @Override
    public void speak(final String text, boolean trimSilences) {
        RequestBody body = RequestBody.create(text, MEDIA_TYPE);
        Request request = new Request.Builder().url(//
                        HttpUrl.parse(URL + "speak").newBuilder()//
                                .addQueryParameter("volume", "" + volume)//
                                .addQueryParameter("rate", "" + rate)//
                                .addQueryParameter("voice", "" + voice)//
                                .addQueryParameter("wav", trimSilences ? "true" : null)//
                                .build())
                .post(body).build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (trimSilences) playResultingFileSyncRemovingSilence(response);
        } catch (Exception e) {
            LOGGER.error("Couldn't speak with SAPI voice", e);
        }
    }


    @Override
    public void speakSsml(final String ssml, boolean trimSilences) {
        RequestBody body = RequestBody.create(ssml, MEDIA_TYPE);
        Request request = new Request.Builder().url(//
                        HttpUrl.parse(URL + "speak-ssml").newBuilder()//
                                .addQueryParameter("volume", "" + volume)//
                                .addQueryParameter("rate", "" + rate)//
                                .addQueryParameter("voice", "" + voice)//
                                .addQueryParameter("wav", trimSilences ? "true" : null)//
                                .build())
                .post(body).build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (trimSilences) playResultingFileSyncRemovingSilence(response);
        } catch (Exception e) {
            LOGGER.error("Couldn't speak ssml with SAPI voice", e);
        }
    }

    @Override
    public void stopCurrentSpeak() {
        if (this.synthesizerProcess != null) {
            Request request = new Request.Builder().url(URL + "stop").get().build();
            try (Response ignored = httpClient.newCall(request).execute()) {
            } catch (IOException e) {
                LOGGER.error("stopCurrentSpeak call didn't work", e);
            }
        }
        if (this.currentMediaPlayer != null) {
            MediaPlayer oldMediaPlayer = this.currentMediaPlayer;
            oldMediaPlayer.stop();
            oldMediaPlayer.dispose();
            this.currentMediaPlayer = null;
        }
    }

    @Override
    public void setVoice(final VoiceInfoI voiceInfo) {
        this.voice = voiceInfo.getId();
    }

    @Override
    public void setVolume(final int volumeP) {
        this.volume = volumeP;
    }

    @Override
    public void setRate(final int rateP) {
        this.rate = rateP;
    }

    @Override
    public void setPitch(final int pitchP) {
        //Can't set the pitch on SAPI voices
    }

    @Override
    public String getId() {
        return "sapi5-windows-synthesizer";
    }

    @Override
    public List<SystemType> getCompatibleSystems() {
        return Collections.singletonList(SystemType.WINDOWS);
    }

    @Override
    public boolean isInitialized() {
        return this.synthesizerProcess != null && this.synthesizerProcess.isAlive();
    }
    //========================================================================

    private static class VoiceInfoDto {
        String name;
        String language;
        String gender;
    }

    // PLAYING WAV FILES
    //========================================================================
    private void playResultingFileSyncRemovingSilence(Response response) throws Exception {
        if (response.isSuccessful()) {
            String wavFilePath = response.body().string();
            if (StringUtils.isNotBlank(wavFilePath)) {
                File wavFile = new File(wavFilePath);
                if (wavFile.exists()) {
                    playWavFileSync(trimSilences(wavFile));
                } else {
                    throw new IOException("Given wav file \"" + wavFilePath + "\" doesn't exist");
                }
            } else {
                throw new IOException("Didn't get any valid wav file from SAPI server");
            }
        }
    }

    private void playWavFileSync(File wavFile) throws Exception {
        this.currentMediaPlayer = new MediaPlayer(new Media(wavFile.toURI().toURL().toString()));
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        //Unlock on end/error
        final Runnable releaseCDL = countDownLatch::countDown;
        currentMediaPlayer.setOnEndOfMedia(releaseCDL);
        currentMediaPlayer.setOnStopped(releaseCDL);
        currentMediaPlayer.setOnHalted(releaseCDL);
        currentMediaPlayer.setOnError(() -> {
            this.LOGGER.error("Error for sound", currentMediaPlayer.getError());
            releaseCDL.run();
        });
        //Start and lock
        currentMediaPlayer.play();
        try {
            countDownLatch.await();
        } catch (Exception e) {
            this.LOGGER.error("Can't wait for player to finish", e);
        } finally {
            currentMediaPlayer = null;
        }
    }

    public File trimSilences(File wavFile) throws IOException {
        long start = System.currentTimeMillis();
        int WAV_FILEFORMAT_DATA_START = 44;
        File modifiedWavFile = org.lifecompanion.util.IOUtils.getTempFile("silence-trimmed-wav", ".wav");
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(wavFile))) {
            byte[] data = bufferedInputStream.readAllBytes();
            byte[] wavFFData = Arrays.copyOfRange(data, 0, WAV_FILEFORMAT_DATA_START);
            short[] dataAsShort = new short[(data.length - wavFFData.length) / 2];
            short maxVal = 0;
            // Find max to compute threshold (and convert values)
            for (int i = WAV_FILEFORMAT_DATA_START; i < data.length; i += 2) {
                short val = (short) (((data[i + 1] & 0xff) << 8) + (data[i] & 0xff));
                dataAsShort[(i - WAV_FILEFORMAT_DATA_START) / 2] = val;
                maxVal = (short) Math.max(Math.abs(val), maxVal);
            }
            short threshold = (short) (0.1 * maxVal);
            // Find start index
            int startI = 0, endI = 0;
            for (int i = 0; i < dataAsShort.length; i++) {
                if (Math.abs(dataAsShort[i]) > threshold) {
                    startI = i;
                    break;
                }
            }
            // Find end index
            for (int i = dataAsShort.length - 1; i >= 0; i--) {
                if (Math.abs(dataAsShort[i]) > threshold) {
                    endI = i;
                    break;
                }
            }
            // Write output wav file
            try (DataOutputStream bos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(modifiedWavFile)))) {
                bos.write(wavFFData);
                for (int i = startI; i < endI; i++) {
                    bos.writeShort(Short.reverseBytes(dataAsShort[i]));
                }
            }
        }
        LOGGER.debug("Took {} ms to remove silences from file", System.currentTimeMillis() - start);
        return modifiedWavFile;
    }
    //========================================================================
}
