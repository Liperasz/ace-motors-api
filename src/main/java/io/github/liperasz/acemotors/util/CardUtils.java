package io.github.liperasz.acemotors.util;

// Funções simples para cartão (validação simulada, sem falar com operadora - RF12)
public class CardUtils {

    private CardUtils () {

    }

    // Remove espaços e hífens: "4111 1111-1111 1111" -> "4111111111111111"
    public static String onlyDigits(String value) {
        return value == null ? "" : value.replaceAll("[\\s-]", "");
    }

    // Número de cartão válido: 13 a 19 dígitos e passa no algoritmo de Luhn
    public static boolean isValidCardNumber(String cardNumber) {
        String digits = onlyDigits(cardNumber);

        if (!digits.matches("\\d{13,19}")) {
            return false;
        }

        // Algoritmo de Luhn: da direita para a esquerda, dobra um dígito sim, outro não
        int sum = 0;
        boolean doubleIt = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int n = digits.charAt(i) - '0';
            if (doubleIt) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            doubleIt = !doubleIt;
        }

        return sum % 10 == 0;
    }

    // Guarda só os 4 últimos dígitos: "**** 1111"
    public static String maskCard(String cardNumber) {
        String digits = onlyDigits(cardNumber);
        return "**** " + digits.substring(digits.length() - 4);
    }

    // Esconde o meio da chave PIX: "ana@email.com" -> "ana***com"
    public static String maskPixKey(String pixKey) {
        if (pixKey.length() <= 6) {
            return "***";
        }
        return pixKey.substring(0, 3) + "***" + pixKey.substring(pixKey.length() - 3);
    }
}
