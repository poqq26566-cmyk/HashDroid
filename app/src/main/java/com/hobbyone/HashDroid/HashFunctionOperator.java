/* HashFunctionOperator.java --
   版权所有 (C) 2010 Christophe Bouyer (Hobby One)

   此文件是 Hash Droid 的一部分。

   Hash Droid 是自由软件：您可以根据自由软件基金会发布的 GNU 通用公共许可证的条款重新分发和/或修改它；无论是许可证的第 3 版，还是（根据您的选择）任何更高版本。

   Hash Droid 的分发是希望它有用，但没有任何担保；甚至没有适销性或特定用途适用性的隐含担保。有关更多详细信息，请参阅 GNU 通用公共许可证。

   您应该已经随 Hash Droid 一起收到了 GNU 通用公共许可证的副本。如果没有，请参见 <http://www.gnu.org/licenses/>。
 */

package com.hobbyone.HashDroid;

import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class HashFunctionOperator {
    private String _sAlgo = "md5";

    public void SetAlgorithm(String sAlgo) {
        _sAlgo = sAlgo;
    }

    private String PrependValue(String iStr, int NbDigits) {
        String sReturnedStr = iStr;
        while (sReturnedStr.length() < NbDigits)
            sReturnedStr = "0" + sReturnedStr;
        return sReturnedStr;
    }

    private String CreateHashString(byte messageDigest[]) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++) {
            String h = Integer.toHexString(0xFF & messageDigest[i]);
            String StrComplete = PrependValue(h, 2);
            hexString.append(StrComplete);
        }
        return hexString.toString();
    }

    public String StringToHash(String s) {
        String sReturnedStr = "";
        if (_sAlgo.equals("CRC-32") || _sAlgo.equals("Adler-32")) {
            long value = 0;
            Checksum checksumv = null;
            if (_sAlgo.equals("CRC-32")) {
                CRC32 crc32v = new CRC32();
                crc32v.update(s.getBytes());
                checksumv = crc32v;
            } else if (_sAlgo.equals("Adler-32")) {
                Adler32 adler32v = new Adler32();
                adler32v.update(s.getBytes());
                checksumv = adler32v;
            }
            if (checksumv != null) {
                value = checksumv.getValue();
                String StrHex = Long.toHexString(value);
                sReturnedStr = PrependValue(StrHex, 8); // 32 位（十六进制为 8 位数字）
            }
        } else {
            IMessageDigest MessageDig = HashFactory.getInstance(_sAlgo);
            if (MessageDig != null) {
                byte[] in = s.getBytes();
                MessageDig.update(in, 0, in.length);
                byte messageDigest[] = MessageDig.digest();
                sReturnedStr = CreateHashString(messageDigest);
            }
        }

        return sReturnedStr;
    }

    public String FileToHash(InputStream inputstream) {
        String sReturnedStr = "";
        if (null != inputstream) {
            if (_sAlgo.equals("CRC-32") || _sAlgo.equals("Adler-32")) {
                try {
                    byte[] databytes = new byte[1024];
                    int nread = 0;
                    long value = 0;
                    Checksum checksumv = null;
                    if (_sAlgo.equals("CRC-32")) {
                        CRC32 crc32v = new CRC32();
                        checksumv = crc32v;
                    } else if (_sAlgo.equals("Adler-32")) {
                        Adler32 adler32v = new Adler32();
                        checksumv = adler32v;
                    }
                    if (checksumv != null) {
                        while ((nread = inputstream.read(databytes)) > 0) {
                            checksumv.update(databytes, 0, nread);
                        }
                        value = checksumv.getValue();
                        String StrHex = Long.toHexString(value);
                        sReturnedStr = PrependValue(StrHex, 8); // 32 位（十六进制为 8 位数字）
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    IMessageDigest MessageDig = HashFactory.getInstance(_sAlgo);
                    if (MessageDig != null) {
                        byte[] databytes = new byte[1024];
                        int nread = 0;
                        while ((nread = inputstream.read(databytes)) != -1) {
                            MessageDig.update(databytes, 0, nread);
                        }
                        byte messageDigest[] = MessageDig.digest();
                        sReturnedStr = CreateHashString(messageDigest);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sReturnedStr;
    }
}
