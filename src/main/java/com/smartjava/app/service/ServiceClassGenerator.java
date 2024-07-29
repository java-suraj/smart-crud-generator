package com.smartjava.app.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ServiceClassGenerator {
	public Map<String, String> generateServiceClass(String tableName)  {
		StringBuilder classContent = new StringBuilder();
        Map<String, String> fileContent = new HashMap<>();
	    String className = CommonUtil.toCamelCase(tableName.toLowerCase(), true)+("Service");;
	    String repoObjectName = className.toLowerCase() + "Repository";
	    
	    classContent.append("import org.springframework.beans.factory.annotation.Autowired;\n");
	    classContent.append("import org.springframework.stereotype.Service;\n");
	    classContent.append("import java.util.List;\n");
	    classContent.append("import java.util.Optional;\n");
	    classContent.append("import org.modelmapper.ModelMapper;\n");
	    classContent.append("import lombok.extern.slf4j.Slf4j;\n");
	    classContent.append("\n");
	    classContent.append("import com.yourpackage.ApiResponse;\n"); // Import ApiResponse
	    classContent.append("\n");
	    classContent.append("@Service\n");
	    classContent.append("@Slf4j\n");
	    classContent.append("public class ").append(className).append("Service {\n");
	    classContent.append("\n");
	    classContent.append("    @Autowired\n");
	    classContent.append("    private ").append(className).append("Repository ").append(repoObjectName).append(";\n");
	    classContent.append("\n");
	    classContent.append("    @Autowired\n");
	    classContent.append("    private ModelMapper modelMapper;\n");
	    classContent.append("\n");
	    
	    // findAll method
	    classContent.append("    public ApiResponse findAll() {\n");
	    classContent.append("        try {\n");
	    classContent.append("            List&lt;").append(className).append("&gt; entities = ").append(repoObjectName).append(".findAll();\n");
	    classContent.append("            return new ApiResponse(true, \"Fetch successful.\", HttpStatus.OK.value(), entities, Collections.emptyList());\n");
	    classContent.append("        } catch (Exception e) {\n");
	    classContent.append("            log.error(\"Error in findAll method: {}\", e.getMessage());\n");
	    classContent.append("            return new ApiResponse(false, \"Error fetching entities.\", HttpStatus.INTERNAL_SERVER_ERROR.value(), Collections.emptyList(), Collections.emptyList());\n");
	    classContent.append("        }\n");
	    classContent.append("    }\n");
	    classContent.append("\n");
	    
	    // findById method
	    classContent.append("    public ApiResponse findById(Long id) {\n");
	    classContent.append("        try {\n");
	    classContent.append("            Optional<").append(className).append("> entity = ").append(repoObjectName).append(".findById(id);\n");
	    classContent.append("            if (entity.isPresent()) {\n");
	    classContent.append("                return new ApiResponse(true, \"Fetch successful.\", HttpStatus.OK.value(), Collections.singletonList(entity.get()), Collections.emptyList());\n");
	    classContent.append("            } else {\n");
	    classContent.append("                return new ApiResponse(false, \"Entity not found.\", HttpStatus.NOT_FOUND.value(), Collections.emptyList(), Collections.emptyList());\n");
	    classContent.append("            }\n");
	    classContent.append("        } catch (Exception e) {\n");
	    classContent.append("            log.error(\"Error in findById method: {}\", e.getMessage());\n");
	    classContent.append("            return new ApiResponse(false, \"Error fetching entity.\", HttpStatus.INTERNAL_SERVER_ERROR.value(), Collections.emptyList(), Collections.emptyList());\n");
	    classContent.append("        }\n");
	    classContent.append("    }\n");
	    classContent.append("\n");
	    
	    // save method
	    classContent.append("    public ApiResponse save(").append(className).append(" entity) {\n");
	    classContent.append("        try {\n");
	    classContent.append("            ").append(className).append(" savedEntity = ").append(repoObjectName).append(".save(entity);\n");
	    classContent.append("            return new ApiResponse(true, \"Save successful.\", HttpStatus.CREATED.value(), Collections.singletonList(savedEntity), Collections.emptyList());\n");
	    classContent.append("        } catch (Exception e) {\n");
	    classContent.append("            log.error(\"Error in save method: {}\", e.getMessage());\n");
	    classContent.append("            return new ApiResponse(false, \"Error saving entity.\", HttpStatus.INTERNAL_SERVER_ERROR.value(), Collections.emptyList(), Collections.emptyList());\n");
	    classContent.append("        }\n");
	    classContent.append("    }\n");
	    classContent.append("\n");
	    
	    // update method
	    classContent.append("    public ApiResponse update(").append(className).append(" entity) {\n");
	    classContent.append("        try {\n");
	    classContent.append("            ").append(className).append(" existingEntity = ").append(repoObjectName).append(".findById(entity.getId()).orElse(null);\n");
	    classContent.append("            if (existingEntity == null) {\n");
	    classContent.append("                return new ApiResponse(false, \"").append(className).append(" not found with id: \" + entity.getId(), HttpStatus.NOT_FOUND.value(), Collections.emptyList(), Collections.emptyList());\n");
	    classContent.append("            }\n");
	    classContent.append("            modelMapper.map(entity, existingEntity);\n");
	    classContent.append("            ").append(className).append(" updatedEntity = ").append(repoObjectName).append(".save(existingEntity);\n");
	    classContent.append("            return new ApiResponse(true, \"Update successful.\", HttpStatus.OK.value(), Collections.singletonList(updatedEntity), Collections.emptyList());\n");
	    classContent.append("        } catch (Exception e) {\n");
	    classContent.append("            log.error(\"Error in update method: {}\", e.getMessage());\n");
	    classContent.append("            return new ApiResponse(false, \"Error updating entity.\", HttpStatus.INTERNAL_SERVER_ERROR.value(), Collections.emptyList(), Collections.emptyList());\n");
	    classContent.append("        }\n");
	    classContent.append("    }\n");
	    classContent.append("\n");
	    
	    // deleteById method
	    classContent.append("    public ApiResponse deleteById(Long id) {\n");
	    classContent.append("        try {\n");
	    classContent.append("            ").append(repoObjectName).append(".deleteById(id);\n");
	    classContent.append("            return new ApiResponse(true, \"Delete successful.\", HttpStatus.OK.value(), Collections.emptyList(), Collections.emptyList());\n");
	    classContent.append("        } catch (Exception e) {\n");
	    classContent.append("            log.error(\"Error in deleteById method: {}\", e.getMessage());\n");
	    classContent.append("            return new ApiResponse(false, \"Error deleting entity.\", HttpStatus.INTERNAL_SERVER_ERROR.value(), Collections.emptyList(), Collections.emptyList());\n");
	    classContent.append("        }\n");
	    classContent.append("    }\n");
	    classContent.append("}\n");
	    
	     fileContent.put(className, String.valueOf(classContent));
	     return fileContent;
	}

}
