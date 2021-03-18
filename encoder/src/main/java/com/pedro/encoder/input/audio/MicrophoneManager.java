package com.pedro.encoder.input.audio;

import android.media.AudioFormat;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import com.pedro.encoder.Frame;
import java.nio.ByteBuffer;

/**
 * Created by pedro on 19/01/17.
 */

public class MicrophoneManager {

  private final String TAG = "MicrophoneManager";
  private int BUFFER_SIZE = 0;
  protected AudioRecord audioRecord;
  private final GetMicrophoneData getMicrophoneData;
  protected ByteBuffer pcmBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
  protected byte[] pcmBufferMuted = new byte[BUFFER_SIZE];
  protected boolean running = false;
  private boolean created = false;
  //default parameters for microphone
  private int sampleRate = 32000; //hz
  private final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
  private int channel = AudioFormat.CHANNEL_IN_STEREO;
  protected boolean muted = false;
  private AudioPostProcessEffect audioPostProcessEffect;
  HandlerThread handlerThread;
  protected CustomAudioEffect customAudioEffect = new NoAudioEffect();

  public MicrophoneManager(GetMicrophoneData getMicrophoneData) {
    this.getMicrophoneData = getMicrophoneData;
  }

  public void setCustomAudioEffect(CustomAudioEffect customAudioEffect) {
    this.customAudioEffect = customAudioEffect;
  }

  /**
   * Create audio record
   */
  public void createMicrophone() {
    createMicrophone(sampleRate, true, false, false);
    Log.i(TAG, "Microphone created, " + sampleRate + "hz, Stereo");
  }
  /**默认声音**/
  public static final int DEFAULT = 0;
  /**麦克风声音*/
 public static final int MIC = 1;
  /**通话上行声音*/
  public static final int VOICE_UPLINK = 2;
  /**通话下行声音*/
  public static final int VOICE_DOWNLINK = 3;
  /**通话上下行声音*/
  public static final int VOICE_CALL = 4;
  /**根据摄像头转向选择麦克风*/
  public static final int CAMCORDER = 5;
  /**对麦克风声音进行声音识别，然后进行录制*/
  public static final int VOICE_RECOGNITION = 6;
  /**对麦克风中类似ip通话的交流声音进行识别，默认会开启回声消除和自动增益*/
  public static final int VOICE_COMMUNICATION = 7;
  /**录制系统内置声音*/
  public static final int REMOTE_SUBMIX = 8;

  /**录制系统内置声音*/
  //设置声音的源数据   todo  要修改声音的原  REMOTE_SUBMIX
  public boolean createMicrophone(int sampleRate, boolean isStereo, boolean echoCanceler,
      boolean noiseSuppressor) {
    return createMicrophone(MediaRecorder.AudioSource.DEFAULT, sampleRate, isStereo, echoCanceler,
        noiseSuppressor);
  }

  /**
   * Create audio record with params and selected audio source
   *
   * @param audioSource - the recording source. See {@link MediaRecorder.AudioSource} for the
   * recording source definitions.
   */
  public boolean createMicrophone(int audioSource, int sampleRate, boolean isStereo,
      boolean echoCanceler, boolean noiseSuppressor) {
    try {
      this.sampleRate = sampleRate;
      channel = isStereo ? AudioFormat.CHANNEL_IN_STEREO : AudioFormat.CHANNEL_IN_MONO;
      if (audioRecord==null){
          audioRecord =
                  new AudioRecord(audioSource, sampleRate, channel, audioFormat, getPcmBufferSize());
      }

      audioPostProcessEffect = new AudioPostProcessEffect(audioRecord.getAudioSessionId());
      if (echoCanceler) audioPostProcessEffect.enableEchoCanceler();
      if (noiseSuppressor) audioPostProcessEffect.enableNoiseSuppressor();
      String chl = (isStereo) ? "Stereo" : "Mono";
      Log.i(TAG, "Microphone created, " + sampleRate + "hz, " + chl);
      created = true;
    } catch (IllegalArgumentException e) {
      Log.e(TAG, "create microphone error", e);
    }
    return created;
  }

