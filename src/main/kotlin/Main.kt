import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB
import java.io.File
import javax.imageio.ImageIO

const val LSB = 1

const val STOP_CODE_0 = 0
const val STOP_CODE_1 = 0
const val STOP_CODE_2 = 3

fun main() {
    var command = ""

    while (command != "exit") {
        println("Task (hide, show, exit):")
        print("> ")

        command = readLine()!!.lowercase()
        when (command) {
            "exit" -> { println("Bye!"); break }
            "hide" -> cmdHide()
            "show" -> cmdShow()
            else -> println("Wrong task: $command")
        }
    }
}

fun cmdHide() {
    val inFilename = getInputFilename()
    lateinit var bImage: BufferedImage

    try {
        bImage = ImageIO.read(File(inFilename))
    } catch (e: javax.imageio.IIOException) {
        println("Can't read input file")
        return
    }

    println("Input image data")

    val outFilename: String = getOutputFilename()
    val msgBytes = inputMessageToHide()

    if (!validMsgSize(bImage, msgBytes)) {
        return
    }

    val bImage2: BufferedImage = processImage(bImage, msgBytes)
    println("Output image data")

    try {
        ImageIO.write(
            bImage2,
            "png",
            File(outFilename),
        )
    } catch (e: javax.imageio.IIOException) {
        println("Error writing output file!")
        return
    }


    println("Message saved in $outFilename image.")
}

fun getInputFilename(): String {
    println("Input image file:")
    return readLine()!!.toString()
}

fun getOutputFilename(): String {
    println("Output image file:")
    return readLine()!!.toString()
}

fun inputMessageToHide(): ByteArray {
    println("Message to hide: ")
    val stopBytes = byteArrayOf(0, 0, 3)
    val msg = readLine()!!

    return msg.encodeToByteArray() + stopBytes
}

fun processImage(img: BufferedImage, msgBytes: ByteArray): BufferedImage {
    val width = img.width
    val height = img.height
    val img2 = BufferedImage(img.width, img.height, TYPE_INT_RGB )
    var fpixel: Int
    var tpixel: Int
    var msgByteMask: Int
    var intMsg: Int
    var r = 0
    var c = 0

    for (element in msgBytes) {
        intMsg = element.toInt()
        msgByteMask = 128
        repeat(8) {
            fpixel = img.getRGB(c, r)
            if ((intMsg and msgByteMask) == msgByteMask) {
                tpixel = fpixel or LSB
            } else {
                tpixel = fpixel and LSB.inv()
            }
            img2.setRGB(c, r, tpixel)

            c = (c + 1) % width

            if(c == 0) {
              if(r < height - 1) {
                  r++
              } else {
                  return img2
              }
            }

            msgByteMask = msgByteMask.shr(1)
        }
    }

    return img2
}

fun validMsgSize(img: BufferedImage, msgBytes: ByteArray): Boolean {
    if (img.width * img.height < msgBytes.size * Byte.SIZE_BITS) {
        println("The input image is not large enough to hold this message.")
        return false
    }
    return true
}

fun cmdShow() {
    val inFilename = getInputFilename()
    val bImage: BufferedImage

    try {
        bImage = ImageIO.read(File(inFilename))
    } catch (e: javax.imageio.IIOException) {
        println("Can't read input file")
        return
    }

    println("Message:")
    println(getMessage(bImage))
}

fun getMessage(image: BufferedImage): String {
    val width = image.width
    val height = image.height
    var bitVal: Int
    val last3 = ByteArray(3)
    var msgInt: Int

    var i: Int
    var r = 0
    var c = 0

    val mutableBytes = mutableListOf<Byte>()

    i = 0
    while (true) {
        bitVal = 128
        msgInt = 0

        repeat(8) {
            if (image.getRGB(c,r) and LSB == LSB) {
                msgInt = msgInt or bitVal
            } else {
                msgInt = msgInt and bitVal.inv()
            }
            bitVal = bitVal.shr(1)

            c = (c + 1) % width

            if(c == 0) {
                if(r < height - 1) {
                    r++
                }
            }
        }

        if(r > 0) break
        mutableBytes.add(msgInt.toByte())

        if (i >= 2) {
            last3[0] = mutableBytes[i]
            last3[1] = mutableBytes[i - 1]
            last3[2] = mutableBytes[i - 2]

            if (checkStopBytes(last3)) {
                break
            }
        }
        i++
    }
    val lastNdx = mutableBytes.size-6
    return mutableBytes.slice(IntRange(0, lastNdx)).toByteArray().decodeToString()
}

fun checkStopBytes(message: ByteArray): Boolean {

    if (message[0].toInt() == STOP_CODE_0
            && message[1].toInt() == STOP_CODE_1
            && message[2].toInt() == STOP_CODE_2) {
            return true
    }
    return false
}
