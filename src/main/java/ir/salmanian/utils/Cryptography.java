package ir.salmanian.utils;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class Cryptography {

    public static String hash256(String input){
        return Hashing.sha256().hashString(input,StandardCharsets.UTF_8).toString();
    }
}