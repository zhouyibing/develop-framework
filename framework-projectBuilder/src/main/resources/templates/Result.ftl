package ${object.packageStr}.result;
import com.yipeng.framework.common.constants.Direction;
import com.yipeng.framework.common.constants.annotation.FieldMapping;
import io.swagger.annotations.ApiModelProperty;
import com.yipeng.framework.common.service.converter.*;
import lombok.Data;

/**
* ${object.comment} 返回结果
* @author ${base.authorName}
* email:${base.authorEmail}
* AUTO-GENERATE BY PROJECT-BUILDER
**/
@Data
public class ${object.name}Result {
	<#if (object.fields)??>
	<#list object.fields as item>
		<#if item.notInResult == false>
			<#if (item.fieldComment)??>

	@ApiModelProperty("${item.fieldComment}")
			</#if>
			<#if item.date == true>
	@FieldMapping(name = "${item.fieldName}",direction = Direction.IN, converter = StringDateConverter.class)
	private String ${item.fieldName};
			<#elseif item.float>
	@FieldMapping(name = "${item.fieldName}",direction = Direction.IN, converter = StringFloatConverter.class)
	private String ${item.fieldName};
							<#elseif item.double>
	@FieldMapping(name = "${item.fieldName}",direction = Direction.IN, converter = StringDoubleConverter.class)
	private String ${item.fieldName};
							<#elseif item.short>
	@FieldMapping(name = "${item.fieldName}",direction = Direction.IN, converter = IntegerShortConverter.class)
	private Integer ${item.fieldName};
							<#elseif item.byte>
	@FieldMapping(name = "${item.fieldName}",direction = Direction.IN, converter = IntegerByteConverter.class)
	private Integer ${item.fieldName};
							<#elseif item.bigDecimal>
	@FieldMapping(name = "${item.fieldName}",direction = Direction.IN, converter = StringDecimalConverter.class)
	private String ${item.fieldName};
			<#else>
	private ${item.fieldType} ${item.fieldName};
			</#if>
		</#if>
	</#list>

	</#if>
}