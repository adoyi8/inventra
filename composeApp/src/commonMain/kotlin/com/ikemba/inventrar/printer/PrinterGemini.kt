import com.ikemba.inventrar.cart.data.mappers.toReceipt
import com.ikemba.inventrar.cart.presentation.ReceiptModel
import com.ikemba.inventrar.dashboard.utils.Util
import com.ikemba.inventrar.dashboard.utils.Util.currencyFormat
import com.ikemba.inventrar.printer.EscPos
import com.ikemba.inventrar.printer.generateBarcodeCommands
import org.usb4java.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import java.nio.charset.Charset
import kotlin.experimental.and

// --- Data Classes for Receipt Structure (same as before) ---
data class ReceiptItem(
    val name: String,
    val price: Double,
    val quantity: Int
) {
    val total: Double
        get() = price * quantity
}

data class Receipt(
    val orderNumber: String,
    val invoiceNumber: String,
    val dateTime: String,
    val items: List<ReceiptItem>,
    val discount: Double = 0.0,
    val taxRate: Double = 0.075,
    val qrCodeData: String? = null // Added for QR Code
) {
    val subtotal: Double
        get() = items.sumOf { it.total }

    val taxAmount: Double
        get() = subtotal * taxRate

    val totalAmount: Double
        get() = subtotal - discount + taxAmount
}

// --- ESC/POS Command Placeholders ---


// --- Receipt Formatter ---
class ReceiptFormatter(private val paperWidthChars: Int = 200) { // Adjust for your printer
    private fun center(text: String): String {
        val padding = (paperWidthChars - text.length) / 2
        return " ".repeat(maxOf(0, padding)) + text
    }

    private fun twoColumn(left: String, right: String): String {
        val spaces = paperWidthChars - left.length - right.length
        return left + " ".repeat(maxOf(1, spaces)) + right
    }

    private fun generateQrCodeCommands(qrData: String): String {
        val sb = StringBuilder()
        sb.append(EscPos.ALIGN_CENTER) // Center the QR Code
        sb.append(EscPos.QR_MODEL_2)
        sb.append(EscPos.qrSetSize(5)) // Adjust size as needed (3-8 is usually good)
        sb.append(EscPos.qrSetErrorCorrection(49)) // Level M (15%)
        sb.append(EscPos.qrStoreData(qrData))
        sb.append(EscPos.QR_PRINT)
        sb.append(EscPos.lineFeed(1)) // Add some space after QR
        sb.append(EscPos.ALIGN_LEFT) // Reset alignment
        return sb.toString()
    }


// You would need to define constants in your EscPos object/class:
// object EscPos {
//     // ... other commands
//     const val ALIGN_CENTER = "\u001BA\u0001"
//     const val ALIGN_LEFT = "\u001BA\u0000"
//     const val LINE_FEED = "\n"
//     fun lineFeed(lines: Int): String = LINE_FEED.repeat(lines)

//     // --- Barcode Specific Commands (EXAMPLES - VERIFY WITH YOUR PRINTER/LIBRARY) ---
//     // HRI position: GS H n (n=0,1,2,3)
//     fun barcodeSetHriPosition(position: Int): String = "\u001D\u0048${position.toChar()}"

//     // Barcode height: GS h n (n=1-255)
//     fun barcodeSetHeight(height: Int): String = "\u001D\u0068${height.toChar()}"

//     // Barcode width: GS w n (n=2-6 for narrow bar width)
//     fun barcodeSetWidth(width: Int): String = "\u001D\u0077${width.toChar()}"

//     // Barcode systems (examples, 'm' value for GS k m ...)
//     const val BARCODE_TYPE_UPC_A: Int = 65
//     const val BARCODE_TYPE_UPC_E: Int = 66
//     const val BARCODE_TYPE_EAN13: Int = 67
//     const val BARCODE_TYPE_EAN8: Int = 68
//     const val BARCODE_TYPE_CODE39: Int = 69
//     const val BARCODE_TYPE_ITF: Int = 70
//     const val BARCODE_TYPE_CODABAR: Int = 71
//     const val BARCODE_TYPE_CODE93: Int = 72
//     const val BARCODE_TYPE_CODE128: Int = 73

//     // Hypothetical function to select system (might not exist, could be part of printBarcode)
//     fun selectBarcodeSystem(system: Int): String = "" // Placeholder, often part of printBarcode command

//     // Function to print barcode: GS k m d1...dk NUL or GS k m n d1...dn
//     // This is highly dependent on the printer and library.
//     // A common pattern for CODE128 (m=73): <GS>kI<len_byte><{B><data>
//     // The "{B" tells the printer to use CODE128 Code B character set.
//     fun barcodePrint(data: String, type: Int = BARCODE_TYPE_CODE128): String {
//         return when (type) {
//             BARCODE_TYPE_CODE128 -> {
//                 // For CODE128, the data might need to be prefixed with a mode char like '{B'
//                 // and the length of the data (including the mode char) is often required.
//                 val code128Data = "{B$data" // Example for Code B
//                 "\u001D\u006B${type.toChar()}${code128Data.length.toChar()}$code128Data"
//             }
//             // Add other barcode types here
//             else -> "" // Or throw an error for unsupported type
//         }
//     }
//      const val NULL_TERMINATOR = "\u0000"
// }

