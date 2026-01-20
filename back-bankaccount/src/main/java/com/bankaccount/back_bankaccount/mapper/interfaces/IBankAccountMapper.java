package com.bankaccount.back_bankaccount.mapper.interfaces;

import com.bankaccount.back_bankaccount.dto.BankAccountDto;
import com.bankaccount.back_bankaccount.mapper.AbstractMapper;
import com.bankaccount.back_bankaccount.model.BankAccountEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IBankAccountMapper extends AbstractMapper<BankAccountEntity, BankAccountDto> {

}
