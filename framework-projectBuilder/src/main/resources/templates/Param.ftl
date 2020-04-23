package ${object.packageStr}.param;
import com.yipeng.framework.common.model.BaseParam;
import io.swagger.annotations.ApiModelProperty;
import com.yipeng.framework.common.constants.Direction;
import com.yipeng.framework.common.constants.annotation.FieldMapping;
import com.yipeng.framework.common.service.converter.*;
import lombok.Data;

<#if (object.importList)??>
	<#list object.importList as item>
import ${item};
	</#list>
</#if>
/**
* ${object.comment} 查询参数
* @author ${base.authorName}
* email:${base.authorEmail}
* AUTO-GENERATE BY PROJECT-BUILDER
**/
@Data
public class ${object.name}Param extends BaseParam {

	<#if (object.fields)??>
		<#list object.fields as item>
			<#if item.notInParam == false>
				<#if (item.fieldComment)??>
	@ApiModelProperty("${item.fieldComment}")
				</#if>
				<#if item.date>
	@FieldMapping(name = "${item.fieldName}",direction = Direction.OUT, converter = StringDateConverter.class)
	private String ${item.fieldName};
				<#elseif item.float>
	@FieldMapping(name = "${item.fieldName}",direction = Direction.OUT, converter = StringFloatConverter.class)
	private String ${item.fieldName};
				<#elseif item.double>
	@FieldMapping(name = "${item.fieldName}",direction = Direction.OUT, converter = StringDoubleConverter.class)
	private String ${item.fieldName};
				<#elseif item.short>
	@FieldMapping(name = "${item.fieldName}",direction = Direction.OUT, converter = IntegerShortConverter.class)
	private Integer ${item.fieldName};
				<#elseif item.byte>
	@FieldMapping(name = "${item.fieldName}",direction = Direction.OUT, converter = IntegerByteConverter.class)
	private Integer ${item.fieldName};
				<#elseif item.bigDecimal>
	@FieldMapping(name = "${item.fieldName}",direction = Direction.OUT, converter = StringDecimalConverter.class)
	private String ${item.fieldName};
				<#else>
	private ${item.fieldType} ${item.fieldName};
				</#if>
			</#if>
		</#list>
	</#if>
	<#if (object.primaryKeyName)??>
	public String primaryKeyName() {
		return "${object.primaryKeyName!}";
	}
	</#if>
}