package ${object.packageStr}.controller;
import com.yipeng.framework.common.web.controller.BaseController;
import ${object.packageStr}.service.${object.name}Service;
import ${object.packageStr}.api.${object.name}Facade;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import ${object.packageStr}.result.${object.name}Result;

/**
* @author ${base.authorName}
* email:${base.authorEmail}
* AUTO-GENERATE BY PROJECT-BUILDER
**/
@RestController
@RequestMapping("/${object.camelName}")
@Api(tags = { "${object.comment} 接口" })
public class ${object.name}Controller extends BaseController<${object.name}Result, ${object.name}Service> implements ${object.name}Facade {
}