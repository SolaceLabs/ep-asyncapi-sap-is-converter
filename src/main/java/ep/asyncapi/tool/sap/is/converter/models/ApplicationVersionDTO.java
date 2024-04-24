package ep.asyncapi.tool.sap.is.converter.models;

import lombok.Data;

@Data
public class ApplicationVersionDTO {

    private String id;
    private String description;
    private String version;
    private String state;

}
