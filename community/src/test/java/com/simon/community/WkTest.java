package com.simon.community;

import java.io.IOException;

/**
 * @author zhengx
 * @version 1.0
 */

public class WkTest {
    public static void main() {
        String cmd="D:\\applications\\wkhtmltopdf\\bin\\wkhtmltoimage https://www.nowcoder.com D:\\learning\\projectforwork\\nowcoder\\community\\wk\\wk-images\\3.png";
        //执行该命令去
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
