package utils;

public class InputSanitizer {
    /**
     * Private constructor to prevent instantiation.
     */
    private InputSanitizer() {
        // Utility class, should not be instantiated
    }

    /**
     * Sanitizes user-provided input to prevent potential injection attacks.
     *
     * @param input           The raw user input.
     * @param maxLength       The maximum allowed length of input.
     * @param numericOnly     Whether to allow only numbers.
     * @return Sanitized and safe user input.
     * @throws IllegalArgumentException If input is null, invalid, or unsafe.
     */
    public static String sanitize(String input, int maxLength, boolean numericOnly) throws IllegalArgumentException {
        if (input == null) {
            throw new IllegalArgumentException("A entrada não pode ser nula.");
        }
        input = input.trim();
        if (input.isEmpty() || input.length() > maxLength) {
            throw new IllegalArgumentException("A entrada é inválida ou excede o comprimento permitido.");
        }
        if (numericOnly && !input.matches("\\d+")) {
            throw new IllegalArgumentException("A entrada deve conter apenas caracteres numéricos.");
        }
        if (!numericOnly && input.indexOf(';') >= 0 ||
                input.indexOf('\'') >= 0 ||
                input.indexOf('"') >= 0 ||
                input.indexOf('<') >= 0 ||
                input.indexOf('>') >= 0 ||
                input.indexOf(',') >= 0) {
            throw new IllegalArgumentException("A entrada contém caracteres inseguros.");
        }
        return input;
    }

    /**
    * Escapa entradas potencialmente inseguras para registro seguro.
    * Entrada fornecida pelo usuário.
    * Entrada com caracteres inseguros escapou.
     
     * @param input 
     * @return 
     */
    public static String escapeForLog(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}