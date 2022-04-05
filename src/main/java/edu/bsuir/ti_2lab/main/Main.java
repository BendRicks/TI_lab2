package edu.bsuir.ti_2lab.main;

import edu.bsuir.ti_2lab.encrypting.Encryptor;
import edu.bsuir.ti_2lab.gui_components.DataFileTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Encrypting");
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        Container container = frame.getContentPane();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(900, 500);
        panel.setBackground(Color.LIGHT_GRAY);
        container.add(panel);

        GridLayout gridLayout = new GridLayout();
        gridLayout.setHgap(10);
        gridLayout.setVgap(10);
        JPanel textFields = new JPanel(gridLayout);
        panel.add(textFields, BorderLayout.CENTER);
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel,  BoxLayout.LINE_AXIS));
        panel.add(controlPanel, BorderLayout.NORTH);

        JTable sourceFileArea = new JTable();
        sourceFileArea.setBorder(BorderFactory.createLineBorder(Color.black));
        sourceFileArea.setBackground(Color.white);
        JScrollPane sourceFileAreaScroll = new JScrollPane (sourceFileArea);
        sourceFileAreaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sourceFileAreaScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JTable keyArea = new JTable();
        keyArea.setBorder(BorderFactory.createLineBorder(Color.black));
        keyArea.setBackground(Color.white);
        JScrollPane keyAreaScroll = new JScrollPane (keyArea);
        keyAreaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        keyAreaScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JTable encryptedFileArea = new JTable();
        encryptedFileArea.setBorder(BorderFactory.createLineBorder(Color.black));
        encryptedFileArea.setBackground(Color.white);
        JScrollPane encryptedFileAreaScroll = new JScrollPane (encryptedFileArea);
        encryptedFileAreaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        encryptedFileAreaScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        textFields.add(sourceFileAreaScroll);
        textFields.add(keyAreaScroll);
        textFields.add(encryptedFileAreaScroll);

        JPanel keyPanel = new JPanel();
        JPanel textFieldPanel = new JPanel(new BorderLayout());
        JTextField keyTextField = new JTextField(20);
        keyTextField.setText("111111111111111111111111111");
        keyTextField.setHorizontalAlignment(SwingConstants.CENTER);
        keyTextField.setTransferHandler(null);
        keyTextField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        keyTextField.setSize(300, keyTextField.getHeight());
        JLabel keyStatusLabel = new JLabel("Ключ принят (Длина: 27)");
        keyTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                boolean invalidCharacter;
                String alphabet = "01";
                char c = e.getKeyChar();
                String str = String.valueOf(c);
                invalidCharacter = (!alphabet.contains(str) && (c != KeyEvent.VK_BACK_SPACE));
                int textLength = keyTextField.getText().length() + (c == KeyEvent.VK_BACK_SPACE ?
                        0 :
                        alphabet.contains(str) ?
                                1 :
                                0);
                int left = 27 - textLength;
                if (left <= 0) {
                    keyStatusLabel.setText("Ключ принят (Длина: " + textLength + ")");
                } else {
                    keyStatusLabel.setText("Осталось ввести " + left + " символов");
                }
                if (invalidCharacter){
                    e.consume();
                }
            }
        });
        textFieldPanel.add(keyTextField, BorderLayout.CENTER);
        keyPanel.add(textFieldPanel);
        keyPanel.add(keyStatusLabel);

        JLabel progressLabel = new JLabel("Прогресс 0%");
        progressLabel.setHorizontalAlignment(0);

        JPanel filePanel = new JPanel(new FlowLayout());
        JLabel infoLabel = new JLabel("Выберите файлы");
        JButton processButton = new JButton("Открыть/Сохранить");
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (keyTextField.getText().length() >= 27) {
                    progressLabel.setText("Прогресс 0%");
                    JFileChooser openFileChooser = new JFileChooser();
                    int openFileOption = openFileChooser.showOpenDialog(frame);
                    if(openFileOption == JFileChooser.APPROVE_OPTION){
                        File fileToOpen = openFileChooser.getSelectedFile();
                        if (fileToOpen != null){
                            JFileChooser saveFileChooser = new JFileChooser();
                            int saveFileOption = saveFileChooser.showSaveDialog(frame);
                            if(saveFileOption == JFileChooser.APPROVE_OPTION) {
                                File fileToSave = saveFileChooser.getSelectedFile();
                                if (fileToSave != null){
                                    Encryptor encryptor = new Encryptor(fileToOpen, fileToSave,
                                            keyTextField.getText(), frame, progressLabel,
                                            new JTable[] {sourceFileArea, keyArea, encryptedFileArea});
                                    try {
                                        encryptor.encrypt();
                                    } catch (IOException exception){
                                        callDialog("Шифрование", "Произошла ошибка");
                                    }
                                } else {
                                    callDialog("Выходной файл", "Не удалось определить файл");
                                }
                            } else {
                                callDialog("Выходной файл", "Не удалось определить файл");
                            }
                        } else {
                            callDialog("Исходный файл", "Не удалось определить файл");
                        }
                    }else{
                        callDialog("Исходный файл", "Не удалось определить файл");
                    }
                } else {
                    callDialog("Ключ", "Не валидный ключ!");
                }
            }

            private void callDialog(String title, String msg){
                JDialog alert = new JDialog(frame);
                alert.setSize(200, 100);
                alert.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                alert.setTitle(title);
                JLabel alertText = new JLabel(msg);
                alertText.setHorizontalAlignment(0);
                alert.add(alertText);
                alert.setVisible(true);
            }
        });
        filePanel.add(infoLabel);
        filePanel.add(processButton);


        controlPanel.add(keyPanel, BorderLayout.WEST);
        controlPanel.add(progressLabel, BorderLayout.CENTER);
        controlPanel.add(filePanel, BorderLayout.EAST);

        frame.setVisible(true);
    }

}
