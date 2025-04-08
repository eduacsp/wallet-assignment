package com.eduacsp.walletassignment.config;

import com.eduacsp.walletassignment.mapper.WalletMapper;
import com.eduacsp.walletassignment.service.WalletService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WalletControllerTestConfig {

    @Bean
    public WalletService walletService() {
        return Mockito.mock(WalletService.class);
    }

    @Bean
    public WalletMapper walletMapper() {
        return Mockito.mock(WalletMapper.class);
    }
}
