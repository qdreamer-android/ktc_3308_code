package com.pwong.library.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WaveUtils {

	/**
	 * pcm 格式音频转wav格式
	 * 
	 * @param wavPath
	 *            wav音频路径
	 * @param pcmPath
	 *            pcm音频路径
	 * @param sampleRate
	 *            pcm音频的采样率
	 * @param channel
	 *            pcm音频的声道数
	 * @throws Exception
	 */
	public static void pcm2Wav(String wavPath, String pcmPath, int sampleRate, short channel) throws Exception {
		FileOutputStream fos = new FileOutputStream(wavPath);
		byte[] pcm = fileReadBuf(pcmPath);
		int pcmLen = pcm.length;
		byte[] h = makeHeader(pcmLen, sampleRate, channel);

		fos.write(h, 0, h.length);
		fos.write(pcm);
		fos.close();
	}

	private static byte[] fileReadBuf(String fn) {
		File f = new File(fn);
		Long file_len = f.length();
		int len = file_len.intValue();
		byte[] content = new byte[len];
		try {
			FileInputStream in = new FileInputStream(f);
			in.read(content);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	private static byte[] makeHeader(int pcmLen, int sampleRate, short channel) throws IOException {
		final char fileID[] = { 'R', 'I', 'F', 'F' };
		int fileLength = pcmLen + (44 - 8);
		char wavTag[] = { 'W', 'A', 'V', 'E' };
		char FmtHdrID[] = { 'f', 'm', 't', ' ' };
		int FmtHdrLeth = 16;
		short FormatTag = 0x0001;
		short Channel = channel;
		int SamplesPerSec = sampleRate;
		short BitsPerSample = 16;
		int AvgBytesPerSec = (Channel * BitsPerSample / 8) * sampleRate;
		short BlockAlign = (short) (Channel * BitsPerSample / 8);
		char DataHdrID[] = { 'd', 'a', 't', 'a' };
		int DataHdrLen = pcmLen;

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		writeChar(os, fileID);
		writeInt(os, fileLength);
		writeChar(os, wavTag);
		writeChar(os, FmtHdrID);
		writeInt(os, FmtHdrLeth);
		writeShort(os, FormatTag);
		writeShort(os, Channel);
		writeInt(os, SamplesPerSec);
		writeInt(os, AvgBytesPerSec);
		writeShort(os, BlockAlign);
		writeShort(os, BitsPerSample);
		writeChar(os, DataHdrID);
		writeInt(os, DataHdrLen);
		os.flush();
		byte[] r = os.toByteArray();
		os.close();
		return r;
	}

	public static byte[] readWav(String wav_fn) {
		byte[] tmp = null, wav = null;
		FileInputStream in = null;
		int i, n;
		int hlen = 44;

		try {
			in = new FileInputStream(wav_fn);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			n = in.available();
			tmp = new byte[n];
			wav = new byte[n - hlen];
			in.read(tmp, 0, n);
			in.close();
			for (i = hlen; i < n; ++i) {
				wav[i - hlen] = tmp[i];
			}
			n = n - hlen;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return wav;
	}

	private static void writeShort(ByteArrayOutputStream os, int s) throws IOException {
		byte[] mybyte = new byte[2];
		mybyte[1] = (byte) ((s << 16) >> 24);
		mybyte[0] = (byte) ((s << 24) >> 24);
		os.write(mybyte);
	}

	private static void writeInt(ByteArrayOutputStream os, int n) throws IOException {
		byte[] buf = new byte[4];
		buf[3] = (byte) (n >> 24);
		buf[2] = (byte) ((n << 8) >> 24);
		buf[1] = (byte) ((n << 16) >> 24);
		buf[0] = (byte) ((n << 24) >> 24);
		os.write(buf);
	}

	private static void writeChar(ByteArrayOutputStream os, char[] id) {
		for (int i = 0; i < id.length; i++) {
			char c = id[i];
			os.write(c);
		}
	}
}
