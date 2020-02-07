package com.marketing.web.configs.security.JWTAuthToken;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketing.web.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JWTGenerator {

    public static String generate(String secret, Map<String, Object> header, long expireTime, Map<String, Object> body) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
        byte[] bytes = secret.getBytes();
        byte[] decoded = Base64.getDecoder().decode(bytes);
        DatatypeConverter.parseBase64Binary(secret);
        Key signingKey = new SecretKeySpec(decoded, signatureAlgorithm.getJcaName());

        return Jwts.builder()
                .addClaims(body)
                .setHeaderParams(header)
                .setHeaderParam("type", "JWT")
                .signWith(signatureAlgorithm, signingKey)
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .compact();
    }

    public static String generate(String secret, Map<String, Object> header, long expireTime, Object body) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> bodyMap = objectMapper.convertValue(body, Map.class);
        return generate(secret, header, expireTime, bodyMap);
    }

    public static Object validate(String token, String secret) {
        return Jwts.parser()
                .setSigningKey(Base64.getDecoder().decode(secret))
                .parseClaimsJws(token)
                .getBody();
    }
}