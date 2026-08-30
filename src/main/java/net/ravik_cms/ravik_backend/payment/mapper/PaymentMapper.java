package net.ravik_cms.ravik_backend.payment.mapper;

import net.ravik_cms.ravik_backend.payment.dtos.CreatePaymentDto;
import net.ravik_cms.ravik_backend.payment.dtos.UpdatePaymentDto;
import net.ravik_cms.ravik_backend.payment.entity.Payment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toEntity(CreatePaymentDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePayment(UpdatePaymentDto dto, @MappingTarget Payment entity);
}
