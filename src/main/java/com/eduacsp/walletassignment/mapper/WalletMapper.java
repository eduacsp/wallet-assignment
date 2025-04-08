package com.eduacsp.walletassignment.mapper;

import com.eduacsp.walletassignment.domain.Wallet;
import com.eduacsp.walletassignment.domain.request.WalletRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);

    Wallet toEntity(WalletRequest request);
}
