package com.nowcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {
    /**
     *  Producer是Kaptcha的核心接口
     *  DefaultKaptcha是Kaptcha核心接口的默认实现类
     * @return
     */
    @Bean
    public Producer kaptchaProducer(){
        Properties properties=new Properties();
        //设置图片宽度
        properties.setProperty("kaptcha.image.width","100");
        //设置图片高度
        properties.setProperty("kaptcha.image.height","40");
        //设置验证码字体大小
        properties.setProperty("kaptcha.textproducer.font.size","32");
        //设置验证码字体颜色
        properties.setProperty("kaptcha.textproducer.font.color","0,0,0");
        //设置验证码选用什么字符
        properties.setProperty("kaptcha.textproducer.char.string","0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        //设置验证码长度
        properties.setProperty("kaptcha.textproducer.char.length","4");
        //设置干扰实现类
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");
        //DefaultKaptcha是Kaptcha核心接口的默认实现类
        DefaultKaptcha kaptcha=new DefaultKaptcha();
        Config config =new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
     }
}
