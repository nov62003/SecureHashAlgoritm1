package encrypt;

public class SHA {
    private final int[] h = {
        0x67452301,
        0xEFCDAB89,
        0x98BADCFE,
        0x10325476,
        0xC3D2E1F0
    };
    
    private char[] Digit = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C',
        'D', 'E', 'F'
    };
    
    private int[] int_digest = new int[5];
    private int[] tmp = new int[80];
    
    @SuppressWarnings("ManualArrayToCollectionCopy")
    private byte[] ByteArrFormat(byte[] bytedata) {
        int zeros = 0;
        int size = 0;
        int n = bytedata.length;
        int m = n % 64;
        
        /* Jika panjang pesan < 448 bit */
        if (m < 56) {
            zeros = 55 - m;
            size = n - m + 64;
        } /* Jika panjang pesan =448 bit */
        else if (m == 56) {
            zeros = 63;
            size = n + 8 + 64;
        } /* Jika panjang pesan lebih dari 512 bit */
        else {
            zeros = 63 - m + 56;
            size = (n + 64) - m + 64;
        }
        
        byte[] newbyte = new byte[size];
        
        for (int j = 0; j < n; j++){
            newbyte[j] = bytedata[j];
        }
        int l = n;
        
        /* Penambahan bit 1 */
        newbyte[l++] = (byte) 0x80;
        /* Penambahan bit 0 */
        for (int i = 0; i < zeros; i++) {
            newbyte[l++] = (byte) 0x00;
        }
        
        /* Penambahan 64 bit yang menyatakan panjang pesan  */
        long N = (long) n * 8;
        byte h8 = (byte) (N & 0xFF);
        byte h7 = (byte) ((N >> 8) & 0xFF);
        byte h6 = (byte) ((N >> 16) & 0xFF);
        byte h5 = (byte) ((N >> 24) & 0xFF);
        byte h4 = (byte) ((N >> 32) & 0xFF);
        byte h3 = (byte) ((N >> 40) & 0xFF);
        byte h2 = (byte) ((N >> 48) & 0xFF);
        byte h1 = (byte) (N >> 56);
        newbyte[l++] = h1;
        newbyte[l++] = h2;
        newbyte[l++] = h3;
        newbyte[l++] = h4;
        newbyte[l++] = h5;
        newbyte[l++] = h6;
        newbyte[l++] = h7;
        newbyte[l++] = h8;
        return newbyte;
    }

    private int count1(int x, int y, int z) {
        return (x & y) | (~x & z);
    }

    private int count2(int x, int y, int z) {
        return x ^ y ^ z;
    }

    private int count3(int x, int y, int z) {
        return (x & y) | (x & z) | (y & z);
    }

    private int count3(int x, int y) {
        return (x << y) | x >>> (32 - y);
    }

    private int ByteArrToInt(byte[] bytedata, int i) {
        return ((bytedata[i] & 0xff) << 24) |
                ((bytedata[i + 1] & 0xff) << 16) |
                ((bytedata[i + 2] & 0xff) << 8) | (bytedata[i + 3] & 0xff);
    }
    
    private void intToByteArray(int intValue, byte[] byteData, int i) {
        byteData[i] = (byte) (intValue >>> 24);
        byteData[i + 1] = (byte) (intValue >>> 16);
        byteData[i + 2] = (byte) (intValue >>> 8);
        byteData[i + 3] = (byte) intValue;
    }
    
    @SuppressWarnings("ManualArrayToCollectionCopy")
    private byte[] BytesToDigest(byte[] byteData) {
        System.arraycopy(h, 0, int_digest, 0, int_digest.length);
        byte[] newbyte = ByteArrFormat(byteData);
        int MCount = newbyte.length / 64;
        /* Untuk tiap-tiap blok-blok 512 bit */
        for (int pos = 0; pos < MCount; pos++) {
            /* Bagi pesan kedalam 32 bit */
            for (int j = 0; j < 16; j++) {
                tmp[j] = ByteArrToInt(newbyte, (pos * 64) + (j * 4));
            }
            /* persamaan 5.09 */
            for (int a = 16; a <= 79; a++) {
                tmp[a] = count3(tmp[a - 3] ^ 
                        tmp[a - 8] ^ tmp[a - 14] ^ 
                        tmp[a - 16], 1);
            }

            int[] tmpabcde = new int[5];
            
            System.arraycopy(int_digest, 0, tmpabcde, 0, tmpabcde.length);
            
            /*for (int j = 0; j < 80; j++){
                int f = 0;
                int k = 0;
                if (0 < j && j > 20){
                    f = count1(tmpabcde[1], tmpabcde[2],tmpabcde[3]);
                    k = 0x5a827999;
                } else if (20 < j && j > 40){
                    f = count2(tmpabcde[1], tmpabcde[2],tmpabcde[3]);
                    k = 0x6ed9eba1;
                } else if (40 < j && j > 60){
                    f = count3(tmpabcde[1], tmpabcde[2],tmpabcde[3]);
                    k = 0x8f1bbcdc;
                } else {
                    f = count2(tmpabcde[1], tmpabcde[2],tmpabcde[3]);
                    k = 0xca62c1d6;
                }
                int temp = count3(tmpabcde[0], 5) + f + tmpabcde[4] + this.tmp[j] + k;
                tmpabcde[4] = tmpabcde[3];
                tmpabcde[3] = tmpabcde[2];
                tmpabcde[2] = count3(tmpabcde[1], 30);
                tmpabcde[1] = tmpabcde[0];
                tmpabcde[0] = temp;
            }
            */for (int j = 0; j <= 19; j++) {
                int tmp = count3(tmpabcde[0], 5) +
                    count1(tmpabcde[1], tmpabcde[2],
                    tmpabcde[3]) + tmpabcde[4] +
                    this.tmp[j] + 0x5a827999;
                tmpabcde[4] = tmpabcde[3];
                tmpabcde[3] = tmpabcde[2];
                tmpabcde[2] = count3(tmpabcde[1], 30);
                tmpabcde[1] = tmpabcde[0];
                tmpabcde[0] = tmp;
            }

            for (int k = 20; k <= 39; k++) {
                int tmp = count3(tmpabcde[0], 5) +
                        count2(tmpabcde[1], tmpabcde[2],
                        tmpabcde[3]) + tmpabcde[4] +
                        this.tmp[k] + 0x6ed9eba1;
                tmpabcde[4] = tmpabcde[3];
                tmpabcde[3] = tmpabcde[2];
                tmpabcde[2] = count3(tmpabcde[1], 30);
                tmpabcde[1] = tmpabcde[0];
                tmpabcde[0] = tmp;
            }
            
            for (int l = 40; l <= 59; l++) {
                int tmp = count3(tmpabcde[0], 5) +
                        count3(tmpabcde[1], tmpabcde[2],
                        tmpabcde[3]) + tmpabcde[4] +
                        this.tmp[l] + 0x8f1bbcdc;
                tmpabcde[4] = tmpabcde[3];
                tmpabcde[3] = tmpabcde[2];
                tmpabcde[2] = count3(tmpabcde[1], 30);
                tmpabcde[1] = tmpabcde[0];
                tmpabcde[0] = tmp;
            }
            
            for (int m = 60; m <= 79; m++) {
                int tmp = count3(tmpabcde[0], 5) +
                        count2(tmpabcde[1], tmpabcde[2],
                        tmpabcde[3]) + tmpabcde[4] +
                        this.tmp[m] + 0xca62c1d6;
                tmpabcde[4] = tmpabcde[3];
                tmpabcde[3] = tmpabcde[2];
                tmpabcde[2] = count3(tmpabcde[1], 30);
                tmpabcde[1] = tmpabcde[0];
                tmpabcde[0] = tmp;
            }

            for (int i2 = 0; i2 < tmpabcde.length; i2++) {
                int_digest[i2] = int_digest[i2] + tmpabcde[i2];
            }
            
            for (int n = 0; n < tmp.length; n++) {
                tmp[n] = 0;
            }
        }
        byte[] digest = new byte[20];
        for (int i = 0; i < int_digest.length; i++) {
            intToByteArray(int_digest[i], digest, i * 4);
        }
        return digest;
    }

    public String StringToDigest(byte[] byteData) {
        String strDigest = "";
        byte[] data = BytesToDigest(byteData);
        for (int i = 0; i < data.length; i++) {
            char[] ob = new char[2];
            ob[0] = Digit[(data[i] >>> 4) & 0X0F];
            ob[1] = Digit[data[i] & 0X0F];
            String s = new String(ob);
            strDigest += s;
        }
        return strDigest;
    }
}