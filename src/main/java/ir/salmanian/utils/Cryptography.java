package ir.salmanian.utils;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

/**
 * Cryptography class is an utility class which is used to encrypt and hashing data.
 */
public class Cryptography {

    /**
     * This method is used to hash the input string.
     * @param input
     * @return the hashed string of input
     */
    public static String hash256(String input){
        return Hashing.sha256().hashString(input,StandardCharsets.UTF_8).toString();
    }
}