    fun formatReceipt(receipt: ReceiptModel, qrCodeData: String?): String {
        val sb = StringBuilder()
        sb.append(EscPos.INIT)
        sb.append(EscPos.ALIGN_CENTER)
        sb.append(EscPos.DOUBLE_SIZE)
        sb.append(receipt.businessName+"\n")
        sb.append(EscPos.NORMAL_SIZE)
        sb.append( receipt.address+ "\n")
        sb.append(EscPos.BOLD_ON)
        sb.append("Thank you for your Patronage!\n")
        sb.append(EscPos.BOLD_OFF)
        sb.append(EscPos.lineFeed())
        sb.append(EscPos.ALIGN_LEFT)

        sb.append(twoColumn("Invoice: ${receipt.reference}", "${receipt.date} ${receipt.time}\n"))
        sb.append("-".repeat(paperWidthChars) + "\n")
        val itemColWidth =23
        sb.append(EscPos.ALIGN_LEFT,
            String.format(
                "%-${itemColWidth}s %-9s %-3s %-9s\n",
                "Item", "Price(${Util.CURRENCY_CODE})", "Qty", "Total(${Util.CURRENCY_CODE})"
            )
        )
        receipt.cartItems.forEach { item ->
            val priceStr = String.format("%.2f", item.price)
            val totalStr = String.format("%.2f", item.getSubTotal())
            sb.append(EscPos.ALIGN_LEFT,
                item.itemName?.let {
                    String.format(
                        "%-${itemColWidth}s %-9s %-3d %-9s\n",
                        it.take(itemColWidth),
                        priceStr,
                        item.quantity,
                        totalStr
                    )
                }
            )
        }
        sb.append("-".repeat(paperWidthChars) + "\n")
        sb.append(EscPos.ALIGN_RIGHT)
        sb.append(EscPos.ALIGN_LEFT, twoColumn("Subtotal", currencyFormat(receipt.subTotal) + "\n"))
        sb.append(EscPos.ALIGN_LEFT,twoColumn("Discount", currencyFormat(receipt.discount) + "\n"))
        sb.append(EscPos.ALIGN_LEFT,twoColumn("TAX", currencyFormat(receipt.taxTotal) + "\n"))
        sb.append(EscPos.BOLD_ON)
        sb.append(EscPos.DOUBLE_HEIGHT)
        sb.append(EscPos.ALIGN_LEFT,twoColumn("TOTAL", currencyFormat(receipt.grandTotal) + "\n"))
        sb.append(EscPos.NORMAL_SIZE)
        sb.append(EscPos.BOLD_OFF)
        sb.append(EscPos.lineFeed(1))
        sb.append(EscPos.ALIGN_LEFT,"Cashier: ${receipt.cashier}\n")
        sb.append(EscPos.lineFeed(2))

        // --- Add QR Code ---
        if (qrCodeData != null) {
            sb.append(generateBarcodeCommands(qrCodeData))
            sb.append(EscPos.ALIGN_CENTER) // Re-center for footer if needed, or remove if QR is last
            sb.append("Scan for contact or promotions!\n") // Optional text
        }
        // --- End QR Code ---

        sb.append(EscPos.ALIGN_CENTER)
        sb.append("Powered by Inventrar\n")
        sb.append("Have a nice day!\n\n\n")
        sb.append(EscPos.lineFeed(5))
        sb.append(EscPos.CUT)
        return sb.toString()
    }
}

