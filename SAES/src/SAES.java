public class SAES {
    // 定义S-盒和逆S-盒
    private static final int[][] S_BOX = {
            {0x9, 0x4, 0xA, 0xB},
            {0xD, 0x1, 0x8, 0x5},
            {0x6, 0x2, 0x0, 0x3},
            {0xC, 0xE, 0xF, 0x7}
    };
    private static final int[][] INVERSE_S_BOX = {
            {0xA, 0x5, 0x9, 0xB},
            {0x1, 0x7, 0x8, 0xF},
            {0x6, 0x0, 0x2, 0x3},
            {0xC, 0x4, 0xD, 0xE}
    };

    // 定义列混淆矩阵和逆列混淆矩阵
    private static final int[][] MIX_COLUMNS_MATRIX = {
            {0x1, 0x4},
            {0x4, 0x1}
    };
    private static final int[][] INVERSE_MIX_COLUMNS_MATRIX = {
            {0x9, 0x2},
            {0x2, 0x9}
    };

    // 轮密钥加函数 ：将16位状态矩阵与16位轮密钥逐位异或。
    private static int addRoundKey(int state, int roundKey) {
        int result = state ^ roundKey;
        return result;
    }

    // 字节替换函数：利用状态矩阵索引S盒中的值
    private static int substituteBytes(int state, int[][] sBox) {
        int result = 0;
        result |= (sBox[(state >> 14) & 0x03][(state >> 12) & 0x03] << 12);
        result |= (sBox[(state >> 10) & 0x03][state >> 8 & 0x03] << 8 );
        result |= (sBox[(state >> 6) & 0x03][(state >> 4) & 0x03] << 4);
        result |= (sBox[(state >> 2) & 0x03][state & 0x03] );
        return result;
    }

    // 行移位函数
    private static int rowShift(int state) {
        int first = state & 0xF000;  // 提取第一个
        int third = state & 0x00F0; // 提取第三个  
        int second = state & 0x0F00;  // 提取第二个   
        int fourth = state & 0x000F; // 提取第四个
        return first | third | (( second >> 8 ) & 0x000F)| (( fourth << 8 ) & 0x0F00 ) ;
    }

    // 列混合函数
    private static int mixColumns(int state, int[][] matrix) {
        int[] columns = new int[4];
        columns[0] = (state >> 12) & 0xF;	//S00
        columns[1] = (state >> 8) & 0xF;	//S10
        columns[2] = (state >> 4) & 0xF;	//S01
        columns[3] = state & 0xF;	//S11

        int[] result = new int[4];
        result[0] = multiply(matrix[0][0], columns[0]) ^ multiply(matrix[0][1], columns[1]);	//S‘00
        result[1] = multiply(matrix[1][0], columns[0]) ^ multiply(matrix[1][1], columns[1]);      //S’10
        result[2] = multiply(matrix[0][0], columns[2]) ^ multiply(matrix[0][1], columns[3]);	//S‘01
        result[3] = multiply(matrix[1][0], columns[2]) ^ multiply(matrix[1][1], columns[3]);      //S’11
        return (result[0] << 12) | (result[1] << 8) | (result[2] << 4) | result[3];
    }
    static int multiply(int a, int b) {
        int result = 0;
        while (b != 0) {
            if ((b & 0x01) != 0) {
                result ^= a;
            }
            boolean carry = (a & 0x08) != 0;  // 判断是否有进位
            a <<= 1;
            if (carry) {
                a ^= 0x13;  // GF(2^4)的模多项式：x^4 + x + 1
            }
            b >>= 1;
        }
        return result;
    }

    // Rcon函数，用于生成轮常量
    private static int rcon(int round) {
        if (round == 1) {
            return 0x80;
        } else if (round == 2) {
            return 0x30;
        } else {
            return 0;
        }
    }
    private static int g(int w, int round) {
        int n0_ = S_BOX[(w >> 6) & 0x03][(w >> 4) & 0x03];
        int n1_ = S_BOX[(w >> 2) & 0x03][w & 0x03];
        int result = n0_ | ( n1_ << 4 );
        return rcon(round) ^ result;
    }
    // 扩展密钥
    private static int[] expandKey(int key) {
        int[] roundKey = new int[3];
        roundKey[0] = key;
        for (int i = 1; i < 3; i++) {
            roundKey[i] = generateRoundKey(roundKey[i - 1], i);
        }
        return roundKey;
    }
    // 生成轮密钥
    private static int generateRoundKey(int roundKey, int round) {
        int result = roundKey;
        int w0 = ( result >> 8 ) & 0xFF;
        int w1 = result & 0xFF;
        int w2 = w0 ^ g(w1, round);
        int w3 = w2 ^ w1;
        return ( w2 << 8 ) ^ w3;
    }

    // 加密方法
    public static int encrypt1(int plaintext, int key) {
        int[] roundKey = expandKey(key); // 扩展密钥
        int state = plaintext;
        state = addRoundKey(state, roundKey[0]);// 初始轮密钥加
        for (int i = 0; i < 2; i++) {
            state = substituteBytes(state, S_BOX);// 字节代换
            state = rowShift(state);// 行移位
            if (i < 1) {
                state = mixColumns(state, MIX_COLUMNS_MATRIX);
            }// 列混合            
            state = addRoundKey(state, roundKey[i + 1]);// 轮密钥加
        }
        return state;
    }

    // 解密方法
    public static int decrypt1(int ciphertext, int key) {
        int[] roundKey = expandKey(key);// 扩展密钥
        int state = ciphertext;
        state = addRoundKey(state, roundKey[2]);// 初始轮密钥加
        for (int i = 1; i >= 0; i--) {
            if (i < 1) {
                state = mixColumns(state, INVERSE_MIX_COLUMNS_MATRIX);
            }// 逆列混合
            state = rowShift(state);// 逆行移位
            state = substituteBytes(state, INVERSE_S_BOX);// 逆字节代换
            state = addRoundKey(state, roundKey[i]);// 轮密钥加
        }
        return state;
    }

    public static int encryptsame(int plaintext, int key){//key加密两次
        int state = plaintext;
        int middlestate=encrypt1(state,key);//key加密
        state=encrypt1(middlestate,key);//key加密
        return state;
    }

    public static int decryptsame(int ciphertext, int key){//key解密两次
        int state = ciphertext;
        int middlestate=decrypt1(state,key);
        state=decrypt1(middlestate,key);
        return state;
    }

    public static int encrypt2(int plaintext, int key){//key1加密，key2解密
        int key1 = key >> 16 & 0xFFFF;
        int key2 = key & 0xFFFF;
        int state = plaintext;
        int middlestate=encrypt1(state,key1);//key1加密
        state=decrypt1(middlestate,key2);//key2解密
        return state;
    }

    public static int decrypt2(int ciphertext, int key){//key2加密，key1解密
        int key1 = key >> 16 & 0xFFFF;
        int key2 = key & 0xFFFF;
        int state = ciphertext;
        int middlestate=encrypt1(state,key2);
        state=decrypt1(middlestate,key1);
        return state;
    }

    public static int encrypt3(int plaintext, int key){//key1加密，key2解密，key1加密
        int key1 = key >> 16 & 0xFFFF;
        int key2 = key & 0xFFFF;
        int state = plaintext;
        int middlestate1=encrypt1(state,key1);//key1加密
        int middlestate2=decrypt1(middlestate1,key2);//key2解密
        state=encrypt1(middlestate2,key1);//key1加密
        return state;
    }

    public static int decrypt3(int plaintext, int key){//key1解密，key2加密，key1解密
        int key1 = key >> 16 & 0xFFFF;
        int key2 = key & 0xFFFF;
        int state = plaintext;
        int middlestate1=decrypt1(state,key1);//key1解密
        int middlestate2=encrypt1(middlestate1,key2);//key2加密
        state=decrypt1(middlestate2,key1);//key1解密
        return state;
    }

    public static int encrypt4(int plaintext, int key){//key1加密，key2解密，key3加密
        int key1 = key >> 32 & 0xFFFF;
        int key2 = key >> 16 & 0xFFFF;
        int key3 = key & 0xFFFF;
        int state = plaintext;
        int middlestate1=encrypt1(state,key1);//key1加密
        int middlestate2=decrypt1(middlestate1,key2);//key2解密
        state=encrypt1(middlestate2,key3);//key3加密
        return state;
    }
    public static int decrypt4(int plaintext, int key){//key3解密，key2加密，key1解密
        int key1 = key >> 32 & 0xFFFF;
        int key2 = key >> 16 & 0xFFFF;
        int key3 = key & 0xFFFF;
        int state = plaintext;
        int middlestate1=decrypt1(state,key3);//key3解密
        int middlestate2=encrypt1(middlestate1,key2);//key2加密
        state=decrypt1(middlestate2,key1);//key1解密
        return state;
    }
}