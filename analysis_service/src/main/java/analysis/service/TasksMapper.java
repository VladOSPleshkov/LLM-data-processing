package analysis.service;

import analysis.entity.Tasks;
import analysis.entity.TokenUsage;
import com.shared.dto.analysis.*;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.time.LocalDateTime;


@Mapper(componentModel = "spring")
public interface TasksMapper {

    @Mapping(target = "id", source = "id")
    TaskDto toDto(Tasks task);

    List<TaskDto> toDtoList(List<Tasks> tasks);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "tokenUsed", constant = "0")
    @Mapping(target = "result", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "topK", ignore = true)
    //@Mapping(target = "tokenUsages", ignore = true) // Если в Tasks есть List<TokenUsage>
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    Tasks toEntity(CreateTaskRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "brandId", ignore = true) // Обычно бизнес-ключи не меняют partial-апдейтом
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateTaskRequest request, @MappingTarget Tasks task);
}