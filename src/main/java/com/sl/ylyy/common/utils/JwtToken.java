package com.sl.ylyy.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sl.ylyy.common.Interceptor.MyException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import java.util.*;

@SuppressWarnings("deprecation")
@Component
public class JwtToken {

    /** token秘钥，请勿泄露，请勿随便修改 backups:JKKLJOoasdlfj */
    public static final String SECRET = "JKKLJOoasdlfj";
    /** token 过期时间: 10天 */
    public static final int calendarField = Calendar.DATE;
    public static final int calendarInterval = 10;

    public static void main(String[] args) {
        long a=1;
        try {
            System.out.println(JwtToken.getWebToken(a));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * JWT生成Token.<br/>
     *
     * JWT构成: header, payload, signature
     *
     * @param user_id
     *            登录成功后用户user_id, 参数user_id不可传空
     */
    public static String getWebToken(Object user_id){
        String token ="";
        try {
            Date iatDate = new Date();
            // expire time
            Calendar nowTime = Calendar.getInstance();
            nowTime.add(calendarField, calendarInterval);
            Date expiresDate = nowTime.getTime();

            // header Map
            Map<String, Object> map = new HashMap<>();
            map.put("alg", "HS256");
            map.put("typ", "JWT");

            // build token
            // param backups {iss:Service, aud:APP}
            token = JWT.create().withHeader(map) // header
                    .withClaim("iss", "Service") // payload
                    .withClaim("aud", "APP").withClaim("user_id", null == user_id ? null : user_id.toString())
                    .withIssuedAt(iatDate) // sign time
                    .withExpiresAt(expiresDate) // expire time
                    .sign(Algorithm.HMAC256(SECRET)); // signature

        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }




    /**
     * 解密Token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static Map<String, Object> parseWebToken(String token) {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            jwt = verifier.verify(token);
        } catch (Exception e) {
            // token 校验失败, 抛出Token验证非法异常
            throw new MyException(CodeMsg.AUTH_EXPIRES,0);
        }

        Claim user_id_claim = jwt.getClaims().get("user_id");
        if (null == user_id_claim || StringUtils.isEmpty(user_id_claim.asString())) {
            // token 校验失败, 抛出Token验证非法异常
            throw new MyException(CodeMsg.AUTH_EXPIRES,0);
        }
        return JSONObject.parseObject(user_id_claim.asString());
    }


    /**
     * 获取当前登录的用户对象
     */
     public static Map<String, Object> getUserByWebToken(String token) {
         try {
               Map<String, Object> tUserMap =parseWebToken(token);
               if (tUserMap == null) {
                   throw new MyException(1002,"授权过期,请重新登录",0);
               }
               return tUserMap;
          } catch (MyException ex) {
             throw ex;
         }catch (Exception ex) {
             throw new MyException(CodeMsg.TOKEN_FAILS,0);
         }
     }

     //获取user_id
     public static Integer getUserIdByToken(String token){
         return (Integer)getUserByWebToken(token).get("user_id");
     }
    //获取rong_user_id
     public static String getRongUserIdByToken(String token){ return String.valueOf(getUserByWebToken(token).get("rong_user_id")); }
    //获取role_id
     public static Integer getRoleIdByToken(String token){
        return (Integer)getUserByWebToken(token).get("role_id");
    }
}