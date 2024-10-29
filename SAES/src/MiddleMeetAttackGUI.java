import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class MiddleMeetAttackGUI extends JFrame {

    private List<PCPair> pairs;//明密文对存储列表
    private JComboBox<Integer> pairCountComboBox;//明密文对数量
    private JPanel inputPairsPanel;//输入明密文对的面板
    private JTextArea resultArea;//显示密钥
    private Timer timer;//时间戳计时器
    private long startTime;//开始时间
    private JLabel timerLabel;//显示时间
    private JComboBox<String> operationComboBox;//下拉框
    private Font font = new Font("宋体",Font.PLAIN,14); // 设置字体


    public MiddleMeetAttackGUI() {
        setTitle("S-AES中间相遇攻击");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pairs = new ArrayList<>();
        JPanel mainPanel = new JPanel(new BorderLayout());//主面板

        JPanel pairCountPanel = new JPanel();//明密文对数量面板
        JLabel pairCountLabel = new JLabel("选择明密文对的数量：");
        pairCountLabel.setFont(font);
        Integer[] pairCounts = {1, 2, 3, 4, 5};
        pairCountComboBox = new JComboBox<>(pairCounts);//下拉框，选择明密文对数量
        pairCountComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateInputPairsPanel();
            }
        });



        pairCountPanel.add(pairCountLabel);
        pairCountPanel.add(pairCountComboBox);


        inputPairsPanel = new JPanel();//输入明密文对面板
        inputPairsPanel.setLayout(new BoxLayout(inputPairsPanel, BoxLayout.Y_AXIS));
        JScrollPane inputPairsScrollPane = new JScrollPane(inputPairsPanel);//设计滚动栏

        JPanel buttonPanel = new JPanel();//查找面板，包含查找按钮和时间戳计时器
        JButton findKeyButton = new JButton("查找密钥");
        findKeyButton.setBackground(new Color(0x5D7599));
        findKeyButton.setForeground(Color.white);
        findKeyButton.setFont(font);
        findKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findKey();
            }
        });
        buttonPanel.add(findKeyButton,BorderLayout.CENTER);
        timerLabel = new JLabel("   已用时：   ");// 创建一个 JLabel 用于显示已用时间
        timerLabel.setFont(font);
        buttonPanel.add(timerLabel);

        resultArea = new JTextArea(5, 30);//结果显示面板
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane resultScrollPane = new JScrollPane(resultArea);

        JPanel southPanel = new JPanel(new BorderLayout());// 创建一个新的面板，使用 BorderLayout 布局，将按钮面板和结果滚动面板添加到其中
        southPanel.add(buttonPanel, BorderLayout.NORTH);
        southPanel.add(resultScrollPane, BorderLayout.CENTER);

        mainPanel.add(pairCountPanel, BorderLayout.NORTH);//向主面板增加子面板
        mainPanel.add(inputPairsScrollPane, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // 初始化计时器
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long currentTime = System.nanoTime();
                long elapsedTime = currentTime - startTime;
            }
        });
    }

    private void updateInputPairsPanel() {//更新输入明密文对的面板
        inputPairsPanel.removeAll();
        int pairCount = (int) pairCountComboBox.getSelectedItem();//获取下拉框的选择
        for (int i = 0; i < pairCount; i++) {//for循环处理多对
            JPanel pairPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTextArea plaintextTextArea = new JTextArea(1, 16);//输入明密文对文本框大小
            JTextArea ciphertextTextArea = new JTextArea(1, 16);
            plaintextTextArea.setLineWrap(true);
            plaintextTextArea.setWrapStyleWord(true);
            ciphertextTextArea.setLineWrap(true);
            ciphertextTextArea.setWrapStyleWord(true);
            JLabel t1=new JLabel(" 明文" + (i + 1) + ":");
            t1.setFont(font);
            pairPanel.add(t1);
            pairPanel.add(plaintextTextArea);
            JLabel t2=new JLabel(" 密文" + (i + 1) + ":");
            t2.setFont(font);
            pairPanel.add(t2);
            pairPanel.add(ciphertextTextArea);
            inputPairsPanel.add(pairPanel);//显示明密文对
        }
        revalidate();
        repaint();
    }

    private void findKey() {
        pairs.clear();
        int pairCount = (int) pairCountComboBox.getSelectedItem();
        List<String> foundKeys = new ArrayList<>(); // 用于存储找到的密钥
        List<String> foundKeys1 = new ArrayList<>(); // 用于存储找到的密钥key1
        List<String> foundKeys2 = new ArrayList<>(); // 用于存储找到的密钥key2
        for (int i = 0; i < pairCount; i++) {//将每个明密文对存储在plaintext，ciphertext
            Component[] pairComponents = ((JPanel) inputPairsPanel.getComponent(i)).getComponents();
            if (pairComponents.length >= 2 && pairComponents[1] instanceof JTextArea && pairComponents[3] instanceof JTextArea) {
                JTextArea plaintextTextArea = (JTextArea) pairComponents[1];
                JTextArea ciphertextTextArea = (JTextArea) pairComponents[3];
                String plaintextStr = plaintextTextArea.getText().trim();
                String ciphertextStr = ciphertextTextArea.getText().trim();
                if (!plaintextStr.isEmpty() && !ciphertextStr.isEmpty()) {
                    int[] plaintext = stringToArray(plaintextStr);
                    int[] ciphertext = stringToArray(ciphertextStr);
                    if (plaintext.length == 16 && ciphertext.length == 16) {
                        PCPair pair = new PCPair(plaintext, ciphertext);//按照明文和对应密文的方式存储，便于后续处理
                        pairs.add(pair);
                    } else {
                        JOptionPane.showMessageDialog(this, "每个明文和密文对必须是16位二进制数字。");
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "请填写所有明文和密文对。");
                    return;
                }
            }
        }
        if (pairs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请添加至少一个明文和密文对。");
            return;
        }

        // 在开始查询之前启动计时器
        startTime = System.nanoTime();
        timer.start();


            // 根据所选的加密选项执行加密

                // 创建一个线程池，你可以根据需要设置线程数
                int numThreads = Runtime.getRuntime().availableProcessors(); // 获取可用处理器核心数
                ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

                for (int key1 = 0; key1 < 65536; key1++) {
                    final int finalKey1 = key1;
                    executorService.execute(() -> {
                        for (int key2 = 0; key2 < 65536; key2++) {
                            int[] keyBits1 = intToBits(finalKey1);
                            int keyInt1 = 0;
                            for (int ii = 0; ii < keyBits1.length; ii++) {
                                keyInt1 = (keyInt1 << 1) | keyBits1[ii];
                            }
                            int[] keyBits2 = intToBits(key2);
                            int keyInt2 = 0;
                            for (int ii = 0; ii < keyBits2.length; ii++) {
                                keyInt2 = (keyInt2 << 1) | keyBits2[ii];
                            }
                            boolean foundKey = true;

                            for (PCPair pair : pairs) {
                                int a = 0;
                                for (int bit : pair.getCiphertext()) {
                                    a = (a << 1) | bit;
                                }
                                int b = 0;
                                for (int bit : pair.getPlaintext()) {
                                    b = (b << 1) | bit;
                                }

                                int decryptedValue = SAES.encrypt1(a, keyInt2);
                                int encryptedValue = SAES.encrypt1(b, keyInt1);

                                if (encryptedValue != decryptedValue) {
                                    foundKey = false;
                                    break;
                                }
                            }

                            if (foundKey) {
                                String keyString1 = arrayToString(keyBits1);
                                foundKeys1.add(keyString1);
                                String keyString2 = arrayToString(keyBits2);
                                foundKeys2.add(keyString2);
                            }
                        }
                    });
                }

                executorService.shutdown();
                try {
                    executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



                // 在查询完毕后停止计时器
                timer.stop();
                // 计算已用时间
                long elapsedTime = System.nanoTime() - startTime;
                // 更新 timerLabel 上的时间
                timerLabel.setText("   已用时：" + formatTime(elapsedTime));

                if (!(foundKeys1.isEmpty()||foundKeys2.isEmpty())) {// 如果有找到的密钥，将它们显示在结果区域中
                    StringBuilder resultText = new StringBuilder("        找到以下密钥：\n");
                    for (int i = 0; i < Math.min(foundKeys1.size(), foundKeys2.size()); i++) {
                        String key1 = foundKeys1.get(i);
                        String key2 = foundKeys2.get(i);
                        resultText.append("        key1: " + key1 + ", key2: " + key2).append("\n");
                    }
                    resultArea.setText(resultText.toString());
                } else {
                    resultArea.setText("未找到密钥。");
                }




    }

    private String formatTime(long nanos) {
        long micros = nanos / 1000;
        nanos = nanos % 1000;
        long millis = micros / 1000;
        micros = micros % 1000;
        long seconds = millis / 1000;
        millis = millis % 1000;
        return String.format("%03d s %02d ms %02d μs %03d ns",seconds, millis,micros,nanos);
    }


    private int[] stringToArray(String str) {//把字符串转化为数组
        int[] result = new int[str.length()];
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '0' || c == '1') {
                result[i] = Character.getNumericValue(c);
            } else {
                return new int[0];
            }
        }
        return result;
    }

    private String arrayToString(int[] arr) {//把数组转化为字符串
        StringBuilder sb = new StringBuilder();
        for (int bit : arr) {
            sb.append(bit);
        }
        return sb.toString();
    }

    private int[] intToBits(int n) {//把整型转化为二进制位
        int[] bits = new int[16];
        for (int i = 0; i < 16; i++) {
            bits[i] = (n >> i) & 1;
        }
        return bits;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MiddleMeetAttackGUI gui = new MiddleMeetAttackGUI();
                gui.setLocationRelativeTo(null);//居中显示
                gui.setVisible(true);
            }
        });
    }
}

class PCPair {//用于存储明密文对
    private int[] plaintext;
    private int[] ciphertext;
    public PCPair(int[] plaintext, int[] ciphertext) {
        this.plaintext = plaintext;
        this.ciphertext = ciphertext;
    }
    public int[] getPlaintext() {
        return plaintext;
    }
    public int[] getCiphertext() {
        return ciphertext;
    }
}