  /**
   * Create audio record with params and AudioPlaybackCaptureConfig used for capturing internal
   * audio
   * Notice that you should granted {@link android.Manifest.permission#RECORD_AUDIO} before calling
   * this!
   *
   * @param config - AudioPlaybackCaptureConfiguration received from {@link
   * android.media.projection.MediaProjection}
   * @see AudioPlaybackCaptureConfiguration.Builder#Builder(MediaProjection)
   * @see "https://developer.android.com/guide/topics/media/playback-capture"
   * @see "https://medium.com/@debuggingisfun/android-10-audio-capture-77dd8e9070f9"
   */
  public boolean createInternalMicrophone(AudioPlaybackCaptureConfiguration config, int sampleRate,
      boolean isStereo, boolean echoCanceler, boolean noiseSuppressor) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        this.sampleRate = sampleRate;
        channel = isStereo ? AudioFormat.CHANNEL_IN_STEREO : AudioFormat.CHANNEL_IN_MONO;
        audioRecord = new AudioRecord.Builder().setAudioPlaybackCaptureConfig(config)
            .setAudioFormat(new AudioFormat.Builder().setEncoding(audioFormat)
                .setSampleRate(sampleRate)
                .setChannelMask(channel)
                .build())
            .setBufferSizeInBytes(getPcmBufferSize())
            .build();
        audioPostProcessEffect = new AudioPostProcessEffect(audioRecord.getAudioSessionId());
        if (echoCanceler) audioPostProcessEffect.enableEchoCanceler();
        if (noiseSuppressor) audioPostProcessEffect.enableNoiseSuppressor();
        String chl = (isStereo) ? "Stereo" : "Mono";
        Log.i(TAG, "Internal microphone created, " + sampleRate + "hz, " + chl);
        created = true;
      } else {
        return createMicrophone(sampleRate, isStereo, echoCanceler, noiseSuppressor);
      }
    } catch (IllegalArgumentException e) {
      Log.e(TAG, "create microphone error", e);
    }
    return created;
  }

  public boolean createInternalMicrophone(AudioPlaybackCaptureConfiguration config, int sampleRate,
      boolean isStereo) {
    return createInternalMicrophone(config, sampleRate, isStereo, false, false);
  }

  /**
   * Start record and get data
   */
  public synchronized void start() {
    init();
    handlerThread = new HandlerThread(TAG);
    handlerThread.start();
    Handler handler = new Handler(handlerThread.getLooper());
    handler.post(new Runnable() {
      @Override
      public void run() {
        while (running) {
          Frame frame = read();
          if (frame != null) {
            getMicrophoneData.inputPCMData(frame);
          }
        }
      }
    });
  }

  private void init() {
    if (audioRecord != null) {
      audioRecord.startRecording();
      running = true;
      Log.i(TAG, "Microphone started");
    } else {
      Log.e(TAG, "Error starting, microphone was stopped or not created, "
          + "use createMicrophone() before start()");
    }
  }

  public void mute() {
    muted = true;
  }

  public void unMute() {
    muted = false;
  }

  public boolean isMuted() {
    return muted;
  }

  /**
   * @return Object with size and PCM buffer data
   */
  private Frame read() {
    pcmBuffer.rewind();
    int size = audioRecord.read(pcmBuffer, pcmBuffer.remaining());
    if (size < 0) {
      return null;
    }
    return new Frame(muted ? pcmBufferMuted : customAudioEffect.process(pcmBuffer.array()),
        muted ? 0 : pcmBuffer.arrayOffset(), size);
  }

  /**
   * Stop and release microphone
   */
  public synchronized void stop() {
    running = false;
    created = false;
    if (handlerThread != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        handlerThread.quitSafely();
      } else {
        handlerThread.quit();
      }
    }
    if (audioRecord != null) {
      audioRecord.setRecordPositionUpdateListener(null);
      audioRecord.stop();
      audioRecord.release();
      audioRecord = null;
    }
    if (audioPostProcessEffect != null) {
      audioPostProcessEffect.releaseEchoCanceler();
      audioPostProcessEffect.releaseNoiseSuppressor();
    }
    Log.i(TAG, "Microphone stopped");
  }

  /**
   * Get PCM buffer size
   */
  private int getPcmBufferSize() {
    BUFFER_SIZE = AudioRecord.getMinBufferSize(sampleRate, channel, audioFormat);
    pcmBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    pcmBufferMuted = new byte[BUFFER_SIZE];
    return BUFFER_SIZE * 5;
  }

  public int getMaxInputSize() {
    return BUFFER_SIZE;
  }

  public int getSampleRate() {
    return sampleRate;
  }

  public void setSampleRate(int sampleRate) {
    this.sampleRate = sampleRate;
  }

  public int getAudioFormat() {
    return audioFormat;
  }

  public int getChannel() {
    return channel;
  }

  public boolean isRunning() {
    return running;
  }

  public boolean isCreated() {
    return created;
  }
}
