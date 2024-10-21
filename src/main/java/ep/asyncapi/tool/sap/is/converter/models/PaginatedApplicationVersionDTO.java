package ep.asyncapi.tool.sap.is.converter.models;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedApplicationVersionDTO {

    private int totalCount;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private List<ApplicationVersionDTO> applicationVersionDTOList;

}
