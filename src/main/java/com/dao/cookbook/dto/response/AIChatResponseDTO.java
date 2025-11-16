package com.dao.cookbook.dto.response;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AIChatResponseDTO {
    private String answer;
    private List<Map<String, Object>> sources;
}
