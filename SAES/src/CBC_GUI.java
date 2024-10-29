import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CBC_GUI extends JFrame {

    private JTextArea inputContent; // 输入文本
    private JTextField keyContent; // 密钥输入框
    private JTextArea resultContent; // 结果
    private JRadioButton binaryInputRadio; // 二进制输入单选按钮
    private JRadioButton asciiInputRadio; // ASCII字符串输入单选按钮
    private JRadioButton encryptRadio; // 加密单选按钮
    private JRadioButton decryptRadio; // 解密单选按钮

    public CBC_GUI(int IV) {
        setTitle("CBC模式加解密"); // 设置窗口标题
        setSize(500, 350); // 设置窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭窗口时的默认操作

        JPanel mainPanel = new JPanel(new GridLayout(7, 1)); // 创建主面板，采用7行1列的网格布局
        mainPanel.setBackground(new Color(0xf0f8ff)); // 设置主面板背景颜色

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 输入面板，采用左对齐流式布局
        Font font = new Font("微软雅黑", Font.PLAIN, 14); // 设置字体
        JLabel inputLabel = new JLabel("输入："); // 输入标签
        inputLabel.setFont(font); // 设置字体
        inputContent = new JTextArea(2, 35); // 输入文本区域，2行35列
        inputContent.setLineWrap(true); // 自动换行
        JScrollPane inputScrollPane = new JScrollPane(inputContent); // 创建带滚动条的输入文本区域
        inputPanel.add(inputLabel); // 添加输入标签
        inputPanel.add(inputScrollPane); // 添加输入文本区域
        inputPanel.setBackground(new Color(0xe6e6e6)); // 设置背景颜色

        JPanel inputTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 输入类型面板，采用左对齐流式布局
        binaryInputRadio = new JRadioButton("二进制"); // 二进制输入单选按钮
        binaryInputRadio.setFont(font); // 设置字体
        asciiInputRadio = new JRadioButton("ASCII字符串"); // ASCII字符串输入单选按钮
        asciiInputRadio.setFont(font);
        ButtonGroup inputTypeGroup = new ButtonGroup(); // 创建输入类型按钮组
        inputTypeGroup.add(binaryInputRadio); // 将二进制单选按钮添加到按钮组
        inputTypeGroup.add(asciiInputRadio); // 将ASCII字符串单选按钮添加到按钮组
        inputTypePanel.add(binaryInputRadio);
        inputTypePanel.add(asciiInputRadio);
        binaryInputRadio.setBackground(new Color(0xe6e6e6)); // 设置背景颜色
        asciiInputRadio.setBackground(new Color(0xe6e6e6));
        inputTypePanel.setBackground(new Color(0xe6e6e6));

        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 密钥面板，采用左对齐流式布局
        JLabel keyLabel = new JLabel("密钥(16位)："); // 密钥标签
        keyLabel.setFont(font);
        keyContent = new JTextField(16); // 密钥输入框，16列
        keyPanel.add(keyLabel); // 添加密钥标签
        keyPanel.add(keyContent); // 添加密钥输入框
        keyPanel.setBackground(new Color(0xe6e6e6)); // 设置背景颜色

        JPanel operationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 操作面板，采用左对齐流式布局
        encryptRadio = new JRadioButton("加密"); // 加密单选按钮
        encryptRadio.setFont(font); // 设置字体
        decryptRadio = new JRadioButton("解密"); // 解密单选按钮
        decryptRadio.setFont(font); // 设置字体
        ButtonGroup operationGroup = new ButtonGroup(); // 创建操作类型按钮组
        operationGroup.add(encryptRadio); // 将加密单选按钮添加到按钮组
        operationGroup.add(decryptRadio); // 将解密单选按钮添加到按钮组
        operationPanel.add(encryptRadio);
        operationPanel.add(decryptRadio);

        // 添加IV
        String IVBinary = Integer.toBinaryString(IV);
        String IVText = "      IV:  " + IVBinary;
        JLabel ivLabel = new JLabel(IVText);
        ivLabel.setFont(font); // 设置字体
        inputTypePanel.add(ivLabel);

        encryptRadio.setBackground(new Color(0xe6e6e6)); // 设置背景颜色
        decryptRadio.setBackground(new Color(0xe6e6e6));
        operationPanel.setBackground(new Color(0xe6e6e6));

        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 结果面板，采用左对齐流式布局
        JLabel resultLabel = new JLabel("结果："); // 结果标签
        resultLabel.setFont(font); // 设置字体
        resultContent = new JTextArea(2, 35); // 结果文本区域，2行35列
        resultContent.setLineWrap(true); // 自动换行
        resultContent.setEditable(false); // 不可编辑

        JScrollPane resultScrollPane = new JScrollPane(resultContent); // 创建带滚动条的结果文本区域
        resultPanel.add(resultLabel); // 添加结果标签
        resultPanel.add(resultScrollPane); // 添加结果文本区域
        resultPanel.setBackground(new Color(0xe6e6e6)); // 设置背景颜色

        mainPanel.add(inputPanel); // 将输入面板添加到主面板
        mainPanel.add(inputTypePanel); // 将输入类型面板添加到主面板
        mainPanel.add(keyPanel); // 将密钥面板添加到主面板
        mainPanel.add(operationPanel); // 将操作面板添加到主面板
        mainPanel.add(resultPanel); // 将结果面板添加到主面板

        JButton executeButton = new JButton("开始执行"); // 创建执行按钮
        executeButton.setFont(font);
        executeButton.setBackground(new Color(0x5D7599));
        executeButton.setForeground(Color.white); // 设置前景颜色
        executeButton.setBorder(BorderFactory.createLineBorder(new Color(0x5D7599))); // 设置边框颜色
        executeButton.setPreferredSize(new Dimension(150, 40)); // 设置按钮大小
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handle(IV); // 执行操作方法
            }
        });
        mainPanel.add(executeButton); // 添加执行按钮到主面板
        add(mainPanel, BorderLayout.CENTER); // 将主面板添加到窗口中心位置

        setLocationRelativeTo(null); // 窗口居中显示
    }

    private void handle(int iv) {
        String input = inputContent.getText(); // 获取输入文本
        String key = keyContent.getText(); // 获取密钥文本
        String result = ""; // 存储结果的字符串

        List<Integer> inputBits = new ArrayList<>();
        if (asciiInputRadio.isSelected()) {
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                int asciiValue = (int) c;
                for (int j = 15; j >= 0; j--) {
                    inputBits.add((asciiValue >> j) & 1); // 将ASCII字符转换为二进制位并添加到列表
                }
            }
        } else if (binaryInputRadio.isSelected()) {
            input = input.replaceAll("\\s+", "");  // 移除空白字符
            if (!input.matches("[01]+")) {
                JOptionPane.showMessageDialog(this, "请输入有效的二进制数据。"); // 弹出提示框
                return;
            }
            for (int i = 0; i < input.length(); i++) {
                inputBits.add(Integer.parseInt(String.valueOf(input.charAt(i)))); // 将二进制字符串转换为整数列表
            }
        }

        if (inputBits.size() % 16 != 0) {
            JOptionPane.showMessageDialog(this, "输入数据的长度必须是16的倍数。"); // 弹出提示框
            return;
        }

        List<Integer> outputBits = new ArrayList<>();
        for (int i = 0; i < inputBits.size(); i += 16) { // 每次处理16位
            List<Integer> block = inputBits.subList(i, i + 16);
            int a = 0;
            for (int bit : block) {
                a = (a << 1) | bit;
            }
            int[] resultArray = {0};
            if (key.length() != 16) {
                JOptionPane.showMessageDialog(this, "请输入正确的16位密钥。"); // 弹出提示框
                return;
            }
            if (encryptRadio.isSelected()) { // 选择加密
                int[] keyBits = new int[16];
                for (int ii = 0; ii < 16; ii++) {
                    keyBits[ii] = Integer.parseInt(String.valueOf(key.charAt(ii))); // 将密钥字符串转换为整数数组
                }
                int keyInt = 0;
                for (int ii = 0; ii < keyBits.length; ii++) {
                    keyInt = (keyInt << 1) | keyBits[ii];
                }
                String encryptedValue = CBC.encrypt_CBC(input, keyInt, iv);
                resultArray = new int[input.length()];
                for (int ii = 0; ii < input.length(); ii++) {
                    char c = encryptedValue.charAt(ii);
                    resultArray[ii] = Character.getNumericValue(c);
                }

            } else if (decryptRadio.isSelected()) { // 选择解密
                int[] keyBits = new int[16];
                for (int ii = 0; ii < 16; ii++) {
                    keyBits[ii] = Integer.parseInt(String.valueOf(key.charAt(ii))); // 将密钥字符串转换为整数数组
                }
                int keyInt = 0;
                for (int ii = 0; ii < keyBits.length; ii++) {
                    keyInt = (keyInt << 1) | keyBits[ii];
                }
                String decryptedValue = CBC.decrypt_CBC(input, keyInt, iv);
                resultArray = new int[input.length()];
                for (int ii = 0; ii < input.length(); ii++) {
                    char c = decryptedValue.charAt(ii);
                    resultArray[ii] = Character.getNumericValue(c);
                }

            } else {
                JOptionPane.showMessageDialog(this, "请选择加密或解密操作。"); // 弹出提示框
                return;
            }
            for (int value : resultArray) {
                outputBits.add(value);
            }
        }

        if (asciiInputRadio.isSelected()) {
            result = bitsToAsciiString(outputBits); // 将二进制位转换为ASCII字符串
        } else if (binaryInputRadio.isSelected()) {
            result = bitsToString(outputBits); // 将二进制位转换为二进制字符串
        }

        resultContent.setText(result); // 在结果文本区域中显示结果
    }

    private String bitsToString(List<Integer> bits) { // 将二进制位转换为二进制字符串
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < bits.size(); i++) {
            result.append(bits.get(i));
        }
        return result.toString();
    }

    private String bitsToAsciiString(List<Integer> bits) { // 二进制转换成ASCII字符串
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < bits.size(); i += 16) {
            int asciiValue = 0;
            for (int j = 0; j < 16; j++) {
                asciiValue = (asciiValue << 1) | bits.get(i + j);
            }
            result.append((char) asciiValue);
        }
        return result.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int IV = CBC.generateIV();
                CBC_GUI gui = new CBC_GUI(IV);
                gui.setLocationRelativeTo(null); // 居中显示
                gui.setVisible(true); // 设置窗口可见
            }
        });
    }
}