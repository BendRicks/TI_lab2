package edu.bsuir.ti_2lab.encrypting;

public class KeyGenerator {
    private String shiftRegister;

    public KeyGenerator(String key) {
        this.shiftRegister = key;
    }

    public byte[] getKey(int amount) {
        byte[] byteArray = new byte[amount];
        for (int i = 0; i < amount; i++){
            byteArray[i] = getKeyByte();
        }
        return byteArray;
    }

    private byte getKeyByte() {
        byte outByte = 0;
        outByte = updateKeyRegister() ? (byte) (outByte ^ 0b10000000) : outByte;
        outByte = updateKeyRegister() ? (byte) (outByte ^ 0b01000000) : outByte;
        outByte = updateKeyRegister() ? (byte) (outByte ^ 0b00100000) : outByte;
        outByte = updateKeyRegister() ? (byte) (outByte ^ 0b00010000) : outByte;
        outByte = updateKeyRegister() ? (byte) (outByte ^ 0b00001000) : outByte;
        outByte = updateKeyRegister() ? (byte) (outByte ^ 0b00000100) : outByte;
        outByte = updateKeyRegister() ? (byte) (outByte ^ 0b00000010) : outByte;
        outByte = updateKeyRegister() ? (byte) (outByte ^ 0b00000001) : outByte;
        return outByte;
    }

    private boolean updateKeyRegister() {
        int keyLength = shiftRegister.length();
        byte[] symbols = new byte[]{
                shiftRegister.charAt(keyLength - 27) == '1' ? (byte) 1 : 0,
                shiftRegister.charAt(keyLength - 8) == '1' ? (byte) 1 : 0,
                shiftRegister.charAt(keyLength - 7) == '1' ? (byte) 1 : 0,
                shiftRegister.charAt(keyLength - 1) == '1' ? (byte) 1 : 0
        };
        char c = shiftRegister.charAt(0);
        shiftRegister = shiftRegister.substring(1, keyLength) +
                ((symbols[0] ^ symbols[1] ^ symbols[2] ^ symbols[3]) == 0 ?
                        '0' :
                        '1');
        return c == '1';
    }
}
