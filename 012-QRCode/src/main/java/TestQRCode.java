import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: p2p
 * @Package: PACKAGE_NAME
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/28 21:45
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
public class TestQRCode {

    public static void main(String[] args) throws WriterException, IOException {
        //设置字符集
        Map<EncodeHintType,Object> map= new HashMap<EncodeHintType, Object>();
        map.put(EncodeHintType.CHARACTER_SET,"UTF-8");

        //创建一个矩阵对象
        BitMatrix bitMatrix = new MultiFormatWriter().encode("weixin://wxpay/bizpayurl?pr=PKaq1P600", BarcodeFormat.QR_CODE, 200, 200, map);

        Path path= FileSystems.getDefault().getPath("D:\\","xx.jpg");
        //将矩阵对象转换为二维码图片
        MatrixToImageWriter.writeToPath(bitMatrix,"jpg",path);
        System.out.println("生成成功");

        Double total_fee=600000d;
        BigDecimal bigDecimal=new BigDecimal(total_fee);
        bigDecimal.multiply(new BigDecimal(100));

        System.out.println( bigDecimal.intValue());
    }
}
