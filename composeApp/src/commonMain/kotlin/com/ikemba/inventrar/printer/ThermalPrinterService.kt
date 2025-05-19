package com.ikemba.inventrar.printer

import java.io.OutputStream
import java.net.Socket
import java.io.IOException

class ThermalPrinterService(private val ipAddress: String, private val port: Int = 9100) {

    private var socket: Socket? = null
    private var outputStream: OutputStream? = null

    // ESC/POS Commands (common ones)
    companion object {
        val ESC = 0x1B.toByte()
        val GS = 0x1D.toByte()

        // Initialize printer
        val INIT = byteArrayOf(ESC, 0x40)

        // Feed paper and cut
        val FULL_CUT = byteArrayOf(GS, 0x56, 0x00) // GS V 0
        val PARTIAL_CUT = byteArrayOf(GS, 0x56, 0x01) // GS V 1

        // Text Alignment
        val ALIGN_LEFT = byteArrayOf(ESC, 0x61, 0x00)
        val ALIGN_CENTER = byteArrayOf(ESC, 0x61, 0x01)
        val ALIGN_RIGHT = byteArrayOf(ESC, 0x61, 0x02)

        // Text Styles
        val BOLD_ON = byteArrayOf(ESC, 0x45, 0x01)
        val BOLD_OFF = byteArrayOf(ESC, 0x45, 0x00)
        val DOUBLE_HEIGHT_ON = byteArrayOf(GS, 0x21, 0x10) // GS ! 10 (double height)
        val DOUBLE_WIDTH_ON = byteArrayOf(GS, 0x21, 0x20) // GS ! 20 (double width)
        val DOUBLE_HEIGHT_WIDTH_ON = byteArrayOf(GS, 0x21, 0x30) // GS ! 30 (double height & width)
        val NORMAL_TEXT = byteArrayOf(GS, 0x21, 0x00) // GS ! 00 (normal)

        // Line Feed
        val LF = 0x0A.toByte() // Line feed

        // Set character code page (e.g., for special characters if needed)
        // For example, PC437 (USA, Standard Europe)
        val CODE_PAGE_PC437 = byteArrayOf(ESC, 0x74, 0x00)

        // For images, barcodes, QR codes - these are more complex and require libraries or careful byte generation
        // Example: GS k for barcode, GS ( k for QR code
    }

    fun connect() {
        try {
            socket = Socket(ipAddress, port)
            outputStream = socket?.getOutputStream()
            println("Connected to printer at $ipAddress:$port")
        } catch (e: IOException) {
            println("Error connecting to printer: ${e.message}")
            e.printStackTrace()
            close() // Ensure resources are closed on error
        }
    }

    fun print(data: ByteArray) {
        try {
            outputStream?.write(data)
            outputStream?.flush() // Ensure data is sent immediately
        } catch (e: IOException) {
            println("Error sending data to printer: ${e.message}")
            e.printStackTrace()
            close() // Connection might be lost, try to close
        }
    }

    fun printText(text: String) {
        print(text.toByteArray())
    }

    fun printLine(text: String) {
        printText(text)
        print(byteArrayOf(LF))
    }

    fun printCommand(command: ByteArray) {
        print(command)
    }

    fun close() {
        try {
            outputStream?.close()
            socket?.close()
            println("Disconnected from printer.")
        } catch (e: IOException) {
            println("Error closing printer connection: ${e.message}")
            e.printStackTrace()
        } finally {
            outputStream = null
            socket = null
        }
    }
}
suspend fun printSampleReceipt(printerIp: String) {
    val printer = ThermalPrinterService(printerIp)
    try {
        printer.connect()

        // Initialize printer
        printer.printCommand(ThermalPrinterService.INIT)
        printer.printCommand(ThermalPrinterService.CODE_PAGE_PC437) // Set character set if needed

        // Header
        printer.printCommand(ThermalPrinterService.ALIGN_CENTER)
        printer.printCommand(ThermalPrinterService.DOUBLE_HEIGHT_WIDTH_ON)
        printer.printLine("YOUR STORE NAME")
        printer.printCommand(ThermalPrinterService.NORMAL_TEXT)
        printer.printCommand(ThermalPrinterService.ALIGN_LEFT)
        printer.printLine("----------------------------------------")
        printer.printLine("Receipt No: 123456")
        printer.printLine("----------------------------------------")

        // Items
        printer.printLine("Item Name             Qty    Price   Total")
        printer.printLine("----------------------------------------")
        printer.printLine("Product A             1      10.00   10.00")
        printer.printLine("Product B             2      5.00    10.00")
        printer.printLine("Product C             1      25.00   25.00")
        printer.printLine("----------------------------------------")

        // Totals
        printer.printCommand(ThermalPrinterService.ALIGN_RIGHT)
        printer.printCommand(ThermalPrinterService.BOLD_ON)
        printer.printLine("Subtotal:             45.00")
        printer.printLine("Tax (5%):             2.25")
        printer.printLine("TOTAL:                47.25")
        printer.printCommand(ThermalPrinterService.BOLD_OFF)
        printer.printLine("")

        // Footer
        printer.printCommand(ThermalPrinterService.ALIGN_CENTER)
        printer.printLine("THANK YOU FOR YOUR PURCHASE!")
        printer.printLine("Visit us again soon.")

        // Add some line feeds before cutting
        printer.printLine("\n\n\n\n")

        // Cut paper
        printer.printCommand(ThermalPrinterService.FULL_CUT)

    } catch (e: Exception) {
        println("Printing failed: ${e.message}")
        e.printStackTrace()
    } finally {
        printer.close() // Always close the connection
    }
}