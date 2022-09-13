/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cskefu.cc.util;

public class MD5 {

	public String getMD5ofStr(String inbuf) {
		md5Init();
		md5Update(inbuf.getBytes(), inbuf.length());
		md5Final();
		digestHexStr = "";
		for (int i = 0; i < 16; i++)
			digestHexStr += byteHEX(digest[i]);

		return digestHexStr;
	}
	
	public String getMD5ofByte(byte[] inbuf) {
		md5Init();
		md5Update(inbuf, inbuf.length);
		md5Final();
		digestHexStr = "";
		for (int i = 0; i < 16; i++)
			digestHexStr += byteHEX(digest[i]);

		return digestHexStr;
	}
	
	public MD5() {
		state = new long[4];
		count = new long[2];
		buffer = new byte[64];
		digest = new byte[16];
		md5Init();
	}

	private void md5Init() {
		count[0] = 0L;
		count[1] = 0L;
		state[0] = 0x67452301L;
		state[1] = 0xefcdab89L;
		state[2] = 0x98badcfeL;
		state[3] = 0x10325476L;
	}

	private long F(long x, long y, long z) {
		return x & y | ~x & z;
	}

	private long G(long x, long y, long z) {
		return x & z | y & ~z;
	}

	private long H(long x, long y, long z) {
		return x ^ y ^ z;
	}

	private long I(long x, long y, long z) {
		return y ^ (x | ~z);
	}

	private long FF(long a, long b, long c, long d, long x, long s, long ac) {
		a += F(b, c, d) + x + ac;
		a = (int) a << (int) s | (int) a >>> (int) (32L - s);
		a += b;
		return a;
	}

	private long GG(long a, long b, long c, long d, long x, long s, long ac) {
		a += G(b, c, d) + x + ac;
		a = (int) a << (int) s | (int) a >>> (int) (32L - s);
		a += b;
		return a;
	}

	private long HH(long a, long b, long c, long d, long x, long s, long ac) {
		a += H(b, c, d) + x + ac;
		a = (int) a << (int) s | (int) a >>> (int) (32L - s);
		a += b;
		return a;
	}

	private long II(long a, long b, long c, long d, long x, long s, long ac) {
		a += I(b, c, d) + x + ac;
		a = (int) a << (int) s | (int) a >>> (int) (32L - s);
		a += b;
		return a;
	}

	private void md5Update(byte inbuf[], int inputLen) {
		byte block[] = new byte[64];
		int index = (int) (count[0] >>> 3) & 0x3f;
		if ((count[0] += inputLen << 3) < (long) (inputLen << 3))
			count[1]++;
		count[1] += inputLen >>> 29;
		int partLen = 64 - index;
		int i;
		if (inputLen >= partLen) {
			md5Memcpy(buffer, inbuf, index, 0, partLen);
			md5Transform(buffer);
			for (i = partLen; i + 63 < inputLen; i += 64) {
				md5Memcpy(block, inbuf, 0, i, 64);
				md5Transform(block);
			}

			index = 0;
		} else {
			i = 0;
		}
		md5Memcpy(buffer, inbuf, index, i, inputLen - i);
	}

	private void md5Final() {
		byte bits[] = new byte[8];
		encode(bits, count, 8);
		int index = (int) (count[0] >>> 3) & 0x3f;
		int padLen = index >= 56 ? 120 - index : 56 - index;
		md5Update(PADDING, padLen);
		md5Update(bits, 8);
		encode(digest, state, 16);
	}

	private void md5Memcpy(byte output[], byte input[], int outpos, int inpos, int len) {
		for (int i = 0; i < len; i++)
			output[outpos + i] = input[inpos + i];

	}

