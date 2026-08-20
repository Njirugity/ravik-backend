package net.ravik_cms.ravik_backend.milestoneBudget.mapper;

import net.ravik_cms.ravik_backend.milestoneBudget.dtos.CreateMilestoneBudgetLineDto;
import net.ravik_cms.ravik_backend.milestoneBudget.dtos.MilestoneBudgetLineDto;
import net.ravik_cms.ravik_backend.milestoneBudget.dtos.UpdateMilestoneBudgetLineDto;
import net.ravik_cms.ravik_backend.milestoneBudget.entity.MilestoneBudget;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MilestoneBudgetMapper {
    MilestoneBudget toEntity(CreateMilestoneBudgetLineDto dto);

    @Mapping(source = "milestone.id", target = "milestoneId")
    MilestoneBudgetLineDto toLineDto(MilestoneBudget entity);

    List<MilestoneBudgetLineDto> toLineDtoList(List<MilestoneBudget> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLine(UpdateMilestoneBudgetLineDto dto, @MappingTarget MilestoneBudget entity);
}
