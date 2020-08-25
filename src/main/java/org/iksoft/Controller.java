package org.iksoft;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * @author Ilyos Khurozov
 */

@RestController
public class Controller {

    @GetMapping(value = "/qr-create")
    @ResponseBody
    public ResponseEntity<byte[]> qrCreate(
            @RequestParam String txt
    ) {
        QRCodeWriter writer = new QRCodeWriter();
        int width = 250, height = 250;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // create an empty image
        //           R           G          B
        int white = 255 << 16 | 255 << 8 | 255;
        int black = 0;

        try {
            BitMatrix bitMatrix = writer.encode(txt, BarcodeFormat.QR_CODE, width, height);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    image.setRGB(i, j, bitMatrix.get(i, j) ? black : white);
                }
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write( image, "png", os );
            os.flush();
            byte[] imageInByte = os.toByteArray();
            os.close();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .body(Base64.encodeBase64(imageInByte));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("qr-scan")
    @ResponseBody
    public String qrScan(
            @RequestParam(name = "multi") MultipartFile multi
    ){
        try {
            BufferedImage image = ImageIO.read(multi.getInputStream());
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
            QRCodeReader reader = new QRCodeReader();
            Result result = reader.decode(new BinaryBitmap(new HybridBinarizer(source)));

            return "{\"status\":\"ok\", \"txt\":\""+result.getText() + "\"}";
        } catch (IOException | ChecksumException | FormatException | NotFoundException e) {
            return "{\"status\":\"error\"}";
        }
    }
}
