
package Usuarios;

import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class VerificadorClave {

    public static boolean verificar(char[] claveIngresada,
                                    String hashGuardado) {

        if (claveIngresada == null || hashGuardado == null) {
            return false;
        }

        PBEKeySpec especificacion = null;

        try {
            String[] partes = hashGuardado.split("\\$");

            if (partes.length != 4) {
                return false;
            }

            if (!partes[0].equals("pbkdf2_sha256")) {
                return false;
            }

            int iteraciones = Integer.parseInt(partes[1]);

            if (iteraciones < 1 || iteraciones > 2000000) {
                return false;
            }

            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[3]);

            if (sal.length != 16 || hashEsperado.length != 32) {
                return false;
            }

            especificacion = new PBEKeySpec(
                    claveIngresada,
                    sal,
                    iteraciones,
                    hashEsperado.length * 8
            );

            SecretKeyFactory fabrica =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hashCalculado =
                    fabrica.generateSecret(especificacion).getEncoded();

            return MessageDigest.isEqual(hashEsperado, hashCalculado);

        } catch (Exception e) {
            return false;

        } finally {
            if (especificacion != null) {
                especificacion.clearPassword();
            }
        }
    }
}
