package com.ikemba.inventrar.printer

object EscPos {
    const val INIT = "\u001B@" // Initialize printer
    const val CUT = "\u001DV\u0000" // Full cut
    fun lineFeed(n: Int = 1) = "\n".repeat(n)
    const val BOLD_ON = "\u001BE\u0001"
    const val BOLD_OFF = "\u001BE\u0000"
    const val ALIGN_LEFT = "\u001Ba\u0000"
    const val ALIGN_CENTER = "\u001Ba\u0001"
    const val ALIGN_RIGHT = "\u001Ba\u0002"
    const val NORMAL_SIZE = "\u001D!\u0000"
    const val DOUBLE_HEIGHT = "\u001D!\u0001"
    const val DOUBLE_WIDTH = "\u001D!\u0010"
    const val DOUBLE_SIZE = "\u001D!\u0011"

    // --- QR Code Commands (Common ESC/POS Sequences) ---
    // Function 165: Select QR Code Model
    // m = 49 (Model 1), 50 (Model 2 - preferred), 51 (Micro QR)
    const val QR_MODEL_2 = "\u001D(k\u0004\u0000\u0031\u0041\u0032\u0000"

    // Function 167: Set QR Code Module Size (dot size)
    // n = 1 to 16.
    fun qrSetSize(size: Byte = 4): String {
        return "\u001D(k\u0003\u0000\u0031\u0043${size.toChar()}"
    }

    // Function 169: Set QR Code Error Correction Level
    // n = 48 (L:7%), 49 (M:15%), 50 (Q:25%), 51 (H:30%)
    fun qrSetErrorCorrection(level: Byte = 49): String { // Default to Level M
        return "\u001D(k\u0003\u0000\u0031\u0045${level.toChar()}"
    }

    // Function 180: Store QR Code Data in Symbol Storage
    // m = 48 ('0')
    // d1...dk = data
    // pL pH = (length of data d1...dk) + 3
    fun qrStoreData(data: String): String {
        val dataBytes = data.toByteArray(Charsets.ISO_8859_1)
        val len = dataBytes.size + 3
        val pL = (len % 256).toByte()
        val pH = (len / 256).toByte()
        return "\u001D(k${pL.toChar()}${pH.toChar()}\u0031\u0050\u0030$data"
    }

    // Function 181: Print QR Code Symbol from Storage
    // m = 48 ('0')
    const val QR_PRINT = "\u001D(k\u0003\u0000\u0031\u0051\u0030"

    // --- Barcode Commands ---

    /**
     * Selects the print position of Human Readable Interpretation (HRI) characters.
     * GS H n
     * @param position 0: Not printed, 1: Above barcode, 2: Below barcode, 3: Both above and below.
     * Default is 2 (Below barcode).
     */
    fun barcodeSetHriPosition(position: Int = 2): String {
        if (position !in 0..3) throw IllegalArgumentException("HRI position must be between 0 and 3.")
        return "\u001DH${position.toChar()}"
    }

    /**
     * Sets the barcode height.
     * GS h n
     * @param height Height in dots. 1 to 255. Default is 162.
     */
    fun barcodeSetHeight(height: Int = 162): String {
        if (height !in 1..255) throw IllegalArgumentException("Barcode height must be between 1 and 255.")
        return "\u001Dh${height.toChar()}"
    }

    /**
     * Sets the barcode width (module width).
     * GS w n
     * @param width Width of the narrowest bar/space. Typically 2 to 6. Default is 3.
     */
    fun barcodeSetWidth(width: Int = 3): String {
        if (width !in 1..6) throw IllegalArgumentException("Barcode width (module) must be between 1 and 6.") // Some printers might support 1.
        return "\u001Dw${width.toChar()}"
    }

    /**
     * Prints a CODE128 barcode.
     * GS k m n d1...dn
     * Uses CODE128 (m=73). Automatically selects CODE B for alphanumeric data.
     * For more complex CODE128 scenarios (like using CODE A or C, or FNC characters),
     * the data string might need to be pre-formatted according to CODE128 specifications.
     * @param data The string data to encode in the barcode.
     */
    fun printCode128(data: String): String {
        if (data.isEmpty()) return "" // Or throw an exception

        // For CODE128, m = 73 (dec), which is 'I' in ASCII if used directly as a character.
        // The command format is GS k m n d1...dn where n is the length of the data.
        // To specify Code B for alphanumeric characters, data should typically start with "{B".
        // The printer's firmware interprets this.
        val code128Data = "{B$data" // Prefix for CODE B. Some printers might not need this or handle it differently.
        // Or, if your printer automatically handles CODE128 subsets or
        // expects raw data for automatic selection, you might just use `data`.
        // Check your printer manual.
        val n = code128Data.length
        if (n > 255) throw IllegalArgumentException("CODE128 data is too long (max 255 bytes for this ESC/POS command structure including prefix).")

        // GS k m n d...
        // m = 73 (CODE128)
        return "\u001Dk${73.toChar()}${n.toChar()}$code128Data"
    }

