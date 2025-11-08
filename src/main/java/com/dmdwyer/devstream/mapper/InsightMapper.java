package com.dmdwyer.devstream.mapper;

import com.dmdwyer.devstream.dto.InsightDto;
import com.dmdwyer.devstream.entity.Insight;

public class InsightMapper {
	public static InsightDto toDto(Insight insight) {
		if (insight == null) return null;
		InsightDto dto = new InsightDto();
		dto.setId(insight.getId());
		dto.setTopic(insight.getTopic());
		dto.setDetail(insight.getDetail());
		return dto;
	}

	public static Insight toEntity(InsightDto dto) {
		if (dto == null) return null;
		Insight i = new Insight();
		i.setId(dto.getId());
		i.setTopic(dto.getTopic());
		i.setDetail(dto.getDetail());
		return i;
	}
}
