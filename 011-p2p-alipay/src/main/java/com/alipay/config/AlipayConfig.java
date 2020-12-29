package com.alipay.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2021000116681517";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCF/YhFSOTgZ4ZAhO7sqn6xeP2nf2fAYF4tt4dOv7VkFElufSyi95QXZkiy9emvRBt21ybg0rJdqlfmPSLK4DsbNWXUPT2KG8CHDD+sYWmeUAamprS3s5dd7K50gFh/PdMFHYuYoOqEKM0lIxChFl9lWOvuKzSG5mm5g0Rhes6RS2+D9xOAKnUor/Fo0Phyu7GXYqQ02uAUji8dBfjpf/fAu3xjJfr8Qe2NBiVVnWcKY8fuFrhfPLnzeGQHWjf90uwCdgoBmvhC+8VP878/0ih3gyaMQ0+P5EmGwFSWJLUiz6ZFgnC+EJm5mVG+0d/9XpqhKeNXRQtyyJ6seVkUf6GHAgMBAAECggEALsH4yeTUKhM2YFGDZwdgSuJj8g2d0R5P/zgqeaqgzBngPjRkXUJ0ZW4GNJ1oPQdRZjlSkeY05EbLgccX2yBAwA/0RwpP7WAd4YK+vZMI3nwitgk6y1hanCY6AuG/ADg86W4UBuEwtYnffPQtm2lJRP6w5l4haGoDvv8136aRa1YcgOh7CNyiGLqEkwoJ6tb/661EpSMqC3O4hXbMDtM5VAdy2hDXoNLsOaO1B+GExEQ4xbcxYQe+gF7A9iU/JIRF0BAUMF6WtlvYIO6ZNfPFuNmAvpOXvSvkNkonC2XRWmEUuviMeG5NOcqVqasHf3+zFBpoLBWfrleeRHIqcj0FUQKBgQC7TIXA+UoVIvPJIdhXEI6zmIpON6PpyvU1kGvASgt9Rr9n4GKkrnB5x6gmYnIS5rMcm9/Ni7PD3XCKdVomw7D29zmNyvz4U+Zp6quNewC27y/2xtetnW84cOvok6HL8X/ZW/WTViK2pAVSGXfhzUl4fzjEI51kdnOClaJgeJaKMwKBgQC3I04svokH9R3dIK6P6Vc00I6Wkn790zzrB2es/zELu/8XsftDqLRy5D17XH7wOhEDU6XP78DBhObEzp9++QlfRLS5o64P0YDg1plTJGx63HFvwOT4BKPXR2nutmgsBE7kfFSFoulS6IlqXST3a8EEcve5HBVO9XHqXbaiO9/fXQKBgE5UYluh5emRBINJNEC1G/jgAndTralIx+Pu0iFI1iU+QS4gGSDmEwPbC+eBpFNjR+TfC2na0wNGyJofGeS8UnX2TouOKN1xuRrymWxB1LqsrVJ3NhvVi33/O6m4lCHGjcFJRBqqGzpmvhLns8srH17VVdgxTNGG+CfUjGPsCokhAoGBAIVa9g/q5ome2ZItvC8a8N+KhmnqXwKhkRhrmfkFEkfDK2urtJV+AXgv7v0nS1/OSJktje4uXSQNjKT++HFTEpoGw7ryD0uBeOFAIwHB4iRM5WVFBvX7GvsmC2NB+/UhZflG950gFdrDbVzAWqy4ECMXPH/UjUejvsG0X8se4lQZAoGAXrpXS1S7QyvZatoLFqHovGObqPzVU3hFlu2geDIZvmfkQ6ciAMalR0AhFz5JDZ1uHbs5j2zZqU9m7djqhpXNMYUOEpQU0OkSgrqyxahL6VpWt8kkYqOu8lEOJsiqwxnkGWb95QZo/pinwVjoPaNC6NKnrO4VNYlP3UnIY2R08G0=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh3fe50a4dwYaaxNfVExXsqApi3R679ZqmOaMd5ogRCspNPAzr77lXOq2QXIe0PFhu5FBNral1efiF50CqUZYX9o0cpX1jeV9wx19fO6G84pWjffYGLaBvpa6AEGrloaz82cCX9YqAmy5sqlYCZTgb1w+dTr1wa5shU7gy2t6R5lwUQvLLoWJqV+h0J8MzirwzpsAv6WvXV9p2Paufgw1h/FaIKCKS0nedRAgtk9MBMk99U9tryaGTcoGdr+YYTBmayGACWm8kLQfv4QJv4mlzcbE+gCiUvqEpFi4IojGAlet3D7PguAYhg/mYIJ5QyosS6+3hnV5SfpS6eE3+ld2iwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8080/011_p2p_alipay_war_exploded/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8080/011_p2p_alipay_war_exploded/alipay.trade.page.pay-JAVA-UTF-8/return_url.jsp";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

