package edu.bsuir.ti_2lab.encrypting;

import edu.bsuir.ti_2lab.gui_components.DataFileTableModel;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Encryptor {

    private final JFrame mainFrame;
    private final JLabel loadingLabel;
    private double progress = 0;
    private final File fileToOpen;
    private final File fileToSave;
    private final KeyGenerator keyGenerator;
    private int FILE_SIZE;
    private final int BUFFER_SIZE = 1024;
    private final JTable[] textAreas;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;

    public Encryptor(File fileToOpen, File fileToSave, String key, JFrame frame, JLabel loadingLabel, JTable[] textAreas) {
        this.fileToOpen = fileToOpen;
        this.fileToSave = fileToSave;
        keyGenerator = new KeyGenerator(key);
        this.textAreas = textAreas;
        this.loadingLabel = loadingLabel;
        this.mainFrame = frame;
    }

    public void encrypt() throws IOException {

        Thread encryptionThread = new Thread(() -> {
            try {
                bis = new BufferedInputStream(new FileInputStream(fileToOpen));
                bos = new BufferedOutputStream(new FileOutputStream(fileToSave));
                BufferedWriter srcFile = new BufferedWriter(new FileWriter("bin\\srcFile.txt"));
                BufferedWriter keyFile = new BufferedWriter(new FileWriter("bin\\keyFile.txt"));
                BufferedWriter encFile = new BufferedWriter(new FileWriter("bin\\encFile.txt"));
                int row = 0;
                FILE_SIZE = bis.available();
                while (bis.available() > 0) {
                    final int loopRow = row;
                    int bufferSize = Math.min(bis.available(), BUFFER_SIZE);
                    byte[] buffer = new byte[bufferSize];
                    bis.read(buffer);
                    Thread srcThread = new Thread(() -> {
                        {
                            StringBuilder str = new StringBuilder();
                            for (int i = 0; i < bufferSize; i++) {
                                str.append(String.format("%8s", Integer.toBinaryString(buffer[i] & 0xFF)).replace(' ', '0')).append(" ");
                                if ((i + 1 - loopRow) % 4 == 0) {
                                    str.append("\n");
                                }
                            }
                            try {
                                srcFile.write(str.toString());
                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }
                        }
                    });
                    srcThread.start();

                    byte[] keyBuffer = keyGenerator.getKey(bufferSize);

                    Thread keyThread = new Thread(() -> {
                        StringBuilder str = new StringBuilder();
                        for (int i = 0; i < bufferSize; i++) {
                            str.append(String.format("%8s", Integer.toBinaryString(keyBuffer[i] & 0xFF)).replace(' ', '0')).append(" ");
                            if ((i + 1 - loopRow) % 4 == 0) {
                                str.append("\n");
                            }
                        }
                        try {
                            keyFile.write(str.toString());
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    });
                    keyThread.start();

                    byte[] encBuffer = new byte[bufferSize];

                    for (int i = 0; i < bufferSize; i++) {
                        encBuffer[i] = (byte) ((buffer[i] & 0xFF) ^ (keyBuffer[i] & 0xFF));
                    }

                    Thread encThread = new Thread(() -> {
                        StringBuilder str = new StringBuilder();
                        for (int i = 0; i < bufferSize; i++) {
                            str.append(String.format("%8s", Integer.toBinaryString(encBuffer[i] & 0xFF)).replace(' ', '0')).append(" ");
                            if ((i + 1 - loopRow) % 4 == 0) {
                                str.append("\n");
                            }
                        }
                        try {
                            encFile.write(str.toString());
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    });
                    encThread.start();

                    try {
                        progress = (1.0 - bis.available() / (double) FILE_SIZE) * 100;
                        loadingLabel.setText("Прогресс " + String.format("%.2f", progress) + "%");
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                    bos.write(encBuffer);
                    ++row;
                    row = (row == 4) ? 0 : row;
                    srcThread.join();
                    keyThread.join();
                    encThread.join();
                }
                bis.close();
                bos.close();
                srcFile.close();
                keyFile.close();
                encFile.close();
                textAreas[0].setModel(new DataFileTableModel("Исходный файл", "bin\\srcFile.txt"));
                textAreas[1].setModel(new DataFileTableModel("Ключ", "bin\\keyFile.txt"));
                textAreas[2].setModel(new DataFileTableModel("Выходной файл", "bin\\encFile.txt"));
                JDialog alertDialog = new JDialog(mainFrame);
                alertDialog.setSize(200, 100);
                alertDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                alertDialog.setTitle("Шифрование/Дешифрование");
                JLabel alertText = new JLabel("Процесс завершен успешно");
                alertText.setHorizontalAlignment(0);
                alertDialog.add(alertText);
                alertDialog.setVisible(true);
                if (SystemTray.isSupported()) {
                    SystemTray tray = SystemTray.getSystemTray();

                    Image image = Toolkit.getDefaultToolkit().getImage("images/tray.gif");
                    TrayIcon trayIcon = new TrayIcon(image);
                    tray.add(trayIcon);
                    trayIcon.displayMessage("Шифрование", "Процесс успешно завершен.",
                            TrayIcon.MessageType.INFO);
                }
            } catch (IOException | AWTException | InterruptedException exception) {
                exception.printStackTrace();
                JDialog errorDialog = new JDialog(mainFrame);
                errorDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                errorDialog.setTitle("Шифрование");
                JLabel alertText = new JLabel("Произошла ошибка...");
                alertText.setHorizontalAlignment(0);
                errorDialog.add(alertText);
                errorDialog.setVisible(true);
            }
        });
        encryptionThread.start();
    }


}
