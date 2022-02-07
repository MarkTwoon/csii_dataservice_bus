package com.csii.regist.config;

import brave.sampler.Sampler;
import org.springframework.cloud.sleuth.sampler.ProbabilityBasedSampler;
import org.springframework.cloud.sleuth.sampler.SamplerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration  /*自己声明配置一个zipkin的采样器 防止项目启动 与redis连接冲突 产生线程死锁*/
public class SleuthSamplerConfiguration {
    @Bean
    public Sampler defaultSampler() throws Exception {
        // 这个值是采样率，设置为1就是100%采样，每一条请求都采，0.1就是10%采样率
        Float f = new Float(0.1f);
        SamplerProperties samplerProperties = new SamplerProperties();
        samplerProperties.setProbability(f);
        ProbabilityBasedSampler sampler = new ProbabilityBasedSampler(samplerProperties);
        return sampler;
    }

}
