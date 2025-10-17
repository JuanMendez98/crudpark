package com.crudzaso.crudpark.util;

import com.crudzaso.crudpark.config.AppConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Generador de códigos QR para tickets
 */
public class QRGenerator {
    private final AppConfig config;

    public QRGenerator() {
        this.config = AppConfig.getInstance();
    }

    public BufferedImage generarQR(int ticketId, String placa, long timestamp) {
        String contenido = String.format(
                config.getQrFormat(),
                ticketId,
                placa,
                timestamp
        );

        return generarQRDesdeTexto(contenido);
    }

    public BufferedImage generarQRDesdeTexto(String texto) {
        try {
            QRCodeWriter qrWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrWriter.encode(
                    texto,
                    BarcodeFormat.QR_CODE,
                    config.getQrWidth(),
                    config.getQrHeight()
            );

            return MatrixToImageWriter.toBufferedImage(bitMatrix);

        } catch (WriterException e) {
            System.err.println("Error al generar QR: " + e.getMessage());
            return null;
        }
    }

    public void guardarQREnArchivo(BufferedImage qrImage, String rutaArchivo) {
        try {
            Path path = Path.of(rutaArchivo);
            MatrixToImageWriter.writeToPath(
                    toBitMatrix(qrImage),
                    "PNG",
                    path
            );
            System.out.println("QR guardado en: " + rutaArchivo);

        } catch (IOException e) {
            System.err.println("Error al guardar QR: " + e.getMessage());
        }
    }

    private BitMatrix toBitMatrix(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BitMatrix bitMatrix = new BitMatrix(width, height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (image.getRGB(x, y) == 0xFF000000) {
                    bitMatrix.set(x, y);
                }
            }
        }

        return bitMatrix;
    }
}