    /**
     * Prints a barcode using a specified system and data.
     * GS k m d1...dk NUL (older command form)
     * This is a more generic function if you need other barcode types.
     * You'll need to know the 'm' value for your desired barcode system.
     * @param system The 'm' value for the barcode system (e.g., 65 for UPC-A, 67 for EAN13, 69 for CODE39).
     * @param data The data to encode. Some systems have specific data format requirements.
     * Data is typically terminated by a NUL character.
     */
    fun printGenericBarcode(system: Int, data: String): String {
        if (data.isEmpty()) return ""
        if (system !in 0..255) throw IllegalArgumentException("Barcode system identifier must be between 0 and 255.")
        // GS k m d1...dk NUL
        return "\u001Dk${system.toChar()}$data\u0000"
    }

    // Example 'm' values for printGenericBarcode (check your printer manual for supported types and their 'm' values):
    // const val BARCODE_SYSTEM_UPC_A = 65
    // const val BARCODE_SYSTEM_UPC_E = 66
    // const val BARCODE_SYSTEM_EAN13 = 67
    // const val BARCODE_SYSTEM_EAN8 = 68
    // const val BARCODE_SYSTEM_CODE39 = 69
    // const val BARCODE_SYSTEM_ITF = 70
    // const val BARCODE_SYSTEM_CODABAR_NW7 = 71
    // const val BARCODE_SYSTEM_CODE93 = 72
    // const val BARCODE_SYSTEM_CODE128 = 73 // Handled by printCode128 more specifically

    // It's crucial to convert these strings to bytes using the printer's supported encoding.
    // Example: EscPos.INIT.toByteArray(Charset.forName("CP437")) or an appropriate charset.
}

/**
 * Generates ESC/POS commands to print a barcode.
 * This example uses CODE128.
 * @param barcodeData The data to be encoded in the barcode.
 * @return A string containing the ESC/POS commands.
 */
fun generateBarcodeCommands(barcodeData: String): String {
    val sb = StringBuilder()
    sb.append(EscPos.ALIGN_CENTER)           // Center the barcode

    // Configure barcode appearance (optional, defaults can be used)
    sb.append(EscPos.barcodeSetHriPosition(2)) // HRI text below barcode (default)
    sb.append(EscPos.barcodeSetHeight(80))     // Set barcode height (e.g., 80 dots)
    // Default in EscPos is 162, adjust as needed.
    sb.append(EscPos.barcodeSetWidth(2))       // Set barcode module width (e.g., 2 dots)
    // Default in EscPos is 3, adjust as needed.

    // Add command to print CODE128 barcode
    sb.append(EscPos.printCode128(barcodeData))

    sb.append(EscPos.lineFeed(1))        // Add some space after the barcode
    sb.append(EscPos.ALIGN_LEFT)         // Reset alignment to left
    return sb.toString()
}

// Example of how you might use it:
// fun main() {
//     val printerCommands = StringBuilder()
//     printerCommands.append(EscPos.INIT)
//     printerCommands.append("Printing a Barcode:\n")
//     printerCommands.append(EscPos.lineFeed())
//     printerCommands.append(generateBarcodeCommands("TEST12345"))
//     printerCommands.append(EscPos.lineFeed(3))
//     printerCommands.append(EscPos.CUT)

//     // Send printerCommands.toString().toByteArray(Charset.forName("CP437")) to the printer
//     println("--- Commands ---")
//     println(printerCommands.toString().replace("\u001B", "<ESC>").replace("\u001D", "<GS>").replace("\n", "<LF>\n"))
//     println("--- End Commands ---")

//      val qrCommands = StringBuilder()
//      qrCommands.append(EscPos.INIT)
//      qrCommands.append("Printing a QR Code:\n")
//      qrCommands.append(generateQrCodeCommands("Hello QR World")) // Assuming your original function
//      qrCommands.append(EscPos.lineFeed(3))
//      qrCommands.append(EscPos.CUT)
//      // Send qrCommands.toString().toByteArray(Charset.forName("CP437")) to the printer
// }

// Original QR Code function (ensure it's in the same scope or accessible if using in main example)
private fun generateQrCodeCommands(qrData: String): String {
    val sb = StringBuilder()
    sb.append(EscPos.ALIGN_CENTER)
    sb.append(EscPos.QR_MODEL_2)
    sb.append(EscPos.qrSetSize(5))
    sb.append(EscPos.qrSetErrorCorrection(49))
    sb.append(EscPos.qrStoreData(qrData))
    sb.append(EscPos.QR_PRINT)
    sb.append(EscPos.lineFeed(1))
    sb.append(EscPos.ALIGN_LEFT)
    return sb.toString()
}