// --- PrinterService Interface (same as before) ---
interface PrinterService {
    fun findAndOpenPrinter(nameHint: String = ""): Boolean
    fun print(data: ByteArray): Boolean
    fun closePrinter()
    fun shutdownUsb()
}

// --- Usb4Java Printer Service Implementation (same as before, ensure imports are correct) ---
class Usb4JavaPrinterService(
    private val targetVendorId: Short,
    private val targetProductId: Short,
    private val targetInterfaceNumber: Int = 0, // Often 0 for printers
    private val configurationNumber: Int = 1 // Often 1 (first configuration)
) : PrinterService {

    private var deviceHandle: DeviceHandle? = null
    private var claimedInterface: Int = -1
    private var outEndpointAddress: Byte = 0

    init {
        val result = LibUsb.init(null)
        if (result != LibUsb.SUCCESS) {
            throw LibUsbException("Unable to initialize libusb", result)
        }
        println("LibUsb initialized successfully.")
    }

    override fun findAndOpenPrinter(nameHint: String): Boolean {
        if (deviceHandle != null) {
            println("Printer already open.")
            return true
        }

        val list = DeviceList()
        var result = LibUsb.getDeviceList(null, list)
        if (result < 0) {
            LibUsb.freeDeviceList(list, true) // Clean up list
            throw LibUsbException("Unable to get device list", result)
        }
        println("Found $result USB devices.")

        try {
            for (device in list) {
                val descriptor = DeviceDescriptor()
                result = LibUsb.getDeviceDescriptor(device, descriptor)
                if (result != LibUsb.SUCCESS) {
                    System.err.println("Failed to read device descriptor for a device: ${LibUsb.strError(result)}")
                    continue // Skip this device
                }

                println("Checking device: VID=0x%04x, PID=0x%04x".format(descriptor.idVendor(), descriptor.idProduct()))
                if (descriptor.idVendor() == targetVendorId && descriptor.idProduct() == targetProductId) {
                    println("Target printer found: VID=0x%04x, PID=0x%04x".format(targetVendorId, targetProductId))
                    val handle = DeviceHandle()
                    result = LibUsb.open(device, handle)
                    if (result != LibUsb.SUCCESS) {
                        System.err.println("Unable to open USB device: ${LibUsb.strError(result)}. Try Zadig on Windows or check permissions.")
                        continue // Try next device if open fails
                    }
                    this.deviceHandle = handle
                    println("USB Device opened successfully.")

                    // Detach kernel driver if active (important on Linux/macOS)
                    val kernelDriverActive = LibUsb.kernelDriverActive(this.deviceHandle, targetInterfaceNumber)
                    if (kernelDriverActive == 1) { // 1 means active
                        println("Kernel driver active on interface $targetInterfaceNumber. Detaching...")
                        result = LibUsb.detachKernelDriver(this.deviceHandle, targetInterfaceNumber)
                        if (result != LibUsb.SUCCESS) {
                            closePrinter() // Clean up
                            throw LibUsbException("Unable to detach kernel driver: ${LibUsb.strError(result)}", result)
                        }
                        println("Kernel driver detached successfully.")
                    } else if (kernelDriverActive < 0 && kernelDriverActive != LibUsb.ERROR_NOT_SUPPORTED) {
                        System.err.println("Error checking kernel driver status: ${LibUsb.strError(kernelDriverActive)}")
                    }


                    result = LibUsb.claimInterface(this.deviceHandle, targetInterfaceNumber)
                    if (result != LibUsb.SUCCESS) {
                        closePrinter() // Clean up
                        throw LibUsbException("Unable to claim interface $targetInterfaceNumber: ${LibUsb.strError(result)}", result)
                    }
                    this.claimedInterface = targetInterfaceNumber
                    println("Interface $targetInterfaceNumber claimed successfully.")

                    // Find the OUT endpoint
                    val configDescriptor = ConfigDescriptor()
                    result = LibUsb.getActiveConfigDescriptor(device, configDescriptor)
                    if (result != LibUsb.SUCCESS) throw LibUsbException("Unable to get active config descriptor", result)

                    try {
                        if (configDescriptor.bNumInterfaces() <= targetInterfaceNumber) {
                            throw LibUsbException("Interface $targetInterfaceNumber not found in config.", LibUsb.ERROR_OTHER)
                        }
                        val iface = configDescriptor.iface()[targetInterfaceNumber]
                        if (iface.numAltsetting() == 0) {
                            throw LibUsbException("Interface $targetInterfaceNumber has no alternate settings.", LibUsb.ERROR_OTHER)
                        }
                        val ifaceDescriptor = iface.altsetting()[0] // Assuming first alt setting

                        var foundEndpoint = false
                        for (endpointDescriptor in ifaceDescriptor.endpoint()) {
                            val isOutEndpoint = (endpointDescriptor.bEndpointAddress() and LibUsb.ENDPOINT_DIR_MASK) == LibUsb.ENDPOINT_OUT
                            val isBulkEndpoint = (endpointDescriptor.bmAttributes() and LibUsb.TRANSFER_TYPE_MASK) == LibUsb.TRANSFER_TYPE_BULK
                            println("Found endpoint: 0x%02x, OUT: $isOutEndpoint, BULK: $isBulkEndpoint".format(endpointDescriptor.bEndpointAddress().toInt() and 0xFF))

                            if (isOutEndpoint && isBulkEndpoint) {
                                this.outEndpointAddress = endpointDescriptor.bEndpointAddress()
                                println("Using Bulk OUT endpoint: 0x%02x".format(this.outEndpointAddress.toInt() and 0xFF))
                                foundEndpoint = true
                                break
                            }
                        }
                        if (!foundEndpoint) {
                            throw LibUsbException("No Bulk OUT endpoint found on interface $targetInterfaceNumber.", LibUsb.ERROR_OTHER)
                        }
                    } finally {
                        LibUsb.freeConfigDescriptor(configDescriptor)
                    }
                    return true // Printer found and configured
                }
            }
        } catch (e: LibUsbException) {
            System.err.println("UsbException during printer search/setup: ${e.message} ")
            closePrinter() // Ensure cleanup on error
            return false
        }
        finally {
            LibUsb.freeDeviceList(list, true) // Crucial: free the device list
            println("Device list freed.")
        }

        println("Target printer (VID=0x%04x, PID=0x%04x) not found.".format(targetVendorId, targetProductId))
        return false
    }

    override fun print(data: ByteArray): Boolean {
        if (deviceHandle == null || outEndpointAddress == 0.toByte()) {
            System.err.println("Printer not opened or endpoint not configured. Call findAndOpenPrinter() first.")
            return false
        }

        val buffer = ByteBuffer.allocateDirect(data.size) // Use direct buffer for libusb
        buffer.put(data)
        buffer.rewind() // Prepare for reading from buffer by libusb

        val transferred = IntBuffer.allocate(1) // To store number of bytes transferred
        val timeout = 10000L // Increased timeout to 10 seconds for QR code printing which might take longer

        println("Sending ${data.size} bytes to endpoint 0x%02x...".format(outEndpointAddress.toInt() and 0xFF))
        val result = LibUsb.bulkTransfer(deviceHandle, outEndpointAddress, buffer, transferred, timeout)

        if (result == LibUsb.SUCCESS) {
            println("Successfully sent ${transferred.get(0)} bytes to printer.")
            if (transferred.get(0) != data.size) {
                System.err.println("Warning: Not all bytes were transferred. Sent: ${transferred.get(0)}, Expected: ${data.size}")
                // For QR codes, partial transfer might still print something, or nothing.
            }
            return true
        } else {
            System.err.println("Bulk transfer failed: ${LibUsb.strError(result)} (Error code: $result)")
            // Specific error handling can be added here based on the error code
            if (result == LibUsb.ERROR_TIMEOUT) System.err.println("Transfer timed out. Printer might be busy or QR data too complex for buffer/speed.")
            if (result == LibUsb.ERROR_PIPE) System.err.println("Endpoint halted or pipe error. Printer might need reset or attention (e.g. out of paper, cover open).")
            if (result == LibUsb.ERROR_NO_DEVICE) System.err.println("Device disconnected.")
            return false
        }
    }

    override fun closePrinter() {
        if (deviceHandle != null) {
            if (claimedInterface != -1) {
                val releaseResult = LibUsb.releaseInterface(deviceHandle, claimedInterface)
                if (releaseResult != LibUsb.SUCCESS) {
                    System.err.println("Warning: Failed to release interface $claimedInterface: ${LibUsb.strError(releaseResult)}")
                } else {
                    println("Interface $claimedInterface released.")
                }
                claimedInterface = -1
            }
            LibUsb.close(deviceHandle)
            println("USB Device closed.")
            deviceHandle = null
            outEndpointAddress = 0
        }
    }

    override fun shutdownUsb() {
        closePrinter() // Ensure any open device is closed
        LibUsb.exit(null) // Release libusb context
        println("LibUsb context exited.")
    }
}


