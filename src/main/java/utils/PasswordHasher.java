package utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    public static String hashPassword(String password) {
        try {
            // Crear una instancia de MessageDigest para el algoritmo MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Convertir el password a un array de bytes y generar el hash
            byte[] messageDigest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            // Convertir el hash a un número BigInteger
            BigInteger no = new BigInteger(1, messageDigest);
            // Convertir el número a una cadena hexadecimal
            return no.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash MD5", e);
        }
    }
}