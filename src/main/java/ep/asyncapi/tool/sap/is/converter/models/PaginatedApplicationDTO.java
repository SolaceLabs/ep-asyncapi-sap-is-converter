package ep.asyncapi.tool.sap.is.converter.models;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedApplicationDTO {
    private int totalCount;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private List<ApplicationDTO> applicationDTOList;
}