// --- Main Application Logic ---
fun printReceiptGem(receiptModel: ReceiptModel) {
    println("Compose for Desktop POS Printer Integration with usb4java")
    println("IMPORTANT: Ensure usb4java and native libusb are set up correctly.")
    println("           Replace PLACEHOLDER_VID and PLACEHOLDER_PID with your printer's actual IDs.")

    // --- !!! YOU MUST CHANGE THESE !!! ---
    // Find these using Device Manager (Windows), lsusb (Linux), or System Information (macOS)
    val printerVendorId: Short = 0x0416 // Placeholder - CHANGE THIS
    val printerProductId: Short = 0x5011 // Placeholder - CHANGE THIS
    // Common VIDs for POS printers: 0x04B8 (Epson), 0x0DD4 (Bixolon), 0x0483 (STMicroelectronics), 0x0525 (Eminent)

    val printerService: PrinterService = Usb4JavaPrinterService(
        targetVendorId = printerVendorId,
        targetProductId = printerProductId
    )

    val qrCodeEmail = receiptModel.reference

    try {
        if (printerService.findAndOpenPrinter()) {
            println("Printer found and opened successfully.")



            // 2. Format the receipt
            val formatter = ReceiptFormatter(paperWidthChars = 48) // Common for 80mm, adjust if needed
            val receiptString = formatter.formatReceipt(receiptModel, qrCodeEmail)

            println("\n--- Generated ESC/POS Style Commands (String Representation) ---")
            // Print only a snippet if it's too long
            val snippet = if (receiptString.length > 500) receiptString.substring(0, 500) + "..." else receiptString
            println(snippet)
            println("Total command length: ${receiptString.length}")
            println("--- End of Generated Commands ---\n")

            // 3. Convert to bytes using printer's supported encoding (CP437 is common for text)
            // For QR codes, the data within qrStoreData is often handled as ISO-8859-1 or similar by the printer firmware.
            // The overall command string should still be encoded with something like CP437.
            val receiptBytes = receiptString.toByteArray(Charset.forName("CP437"))
            // val receiptBytes = receiptString.toByteArray(Charset.forName("GB18030")) // Example for Chinese characters
            // val receiptBytes = receiptString.toByteArray(Charsets.UTF_8) // Less likely for older POS printers

            // 4. Print
            if (printerService.print(receiptBytes)) {
                println("Receipt sent to printer successfully.")
            } else {
                println("Failed to send receipt to printer. Check console for errors.")
            }
        } else {
            println("Could not find or open the specified printer. Check VID/PID, connections, and drivers (Zadig on Windows).")
        }
    } catch (e: LibUsbException) {
        System.err.println("A USB exception occurred: ${e.message}")
        e.printStackTrace()
    } catch (e: Exception) {
        System.err.println("An unexpected error occurred: ${e.message}")
        e.printStackTrace()
    } finally {
        println("Shutting down USB service...")
        printerService.shutdownUsb() // This calls closePrinter() and LibUsb.exit()
    }
}