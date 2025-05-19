import com.ikemba.inventrar.cart.presentation.ReceiptModel
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
    // We'll use Model 2. pLpH is for "1A20" (hex values of arguments)
    const val QR_MODEL_2 = "\u001D(k\u0004\u0000\u0031\u0041\u0032\u0000"

    // Function 167: Set QR Code Module Size (dot size)
    // n = 1 to 16. Let's use 4 for a decent size. pLpH for "1C<size>"
    fun qrSetSize(size: Byte = 4): String {
        // pL, pH, cn, fn, n
        // <pL> = 3 (fixed length for this function's parameters)
        // <pH> = 0
        // <cn> = 49 ('1')
        // <fn> = 67 ('C')
        // <n> = size (e.g., 3, 4, 5)
        return "\u001D(k\u0003\u0000\u0031\u0043${size.toChar()}"
    }

    // Function 169: Set QR Code Error Correction Level
    // n = 48 (L:7%), 49 (M:15%), 50 (Q:25%), 51 (H:30%)
    // We'll use Level M (15%). pLpH for "1E<level>"
    fun qrSetErrorCorrection(level: Byte = 49): String { // Default to Level M
        // pL, pH, cn, fn, n
        // <pL> = 3
        // <pH> = 0
        // <cn> = 49 ('1')
        // <fn> = 69 ('E')
        // <n> = level
        return "\u001D(k\u0003\u0000\u0031\u0045${level.toChar()}"
    }

    // Function 180: Store QR Code Data in Symbol Storage
    // m = 48 ('0')
    // d1...dk = data
    // pL pH = (length of data d1...dk) + 3
    fun qrStoreData(data: String): String {
        val dataBytes = data.toByteArray(Charsets.ISO_8859_1) // Use an encoding printer likely supports for QR data
        val len = dataBytes.size + 3
        val pL = (len % 256).toByte()
        val pH = (len / 256).toByte()
        // GS ( k <pL> <pH> <cn> <fn> <m> d1...dk
        // cn = 49 ('1'), fn = 80 ('P'), m = 48 ('0')
        return "\u001D(k${pL.toChar()}${pH.toChar()}\u0031\u0050\u0030$data"
    }

    // Function 181: Print QR Code Symbol from Storage
    // m = 48 ('0')
    // pLpH for "1Q0"
    const val QR_PRINT = "\u001D(k\u0003\u0000\u0031\u0051\u0030"

    // It's crucial to convert these strings to bytes using the printer's supported encoding.
    // Example: EscPos.INIT.toByteArray(Charset.forName("CP437"))
}

// --- Receipt Formatter ---
class ReceiptFormatter(private val paperWidthChars: Int = 48) { // Adjust for your printer
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

    fun formatReceipt(receipt: ReceiptModel, qrCodeData: String?): String {
        val sb = StringBuilder()
        sb.append(EscPos.INIT)
        sb.append(EscPos.ALIGN_CENTER)
        sb.append(EscPos.DOUBLE_SIZE)
        sb.append("Chidinobi Farms\n")
        sb.append(EscPos.NORMAL_SIZE)
        sb.append( receipt.address+ "\n")
        sb.append(EscPos.BOLD_ON)
        sb.append("Thank you for your Patronage!\n")
        sb.append(EscPos.BOLD_OFF)
        sb.append(EscPos.lineFeed())
        sb.append(EscPos.ALIGN_LEFT)
        sb.append("Order No: ${receipt.reference}\n")
        sb.append(twoColumn("Invoice: ${receipt.reference}", "${receipt.date} ${receipt.time}\n"))
        sb.append("-".repeat(paperWidthChars) + "\n")
        val itemColWidth = paperWidthChars - 10 - 5 - 10 - 2
        sb.append(
            String.format(
                "%-${itemColWidth}s %-8s %3s %9s\n",
                "Item", "Price(\$)", "Qty", "Total(\$)"
            )
        )
        receipt.cartItems.forEach { item ->
            val priceStr = String.format("%.2f", item.price)
            val totalStr = String.format("%.2f", item.getSubTotal())
            sb.append(
                item.itemName?.let {
                    String.format(
                        "%-${itemColWidth}s %8s %3d %9s\n",
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
        sb.append(twoColumn("Subtotal", String.format("\$%.2f", receipt.subTotal) + "\n"))
        sb.append(twoColumn("Discount", String.format("\$%.2f", receipt.discount) + "\n"))
        sb.append(twoColumn("TAX", String.format("\$%.2f", receipt.taxTotal) + "\n"))
        sb.append(EscPos.BOLD_ON)
        sb.append(EscPos.DOUBLE_HEIGHT)
        sb.append(twoColumn("TOTAL", String.format("\$%.2f", receipt.grandTotal) + "\n"))
        sb.append(EscPos.NORMAL_SIZE)
        sb.append(EscPos.BOLD_OFF)
        sb.append(EscPos.lineFeed(2))

        // --- Add QR Code ---
        if (qrCodeData != null) {
            sb.append(generateQrCodeCommands(qrCodeData))
            sb.append(EscPos.ALIGN_CENTER) // Re-center for footer if needed, or remove if QR is last
            sb.append("Scan for contact or promotions!\n") // Optional text
        }
        // --- End QR Code ---

        sb.append(EscPos.ALIGN_CENTER)
        sb.append("Powered by Inventra\n")
        sb.append("Have a nice day!\n")
        sb.append(EscPos.lineFeed(3))
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
            val formatter = ReceiptFormatter(paperWidthChars = 42) // Common for 80mm, adjust if needed
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