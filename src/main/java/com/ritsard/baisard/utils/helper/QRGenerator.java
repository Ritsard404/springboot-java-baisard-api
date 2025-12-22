/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.zxing.BarcodeFormat
 *  com.google.zxing.Binarizer
 *  com.google.zxing.BinaryBitmap
 *  com.google.zxing.DecodeHintType
 *  com.google.zxing.EncodeHintType
 *  com.google.zxing.LuminanceSource
 *  com.google.zxing.MultiFormatReader
 *  com.google.zxing.NotFoundException
 *  com.google.zxing.Result
 *  com.google.zxing.WriterException
 *  com.google.zxing.client.j2se.BufferedImageLuminanceSource
 *  com.google.zxing.client.j2se.MatrixToImageWriter
 *  com.google.zxing.common.BitMatrix
 *  com.google.zxing.common.HybridBinarizer
 *  com.google.zxing.qrcode.QRCodeWriter
 *  com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
 *  org.springframework.web.multipart.MultipartFile
 */
package com.ritsard.baisard.utils.helper;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class QRGenerator {
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String DEFAULT_FORMAT = "PNG";
    private static final int DEFAULT_MARGIN = 2;
    private static final int DEFAULT_LOGO_SIZE_RATIO = 5;

    public static String readQRCode(String filePath) throws QRProcessingException {
        QRGenerator.validateFilePath(filePath);
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(filePath));
            if (bufferedImage == null) {
                throw new QRProcessingException("\uc774\ubbf8\uc9c0 \ud30c\uc77c\uc744 \uc77d\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4: " + filePath);
            }
            return QRGenerator.decodeQRFromImage(bufferedImage);
        }
        catch (IOException e) {
            throw new QRProcessingException("\ud30c\uc77c \uc77d\uae30 \uc911 \uc624\ub958 \ubc1c\uc0dd: " + filePath, e);
        }
    }

    public static String readQRCodeFromImage(MultipartFile file) throws QRProcessingException {
        QRGenerator.validateMultipartFile(file);
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                throw new QRProcessingException("\uc5c5\ub85c\ub4dc\ub41c \uc774\ubbf8\uc9c0 \ud30c\uc77c\uc744 \uc77d\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4");
            }
            return QRGenerator.decodeQRFromImage(bufferedImage);
        }
        catch (IOException e) {
            throw new QRProcessingException("\uc5c5\ub85c\ub4dc\ub41c \ud30c\uc77c \uc77d\uae30 \uc911 \uc624\ub958 \ubc1c\uc0dd", e);
        }
    }

    public static void generateQRCode(String text, int width, int height, String filePath) throws QRProcessingException {
        QRGenerator.generateQRCode(text, width, height, filePath, ErrorCorrectionLevel.L);
    }

    public static void generateQRCode(String text, int width, int height, String filePath, ErrorCorrectionLevel errorCorrectionLevel) throws QRProcessingException {
        QRGenerator.validateQRParameters(text, width, height, filePath);
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hintMap = QRGenerator.createEncodeHints(errorCorrectionLevel);
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hintMap);
            Path path = FileSystems.getDefault().getPath(filePath, new String[0]);
            MatrixToImageWriter.writeToPath((BitMatrix)bitMatrix, (String)DEFAULT_FORMAT, (Path)path);
        }
        catch (WriterException | IOException e) {
            throw new QRProcessingException("QR \ucf54\ub4dc \uc0dd\uc131 \uc911 \uc624\ub958 \ubc1c\uc0dd: " + filePath, e);
        }
    }

    public static void generateQRCodeWithLogo(String text, int width, int height, String filePath, String logoPath) throws QRProcessingException {
        QRGenerator.validateQRParameters(text, width, height, filePath);
        QRGenerator.validateFilePath(logoPath);
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hintMap = QRGenerator.createEncodeHints(ErrorCorrectionLevel.H);
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hintMap);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage((BitMatrix)bitMatrix);
            BufferedImage logoImage = ImageIO.read(new File(logoPath));
            if (logoImage == null) {
                throw new QRProcessingException("\ub85c\uace0 \uc774\ubbf8\uc9c0\ub97c \uc77d\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4: " + logoPath);
            }
            QRGenerator.overlayLogo(qrImage, logoImage);
            Path path = FileSystems.getDefault().getPath(filePath, new String[0]);
            ImageIO.write((RenderedImage)qrImage, DEFAULT_FORMAT, path.toFile());
        }
        catch (WriterException | IOException e) {
            throw new QRProcessingException("\ub85c\uace0\uac00 \uc788\ub294 QR \ucf54\ub4dc \uc0dd\uc131 \uc911 \uc624\ub958 \ubc1c\uc0dd: " + filePath, e);
        }
    }

    private static String decodeQRFromImage(BufferedImage bufferedImage) throws QRProcessingException {
        try {
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap((Binarizer)new HybridBinarizer((LuminanceSource)source));
            Map<DecodeHintType, Object> hintMap = QRGenerator.createDecodeHints();
            Result result = new MultiFormatReader().decode(bitmap, hintMap);
            return result.getText();
        }
        catch (NotFoundException e) {
            throw new QRProcessingException("QR \ucf54\ub4dc\ub97c \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4", e);
        }
    }

    private static void overlayLogo(BufferedImage qrImage, BufferedImage logoImage) {
        int logoWidth = qrImage.getWidth() / 5;
        int logoHeight = qrImage.getHeight() / 5;
        Image scaledLogo = logoImage.getScaledInstance(logoWidth, logoHeight, 4);
        Graphics2D g = qrImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        int x = (qrImage.getWidth() - logoWidth) / 2;
        int y = (qrImage.getHeight() - logoHeight) / 2;
        g.drawImage(scaledLogo, x, y, logoWidth, logoHeight, null);
        g.dispose();
    }

    private static Map<EncodeHintType, Object> createEncodeHints(ErrorCorrectionLevel errorCorrectionLevel) {
        HashMap<EncodeHintType, Object> hintMap = new HashMap<EncodeHintType, Object>();
        hintMap.put(EncodeHintType.CHARACTER_SET, DEFAULT_CHARSET);
        hintMap.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        hintMap.put(EncodeHintType.MARGIN, 2);
        return hintMap;
    }

    private static Map<DecodeHintType, Object> createDecodeHints() {
        HashMap<DecodeHintType, Object> hintMap = new HashMap<DecodeHintType, Object>();
        hintMap.put(DecodeHintType.CHARACTER_SET, DEFAULT_CHARSET);
        return hintMap;
    }

    private static void validateFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("\ud30c\uc77c \uacbd\ub85c\ub294 \ud544\uc218\uc785\ub2c8\ub2e4");
        }
    }

    private static void validateMultipartFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("\uc5c5\ub85c\ub4dc\ub41c \ud30c\uc77c\uc774 \uc5c6\uc2b5\ub2c8\ub2e4");
        }
    }

    private static void validateQRParameters(String text, int width, int height, String filePath) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("QR \ucf54\ub4dc \ud14d\uc2a4\ud2b8\ub294 \ud544\uc218\uc785\ub2c8\ub2e4");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("QR \ucf54\ub4dc \ud06c\uae30\ub294 0\ubcf4\ub2e4 \ucee4\uc57c \ud569\ub2c8\ub2e4");
        }
        QRGenerator.validateFilePath(filePath);
    }

    public static class QRProcessingException
    extends Exception {
        public QRProcessingException(String message) {
            super(message);
        }

        public QRProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

