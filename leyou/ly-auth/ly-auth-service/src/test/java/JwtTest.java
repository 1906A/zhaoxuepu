import com.leyou.common.JwtUtils;
import com.leyou.common.RsaUtils;
import com.leyou.common.UserInfo;
import com.leyou.config.JwtProperties;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "C:\\tmp\\rsa\\rsa.pub";

    private static final String priKeyPath = "C:\\tmp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    private static final Logger logger = LoggerFactory.getLogger(JwtTest.class);

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU5MjI3Njk1Nn0.h--64hKyXSYn7kBO5f9QdT2EdWF_ofS-GlhxJhwe0rvdqFZMgC5AGsui7d-hs1hOe9u-5fH21lTcjL5Yj3ShqHjapfdWq7r8QbDfaKS4Hs-gxs_14jFHmOG-YzP1OHdX_j_de_C48K7jY99bKSZ-0EHoDjFhuTVmgG20YsssA5I";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
