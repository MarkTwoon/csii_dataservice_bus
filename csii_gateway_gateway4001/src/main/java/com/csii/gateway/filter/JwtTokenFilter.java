package com.csii.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.csii.commons.entities.CommonResult;
import com.csii.commons.util.TokenUtil;
import com.csii.gateway.util.RedisUtil;
import com.fasterxml.jackson.core.filter.TokenFilter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.net.URI;
@Slf4j
@Component
public class JwtTokenFilter implements GlobalFilter, Ordered {


    Logger logger = LoggerFactory.getLogger(TokenFilter.class);
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("==全局过滤器JwtTokenFilter==");

        /*getAllParamtersRequest(exchange.getRequest());
        getAllHeadersRequest(exchange.getRequest());*/
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add("Content-Type","application/json;charset=UTF-8");
        ServerHttpRequest request = exchange.getRequest();
        //response.getHeaders().add("Content-Type","application/json;charset=UTF-8");
        logger.info("UrlFilter开始............");
        URI uri = request.getURI();
        logger.info("UrlFilter开始............URI==="+uri.toString());

        HttpMethod method = request.getMethod();
        logger.info("UrlFilter开始............HttpMethod==="+method.toString());

        String methodValue= request.getMethodValue();
        logger.info("UrlFilter开始............getMethodValue==="+methodValue);


        RequestPath requestPath =request.getPath();
        logger.info("UrlFilter开始............RequestPath==="+requestPath.toString());

        InetSocketAddress inetSocketAddress =request.getRemoteAddress();
        logger.info("UrlFilter开始............InetSocketAddress==="+inetSocketAddress.toString());

        //拦截的逻辑。根据具体业务逻辑做拦截。
       // String token = request.getQueryParams().getFirst("token");

