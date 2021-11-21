import java.awt.image.BufferedImage

class CryptoTools {
    private val hexArray = "0123456789ABCDEF".toCharArray()

    fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF

            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    fun printImage(image: BufferedImage, iLength: Int) {
        var r = 0
        var c = 0
        var pixel = 0

        repeat(times = iLength) {
            pixel = image.getRGB(c, r)
            printPixelARGB(pixel)
            c = (c + 1) % image.width
            if (c == 0) {
                r++
            }
        }
    }

    fun printPixelARGB(pixel: Int) {
//    val alpha = pixel shr 24 and 0xff
        val red = pixel shr 16 and 0xff
        val green = pixel shr 8 and 0xff
        val blue = pixel and 0xff
        val numbits = 8

//    print("argb: $alpha, $red, $green, $blue ")
//    print(toBinary(alpha, numbits))
//    print(" ")
        print("rgb: $red, $green, $blue ")
        print(toBinary(red, numbits))
        print(" ")
        print(toBinary(green, numbits))
        print(" ")
        print(toBinary(blue, numbits))
        println()
    }

    fun toBinary(x: Int, len: Int): String {
        return String.format(
            "%" + len + "s",
            Integer.toBinaryString(x)
        ).replace(" ".toRegex(), "0")
    }

    fun cmpImages(image1: BufferedImage, image2: BufferedImage, len: Int) {
        var r = 0
        var c = 0
        var pixel1 = 0
        var pixel2 = 0

        repeat(times = len) {
            pixel1 = image1.getRGB(c, r)
            pixel2 = image2.getRGB(c, r)
            printPixelARGB(pixel1)
            printPixelARGB(pixel2)
            println()
            c = (c + 1) % image1.width
            if (c == 0) {
                r++
            }
        }
    }

    fun copyImage(image1: BufferedImage, image2: BufferedImage): BufferedImage {
        // shallow copy of image
        for (r in 0 until image1.height) {
            for (c in 0 until image1.width) {
                image2.setRGB(c, r, image1.getRGB(c, r))
            }
        }
        return image2
    }
}