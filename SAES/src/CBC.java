import java.util.Random;

public class CBC {

    //随机数生成IV（int）16位，
    public static int generateIV() {
        Random random = new Random();
        String binaryString = "";
        for (int i = 0; i < 16; i++)
            binaryString += random.nextInt(2);
        return Integer.parseInt(binaryString, 2); // 将二进制字符串转换为十进制数
    }

    //加密函数
    public static String encrypt_CBC(String plaintext, int key, int IV){
        //划分明文组数
        int num = plaintext.length()/16;
        //定义密文
        String ciphertext = "";
        //定义Ci.Pi
        int C = IV;
        int []P = new int[num];
        String Half = "";
        for(int i = 0;i < num;i++) {
            Half = plaintext.substring(16*i, 16*i+16);
            P[i] = Integer.parseInt(Half, 2);
        }
        System.out.print("初始的IV："+C);
        //for循环加密
        for(int i = 1; i <= num; i++) {
            C = SAES.encrypt1( C ^ P[i-1], key);
            if(Integer.toBinaryString(C).length()!=16) {   //自动补0
                for(int j = 0; j < 16 - Integer.toBinaryString(C).length();j++)    ciphertext += "0";
            }
            ciphertext += Integer.toBinaryString(C);
        }
        //返回密文
        return ciphertext;
    }

    //解密函数
    public static String decrypt_CBC(String ciphertext, int key, int IV1){
        //划分密文组数
        int num = ciphertext.length()/16;
        //定义明文
        String plaintext = "";
        //定义Ci.Pi
        int P,IV = IV1;
        int []C = new int[num];
        String Half = "";
        for(int i = 0; i < num; i++) {
            Half = ciphertext.substring(i*16, 16*i+16); // 获取前16位的子字符串
            C[i] = Integer.parseInt(Half, 2);
        }
        //for循环解密
        for(int i = 1; i <= num; i++) {
            P = SAES.decrypt1(C[i - 1], key) ^ IV;
            IV = C[i - 1];
            if(Integer.toBinaryString(P).length()!=16) {   //自动补0
                for(int j = 0; j < 16 - Integer.toBinaryString(P).length();j++)    plaintext += "0";
            }
            plaintext += Integer.toBinaryString(P);
        }
        //返回明文
        return plaintext;
    }

}