	private void md5Transform(byte block[]) {
		long a = state[0];
		long b = state[1];
		long c = state[2];
		long d = state[3];
		long x[] = new long[16];
		decode(x, block, 64);
		a = FF(a, b, c, d, x[0], 7L, 0xd76aa478L);
		d = FF(d, a, b, c, x[1], 12L, 0xe8c7b756L);
		c = FF(c, d, a, b, x[2], 17L, 0x242070dbL);
		b = FF(b, c, d, a, x[3], 22L, 0xc1bdceeeL);
		a = FF(a, b, c, d, x[4], 7L, 0xf57c0fafL);
		d = FF(d, a, b, c, x[5], 12L, 0x4787c62aL);
		c = FF(c, d, a, b, x[6], 17L, 0xa8304613L);
		b = FF(b, c, d, a, x[7], 22L, 0xfd469501L);
		a = FF(a, b, c, d, x[8], 7L, 0x698098d8L);
		d = FF(d, a, b, c, x[9], 12L, 0x8b44f7afL);
		c = FF(c, d, a, b, x[10], 17L, 0xffff5bb1L);
		b = FF(b, c, d, a, x[11], 22L, 0x895cd7beL);
		a = FF(a, b, c, d, x[12], 7L, 0x6b901122L);
		d = FF(d, a, b, c, x[13], 12L, 0xfd987193L);
		c = FF(c, d, a, b, x[14], 17L, 0xa679438eL);
		b = FF(b, c, d, a, x[15], 22L, 0x49b40821L);
		a = GG(a, b, c, d, x[1], 5L, 0xf61e2562L);
		d = GG(d, a, b, c, x[6], 9L, 0xc040b340L);
		c = GG(c, d, a, b, x[11], 14L, 0x265e5a51L);
		b = GG(b, c, d, a, x[0], 20L, 0xe9b6c7aaL);
		a = GG(a, b, c, d, x[5], 5L, 0xd62f105dL);
		d = GG(d, a, b, c, x[10], 9L, 0x2441453L);
		c = GG(c, d, a, b, x[15], 14L, 0xd8a1e681L);
		b = GG(b, c, d, a, x[4], 20L, 0xe7d3fbc8L);
		a = GG(a, b, c, d, x[9], 5L, 0x21e1cde6L);
		d = GG(d, a, b, c, x[14], 9L, 0xc33707d6L);
		c = GG(c, d, a, b, x[3], 14L, 0xf4d50d87L);
		b = GG(b, c, d, a, x[8], 20L, 0x455a14edL);
		a = GG(a, b, c, d, x[13], 5L, 0xa9e3e905L);
		d = GG(d, a, b, c, x[2], 9L, 0xfcefa3f8L);
		c = GG(c, d, a, b, x[7], 14L, 0x676f02d9L);
		b = GG(b, c, d, a, x[12], 20L, 0x8d2a4c8aL);
		a = HH(a, b, c, d, x[5], 4L, 0xfffa3942L);
		d = HH(d, a, b, c, x[8], 11L, 0x8771f681L);
		c = HH(c, d, a, b, x[11], 16L, 0x6d9d6122L);
		b = HH(b, c, d, a, x[14], 23L, 0xfde5380cL);
		a = HH(a, b, c, d, x[1], 4L, 0xa4beea44L);
		d = HH(d, a, b, c, x[4], 11L, 0x4bdecfa9L);
		c = HH(c, d, a, b, x[7], 16L, 0xf6bb4b60L);
		b = HH(b, c, d, a, x[10], 23L, 0xbebfbc70L);
		a = HH(a, b, c, d, x[13], 4L, 0x289b7ec6L);
		d = HH(d, a, b, c, x[0], 11L, 0xeaa127faL);
		c = HH(c, d, a, b, x[3], 16L, 0xd4ef3085L);
		b = HH(b, c, d, a, x[6], 23L, 0x4881d05L);
		a = HH(a, b, c, d, x[9], 4L, 0xd9d4d039L);
		d = HH(d, a, b, c, x[12], 11L, 0xe6db99e5L);
		c = HH(c, d, a, b, x[15], 16L, 0x1fa27cf8L);
		b = HH(b, c, d, a, x[2], 23L, 0xc4ac5665L);
		a = II(a, b, c, d, x[0], 6L, 0xf4292244L);
		d = II(d, a, b, c, x[7], 10L, 0x432aff97L);
		c = II(c, d, a, b, x[14], 15L, 0xab9423a7L);
		b = II(b, c, d, a, x[5], 21L, 0xfc93a039L);
		a = II(a, b, c, d, x[12], 6L, 0x655b59c3L);
		d = II(d, a, b, c, x[3], 10L, 0x8f0ccc92L);
		c = II(c, d, a, b, x[10], 15L, 0xffeff47dL);
		b = II(b, c, d, a, x[1], 21L, 0x85845dd1L);
		a = II(a, b, c, d, x[8], 6L, 0x6fa87e4fL);
		d = II(d, a, b, c, x[15], 10L, 0xfe2ce6e0L);
		c = II(c, d, a, b, x[6], 15L, 0xa3014314L);
		b = II(b, c, d, a, x[13], 21L, 0x4e0811a1L);
		a = II(a, b, c, d, x[4], 6L, 0xf7537e82L);
		d = II(d, a, b, c, x[11], 10L, 0xbd3af235L);
		c = II(c, d, a, b, x[2], 15L, 0x2ad7d2bbL);
		b = II(b, c, d, a, x[9], 21L, 0xeb86d391L);
		state[0] += a;
		state[1] += b;
		state[2] += c;
		state[3] += d;
	}

	private void encode(byte output[], long input[], int len) {
		int i = 0;
		for (int j = 0; j < len; j += 4) {
			output[j] = (byte) (int) (input[i] & 255L);
			output[j + 1] = (byte) (int) (input[i] >>> 8 & 255L);
			output[j + 2] = (byte) (int) (input[i] >>> 16 & 255L);
			output[j + 3] = (byte) (int) (input[i] >>> 24 & 255L);
			i++;
		}

	}

	private void decode(long output[], byte input[], int len) {
		int i = 0;
		for (int j = 0; j < len; j += 4) {
			output[i] = b2iu(input[j]) | b2iu(input[j + 1]) << 8 | b2iu(input[j + 2]) << 16 | b2iu(input[j + 3]) << 24;
			i++;
		}

	}

	private long b2iu(byte b) {
		return b >= 0 ? b : b & 0xff;
	}

	private String byteHEX(byte ib) {
		char Digit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		char ob[] = new char[2];
		ob[0] = Digit[ib >>> 4 & 0xf];
		ob[1] = Digit[ib & 0xf];
		String s = new String(ob);
		return s;
	}

	static final int S11 = 7;
	static final int S12 = 12;
	static final int S13 = 17;
	static final int S14 = 22;
	static final int S21 = 5;
	static final int S22 = 9;
	static final int S23 = 14;
	static final int S24 = 20;
	static final int S31 = 4;
	static final int S32 = 11;
	static final int S33 = 16;
	static final int S34 = 23;
	static final int S41 = 6;
	static final int S42 = 10;
	static final int S43 = 15;
	static final int S44 = 21;
	static final byte PADDING[] = { -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0 };
	private long state[];
	private long count[];
	private byte buffer[];
	private String digestHexStr;
	private byte digest[];
}