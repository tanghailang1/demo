package com.efs.cloud.trackingservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.efs.cloud.trackingservice.ServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * JWTService
 */
@Service
@Slf4j
@Component
public class JwtService {

    @Value("${jwt_secret}")
    private String jwtSecret;

    @Value("${jwt_issuer}")
    private String jwtIssuer;

    @Autowired
    private EncryptService encryptService;

    /**
     * 验证JWT
     *
     * @param jwt
     * @return
     */
    public Map<String, Claim> verifyJWT(String jwt) {
        //log.info("jwt_secre:"+jwtSecret+" jwt_issuer:"+jwtIssuer);
        Map<String, Claim> stringClaimMap = null;
        try {
            if (StringUtils.isBlank(jwt)) {
                return stringClaimMap;
            }
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(jwtIssuer)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(jwt);
            stringClaimMap = decodedJWT.getClaims();
        } catch (Exception e) {
        }
        return stringClaimMap;
    }

    /**
     * 验证JWT与cartKey中CustomerId一致
     *
     * @param cartKey
     * @param jwt
     * @return
     */
    public ServiceResult checkJWTAndCartKey(String cartKey, String jwt) {

        try {
            cartKey = encryptService.base64Decode(cartKey);

            Map<String, Claim> claimMap = verifyJWT(jwt);
            if (claimMap == null) {
                return ServiceResult.builder().code(-1001).msg("JWT验证失败").build();
            }
            Claim customerIdClaim = claimMap.get("customerId");
            if (customerIdClaim != null) {
                // 取到cartKey里的CustomerId
                String[] keys = cartKey.split(":");
                String customerIdInCartKey = keys[3];
                if (customerIdClaim.asString().equals(customerIdInCartKey)) {
                    return ServiceResult.builder().code(1000).msg("验证成功").build();
                } else {
                    return ServiceResult.builder().code(-1002).msg("JWT与customerId不一致，验证失败").build();
                }
            } else {
                return ServiceResult.builder().code(-1003).msg("JWT里无CustomerId信息，验证失败").build();
            }
        }catch (Exception e){
            log.error("checkJWTAndCartKey failed." + e.getMessage(), e);
            return ServiceResult.builder().code(-1004).msg("cartKey验证失败").build();
        }
    }

    /**
     * 验证JWT和订单信息的一致性
     *
     * @param jwt
     * @param customerId
     * @return
     */
    public ServiceResult checkJwtAndCustomerId(String jwt, Integer customerId) {

        Map<String, Claim> claimMap = verifyJWT(jwt);
        if (claimMap == null) {
            return ServiceResult.builder().code(-1001).msg("JWT验证失败").build();
        }
        Claim customerIdClaim = claimMap.get("customerId");
        if (customerIdClaim == null) {
            return ServiceResult.builder().code(-1001).msg("JWT里无CustomerId信息，验证失败").build();
        }
        if(!customerIdClaim.asString().equals(customerId.toString())){
            return ServiceResult.builder().code(-1002).msg("JWT与订单customerId不一致，验证失败").build();
        }
        return ServiceResult.builder().code(1001).msg("验证成功").build();
    }

    /**
     * 验证JWT和订单merchantId的一致性
     *
     * @param jwt
     * @param merchantId
     * @return
     */
    public ServiceResult checkJwtAndMerchantId(String jwt, Integer merchantId) {

        Map<String, Claim> claimMap = verifyJWT(jwt);
        if (claimMap == null) {
            return ServiceResult.builder().code(-1001).msg("JWT验证失败").build();
        }
        Claim customerIdClaim = claimMap.get("merchantId");
        if (customerIdClaim == null) {
            return ServiceResult.builder().code(-1001).msg("JWT里无MerchantId信息，验证失败").build();
        }
        if(!customerIdClaim.asString().equals(merchantId.toString())){
            return ServiceResult.builder().code(-1002).msg("JWT与MerchantId不一致，验证失败").build();
        }
        return ServiceResult.builder().code(1001).msg("验证成功").build();
    }

    /**
     * 获取merchantId
     * @param  jwt
     * @return
     */
    public ServiceResult getMerchantId(String jwt){
        Map<String, Claim> claimMap = verifyJWT(jwt);
        if(claimMap == null ){
            return ServiceResult.builder().code(-1004).msg("jwt验证失败").build();
        }
        if (!claimMap.containsKey("merchantId")) {
            return ServiceResult.builder().code(-1005).msg("jwt里无merchantId信息，验证失败").build();
        }
        Claim customerIdClaim = claimMap.get("merchantId");
        return ServiceResult.builder().code(1000).msg("验证成功").data(customerIdClaim.asString()).build();
    }

    /**
     * 获取customerId
     * @param  jwt
     * @return
     */
    public Integer getCustomerId(String jwt){
        Map<String, Claim> claimMap = verifyJWT(jwt);
        if(claimMap == null ){
            return 0;
        }
        if (!claimMap.containsKey("customerId")) {
            return 0;
        }
        Claim customerIdClaim = claimMap.get("customerId");
        return Integer.parseInt(customerIdClaim.asString());
    }
}