       /* String token = request.mutate().header("token");*/
        String token = request.getHeaders().getFirst("token");
        if(requestPath.toString().equals("/login")){
            return chain.filter(exchange);
        }else
        if ((token == null || token.isEmpty())) {
            logger.info("token is empty...");
//			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//			return exchange.getResponse().setComplete();

            //设置status和body
            return Mono.defer(() -> {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);//设置status
                byte[] bytes = JSON.toJSONString(new CommonResult(99999,"非法访问,没有检测到token~~~~~~")).getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                response.getHeaders().set("aaa", "bbb");//设置header
                logger.info("TokenFilter拦截非法请求，没有检测到token............");
                return response.writeWith(Flux.just(buffer));//设置body
            });
        }else {
            try{
                if(TokenUtil.verify(token)){
                    //判断Redis是否存在所对应的RefreshToken
                    String account = TokenUtil.getAccount(token);
                    Long currentTime=TokenUtil.getCurrentTime(token);
                    if (RedisUtil.hasKey(account)) {
                        Long currentTimeMillisRedis = (Long) RedisUtil.get(account);
                        if (currentTimeMillisRedis.equals(currentTime)) {
                            return chain.filter(exchange);
                        }else{
                            return Mono.defer(() -> {
                                response.setStatusCode(HttpStatus.UNAUTHORIZED);//设置status
                                byte[] bytes = JSON.toJSONString(new CommonResult(99999,"Token手令已经过期，请重新登录获取新token............")).getBytes(StandardCharsets.UTF_8);
                                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                                response.getHeaders().set("aaa", "bbb");//设置header
                                logger.info("Token手令已经过期，请重新登录获取新token............");
                                return response.writeWith(Flux.just(buffer));//设置body
                            });
                        }
                    }
                }
                return Mono.defer(() -> {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);//设置status
                    byte[] bytes = JSON.toJSONString(new CommonResult(99999,"Token格式验证失败，请重新登录获取正确token............")).getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                    response.getHeaders().set("aaa", "bbb");//设置header
                    logger.info("Token格式验证失败，请重新登录获取正确token............");
                    return response.writeWith(Flux.just(buffer));//设置body
                });
                //return false;
            }catch (Exception e){
                Throwable throwable = e.getCause();
                System.out.println("token验证："+e.getClass());
                if (e instanceof TokenExpiredException){
                    System.out.println("TokenExpiredException");
                    if (refreshToken(token,response,exchange)) {
                        return chain.filter(exchange);
                    }else {
                        return Mono.defer(() -> {
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);//设置status
                            byte[] bytes = JSON.toJSONString(new CommonResult(99999,"Token手令已经过期，请重新登录获取新token............")).getBytes(StandardCharsets.UTF_8);
                            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                            response.getHeaders().set("aaa", "bbb");//设置header
                            logger.info("Token手令已经过期，请重新登录获取新token............");
                            return response.writeWith(Flux.just(buffer));//设置body
                        });
                    }
                }else{
                    return Mono.defer(() -> {
                        response.setStatusCode(HttpStatus.UNAUTHORIZED);//设置status
                        byte[] bytes = JSON.toJSONString(new CommonResult(99999,"Token格式验证失败，请重新登录获取正确token............")).getBytes(StandardCharsets.UTF_8);
                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                        response.getHeaders().set("aaa", "bbb");//设置header
                        logger.info("Token格式验证失败，请重新登录获取正确token............");
                        return response.writeWith(Flux.just(buffer));//设置body
                    });
                }
            }
        }
        //没有被if条件拦截，就放行
       // return chain.filter(exchange);
    }



    /**
     * 刷新AccessToken，进行判断RefreshToken是否过期，未过期就返回新的AccessToken且继续正常访问
     * @param
      * @return
     */
    private boolean refreshToken(String token,ServerHttpResponse response,ServerWebExchange exchange) {

        String account = TokenUtil.getAccount(token);
        Long currentTime=TokenUtil.getCurrentTime(token);
        // 判断Redis中RefreshToken是否存在
        if (RedisUtil.hasKey(account)) {
            // Redis中RefreshToken还存在，获取RefreshToken的时间戳
            Long currentTimeMillisRedis = (Long) RedisUtil.get(account);
            // 获取当前AccessToken中的时间戳，与RefreshToken的时间戳对比，如果当前时间戳一致，进行AccessToken刷新
            if (currentTimeMillisRedis.equals(currentTime)) {
                // 获取当前最新时间戳
                Long currentTimeMillis =System.currentTimeMillis();
                RedisUtil.set(account, currentTimeMillis,
                        TokenUtil.REFRESH_EXPIRE_TIME);
                // 刷新AccessToken，设置时间戳为当前最新时间戳
                token = TokenUtil.sign(account, currentTimeMillis);
                response.getHeaders().set("Authorization", token);//设置header
                response.getHeaders().set("Access-Control-Expose-Headers", "Authorization");//设置header
               /* response.setHeader("Authorization", token);
                response.setHeader("Access-Control-Expose-Headers", "Authorization");*/
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return -100;
    }



    private Map getAllParamtersRequest(ServerHttpRequest request) {
        logger.info("getAllParamtersRequest开始............");
        Map map = new HashMap();
        MultiValueMap<String, String> paramNames = request.getQueryParams();
        Iterator it= paramNames.keySet().iterator();
        while (it.hasNext()) {
            String paramName = (String) it.next();

            List<String> paramValues = paramNames.get(paramName);
            if (paramValues.size() >= 1) {
                String paramValue = paramValues.get(0);
                logger.info("request参数："+paramName+",值："+paramValue);
                map.put(paramName, paramValue);
            }
        }
        return map;
    }

    private Map getAllHeadersRequest(ServerHttpRequest request) {
        logger.info("getAllHeadersRequest开始............");
        Map map = new HashMap();
        HttpHeaders hearders = request.getHeaders();
        Iterator it= hearders.keySet().iterator();
        while (it.hasNext()) {
            String keyName = (String) it.next();

            List<String> headValues = hearders.get(keyName);
            if (headValues.size() >= 1) {
                String kvalue = headValues.get(0);
                logger.info("request header的key："+keyName+",值："+kvalue);
                map.put(keyName, kvalue);
            }
        }
        return map;
    }

}
